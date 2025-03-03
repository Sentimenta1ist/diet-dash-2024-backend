package app.dietdash.repository;

import app.dietdash.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    Page<Product> findByUserIdIsNullOrUserId(Long userId, Pageable pageable);

    Page<Product> findByUserIdIsNull(Pageable pageable);

    Page<Product> findByEngNameContainingIgnoreCaseAndUserIdIsNull(String engName, Pageable pageable);

    Page<Product> findByNameContainingIgnoreCaseAndUserIdIsNull(String name, Pageable pageable);

    Page<Product> findByUserId(Long userId, Pageable pageable);

    Page<Product> findByNameContainingIgnoreCaseAndUserId(String name, Long userId, Pageable pageable);

    @Query("SELECT e FROM Product e WHERE (e.user.id IS NULL AND LOWER(e.name) LIKE LOWER(CONCAT('%', :name, '%'))) " +
            "OR (e.user.id = :userId AND LOWER(e.name) LIKE LOWER(CONCAT('%', :name, '%')))")
    Page<Product> findByUserIdAndName(
            @Param("userId") Long userId,
            @Param("name") String name,
            Pageable pageable
    );

    @Query("SELECT e FROM Product e WHERE (e.user.id IS NULL AND LOWER(e.engName) LIKE LOWER(CONCAT('%', :name, '%'))) " +
            "OR (e.user.id = :userId AND LOWER(e.engName) LIKE LOWER(CONCAT('%', :name, '%')))")
    Page<Product> findByUserIdAndEngName(
            @Param("userId") Long userId,
            @Param("name") String name,
            Pageable pageable
    );
}
