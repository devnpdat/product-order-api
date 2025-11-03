package com.example.productorder.service;

import com.example.productorder.document.ProductDocument;
import com.example.productorder.dto.ProductDTO;
import com.example.productorder.exception.ResourceNotFoundException;
import com.example.productorder.model.Product;
import com.example.productorder.repository.ProductRepository;
import com.example.productorder.repository.ProductSearchRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;

    @Autowired(required = false)
    private ProductSearchRepository productSearchRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Cacheable(value = "products", key = "'all'")
    public List<Product> getAllProducts() {
        log.debug("Fetching all products from database");
        return productRepository.findAll();
    }

    @Cacheable(value = "products", key = "#id")
    public Product getProductById(Long id) {
        log.debug("Fetching product with id: {} from database", id);
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
    }

    // Search using Elasticsearch
    public List<Product> searchProductsByName(String name) {
        log.debug("Searching products with name: {}", name);

        // If Elasticsearch is not available, fallback to database search
        if (productSearchRepository == null) {
            log.warn("Elasticsearch not available, using database search");
            return productRepository.findByNameContainingIgnoreCase(name);
        }

        try {
            List<ProductDocument> documents = productSearchRepository
                    .findByNameContainingOrDescriptionContainingAllIgnoreCase(name, name);

            // Convert documents to entities
            return documents.stream()
                    .map(doc -> getProductById(Long.parseLong(doc.getId())))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.warn("Elasticsearch search failed, falling back to database search", e);
            return productRepository.findByNameContainingIgnoreCase(name);
        }
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "products", key = "'all'")
    })
    public Product createProduct(ProductDTO productDTO) {
        log.debug("Creating new product: {}", productDTO.getName());
        Product product = new Product();
        product.setName(productDTO.getName());
        product.setDescription(productDTO.getDescription());
        product.setPrice(productDTO.getPrice());
        product.setStock(productDTO.getStock());

        Product savedProduct = productRepository.save(product);

        // Index in Elasticsearch
        indexProductInElasticsearch(savedProduct);

        return savedProduct;
    }

    @Transactional
    @Caching(
            put = {@CachePut(value = "products", key = "#id")},
            evict = {@CacheEvict(value = "products", key = "'all'")}
    )
    public Product updateProduct(Long id, ProductDTO productDTO) {
        log.debug("Updating product with id: {}", id);
        Product product = getProductById(id);
        product.setName(productDTO.getName());
        product.setDescription(productDTO.getDescription());
        product.setPrice(productDTO.getPrice());
        product.setStock(productDTO.getStock());

        Product updatedProduct = productRepository.save(product);

        // Update in Elasticsearch
        indexProductInElasticsearch(updatedProduct);

        return updatedProduct;
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "products", key = "#id"),
            @CacheEvict(value = "products", key = "'all'")
    })
    public void deleteProduct(Long id) {
        log.debug("Deleting product with id: {}", id);
        Product product = getProductById(id);
        productRepository.delete(product);

        // Remove from Elasticsearch
        if (productSearchRepository != null) {
            try {
                productSearchRepository.deleteById(id.toString());
            } catch (Exception e) {
                log.warn("Failed to delete product from Elasticsearch", e);
            }
        }
    }

    @Transactional
    @Caching(
            put = {@CachePut(value = "products", key = "#id")},
            evict = {@CacheEvict(value = "products", key = "'all'")}
    )
    public Product updateStock(Long id, Integer quantity) {
        log.debug("Updating stock for product id: {} by quantity: {}", id, quantity);
        Product product = getProductById(id);
        product.setStock(product.getStock() + quantity);
        Product updatedProduct = productRepository.save(product);

        // Update in Elasticsearch
        indexProductInElasticsearch(updatedProduct);

        return updatedProduct;
    }

    // Index product in Elasticsearch
    private void indexProductInElasticsearch(Product product) {
        if (productSearchRepository == null) {
            log.debug("Elasticsearch not available, skipping indexing");
            return;
        }

        try {
            ProductDocument document = ProductDocument.fromProduct(product);
            productSearchRepository.save(document);
            log.debug("Product {} indexed in Elasticsearch", product.getId());
        } catch (Exception e) {
            log.error("Failed to index product in Elasticsearch", e);
        }
    }

    // Reindex all products in Elasticsearch
    @Transactional(readOnly = true)
    public void reindexAllProducts() {
        log.info("Starting reindexing all products");
        List<Product> products = productRepository.findAll();

        products.forEach(product -> {
            try {
                indexProductInElasticsearch(product);
            } catch (Exception e) {
                log.error("Failed to index product: {}", product.getId(), e);
            }
        });

        log.info("Reindexing completed. Total products: {}", products.size());
    }
}

