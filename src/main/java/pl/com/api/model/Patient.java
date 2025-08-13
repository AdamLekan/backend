package pl.com.api.model;

import jakarta.persistence.*;


import java.security.SecureRandom;

@Entity
@Table(name = "patients")
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String surname;

    @Column(nullable = false, unique = true)
    private String pesel;

    @Column(nullable = false)
    private String seed;

    @Column(nullable = false)
    private Long firstTick;

    public Patient(String name, String surname, String pesel) {
        this.name = name;
        this.surname = surname;
        this.pesel = pesel;
        this.seed = generateSeed();
        this.firstTick = 0L;
    }

    public Patient() {
        // Domyślny konstruktor wymagany przez JPA
    }

    private String generateSeed() {
        StringBuilder sb = new StringBuilder(55);
        SecureRandom rnd = new SecureRandom();  // Użyj SecureRandom dla lepszego bezpieczeństwa
        for (int i = 0; i < 55; i++) {
            char c = (char)('a' + rnd.nextInt(26));  // Wybór z liter a-z
            sb.append(c);
        }
        return sb.toString();
    }

    // Gettery i Settery
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getPesel() {
        return pesel;
    }

    public void setPesel(String pesel) {
        this.pesel = pesel;
    }

    public String getSeed() {
        return seed;
    }

    public void setSeed(String seed) {
        this.seed = seed;
    }
    public Long getFirstTick() {
        return firstTick;
    }

    public void setFirstTick(Long firstTick) {
        this.firstTick = firstTick;
    }


}
