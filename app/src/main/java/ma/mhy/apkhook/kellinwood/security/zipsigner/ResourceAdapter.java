package ma.mhy.apkhook.kellinwood.security.zipsigner;

/**
 * 项目名 HookAPK
 * 所在包 ma.mhy.hookapk.ma.mhy.apkhook.kellinwood.security.zipsigner
 * 作者 mahongyin
 * 时间 2019/4/8 11:52
 * 邮箱 mhy.work@qq.com
 * 描述 说明:
 */
public interface ResourceAdapter {

    public enum Item {
        INPUT_SAME_AS_OUTPUT_ERROR,
        AUTO_KEY_SELECTION_ERROR,
        LOADING_CERTIFICATE_AND_KEY,
        PARSING_CENTRAL_DIRECTORY,
        GENERATING_MANIFEST,
        GENERATING_SIGNATURE_FILE,
        GENERATING_SIGNATURE_BLOCK,
        COPYING_ZIP_ENTRY
    };

    public String getString(Item item, Object... args);
}
