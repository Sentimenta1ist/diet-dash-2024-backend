package app.dietdash.controller;

import app.dietdash.dto.UserDto;
import app.dietdash.model.Product;
import app.dietdash.service.ProductService;
import app.dietdash.service.UtilService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/products/all")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final UtilService utilService;

    @GetMapping
    public ResponseEntity<Page<Product>> getAllProducts(@RequestParam(defaultValue = "0") int page,
                                                        @RequestParam(defaultValue = "20") int size,
                                                        @RequestParam(defaultValue = "en") String lang,
                                                        @RequestParam(required = false) String term,
                                                        @AuthenticationPrincipal UserDto principal) {
        return ResponseEntity.ok(productService.getProductsPage(term, lang, principal, PageRequest.of(page, size)));
    }

    @GetMapping("/created")
    public ResponseEntity<Page<Product>> getProductsOfUser(@RequestParam(defaultValue = "0") int page,
                                                           @RequestParam(defaultValue = "20") int size,
                                                           @RequestParam(required = false) String term,
                                                           @AuthenticationPrincipal UserDto principal) {
        return ResponseEntity.ok(productService.getProductCreatedPage(term, principal, PageRequest.of(page, size)));
    }

    @GetMapping("/base")
    public ResponseEntity<Page<Product>> getBaseProducts(@RequestParam(defaultValue = "0") int page,
                                                         @RequestParam(defaultValue = "20") int size,
                                                         @RequestParam(defaultValue = "en") String lang,
                                                         @RequestParam(required = false) String term) {
        return ResponseEntity.ok(productService.getProductBasePage(lang, term, PageRequest.of(page, size)));
    }

    @PostMapping
    public ResponseEntity<String> addProduct(@RequestBody Product product, @AuthenticationPrincipal UserDto principal) {
        productService.createProduct(product, principal);
        return ResponseEntity.ok("Product added");
    }

    @PostMapping("/fill")
    public ResponseEntity<String> addProduct() {
        productService.fill()
                .thenRun(() -> System.out.println("Filled."))
                .exceptionally(throwable -> {
                    System.err.println("Error: " + throwable.getMessage());
                    return null;
                });
        return ResponseEntity.accepted().body("Filling started.");
    }

}
