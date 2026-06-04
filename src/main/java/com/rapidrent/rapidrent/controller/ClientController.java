package com.rapidrent.rapidrent.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.rapidrent.rapidrent.dto.ReservationRequest;
import com.rapidrent.rapidrent.dto.ReservationSummaryResponse;
import com.rapidrent.rapidrent.model.Car;
import com.rapidrent.rapidrent.model.Reservation;
import com.rapidrent.rapidrent.service.ReservationService;

@RestController
@RequestMapping("/api/client")
public class ClientController {

    @Autowired
    private ReservationService reservationService;

    @GetMapping("/cars")
    public ResponseEntity<List<Car>> getAvailableCars(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String transmission,
            @RequestParam(required = false) Double maxPrice) {
        return ResponseEntity.ok(reservationService.searchAvailableCars(keyword, category, transmission, maxPrice));
    }

    @PostMapping("/reserve")
    public ResponseEntity<?> reserveCar(@RequestBody ReservationRequest request) {
        try {
            Reservation reservation = reservationService.createReservation(request);
            return ResponseEntity.ok("Rezervare creată cu succes! Cost total: " + reservation.getTotalPrice() + " RON.");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/reservations/{clientId}")
    public ResponseEntity<List<ReservationSummaryResponse>> getClientReservations(@PathVariable Long clientId) {
        return ResponseEntity.ok(reservationService.getClientReservations(clientId));
    }

    @PostMapping("/cancel/{reservationId}")
    public ResponseEntity<String> cancelReservation(
            @PathVariable Long reservationId,
            @RequestParam Long clientId) {
        try {
            String message = reservationService.cancelReservation(reservationId, clientId);
            return ResponseEntity.ok(message);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
