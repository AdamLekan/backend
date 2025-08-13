package pl.com.api.repository;

import pl.com.api.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
    boolean existsByUsername(String username);
    boolean existsByWalletAddressHash(String walletAddressHash);
}
