package com.rapidrent.rapidrent.service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.rapidrent.rapidrent.dto.ReservationRequest;
import com.rapidrent.rapidrent.dto.ReservationSummaryResponse;
import com.rapidrent.rapidrent.model.Car;
import com.rapidrent.rapidrent.model.CarStatus;
import com.rapidrent.rapidrent.model.DocumentStatus;
import com.rapidrent.rapidrent.model.Reservation;
import com.rapidrent.rapidrent.model.User;
import com.rapidrent.rapidrent.repository.CarRepository;
import com.rapidrent.rapidrent.repository.ReservationRepository;
import com.rapidrent.rapidrent.repository.UserRepository;

@Service
public class ReservationService {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private CarRepository carRepository;

    @Autowired
    private UserRepository userRepository;

    public List<Car> getAvailableCars() {
        return carRepository.findByStatus(CarStatus.APPROVED);
    }

    public List<Car> searchAvailableCars(String location, String category, String transmission, Double maxPrice) {
        return carRepository.searchAvailableCars(
                CarStatus.APPROVED,
                location == null ? "" : location.trim(),
                category == null ? "" : category.trim(),
                transmission == null ? "" : transmission.trim(),
                maxPrice == null ? -1.0 : maxPrice);
    }

    public List<ReservationSummaryResponse> getClientReservations(Long clientId) {
        return reservationRepository.findByClientIdOrderByStartDateDesc(clientId)
                .stream()
                .map(ReservationSummaryResponse::fromReservation)
                .collect(Collectors.toList());
    }

    @Transactional
    public Reservation createReservation(ReservationRequest request) {
        User client = userRepository.findById(request.getClientId())
                .orElseThrow(() -> new RuntimeException("Eroare: Clientul nu a fost găsit."));

        if (client.getDocumentStatus() != DocumentStatus.VALIDATED) {
            throw new RuntimeException("Eroare: Actele nu sunt validate. Un administrator trebuie să îți aprobe documentele înainte de a rezerva.");
        }

        Car car = carRepository.findByIdAndLock(request.getCarId())
                .orElseThrow(() -> new RuntimeException("Eroare: Mașina nu este disponibilă pentru închiriere momentan."));

        long overlaps = reservationRepository.countOverlappingReservations(request.getCarId(), request.getStartDate(), request.getEndDate());
        if (overlaps > 0) {
            throw new RuntimeException("Eroare: Mașina este deja rezervată pentru o parte sau toată perioada selectată.");
        }

        long days = ChronoUnit.DAYS.between(request.getStartDate(), request.getEndDate());
        if (days <= 0) {
            throw new RuntimeException("Eroare: Perioada selectată este invalidă. Minim 1 zi de închiriere.");
        }
        double totalPrice = days * car.getPrice();

        Reservation reservation = new Reservation();
        reservation.setClient(client);
        reservation.setCar(car);
        reservation.setStartDate(request.getStartDate());
        reservation.setEndDate(request.getEndDate());
        reservation.setTotalPrice(totalPrice);

        return reservationRepository.save(reservation);
    }

    @Transactional
    public String cancelReservation(Long reservationId, Long clientId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Eroare: Rezervarea nu a fost găsită."));

        if (!reservation.getClient().getId().equals(clientId)) {
            throw new RuntimeException("Eroare: Nu ai permisiunea de a anula această rezervare.");
        }

        if ("CANCELLED".equals(reservation.getStatus())) {
            throw new RuntimeException("Eroare: Rezervarea este deja anulată.");
        }

        long daysUntilStart = ChronoUnit.DAYS.between(LocalDate.now(), reservation.getStartDate());
        if (daysUntilStart < 1) {
            throw new RuntimeException("Eroare: Anularea nu se mai poate face. Au rămas mai puțin de 24 de ore până la preluarea mașinii.");
        }

        reservation.setStatus("CANCELLED");
        reservationRepository.save(reservation);

        return "Rezervarea a fost anulată cu succes. Mașina este din nou disponibilă pe platformă.";
    }
}
