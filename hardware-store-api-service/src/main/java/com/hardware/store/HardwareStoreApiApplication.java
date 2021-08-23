package com.hardware.store;

import com.hardware.store.domain.*;
import com.hardware.store.dto.ProductDTO;
import com.hardware.store.service.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.List;
import java.util.Set;

@SpringBootApplication
public class HardwareStoreApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(HardwareStoreApiApplication.class, args);
    }

    @Bean
    CommandLineRunner run(UserService userService, RoleService roleService, ProductService productService, CartService cartService, NewsService newsService) {
        return args -> {

            //Users
            Role roleAdmin = roleService.save(Role.builder().name("ADMIN").build());
            Role roleCustomer = roleService.save(Role.builder().name("CUSTOMER").build());
            User mangesh = userService.save(new User(null, "Mangesh", "Rananavare", "a@a.com", "123", Set.of(roleAdmin)));
            User bitopi = userService.save(new User(null, "Bitopi", "Sarma", "b@b.com", "456", Set.of(roleCustomer)));

            //Products
            Product iPhone = productService.save(ProductDTO.builder().name("iPhone").price(1000.0).build());
            Product ps5 = productService.save(ProductDTO.builder().name("PS5").price(500.0).build());

            //Pre populated cart for demo
            Cart cartBitopi = Cart.builder()
                    .cartItems(List.of(CartItem.builder()
                            .product(iPhone)
                            .quantity(1)
                            .build()))
                    .user(bitopi)
                    .build();
            cartService.save(cartBitopi);

            //News
            News news = News.builder()
                    .news("New iPhone launching!")
                    .build();

            newsService.save(news);

        };
    }

}
