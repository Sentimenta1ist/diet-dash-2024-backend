package app.dietdash.response;

import app.dietdash.model.Product;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class ProductResponse {
    private Long id;
    private String name;
    private float calories;
    private float proteins;
    private float fats;
    private float carbs;
    private boolean created;

    public static ProductResponse fromProductWithLang(final Product product, final String lang) {
        return ProductResponse.builder()
                .id(product.getId())
                .created(product.getUser() != null)
                .carbs(product.getCarbs())
                .calories(product.getCalories())
                .proteins(product.getProteins())
                .fats(product.getFats())
                .name("en".equalsIgnoreCase(lang) ? product.getEngName() : product.getName())
                .build();
    }
}
