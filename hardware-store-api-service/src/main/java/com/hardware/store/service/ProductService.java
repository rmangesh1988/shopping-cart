package com.hardware.store.service;

import com.hardware.store.domain.Product;
import com.hardware.store.dto.ProductDTO;
import com.hardware.store.exception.EntityNotFoundException;
import com.hardware.store.mapper.ProductMapper;
import com.hardware.store.repository.ProductRepository;
import com.hardware.store.repository.specification.ProductSpecification;
import com.hardware.store.repository.specification.SearchCriteria;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

import static com.hardware.store.domain.Product_.NAME;
import static com.hardware.store.repository.specification.SearchOperation.LIKE;

@Service
@Transactional
@AllArgsConstructor(onConstructor = @__({@Autowired}))
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    public List<Product> findAll() {
        return productRepository.findAll();
    }

    public List<Product> findAll(Integer pageNumber, Integer size) {
        Page<Product> pages = productRepository.findAll(PageRequest.of(pageNumber, size));
        return pages.stream().collect(Collectors.toList());
    }

    public Product findById(Long id) {
        return productRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Product with ID : " + id + " not found!"));
    }

    public Product save(ProductDTO productDTO) {
        return productRepository.save(productMapper.fromDTO(productDTO));
    }

    public Product update(Long id, ProductDTO productDTO) {
        Product product = productRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Product with ID : " + id + " not found!"));
        productMapper.fromDTO(productDTO, product);
        return productRepository.save(product);
    }

    public void delete(Long id) {
        productRepository.deleteById(id);
    }

    public List<Product> searchByProductName(String name) {
        ProductSpecification productNameSpecification = ProductSpecification.builder()
                .searchCriteria(SearchCriteria.builder()
                        .key(NAME)
                        .searchOperation(LIKE)
                        .value(name)
                        .build())
                .build();
        return productRepository.findAll(productNameSpecification);
    }
}
