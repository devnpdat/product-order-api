package com.example.productorder.controller;

import com.example.productorder.dto.ProductDTO;
import com.example.productorder.model.Product;
import com.example.productorder.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Tag(name = "Products", description = "API quản lý sản phẩm")
public class ProductController {

    private final ProductService productService;

    @Operation(summary = "Lấy danh sách tất cả sản phẩm", description = "Trả về danh sách tất cả sản phẩm có trong hệ thống")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Thành công",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Product.class)))
    })
    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    @Operation(summary = "Lấy sản phẩm theo ID", description = "Trả về thông tin chi tiết của một sản phẩm")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tìm thấy sản phẩm",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Product.class))),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy sản phẩm", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(
            @Parameter(description = "ID của sản phẩm") @PathVariable Long id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    @Operation(summary = "Tìm kiếm sản phẩm theo tên", description = "Tìm kiếm sản phẩm theo tên (không phân biệt hoa thường)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Thành công",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Product.class)))
    })
    @GetMapping("/search")
    public ResponseEntity<List<Product>> searchProducts(
            @Parameter(description = "Tên sản phẩm cần tìm") @RequestParam String name) {
        return ResponseEntity.ok(productService.searchProductsByName(name));
    }

    @Operation(summary = "Tạo sản phẩm mới", description = "Thêm một sản phẩm mới vào hệ thống")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Tạo sản phẩm thành công",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Product.class))),
            @ApiResponse(responseCode = "400", description = "Dữ liệu không hợp lệ", content = @Content)
    })
    @PostMapping
    public ResponseEntity<Product> createProduct(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Thông tin sản phẩm mới",
                    required = true,
                    content = @Content(schema = @Schema(implementation = ProductDTO.class)))
            @Valid @RequestBody ProductDTO productDTO) {
        Product createdProduct = productService.createProduct(productDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdProduct);
    }

    @Operation(summary = "Cập nhật sản phẩm", description = "Cập nhật thông tin của một sản phẩm đã có")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cập nhật thành công",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Product.class))),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy sản phẩm", content = @Content),
            @ApiResponse(responseCode = "400", description = "Dữ liệu không hợp lệ", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(
            @Parameter(description = "ID của sản phẩm cần cập nhật") @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Thông tin sản phẩm mới",
                    required = true,
                    content = @Content(schema = @Schema(implementation = ProductDTO.class)))
            @Valid @RequestBody ProductDTO productDTO) {
        Product updatedProduct = productService.updateProduct(id, productDTO);
        return ResponseEntity.ok(updatedProduct);
    }

    @Operation(summary = "Xóa sản phẩm", description = "Xóa một sản phẩm khỏi hệ thống")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Xóa thành công"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy sản phẩm", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(
            @Parameter(description = "ID của sản phẩm cần xóa") @PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/stock")
    public ResponseEntity<Product> updateStock(
            @PathVariable Long id,
            @RequestParam Integer quantity) {
        Product updatedProduct = productService.updateStock(id, quantity);
        return ResponseEntity.ok(updatedProduct);
    }
}

