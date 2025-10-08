package com.kaii.dentix.global.common.util;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static javax.crypto.Cipher.SECRET_KEY;

@Slf4j
@Component
public class CryptoUtil {

    @Value("${encrypt.secret-key:}")
    private String secretKey; // 16자 or 32자 (AES-256 기준)
private static final String ALGORITHM = "AES/CBC/PKCS5Padding";
    private static final String SECRET_KEY = "DentixAesSecretKey16";
    private static final String IV = "DentixCryptoIV16";

 // 반드시 16자
    private boolean keyValid = false; // 키 검증 상태 플래그

    @PostConstruct
    public void validateKey() {
        if (secretKey == null || secretKey.isBlank()) {
            log.warn("⚠️ [CryptoUtil] encrypt.secret-key 설정이 없습니다. 암호화 기능이 비활성화됩니다.");
            keyValid = false;
        } else if (secretKey.length() != 16 && secretKey.length() != 32) {
            log.warn("⚠️ [CryptoUtil] secret-key 길이가 유효하지 않습니다. (길이: {}) → 16 또는 32자 필요", secretKey.length());
            keyValid = false;
        } else {
            keyValid = true;
            log.info("✅ [CryptoUtil] secret-key 로드 완료 (length={})", secretKey.length());
        }
    }

    public String encrypt(String plainText) {
        if (!keyValid) {
            log.warn("⚠️ [CryptoUtil] 암호화 시도 중, 유효하지 않은 secret-key");
            return plainText; // 실패 시 평문 반환 (서버 중단 방지)
        }
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            SecretKeySpec keySpec = new SecretKeySpec(secretKey.getBytes(), "AES");
            IvParameterSpec ivSpec = new IvParameterSpec(IV.getBytes());
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
            byte[] encrypted = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception e) {
            log.error("❌ [CryptoUtil] 암호화 실패", e);
            return plainText;
        }
    }

//    public String decrypt(String cipherText) {
//        if (!keyValid) {
//            log.info(cipherText);
//            log.warn("⚠️ [CryptoUtil] 복호화 시도 중, 유효하지 않은 secret-key");
//            return cipherText; // 실패 시 원문 그대로 반환
//        }
//        try {
//            Cipher cipher = Cipher.getInstance(ALGORITHM);
//            SecretKeySpec keySpec = new SecretKeySpec(secretKey.getBytes(), "AES");
//            IvParameterSpec ivSpec = new IvParameterSpec(IV.getBytes());
//            cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
//            byte[] decoded = Base64.getDecoder().decode(cipherText);
//            return new String(cipher.doFinal(decoded), StandardCharsets.UTF_8);
//        } catch (Exception e) {
//            log.error("❌ [CryptoUtil] 복호화 실패", e);
//            return cipherText;
//        }
//    }
public static String decrypt(String cipherText) {
    try {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        // 🔥 Python에서 [:16] 사용했으므로 동일하게 16자로 잘라야 함
        SecretKeySpec keySpec = new SecretKeySpec(
                SECRET_KEY.substring(0, 16).getBytes(StandardCharsets.UTF_8),
                "AES"
        );
        IvParameterSpec ivSpec = new IvParameterSpec(IV.getBytes(StandardCharsets.UTF_8));
        cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
        byte[] decoded = Base64.getDecoder().decode(cipherText.trim());
        return new String(cipher.doFinal(decoded), StandardCharsets.UTF_8);
    } catch (Exception e) {
        e.printStackTrace();
        return "[복호화 실패]";
    }
}
}