package pl.com.api.service;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;

public class QubicTransactionSigner {

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    public static byte[] sign(byte[] privateKeyEncoded, byte[] payload) throws Exception {
        PrivateKey privateKey = KeyFactory.getInstance("Ed25519", "BC")
                .generatePrivate(new PKCS8EncodedKeySpec(privateKeyEncoded));

        Signature sig = Signature.getInstance("Ed25519", "BC");
        sig.initSign(privateKey);
        sig.update(payload);
        return sig.sign();
    }
}
