package ma.mhy.apkhook.kellinwood.security.zipsigner.optional;

/**
 * 项目名 HookAPK
 * 所在包 ma.mhy.hookapk.ma.mhy.apkhook.kellinwood.security.zipsigner.optional
 * 作者 mahongyin
 * 时间 2019/4/8 11:55
 * 邮箱 mhy.work@qq.com
 * 描述 说明:
 */
import java.io.IOException;

/**
 * Thrown by JKS.engineLoad() for errors that occur after determining the keystore is actually aService JKS keystore.
 */
public class LoadKeystoreException extends IOException {

    public LoadKeystoreException() {
    }

    public LoadKeystoreException(String message) {
        super(message);
    }

    public LoadKeystoreException(String message, Throwable cause) {
        super(message, cause);
    }

    public LoadKeystoreException(Throwable cause) {
        super(cause);
    }
}