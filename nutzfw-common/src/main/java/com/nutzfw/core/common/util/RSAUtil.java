package com.nutzfw.core.common.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Hex;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.Cipher;
import java.security.*;

/**
 * @author huchuc@vip.qq.com
 * @date: 2019/5/10
 */
@Slf4j
public class RSAUtil {

    static final String ALGORITHOM = "RSA";

    static final Provider DEFAULT_PROVIDER = new BouncyCastleProvider();

    /**
     * 生成并返回RSA密钥对
     */
    private static synchronized KeyPair generateKeyPair(int keySize) {
        try {
            KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance(ALGORITHOM, DEFAULT_PROVIDER);
            keyPairGen.initialize(keySize, new SecureRandom((System.currentTimeMillis() + "").getBytes()));
            return keyPairGen.generateKeyPair();
        } catch (NoSuchAlgorithmException ex) {
            log.error("生成并返回RSA密钥失败", ex);
        }
        return null;
    }

    private static PublicKey readPublicKey(String publicKeyPath) {
        return (PublicKey) SerializeUtil.deserialize(publicKeyPath);
    }

    private static PrivateKey readPrivateKey(String privateKeyPath) {
        return (PrivateKey) SerializeUtil.deserialize(privateKeyPath);
    }

    /**
     * 使用指定的公钥加密数据
     *
     * @param publicKey 给定的公钥
     * @param data      要加密的数据
     * @return 加密后的数据
     */
    public static byte[] encrypt(PublicKey publicKey, byte[] data) throws Exception {
        Cipher ci = Cipher.getInstance(ALGORITHOM, DEFAULT_PROVIDER);
        ci.init(Cipher.ENCRYPT_MODE, publicKey);
        return ci.doFinal(data);
    }


    /**
     * 使用给定的公钥加密给定的字符串
     *
     * @param publicKey 给定的公钥
     * @param plaintext 字符串
     * @return 给定字符串的密文
     */
    public static String encryptString(PublicKey publicKey, String plaintext) {
        if (publicKey == null || plaintext == null) {
            return null;
        }
        byte[] data = plaintext.getBytes();
        try {
            byte[] en_data = encrypt(publicKey, data);
            return new String(Hex.encodeHex(en_data));
        } catch (Exception ex) {
            log.error("使用给定的公钥加密给定的字符串加密失败", ex);
        }
        return null;
    }

    public static void main(String[] args) throws Exception {
        String privateKeyPath = "D:/TEMP/RSA/privateKey.key";
        String publicKeyPath = "D:/TEMP/RSA/publicKey.key";
        KeyPair keyPair = RSAUtil.generateKeyPair(4096);
        PrivateKey privateKey = keyPair.getPrivate();
        PublicKey publicKey = keyPair.getPublic();
        FileUtil.writeFileToByte(privateKeyPath, SerializeUtil.serizlize(privateKey));
        FileUtil.writeFileToByte(publicKeyPath, SerializeUtil.serizlize(publicKey));
        System.out.println("生成成功");
    }
}
