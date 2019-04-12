package ma.mhy.apkhook.util;

/**
 * 项目名 HookAPK
 * 所在包 ma.mhy.hookapk.util
 * 作者 mahongyin
 * 时间 2019/4/7 18:06
 * 邮箱 mhy.work@qq.com
 * 描述 说明:
 */

import android.content.Context;
import android.content.res.AssetManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class AssetsUtils {
    public static void copy(AssetManager assetManager, String str, String str2) throws IOException {
        File file = new File("sdcard/", str2);
        if (!file.exists()) {
            copy(assetManager.open(str), new FileOutputStream(file));
        }
    }

    public static void copy(InputStream inputStream, OutputStream outputStream) throws IOException {
        byte[] bArr = new byte[1024];
        while (true) {
            int read = inputStream.read(bArr);
            if (read == -1) {
                inputStream.close();
                outputStream.close();
                return;
            }
            outputStream.write(bArr, 0, read);
        }
    }

    public static String readAssetsTxt(Context context, String str) {
        try {
            InputStream open = context.getAssets().open(str);
            byte[] bArr = new byte[open.available()];
            open.read(bArr);
            open.close();
            return new String(bArr, "utf-8");
        } catch (IOException e) {
            e.printStackTrace();
            return "读取错误，请检查文件名";
        }
    }
}