package com.example.demo.service;

/* 2 problems + 1 task
+ the size price as of now is getsizeprice menuitem one, but after jasper updates size to small and large seperately
bring a disticntion logic here probably if else like if size = large getlargeprice and vice versa
+ also the logic for loyalty is left, as of now user has a field but customer and staff class does not either connect them
in a way that its included or ask them to include or find a way for the loyalty logic to work
+add comments to order controller and service for better understanding also check if entitylayer class has some complex field name which needs commenting
 */
import com.example.demo.entity.*;
import com.example.demo.repository.*;
import java.util.List;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.aspectj.weaver.ast.Or;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service

public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepo orderItemRepo;
    private final UserRepository userRepository;
    private final MenuItemRepository menuItemRepository;
    private final CustomerRepository customerRepository;
    private final StaffRepository staffRepository;

    @Value("${loyalty.buy.count}")//this is to read loyalty count rule and threshold from application.properties

    private int loyaltyBuyCount;

    @Value("${station.opening.hour}")//opening hours from application.properties
    private int openingHour;

    @Value("${station.closing.hour}")//closing hours from application.properties
    private int closingHour;


    //constructor to take in repositories
    public OrderService(OrderRepository orderRepository, OrderItemRepo orderItemRepo, UserRepository userRepository, MenuItemRepository menuItemRepository, CustomerRepository customerRepository, StaffRepository staffRepository) {
        this.orderRepository = orderRepository;
        this.orderItemRepo = orderItemRepo;
        this.userRepository = userRepository;
        this.menuItemRepository = menuItemRepository;
        this.customerRepository = customerRepository;
        this.staffRepository = staffRepository;
    }

    public void userRoleDefinition(Long userId){
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("No user found"));

        switch(user.getRole()){
            case CUSTOMER -> connectCustomer(user);
            case STAFF -> connectStaff(user);
            default -> throw new RuntimeException("role not found");
        }
    }

    private void connectCustomer(User user){
        Customer customer = customerRepository.findByEmail(user.getEmail()).orElseThrow(() -> new RuntimeException("Customer not found"));
    }

    private void connectStaff(User user){
        Staff staff = staffRepository.findById(user.getId()).orElseThrow(() -> new RuntimeException("Staff not found"));
    }


    /*
     *The below method helps to place order
     *We build the order with initial order status new, total price none(zero), and the false(ie. no) for loyalty.
     *Then the for loop finds the item along with its size and availibility.
     *Later it adds up the items and gets the total and the order is saved in the MYSQL.
     */


    @Transactional//To make sure the entire method succeeds, or nothing is saved to eliminate half success confusing transaction in database
    public Order placeOrder(User customer, String guestName, List<Long>menuItemIds, List<String>sizes, List<Integer>quantities){
        Order order = new Order(customer, guestName, OrderStatus.NEW,BigDecimal.ZERO ,false);

        BigDecimal total = BigDecimal.ZERO;

        for (int i =0; i< menuItemIds.size(); i++){
            MenuItem menuItem = menuItemRepository.findById(menuItemIds.get(i))
                    .orElseThrow(()-> new RuntimeException("Item not found"));//throws exception if the item not found through id

            if(!menuItem.getIsAvailable()){
                throw new RuntimeException(menuItem.getName()+" is not available");
            }

            String size = sizes.get(i);
            BigDecimal price;

            if(size.equalsIgnoreCase("LARGE")){
                price = menuItem.getLargePrice();
            }else{
                price = menuItem.getRegularPrice();
            }


            OrderItem item = new OrderItem(order, menuItem, size, quantities.get(i), price );

            order.getItems().add(item);
            total = total.add(price.multiply(BigDecimal.valueOf(quantities.get(i))));

        }
        order.setTotalPrice(total);
        return orderRepository.save(order);
    }


    /*
    This method is for staff to update the order alongwith, validate statustransition and handleloyalty method working
    as a complimentary to help with changing status and handling loyalty
     */

    @Transactional
    public Order updateOrderStatus(Long orderId, OrderStatus newStatus){
        //find the order by id or else throw exception
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new RuntimeException("Order not found"));
        //validatestatustransition method calls if the status change is valid as given in the Enum'orderstatus'
        validateStatusTransition(order.getStatus(), newStatus);
        order.setStatus(newStatus);//updating the status
        order.setLastStatusChange(LocalDateTime.now());// records the time while the status was changed

        if(newStatus == OrderStatus.COLLECTED){//after the order is collected the addition is recorded towards the loyalty scheme
            handleLoyalty(order);
        }
        return orderRepository.save(order);//saving the order
    }

    private void validateStatusTransition(OrderStatus current, OrderStatus next){

        boolean valid = switch (current) {
            case NEW -> next == OrderStatus.ACCEPTED || next == OrderStatus.CANCELLED;//this means from the status new the order can only be changed to status = accepted/cancelled

            case ACCEPTED -> next == OrderStatus.PREPARING || next == OrderStatus.CANCELLED;

            case PREPARING -> next == OrderStatus.READY || next == OrderStatus.CANCELLED;

            case READY -> next == OrderStatus.COLLECTED || next == OrderStatus.CANCELLED;

            case COLLECTED, CANCELLED -> false;
        };
            if(!valid){
                throw new RuntimeException("Cannot change status from " + current + " to " + next);
            }

    }

    private void handleLoyalty(Order order){
        User customer = order.getCustomer();
        if(customer == null) return;// if the customer is null, ie. guest
//completed order and loyalty count goes up by one
        customer.setTotalCompletedOrders( customer.getTotalCompletedOrders() + 1);
        customer.setLoyaltyCount(customer.getLoyaltyCount() + 1);
//after loyalty count is reached '9' it resets to zero
        if (customer.getLoyaltyCount() >= loyaltyBuyCount){
            customer.setLoyaltyCount(0);//
        }
        userRepository.save(customer);
    }

    public List<Order> getOrderForCustomer(User customer){//returns the order history of a customer through this method
        return orderRepository.findByCustomer(customer);
    }

    public List<Order> getActiveOrderForStaff(){//staff can see order status matching new, accepted, preparing as these are the live orders
        return orderRepository.findByStatusIn(List.of(OrderStatus.NEW, OrderStatus.ACCEPTED, OrderStatus.PREPARING, OrderStatus.READY));
    }


    public Order getOrderById(Long orderId){//can fetch a specific order through its id. and if no order is found it throws exception
        return orderRepository.findById(orderId).orElseThrow(() -> new RuntimeException("Order not found"));
    }
}
