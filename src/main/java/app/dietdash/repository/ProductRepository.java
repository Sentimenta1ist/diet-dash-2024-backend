package app.dietdash.repository;

import app.dietdash.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    Page<Product> findByUserIdIsNullOrUserId(Long userId, Pageable pageable);

    Page<Product> findByUserIdIsNull(Pageable pageable);

    Page<Product> findByNameContainingIgnoreCaseAndUserIdIsNull(String name, Pageable pageable);

    Page<Product> findByEngNameContainingIgnoreCaseAndUserIdIsNull(String engName, Pageable pageable);

    Page<Product> findByUserId(Long userId, Pageable pageable);

    Page<Product> findByNameContainingIgnoreCaseAndUserId(String name, Long userId, Pageable pageable);

    Page<Product> findByNameContainingIgnoreCaseAndUserIdIsNullOrUserId(String name, Long userId, Pageable pageable);

    Page<Product> findByEngNameContainingIgnoreCaseAndUserIdIsNullOrUserId(String engName, Long userId, Pageable pageable);
}
