package com.geecommerce.checkout.controller;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.geecommerce.checkout.enums.OrderStatus;
import com.geecommerce.checkout.model.Order;
import com.geecommerce.checkout.service.CheckoutService;
import com.geecommerce.core.service.QueryOptions;
import com.geecommerce.core.service.api.GlobalColumn;
import com.geecommerce.core.system.model.Country;
import com.geecommerce.core.type.Id;
import com.geecommerce.core.web.BaseController;
import com.geecommerce.country.service.CountryService;
import com.geecommerce.customer.model.Customer;
import com.geemvc.annotation.Controller;
import com.geemvc.annotation.Request;
import com.geemvc.bind.param.annotation.Param;
import com.geemvc.bind.param.annotation.PathParam;
import com.geemvc.view.bean.Result;
import com.google.inject.Inject;

@Controller
@Request("/order-summary")
public class OrderSummaryController extends BaseController {

    private final CheckoutService checkoutService;
    private final CountryService countryService;

    private static final Logger LOG = LogManager.getLogger(OrderSummaryController.class);

    @Inject
    public OrderSummaryController(CheckoutService checkoutService, CountryService countryService) {
        this.checkoutService = checkoutService;
        this.countryService = countryService;
    }

    @Request("/orders")
    public Result ordersView(@Param("orderFilterDate") String orderFilterDate, @Param("view") String view) {
        return view("order_summary/orders").bind("orders", findOrders(orderFilterDate, view));
    }

    @Request("/overview")
    public Result overview(@Param("orderFilterDate") String orderFilterDate) {
        if (!isCustomerLoggedIn())
            return redirect("/customer/account/login");

        return view("order_summary/overview").bind("orders", findOrders(orderFilterDate, null))
            .bind("orderCreatedDates", getOrderCreatedDates(orderFilterDate))
            .bind("orderFilterDate", orderFilterDate);
    }

    @Request("/detail/{id}")
    public Result detail(@PathParam("id") Id id) {
        Order order = null;
        if (isCustomerLoggedIn()) {
            order = checkoutService.getOrder(id);
        }

        return view("order_summary/detail").bind("order", order);
    }

    private Map<String, String> getCountries() {
        List<Country> countries = countryService.getAll();
        Map<String, String> countryMap = new HashMap<>();
        countries.stream().forEach(country -> countryMap.put(country.getCode(), country.getName().getStr()));
        return countryMap;
    }

    public Map<String, String> getOrderCreatedDates(String orderFilterDate) {

        Map<String, String> dates = new LinkedHashMap<>();
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);

        dates.put("last30", "");
        dates.put("months-6", "");

        for (int i = 0; i < 5; i++)
            dates.put(String.valueOf(currentYear - i), "");

        dates.keySet().stream().filter(key -> key.equals(orderFilterDate))
            .forEach(key -> dates.replace(key, "selected"));
        return dates;
    }

    private List<Order> findOrders(String orderFilterDate, String view) {

        Customer customer = getLoggedInCustomer();
        if (customer != null) {
            Map<String, Object> filter = new HashMap<>();
            filter.put(Order.Col.CUSTOMER_ID, customer.getId());
            if ("cancelled".equals(view)) {
                filter.put(Order.Col.ORDER_STATUS, OrderStatus.CANCELED.toId());
            } else if ("open".equals(view)) {
                List<Integer> statuses = new ArrayList<>();
                statuses.add(OrderStatus.NEW.toId());
                statuses.add(OrderStatus.PENDING.toId());
                statuses.add(OrderStatus.ACCEPTED.toId());
                statuses.add(OrderStatus.CONFIRMED.toId());
                statuses.add(OrderStatus.DISPATCHED.toId());
                filter.put(Order.Col.ORDER_STATUS, statuses);
            }

            if (orderFilterDate != null && !"all".equals(orderFilterDate)) {
                Map<String, Object> f = new HashMap<>();
                DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy-MM-dd");

                if ("last30".equals(orderFilterDate)) {
                    f.put("$gte", fmt.print(new DateTime().minusDays(30)));
                } else if (orderFilterDate.equals("months-6")) {
                    f.put("$gte", fmt.print(new DateTime().minusMonths(6)));
                } else {
                    int currentYear = Calendar.getInstance().get(Calendar.YEAR);
                    int year = Integer.valueOf(orderFilterDate).intValue();
                    if (year == currentYear) {
                        f.put("$gte", orderFilterDate + "-01-01");
                    } else if (year < currentYear) {
                        String fromDate = fmt.print(new DateTime(orderFilterDate));
                        String toDate = fmt.print(new DateTime(orderFilterDate).plusYears(1));
                        f.put("$gte", fromDate);
                        f.put("$lt", toDate);
                    }
                }

                filter.put(GlobalColumn.CREATED_ON, f);
            }

            return checkoutService.getOrders(filter,
                QueryOptions.builder().sortByDesc(GlobalColumn.CREATED_ON).build());
        }

        return new ArrayList<>();
    }

}
