package com.rapidrent.rapidrent.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.rapidrent.rapidrent.model.Reservation;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    @Query("SELECT COUNT(r) FROM Reservation r WHERE r.car.id = :carId AND r.status = 'ACTIVE' " +
           "AND r.startDate < :endDate AND r.endDate > :startDate")
    long countOverlappingReservations(@Param("carId") Long carId,
                                      @Param("startDate") LocalDate startDate,
                                      @Param("endDate") LocalDate endDate);

    List<Reservation> findTop5ByOrderByStartDateDesc();

    List<Reservation> findByClientIdOrderByStartDateDesc(Long clientId);

    @Query("SELECT COALESCE(SUM(r.totalPrice), 0) FROM Reservation r WHERE r.status = 'ACTIVE'")
    double sumTotalRevenue();

    @Query("SELECT COALESCE(SUM(r.totalPrice), 0) FROM Reservation r WHERE r.car.provider.id = :providerId AND r.status = 'ACTIVE'")
    double sumProviderRevenue(@Param("providerId") Long providerId);
}
