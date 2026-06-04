package com.rapidrent.rapidrent.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.rapidrent.rapidrent.dto.AdminDashboardResponse;
import com.rapidrent.rapidrent.dto.PendingDocumentResponse;
import com.rapidrent.rapidrent.model.Car;
import com.rapidrent.rapidrent.service.CarService;
import com.rapidrent.rapidrent.service.DashboardService;
import com.rapidrent.rapidrent.service.DocumentService;

@RestController
@RequestMapping("/api/admin/cars")
public class AdminController {

    @Autowired
    private CarService carService;

    @Autowired
    private DocumentService documentService;

    @Autowired
    private DashboardService dashboardService;

    @GetMapping("/pending")
    public ResponseEntity<List<Car>> getPendingCars() {
        List<Car> pendingCars = carService.getPendingCars();
        return ResponseEntity.ok(pendingCars);
    }

    @PostMapping("/moderate/{carId}")
    public ResponseEntity<String> moderateCar(
            @PathVariable Long carId,
            @RequestParam String action) {
        try {
            String message = carService.moderateCar(carId, action);
            return ResponseEntity.ok(message);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/documents/pending")
    public ResponseEntity<List<PendingDocumentResponse>> getPendingDocuments() {
        return ResponseEntity.ok(documentService.getPendingDocuments());
    }

    @PostMapping("/documents/moderate/{clientId}")
    public ResponseEntity<String> moderateDocuments(
            @PathVariable Long clientId,
            @RequestParam String action) {
        try {
            String message = documentService.moderateDocuments(clientId, action);
            return ResponseEntity.ok(message);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/dashboard")
    public ResponseEntity<AdminDashboardResponse> getDashboard() {
        return ResponseEntity.ok(dashboardService.getAdminDashboard());
    }
    @GetMapping("/active")
    public ResponseEntity<List<Car>> getActiveCars() {
        return ResponseEntity.ok(carService.getActiveCars());
    }

    @GetMapping("/finance/chart")
    public ResponseEntity<List<java.util.Map<String, Object>>> getProfitChart() {
        return ResponseEntity.ok(dashboardService.getProfitChartData());
    }
}
