package ma.mhy.apkhook.ui;

import com.google.android.material.textfield.TextInputLayout;

/**
 * 项目名 HookAPK
 * 所在包 ma.mhy.hookapk.ui
 * 作者 mahongyin
 * 时间 2019/4/7 21:55
 * 邮箱 mhy.work@qq.com
 * 描述 说明:
 * @author mahongyin
 */

public class TextInputError {
    public static void showError(TextInputLayout paramTextInputLayout, String paramString) {
        paramTextInputLayout.setError(paramString);
        paramTextInputLayout.getEditText().setFocusable(true);
        paramTextInputLayout.getEditText().setFocusableInTouchMode(true);
        paramTextInputLayout.getEditText().requestFocus();
    }
}
