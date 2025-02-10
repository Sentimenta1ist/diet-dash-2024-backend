package app.dietdash.repository;

import app.dietdash.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {

    Page<Product> findByNameContainingIgnoreCaseAndLang(String name, String lang, Pageable pageable);

    Page<Product> findByLang(String lang, Pageable pageable);
}
