package br.com.din.pixcraft.order;

import br.com.din.pixcraft.product.Product;

import java.util.UUID;

public class Order {
    private final UUID id;
    private final Product product;


    public Order(UUID id, Product product) {
        this.id = id;
        this.product = product;
    }

    public UUID getId() {
        return id;
    }

    public Product getProduct() {
        return product;
    }

    public void cancel() {
    }
}
