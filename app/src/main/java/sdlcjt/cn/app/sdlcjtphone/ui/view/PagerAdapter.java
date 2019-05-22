package sdlcjt.cn.app.sdlcjtphone.ui.view;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 *
 */
public class PagerAdapter extends FragmentPagerAdapter {
    private List<Fragment> mList;
    private List<String> mTitle;
    private String[] mTitleStr;

    public PagerAdapter(FragmentManager fm) {
        super(fm);
    }

    public PagerAdapter(FragmentManager fm, List<Fragment> list, List<String> titles) {
        super(fm);
        mList = list;
        mTitle = titles;
    }

    public PagerAdapter(FragmentManager fm, List<Fragment> list, String[] mTitleStr) {
        super(fm);
        mList = list;
        this.mTitleStr = mTitleStr;
    }

    @Override
    public Fragment getItem(int position) {
        return mList.get(position);
    }

    @Override
    public int getCount() {
        if (mTitle == null) {
            return mTitleStr.length;
        }
        return mTitle.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (mTitle == null) {
            return mTitleStr[position];
        }
        return mTitle.get(position);
    }
}
