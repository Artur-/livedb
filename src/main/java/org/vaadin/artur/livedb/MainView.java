package org.vaadin.artur.livedb;

import java.util.List;

import org.vaadin.artur.livedb.data.Item;

import com.vaadin.flow.component.ComponentEffect;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.Route;
import com.vaadin.signals.ListSignal;

@Route("")
public class MainView extends Div {

    public MainView(ItemService itemService) {
        Grid<Item> grid = new Grid<>(Item.class);
        ComponentEffect.effect(grid, () -> {
            grid.setItems(toList(itemService.getItems()));
        });
        add(grid);

    }

    private static List<Item> toList(ListSignal<Item> itemService) {
        return itemService.value().stream()
                .map(item -> item.value()).toList();
    }
}
