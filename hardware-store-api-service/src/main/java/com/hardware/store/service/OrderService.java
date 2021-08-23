package com.hardware.store.service;

import com.hardware.store.domain.Cart;
import com.hardware.store.domain.Order;
import com.hardware.store.domain.User;
import com.hardware.store.dto.AddressDTO;
import com.hardware.store.mapper.AddressMapper;
import com.hardware.store.repository.OrderRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.util.List;

@Service
@Transactional
@AllArgsConstructor(onConstructor = @__({@Autowired}))
public class OrderService {

    private OrderRepository orderRepository;

    private UserService userService;

    private CartService cartService;

    private AddressMapper addressMapper;

    public Order placeOrder(String username, @Valid AddressDTO addressDTO) {
        User user = userService.findByEmail(username);
        Cart cart = cartService.findByUserId(user.getId());
        Order order = cartService.buildOrder(cart, user, addressMapper.fromDTO(addressDTO));
        order = orderRepository.save(order);
        cartService.delete(cart);
        return order;
    }



    public List<Order> findOrdersByUser(String username) {
        User user = userService.findByEmail(username);
        return orderRepository.findAllByUserId(user.getId());
    }
}
