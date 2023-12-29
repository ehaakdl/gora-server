package org.gora.server.common;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AesUtils {
    // 알고리즘
    public static String algorithms = "AES/CBC/PKCS5Padding";
    // AES/CBC/PKCS5Padding -> AES, CBC operation mode, PKCS5 padding scheme 으로 초기화된
    // Cipher 객체

    // 키
    private static final String aesKey = System.getenv("AES_KEY"); // 32byte

    // 초기화 벡터
    private static final String aesIv = System.getenv("AES_IV"); // 16byte

    public static String encrypt(String plainText) {
        try {
            String result;
            // 암호화/복호화 기능이 포함된 객체 생성
            Cipher cipher = Cipher.getInstance(algorithms);

            // 키로 비밀키 생성
            SecretKeySpec keySpec = new SecretKeySpec(aesKey.getBytes(), "AES");

            // iv 로 spec 생성
            IvParameterSpec ivParamSpec = new IvParameterSpec(aesIv.getBytes());
            // 매번 다른 IV를 생성하면 같은 평문이라도 다른 암호문을 생성할 수 있다.
            // 또한 IV는 암호를 복호화할 사람에게 미리 제공되어야 하고 키와 달리 공개되어도 상관없다

            // 암호화 적용
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivParamSpec);

            // 암호화 실행
            byte[] encrypted = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8)); // ID 암호화(인코딩 설정)
            result = Base64.getEncoder().encodeToString(encrypted); // 암호화 인코딩 후 저장

            return result;
        }

        catch (Exception e) {
            log.error("암호화 중 오류 발생하였습니다. {}", CommonUtils.getStackTraceElements(e));
        }

        return "";
    }

    public static String decrypt(String encryptedText) {
        try {
            // 암호화/복호화 기능이 포함된 객체 생성
            Cipher cipher = Cipher.getInstance(algorithms);

            // 키로 비밀키 생성
            SecretKeySpec keySpec = new SecretKeySpec(aesKey.getBytes(), "AES");

            // iv 로 spec 생성
            IvParameterSpec ivParamSpec = new IvParameterSpec(aesIv.getBytes());
            // 매번 다른 IV를 생성하면 같은 평문이라도 다른 암호문을 생성할 수 있다.
            // 또한 IV는 암호를 복호화할 사람에게 미리 제공되어야 하고 키와 달리 공개되어도 상관없다

            // 암호화 적용
            cipher.init(Cipher.DECRYPT_MODE, keySpec, ivParamSpec);

            // 암호 해석
            byte[] decodedBytes = Base64.getDecoder().decode(encryptedText);
            byte[] decrypted = cipher.doFinal(decodedBytes);

            return new String(decrypted, StandardCharsets.UTF_8);
        }

        catch (Exception e) {
            log.error("복호화 중 오류 발생하였습니다. {}", CommonUtils.getStackTraceElements(e));
        }

        return "";
    }
}