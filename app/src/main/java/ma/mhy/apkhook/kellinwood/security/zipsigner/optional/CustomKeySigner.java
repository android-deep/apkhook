package ma.mhy.apkhook.kellinwood.security.zipsigner.optional;

/**
 * 项目名 HookAPK
 * 所在包 ma.mhy.hookapk.ma.mhy.apkhook.kellinwood.security.zipsigner.optional
 * 作者 mahongyin
 * 时间 2019/4/8 11:57
 * 邮箱 mhy.work@qq.com
 * 描述 说明:
 */

import java.security.Key;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;

import ma.mhy.apkhook.kellinwood.security.zipsigner.ZipSigner;

/**
 */
public class CustomKeySigner {

    /** KeyStore-type agnostic.  This method will sign the zip file, automatically handling JKS or BKS keystores. */
    public static void signZip( ZipSigner zipSigner,
                                String keystorePath,
                                char[] keystorePw,
                                String certAlias,
                                char[] certPw,
                                String signatureAlgorithm,
                                String inputZipFilename,
                                String outputZipFilename)
            throws Exception
    {
        zipSigner.issueLoadingCertAndKeysProgressEvent();
        KeyStore keystore = KeyStoreFileManager.loadKeyStore( keystorePath, keystorePw);
        Certificate cert = keystore.getCertificate(certAlias);
        X509Certificate publicKey = (X509Certificate)cert;
        Key key = keystore.getKey(certAlias, certPw);
        PrivateKey privateKey = (PrivateKey)key;

        zipSigner.setKeys( "custom", publicKey, privateKey, signatureAlgorithm, null);
        zipSigner.signZip( inputZipFilename, outputZipFilename);
    }

}
