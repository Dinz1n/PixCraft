package br.din.pixCraft.payment;

public enum PaymentStatus {
    PENDING,
    APPROVED,
    AUTHORIZED,
    IN_PROCESS,
    IN_MEDIATION,
    REJECTED,
    CANCELLED,
    REFUNDED,
    CHARGED_BACK,
    UNKNOWN;

    public static PaymentStatus fromString(String status) {
        try {
            return PaymentStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}