package pl.com.api.service;

import org.springframework.stereotype.Service;
import org.bouncycastle.crypto.params.Ed25519PrivateKeyParameters;

import pl.com.api.util.Base58;
import pl.com.api.model.QubicWallet;

import java.security.SecureRandom;

@Service
public class WalletService {

    public QubicWallet generateWallet() {
        SecureRandom random = new SecureRandom();
        byte[] seed = new byte[32];
        random.nextBytes(seed);
        System.out.println("Seed: " + Base58.encode(seed));
        Ed25519PrivateKeyParameters privateKey = new Ed25519PrivateKeyParameters(seed, 0);
        byte[] publicKey = privateKey.generatePublicKey().getEncoded();
        byte[] secretKey = privateKey.getEncoded();

        String address = "Q" + Base58.encode(publicKey);
        System.out.println("address: " + address);

        return new QubicWallet(address, Base58.encode(publicKey), Base58.encode(secretKey));
    }
}
