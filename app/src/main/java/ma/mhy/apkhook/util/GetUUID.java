package ma.mhy.apkhook.util;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import java.util.UUID;

/**
 * 项目名 APKHook
 * 所在包 ma.mhy.apkhook.util
 * 作者 mahongyin
 * 时间 2019/4/13 9:22
 * 邮箱 mhy.work@qq.com
 * 描述 说明:设备唯一标识符需要特别注意，原来的READ_PHONE_STATE权限已经不能获得IMEI和序列号，如果想在Q设备上通过
 * ((TelephonyManager) getActivity().getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId()
 * 复制代码获得设备ID，会返回空值(targetSDK<=P)或者报错(targetSDK==Q)。
 * 且官方所说的READ_PRIVILEGED_PHONE_STATE权限只提供给系统app，所以这个方法算是废了。
 * 谷歌官方给予了设备唯一ID最佳做法，但是此方法给出的ID可变，可以按照具体需求具体解决。
 * 一个不变和基本不重复的UUID方法。
 */
public class GetUUID {
    public static String getUUID(Context context) {

        String serial = null;

        String m_szDevIDShort = "35" +
                Build.BOARD.length() % 10 + Build.BRAND.length() % 10 +

                Build.CPU_ABI.length() % 10 + Build.DEVICE.length() % 10 +

                Build.DISPLAY.length() % 10 + Build.HOST.length() % 10 +

                Build.ID.length() % 10 + Build.MANUFACTURER.length() % 10 +

                Build.MODEL.length() % 10 + Build.PRODUCT.length() % 10 +

                Build.TAGS.length() % 10 + Build.TYPE.length() % 10 +

                Build.USER.length() % 10; //13 位

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                if (context.checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    Activity#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for Activity#requestPermissions for more details.

                }
                serial = android.os.Build.getSerial();
            } else {
                serial = Build.SERIAL;
            }
            //API>=9 使用serial号
            return new UUID(m_szDevIDShort.hashCode(), serial.hashCode()).toString();
        } catch (Exception exception) {
            //serial需要一个初始化
            serial = "serial"; // 随便一个初始化
        }
        //使用硬件信息拼凑出来的15位号码
        return new UUID(m_szDevIDShort.hashCode(), serial.hashCode()).toString();
    }

}
