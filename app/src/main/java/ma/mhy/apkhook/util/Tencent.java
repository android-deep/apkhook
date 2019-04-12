package ma.mhy.apkhook.util;

/**
 * 项目名 HookAPK
 * 所在包 ma.mhy.hookapk.util
 * 作者 mahongyin
 * 时间 2019/4/7 18:08
 * 邮箱 mhy.work@qq.com
 * 描述 说明:
 */
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

public class Tencent {
   static boolean bool=false;
    public static boolean joinQQGroup(Context paramContext, String paramString)
    {
        Intent localIntent = new Intent();
        localIntent.setData(Uri.parse("mqqopensdkapi://bizAgent/qm/qr?url=http%3A%2F%2Fqm.qq.com%2Fcgi-bin%2Fqm%2Fqr%3Ffrom%3Dapp%26p%3Dandroid%26k%3D" + paramString));
        try
        {
            paramContext.startActivity(localIntent);
             bool = true;
        }
        catch (Exception e)
        {
                boolean bool = false;
        }
        return bool;
    }
}
