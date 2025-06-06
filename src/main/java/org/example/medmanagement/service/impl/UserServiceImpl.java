package org.example.medmanagement.service.impl;

import org.example.medmanagement.dto.RegisterRequest;
import org.example.medmanagement.dto.UserDto;
import org.example.medmanagement.model.Role;
import org.example.medmanagement.model.User;
import org.example.medmanagement.repository.RoleRepository;
import org.example.medmanagement.repository.UserRepository;
import org.example.medmanagement.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;


@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    // -------------------- metody pomocnicze do mapowania --------------------

    private UserDto mapToDto(User user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setEnabled(user.isEnabled());
        dto.setRoles(
                user.getRoles().stream()
                        .map(Role::getName)
                        .collect(Collectors.toSet())
        );
        return dto;
    }

    private User mapToEntity(UserDto dto) {
        User newUser = new User();
        newUser.setEmail(dto.getEmail());
        newUser.setPassword(passwordEncoder.encode(dto.getPassword()));
        newUser.setEnabled(dto.isEnabled());

        Set<Role> roles = dto.getRoles().stream()
                .map(roleName -> roleRepository.findByName(roleName)
                        .orElseThrow(() -> new RuntimeException("Rola nie istnieje: " + roleName)))
                .collect(Collectors.toSet());
        newUser.setRoles(roles);

        return newUser;
    }

    // ----------------------------------------------------------------------

    /**
     * Rejestracja nowego użytkownika.
     */
    @Override
    public UserDto register(RegisterRequest request) {
        // 1. Jeśli w bazie już jest użytkownik o tym samym emailu → wyjątek
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Użytkownik o tym emailu już istnieje: " + request.getEmail());
        }

        // 2. Tworzymy nową encję User
        User newUser = new User();
        newUser.setEmail(request.getEmail());
        newUser.setPassword(passwordEncoder.encode(request.getPassword()));
        newUser.setEnabled(true);

        // 3. Przypisujemy role z request.getRoles()
        Set<String> requestedRoles = request.getRoles();
        Set<Role> roles = new HashSet<>();

        if (requestedRoles == null || requestedRoles.isEmpty()) {
            // Jeśli klient nie podał ról, domyślnie nadajemy "ROLE_USER"
            Role userRole = roleRepository.findByName("ROLE_USER")
                    .orElseThrow(() -> new RuntimeException("Brak roli ROLE_USER w bazie"));
            roles.add(userRole);
        } else {
            for (String r : requestedRoles) {
                // Zakładamy, że klient podaje pełne nazwy ról, np. "ROLE_ADMIN"
                String roleName = r;
                Role role = roleRepository.findByName(roleName)
                        .orElseThrow(() -> new RuntimeException("Rola nie istnieje: " + roleName));
                roles.add(role);
            }
        }

        newUser.setRoles(roles);

        // 4. Zapisujemy użytkownika
        User savedUser = userRepository.save(newUser);
        return mapToDto(savedUser);
    }

    /**
     * Utworzenie użytkownika na podstawie gotowego UserDto.
     * Sprawdza, czy email nie istnieje. Mapuje DTO → encję i zapisuje.
     */
    @Override
    public UserDto create(UserDto dto) {
        if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new RuntimeException("Użytkownik o tym emailu już istnieje: " + dto.getEmail());
        }
        User user = mapToEntity(dto);
        User saved = userRepository.save(user);
        return mapToDto(saved);
    }

    /**
     * Pobranie użytkownika po ID. Jeśli nie ma → rzuca RuntimeException("Użytkownik nie znaleziony: id=<id>").
     */
    @Override
    public UserDto findById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Użytkownik nie znaleziony: id=" + id));
        return mapToDto(user);
    }

    /**
     * Aktualizacja istniejącego użytkownika (po ID).
     * Jeśli użytkownik nie istnieje → rzuca RuntimeException("Użytkownik nie znaleziony: id=<id>").
     * Jeśli którakolwiek z podanych ról nie istnieje → rzuca RuntimeException("Rola nie istnieje: <rolename>").
     */
    @Override
    public UserDto update(Long id, UserDto dto) {
        User existing = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Użytkownik nie znaleziony: id=" + id));

        existing.setEmail(dto.getEmail());
        if (dto.getPassword() != null && !dto.getPassword().isEmpty()) {
            existing.setPassword(passwordEncoder.encode(dto.getPassword()));
        }
        existing.setEnabled(dto.isEnabled());

        Set<Role> roles = dto.getRoles().stream()
                .map(roleName -> roleRepository.findByName(roleName)
                        .orElseThrow(() -> new RuntimeException("Rola nie istnieje: " + roleName)))
                .collect(Collectors.toSet());
        existing.setRoles(roles);

        User updated = userRepository.save(existing);
        return mapToDto(updated);
    }

    /**
     * Usunięcie użytkownika po ID. Jeśli użytkownika nie ma → rzuca RuntimeException("Użytkownik nie znaleziony: id=<id>").
     */
    @Override
    public void delete(Long id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("Użytkownik nie znaleziony: id=" + id);
        }
        userRepository.deleteById(id);
    }

    /**
     * Pobranie użytkownika po emailu. Jeśli nie istnieje → rzuca RuntimeException("Użytkownik nie znaleziony: email=<email>").
     */
    @Override
    public UserDto findByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Użytkownik nie znaleziony: email=" + email));
        return mapToDto(user);
    }

    /**
     * Pobiera wszystkich użytkowników z bazy i zwraca listę UserDto.
     */
    @Override
    public List<UserDto> findAll() {
        return userRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }
}
