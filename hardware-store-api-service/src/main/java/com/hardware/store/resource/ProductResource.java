package com.hardware.store.resource;

import com.hardware.store.domain.Product;
import com.hardware.store.dto.ProductDTO;
import com.hardware.store.service.ProductService;
import com.sun.istack.NotNull;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("api/v1/products")
@AllArgsConstructor(onConstructor = @__({@Autowired}))
public class ProductResource {

    private ProductService productService;

    @GetMapping
    public ResponseEntity<List<Product>> findAllProducts() {
        return ResponseEntity.ok(productService.findAll());
    }

    @GetMapping("/pages")
    public ResponseEntity<List<Product>> findAllProductsWithPagination(@RequestParam Integer pageNumber,@RequestParam Integer size) {
        return ResponseEntity.ok(productService.findAll(pageNumber, size));
    }

    @GetMapping("/search")
    public ResponseEntity<List<Product>> searchByName(@RequestParam String name) {
        return ResponseEntity.ok(productService.searchByProductName(name));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> findProduct(@PathVariable @NotNull Long id) {
        return ResponseEntity.ok(productService.findById(id));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Product> addProduct(@RequestBody @Valid ProductDTO productDTO) {
        Product product = productService.save(productDTO);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest().path("/{id}")
                .buildAndExpand(product.getId()).toUri();
        return ResponseEntity.created(location).body(product);
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Product> updateProduct(@PathVariable @NotNull Long id, @RequestBody @Valid ProductDTO productDTO) {
        return ResponseEntity.ok(productService.update(id, productDTO));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity deleteProduct(@PathVariable @NotNull Long id) {
        productService.delete(id);
        return ResponseEntity.ok(Boolean.TRUE);
    }

}
