package team.ten.buyticket;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import java.util.List;

import team.ten.buyticket.Fragment.HomeFragment;
import team.ten.buyticket.Fragment.MyselfFragment;
import team.ten.buyticket.Fragment.TicketFragment;

/**
 * Created by dx on 2018/9/18.
 */
public class TabFragmentPagerAdapter extends FragmentPagerAdapter {
    private List<Fragment> mlist;

    public TabFragmentPagerAdapter(FragmentManager fm, List<Fragment> list) {
        super(fm);
        this.mlist = list;
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        super.setPrimaryItem(container, position, object);
    }

    @Override
    public Fragment getItem(int arg0) {
        return mlist.get(arg0);//顯示第幾個頁面
    }

    @Override
    public int getCount() {
        return mlist.size();//有幾個頁面
    }
}
