package com.example.productorder.document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.math.BigDecimal;

@Document(indexName = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDocument {

    @Id
    private String id;

    @Field(type = FieldType.Text, analyzer = "standard")
    private String name;

    @Field(type = FieldType.Text, analyzer = "standard")
    private String description;

    @Field(type = FieldType.Double)
    private BigDecimal price;

    @Field(type = FieldType.Integer)
    private Integer stock;

    @Field(type = FieldType.Boolean)
    private Boolean available;

    // Convert from Product entity
    public static ProductDocument fromProduct(com.example.productorder.model.Product product) {
        ProductDocument doc = new ProductDocument();
        doc.setId(product.getId().toString());
        doc.setName(product.getName());
        doc.setDescription(product.getDescription());
        doc.setPrice(product.getPrice());
        doc.setStock(product.getStock());
        doc.setAvailable(product.getStock() > 0);
        return doc;
    }
}

