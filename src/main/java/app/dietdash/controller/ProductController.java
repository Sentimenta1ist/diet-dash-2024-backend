package app.dietdash.controller;

import app.dietdash.model.Product;
import app.dietdash.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public List<Product> getAllProducts() {
        return productService.getAllProducts();
    }

    @GetMapping("/{search}")
    public List<Product> getProductsByName(@PathVariable String search) {
        return productService.getProductsByName(search);
    }

    @PostMapping("/add")
    public ResponseEntity<String> addProduct(@RequestBody Product product) {
        Product added = productService.createProduct(product);
        return ResponseEntity.ok("Product added successfully.");
    }
}
