package org.vaadin.artur.livedb.data;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;

import org.jspecify.annotations.NonNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.relational.core.mapping.Table;


@Table
public class Item {

    @Id
    private Integer id;

    @NonNull
    @NotBlank
    @Max(100)
    private String name;

    private int quantity;

    @LastModifiedDate
    private LocalDateTime lastModifiedDate;

    public Item() {

    }

    public Item(String name, int quantity) {
        this.name = name;
        this.quantity = quantity;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
