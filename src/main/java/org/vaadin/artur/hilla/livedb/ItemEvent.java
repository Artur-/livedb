package org.vaadin.artur.hilla.livedb;

import org.jspecify.annotations.NonNull;

public class ItemEvent {
    public enum Operation {
        ADD, UPDATE, DELETE;

        public static Operation forName(String name) {
            if (name.equals("item_added")) {
                return Operation.ADD;
            } else if (name.equals("item_updated")) {
                return Operation.UPDATE;
            } else {
                return Operation.DELETE;
            }
        }
    }

    @NonNull
    private Operation operation;

    private int id;

    private Item item;

    public ItemEvent() {

    }

    public ItemEvent(Operation operation, int id) {
        this.operation = operation;
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setOperation(Operation operation) {
        this.operation = operation;
    }

    public Operation getOperation() {
        return operation;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public Item getItem() {
        return item;
    }
}
