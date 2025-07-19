package br.com.din.pixcraft.payment;

import java.util.concurrent.CompletableFuture;

public enum PaymentStatus {
    PENDING("pending"),
    APPROVED("approved"),
    CANCELLED("cancelled");

    private final String string;
    PaymentStatus(String string) {
        this.string = string;
    }

    public String toString() {
        return string;
    }
}
