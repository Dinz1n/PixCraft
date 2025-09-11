package br.com.din.pixcraft.payment.verification;

import br.com.din.pixcraft.listeners.custom.PaymentUpdateEvent;
import br.com.din.pixcraft.order.Order;
import br.com.din.pixcraft.order.OrderManager;
import br.com.din.pixcraft.payment.PaymentStatus;
import br.com.din.pixcraft.payment.gateway.PaymentProvider;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.net.InetSocketAddress;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class Webhook implements PaymentChecker {
    private final JavaPlugin plugin;
    private final PaymentProvider paymentProvider;
    private final int port;
    private final HttpServer server;
    private final Gson gson = new Gson();
    private final OrderManager orderManager;
    private final Logger logger = Logger.getLogger("HTTP Server");

    public Webhook(JavaPlugin plugin, PaymentProvider paymentProvider, int port, OrderManager orderManager) {
        this.plugin = plugin;
        this.paymentProvider = paymentProvider;
        this.port = port;
        this.orderManager = orderManager;
        try {
            server = HttpServer.create(new InetSocketAddress(port), 0);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void start() {
        server.createContext("/payment", exchange ->  {
            try {
                String body = readBody(exchange.getRequestBody());
                JsonObject requestBody = gson.fromJson(body, JsonObject.class);
                if (requestBody == null) {
                    sendResponse(exchange, 400, "Invalid payload");
                    return;
                }

                String action = requestBody.has("action") ? requestBody.get("action").getAsString() : "";
                if (!"payment.updated".equalsIgnoreCase(action)) {
                    sendResponse(exchange, 200, "Ignored");
                    return;
                }

                if (!requestBody.has("data") || !requestBody.getAsJsonObject("data").has("id")) {
                    sendResponse(exchange, 400, "Invalid payload");
                    return;
                }

                long id = requestBody.getAsJsonObject("data").get("id").getAsLong();


                Order order = orderManager.getOrderByPaymentId(id);

                if (order == null) {
                    sendResponse(exchange, 404, "Order not found");
                    return;
                }

                paymentProvider.getStatus(id, paymentStatus -> {
                    try {
                        if (paymentStatus.equals(PaymentStatus.PENDING)) return;

                        Bukkit.getScheduler().runTask(plugin, () -> {
                            order.getPayment().setStatus(paymentStatus);
                            orderManager.addOrder(order);
                            Bukkit.getPluginManager().callEvent(new PaymentUpdateEvent(order));
                        });
                    } catch (Exception ex) {
                        logger.severe("Erro ao atualizar status do pedido: " + ex.getMessage());
                    }
                });

                sendResponse(exchange, 200, "OK");
            } catch (Exception e) {
                logger.severe("Erro ao processar webhook: " + e.getMessage());
                sendResponse(exchange, 500, "Internal Server Error");
            }
        });
        server.start();
        logger.info("Servidor iniciado na porta " +  port + ".");
    }

    @Override
    public void stop() {
        server.stop(0);
    }

    private void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        exchange.sendResponseHeaders(statusCode, response.getBytes().length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes());
        }
    }

    private static String readBody(InputStream inputStream) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            return reader.lines().collect(Collectors.joining("\n"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}