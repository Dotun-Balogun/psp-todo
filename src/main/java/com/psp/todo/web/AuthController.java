package com.psp.todo.web;

import com.psp.todo.model.User;
import com.psp.todo.repo.UserRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import java.util.Objects;

@Controller
@RequiredArgsConstructor
public class AuthController {
    private final UserRepository users;
    private final PasswordEncoder encoder;

    @Data
    public static class RegisterForm {
        @NotBlank(message = "Enter your full name")
        private String fullName;
        @Pattern(regexp = "^[A-Za-z0-9_.]{3,50}$", message = "Use 3–50 letters, numbers, dots or underscores")
        private String username;
        @NotBlank(message = "Enter your email") @Email(message = "Enter a valid email address")
        private String email;
        @Size(min = 8, message = "Password must be at least 8 characters")
        private String password;
        private String confirm;
    }

    @GetMapping("/")
    String root() { return "redirect:/dashboard"; }

    @GetMapping("/login")
    String login() { return "login"; }

    @GetMapping("/register")
    String registerForm(Model m) {
        m.addAttribute("form", new RegisterForm());
        return "register";
    }

    @PostMapping("/register")
    String register(@Valid @ModelAttribute("form") RegisterForm f, BindingResult br) {
        String username = f.getUsername() == null ? "" : f.getUsername().trim().toLowerCase();
        String email = f.getEmail() == null ? "" : f.getEmail().trim().toLowerCase();
        if (!Objects.equals(f.getPassword(), f.getConfirm()))
            br.rejectValue("confirm", "mismatch", "Passwords do not match");
        if (!br.hasFieldErrors("username") && users.existsByUsername(username))
            br.rejectValue("username", "taken", "This username is already taken");
        if (!br.hasFieldErrors("email") && users.existsByEmail(email))
            br.rejectValue("email", "taken", "This email is already registered");
        if (br.hasErrors()) return "register";

        User u = new User();
        u.setFullName(f.getFullName().trim());
        u.setUsername(username);
        u.setEmail(email);
        u.setPasswordHash(encoder.encode(f.getPassword()));
        users.save(u);
        return "redirect:/login?registered";
    }
}
