package advance.lld;

import advance.lld.model.*;
import advance.lld.service.BillingService;
import advance.lld.service.OrderService;
import advance.lld.service.PaymentService;
import advance.lld.service.TableAllocationService;
import advance.lld.strategy.CardPaymentStrategy;
import advance.lld.strategy.CashPaymentStrategy;
import advance.lld.strategy.PaymentStrategy;
import advance.lld.strategy.UpiPaymentStrategy;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

public class ClientDemo {

    public void demo() {

        Restaurant restaurant = new Restaurant(1L, "Restaurant1");
        List<Table> tableList = new ArrayList<>();
        Table table1 = new Table(1L, "table1", 4);
        Table table2 = new Table(2L, "table2", 6);
        tableList.add(table1);
        tableList.add(table2);
        List<MenuItem> menuItemList = new ArrayList<>();
        MenuItem menuItem = new MenuItem(1L, "ice cream", 20.0, Boolean.TRUE);
        menuItemList.add(menuItem);
        Menu menu = new Menu(menuItemList);

        Branch branch = new Branch(menu, tableList);
        Chef chef = new Chef();
        Waiter waiter = new Waiter();

        branch.addChef(chef, "chef");
        branch.addWaiter(waiter, "waiter");
        restaurant.addBranch(branch);

        Customer customer = new Customer();

        TableAllocationService tableAllocationService = new TableAllocationService();
        LocalDateTime fromTime = LocalDateTime.now();
        LocalDateTime toTime = fromTime.plusHours(5);

        //reserve table
        tableAllocationService.reserveTable(branch, customer, fromTime, toTime, 4);
        tableAllocationService.allocateWalkIn(branch, customer, 4, waiter);

        OrderService orderService =  new OrderService();
        Order order = orderService.getOrCreateOrder(waiter, branch.getTableList().get(0));

        OrderItem orderItem = orderService.addItem(order, menuItem, 2, waiter);

        orderService.prepareOrderItem(orderItem, chef);
        orderService.markOrderItemReady(orderItem);

        BillingService billingService = new BillingService(tableAllocationService);
        BillComponent billComponent = new ServiceChargeDecorator(new BaseOrderAmount(order), 5.0);
        Bill bill = billingService.getBill(order, billComponent);

        Map<PaymentMethod, PaymentStrategy> paymentMethodPaymentStrategyMap = new HashMap<>();
        paymentMethodPaymentStrategyMap.put(PaymentMethod.UPI, new UpiPaymentStrategy());
        paymentMethodPaymentStrategyMap.put(PaymentMethod.CARD, new CardPaymentStrategy());
        paymentMethodPaymentStrategyMap.put(PaymentMethod.CASH, new CashPaymentStrategy());

        PaymentService paymentService = new PaymentService(billingService, paymentMethodPaymentStrategyMap);
        paymentService.makePayment(bill, PaymentMethod.CARD, 10.0);
        paymentService.makePayment(bill, PaymentMethod.CASH, 10.0);
        tableAllocationService.releaseTable(bill.getOrder().getTable());
    }

}
