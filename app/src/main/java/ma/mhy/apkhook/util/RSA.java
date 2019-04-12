package ma.mhy.apkhook.util;

/**
 * 项目名 HookAPK
 * 所在包 ma.mhy.hookapk.util
 * 作者 mahongyin
 * 时间 2019/4/7 18:07
 * 邮箱 mhy.work@qq.com
 * 描述 说明:
 */

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;

public class RSA {
    public static final String RSA = "RSA";

    public static byte[] decryptWithPrivateKey(byte[] bArr, RSAPrivateKey rSAPrivateKey) {
        return doRSA(rSAPrivateKey, 2, bArr);
    }

    public static byte[] decryptWithPublicKey(byte[] bArr, RSAPublicKey rSAPublicKey) {
        return doRSA(rSAPublicKey, 2, bArr);
    }

    private static byte[] doRSA(RSAKey rSAKey, int i, byte[] bArr) {
        try {
            Cipher instance = Cipher.getInstance(RSA);
            instance.init(i, (Key) rSAKey);
            return doRSA(instance, i, bArr, rSAKey.getModulus().bitLength());
        } catch (Throwable e) {
            throw new IllegalArgumentException(e);
        }
    }

    private static byte[] doRSA(Cipher cipher, int i, byte[] bArr, int i2) throws IOException, BadPaddingException, IllegalBlockSizeException {
        int i3 = 0;
        int i4 = i == 2 ? i2 / 8 : (i2 / 8) - 11;
        int length = bArr.length;
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        int i5 = 0;
        while (length - i3 > 0) {
            byte[] doFinal = length - i3 > i4 ? cipher.doFinal(bArr, i3, i4) : cipher.doFinal(bArr, i3, length - i3);
            byteArrayOutputStream.write(doFinal, 0, doFinal.length);
            i3 = i5 + 1;
            int i6 = i3;
            i3 *= i4;
            i5 = i6;
        }
        try {
            byteArrayOutputStream.flush();
            byte[] toByteArray = byteArrayOutputStream.toByteArray();
            byteArrayOutputStream.close();
            return toByteArray;
        } catch (Throwable th) {
            byteArrayOutputStream.close();
            return null;
        }

    }

    public static byte[] encryptWithPrivateKey(byte[] bArr, RSAPrivateKey rSAPrivateKey) {
        return doRSA(rSAPrivateKey, 1, bArr);
    }

    public static byte[] encryptWithPublicKey(byte[] bArr, RSAPublicKey rSAPublicKey) {
        return doRSA(rSAPublicKey, 1, bArr);
    }

    public static RSAPrivateKey generatePrivateKey(byte[] bArr) {
        try {
            return (RSAPrivateKey) KeyFactory.getInstance(RSA).generatePrivate(new PKCS8EncodedKeySpec(bArr));
        } catch (Throwable e) {
            throw new IllegalArgumentException("generate privateKey caught error", e);
        }
    }

    public static RSAPublicKey generatePublicKey(byte[] bArr) {
        try {
            return (RSAPublicKey) KeyFactory.getInstance(RSA).generatePublic(new X509EncodedKeySpec(bArr));
        } catch (Throwable e) {
            throw new IllegalArgumentException("generate publicKey caught error", e);
        }
    }

    public static KeyPair generateRSAKeyPair(int i) throws NoSuchAlgorithmException {
        KeyPairGenerator instance = KeyPairGenerator.getInstance(RSA);
        instance.initialize(i);
        return instance.genKeyPair();
    }

    public static RSAPrivateKey getPrivateKey(KeyPair keyPair) {
        return (RSAPrivateKey) keyPair.getPrivate();
    }

    public static RSAPublicKey getPublicKey(KeyPair keyPair) {
        return (RSAPublicKey) keyPair.getPublic();
    }

    public static byte[] readbyte(InputStream inputStream) throws IOException {
        byte[] bArr = new byte[1024];
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        while (true) {
            int read = inputStream.read(bArr);
            if (read == -1) {
                return byteArrayOutputStream.toByteArray();
            }
            byteArrayOutputStream.write(bArr, 0, read);
        }
    }

    public static byte[] readbyte(String str) throws IOException {
        FileInputStream fileInputStream = (FileInputStream) null;
        fileInputStream = new FileInputStream(str);
        byte[] bArr = new byte[1024];
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        while (true) {
            int read = fileInputStream.read(bArr);
            if (read == -1) {
                return byteArrayOutputStream.toByteArray();
            }
            byteArrayOutputStream.write(bArr, 0, read);
        }
    }
}