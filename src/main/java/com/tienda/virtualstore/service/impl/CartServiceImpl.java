package com.tienda.virtualstore.service.impl;

import com.tienda.virtualstore.dto.request.CartItemRequest;
import com.tienda.virtualstore.dto.response.CartResponse;
import com.tienda.virtualstore.mapper.CartMapper;
import com.tienda.virtualstore.model.Cart;
import com.tienda.virtualstore.model.CartItem;
import com.tienda.virtualstore.model.Product;
import com.tienda.virtualstore.repository.CartItemRepository;
import com.tienda.virtualstore.repository.CartRepository;
import com.tienda.virtualstore.repository.ProductRepository;
import com.tienda.virtualstore.repository.UserRepository;
import com.tienda.virtualstore.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartRepository     cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository  productRepository;
    private final CartMapper         cartMapper;
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public CartResponse getCart(Long userId) {
        Cart cart = getOrCreateCart(userId);
        return cartMapper.toResponse(cart);
    }

    @Override
    @Transactional
    public CartResponse addItem(Long userId, CartItemRequest request) {

        // 1. Obtener o crear el carrito del usuario
        Cart cart = getOrCreateCart(userId);

        // 2. Verificar que el producto exista y esté activo
        Product product = productRepository.findById(request.getProductId())
                .filter(Product::isActive)
                .orElseThrow(() -> new RuntimeException(
                        "Producto no encontrado con id: " + request.getProductId()));

        // 3. Verificar stock disponible
        if (product.getStock() < request.getQuantity()) {
            throw new RuntimeException(
                    "Stock insuficiente. Disponible: " + product.getStock());
        }

        // 4. ¿El producto ya está en el carrito?
        cartItemRepository.findByCartIdAndProductId(cart.getId(), product.getId())
                .ifPresentOrElse(
                        existingItem -> {
                            // Actualizar cantidad
                            int newQty = existingItem.getQuantity() + request.getQuantity();
                            if (newQty > product.getStock()) {
                                throw new RuntimeException(
                                        "Stock insuficiente. Disponible: " + product.getStock());
                            }
                            existingItem.setQuantity(newQty);
                            cartItemRepository.save(existingItem);
                        },
                        () -> {
                            // Crear nuevo item
                            CartItem item = new CartItem();
                            item.setCart(cart);
                            item.setProduct(product);
                            item.setQuantity(request.getQuantity());
                            item.setUnitPrice(product.getPrice());
                            cartItemRepository.save(item);
                            cart.getItems().add(item);
                        }
                );

        return cartMapper.toResponse(cart);
    }

    @Override
    @Transactional
    public CartResponse updateItem(Long userId, Long itemId, CartItemRequest request) {

        // 1. Obtener el carrito
        Cart cart = getOrCreateCart(userId);

        // 2. Buscar el item
        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException(
                        "Item no encontrado con id: " + itemId));

        // 3. Verificar que el item pertenece al carrito del usuario
        if (!item.getCart().getId().equals(cart.getId())) {
            throw new RuntimeException("El item no pertenece a tu carrito");
        }

        // 4. Verificar stock
        if (item.getProduct().getStock() < request.getQuantity()) {
            throw new RuntimeException(
                    "Stock insuficiente. Disponible: " + item.getProduct().getStock());
        }

        // 5. Actualizar cantidad
        item.setQuantity(request.getQuantity());
        cartItemRepository.save(item);

        return cartMapper.toResponse(cart);
    }

    @Override
    @Transactional
    public CartResponse removeItem(Long userId, Long itemId) {

        // 1. Obtener el carrito
        Cart cart = getOrCreateCart(userId);

        // 2. Buscar el item
        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException(
                        "Item no encontrado con id: " + itemId));

        // 3. Verificar que pertenece al carrito del usuario
        if (!item.getCart().getId().equals(cart.getId())) {
            throw new RuntimeException("El item no pertenece a tu carrito");
        }

        // 4. Eliminar — orphanRemoval lo borra de la DB
        cart.getItems().remove(item);
        cartRepository.save(cart);

        return cartMapper.toResponse(cart);
    }

    @Override
    @Transactional
    public void clearCart(Long userId) {
        cartRepository.findByUserId(userId)
                .ifPresent(cart -> {
                    cartItemRepository.deleteByCartId(cart.getId());
                    cart.getItems().clear();
                    cartRepository.save(cart);
                });
    }

    // ── Método privado — obtener carrito o crear uno nuevo ───────────────
    private Cart getOrCreateCart(Long userId) {
        return cartRepository.findByUserId(userId)
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setUser(
                            userRepository.getReferenceById(userId)
                    );
                    return cartRepository.save(newCart);
                });
    }
}