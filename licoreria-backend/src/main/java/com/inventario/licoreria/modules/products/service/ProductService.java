package com.inventario.licoreria.modules.products.service;

import com.inventario.licoreria.modules.products.dto.ProductDTO;
import com.inventario.licoreria.modules.products.model.Product;
import com.inventario.licoreria.modules.products.repository.ProductRepository;
import com.inventario.licoreria.modules.store.service.StoreService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import org.springframework.lang.NonNull;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final StoreService storeService;

    public ProductService(ProductRepository productRepository, StoreService storeService) {
        this.productRepository = productRepository;
        this.storeService = storeService;
    }

    public List<Product> findAll() {
        return productRepository.findAll();
    }

    public Product create(final ProductDTO dto) {
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

    public Product update(@NonNull final Long id, final ProductDTO dto) {
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

    public List<Product> findByStoreId(@NonNull Long storeId) {
        return productRepository.findByStoreId(storeId);
    }

}
