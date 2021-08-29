package com.hardware.store.config;

import com.hardware.store.domain.Cart;
import com.hardware.store.domain.CartItem;
import com.hardware.store.domain.News;
import com.hardware.store.domain.Product;
import com.hardware.store.domain.Role;
import com.hardware.store.domain.User;
import com.hardware.store.dto.ProductDTO;
import com.hardware.store.service.CartService;
import com.hardware.store.service.NewsService;
import com.hardware.store.service.ProductService;
import com.hardware.store.service.RoleService;
import com.hardware.store.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Set;

@Configuration
public class DataSetupConfig {

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
