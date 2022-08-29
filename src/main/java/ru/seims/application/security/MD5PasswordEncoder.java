package ru.seims.application.security;

import org.apache.tomcat.util.security.MD5Encoder;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5PasswordEncoder implements PasswordEncoder {
    private static AnnotationConfigApplicationContext md5PasswordEncoderContext;

    public static MD5PasswordEncoder getInstance() {
        if(md5PasswordEncoderContext == null)
            md5PasswordEncoderContext = new AnnotationConfigApplicationContext(MD5PasswordEncoder.class);
        return md5PasswordEncoderContext.getBean(MD5PasswordEncoder.class);
    }

    @Override
    public String encode(CharSequence rawPassword) {
        if(rawPassword.equals("userNotFoundPassword"))
            return "";
        try {

            // Static getInstance method is called with hashing MD5
            MessageDigest md = MessageDigest.getInstance("MD5");

            // digest() method is called to calculate message digest
            //  of an input digest() return array of byte
            byte[] messageDigest = md.digest(rawPassword.toString().getBytes(StandardCharsets.UTF_8));

            // Convert byte array into signum representation
            BigInteger no = new BigInteger(1, messageDigest);

            // Convert message digest into hex value
            String hashtext = no.toString(16);
            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }
            return hashtext;
        }

        // For specifying wrong message digest algorithms
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        String encodedRawPassword = encode(rawPassword);
        return encodedRawPassword.equals(encodedPassword);
    }
}
