package com.rapidrent.rapidrent.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class FrontendController {

    @GetMapping("/ui")
    public String home(Model model) {
        model.addAttribute("pageTitle", "RapidRent - Închirieri auto");
        return "home";
    }

    @GetMapping("/ui/cars")
    public String cars(Model model) {
        model.addAttribute("pageTitle", "Mașini disponibile - RapidRent");
        return "cars";
    }

    @GetMapping("/ui/login")
    public String login(Model model) {
        model.addAttribute("pageTitle", "Autentificare - RapidRent");
        return "login";
    }

    @GetMapping("/ui/register")
    public String register(Model model) {
        model.addAttribute("pageTitle", "Înregistrare - RapidRent");
        return "register";
    }

    @GetMapping("/ui/client")
    public String client(Model model) {
        model.addAttribute("pageTitle", "Cont client - RapidRent");
        return "client";
    }

    @GetMapping("/ui/provider")
    public String provider(Model model) {
        model.addAttribute("pageTitle", "Dashboard furnizor - RapidRent");
        return "provider";
    }

    @GetMapping("/ui/provider/cars/new")
    public String newProviderCar(Model model) {
        model.addAttribute("pageTitle", "Adaugă mașină - RapidRent");
        return "provider-car-form";
    }

    @GetMapping("/ui/admin")
    public String admin(Model model) {
        model.addAttribute("pageTitle", "Dashboard admin - RapidRent");
        return "admin";
    }

    @GetMapping("/ui/verification")
    public String verification(Model model) {
        model.addAttribute("pageTitle", "Verificare cont - RapidRent");
        return "verification";
    }

    @GetMapping("/ui/reservation-confirmation")
    public String reservationConfirmation(Model model) {
        model.addAttribute("pageTitle", "Rezervare confirmată - RapidRent");
        return "reservation-confirmation";
    }
}
