package com.hardware.store.resource;

import com.hardware.store.domain.Product;
import com.hardware.store.dto.ProductDTO;
import com.hardware.store.service.ProductService;
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

import java.util.Collections;
import java.util.List;

import static com.hardware.store.util.TestUtil.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@WithMockUser
public class ProductResourceTest {

    private static final String API_URL = "/api/v1/products";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @Test
    @WithMockUser(username=USERNAME_CUSTOMER, authorities={"CUSTOMER"})
    public void testGetFindAllProductsByCustomerSuccess() throws Exception {
        List<Product> products = List.of(
                Product.builder().build(),
                Product.builder().build()
                );

        when(productService.findAll()).thenReturn(products);

        mockMvc.perform(get(API_URL))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.asMediaType(APPLICATION_JSON)))
                .andExpect(jsonPath("$[*]", hasSize(2)));

        verify(productService, times(1)).findAll();
    }

    @Test
    @WithMockUser(username=USERNAME_ADMIN, authorities={"ADMIN"})
    public void testGetSearchByNameByAdminSuccess() throws Exception {
        String searchString = "iph";
        String iPhoneName = "iPhone";
        Double price = 400.0;
        List<Product> products = Collections.singletonList(Product.builder()
                .id(1L)
                .name(iPhoneName)
                .price(price)
                .build());

        when(productService.searchByProductName(searchString)).thenReturn(products);

        mockMvc.perform(get(API_URL+"/search?name="+searchString))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.asMediaType(APPLICATION_JSON)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is(iPhoneName)))
                .andExpect(jsonPath("$[0].price", is(price)));

        verify(productService, times(1)).searchByProductName(searchString);
    }

    @Test
    @WithMockUser(username=USERNAME_ADMIN, authorities={"ADMIN"})
    public void testGetFindAllProductsWithPaginationByAdminSuccess() throws Exception {
        Integer pageNumber = 0;
        Integer size = 1;
        String searchString = "iph";
        String iPhoneName = "iPhone";
        Double price = 400.0;
        List<Product> products = Collections.singletonList(Product.builder()
                .id(1L)
                .name(iPhoneName)
                .price(price)
                .build());

        when(productService.findAll(pageNumber, size)).thenReturn(products);

        mockMvc.perform(get(API_URL+"/pages?pageNumber="+pageNumber+"&size="+size))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.asMediaType(APPLICATION_JSON)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is(iPhoneName)))
                .andExpect(jsonPath("$[0].price", is(price)));

        verify(productService, times(1)).findAll(pageNumber, size);
    }

    @Test
    @WithMockUser(username=USERNAME_ADMIN, authorities={"ADMIN"})
    public void testGetFindAllByIdByAdminSuccess() throws Exception {
        Long productId = 1L;
        String iPhoneName = "iPhone";
        Double price = 400.0;
        Product product = Product.builder()
                .id(1L)
                .name(iPhoneName)
                .price(price)
                .build();

        when(productService.findById(productId)).thenReturn(product);

        mockMvc.perform(get(API_URL+"/"+productId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.asMediaType(APPLICATION_JSON)))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is(iPhoneName)))
                .andExpect(jsonPath("$.price", is(price)));

        verify(productService, times(1)).findById(productId);
    }


    @Test
    @WithMockUser(username=USERNAME_ADMIN, authorities={"ADMIN"})
    public void testPostAddProductByAdminSuccess() throws Exception {
        String samsungPhoneName = "Samsung phone";
        Double samsungPhonePrice = 100.0;
        Long productId = 1L;
        ProductDTO samsungPhoneDTO = ProductDTO.builder()
                .name(samsungPhoneName)
                .price(samsungPhonePrice)
                .build();

        Product samsungPhone = Product.builder()
                .id(productId)
                .name(samsungPhoneName)
                .price(samsungPhonePrice)
                .build();

        when(productService.save(samsungPhoneDTO)).thenReturn(samsungPhone);

        mockMvc.perform(post(API_URL).contentType(APPLICATION_JSON).content(asJsonString(samsungPhoneDTO)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.asMediaType(APPLICATION_JSON)))
                .andExpect(header().stringValues("Location", "http://localhost/api/v1/products/"+productId))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is(samsungPhoneName)))
                .andExpect(jsonPath("$.price", is(samsungPhonePrice)));

        verify(productService, times(1)).save(samsungPhoneDTO);
    }

    @Test
    @WithMockUser(username=USERNAME_ADMIN, authorities={"ADMIN"})
    public void testPostAddProductByAdminFailsDueToRequestValidation() throws Exception {
        ProductDTO invalidDTO = ProductDTO.builder()
                .name("")
                .price(null)
                .build();

        mockMvc.perform(post(API_URL).contentType(APPLICATION_JSON).content(asJsonString(invalidDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.asMediaType(APPLICATION_JSON)))
                .andExpect(jsonPath("$.message", is("Request validation error")))
                .andExpect(jsonPath("$.errors[*]", hasSize(2)))
                .andExpect(jsonPath("$.errors[*]").value(containsInAnyOrder(
                "name must not be empty",
                        "price must not be null"
                )));

    }

    @Test
    @WithMockUser(username=USERNAME_CUSTOMER, authorities={"CUSTOMER"})
    public void testPostAddProductByCustomerUnauthorized() throws Exception {
        String samsungPhoneName = "Samsung phone";
        Double samsungPhonePrice = 100.0;
        ProductDTO samsungPhoneDTO = ProductDTO.builder()
                .name(samsungPhoneName)
                .price(samsungPhonePrice)
                .build();

        mockMvc.perform(post(API_URL).contentType(APPLICATION_JSON).content(asJsonString(samsungPhoneDTO)))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType(MediaType.asMediaType(APPLICATION_JSON)))
                .andExpect(jsonPath("$.message", is("Access is denied")));

        verify(productService, times(0)).save(samsungPhoneDTO);
    }

    @Test
    @WithMockUser(username=USERNAME_ADMIN, authorities={"ADMIN"})
    public void testPatchUpdateProductByAdminSuccess() throws Exception {
        Long productId = 1L;
        String samsungPhoneName = "Samsung phone";
        Double newSamsungPhonePrice = 200.0;
        ProductDTO samsungPhoneDTO = ProductDTO.builder()
                .name(samsungPhoneName)
                .price(newSamsungPhonePrice)
                .build();

        Product samsungPhone = Product.builder()
                .id(productId)
                .name(samsungPhoneName)
                .price(newSamsungPhonePrice)
                .build();

        when(productService.update(productId, samsungPhoneDTO)).thenReturn(samsungPhone);

        mockMvc.perform(patch(API_URL+"/"+productId).contentType(APPLICATION_JSON).content(asJsonString(samsungPhoneDTO)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.asMediaType(APPLICATION_JSON)))
                .andExpect(jsonPath("$.id", is(productId.intValue())))
                .andExpect(jsonPath("$.name", is(samsungPhoneName)))
                .andExpect(jsonPath("$.price", is(newSamsungPhonePrice)));

        verify(productService, times(1)).update(productId, samsungPhoneDTO);
    }

    @Test
    @WithMockUser(username=USERNAME_CUSTOMER, authorities={"CUSTOMER"})
    public void testPatchUpdateProductByCustomerUnauthorized() throws Exception {
        Long productId = 1L;
        ProductDTO samsungPhoneDTO = ProductDTO.builder()
                .name("Samsung phone")
                .price(200.0)
                .build();

        mockMvc.perform(patch(API_URL+"/"+productId).contentType(APPLICATION_JSON).content(asJsonString(samsungPhoneDTO)))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType(MediaType.asMediaType(APPLICATION_JSON)))
                .andExpect(jsonPath("$.message", is("Access is denied")));

        verify(productService, times(0)).update(productId, samsungPhoneDTO);
    }


    @Test
    @WithMockUser(username=USERNAME_ADMIN, authorities={"ADMIN"})
    public void testDeleteDeleteProductByAdminSuccess() throws Exception {
        Long productId = 1L;

        doNothing().when(productService).delete(productId);

        mockMvc.perform(delete(API_URL+"/"+productId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.asMediaType(APPLICATION_JSON)))
                .andExpect(jsonPath("$", is(Boolean.TRUE)));

        verify(productService, times(1)).delete(productId);
    }

    @Test
    @WithMockUser(username=USERNAME_CUSTOMER, authorities={"CUSTOMER"})
    public void testDeleteDeleteProductByCustomerUnauthorized() throws Exception {
        Long productId = 1L;

        mockMvc.perform(delete(API_URL+"/"+productId))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType(MediaType.asMediaType(APPLICATION_JSON)))
                .andExpect(jsonPath("$.message", is("Access is denied")));

        verify(productService, times(0)).delete(productId);
    }
}
