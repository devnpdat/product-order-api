package com.example.productorder.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImageUploadResponseDTO {
    private String imageUrl;
    private String message;
    private String filename;
}

