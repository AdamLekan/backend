package pl.com.api.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import pl.com.api.dto.UserDto;
import pl.com.api.model.User;
//import pl.com.api.model.UserPrincipal;
import pl.com.api.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User createUser(
            String firstName,
            String lastName,
            String nationalId,
            String username,
            String plainPassword,
            String walletAddressPlain
    ) {
        String hashedPassword = passwordEncoder.encode(plainPassword);
        String hashedWalletAddress = hashWalletAddress(walletAddressPlain);

        User user = new User(
                firstName,
                lastName,
                nationalId,
                username,
                hashedPassword,
                hashedWalletAddress
        );

        return userRepository.save(user);
    }

    private String hashWalletAddress(String walletAddress) {
        // implementacja hashowania adresu portfela
        return walletAddress; // tymczasowo zwracamy bez hashowania
    }

//    public UserDetails loadUserByUsername(String username) {
//        return null;
//    }
}