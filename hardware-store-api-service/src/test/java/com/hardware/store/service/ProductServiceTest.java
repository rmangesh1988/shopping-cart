package com.hardware.store.service;

import com.hardware.store.domain.Product;
import com.hardware.store.dto.ProductDTO;
import com.hardware.store.exception.EntityNotFoundException;
import com.hardware.store.mapper.ProductMapper;
import com.hardware.store.repository.ProductRepository;
import com.hardware.store.repository.specification.ProductSpecification;
import com.hardware.store.repository.specification.SearchCriteria;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.hardware.store.domain.Product_.NAME;
import static com.hardware.store.repository.specification.SearchOperation.LIKE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductMapper productMapper;

    @InjectMocks
    private ProductService productService;

    @Test
    public void testFindAllSuccess() {
        List<Product> products = List.of(
                Product.builder().build(),
                Product.builder().build()
        );

        when(productRepository.findAll()).thenReturn(products);

        List<Product> productsResult = productService.findAll();

        assertThat(productsResult).isNotEmpty();
        assertThat(productsResult).hasSize(2);

        verify(productRepository, times(1)).findAll();
    }

    @Test
    public void testFindAllWithPaginationSuccess() {
        Integer pageNumber = 0;
        Integer size = 1;

        PageRequest pageRequest = PageRequest.of(pageNumber, size);

        List<Product> products = List.of(
                Product.builder().build(),
                Product.builder().build()
        );

        Page<Product> productsPage = new PageImpl(Collections.singletonList(products.get(0)));

        when(productRepository.findAll(pageRequest)).thenReturn(productsPage);

        List<Product> productsResult = productService.findAll(pageNumber, size);

        assertThat(productsResult).isNotEmpty();
        assertThat(productsResult).hasSize(1);

        verify(productRepository, times(1)).findAll(pageRequest);
    }

    @Test
    public void testFindByIdSuccess() {
        Long productId = 1L;
        Product product = Product.builder()
                .id(productId)
                .build();

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        Product productResult = productService.findById(productId);

        assertThat(productResult).isNotNull();
        assertThat(productResult.getId()).isEqualTo(productId);

        verify(productRepository, times(1)).findById(productId);
    }

    @Test(expected = EntityNotFoundException.class)
    public void testFindByIdNotFound() {
        Long productId = 1L;

        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        productService.findById(productId);

        verify(productRepository, times(1)).findById(productId);
    }

    @Test
    public void testSaveSuccess() {

        String name = "iPhone";
        Double price = 500.0;
        Long productId = 1L;

        ProductDTO productDTO = ProductDTO.builder()
                .name(name)
                .price(price)
                .build();

        Product product = Product.builder()
                .name(name)
                .price(price)
                .build();

        when(productMapper.fromDTO(productDTO)).thenReturn(product);
        when(productRepository.save(product)).thenReturn(product.toBuilder().id(productId).build());

        Product productResult = productService.save(productDTO);

        assertThat(productResult).isNotNull();
        assertThat(productResult.getId()).isEqualTo(productId);

        verify(productMapper, times(1)).fromDTO(productDTO);
        verify(productRepository, times(1)).save(product);
    }

    @Test
    public void testUpdateSuccess() {

        String name = "iPhone";
        Double price = 500.0;
        Double newPrice = 300.0;
        Long productId = 1L;

        ProductDTO productDTO = ProductDTO.builder()
                .name(name)
                .price(newPrice)
                .build();

        Product product = Product.builder()
                .id(productId)
                .name(name)
                .price(price)
                .build();

        doAnswer(invocationOnMock -> {
            ProductDTO productDTOArg = invocationOnMock.getArgument(0);
            Product productArg = invocationOnMock.getArgument(1);
            productArg.setName(productDTOArg.getName());
            productArg.setPrice(productDTOArg.getPrice());
            return productArg;
        }).when(productMapper).fromDTO(productDTO, product);
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(productRepository.save(product)).thenReturn(product.toBuilder().price(newPrice).build());

        Product productResult = productService.update(productId, productDTO);

        assertThat(productResult).isNotNull();
        assertThat(productResult.getId()).isEqualTo(productId);
        assertThat(productResult.getPrice()).isEqualTo(newPrice);

        verify(productMapper, times(1)).fromDTO(productDTO, product);
        verify(productRepository, times(1)).save(product);
    }

    @Test(expected = EntityNotFoundException.class)
    public void testUpdateProductNotFound() {
        Long productId = 1L;

        ProductDTO productDTO = ProductDTO.builder().build();

        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        productService.update(productId, productDTO);

        verify(productRepository, times(1)).findById(productId);
    }

    @Test
    public void testDeleteSuccess() {

        Long productId = 1L;

        when(productRepository.findById(productId)).thenReturn(Optional.of(Product.builder().id(1L).build()));
        doNothing().when(productRepository).deleteById(productId);

        productService.delete(productId);

        verify(productRepository, times(1)).findById(productId);
        verify(productRepository, times(1)).deleteById(productId);
    }

    @Test
    public void testSearchByProductNameSuccess() {

        String searchString = "iph";
        String name = "iPhone";
        Double price = 500.0;
        Long productId = 1L;

        Product iPhone = Product.builder()
                .id(productId)
                .name(name)
                .price(price)
                .build();

        ProductSpecification productNameSpecification = ProductSpecification.builder()
                .searchCriteria(SearchCriteria.builder()
                        .key(NAME)
                        .searchOperation(LIKE)
                        .value(searchString)
                        .build())
                .build();

        when(productRepository.findAll(productNameSpecification)).thenReturn(Collections.singletonList(iPhone));

        List<Product> products = productService.searchByProductName(searchString);

        assertThat(products).isNotEmpty();
        assertThat(products).hasSize(1);
        assertThat(products.get(0).getId()).isEqualTo(productId);
        assertThat(products.get(0).getName()).isEqualTo(name);
        assertThat(products.get(0).getPrice()).isEqualTo(price);

        verify(productRepository, times(1)).findAll(productNameSpecification);
    }
}
