package org.vaadin.artur.livedb;

import jakarta.annotation.PreDestroy;

import java.util.List;

import io.r2dbc.postgresql.api.PostgresqlConnection;
import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.Wrapped;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;
import org.vaadin.artur.livedb.data.Item;
import org.vaadin.artur.livedb.data.ItemRepository;
import reactor.core.publisher.Mono;

import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.signals.shared.SharedListSignal;
import com.vaadin.flow.signals.Signal;

@Service
@AnonymousAllowed
public class ItemService {

    private final PostgresqlConnection connection;

    private final SharedListSignal<Item> items = new SharedListSignal<>(Item.class);

    private final ItemRepository itemRepository;

    private boolean inited = false;

    public ItemService(ItemRepository itemRepository, ConnectionFactory connectionFactory) {
        this.itemRepository = itemRepository;
        Wrapped<PostgresqlConnection> pooledConnection = (Wrapped<PostgresqlConnection>) Mono
                .from(connectionFactory.create()).block();
        connection = pooledConnection.unwrap();
    }

    private SharedListSignal<Item> initItems() {
        if (!inited) {
            inited = true;

            // Fetch initial items from the database
            List<Item> initialItems = itemRepository.findAll().collectList().block();
            Signal.runWithoutTransaction(() -> {
                // This must be run without transaction to avoid the grid to set items during
                // the operation, and then calling this method again recursively
                initialItems.forEach(items::insertLast);
            });

            // Subscribe to database events
            connection.createStatement("LISTEN item_added;LISTEN item_updated;LISTEN item_deleted;").execute()
                    .subscribe();

            connection.getNotifications().doOnNext(notification -> {
                System.out.println("Got event from database: " + notification);
                int id = Integer.parseInt(notification.getParameter());
                switch (notification.getName()) {
                    case "item_added":
                        itemRepository.findById(id).doOnNext(item -> {
                            items.insertLast(item);
                        }).subscribe();
                        break;
                    case "item_updated":
                        itemRepository.findById(id).doOnNext(updateItem -> {
                            items.peek().stream()
                                    .filter(existingItem -> existingItem.peek().getId() == updateItem.getId())
                                    .findFirst()
                                    .ifPresent(existingItem -> {
                                        existingItem.set(updateItem);
                                    });
                        }).subscribe();
                        break;
                    case "item_deleted":
                        items.peek().stream()
                                .filter(existingItem -> existingItem.peek().getId() == id)
                                .findFirst()
                                .ifPresent(existingItem -> {
                                    items.remove(existingItem);
                                });
                        break;
                    default:
                        System.err.println("Unknown event: " + notification.getName());
                }
            }).subscribe();
        }
        return items;
    }

    @PreDestroy
    private void preDestroy() {
        connection.close().subscribe();
    }

    @NonNull
    public SharedListSignal<Item> getItems() {
        // This must be lazily inited because signals are not available during
        // @PostConstruct
        return initItems();
    }

}
