package pl.com.api.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "\"user\"")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

    @Column(name = "national_id", nullable = false, unique = true, length = 11)
    private String nationalId; // zamiast "pesel"

    @Column(name = "user_name", nullable = false, unique = true, length = 50)
    private String username;

    @Column(name = "password",nullable = false, columnDefinition = "TEXT")
    private String password;

    @Column(name = "wallet_address_hash", nullable = false, columnDefinition = "TEXT")
    private String walletAddressHash;

    public User(String firstName, String lastName, String nationalId, String username, String password, String walletAddressHash) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.nationalId = nationalId;
        this.username = username;
        this.password = password;
        this.walletAddressHash = walletAddressHash;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getNationalId() {
        return nationalId;
    }

    public void setNationalId(String nationalId) {
        this.nationalId = nationalId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getWalletAddressHash() {
        return walletAddressHash;
    }

    public void setWalletAddressHash(String walletAddressHash) {
        this.walletAddressHash = walletAddressHash;
    }
}