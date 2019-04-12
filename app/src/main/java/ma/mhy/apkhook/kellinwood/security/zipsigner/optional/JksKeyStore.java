package ma.mhy.apkhook.kellinwood.security.zipsigner.optional;

/**
 * 项目名 HookAPK
 * 所在包 ma.mhy.hookapk.ma.mhy.apkhook.kellinwood.security.zipsigner.optional
 * 作者 mahongyin
 * 时间 2019/4/8 11:57
 * 邮箱 mhy.work@qq.com
 * 描述 说明:
 */
import java.security.KeyStore;

public class JksKeyStore extends KeyStore {

    public JksKeyStore() {
        super(new JKS(), KeyStoreFileManager.getProvider(), "jks");
    }

}
