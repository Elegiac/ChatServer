package stu.demo.utils;

import java.nio.charset.Charset;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

import org.apache.commons.codec.binary.Base64;

public class AESUtil {

	private static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");

	public static String encrypt2Base64String(String content, String key,
			String iv) {
		return Base64.encodeBase64String(encrypt(
				content.getBytes(DEFAULT_CHARSET),
				key.getBytes(DEFAULT_CHARSET), iv.getBytes(DEFAULT_CHARSET)));
	}

	public static String decrypt2String(String content, String key, String iv) {
		return new String(decrypt(Base64.decodeBase64(content),
				key.getBytes(DEFAULT_CHARSET), iv.getBytes(DEFAULT_CHARSET)),
				DEFAULT_CHARSET);
	}

	public static byte[] encrypt(byte[] content, byte[] key, byte[] iv) {
		try {
			KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
			keyGenerator.init(128, new SecureRandom(key));
			SecretKey secretKey = keyGenerator.generateKey();
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			cipher.init(Cipher.ENCRYPT_MODE, secretKey, new IvParameterSpec(iv));
			byte[] result = cipher.doFinal(content);
			return result;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static byte[] decrypt(byte[] content, byte[] key, byte[] iv) {
		try {
			KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
			keyGenerator.init(128, new SecureRandom(key));
			SecretKey secretKey = keyGenerator.generateKey();
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			cipher.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(iv));
			byte[] result = cipher.doFinal(content);
			return result;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void main(String[] args) {
		String content = "orjgwjfnejgesadwefew1212121ngr哈哈劳动法efewf；了热播突然被他人客服vhoirh 唱片顶顶顶南方人看过我放假哦人夫人为了繁荣我让老公不nsa觉得";
		String key = "aaaaaaaa";
		String iv = "abcdefghijklmnop";

		System.out.println("原字符串:"+content);
		String result1 = encrypt2Base64String(content, key, iv);
		System.out.println("加密:"+result1);
		String result2 = decrypt2String(result1, key, iv);
		System.out.println("解密:"+result2);
	}
}
