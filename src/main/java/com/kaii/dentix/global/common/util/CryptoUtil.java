package com.kaii.dentix.global.common.util;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class CryptoUtil {

    // ✅ application.yml 같은 곳에 설정값으로 키 관리 권장
    private static final String SECRET_KEY = "1234567890123456"; // 16자리 AES Key

    public static String encrypt(String strToEncrypt) {
        try {
            Cipher cipher = Cipher.getInstance("AES");
            SecretKeySpec secretKey = new SecretKeySpec(SECRET_KEY.getBytes(), "AES");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            return Base64.getEncoder().encodeToString(cipher.doFinal(strToEncrypt.getBytes()));
        } catch (Exception e) {
            throw new RuntimeException("암호화 실패", e);
        }
    }

    public static String decrypt(String strToDecrypt) {
        try {
            Cipher cipher = Cipher.getInstance("AES");
            SecretKeySpec secretKey = new SecretKeySpec(SECRET_KEY.getBytes(), "AES");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            return new String(cipher.doFinal(Base64.getDecoder().decode(strToDecrypt)));
        } catch (Exception e) {
            throw new RuntimeException("복호화 실패", e);
        }
    }
}
