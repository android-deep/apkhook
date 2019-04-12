package ma.mhy.apkhook.kellinwood.security.zipsigner;

/**
 * 项目名 HookAPK
 * 所在包 ma.mhy.hookapk.ma.mhy.apkhook.kellinwood.security.zipsigner
 * 作者 mahongyin
 * 时间 2019/4/8 11:51
 * 邮箱 mhy.work@qq.com
 * 描述 说明:
 */

public interface ProgressListener {

    /** Called to notify the listener that progress has been made during
     the zip signing operation.
     */
    public void onProgress(ProgressEvent event);
}