package com.inventario.licoreria.modules.products.service;

import com.inventario.licoreria.modules.products.dto.ProductDTO;
import com.inventario.licoreria.modules.products.model.Product;
import com.inventario.licoreria.modules.products.repository.ProductRepository;
import com.inventario.licoreria.modules.store.model.Store;
import com.inventario.licoreria.modules.store.service.StoreService;
import com.inventario.licoreria.modules.users.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import org.springframework.lang.NonNull;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final StoreService storeService;
    private final UserService userService;

    public ProductService(ProductRepository productRepository, StoreService storeService, UserService userService) {
        this.productRepository = productRepository;
        this.storeService = storeService;
        this.userService = userService;
    }

    private void validateUserOwnsStore(@NonNull Long storeId, @NonNull String username) {
        Store store = storeService.findStoreEntity(storeId);
        if (!store.getManager().getUsername().equals(username)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, 
                "No tienes permiso para acceder a los productos de esta tienda");
        }
    }

    private void validateUserOwnsProduct(@NonNull Long productId, @NonNull String username) {
        Product product = findById(productId);
        if (!product.getStore().getManager().getUsername().equals(username)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, 
                "No tienes permiso para modificar este producto");
        }
    }

    public List<Product> findAll() {
        return productRepository.findAll();
    }

    public List<Product> findAllByUsername(@NonNull String username) {
        return productRepository.findAll().stream()
                .filter(product -> product.getStore().getManager().getUsername().equals(username))
                .toList();
    }

    public Product create(final ProductDTO dto, @NonNull String username) {
        validateUserOwnsStore(dto.getStoreId(), username);
        final Product product = new Product();
        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setCost(dto.getCost());
        product.setPrice(dto.getPrice());
        product.setStock(dto.getStock());
        product.setInitialStock(dto.getStock());
        product.setStore(storeService.findStoreEntity(dto.getStoreId()));
        return productRepository.save(product);
    }

    @org.springframework.lang.NonNull
    @SuppressWarnings("null")
    public Product findById(@NonNull Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado"));
    }

    public Product update(@NonNull final Long id, final ProductDTO dto, @NonNull String username) {
        validateUserOwnsProduct(id, username);
        final Product product = findById(id);
        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setCost(dto.getCost());
        product.setPrice(dto.getPrice());
        product.setStock(dto.getStock());
        return productRepository.save(product);
    }

    public void delete(@NonNull final Long id, String username) {
        final Product product = findById(id);
        // Verificar que el usuario autenticado es el manager de la tienda
        if (!product.getStore().getManager().getUsername().equals(username)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No puedes eliminar un producto que no es de tu tienda");
        }
        productRepository.delete(product);
    }

    public void delete(@NonNull final Long id) {
        final Product product = findById(id);
        productRepository.delete(product);
    }

    public Product adjustStock(@NonNull final Long id, final int stockDelta, @NonNull String username) {
        validateUserOwnsProduct(id, username);
        final Product product = findById(id);
        final int nuevoStock = product.getStock() + stockDelta;
        if (nuevoStock < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                "Stock insuficiente para el producto: " + product.getName());
        }
        product.setStock(nuevoStock);
        return productRepository.save(product);
    }

    public Product adjustStock(@NonNull final Long id, final int stockDelta) {
        final Product product = findById(id);
        final int nuevoStock = product.getStock() + stockDelta;
        if (nuevoStock < 0) {
            throw new RuntimeException("Stock insuficiente para el producto: " + product.getName());
        }
        product.setStock(nuevoStock);
        return productRepository.save(product);
    }

    public List<Product> search(String query) {
        if (query == null || query.trim().isEmpty()) {
            return findAll();
        }
        return productRepository.searchByName(query.trim());
    }

    public List<Product> getSuggestions(String query) {
        if (query == null || query.trim().isEmpty()) {
            return List.of();
        }
        return productRepository.searchSuggestions(query.trim());
    }

    public List<Product> findByStoreId(@NonNull Long storeId, @NonNull String username) {
        validateUserOwnsStore(storeId, username);
        return productRepository.findByStoreId(storeId);
    }

    public List<Product> findByStoreId(@NonNull Long storeId) {
        return productRepository.findByStoreId(storeId);
    }

}
