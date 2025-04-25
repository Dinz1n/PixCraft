package br.din.pixCraft.payment.gateway;

import br.din.pixCraft.payment.PaymentStatus;
import com.mercadopago.MercadoPagoConfig;
import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.client.payment.PaymentCreateRequest;
import com.mercadopago.client.payment.PaymentPayerRequest;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.resources.payment.Payment;
import org.bukkit.Bukkit;

import java.math.BigDecimal;
import java.util.concurrent.CompletableFuture;

public class MercadoPagoAPI {
    private static PaymentClient client;
    private static String accessToken;

    public static void setAccessToken(String newToken) {
        accessToken = newToken;
        MercadoPagoConfig.setAccessToken(newToken);
        client = new PaymentClient(); // Recria o client com o novo token
        Bukkit.getLogger().info("[PixCraft] Access Token atualizado.");
    }

    public static CompletableFuture<Long> createPayment(String productName, double price, String playerName) {
        PaymentCreateRequest paymentCreateRequest = PaymentCreateRequest.builder()
                .transactionAmount(new BigDecimal(price))
                .description(productName)
                .paymentMethodId("pix")
                .payer(
                        PaymentPayerRequest.builder()
                                .email(playerName + "@din.me")
                                .firstName(playerName)
                                .lastName("")
                                .build())
                .build();

        return CompletableFuture.supplyAsync(() -> {
            try {
                Payment payment = client.create(paymentCreateRequest);
                if (payment == null || payment.getId() == null) {
                    Bukkit.getLogger().severe("[PixCraft] Erro: criação do pagamento falhou e retornou null.");
                    return null;
                }
                return payment.getId();
            } catch (MPApiException e) {
                Bukkit.getLogger().severe("[PixCraft] Erro ao criar pagamento: " + e.getMessage());
                Bukkit.getLogger().severe("[PixCraft] Código do erro: " + e.getApiResponse().getStatusCode());
                Bukkit.getLogger().severe("[PixCraft] Resposta da API: " + e.getApiResponse().getContent());
                return null;
            } catch (MPException e) {
                Bukkit.getLogger().severe("[PixCraft] Erro inesperado no Mercado Pago: " + e.getMessage());
                return null;
            }
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

    public static void cancelPayment(Long paymentId) {
        PaymentClient client = new PaymentClient();
        try {
            client.cancel(paymentId);
        } catch (MPException e) {
            throw new RuntimeException(e);
        } catch (MPApiException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getQrCode(Long paymentId) {
        try {
            Payment payment = client.get(paymentId);
            if (payment == null) {
                Bukkit.getLogger().severe("[PixCraft] Erro: pagamento retornado é nulo.");
                return null;
            }
            if (payment.getPointOfInteraction() == null || payment.getPointOfInteraction().getTransactionData() == null) {
                Bukkit.getLogger().severe("[PixCraft] Erro: informações de transação não disponíveis para pagamento ID: " + paymentId);
                return null;
            }
            return payment.getPointOfInteraction().getTransactionData().getQrCode();
        } catch (MPApiException | MPException e) {
            Bukkit.getLogger().severe("[PixCraft] Erro ao obter QR Code Base64: " + e.getMessage());
        }
        return null;
    }
}