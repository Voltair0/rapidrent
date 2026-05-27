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

        // Luăm ultimele 5 rezervări și le transformăm într-un text ușor de citit
        response.setLast5Reservations(reservationRepository.findTop5ByOrderByStartDateDesc()
                .stream()
                .map(r -> "Rezervare #" + r.getId() + " | Mașina: " + r.getCar().getBrand() + " | Cost: " + r.getTotalPrice() + " RON")
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
}