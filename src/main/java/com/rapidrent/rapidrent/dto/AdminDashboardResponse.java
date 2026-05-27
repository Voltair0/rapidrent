package com.rapidrent.rapidrent.dto;

import java.util.List;

import lombok.Data;

@Data
public class AdminDashboardResponse {
    private long totalActiveCars;
    private double totalPlatformProfit; // Cei 10% comision
    private List<String> last5Reservations; // O listă simplă de texte pentru a nu încărca ecranul
}