package com.rapidrent.rapidrent.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.rapidrent.rapidrent.model.Car;
import com.rapidrent.rapidrent.model.CarStatus;

import jakarta.persistence.LockModeType;

@Repository
public interface CarRepository extends JpaRepository<Car, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT c FROM Car c WHERE c.id = :id AND c.status = 'APPROVED'")
    Optional<Car> findByIdAndLock(@Param("id") Long id);

    List<Car> findByStatus(CarStatus status);

    @Query("""
            SELECT c FROM Car c
            WHERE c.status = :status
              AND (:keyword = '' OR 
                   LOWER(COALESCE(c.location, '')) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
                   LOWER(COALESCE(c.brand, '')) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
                   LOWER(COALESCE(c.model, '')) LIKE LOWER(CONCAT('%', :keyword, '%')))
              AND (:category = '' OR LOWER(COALESCE(c.category, '')) = LOWER(:category))
              AND (:transmission = '' OR LOWER(COALESCE(c.transmission, '')) = LOWER(:transmission))
              AND (:maxPrice < 0 OR c.price <= :maxPrice)
            ORDER BY c.price ASC
            """)
    List<Car> searchAvailableCars(
            @Param("status") CarStatus status,
            @Param("keyword") String keyword,
            @Param("category") String category,
            @Param("transmission") String transmission,
            @Param("maxPrice") Double maxPrice);

    List<Car> findByProviderIdOrderByIdDesc(Long providerId);

    long countByStatus(CarStatus status);

    long countByProviderId(Long providerId);
}
