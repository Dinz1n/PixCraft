package br.din.pixCraft.payment.webhook;

import br.din.pixCraft.PixCraft;
import br.din.pixCraft.listeners.custom.PaymentStatusUpdateEvent;
import br.din.pixCraft.order.Order;
import br.din.pixCraft.order.OrderManager;
import br.din.pixCraft.payment.PaymentStatus;

import br.din.pixCraft.payment.gateway.MercadoPagoAPI;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.bukkit.Bukkit;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

public class WebhookServer {
    private HttpServer server;
    private final int port;

    public WebhookServer(int port) {
        this.port = port;
    }

    public void start() {
        try {
            server = HttpServer.create(new InetSocketAddress(port), 0);
            server.createContext("/mercadopago", new WebhookHandler());
            server.setExecutor(null);
            server.start();
            Bukkit.getLogger().info("[PixCraft] Webhook ativo na porta " + port);
        } catch (IOException e) {
            Bukkit.getLogger().severe("[PixCraft] Erro ao iniciar o Webhook: " + e.getMessage());
        }
    }

    public void stop() {
        if (server != null) {
            server.stop(0);
            Bukkit.getLogger().info("[PixCraft] Webhook encerrado.");
        }
    }

    static class WebhookHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                // Lendo o corpo da requisição
                InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8);
                BufferedReader br = new BufferedReader(isr);
                StringBuilder requestBody = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    requestBody.append(line);
                }
                br.close();
                isr.close();

                // Log do JSON recebido
                Bukkit.getLogger().info("[PixCraft] Webhook recebido: " + requestBody);

                try {
                    JSONObject json = new JSONObject(requestBody.toString());
                    long paymentId = json.getJSONObject("data").getLong("id"); // Pegando corretamente o ID

                    Bukkit.getScheduler().runTask(PixCraft.getInstance(), () -> {
                        PaymentStatus status = MercadoPagoAPI.getPaymentStatus(paymentId);
                        Order order = OrderManager.getOrderById(paymentId);
                        if (order != null && order.getStatus() != status) {
                            order.setStatus(status);
                            Bukkit.getPluginManager().callEvent(new PaymentStatusUpdateEvent(order));
                        }
                    });

                    // Log de resposta bem-sucedida
                    Bukkit.getLogger().info("[PixCraft] Webhook processado com sucesso para Payment ID: " + paymentId);
                    exchange.sendResponseHeaders(200, 0);
                } catch (Exception e) {
                    Bukkit.getLogger().severe("[PixCraft] Erro ao processar webhook: " + e.getMessage());
                    exchange.sendResponseHeaders(500, 0); // Responde com erro 500 em caso de falha
                }
            } else {
                exchange.sendResponseHeaders(405, 0);
            }
            exchange.getResponseBody().close();
        }
    }
}