package com.rapidrent.rapidrent.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.rapidrent.rapidrent.dto.CarRequest;
import com.rapidrent.rapidrent.model.Car;
import com.rapidrent.rapidrent.model.CarStatus;
import com.rapidrent.rapidrent.model.Role;
import com.rapidrent.rapidrent.model.User;
import com.rapidrent.rapidrent.repository.CarRepository;
import com.rapidrent.rapidrent.repository.ReservationRepository;
import com.rapidrent.rapidrent.repository.UserRepository;

@Service
public class CarService {

    @Autowired
    private CarRepository carRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    public String addCar(CarRequest request) {
        User provider = userRepository.findById(request.getProviderId())
                .orElseThrow(() -> new RuntimeException("Eroare: Utilizatorul nu a fost găsit."));

        if (provider.getRole() != Role.ROLE_FURNIZOR) {
            throw new RuntimeException("Eroare: Doar utilizatorii cu rol de FURNIZOR pot adăuga mașini.");
        }

        Car car = new Car();
        car.setProvider(provider);
        car.setBrand(request.getBrand());
        car.setModel(request.getModel());
        car.setLicensePlate(request.getLicensePlate());
        car.setPrice(request.getPrice());
        car.setLocation(defaultIfBlank(request.getLocation(), "București"));
        car.setCategory(defaultIfBlank(request.getCategory(), "Compact"));
        car.setTransmission(defaultIfBlank(request.getTransmission(), "Manuală"));
        car.setSeats(request.getSeats() == null ? 5 : request.getSeats());
        car.setImageUrl(request.getImageUrl());
        car.setStatus(CarStatus.PENDING);

        carRepository.save(car);

        return "Mașina " + car.getBrand() + " " + car.getModel() + " a fost adăugată cu succes și așteaptă aprobarea administratorului!";
    }

    public List<Car> getPendingCars() {
        return carRepository.findByStatus(CarStatus.PENDING);
    }

    public List<Car> getProviderCars(Long providerId) {
        return carRepository.findByProviderIdOrderByIdDesc(providerId);
    }

    public String moderateCar(Long carId, String action) {
        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new RuntimeException("Eroare: Mașina nu a fost găsită."));

        if ("APPROVE".equalsIgnoreCase(action)) {
            car.setStatus(CarStatus.APPROVED);
            carRepository.save(car);
            return "Mașina cu ID-ul " + carId + " (" + car.getBrand() + " " + car.getModel() + ") a fost APROBATĂ și este vizibilă clienților.";
        } else if ("REJECT".equalsIgnoreCase(action)) {
            car.setStatus(CarStatus.REJECTED);
            carRepository.save(car);
            return "Mașina cu ID-ul " + carId + " (" + car.getBrand() + " " + car.getModel() + ") a fost RESPINSĂ.";
        } else {
            throw new RuntimeException("Eroare: Acțiune necunoscută. Folosește 'APPROVE' sau 'REJECT'.");
        }
    }

    private String defaultIfBlank(String value, String fallback) {
        return value == null || value.trim().isEmpty() ? fallback : value.trim();
    }

    @Transactional
    public String deleteCar(Long carId) {
        if (!carRepository.existsById(carId)) {
            throw new RuntimeException("Eroare: Mașina nu a fost găsită.");
        }

       reservationRepository.deleteByCarId(carId);
        
        carRepository.deleteById(carId);
        
        return "Mașina și istoricul ei de rezervări au fost eliminate cu succes din platformă!";
    }
}
