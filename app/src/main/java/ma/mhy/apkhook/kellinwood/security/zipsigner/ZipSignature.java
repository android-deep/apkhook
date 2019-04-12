package ma.mhy.apkhook.kellinwood.security.zipsigner;

/**
 * 项目名 HookAPK
 * 所在包 ma.mhy.hookapk.ma.mhy.apkhook.kellinwood.security.zipsigner
 * 作者 mahongyin
 * 时间 2019/4/8 11:50
 * 邮箱 mhy.work@qq.com
 * 描述 说明:
 */
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.PrivateKey;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;

@SuppressWarnings("restriction")
public class ZipSignature {

    byte[] beforeAlgorithmIdBytes =  { 0x30, 0x21 };

    //      byte[] algorithmIdBytes;
    //		algorithmIdBytes =  sun.security.x509.AlgorithmId.get("SHA1").encode();
    byte[] algorithmIdBytes = {0x30, 0x09, 0x06, 0x05, 0x2B, 0x0E, 0x03, 0x02, 0x1A, 0x05, 0x00 };

    byte[] afterAlgorithmIdBytes = { 0x04, 0x14 };

    Cipher cipher;

    MessageDigest md;


    public ZipSignature() throws IOException, GeneralSecurityException
    {
        md = MessageDigest.getInstance("SHA1");
        cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
    }

    public void initSign( PrivateKey privateKey) throws InvalidKeyException
    {
        cipher.init(Cipher.ENCRYPT_MODE, privateKey);
    }

    public void update( byte[] data) {
        md.update( data);
    }

    public void update( byte[] data, int offset, int count) {
        md.update( data, offset, count);
    }

    public byte[] sign() throws BadPaddingException, IllegalBlockSizeException
    {
        cipher.update( beforeAlgorithmIdBytes);
        cipher.update( algorithmIdBytes);
        cipher.update( afterAlgorithmIdBytes);
        cipher.update( md.digest());
        return cipher.doFinal();
    }
}
