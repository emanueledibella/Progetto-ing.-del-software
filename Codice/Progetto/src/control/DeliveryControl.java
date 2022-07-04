package control;

import jdbc.DBAziendaManager;
import model.Order;

public class DeliveryControl {
    private Order order;
    public DBAziendaManager dbAziendaManager = new DBAziendaManager();

    public void setOrder(Order order) {
        this.order = order;
    }

    public Order getOrder() {
        return this.order;
    }
}
