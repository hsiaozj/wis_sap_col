package com.bjev.apps.security;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.codec.binary.Hex;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class MessageEncoder {
	
	private final static MessageEncoder encoder = new MessageEncoder();
	private static Cipher cipher;
	private static SecretKey generateKey;

	static {
		try {
			KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
			SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
	        random.setSeed("0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ".getBytes());
			keyGenerator.init(128,random);//size
			SecretKey secretKey = keyGenerator.generateKey();
			byte[] keyBytes = secretKey.getEncoded();
			generateKey = new SecretKeySpec(keyBytes, "AES");
			cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
		}catch(Exception ex) {
			ex.printStackTrace();
		}
	}
	public static MessageEncoder getInstance() {
		return encoder;
	}

	public String aesEncode(String src){
		try{
			cipher.init(Cipher.ENCRYPT_MODE, generateKey);
			byte[] resultBytes = cipher.doFinal(src.getBytes());
			return Hex.encodeHexString(resultBytes);
		}catch (Exception e){
			e.printStackTrace();
		}
		return null;
	}
	
	public String aesDecode(String src){
		try{
			cipher.init(Cipher.DECRYPT_MODE, generateKey);
			byte[] result = Hex.decodeHex(src.toCharArray());
			return new String(cipher.doFinal(result));
		}catch(Exception e){
				e.printStackTrace();
		}
		return null;
	}

	
	public String shaEncode(String src){
		try{
			MessageDigest md = MessageDigest.getInstance("SHA-1");
			md.update(src.getBytes());
			return Hex.encodeHexString(md.digest());
		}catch (NoSuchAlgorithmException e){
			e.printStackTrace();
		}
		return null;
	}
 
	public String shaDecode(String src){
		throw new RuntimeException("SHA no decode");
	}
	
}

