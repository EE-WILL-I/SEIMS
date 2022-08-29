package ru.seims.application.security;

import com.auth0.jwt.interfaces.ECDSAKeyProvider;

import java.security.*;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.security.spec.ECGenParameterSpec;

public class JwtKeyProvider implements ECDSAKeyProvider {
    private KeyPair keypair;

    public JwtKeyProvider() {
        try {
            generate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void generate() throws NoSuchAlgorithmException, InvalidAlgorithmParameterException {
        ECGenParameterSpec ecSpec = new ECGenParameterSpec("secp256k1");
        KeyPairGenerator g = KeyPairGenerator.getInstance("EC");
        g.initialize(ecSpec, new SecureRandom());
        keypair = g.generateKeyPair();
    }
    @Override
    public ECPublicKey getPublicKeyById(String s) {
        return (ECPublicKey) keypair.getPublic();
    }

    @Override
    public ECPrivateKey getPrivateKey() {
        return (ECPrivateKey) keypair.getPrivate();
    }

    @Override
    public String getPrivateKeyId() {
        return null;
    }
}
