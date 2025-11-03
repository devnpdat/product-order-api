package com.example.productorder.service;

import com.example.productorder.dto.OrderRequestDTO;
import com.example.productorder.dto.OrderResponseDTO;
import com.example.productorder.exception.InsufficientStockException;
import com.example.productorder.exception.ResourceNotFoundException;
import com.example.productorder.model.Order;
import com.example.productorder.model.OrderItem;
import com.example.productorder.model.Product;
import com.example.productorder.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductService productService;

    public List<OrderResponseDTO> getAllOrders() {
        return orderRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public OrderResponseDTO getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));
        return convertToDTO(order);
    }

    public OrderResponseDTO getOrderByOrderNumber(String orderNumber) {
        Order order = orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with number: " + orderNumber));
        return convertToDTO(order);
    }

    public List<OrderResponseDTO> getOrdersByStatus(Order.OrderStatus status) {
        return orderRepository.findByStatus(status).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public OrderResponseDTO createOrder(OrderRequestDTO requestDTO) {
        // Create order
        Order order = new Order();
        order.setOrderNumber(generateOrderNumber());
        order.setCustomerName(requestDTO.getCustomerName());
        order.setCustomerEmail(requestDTO.getCustomerEmail());
        order.setCustomerPhone(requestDTO.getCustomerPhone());
        order.setStatus(Order.OrderStatus.PENDING);

        // Process order items
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (OrderRequestDTO.OrderItemDTO itemDTO : requestDTO.getItems()) {
            Product product = productService.getProductById(itemDTO.getProductId());

            // Check stock
            if (product.getStock() < itemDTO.getQuantity()) {
                throw new InsufficientStockException(
                    "Insufficient stock for product: " + product.getName() +
                    ". Available: " + product.getStock() + ", Requested: " + itemDTO.getQuantity()
                );
            }

            // Create order item
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setQuantity(itemDTO.getQuantity());
            orderItem.setPrice(product.getPrice());
            orderItem.setSubtotal(product.getPrice().multiply(BigDecimal.valueOf(itemDTO.getQuantity())));

            order.getItems().add(orderItem);
            totalAmount = totalAmount.add(orderItem.getSubtotal());

            // Update product stock
            product.setStock(product.getStock() - itemDTO.getQuantity());
        }

        order.setTotalAmount(totalAmount);
        Order savedOrder = orderRepository.save(order);

        return convertToDTO(savedOrder);
    }

    @Transactional
    public OrderResponseDTO updateOrderStatus(Long id, Order.OrderStatus status) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));
        order.setStatus(status);
        Order updatedOrder = orderRepository.save(order);
        return convertToDTO(updatedOrder);
    }

    @Transactional
    public void cancelOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));

        if (order.getStatus() == Order.OrderStatus.DELIVERED ||
            order.getStatus() == Order.OrderStatus.CANCELLED) {
            throw new IllegalStateException("Cannot cancel order with status: " + order.getStatus());
        }

        // Restore product stock
        for (OrderItem item : order.getItems()) {
            Product product = item.getProduct();
            product.setStock(product.getStock() + item.getQuantity());
        }

        order.setStatus(Order.OrderStatus.CANCELLED);
        orderRepository.save(order);
    }

    private String generateOrderNumber() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        return "ORD-" + timestamp;
    }

    private OrderResponseDTO convertToDTO(Order order) {
        OrderResponseDTO dto = new OrderResponseDTO();
        dto.setId(order.getId());
        dto.setOrderNumber(order.getOrderNumber());
        dto.setCustomerName(order.getCustomerName());
        dto.setCustomerEmail(order.getCustomerEmail());
        dto.setCustomerPhone(order.getCustomerPhone());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setStatus(order.getStatus());
        dto.setCreatedAt(order.getCreatedAt());
        dto.setUpdatedAt(order.getUpdatedAt());

        List<OrderResponseDTO.OrderItemResponseDTO> itemDTOs = order.getItems().stream()
                .map(item -> {
                    OrderResponseDTO.OrderItemResponseDTO itemDTO = new OrderResponseDTO.OrderItemResponseDTO();
                    itemDTO.setId(item.getId());
                    itemDTO.setProductId(item.getProduct().getId());
                    itemDTO.setProductName(item.getProduct().getName());
                    itemDTO.setQuantity(item.getQuantity());
                    itemDTO.setPrice(item.getPrice());
                    itemDTO.setSubtotal(item.getSubtotal());
                    return itemDTO;
                })
                .collect(Collectors.toList());

        dto.setItems(itemDTOs);
        return dto;
    }
}

