package com.example.productorder.repository;

import com.example.productorder.document.ProductDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductSearchRepository extends ElasticsearchRepository<ProductDocument, String> {

    List<ProductDocument> findByNameContainingIgnoreCase(String name);

    List<ProductDocument> findByDescriptionContainingIgnoreCase(String description);

    List<ProductDocument> findByNameContainingOrDescriptionContainingAllIgnoreCase(String name, String description);

    List<ProductDocument> findByAvailableTrue();
}

