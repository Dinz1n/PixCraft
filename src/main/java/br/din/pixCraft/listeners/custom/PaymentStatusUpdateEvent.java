package br.din.pixCraft.listeners.custom;

import br.din.pixCraft.order.Order;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PaymentStatusUpdateEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final Order order;

    public PaymentStatusUpdateEvent(Order order) {
        this.order = order;
    }

    public Order getOrder() {
        return order;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}