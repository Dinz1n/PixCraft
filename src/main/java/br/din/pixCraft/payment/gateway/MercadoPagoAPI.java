package br.din.pixCraft.payment.gateway;

import br.din.pixCraft.payment.PaymentStatus;
import br.din.pixCraft.payment.order.Order;
import br.din.pixCraft.product.Product;
import com.mercadopago.MercadoPagoConfig;
import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.client.payment.PaymentCreateRequest;
import com.mercadopago.client.payment.PaymentPayerRequest;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.resources.payment.Payment;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class MercadoPagoAPI {
    private static PaymentClient client;
    private static String accessToken;

    public static void setAccessToken(String newToken) {
        accessToken = newToken;
        MercadoPagoConfig.setAccessToken(newToken);
        client = new PaymentClient();
        Bukkit.getLogger().info("[PixCraft] Access Token atualizado.");
    }

    public static CompletableFuture<Order> createNewOrder(Product product, double price, Player player) {
        PaymentCreateRequest paymentCreateRequest = PaymentCreateRequest.builder()
                .transactionAmount(new BigDecimal(price))
                .description(product.getName())
                .paymentMethodId("pix")
                .payer(
                        PaymentPayerRequest.builder()
                                .email(player.getName() + "@din.me")
                                .firstName(player.getName())
                                .lastName("")
                                .build())
                .build();

        return CompletableFuture.supplyAsync(() -> {
            Payment payment;
            try {
                payment = client.create(paymentCreateRequest);
                if (payment == null || payment.getId() == null) {
                    Bukkit.getLogger().severe("[PixCraft] Erro: criação do pagamento falhou e retornou null.");
                    return null;
                }
            } catch (MPApiException e) {
                Bukkit.getLogger().severe("[PixCraft] Erro ao criar pagamento: " + e.getMessage());
                Bukkit.getLogger().severe("[PixCraft] Código do erro: " + e.getApiResponse().getStatusCode());
                Bukkit.getLogger().severe("[PixCraft] Resposta da API: " + e.getApiResponse().getContent());
                return null;
            } catch (MPException e) {
                Bukkit.getLogger().severe("[PixCraft] Erro inesperado no Mercado Pago: " + e.getMessage());
                return null;
            }

            Order order = new Order(
                    player.getUniqueId(),
                    payment.getId(),
                    payment.getPointOfInteraction().getTransactionData().getQrCode(),
                    product,
                    PaymentStatus.fromString(payment.getStatus())
            );

            return order;
        });
    }

    public static PaymentStatus getPaymentStatus(Long paymentId) {
        try {
            Payment payment = client.get(paymentId);
            if (payment == null || payment.getStatus() == null) {
                Bukkit.getLogger().severe("[PixCraft] Erro: pagamento ID " + paymentId + " retornou status nulo.");
                return null;
            }
            return PaymentStatus.fromString(payment.getStatus());
        } catch (MPApiException | MPException e) {
            Bukkit.getLogger().severe("[PixCraft] Erro ao obter status do pagamento: " + e.getMessage());
            return null;
        }
    }
}