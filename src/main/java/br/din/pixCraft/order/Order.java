package br.din.pixCraft.order;

import br.din.pixCraft.payment.PaymentStatus;
import br.din.pixCraft.product.Product;

import java.util.Objects;
import java.util.UUID;

public final class Order {
    private final UUID playerUUID;
    private final Long paymentID;
    private final Product product;
    private PaymentStatus status;

    public Order(UUID playerUUID, Long paymentID, Product product) {
        this.playerUUID = Objects.requireNonNull(playerUUID, "Player UUID não pode ser nulo");
        this.paymentID = Objects.requireNonNull(paymentID, "Payment ID não pode ser nulo");
        this.product = Objects.requireNonNull(product, "Produto não pode ser nulo");
        this.status = PaymentStatus.PENDING;
    }

    public UUID getPlayerUUID() {
        return playerUUID;
    }

    public Long getPaymentID() {
        return paymentID;
    }

    public Product getProduct() {
        return product;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public void setStatus(PaymentStatus status) {
        this.status = Objects.requireNonNull(status, "Status não pode ser nulo");
    }
}