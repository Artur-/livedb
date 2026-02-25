package org.vaadin.artur.livedb;

import java.util.List;

import org.vaadin.artur.livedb.data.Item;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.signals.Signal;
import com.vaadin.flow.signals.shared.SharedListSignal;

@Route("")
public class MainView extends Div {

    public MainView(ItemService itemService) {
        Grid<Item> grid = new Grid<>(Item.class);
        Signal.effect(grid, () -> {
            grid.setItems(toList(itemService.getItems()));
        });
        add(grid);

    }

    private static List<Item> toList(SharedListSignal<Item> itemsSignal) {
        return itemsSignal.get().stream()
                .map(item -> item.get()).toList();
    }
}
