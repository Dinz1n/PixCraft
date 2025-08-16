package br.com.din.pixcraft.payment.gateway;

import br.com.din.pixcraft.payment.Payment;
import br.com.din.pixcraft.payment.PaymentStatus;
import br.com.din.pixcraft.product.Product;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.squareup.okhttp.*;

import java.io.IOException;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.logging.Logger;

public class MercadoPagoService implements PaymentProvider {
    private String accessToken;
    private static final transient Logger logger = Logger.getLogger("PixCraft");
    private static final Gson gson = new Gson();
    private static final OkHttpClient client = new OkHttpClient();
    private static final MediaType JSON = MediaType.parse("application/json");

    public MercadoPagoService() {
    }

    @Override
    public void createPayment(String payerName, Product product, Consumer<Payment> callback) {
        JsonObject payer = new JsonObject();
        payer.addProperty("first_name", payerName);
        payer.addProperty("email", payerName + "@pixcraft.com");

        JsonObject body = new JsonObject();
        body.add("payer", payer);
        body.addProperty("payment_method_id", "pix");
        body.addProperty("description", product.getName());
        body.addProperty("transaction_amount", product.getPrice());
        body.addProperty("installments", 1);

        RequestBody requestBody = RequestBody.create(JSON, body.toString());
        Request request = new Request.Builder()
                .url("https://api.mercadopago.com/v1/payments")
                .addHeader("Content-Type", "application/json")
                .addHeader("X-Idempotency-Key", UUID.randomUUID().toString())
                .addHeader("Authorization", "Bearer " + accessToken)
                .post(requestBody)
                .build();

        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Response response) throws IOException {
                String responseBody = "";
                try {
                    responseBody = response.body().string();
                    JsonObject jsonObject = gson.fromJson(responseBody, JsonObject.class);

                    long paymentId = jsonObject.get("id").getAsLong();
                    String qrData = jsonObject
                            .getAsJsonObject("point_of_interaction")
                            .getAsJsonObject("transaction_data")
                            .get("qr_code").getAsString();

                    callback.accept(new Payment(paymentId, qrData));
                } catch (Exception e) {
                    logger.severe("Erro na criação do pagamento. Resposta da API:");
                    logger.severe(responseBody);
                    callback.accept(null);
                }
            }

            @Override
            public void onFailure(Request request, IOException e) {
                logger.severe("Erro na requisição");
                e.printStackTrace();
            }
        });
    }

    @Override
    public void getStatus(long paymentId, Consumer<PaymentStatus> callback) {
        Request request = new Request.Builder()
                .url("https://api.mercadopago.com/v1/payments/" + paymentId)
                .addHeader("Content-Type", "application/json")
                .addHeader("X-Idempotency-Key", UUID.randomUUID().toString())
                .addHeader("Authorization", "Bearer " + accessToken)
                .get()
                .build();

        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Response response) throws IOException {
                try {
                    String responseBody = response.body().string();
                    JsonObject jsonObject = gson.fromJson(responseBody, JsonObject.class);

                    String paymentStatus = jsonObject.get("status").getAsString();
                    if (paymentStatus.equalsIgnoreCase("expired")) {
                        callback.accept(PaymentStatus.CANCELLED);
                    } else {
                        callback.accept(PaymentStatus.valueOf(paymentStatus.toUpperCase()));
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Request request, IOException e) {
                System.err.println("[PixCraft] Falha na requisição");
                e.printStackTrace();
            }
        });
    }

    @Override
    public void cancelPayment(long paymentId, Consumer<Boolean> callback) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("status", PaymentStatus.CANCELLED.name().toLowerCase());

        RequestBody requestBody = RequestBody.create(JSON, jsonObject.toString());
        Request request = new Request.Builder()
                .url("https://api.mercadopago.com/v1/payments/" + paymentId)
                .addHeader("Content-Type", "application/json")
                .addHeader("X-Idempotency-Key", UUID.randomUUID().toString())
                .addHeader("Authorization", "Bearer " + accessToken)
                .put(requestBody)
                .build();

        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Response response) throws IOException {
                try {
                    String responseBody = response.body().string();
                    callback.accept(response.code() == 200 || response.code() == 201? true : false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Request request, IOException e) {
                System.err.println("[PixCraft] Falha na requisição");
                e.printStackTrace();
            }
        });
    }

    @Override
    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
}