package com.mento.common.util;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.HexFormat;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.mento.common.error.ErrorCode;
import com.mento.common.error.exception.CryptoException;

@Component
public class AesUtils {

	private static final String ALGORITHM = "AES/GCM/NoPadding";
	private static final int TAG_BIT_LENGTH = 128;
	private static final int IV_BYTE_LENGTH = 12;

	private final byte[] secretKeyBytes;

	private AesUtils(@Value("${encryption.secret-key}") String hexKey) {
		this.secretKeyBytes = HexFormat.of().parseHex(hexKey);
	}

	public String decrypt(String cipherText) {
		try {
			byte[] decoded = Base64.getDecoder().decode(cipherText);

			GCMParameterSpec paramSpec = new GCMParameterSpec(TAG_BIT_LENGTH, decoded, 0, IV_BYTE_LENGTH);
			SecretKeySpec keySpec = new SecretKeySpec(secretKeyBytes, "AES");

			Cipher cipher = Cipher.getInstance(ALGORITHM);
			cipher.init(Cipher.DECRYPT_MODE, keySpec, paramSpec);

			byte[] plainBytes = cipher.doFinal(decoded, IV_BYTE_LENGTH, decoded.length - IV_BYTE_LENGTH);

			return new String(plainBytes, StandardCharsets.UTF_8);
		} catch (Exception e) {
			throw new CryptoException(ErrorCode.DECRYPT_ERROR);
		}
	}

	public String encrypt(String plainText) {
		try {
			byte[] iv = new byte[IV_BYTE_LENGTH];
			new SecureRandom().nextBytes(iv);

			GCMParameterSpec paramSpec = new GCMParameterSpec(TAG_BIT_LENGTH, iv);
			SecretKeySpec keySpec = new SecretKeySpec(secretKeyBytes, "AES");

			Cipher cipher = Cipher.getInstance(ALGORITHM);
			cipher.init(Cipher.ENCRYPT_MODE, keySpec, paramSpec);

			byte[] encryptedData = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));

			ByteBuffer byteBuffer = ByteBuffer.allocate(iv.length + encryptedData.length);
			byteBuffer.put(iv);
			byteBuffer.put(encryptedData);

			return Base64.getEncoder().encodeToString(byteBuffer.array());

		} catch (Exception e) {
			throw new CryptoException(ErrorCode.ENCRYPT_ERROR);
		}
	}
}
