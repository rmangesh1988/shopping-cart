package com.hardware.store.resource;

import com.hardware.store.domain.Address;
import com.hardware.store.domain.Order;
import com.hardware.store.dto.AddressDTO;
import com.hardware.store.service.OrderService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static com.hardware.store.util.TestUtil.*;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@WithMockUser
public class OrderResourceTest {

    private static final String API_URL = "/api/v1/orders";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderService orderService;

    @Test
    @WithMockUser(username= USERNAME_ADMIN, authorities={"CUSTOMER"})
    public void testGetFindAllOrdersByUserSuccess() throws Exception {

        List<Order> orders = List.of(
                Order.builder().build(),
                Order.builder().build()
                );

        when(orderService.findOrdersByUser(USERNAME_ADMIN)).thenReturn(orders);

        mockMvc.perform(get(API_URL))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.asMediaType(APPLICATION_JSON)))
                .andExpect(jsonPath("$[*]", hasSize(2)));

        verify(orderService, times(1)).findOrdersByUser(USERNAME_ADMIN);
    }

    @Test
    @WithMockUser(username= USERNAME_ADMIN, authorities={"CUSTOMER"})
    public void testGetFindByIdSuccess() throws Exception {

        Long orderId = 1L;

        Order order = Order.builder()
                .id(orderId)
                .build();

        when(orderService.findById(orderId)).thenReturn(order);

        mockMvc.perform(get(API_URL+"/"+orderId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.asMediaType(APPLICATION_JSON)))
                .andExpect(jsonPath("$.id", is(orderId.intValue())));

        verify(orderService, times(1)).findById(orderId);
    }

    @Test
    @WithMockUser(username= USERNAME_ADMIN, authorities={"CUSTOMER"})
    public void testPostPlaceOrderByUserSuccess() throws Exception {

        String building = "building";
        String city = "Oslo";
        String street = "street";
        String zipCode = "0661";
        Long orderId = 1L;

        AddressDTO addressDTO = AddressDTO.builder()
                .building(building)
                .city(city)
                .street(street)
                .zipCode(zipCode)
                .build();

        Address address = Address.builder()
                .building(building)
                .city(city)
                .street(street)
                .zipCode(zipCode)
                .build();

        Order order = Order.builder()
                .id(orderId)
                .shippingAddress(address)
                .build();

        when(orderService.placeOrder(USERNAME_ADMIN, addressDTO)).thenReturn(order);

        mockMvc.perform(post(API_URL).contentType(APPLICATION_JSON).content(asJsonString(addressDTO)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.asMediaType(APPLICATION_JSON)))
                .andExpect(header().stringValues("Location", "http://localhost/api/v1/orders/"+orderId))
                .andExpect(jsonPath("$.shippingAddress.building", is(building)))
                .andExpect(jsonPath("$.shippingAddress.city", is(city)))
                .andExpect(jsonPath("$.shippingAddress.street", is(street)))
                .andExpect(jsonPath("$.shippingAddress.zipCode", is(zipCode)));

    }

    @Test
    @WithMockUser(username=USERNAME_ADMIN, authorities={"ADMIN"})
    public void testDeleteDeleteProductByCustomerUnauthorized() throws Exception {
        Long orderId = 1L;

        mockMvc.perform(get(API_URL+"/"+orderId))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType(MediaType.asMediaType(APPLICATION_JSON)))
                .andExpect(jsonPath("$.message", is("Access is denied")));

    }
}
