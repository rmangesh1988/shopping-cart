package com.hardware.store.resource;

import com.hardware.store.domain.News;
import com.hardware.store.dto.NewsDTO;
import com.hardware.store.service.NewsService;
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
@RequestMapping("api/v1/news")
@AllArgsConstructor(onConstructor = @__({@Autowired}))
public class NewsResource {

    private NewsService newsService;

    @GetMapping
    public ResponseEntity<List<News>> findAllNews() {
        return ResponseEntity.ok(newsService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<News> findNews(@PathVariable @NotNull Long id) {
        return ResponseEntity.ok(newsService.findById(id));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<News> addNews(@RequestBody @Valid NewsDTO newsDTO) {
        News product = newsService.save(newsDTO);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest().path("/{id}")
                .buildAndExpand(product.getId()).toUri();
        return ResponseEntity.created(location).body(product);
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<News> updateNews(@PathVariable @NotNull Long id, @RequestBody @Valid NewsDTO newsDTO) {
        return ResponseEntity.ok(newsService.update(id, newsDTO));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity deleteNews(@PathVariable @NotNull Long id) {
        newsService.delete(id);
        return ResponseEntity.ok(Boolean.TRUE);
    }
}
