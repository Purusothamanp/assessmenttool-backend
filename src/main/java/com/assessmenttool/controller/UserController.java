package com.assessmenttool.controller;

import com.assessmenttool.model.User;
import com.assessmenttool.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
        String email = credentials.get("email");
        String password = credentials.get("password");

        if (email == null || email.isBlank() || password == null || password.isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "Email and password are required."));
        }

        String normalizedEmail = email.trim().toLowerCase();
        Optional<User> userOpt = userRepository.findByEmail(normalizedEmail);
        if (userOpt.isEmpty()) {
            userOpt = userRepository.findByEmail(email.trim());
        }

        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Invalid email or password."));
        }

        User user = userOpt.get();

        boolean matches = false;
        String currentPassword = user.getPassword();
        if (currentPassword != null) {
            if (currentPassword.startsWith("$2a$") || currentPassword.startsWith("$2b$") || currentPassword.startsWith("$2y$")) {
                matches = passwordEncoder.matches(password, currentPassword);
            } else {
                matches = currentPassword.equals(password);
                if (matches) {
                    // Migrate to BCrypt on the fly
                    user.setPassword(passwordEncoder.encode(password));
                    userRepository.save(user);
                }
            }
        }

        if (!matches) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Invalid email or password."));
        }

        if ("inactive".equalsIgnoreCase(user.getStatus())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", "Your account is currently inactive."));
        }

        // Update last login timestamp
        String lastLoginStr = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm").format(LocalDateTime.now());
        user.setLastLogin(lastLoginStr);
        userRepository.save(user);

        Map<String, Object> response = new HashMap<>();
        response.put("id", user.getId());
        response.put("name", user.getName());
        response.put("email", user.getEmail());
        response.put("role", user.getRole());
        response.put("status", user.getStatus());
        response.put("lastLogin", user.getLastLogin());
        response.put("dob", user.getDob());
        response.put("studentId", user.getStudentId());
        response.put("token", "token-" + user.getId());

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public List<User> getUsers(@RequestParam(required = false) String email, @RequestParam(required = false) String role) {
        if (email != null && !email.isBlank()) {
            Optional<User> user = userRepository.findByEmail(email);
            return user.map(Collections::singletonList).orElse(Collections.emptyList());
        }
        if (role != null && !role.isBlank()) {
            return userRepository.findByRole(role);
        }
        return userRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable String id) {
        return userRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody User user) {
        if (user.getId() == null || user.getId().isBlank()) {
            user.setId(UUID.randomUUID().toString().substring(0, 11));
        }
        if (user.getStatus() == null) {
            user.setStatus("active");
        }
        if (user.getLastLogin() == null) {
            user.setLastLogin(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm").format(LocalDateTime.now()));
        }

        // Generate studentId if role is student and not provided
        if ("student".equalsIgnoreCase(user.getRole()) && (user.getStudentId() == null || user.getStudentId().isBlank())) {
            long count = userRepository.countByRole("student");
            user.setStudentId(String.format("st%03d", count + 1));
        }

        // Hash password with BCrypt
        if (user.getPassword() != null && !user.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        } else {
            user.setPassword(passwordEncoder.encode("password123"));
        }
        
        Optional<User> existing = userRepository.findByEmail(user.getEmail());
        if (existing.isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message", "Email already exists"));
        }

        User savedUser = userRepository.save(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable String id, @RequestBody User userDetails) {
        return userRepository.findById(id).map(user -> {
            if (userDetails.getName() != null) user.setName(userDetails.getName());
            if (userDetails.getEmail() != null) user.setEmail(userDetails.getEmail());
            if (userDetails.getRole() != null) user.setRole(userDetails.getRole());
            if (userDetails.getStatus() != null) user.setStatus(userDetails.getStatus());
            if (userDetails.getPassword() != null && !userDetails.getPassword().isBlank()) {
                user.setPassword(passwordEncoder.encode(userDetails.getPassword()));
            }
            if (userDetails.getLastLogin() != null) user.setLastLogin(userDetails.getLastLogin());
            if (userDetails.getDob() != null) user.setDob(userDetails.getDob());
            if (userDetails.getStudentId() != null) user.setStudentId(userDetails.getStudentId());
            return ResponseEntity.ok(userRepository.save(user));
        }).orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}")
    public ResponseEntity<User> patchUser(@PathVariable String id, @RequestBody Map<String, Object> updates) {
        return userRepository.findById(id).map(user -> {
            if (updates.containsKey("name")) user.setName((String) updates.get("name"));
            if (updates.containsKey("email")) user.setEmail((String) updates.get("email"));
            if (updates.containsKey("role")) user.setRole((String) updates.get("role"));
            if (updates.containsKey("status")) user.setStatus((String) updates.get("status"));
            if (updates.containsKey("password")) {
                String rawPassword = (String) updates.get("password");
                if (rawPassword != null && !rawPassword.isBlank()) {
                    user.setPassword(passwordEncoder.encode(rawPassword));
                }
            }
            if (updates.containsKey("lastLogin")) user.setLastLogin((String) updates.get("lastLogin"));
            if (updates.containsKey("dob")) user.setDob((String) updates.get("dob"));
            if (updates.containsKey("studentId")) user.setStudentId((String) updates.get("studentId"));
            return ResponseEntity.ok(userRepository.save(user));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable String id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
