package org.example.qnrassignment.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.qnrassignment.dto.CreateOrderDTO;
import org.example.qnrassignment.dto.OrderDTO;
import org.example.qnrassignment.dto.UpdateOrderDTO;
import org.example.qnrassignment.model.User;
import org.example.qnrassignment.service.OrderService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("api/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderDTO> createOrder(@Valid @RequestBody CreateOrderDTO createOrderDTO,
                                                @AuthenticationPrincipal User user) {
        OrderDTO createdOrder = orderService.createOrder(createOrderDTO, user);
        return new ResponseEntity<>(createdOrder, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<Page<OrderDTO>> getUserOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction,
            @AuthenticationPrincipal User user) {

        Sort.Direction sortDirection = direction.equalsIgnoreCase("asc")
                ? Sort.Direction.ASC
                : Sort.Direction.DESC;

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        Page<OrderDTO> orders = orderService.getUserOrders(user, pageable);

        return ResponseEntity.ok(orders);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderDTO> getOrderById(@PathVariable Long id, @AuthenticationPrincipal User user) {
        OrderDTO orderDTO = orderService.getOrderById(id, user);
        return ResponseEntity.ok(orderDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<OrderDTO> updateOrder(@PathVariable Long id,
                                                @Valid @RequestBody UpdateOrderDTO updateOrderDTO,
                                                @AuthenticationPrincipal User user) {
        OrderDTO updatedOrder = orderService.updateOrder(id, updateOrderDTO, user);
        return ResponseEntity.ok(updatedOrder);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id, @AuthenticationPrincipal User user) {
        orderService.deleteOrder(id, user);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("status/{status}")
    public ResponseEntity<Page<OrderDTO>> getOrdersByStatus(
            @PathVariable String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal User user) {


        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<OrderDTO> orders = orderService.getOrdersByStatus(user, status, pageable);

        return ResponseEntity.ok(orders);
    }

    @GetMapping("/search")
    public ResponseEntity<Page<OrderDTO>> getOrdersBySearch(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<OrderDTO> orders = orderService.searchOrders(query, pageable);

        return ResponseEntity.ok(orders);
    }

}
