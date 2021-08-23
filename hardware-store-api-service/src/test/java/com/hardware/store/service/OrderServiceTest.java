package com.hardware.store.service;

import com.hardware.store.domain.*;
import com.hardware.store.dto.AddressDTO;
import com.hardware.store.exception.EntityNotFoundException;
import com.hardware.store.mapper.AddressMapper;
import com.hardware.store.repository.OrderRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private UserService userService;

    @Mock
    private CartService cartService;

    @Mock
    private AddressMapper addressMapper;

    @InjectMocks
    private OrderService orderService;

    @Test
    public void testPlaceOrder() {

        String username = "test@test.com";
        LocalDateTime orderedAt = LocalDateTime.of(LocalDate.of(2021, Month.AUGUST, 1), LocalTime.now());
        Supplier<LocalDateTime> orderDateTimeSupplier = () -> orderedAt;

        Role admin = Role.builder()
                .name("ADMIN")
                .build();

        User user = User.builder()
                .id(1L)
                .email(username)
                .firstName("John")
                .lastName("Doe")
                .password("password")
                .roles(Set.of(admin))
                .build();

        Product product = Product.builder()
                .name("iPhone")
                .price(5500.0)
                .build();

        Cart cart = Cart.builder()
                .id(1L)
                .user(user)
                .cartItems(List.of(
                        CartItem.builder()
                                .product(product)
                                .quantity(1)
                                .build()
                ))
                .build();

        AddressDTO addressDTO = AddressDTO.builder()
                .building("15B")
                .city("Oslo")
                .zipCode(0661)
                .street("Xveien")
                .build();

        Address address = Address.builder()
                .building("15B")
                .city("Oslo")
                .zipCode(0661)
                .street("Xveien")
                .build();

        Order order = Order.builder()
                .localDateTimeSupplier(orderDateTimeSupplier)
                .totalPrice(5500.0)
                .user(user)
                .orderItems(cart.toOrder())
                .shippingAddress(address)
                .build().ordered();

        when(userService.findByEmail(username)).thenReturn(user);
        when(cartService.findByUserId(user.getId())).thenReturn(cart);
        when(addressMapper.fromDTO(addressDTO)).thenReturn(address);
        when(cartService.buildOrder(cart, user, address)).thenReturn(order);
        when(orderRepository.save(order)).thenReturn(order.toBuilder().id(1L).build());
        doNothing().when(cartService).delete(cart);

        Order placedOrder = orderService.placeOrder(username, addressDTO);

        assertThat(placedOrder.getTotalPrice()).isEqualTo(cart.getTotalPrice());
        assertThat(placedOrder.getOrderDateTime()).isEqualTo(orderedAt);
        assertThat(placedOrder.getUser().getEmail()).isEqualTo(username);
        assertThat(placedOrder.getOrderItems()).isNotEmpty();
        assertThat(placedOrder.getOrderItems()).hasSize(1);

        verify(userService, times(1)).findByEmail(username);
        verify(cartService, times(1)).findByUserId(user.getId());
        verify(addressMapper, times(1)).fromDTO(addressDTO);
        verify(cartService, times(1)).buildOrder(cart, user, address);
        verify(orderRepository, times(1)).save(order);
        verify(cartService, times(1)).delete(cart);
    }

    @Test
    public void testFindOrdersByUser() {

        String username = "test@test.com";

        Role admin = Role.builder()
                .name("ADMIN")
                .build();

        User user = User.builder()
                .id(1L)
                .email(username)
                .firstName("John")
                .lastName("Doe")
                .password("password")
                .roles(Set.of(admin))
                .build();

        List<Order> orders = List.of(
                Order.builder().id(1L).build(),
                Order.builder().id(2L).build()
        );

        when(userService.findByEmail(username)).thenReturn(user);
        when(orderRepository.findAllByUserId(user.getId())).thenReturn(orders);

        List<Order> ordersForUser = orderService.findOrdersByUser(username);

        assertThat(ordersForUser).isNotEmpty();
        assertThat(ordersForUser).hasSize(2);

        verify(userService, times(1)).findByEmail(username);
        verify(orderRepository, times(1)).findAllByUserId(user.getId());
    }

    @Test
    public void testFindByIdSuccess() {
        Long orderId = 1L;
        Order order = Order.builder()
                .id(orderId)
                .build();

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        Order orderResult = orderService.findById(orderId);

        assertThat(orderResult).isNotNull();
        assertThat(orderResult.getId()).isEqualTo(orderId);

        verify(orderRepository, times(1)).findById(orderId);
    }

    @Test(expected = EntityNotFoundException.class)
    public void testFindByIdNotFound() {
        Long productId = 1L;

        when(orderRepository.findById(productId)).thenReturn(Optional.empty());

        orderService.findById(productId);

        verify(orderRepository, times(1)).findById(productId);
    }

}
