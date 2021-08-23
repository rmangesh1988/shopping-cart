package com.hardware.store.service;

import com.hardware.store.domain.*;
import com.hardware.store.dto.CartItemDTO;
import com.hardware.store.exception.EntityNotFoundException;
import com.hardware.store.repository.CartRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@AllArgsConstructor(onConstructor = @__({@Autowired}))
public class CartService {

    private CartRepository cartRepository;
    private CartItemService cartItemService;
    private UserService userService;
    private ProductService productService;

    public Cart findByUserId(Long userId) {
        return cartRepository.findByUserId(userId).orElseThrow(() -> new EntityNotFoundException("Cart for user " + userId + " not found!"));
    }

    public Cart findCartByUserOrEmpty(Long userId) {
        return cartRepository.findByUserId(userId).orElse(Cart.emptyCart(userId));
    }

    public Cart findCartByUserOrEmpty(String username) {
        User user = userService.findByEmail(username);
        return cartRepository.findByUserId(user.getId()).orElse(Cart.emptyCart(user.getId()));
    }

    public Cart save(Cart cart) {
        return cartRepository.save(cart);
    }

    public Cart addToCart(Long userId, CartItemDTO cartItemDTO) {
        User user = userService.findById(userId);
        Cart updatedCart = getUpdatedCart(user, cartItemDTO);
        return cartRepository.save(updatedCart);
    }

    public Cart addToCart(String username, CartItemDTO cartItemDTO) {
        User user = userService.findByEmail(username);
        Cart updatedCart = getUpdatedCart(user, cartItemDTO);
        return cartRepository.save(updatedCart);
    }

    public Cart deleteFromCart(Long userId, Long cartItemId) {
        User user = userService.findById(userId);
        return cartRepository.save(getUpdatedCartAfterRemoval(user, cartItemId));
    }

    public Cart deleteFromCart(String username, Long cartItemId) {
        User user = userService.findByEmail(username);
        return cartRepository.save(getUpdatedCartAfterRemoval(user, cartItemId));
    }

    public Cart emptyCartForUser(String username) {
        User user = userService.findByEmail(username);
        Cart cart = cartRepository.findByUserId(user.getId()).orElseThrow(() -> new EntityNotFoundException("Cart for user ID " + user.getId() + " not found!"));
        cart.removeAllItems();
        return cartRepository.save(cart);
    }

    public Cart emptyCart(Long cartId) {
        Cart cart = cartRepository.findById(cartId).orElseThrow(() -> new EntityNotFoundException("Cart with ID " + cartId + " not found!"));
        cart.removeAllItems();
        return cartRepository.save(cart);
    }

    protected Cart getUpdatedCart(User user, CartItemDTO cartItemDTO) {
        Long productId = cartItemDTO.getProductId();
        Integer quantity = cartItemDTO.getQuantity();
        Product product = productService.findById(productId);
        Optional<Cart> cart = cartRepository.findByUserId(user.getId());
        return cart.map(c -> c.addItem(CartItem.builder()
                .product(product)
                .quantity(quantity)
                .build()))
                .orElseGet(() -> Cart.builder()
                        .cartItems(List.of(CartItem.builder()
                                .product(product)
                                .quantity(quantity)
                                .build()))
                        .user(user)
                        .build());
    }

    public Cart getUpdatedCartAfterRemoval(User user, Long cartItemId) {
        Cart cart = cartRepository.findByUserId(user.getId()).orElseThrow(() -> new EntityNotFoundException("Cart not found!"));
        CartItem cartItem = cartItemService.findById(cartItemId);
        return cart.removeItem(cartItem);
    }

    public void delete(Cart cart) {
        cartRepository.delete(cart);
    }

    public Order buildOrder(Cart cart, User user, Address address) {
        return Order.builder()
                //.orderDateTime(LocalDateTime.now())
                .user(user)
                .shippingAddress(address)
                .orderItems(cart.toOrder())
                .totalPrice(cart.getTotalPrice())
                .build().ordered();
    }
}
