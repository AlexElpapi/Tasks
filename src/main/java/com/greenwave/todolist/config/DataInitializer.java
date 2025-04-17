package com.greenwave.todolist.config;

import java.util.Map;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.greenwave.todolist.model.User;
import com.greenwave.todolist.repository.UserRepository;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initUsers(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            // 🔹 Utenti da inserire: username → [password, ruolo, ruoloVisibile]
            Map<String, String[]> utenti = Map.of(
                "Alex",     new String[]{"Alex123", "ROLE_MANAGER", "Manager"},
                "alice",    new String[]{"alice123", "ROLE_MANAGER", "Manager"},
                "luca",     new String[]{"luca123", "ROLE_DESIGNER", "Designer"},
                "marta",    new String[]{"marta123", "ROLE_COPYWRITER", "Copywriter"},
                "simone",   new String[]{"simone123", "ROLE_SOCIAL", "Social Media"},
                "giulia",   new String[]{"giulia123", "ROLE_FRONTEND", "Frontend"},
                "marco",    new String[]{"marco123", "ROLE_BACKEND", "Backend"},
                "elena",    new String[]{"elena123", "ROLE_QA", "QA Tester"},
                "davide",   new String[]{"davide123", "ROLE_FULLSTACK", "Fullstack"}
            );

            for (Map.Entry<String, String[]> entry : utenti.entrySet()) {
                String username = entry.getKey();
                String[] dati = entry.getValue();
                String password = dati[0];
                String ruolo = dati[1];
                String ruoloVisibile = dati[2];

                if (userRepository.findByUsername(username) == null) {
                    User u = new User();
                    u.setUsername(username);
                    u.setPassword(passwordEncoder.encode(password));
                    u.setRoles(ruolo);
                    u.setRuoloVisibile(ruoloVisibile);
                    userRepository.save(u);
                    System.out.println("✅ Utente creato: " + username);
                } else {
                    System.out.println("ℹ️ Utente già esistente: " + username);
                }
            }
        };
    }
}
