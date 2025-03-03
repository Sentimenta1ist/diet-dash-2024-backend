package app.dietdash.service;

import app.dietdash.dto.UserDto;
import app.dietdash.model.Product;
import app.dietdash.model.User;
import app.dietdash.repository.ProductRepository;
import app.dietdash.response.ProductResponse;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

@Service
@AllArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final UtilService utilService;

    private Page<ProductResponse> mapToProductResponsePage(Page<Product> productPage, String lang) {
        return productPage.map(product -> ProductResponse.fromProductWithLang(product, lang));
    }

    public Page<ProductResponse> getProductsPage(final String term, final String lang, final UserDto user, final Pageable pageable) {
        Objects.requireNonNull(user, "User cannot be null");
        Objects.requireNonNull(pageable, "Pageable cannot be null");

        Page<Product> result;

        if (term == null || term.trim().isEmpty()) {
            result = productRepository.findByUserIdIsNullOrUserId(user.getId(), pageable);
        } else {
            result = "en".equalsIgnoreCase(lang) ? // :_)
                    productRepository.findByUserIdAndEngName(user.getId(), term, pageable) :
                    productRepository.findByUserIdAndName(user.getId(), term, pageable);
        }

        return mapToProductResponsePage(result, lang);
    }

    public Page<ProductResponse> getProductBasePage(final String lang, final String term, final Pageable pageable) {
        Objects.requireNonNull(pageable, "Pageable cannot be null");

        Page<Product> result;
        if (term == null || term.trim().isEmpty()) {
            result = productRepository.findByUserIdIsNull(pageable);
        } else {
            result = "en".equalsIgnoreCase(lang) ?
                    productRepository.findByEngNameContainingIgnoreCaseAndUserIdIsNull(term, pageable) :
                    productRepository.findByNameContainingIgnoreCaseAndUserIdIsNull(term, pageable);
        }

        return mapToProductResponsePage(result, lang);
    }

    public Page<ProductResponse> getProductCreatedPage(final String term, final UserDto user, final Pageable pageable) {
        Objects.requireNonNull(user, "User cannot be null");
        Objects.requireNonNull(pageable, "Pageable cannot be null");

        Page<Product> result;
        if (term == null || term.trim().isEmpty()) {
            result = productRepository.findByUserId(user.getId(), pageable);
        } else {
            result = productRepository.findByNameContainingIgnoreCaseAndUserId(term, user.getId(), pageable);
        }
        return mapToProductResponsePage(result, "en");
    }

    @Async
    @Transactional
    public CompletableFuture<Void> fill() {
        List<Product> products = new ArrayList<>(1000); // Буфер для пакетной записи

        try (InputStream inputStream = ProductService.class.getClassLoader().getResourceAsStream("utils/main_data.csv");
             BufferedReader br = new BufferedReader(new InputStreamReader(Objects.requireNonNull(inputStream)))) {

            String line;
            int batchSize = 1000; // Размер пакета для сохранения
            int lineNumber = 0;

            while ((line = br.readLine()) != null) {
                lineNumber++;
                try {
                    String[] values = line.split(",", -1); // -1 для обработки пустых полей

                    if (values.length < 5) {
                        System.err.println("incorrect lenthgs " + lineNumber + ": " + line);
                        continue;
                    }

                    String engName = values[0].trim();
                    engName = StringUtils.capitalize(engName);

                    String name = values[1].trim();
                    name = StringUtils.capitalize(name);

                    float calories = Float.parseFloat(values[2].trim());
                    float proteins = Float.parseFloat(values[3].trim());
                    float fats = Float.parseFloat(values[4].trim());
                    float carbs = Float.parseFloat(values[5].trim());

                    Product product = new Product(null, name, engName, calories, proteins, fats, carbs, null);
                    products.add(product);

                    // Пакетная запись
                    if (products.size() >= batchSize) {
                        productRepository.saveAll(products);
                        products.clear(); // Очищаем список после сохранения
                    }
                } catch (NumberFormatException e) {
                    System.err.println("Parsing error " + lineNumber + ": " + line + " - " + e.getMessage());
                }
            }

            // Сохраняем остаток
            if (!products.isEmpty()) {
                productRepository.saveAll(products);
            }

        } catch (IOException e) {
            System.err.println("Error reading CSV: " + e.getMessage());
            return CompletableFuture.failedFuture(e);
        }

        return CompletableFuture.completedFuture(null);
    }


    @Transactional
    public void createProduct(Product product, UserDto principal) {
        final User user = utilService.getUserFromPrincipal(principal);
        product.setName(product.getName());
        product.setEngName(product.getName());
        Product entity = productRepository.save(product);
        user.addProduct(entity);
    }

    @Transactional
    public Product updateProduct(Product product) {
        return productRepository.save(product);
    }

    @Transactional
    public void deleteProduct(Product product) {
        productRepository.delete(product);
    }

    @Transactional
    public void deleteProductById(Long id) {
        productRepository.deleteById(id);
    }

    @Transactional
    public void deleteAllProducts() {
        productRepository.deleteAll();
    }

    public Product getProductById(Long productId) {
        return productRepository.findById(productId).orElse(null);
    }
}
