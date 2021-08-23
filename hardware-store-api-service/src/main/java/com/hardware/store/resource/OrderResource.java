package com.hardware.store.resource;
import com.hardware.store.domain.Order;
import com.hardware.store.dto.AddressDTO;
import com.hardware.store.service.OrderService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("api/v1/orders")
@AllArgsConstructor(onConstructor = @__({@Autowired}))
public class OrderResource {

    private OrderService orderService;

    @PostMapping
    @PreAuthorize("hasAuthority('CUSTOMER')")
    public ResponseEntity<Order> placeOrder(Principal principal, @RequestBody @Valid AddressDTO addressDTO) {
        return ResponseEntity.ok(orderService.placeOrder(principal.getName(), addressDTO));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('CUSTOMER')")
    public ResponseEntity<List<Order>> findAllOrdersByUser(Principal principal) {
        return ResponseEntity.ok(orderService.findOrdersByUser(principal.getName()));
    }

}
