package com.example.productorder.controller;

import com.example.productorder.dto.OrderRequestDTO;
import com.example.productorder.dto.OrderResponseDTO;
import com.example.productorder.model.Order;
import com.example.productorder.service.OrderService;
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
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Tag(name = "Orders", description = "API quản lý đơn hàng")
public class OrderController {

    private final OrderService orderService;

    @Operation(summary = "Lấy danh sách tất cả đơn hàng", description = "Trả về danh sách tất cả đơn hàng trong hệ thống")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Thành công",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = OrderResponseDTO.class)))
    })
    @GetMapping
    public ResponseEntity<List<OrderResponseDTO>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    @Operation(summary = "Lấy đơn hàng theo ID", description = "Trả về thông tin chi tiết của một đơn hàng")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tìm thấy đơn hàng",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = OrderResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy đơn hàng", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<OrderResponseDTO> getOrderById(
            @Parameter(description = "ID của đơn hàng") @PathVariable Long id) {
        return ResponseEntity.ok(orderService.getOrderById(id));
    }

    @Operation(summary = "Lấy đơn hàng theo mã đơn hàng", description = "Tìm đơn hàng theo order number")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tìm thấy đơn hàng",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = OrderResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy đơn hàng", content = @Content)
    })
    @GetMapping("/number/{orderNumber}")
    public ResponseEntity<OrderResponseDTO> getOrderByOrderNumber(
            @Parameter(description = "Mã đơn hàng (ví dụ: ORD-20231031-001)") @PathVariable String orderNumber) {
        return ResponseEntity.ok(orderService.getOrderByOrderNumber(orderNumber));
    }

    @Operation(summary = "Lấy đơn hàng theo trạng thái", description = "Lọc đơn hàng theo trạng thái")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Thành công",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = OrderResponseDTO.class)))
    })
    @GetMapping("/status/{status}")
    public ResponseEntity<List<OrderResponseDTO>> getOrdersByStatus(
            @Parameter(description = "Trạng thái đơn hàng",
                    schema = @Schema(implementation = Order.OrderStatus.class))
            @PathVariable Order.OrderStatus status) {
        return ResponseEntity.ok(orderService.getOrdersByStatus(status));
    }

    @Operation(summary = "Tạo đơn hàng mới", description = "Tạo một đơn hàng mới với danh sách sản phẩm")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Tạo đơn hàng thành công",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = OrderResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Dữ liệu không hợp lệ hoặc không đủ hàng", content = @Content)
    })
    @PostMapping
    public ResponseEntity<OrderResponseDTO> createOrder(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Thông tin đơn hàng mới",
                    required = true,
                    content = @Content(schema = @Schema(implementation = OrderRequestDTO.class)))
            @Valid @RequestBody OrderRequestDTO requestDTO) {
        OrderResponseDTO createdOrder = orderService.createOrder(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdOrder);
    }

    @Operation(summary = "Cập nhật trạng thái đơn hàng", description = "Thay đổi trạng thái của một đơn hàng")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cập nhật thành công",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = OrderResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy đơn hàng", content = @Content)
    })
    @PatchMapping("/{id}/status")
    public ResponseEntity<OrderResponseDTO> updateOrderStatus(
            @Parameter(description = "ID của đơn hàng") @PathVariable Long id,
            @Parameter(description = "Trạng thái mới",
                    schema = @Schema(implementation = Order.OrderStatus.class))
            @RequestParam Order.OrderStatus status) {
        OrderResponseDTO updatedOrder = orderService.updateOrderStatus(id, status);
        return ResponseEntity.ok(updatedOrder);
    }

    @Operation(summary = "Hủy đơn hàng", description = "Hủy một đơn hàng và hoàn trả số lượng sản phẩm vào kho")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Hủy đơn hàng thành công"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy đơn hàng", content = @Content)
    })
    @PostMapping("/{id}/cancel")
    public ResponseEntity<Void> cancelOrder(
            @Parameter(description = "ID của đơn hàng cần hủy") @PathVariable Long id) {
        orderService.cancelOrder(id);
        return ResponseEntity.noContent().build();
    }
}

