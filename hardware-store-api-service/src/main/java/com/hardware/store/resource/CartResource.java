package com.hardware.store.resource;

import com.hardware.store.domain.Cart;
import com.hardware.store.dto.CartItemDTO;
import com.hardware.store.service.CartService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.security.Principal;

@RestController
@RequestMapping("api/v1/carts")
@AllArgsConstructor(onConstructor = @__({@Autowired}))
public class CartResource {

    private CartService cartService;

    @GetMapping("/{userId}")
    @PreAuthorize("hasAuthority('CUSTOMER')")
    public ResponseEntity<Cart> findCartForUser(@PathVariable @NotNull Long userId) {
        return ResponseEntity.ok(cartService.findCartByUserOrEmpty(userId));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('CUSTOMER')")
    public ResponseEntity<Cart> findCartForUser(Principal principal) {
        return ResponseEntity.ok(cartService.findCartByUserOrEmpty(principal.getName()));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('CUSTOMER')")
    public ResponseEntity<Cart> addToCart(Principal principal, @RequestBody @Valid CartItemDTO cartItemDTO) {
        return ResponseEntity.ok(cartService.addToCart(principal.getName(), cartItemDTO));
    }

    @DeleteMapping("/cartItems/{cartItemId}")
    @PreAuthorize("hasAuthority('CUSTOMER')")
    public ResponseEntity<Cart> deleteFromCart(Principal principal, @PathVariable @NotNull Long cartItemId) {
        return ResponseEntity.ok(cartService.deleteFromCart(principal.getName(), cartItemId));
    }

    @DeleteMapping
    @PreAuthorize("hasAuthority('CUSTOMER')")
    public ResponseEntity<Cart> emptyCart(Principal principal) {
        return ResponseEntity.ok(cartService.emptyCartForUser(principal.getName()));
    }
}
