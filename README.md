MedManagement
Aplikacja MedManagement to system do zarządzania lekami, receptami i przypomnieniami dla pacjentów i administratorów apteki. Projekt wykorzystuje Spring Boot, Spring Security (JWT), Hibernate/JPA, PostgreSQL (Flyway), Docker oraz Maven.

Technologie
Java 17

Spring Boot 3.x (kontrolery REST, konfiguracja aplikacji)
Spring Security (JWT) – autoryzacja i uwierzytelnianie użytkowników
Hibernate / JPA – mapowanie obiektowo-relacyjne
PostgreSQL – baza danych (w kontenerze Docker)
Flyway – migracje schematu bazy danych (src/main/resources/db/migration)
Docker & Docker Compose – konteneryzacja aplikacji i bazy
Maven – zarządzanie zależnościami i procesem build
JUnit + JaCoCo – testy jednostkowe i raport pokrycia kodu (≥ 80 %)
Springdoc OpenAPI (Swagger UI) – automatyczna dokumentacja REST API

Uruchomienie
Sklonuj repozytorium i przejdź na branch projekt-mateusz:
bash
Kopiuj
Edytuj
git clone https://github.com/Matthias3721/JAVA-NST-2024-2025.git
cd JAVA-NST-2024-2025/Mateusz-Wiecek
git checkout projekt-mateusz
Uruchom aplikację za pomocą Dockera:

docker-compose up --build
Kontener db (PostgreSQL) wystartuje na porcie 5432.

Kontener app (Spring Boot) wystartuje na porcie 8080.

Po kilku sekundach aplikacja będzie dostępna pod:

http://localhost:8080
Dokumentacja REST API (Swagger UI):

http://localhost:8080/swagger-ui/index.html
/Mateusz-Wiecek
│
├─ Dockerfile
├─ docker-compose.yml
├─ pom.xml
├─ .gitignore
├─ README.md
│
├─ src
│  ├─ main
│  │  ├─ java/org/example/medmanagement
│  │  │   ├─ config         ← SecurityConfig, OpenApiConfig
│  │  │   ├─ controller     ← REST-kontrolery (Auth, Medications, Prescriptions, Reminders, Stock)
│  │  │   ├─ model          ← encje JPA (User, Role, Medication, Prescription, Reminder, Stock)
│  │  │   ├─ repository     ← interfejsy JpaRepository
│  │  │   ├─ security       ← JwtAuthenticationFilter, JwtUtils, CustomUserDetailsService
│  │  │   └─ service        ← interfejsy i implementacje serwisów (MedicationServiceImpl, PrescriptionServiceImpl itp.)
│  │  │
│  │  └─ resources
│  │      ├─ application.properties  ← konfiguracja Spring Boot i PostgreSQL
│  │      └─ db
│  │          └─ migration            ← pliki Flyway: V1__…, V2__…, V3__…
│  │
│  └─ test
│      └─ java/org/example/medmanagement  ← testy JUnit i JaCoCo
│
└─ docs
   ├─ erd
   │   └─ medmanagement-ERD.png      ← diagram bazy danych
   │
   └─ screenshots
       └─ jacoco-report.png          ← zrzut raportu pokrycia kodu JaCoCo

Diagram ERD bazy danych
Schemat tabel i relacji w bazie danych (wygenerowany z dbdiagram.io):


Główne funkcjonalności
Rejestracja i logowanie

POST /api/auth/register – rejestruje nowego użytkownika (domyślnie z rolą ROLE_USER).

POST /api/auth/login – zwraca token JWT do autoryzacji kolejnych żądań.

Role i uprawnienia (RBAC)

Role: ROLE_USER i ROLE_ADMIN.

ROLE_USER może:

przeglądać listę leków (GET /api/medications),

składać recepty (POST /api/prescriptions),

ustawiać przypomnienia (POST /api/reminders).

ROLE_ADMIN dodatkowo:

zarządzanie magazynem (GET/PUT /api/stock),

dodawanie / edycja / usunięcie leków (POST/PUT/DELETE /api/medications),

przeglądanie wszystkich recept i przypomnień.

Zarządzanie lekami (/api/medications)

GET /api/medications – lista leków (USER i ADMIN).

POST /api/medications – dodawanie nowego leku (ADMIN).

PUT /api/medications/{id} – edytowanie leku (ADMIN).

DELETE /api/medications/{id} – usuwanie leku (ADMIN).

Recepty (/api/prescriptions)

GET /api/prescriptions – USER widzi własne recepty, ADMIN widzi wszystkie.

POST /api/prescriptions – tworzenie recepty (USER).

GET /api/prescriptions/{id} – szczegóły recepty.

Przypomnienia (/api/reminders)

GET /api/reminders – USER widzi własne przypomnienia, ADMIN widzi wszystkie.

POST /api/reminders – dodawanie przypomnienia do recepty.

Wysyłanie powiadomień (e-mail / SMS) za pomocą wzorca Strategy (NotificationService → EmailNotificationService / SmsNotificationService).

Magazyn (/api/stock)

GET /api/stock – stan magazynowy leków (ADMIN).

PUT /api/stock/{medicationId} – aktualizacja ilości (ADMIN).

Migracje bazy (Flyway)
W katalogu src/main/resources/db/migration znajdują się pliki SQL:

V1__create_roles_users.sql

V2__create_medications_prescriptions.sql

V3__create_reminders_stock.sql

Przy starcie aplikacji Flyway automatycznie utworzy (lub zaktualizuje) schemat w Postgresie.

Testy jednostkowe i pokrycie JaCoCo

Aby uruchomić testy i wygenerować raport pokrycia:

mvn clean test jacoco:report
Raport będzie dostępny w target/site/jacoco/index.html. 

Dokumentacja REST API (Swagger UI)
Po uruchomieniu projektu dostępna jest dokumentacja:

http://localhost:8080/swagger-ui/index.html
