package com.hardware.store.resource;

import com.hardware.store.domain.Order;
import com.hardware.store.dto.AddressDTO;
import com.hardware.store.service.OrderService;
import com.sun.istack.NotNull;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("api/v1/orders")
@AllArgsConstructor(onConstructor = @__({@Autowired}))
public class OrderResource {

    private OrderService orderService;

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('CUSTOMER')")
    public ResponseEntity<Order> findById(@PathVariable @NotNull Long id) {
        return ResponseEntity.ok(orderService.findById(id));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('CUSTOMER')")
    public ResponseEntity<Order> placeOrder(Principal principal, @RequestBody @Valid AddressDTO addressDTO) {
        Order order = orderService.placeOrder(principal.getName(), addressDTO);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest().path("/{id}")
                .buildAndExpand(order.getId()).toUri();
        return ResponseEntity.created(location).body(order);
    }

    @GetMapping
    @PreAuthorize("hasAuthority('CUSTOMER')")
    public ResponseEntity<List<Order>> findAllOrdersByUser(Principal principal) {
        return ResponseEntity.ok(orderService.findOrdersByUser(principal.getName()));
    }

}
