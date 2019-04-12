package ma.mhy.apkhook.util;

/**
 * 项目名 HookAPK
 * 所在包 ma.mhy.hookapk.util
 * 作者 mahongyin
 * 时间 2019/4/7 18:07
 * 邮箱 mhy.work@qq.com
 * 描述 说明:
 */

import android.content.Context;
import android.content.SharedPreferences.Editor;
import android.text.TextUtils;
import android.widget.AutoCompleteTextView;

public class Shared {
    private final int MAX_HISTORY_COUNT = 50;
    private final String SP_EMPTY_TAG = "<empty>";
    private final String SP_NAME = "recent_history";
    private final String SP_SEPARATOR = ":-P";
    private Context a;

    public Shared(Context context) {
        this.a = context;
    }

    public void clearHistoryInSharedPreferences() {
        Context context = this.a;
        Editor edit = this.a.getSharedPreferences("recent_history", Context.MODE_PRIVATE).edit();
        edit.clear();
        edit.apply();
    }

    public String[] getHistoryArray(String paramString) {
        String[]chars = getHistoryFromSharedPreferences(paramString).split(":-P");
        if (chars.length > 50) {
            System.arraycopy(paramString, 0, new String[50], 0, 50);
        }
        return chars;
    }
    public String getHistoryFromSharedPreferences(String str) {
        Context context = this.a;
        return this.a.getSharedPreferences("recent_history", Context.MODE_PRIVATE).getString(str, "<empty>");
    }

    public void saveHistory(AutoCompleteTextView autoCompleteTextView, String str) {
        String trim = autoCompleteTextView.getText().toString().trim();
        if (!TextUtils.isEmpty(trim)) {
            String historyFromSharedPreferences = getHistoryFromSharedPreferences(str);
            StringBuilder stringBuilder = this.SP_EMPTY_TAG.equals(historyFromSharedPreferences) ? new StringBuilder() : new StringBuilder(historyFromSharedPreferences);
            stringBuilder.append(new StringBuffer().append(trim).append(":-P").toString());
            if (!historyFromSharedPreferences.contains(new StringBuffer().append(trim).append(":-P").toString())) {
                saveHistoryToSharedPreferences(str, stringBuilder.toString());
            }
        }
    }

    public void saveHistoryToSharedPreferences(String str, String str2) {
        Context context = this.a;
        Editor edit = this.a.getSharedPreferences("recent_history", Context.MODE_PRIVATE).edit();
        edit.putString(str, str2);
        edit.apply();
    }

}
