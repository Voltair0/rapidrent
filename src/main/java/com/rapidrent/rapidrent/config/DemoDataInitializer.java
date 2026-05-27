package com.rapidrent.rapidrent.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.rapidrent.rapidrent.model.Car;
import com.rapidrent.rapidrent.model.CarStatus;
import com.rapidrent.rapidrent.model.DocumentStatus;
import com.rapidrent.rapidrent.model.Role;
import com.rapidrent.rapidrent.model.User;
import com.rapidrent.rapidrent.repository.CarRepository;
import com.rapidrent.rapidrent.repository.UserRepository;

@Configuration
public class DemoDataInitializer {

    @Value("${rapidrent.demo-data.enabled:true}")
    private boolean demoDataEnabled;

    @Bean
    CommandLineRunner seedDemoData(UserRepository userRepository, CarRepository carRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (!demoDataEnabled || userRepository.count() > 0) {
                return;
            }

            User admin = createUser("Administrator", "admin@rapidrent.ro", "Admin123!", Role.ROLE_ADMIN, DocumentStatus.VALIDATED, passwordEncoder);
            User client = createUser("Client Demo", "client@rapidrent.ro", "Client123!", Role.ROLE_CLIENT, DocumentStatus.VALIDATED, passwordEncoder);
            User provider = createUser("Furnizor Demo", "furnizor@rapidrent.ro", "Furnizor123!", Role.ROLE_FURNIZOR, DocumentStatus.VALIDATED, passwordEncoder);

            userRepository.save(admin);
            userRepository.save(client);
            userRepository.save(provider);

            carRepository.save(createCar(provider, "Dacia", "Logan", "B-101-RRT", 135.0, "București Otopeni", "Economic", "Manuală", 5, null));
            carRepository.save(createCar(provider, "Renault", "Clio", "B-202-RRT", 165.0, "București Centru", "Compact", "Automată", 5, null));
            carRepository.save(createCar(provider, "Volkswagen", "T-Roc", "B-303-RRT", 249.0, "București Otopeni", "SUV", "Automată", 5, null));
            carRepository.save(createCar(provider, "Skoda", "Octavia", "B-404-RRT", 210.0, "București Gara de Nord", "Sedan", "Manuală", 5, null));
        };
    }

    private User createUser(String username, String email, String password, Role role, DocumentStatus documentStatus, PasswordEncoder passwordEncoder) {
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(role);
        user.setGdprConsented(true);
        user.setDocumentStatus(documentStatus);
        return user;
    }

    private Car createCar(User provider, String brand, String model, String licensePlate, Double price,
                          String location, String category, String transmission, Integer seats, String imageUrl) {
        Car car = new Car();
        car.setProvider(provider);
        car.setBrand(brand);
        car.setModel(model);
        car.setLicensePlate(licensePlate);
        car.setPrice(price);
        car.setLocation(location);
        car.setCategory(category);
        car.setTransmission(transmission);
        car.setSeats(seats);
        car.setImageUrl(imageUrl);
        car.setStatus(CarStatus.APPROVED);
        return car;
    }
}
