package pl.com.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.com.api.dto.QubicKeyPair;

import java.util.Optional;
import java.util.UUID;

public interface QubicKeyPairRepository extends JpaRepository<QubicKeyPair, UUID> {
    Optional<QubicKeyPair> findByAddressId(String addressId);
}
