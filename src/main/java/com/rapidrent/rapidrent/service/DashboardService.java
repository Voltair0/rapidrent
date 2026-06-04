package com.rapidrent.rapidrent.service;

import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.rapidrent.rapidrent.dto.AdminDashboardResponse;
import com.rapidrent.rapidrent.dto.ProviderDashboardResponse;
import com.rapidrent.rapidrent.model.CarStatus;
import com.rapidrent.rapidrent.repository.CarRepository;
import com.rapidrent.rapidrent.repository.ReservationRepository;

@Service
public class DashboardService {

    @Autowired
    private CarRepository carRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    private final double COMMISSION_RATE = 0.10; // Comision 10%

    // UC9: Calcule pentru Administrator
    public AdminDashboardResponse getAdminDashboard() {
        AdminDashboardResponse response = new AdminDashboardResponse();
        
        response.setTotalActiveCars(carRepository.countByStatus(CarStatus.APPROVED));
        
        double totalRevenue = reservationRepository.sumTotalRevenue();
        response.setTotalPlatformProfit(totalRevenue * COMMISSION_RATE);

        // Modificarea este aici: adăugăm | Status: " + r.getStatus()
        response.setLast5Reservations(reservationRepository.findTop5ByOrderByStartDateDesc()
                .stream()
                .map(r -> "Rezervare #" + r.getId() + " | Mașina: " + r.getCar().getBrand() + " | Cost: " + r.getTotalPrice() + " RON | Status: " + r.getStatus())
                .collect(Collectors.toList()));

        return response;
    }

    // UC11: Calcule pentru Furnizor
    public ProviderDashboardResponse getProviderDashboard(Long providerId) {
        ProviderDashboardResponse response = new ProviderDashboardResponse();
        
        response.setTotalCarsListed(carRepository.countByProviderId(providerId));
        
        double grossRevenue = reservationRepository.sumProviderRevenue(providerId);
        response.setNetIncome(grossRevenue * (1 - COMMISSION_RATE)); // Furnizorul primește 90%

        return response;
    }
    // UC: Date pentru graficul de profit (Ultimele 6 luni)
    public java.util.List<java.util.Map<String, Object>> getProfitChartData() {
        java.util.List<com.rapidrent.rapidrent.model.Reservation> reservations = reservationRepository.findAll();
        java.time.LocalDate sixMonthsAgo = java.time.LocalDate.now().minusMonths(5).withDayOfMonth(1);
        
        java.util.Map<String, Double> monthlyProfit = new java.util.LinkedHashMap<>();
        java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("MMM yyyy", new java.util.Locale("ro", "RO"));
        
        // Inițializăm dicționarul cu ultimele 6 luni (valoare 0) pentru a avea axa completă pe grafic
        for (int i = 5; i >= 0; i--) {
            java.time.LocalDate month = java.time.LocalDate.now().minusMonths(i);
            monthlyProfit.put(month.format(formatter), 0.0);
        }
        
        // Adunăm comisioanele
        for (com.rapidrent.rapidrent.model.Reservation r : reservations) {
            if (("ACTIVE".equals(r.getStatus()) || "COMPLETED".equals(r.getStatus())) && !r.getStartDate().isBefore(sixMonthsAgo)) {
                String monthKey = r.getStartDate().format(formatter);
                if (monthlyProfit.containsKey(monthKey)) {
                    monthlyProfit.put(monthKey, monthlyProfit.get(monthKey) + (r.getTotalPrice() * COMMISSION_RATE));
                }
            }
        }
        
        // Transformăm într-o listă pentru JSON
        java.util.List<java.util.Map<String, Object>> chartData = new java.util.ArrayList<>();
        for (java.util.Map.Entry<String, Double> entry : monthlyProfit.entrySet()) {
            java.util.Map<String, Object> dataPoint = new java.util.HashMap<>();
            dataPoint.put("month", entry.getKey());
            dataPoint.put("profit", entry.getValue());
            chartData.add(dataPoint);
        }
        return chartData;
    }
}