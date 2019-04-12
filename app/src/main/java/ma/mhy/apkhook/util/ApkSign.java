package ma.mhy.apkhook.util;

/**
 * 项目名 HookAPK
 * 所在包 ma.mhy.hookapk.util
 * 作者 mahongyin
 * 时间 2019/4/7 18:05
 * 邮箱 mhy.work@qq.com
 * 描述 说明:
 */
import java.io.InputStream;
import java.security.cert.Certificate;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class ApkSign {
    public static String getApkSignInfo(String str) {
        byte[] bArr = new byte[8192];
        Certificate[] certificateArr = (Certificate[]) null;
        try {
            JarFile jarFile = new JarFile(str);
            Enumeration entries = jarFile.entries();
            Certificate[] certificateArr2 = certificateArr;
            while (entries.hasMoreElements()) {
                JarEntry jarEntry = (JarEntry) entries.nextElement();
                if (!(jarEntry.isDirectory() || jarEntry.getName().startsWith("META-INF/"))) {
                    Certificate[] loadCertificates = loadCertificates(jarFile, jarEntry, bArr);
                    if (certificateArr2 == null) {
                        certificateArr2 = loadCertificates;
                    } else {
                        int i = 0;
                        while (i < certificateArr2.length) {
                            Object obj;
                            int i2 = 0;
                            while (i2 < loadCertificates.length) {
                                if (certificateArr2[i] != null && certificateArr2[i].equals(loadCertificates[i2])) {
                                    obj = 1;
                                    break;
                                }
                                i2++;
                            }
                            obj = null;
                            if (obj == null || certificateArr2.length != loadCertificates.length) {
                                jarFile.close();
                                return (String) null;
                            }
                            i++;
                        }
                        continue;
                    }
                }
            }
            jarFile.close();
            return new String(toChars(certificateArr2[0].getEncoded()));
        } catch (Exception e) {
            e.printStackTrace();
            return (String) null;
        }
    }

    private static Certificate[] loadCertificates(JarFile jarFile, JarEntry jarEntry, byte[] bArr) {
        try {
            InputStream inputStream = jarFile.getInputStream(jarEntry);
            do {
            } while (inputStream.read(bArr, 0, bArr.length) != -1);
            inputStream.close();
            return jarEntry != null ? jarEntry.getCertificates() : (Certificate[]) null;
        } catch (Exception e) {
            return (Certificate[]) null;
        }
    }

    private static char[] toChars(byte[] bArr) {
        int length = bArr.length;
        char[] cArr = new char[(length * 2)];
        for (int i = 0; i < length; i++) {
            byte b = bArr[i];
            int i2 = (b >> 4) & 15;
            cArr[i * 2] = (char) (i2 >= 10 ? (i2 + 97) - 10 : i2 + 48);
            i2 = b & 15;
            cArr[(i * 2) + 1] = (char) (i2 >= 10 ? (i2 + 97) - 10 : i2 + 48);
        }
        return cArr;
    }
}