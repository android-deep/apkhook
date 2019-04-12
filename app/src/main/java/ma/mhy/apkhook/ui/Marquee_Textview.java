package ma.mhy.apkhook.ui;

/**
 * 项目名 HookAPK
 * 所在包 ma.mhy.hookapk.ui
 * 作者 mahongyin
 * 时间 2019/4/7 21:53
 * 邮箱 mhy.work@qq.com
 * 描述 说明:
 */

import android.content.Context;
import android.util.AttributeSet;
import androidx.appcompat.widget.AppCompatTextView;

public class Marquee_Textview extends AppCompatTextView
{
    public Marquee_Textview(Context paramContext)
    {
        super(paramContext);
    }

    public Marquee_Textview(Context paramContext, AttributeSet paramAttributeSet)
    {
        super(paramContext, paramAttributeSet);
    }

    public Marquee_Textview(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
    {
        super(paramContext, paramAttributeSet, paramInt);
    }

    @Override
    public boolean isFocused()
    {
        return true;
    }
}
