package com.psp.todo.web;

import com.psp.todo.model.Category;
import com.psp.todo.model.User;
import com.psp.todo.repo.CategoryRepository;
import com.psp.todo.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import java.util.List;

/** Runs at start-up: adds default categories and the admin account. */
@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {
    private final CategoryRepository cats;
    private final UserRepository users;
    private final PasswordEncoder encoder;
    @Value("${app.admin-password:}")
    private String adminPassword;

    @Override
    public void run(String... args) {
        if (cats.count() == 0) {
            cats.saveAll(List.of(new Category("School", "#C8102E"), new Category("Personal", "#F2B705"),
                    new Category("Project", "#2F6FDE"), new Category("Exam", "#1C1F24")));
        }
        if (!adminPassword.isBlank() && !users.existsByUsername("admin")) {
            User u = new User();
            u.setUsername("admin");
            u.setFullName("Administrator");
            u.setEmail("admin@psp.local");
            u.setRole("ADMIN");
            u.setPasswordHash(encoder.encode(adminPassword));
            users.save(u);
        }
    }
}
