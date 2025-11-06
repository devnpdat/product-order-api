package com.example.productorder;

import com.example.productorder.model.Product;
import com.example.productorder.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final ProductRepository productRepository;

    @Override
    public void run(String... args) throws Exception {
        // Create sample products
        if (productRepository.count() == 0) {
            Product product1 = new Product();
            product1.setName("iPhone 15 Pro");
            product1.setDescription("Latest iPhone with A17 Pro chip");
            product1.setPrice(new BigDecimal("1199.99"));
            product1.setStock(50);
            product1.setImageUrl("https://images.unsplash.com/photo-1695048133142-1a20484d2569?w=400");
            productRepository.save(product1);

            Product product2 = new Product();
            product2.setName("Samsung Galaxy S24");
            product2.setDescription("Flagship Samsung smartphone");
            product2.setPrice(new BigDecimal("999.99"));
            product2.setStock(30);
            product2.setImageUrl("https://images.unsplash.com/photo-1610945415295-d9bbf067e59c?w=400");
            productRepository.save(product2);

            Product product3 = new Product();
            product3.setName("MacBook Pro 14\"");
            product3.setDescription("M3 Pro chip, 16GB RAM, 512GB SSD");
            product3.setPrice(new BigDecimal("1999.99"));
            product3.setStock(15);
            product3.setImageUrl("https://images.unsplash.com/photo-1517336714731-489689fd1ca8?w=400");
            productRepository.save(product3);

            Product product4 = new Product();
            product4.setName("AirPods Pro 2");
            product4.setDescription("Active Noise Cancellation");
            product4.setPrice(new BigDecimal("249.99"));
            product4.setStock(100);
            product4.setImageUrl("https://images.unsplash.com/photo-1606841837239-c5a1a4a07af7?w=400");
            productRepository.save(product4);

            Product product5 = new Product();
            product5.setName("iPad Air");
            product5.setDescription("10.9-inch Liquid Retina display");
            product5.setPrice(new BigDecimal("599.99"));
            product5.setStock(40);
            product5.setImageUrl("https://images.unsplash.com/photo-1544244015-0df4b3ffc6b0?w=400");
            productRepository.save(product5);

            System.out.println("✅ Sample products have been initialized!");
        }
    }
}

