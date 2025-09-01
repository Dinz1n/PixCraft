package br.com.din.pixcraft.payment;

public class Payment {
    private final long id;
    private final String qrData;
    private PaymentStatus status;

    public Payment(long paymentId, String qrData) {
        this.id = paymentId;
        this.qrData = qrData;
        status = PaymentStatus.PENDING;
    }

    public long getId() {
        return id;
    }

    public String getQrData() {
        return qrData;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public void setStatus(PaymentStatus status) {
        this.status = status;
    }
}