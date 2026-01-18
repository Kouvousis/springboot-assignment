package org.example.qnrassignment.service;

import lombok.RequiredArgsConstructor;
import org.example.qnrassignment.core.exceptions.ResourceNotFoundException;
import org.example.qnrassignment.dto.CreateOrderDTO;
import org.example.qnrassignment.dto.OrderDTO;
import org.example.qnrassignment.dto.UpdateOrderDTO;
import org.example.qnrassignment.model.Order;
import org.example.qnrassignment.model.User;
import org.example.qnrassignment.repository.OrderRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {
    private final OrderRepository orderRepository;

    /**
     * Create a new Order
     *
     * @param createOrderDTO DTO containing order creation data
     * @param user           User associated with the order
     * @return Created Order DTO
     */

    public OrderDTO createOrder(CreateOrderDTO createOrderDTO, User user) {
        Order order = Order.builder()
                .description(createOrderDTO.getDescription())
                .status(createOrderDTO.getStatus())
                .user(user)
                .build();

        Order savedOrder = orderRepository.save(order);
        return mapToDTO(savedOrder);
    }

    /**
     * Get paginated orders for a specific user
     *
     * @param user     User whose orders are to be fetched
     * @param pageable Pagination information
     * @return Paginated list of Order DTOs
     */

    @Transactional(readOnly = true)
    public Page<OrderDTO> getUserOrders(User user, Pageable pageable) {
        Page<Order> orders = orderRepository.findByUser(user, pageable);
        return orders.map(this::mapToDTO);
    }

    @Transactional(readOnly = true)
    public OrderDTO getOrderById(Long orderId, User user) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", orderId));

        if (!order.getUser().getId().equals(user.getId()) &&
                !isAdmin(user)) {
            throw new ResourceNotFoundException("Order", "id", orderId);
        }

        return mapToDTO(order);
    }

    @Transactional(readOnly = true)
    public OrderDTO getOrderByIdAndUser(Long orderId, User user) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", orderId));

        if (!order.getUser().getId().equals(user.getId()) &&
                !isAdmin(user)) {
            throw new ResourceNotFoundException("Order", "id", orderId);
        }

        return mapToDTO(order);
    }

    /**
     * Update an existing Order
     *
     * @param orderId        ID of the order to update
     * @param updateOrderDTO DTO containing updated order data
     * @param user           User associated with the order
     * @return Updated Order DTO
     */
    public OrderDTO updateOrder(Long orderId, UpdateOrderDTO updateOrderDTO, User user) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", orderId));

        if (!order.getUser().getId().equals(user.getId()) &&
                !isAdmin(user)) {
            throw new ResourceNotFoundException("Order", "id", orderId);
        }

        order.setDescription(updateOrderDTO.getDescription());
        order.setStatus(updateOrderDTO.getStatus());

        Order updatedOrder = orderRepository.save(order);
        return mapToDTO(updatedOrder);
    }

    /**
     * Delete an Order
     *
     * @param orderId ID of the order to delete
     * @param user    User associated with the order
     */
    public void deleteOrder(Long orderId, User user) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", orderId));

        if (!order.getUser().getId().equals(user.getId()) &&
                !isAdmin(user)) {
            throw new ResourceNotFoundException("Order", "id", orderId);
        }

        orderRepository.delete(order);
    }

    /**
     * Get paginated orders for a specific user filtered by status
     *
     * @param user     User whose orders are to be fetched
     * @param status   Status to filter orders
     * @param pageable Pagination information
     * @return Paginated list of Order DTOs
     */
    @Transactional(readOnly = true)
    public Page<OrderDTO> getOrdersByStatus(User user, String status, Pageable pageable) {
        Page<Order> orders = orderRepository.findByUserAndStatus(user, status, pageable);
        return orders.map(this::mapToDTO);
    }

    /**
     * Search orders by description keyword for a specific user
     *
     * @param keyword  Keyword to search in order descriptions
     * @param pageable Pagination information
     * @return Paginated list of Order DTOs matching the search criteria
     */
    @Transactional(readOnly = true)
    public Page<OrderDTO> searchOrders(String keyword, Pageable pageable) {
        Page<Order> orders = orderRepository.searchByDescription(keyword, pageable);
        return orders.map(this::mapToDTO);
    }

    /**
     * Map Order entity to OrderDTO
     *
     * @param order Order entity
     * @return Order DTO
     */
    private OrderDTO mapToDTO(Order order) {
        return OrderDTO.builder()
                .id(order.getId())
                .description(order.getDescription())
                .status(order.getStatus())
                .userId(order.getUser().getId())
                .username(order.getUser().getUsername())
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .build();
    }

    /**
     * Check if user has ADMIN role
     *
     * @param user User to check
     * @return true if user is admin
     */
    private boolean isAdmin(User user) {
        return user.getRole().name().equals("ADMIN");
    }
}
