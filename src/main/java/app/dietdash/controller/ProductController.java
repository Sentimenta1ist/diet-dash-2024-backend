package app.dietdash.controller;

import app.dietdash.dto.UserDto;
import app.dietdash.model.Product;
import app.dietdash.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public ResponseEntity<Page<Product>> getAllProducts(@RequestParam(defaultValue = "0") int page,
                                                        @RequestParam(defaultValue = "100") int size,
                                                        @RequestParam(required = false) String term) {
        final Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(productService.getProductsPage(term, pageable));
    }

    @PostMapping
    public ResponseEntity<String> addProduct(@RequestBody Product product,  @AuthenticationPrincipal UserDto principal) {
        productService.createProduct(product, principal);
        return ResponseEntity.ok("Product added");
    }

    @PostMapping("/fill")
    public ResponseEntity<String> addProduct() {
        productService.fill();
        return ResponseEntity.ok("Filled.");
    }

}
