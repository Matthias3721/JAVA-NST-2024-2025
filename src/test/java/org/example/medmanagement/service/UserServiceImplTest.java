package org.example.medmanagement.service;

import org.example.medmanagement.dto.RegisterRequest;
import org.example.medmanagement.dto.UserDto;
import org.example.medmanagement.model.Role;
import org.example.medmanagement.model.User;
import org.example.medmanagement.repository.RoleRepository;
import org.example.medmanagement.repository.UserRepository;
import org.example.medmanagement.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private Role userRole;
    private Role adminRole;
    private User exampleUser;

    @BeforeEach
    void setUp() {
        // Utwórzmy role (bez setId)
        userRole = new Role();
        userRole.setName("ROLE_USER");

        adminRole = new Role();
        adminRole.setName("ROLE_ADMIN");

        // Przygotujmy przykładowego użytkownika
        exampleUser = new User();
        exampleUser.setId(10L);
        exampleUser.setEmail("existing@example.com");
        exampleUser.setPassword("encodedpwd");
        exampleUser.setEnabled(true);
        exampleUser.setRoles(new HashSet<>(Collections.singletonList(userRole)));
    }

    // -------------------- register(...) --------------------

    @Test
    void register_ShouldThrow_WhenEmailExists() {
        // Arrange: email już istnieje
        RegisterRequest req = new RegisterRequest("existing@example.com", "pass", Set.of("ROLE_USER"));
        when(userRepository.findByEmail("existing@example.com")).thenReturn(Optional.of(exampleUser));

        // Act & Assert
        RuntimeException ex = assertThrows(RuntimeException.class, () -> userService.register(req));
        assertTrue(ex.getMessage().contains("Użytkownik o tym emailu już istnieje"));
    }

    @Test
    void register_ShouldThrow_WhenRoleNotExist() {
        // Arrange: email wolny, ale podana rola nie istnieje
        RegisterRequest req = new RegisterRequest("new@example.com", "pass", Set.of("ROLE_UNKNOWN"));
        when(userRepository.findByEmail("new@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("enc");
        when(roleRepository.findByName("ROLE_UNKNOWN")).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException ex = assertThrows(RuntimeException.class, () -> userService.register(req));
        assertTrue(ex.getMessage().contains("Rola nie istnieje: ROLE_UNKNOWN"));
    }

    @Test
    void register_ShouldCreateUser_WhenRolesExist() {
        // Arrange: email wolny, obie role istnieją
        RegisterRequest req = new RegisterRequest("new@example.com", "rawpass", Set.of("ROLE_USER", "ROLE_ADMIN"));
        when(userRepository.findByEmail("new@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("rawpass")).thenReturn("encodedpass");
        when(roleRepository.findByName("ROLE_USER")).thenReturn(Optional.of(userRole));
        when(roleRepository.findByName("ROLE_ADMIN")).thenReturn(Optional.of(adminRole));

        // Po zapisaniu do bazy (save) repo zwróci użytkownika z ID=20 i przypisanymi rolami
        User savedUser = new User();
        savedUser.setId(20L);
        savedUser.setEmail("new@example.com");
        savedUser.setPassword("encodedpass");
        savedUser.setEnabled(true);
        savedUser.setRoles(new HashSet<>(Set.of(userRole, adminRole)));
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        // Act
        UserDto dto = userService.register(req);

        // Assert: w zwróconym DTO mamy poprawne pola
        assertNotNull(dto);
        assertEquals(20L, dto.getId());
        assertEquals("new@example.com", dto.getEmail());
        assertTrue(dto.isEnabled());
        assertTrue(dto.getRoles().contains("ROLE_USER"));
        assertTrue(dto.getRoles().contains("ROLE_ADMIN"));

        // Zweryfikujmy, jaki obiekt przekazano do save(...)
        ArgumentCaptor<User> capt = ArgumentCaptor.forClass(User.class);
        verify(userRepository, times(1)).save(capt.capture());
        User passed = capt.getValue();
        assertEquals("new@example.com", passed.getEmail());
        assertEquals("encodedpass", passed.getPassword());
        assertTrue(passed.isEnabled());
        Set<String> roleNames = passed.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toSet());
        assertTrue(roleNames.contains("ROLE_USER"));
        assertTrue(roleNames.contains("ROLE_ADMIN"));
    }

    // -------------------- create(...) --------------------

    @Test
    void create_ShouldThrow_WhenEmailExists() {
        // Arrange: DTO z emailem, który już jest w bazie
        UserDto dto = new UserDto();
        dto.setEmail("existing@example.com");
        when(userRepository.findByEmail("existing@example.com")).thenReturn(Optional.of(exampleUser));

        // Act & Assert
        RuntimeException ex = assertThrows(RuntimeException.class, () -> userService.create(dto));
        assertTrue(ex.getMessage().contains("Użytkownik o tym emailu już istnieje"));
    }

    @Test
    void create_ShouldMapAndSave_WhenOK() {
        // Arrange: DTO z nowym emailem
        UserDto dto = new UserDto();
        dto.setEmail("fresh@example.com");
        dto.setPassword("rawpass");
        dto.setEnabled(true);
        dto.setRoles(Set.of("ROLE_USER"));

        when(userRepository.findByEmail("fresh@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("rawpass")).thenReturn("ep");
        when(roleRepository.findByName("ROLE_USER")).thenReturn(Optional.of(userRole));

        // Po save(...) zwracamy użytkownika z ID=30
        User saved = new User();
        saved.setId(30L);
        saved.setEmail("fresh@example.com");
        saved.setPassword("ep");
        saved.setEnabled(true);
        saved.setRoles(Set.of(userRole));
        when(userRepository.save(any(User.class))).thenReturn(saved);

        // Act
        UserDto result = userService.create(dto);

        // Assert
        assertNotNull(result);
        assertEquals(30L, result.getId());
        assertEquals("fresh@example.com", result.getEmail());
        assertTrue(result.isEnabled());
        assertTrue(result.getRoles().contains("ROLE_USER"));

        // Weryfikacja obiektu w save(...)
        ArgumentCaptor<User> capt = ArgumentCaptor.forClass(User.class);
        verify(userRepository, times(1)).save(capt.capture());
        User passed = capt.getValue();
        assertEquals("fresh@example.com", passed.getEmail());
        assertEquals("ep", passed.getPassword());
        assertTrue(passed.isEnabled());
    }

    // -------------------- findById(...) --------------------

    @Test
    void findById_ShouldThrow_WhenNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());
        RuntimeException ex = assertThrows(RuntimeException.class, () -> userService.findById(99L));
        assertTrue(ex.getMessage().contains("Użytkownik nie znaleziony: id=99"));
    }

    @Test
    void findById_ShouldReturnDto_WhenExists() {
        when(userRepository.findById(10L)).thenReturn(Optional.of(exampleUser));
        UserDto dto = userService.findById(10L);

        assertNotNull(dto);
        assertEquals(10L, dto.getId());
        assertEquals("existing@example.com", dto.getEmail());
        assertTrue(dto.isEnabled());
        assertTrue(dto.getRoles().contains("ROLE_USER"));
    }

    // -------------------- update(...) --------------------

    @Test
    void update_ShouldThrow_WhenNotFound() {
        when(userRepository.findById(50L)).thenReturn(Optional.empty());
        UserDto dto = new UserDto();
        dto.setEmail("nouser@example.com");
        dto.setPassword("pwd");
        dto.setEnabled(false);
        dto.setRoles(Set.of("ROLE_USER"));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> userService.update(50L, dto));
        assertTrue(ex.getMessage().contains("Użytkownik nie znaleziony: id=50"));
    }

    @Test
    void update_ShouldThrow_WhenRoleNotExist() {
        // findById zwraca istniejącego usera
        when(userRepository.findById(10L)).thenReturn(Optional.of(exampleUser));

        // DTO z nieznaną rolą
        UserDto dto = new UserDto();
        dto.setEmail("existing@example.com");
        dto.setPassword("newpwd");
        dto.setEnabled(true);
        dto.setRoles(Set.of("ROLE_UNKNOWN"));
        when(roleRepository.findByName("ROLE_UNKNOWN")).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> userService.update(10L, dto));
        assertTrue(ex.getMessage().contains("Rola nie istnieje: ROLE_UNKNOWN"));
    }

    @Test
    void update_ShouldModifyFields_WhenOK() {
        when(userRepository.findById(10L)).thenReturn(Optional.of(exampleUser));

        // DTO z danymi do zmiany
        UserDto dto = new UserDto();
        dto.setEmail("updated@example.com");
        dto.setPassword("newpwd");
        dto.setEnabled(false);
        dto.setRoles(Set.of("ROLE_ADMIN"));

        when(passwordEncoder.encode("newpwd")).thenReturn("encnew");
        when(roleRepository.findByName("ROLE_ADMIN")).thenReturn(Optional.of(adminRole));

        User updated = new User();
        updated.setId(10L);
        updated.setEmail("updated@example.com");
        updated.setPassword("encnew");
        updated.setEnabled(false);
        updated.setRoles(Set.of(adminRole));
        when(userRepository.save(any(User.class))).thenReturn(updated);

        // Act
        UserDto result = userService.update(10L, dto);

        // Assert
        assertNotNull(result);
        assertEquals(10L, result.getId());
        assertEquals("updated@example.com", result.getEmail());
        assertFalse(result.isEnabled());
        assertTrue(result.getRoles().contains("ROLE_ADMIN"));

        // Sprawdź obiekt przekazany do save(...)
        ArgumentCaptor<User> capt = ArgumentCaptor.forClass(User.class);
        verify(userRepository, times(1)).save(capt.capture());
        User passed = capt.getValue();
        assertEquals("updated@example.com", passed.getEmail());
        assertEquals("encnew", passed.getPassword());
        assertFalse(passed.isEnabled());
    }

    // -------------------- delete(...) --------------------

    @Test
    void delete_ShouldThrow_WhenNotExist() {
        when(userRepository.existsById(99L)).thenReturn(false);
        RuntimeException ex = assertThrows(RuntimeException.class, () -> userService.delete(99L));
        assertTrue(ex.getMessage().contains("Użytkownik nie znaleziony: id=99"));
    }

    @Test
    void delete_ShouldCallRepository_WhenExists() {
        when(userRepository.existsById(10L)).thenReturn(true);
        userService.delete(10L);
        verify(userRepository, times(1)).deleteById(10L);
    }

    // -------------------- findByEmail(...) --------------------

    @Test
    void findByEmail_ShouldThrow_WhenNotFound() {
        when(userRepository.findByEmail("no@example.com")).thenReturn(Optional.empty());
        RuntimeException ex = assertThrows(RuntimeException.class, () -> userService.findByEmail("no@example.com"));
        assertTrue(ex.getMessage().contains("Użytkownik nie znaleziony: email=no@example.com"));
    }

    @Test
    void findByEmail_ShouldReturnDto_WhenExists() {
        when(userRepository.findByEmail("existing@example.com")).thenReturn(Optional.of(exampleUser));
        UserDto dto = userService.findByEmail("existing@example.com");

        assertNotNull(dto);
        assertEquals(10L, dto.getId());
        assertEquals("existing@example.com", dto.getEmail());
    }

    // -------------------- findAll() --------------------

    @Test
    void findAll_ShouldReturnListOfUserDtos() {
        User user2 = new User();
        user2.setId(11L);
        user2.setEmail("u2@example.com");
        user2.setPassword("pwd");
        user2.setEnabled(true);
        user2.setRoles(Set.of(userRole));

        when(userRepository.findAll()).thenReturn(Arrays.asList(exampleUser, user2));

        List<UserDto> list = userService.findAll();
        assertEquals(2, list.size());
        Set<String> emails = list.stream().map(UserDto::getEmail).collect(Collectors.toSet());
        assertTrue(emails.contains("existing@example.com"));
        assertTrue(emails.contains("u2@example.com"));
    }
}
