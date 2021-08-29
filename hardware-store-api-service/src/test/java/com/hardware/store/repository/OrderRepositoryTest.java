package com.hardware.store.repository;

import com.hardware.store.domain.Address;
import com.hardware.store.domain.Order;
import com.hardware.store.domain.OrderItem;
import com.hardware.store.domain.Product;
import com.hardware.store.domain.Role;
import com.hardware.store.domain.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@DataJpaTest
public class OrderRepositoryTest {

    @Autowired
    private TestEntityManager testEntityManager;

    @Autowired
    private OrderRepository orderRepository;

    @Test
    public void testFindAllByUserId() {

        Role admin = Role.builder()
                .name("ADMIN")
                .build();

        Product product = Product.builder()
                .name("iPhone")
                .price(5500.0)
                .build();

        admin = testEntityManager.persist(admin);

        User user = User.builder()
                .email("test@test.com")
                .firstName("John")
                .lastName("Doe")
                .password("password")
                .roles(Set.of(admin))
                .build();

        user = testEntityManager.persist(user);
        product = testEntityManager.persist(product);

        Order order = Order.builder()
                .user(user)
                .orderDateTime(LocalDateTime.now())
                .totalPrice(5500.0)
                .orderItems(List.of(
                        OrderItem.builder()
                                .quantity(2)
                                .product(product)
                                .build()
                ))
                .shippingAddress(Address.builder()
                        .building("15B")
                        .city("Oslo")
                        .street("Xveien")
                        .zipCode("0661")
                        .build())
                .build();

        order = testEntityManager.persist(order);

        List<Order> orders = orderRepository.findAllByUserId(user.getId());

        assertThat(orders).isNotEmpty();
        assertThat(orders).hasSize(1);
        assertThat(orders).containsExactly(order);
    }
}
