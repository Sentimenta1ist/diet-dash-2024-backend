package app.dietdash.service;

import app.dietdash.dto.UserDto;
import app.dietdash.model.Product;
import app.dietdash.model.User;
import app.dietdash.repository.ProductRepository;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.init.ResourceReader;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Objects;

@Service
@AllArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final UtilService utilService;

    public Page<Product> getProductsPage(final String term, final String lang, final Pageable pageable) {
        if (term == null) {
            return productRepository.findAll(pageable);
        } else {
            return productRepository.findByNameContainingIgnoreCase(term, pageable);
        }
    }

    @Async
    public void fill() {

        try (InputStream inputStream = ResourceReader.class.getClassLoader().getResourceAsStream("utils/file.csv");
             BufferedReader br = new BufferedReader(new InputStreamReader(Objects.requireNonNull(inputStream)))) {

            String line;
            while ((line = br.readLine()) != null) {

                // Split the line by commas
                String[] values = line.split(",");

                // Parse values and create a Product object
                String name = values[0].trim();
                name = StringUtils.capitalize(name);

                float calories = Float.parseFloat(values[1].trim());
                float proteins = Float.parseFloat(values[2].trim());
                float fats = Float.parseFloat(values[3].trim());
                float carbs = Float.parseFloat(values[4].trim());

                // Create a new Product object
                Product product = new Product(null, name, calories, proteins, fats, carbs, null);

                // Add the Product to the list
                productRepository.save(product);
            }

        } catch (IOException e) {
            System.err.println("Error reading the CSV file: " + e.getMessage());
        }
    }


    @Transactional
    public void createProduct(Product product, UserDto principal) {
        final User user = utilService.getUserFromPrincipal(principal);
        product.setName(product.getName().toLowerCase());
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
