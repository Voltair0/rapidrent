package com.rapidrent.rapidrent.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rapidrent.rapidrent.dto.CarRequest;
import com.rapidrent.rapidrent.dto.ProviderDashboardResponse;
import com.rapidrent.rapidrent.model.Car;
import com.rapidrent.rapidrent.service.CarService;
import com.rapidrent.rapidrent.service.DashboardService;

@RestController
@RequestMapping("/api/provider/cars")
public class CarController {

    @Autowired
    private CarService carService;

    @Autowired
    private DashboardService dashboardService;

    @PostMapping("/add")
    public ResponseEntity<String> addCar(@RequestBody CarRequest request) {
        try {
            String message = carService.addCar(request);
            return ResponseEntity.ok(message);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/provider/{providerId}")
    public ResponseEntity<List<Car>> getProviderCars(@PathVariable Long providerId) {
        return ResponseEntity.ok(carService.getProviderCars(providerId));
    }

    @GetMapping("/dashboard/{providerId}")
    public ResponseEntity<ProviderDashboardResponse> getProviderDashboard(@PathVariable Long providerId) {
        return ResponseEntity.ok(dashboardService.getProviderDashboard(providerId));
    }
    @org.springframework.web.bind.annotation.DeleteMapping("/delete/{carId}")
    public ResponseEntity<String> deleteCar(@PathVariable Long carId) {
        try {
            String message = carService.deleteCar(carId);
            return ResponseEntity.ok(message);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
