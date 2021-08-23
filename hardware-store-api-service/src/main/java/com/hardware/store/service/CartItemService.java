package com.hardware.store.service;

import com.hardware.store.domain.CartItem;
import com.hardware.store.exception.EntityNotFoundException;
import com.hardware.store.repository.CartItemRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Transactional
@AllArgsConstructor(onConstructor = @__({@Autowired}))
public class CartItemService {

    private CartItemRepository cartItemRepository;

    public CartItem findById(Long id) {
        return cartItemRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Cart item with ID " + id + " not found!"));
    }
}
