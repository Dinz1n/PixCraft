package br.com.din.pixcraft.listeners.custom;

import br.com.din.pixcraft.order.Order;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class PaymentUpdateEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final Order order;

    public PaymentUpdateEvent(Order order) {
        this.order = order;
    }

    public Order getOrder() {
        return order;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    @NotNull
    public static HandlerList getHandlerList() {
        return handlers;
    }
}
