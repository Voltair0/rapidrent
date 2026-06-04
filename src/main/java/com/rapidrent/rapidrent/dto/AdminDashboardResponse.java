package com.rapidrent.rapidrent.dto;

import java.util.List;

import lombok.Data;

@Data
public class AdminDashboardResponse {
    private long totalActiveCars;
    private double totalPlatformProfit;
    private List<String> last5Reservations;
}