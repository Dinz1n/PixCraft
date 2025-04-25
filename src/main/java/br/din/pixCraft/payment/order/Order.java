package br.din.pixCraft.payment.order;

import br.din.pixCraft.payment.PaymentStatus;
import br.din.pixCraft.product.Product;
import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.resources.payment.Payment;

import java.util.Objects;
import java.util.UUID;

public final class Order {
    private final UUID playerUUID;
    private final Long paymentID;
    private final String qrCodeData;
    private final Product product;
    private PaymentStatus status;

    public Order(UUID playerUUID, Long paymentID, String qrCodeData, Product product, PaymentStatus status) {
        this.playerUUID = Objects.requireNonNull(playerUUID, "Player UUID não pode ser nulo");
        this.paymentID = Objects.requireNonNull(paymentID, "Payment ID não pode ser nulo");
        this.qrCodeData = qrCodeData;
        this.product = Objects.requireNonNull(product, "Produto não pode ser nulo");
        this.status = status;
    }

    public UUID getPlayerUUID() {
        return playerUUID;
    }

    public Long getPaymentID() {
        return paymentID;
    }

    public String getQrCodeData() {
        return qrCodeData;
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

    public void cancel() {
        PaymentClient client = new PaymentClient();
        try {
            client.cancel(paymentID);
        } catch (MPException e) {
            throw new RuntimeException(e);
        } catch (MPApiException e) {
            throw new RuntimeException(e);
        }
    }
}