package org.vaadin.artur.hilla.livedb;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

import io.r2dbc.postgresql.api.PostgresqlConnection;
import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.Wrapped;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;
import org.vaadin.artur.hilla.livedb.ItemEvent.Operation;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.hilla.BrowserCallable;

@Service
@BrowserCallable
@AnonymousAllowed
public class ItemService {

    private ItemRepository itemRepository;
    private PostgresqlConnection connection;

    public ItemService(ItemRepository itemRepository, ConnectionFactory connectionFactory) {
        this.itemRepository = itemRepository;
        Wrapped<PostgresqlConnection> pooledConnection = (Wrapped<PostgresqlConnection>) Mono
                .from(connectionFactory.create()).block();
        connection = pooledConnection.unwrap();
    }

    @PostConstruct
    private void postConstruct() {
        connection.createStatement("LISTEN item_added;LISTEN item_updated;LISTEN item_deleted;").execute().subscribe();
    }

    @PreDestroy
    private void preDestroy() {
        connection.close().subscribe();
    }

    @NonNull
    public Flux<@NonNull Item> getItems() {
        return itemRepository.findAll();
    }

    @NonNull
    public Flux<@NonNull ItemEvent> getItemUpdates() {
        Flux<ItemEvent> events = connection.getNotifications().map(notification -> {
            System.out.println("Got event from database: " + notification);
            Operation operation = Operation.forName(notification.getName());
            int id = Integer.parseInt(notification.getParameter());
            return new ItemEvent(operation, id);
        });
        Flux<ItemEvent> deleteEvents = events.filter(event -> event.getOperation() == Operation.DELETE);

        Flux<ItemEvent> eventsWithData = events.filter(event -> event.getOperation() != Operation.DELETE);
        Flux<Item> data = eventsWithData.flatMap(event -> itemRepository.findById(event.getId()));
        Flux<ItemEvent> updateInsertEvents = Flux.zip(eventsWithData, data).map(tuple -> {
            tuple.getT1().setItem(tuple.getT2());
            return tuple.getT1();
        });

        return Flux.merge(deleteEvents, updateInsertEvents);
    }
}
