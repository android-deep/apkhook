package ma.mhy.apkhook.adapter;

/**
 * 项目名 HookAPK
 * 所在包 ma.mhy.hookapk.adapter
 * 作者 mahongyin
 * 时间 2019/4/7 22:52
 * 邮箱 mhy.work@qq.com
 * 描述 说明:
 */
import java.util.List;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

public class FragmentAdapter extends FragmentStatePagerAdapter {
    private List<Fragment> mFragments;
    private List<String> mTitles;

    public FragmentAdapter(FragmentManager paramFragmentManager, List<Fragment> paramList, List<String> paramList1) {
        super(paramFragmentManager);
        this.mFragments = paramList;
        this.mTitles = paramList1;
    }

    @Override
    public int getCount() {
        return this.mFragments.size();
    }

    @Override
    public Fragment getItem(int paramInt) {
        return (Fragment)this.mFragments.get(paramInt);
    }

    @Override
    public CharSequence getPageTitle(int paramInt) {
        return (String)this.mTitles.get(paramInt);
    }
}
