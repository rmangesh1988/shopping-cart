package com.hardware.store.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static org.springframework.util.CollectionUtils.isEmpty;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "cart_id", referencedColumnName = "id")
    private List<CartItem> cartItems;

    @OneToOne
    private User user;

    public Cart addItem(CartItem cartItem) {
        if(isNull(cartItems)) {
            cartItems = new ArrayList<>();
        }
        cartItems.add(cartItem);
        return this;
    }

    public Cart removeItem(CartItem cartItem) {
        if(!isEmpty(cartItems)) {
            cartItems.remove(cartItem);
        }
        return this;
    }

    public Cart removeAllItems() {
        if(!isEmpty(cartItems)) {
            cartItems.clear();
        }
        return this;
    }

    public List<OrderItem> toOrder() {
        return cartItems.stream().map(ci -> OrderItem.builder()
                .product(ci.getProduct())
                .quantity(ci.getQuantity())
                .build()).collect(Collectors.toList());
    }

    @JsonIgnore
    public Double getTotalPrice() {
        return cartItems.stream().map(ci -> (ci.getProduct().getPrice() * ci.getQuantity().doubleValue())).reduce(0.0, Double::sum);
    }

    public static Cart emptyCart(Long userId) {
        return Cart.builder()
                .cartItems(Collections.emptyList())
                .user(User.builder()
                        .id(userId)
                        .build())
                .build();
    }

}
