package com.example.productorder.controller;

import com.example.productorder.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Tag(name = "Admin", description = "API quản trị hệ thống")
public class AdminController {

    private final ProductService productService;

    @Operation(summary = "Reindex tất cả sản phẩm vào Elasticsearch",
               description = "Đồng bộ lại tất cả sản phẩm từ database vào Elasticsearch")
    @PostMapping("/reindex-products")
    public ResponseEntity<Map<String, String>> reindexProducts() {
        productService.reindexAllProducts();

        Map<String, String> response = new HashMap<>();
        response.put("message", "Reindexing completed successfully");
        response.put("status", "success");

        return ResponseEntity.ok(response);
    }
}

