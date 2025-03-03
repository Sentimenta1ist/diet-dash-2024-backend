package app.dietdash;

import app.dietdash.dto.UserDto;
import app.dietdash.model.Product;
import app.dietdash.model.User;
import app.dietdash.repository.ProductRepository;
import app.dietdash.response.ProductResponse;
import app.dietdash.service.ProductService;
import app.dietdash.service.UtilService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ProductServiceTests {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private UtilService utilService;

    @InjectMocks
    private ProductService productService;

    private UserDto user;
    private Pageable pageable;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = new UserDto();
        user.setId(1L); // Пример ID пользователя

        pageable = mock(Pageable.class); // Мокируем Pageable
    }

    @Test
    void testGetProductsPage_withTermAndLanguageEn_shouldReturnProductsForUser() {
        String term = "apple";
        String lang = "en";
        Product product = new Product();
        product.setId(1L);
        product.setName("Apple");
        product.setEngName("Apple");
        User usr = convertToUser(user);
        product.setUser(usr);

        List<Product> products = List.of(product);
        Page<Product> productPage = new PageImpl<>(products);

        when(productRepository.findByUserIdAndEngName(user.getId(), term, pageable)).thenReturn(productPage);

        Page<ProductResponse> result = productService.getProductsPage(term, lang, user, pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().getFirst().getName()).isEqualTo("Apple");
        verify(productRepository, times(1)).findByUserIdAndEngName(user.getId(), term, pageable);
    }

    @Test
    void testGetProductsPage_withoutTerm_shouldReturnAllProductsForUser() {
        String term = null;
        String lang = "ru";

        Product product1 = new Product();
        product1.setId(1L);
        product1.setName("Apple");
        product1.setUser(convertToUser(user));

        Product product2 = new Product();
        product2.setId(2L);
        product2.setName("Banana");
        product2.setUser(convertToUser(user));

        List<Product> products = List.of(product1, product2);
        Page<Product> productPage = new PageImpl<>(products);

        when(productRepository.findByUserIdIsNullOrUserId(user.getId(), pageable)).thenReturn(productPage);

        Page<ProductResponse> result = productService.getProductsPage(term, lang, user, pageable);

        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent().get(0).getName()).isEqualTo("Apple");
        assertThat(result.getContent().get(1).getName()).isEqualTo("Banana");
        verify(productRepository, times(1)).findByUserIdIsNullOrUserId(user.getId(), pageable);
    }

    @Test
    void testGetProductBasePage_withTerm_shouldReturnFilteredProducts() {
        String term = "apple";
        String lang = "en";

        Product product = new Product();
        product.setId(1L);
        product.setEngName("Apple");
        product.setEngName("Apple");

        List<Product> products = List.of(product);
        Page<Product> productPage = new PageImpl<>(products);

        when(productRepository.findByEngNameContainingIgnoreCaseAndUserIdIsNull(term, pageable)).thenReturn(productPage);

        Page<ProductResponse> result = productService.getProductBasePage(lang, term, pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().getFirst().getName()).isEqualTo("Apple");
        verify(productRepository, times(1)).findByEngNameContainingIgnoreCaseAndUserIdIsNull(term, pageable);
    }

    @Test
    void testGetProductCreatedPage_withTerm_shouldReturnProductsForUser() {
        String term = "apple";
        Product product = new Product();
        product.setId(1L);
        product.setEngName("Apple");
        User usr = convertToUser(user);
        product.setUser(usr);

        List<Product> products = List.of(product);
        Page<Product> productPage = new PageImpl<>(products);

        when(productRepository.findByNameContainingIgnoreCaseAndUserId(term, user.getId(), pageable)).thenReturn(productPage);

        Page<ProductResponse> result = productService.getProductCreatedPage(term, user, pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().getFirst().getName()).isEqualTo("Apple");
        verify(productRepository, times(1)).findByNameContainingIgnoreCaseAndUserId(term, user.getId(), pageable);
    }

    @Test
    void testGetProductBasePage_withoutTerm_shouldReturnAllProducts() {
        String term = null;
        String lang = "ru";

        Product product1 = new Product();
        product1.setId(1L);
        product1.setName("Apple");

        Product product2 = new Product();
        product2.setId(2L);
        product2.setName("Banana");

        List<Product> products = List.of(product1, product2);
        Page<Product> productPage = new PageImpl<>(products);

        when(productRepository.findByUserIdIsNull(pageable)).thenReturn(productPage);

        Page<ProductResponse> result = productService.getProductBasePage(lang, term, pageable);

        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent().get(0).getName()).isEqualTo("Apple");
        assertThat(result.getContent().get(1).getName()).isEqualTo("Banana");
        verify(productRepository, times(1)).findByUserIdIsNull(pageable);
    }

    @Test
    void testGetProductCreatedPage_withEmptyTerm_shouldReturnAllUserProducts() {
        String term = "";
        Product product1 = new Product();
        product1.setId(1L);
        product1.setEngName("Apple");
        product1.setUser(convertToUser(user));

        Product product2 = new Product();
        product2.setId(2L);
        product2.setEngName("Banana");
        product2.setUser(convertToUser(user));

        List<Product> products = List.of(product1, product2);
        Page<Product> productPage = new PageImpl<>(products);

        when(productRepository.findByUserId(user.getId(), pageable)).thenReturn(productPage);

        Page<ProductResponse> result = productService.getProductCreatedPage(term, user, pageable);

        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent().get(0).getName()).isEqualTo("Apple");
        assertThat(result.getContent().get(1).getName()).isEqualTo("Banana");
        verify(productRepository, times(1)).findByUserId(user.getId(), pageable);
    }

    // Пример преобразования UserDto в User
    private User convertToUser(UserDto userDto) {
        User user = new User();
        user.setId(userDto.getId());
        return user;
    }

}

