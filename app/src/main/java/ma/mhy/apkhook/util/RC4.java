package ma.mhy.apkhook.util;

/**
 * 项目名 HookAPK
 * 所在包 ma.mhy.hookapk.util
 * 作者 mahongyin
 * 时间 2019/4/7 18:06
 * 邮箱 mhy.work@qq.com
 * 描述 说明:
 */

public class RC4 {
    public static String Rc4(String str, String str2) {
        int i;
        int i2 = 0;
        int[] iArr = new int[256];
        byte[] bArr = new byte[256];
        for (i = 0; i < 256; i++) {
            iArr[i] = i;
        }
        for (i = 0; i < 256; i = (short) (i + 1)) {
            bArr[i] = (byte) str2.charAt(i % str2.length());
        }
        int i3 = 0;
        for (i = 0; i < 255; i++) {
            i3 = ((i3 + iArr[i]) + bArr[i]) % 256;
            int i4 = iArr[i];
            iArr[i] = iArr[i3];
            iArr[i3] = i4;
        }
        char[] toCharArray = str.toCharArray();
        char[] cArr = new char[toCharArray.length];
        i3 = 0;
        for (i = 0; i < toCharArray.length; i = (short) (i + 1)) {
            i2 = (i2 + 1) % 256;
            i3 = (i3 + iArr[i2]) % 256;
            int i5 = iArr[i2];
            iArr[i2] = iArr[i3];
            iArr[i3] = i5;
            cArr[i] = (char) (((char) iArr[(iArr[i2] + (iArr[i3] % 256)) % 256]) ^ toCharArray[i]);
        }
        return new String(cArr);
    }
}