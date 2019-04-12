package ma.mhy.apkhook.kellinwood.security.zipsigner;

/**
 * 项目名 HookAPK
 * 所在包 ma.mhy.hookapk.ma.mhy.apkhook.kellinwood.security.zipsigner
 * 作者 mahongyin
 * 时间 2019/4/8 11:37
 * 邮箱 mhy.work@qq.com
 * 描述 说明:
 */
public class AutoKeyException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public AutoKeyException( String message) {
        super(message);
    }

    public AutoKeyException( String message, Throwable cause) {
        super(message, cause);
    }
}