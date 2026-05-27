package com.rapidrent.rapidrent.dto;

import lombok.Data;

@Data
public class ProviderDashboardResponse {
    private long totalCarsListed;
    private double netIncome; // Cei 90% care îi revin furnizorului
}