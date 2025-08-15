package br.com.din.pixcraft.payment.gateway;

import br.com.din.pixcraft.payment.Payment;
import br.com.din.pixcraft.payment.PaymentStatus;
import br.com.din.pixcraft.product.Product;

import java.util.function.Consumer;

public interface PaymentProvider {
    public abstract void createPayment(String payerName, Product product, String dateOfExpiration, Consumer<Payment> callback);
    public abstract void getStatus(long paymentId, Consumer<PaymentStatus> callback);
    public abstract void cancelPayment(long paymentId, Consumer<Boolean> callback);
    public abstract void setAccessToken(String accessToken);
}
