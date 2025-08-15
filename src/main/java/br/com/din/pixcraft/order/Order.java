package br.com.din.pixcraft.order;

import br.com.din.pixcraft.payment.Payment;
import br.com.din.pixcraft.payment.gateway.PaymentProvider;
import br.com.din.pixcraft.product.Product;

import java.util.UUID;

public class Order {
    private final UUID id;
    private final Product product;
    private final Payment payment;
    private final PaymentProvider paymentProvider;

    public Order(UUID id, Product product, Payment payment, PaymentProvider paymentProvider) {
        this.id = id;
        this.product = product;
        this.payment = payment;
        this.paymentProvider = paymentProvider;
    }

    public UUID getId() {
        return id;
    }

    public Product getProduct() {
        return product;
    }

    public Payment getPaymentData() {
        return payment;
    }

    public void cancel() {
        paymentProvider.cancelPayment(payment.getId(), aBoolean -> {
            if (!aBoolean) {
                System.err.println("Erro ao cancelar o pagamento.");
            }
        });
    }
}