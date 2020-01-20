package team.ten.buyticket;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.internal.BottomNavigationItemView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import team.ten.buyticket.Fragment.HomeFragment;
import team.ten.buyticket.Fragment.MyselfFragment;
import team.ten.buyticket.Fragment.TicketFragment;
import team.ten.buyticket.Activity_login.Login_Activity;


public class MainActivity extends AppCompatActivity {
    private List<Fragment> list;
    private ViewPager myViewPager;
    private TabFragmentPagerAdapter adapter;
    final Fragment fragment1 = new HomeFragment();
    final Fragment fragment2 = new MyselfFragment();
    final Fragment fragment3 = new TicketFragment();
    final FragmentManager fm = getSupportFragmentManager();
    private int itemPosition = 10;
    Fragment active = fragment1;
    BottomNavigationView navigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }


        myViewPager = (ViewPager) findViewById(R.id.myViewPager);

        navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        Bundle bundle =this.getIntent().getExtras();

        try{
            if( bundle.getString("activity").equals("Ticket")){
                active = fragment3;
                itemPosition = 1;
            }else if (bundle.getString("activity").equals("Myself")){
                active = fragment2;
                itemPosition = 2;
            }else{
                active = fragment1;
                itemPosition = 0;
            }
        }catch (Exception e){
            active = fragment1;
            itemPosition = 0;
        }
        initView(itemPosition);
    }

    private void initView(int itemPosition)
    {
        myViewPager = (ViewPager) findViewById(R.id.myViewPager);
        myViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            public void onPageScrollStateChanged(int state) {}
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}
            public void onPageSelected(int position) {

                switch (position){
                    case 0:
                        navigation.getMenu().getItem(0).setChecked(true);
                        active = fragment1;
                        break;
                    case 1:
                        navigation.getMenu().getItem(1).setChecked(true);
                        active = fragment3;
                        break;
                    case 2:
                        navigation.getMenu().getItem(2).setChecked(true);
                        active = fragment2;
                        break;
                }


            }
        });
        //把Fragment添加到List集合裏面
        list = new ArrayList<>();
        list.add(fragment1);
        list.add(fragment3);
        list.add(fragment2);
        adapter = new TabFragmentPagerAdapter(fm, list);
        myViewPager.setAdapter(adapter);
        if (itemPosition == 10) {
            myViewPager.setCurrentItem(0);  //初始化顯示第一個頁面
        }else{
            myViewPager.setCurrentItem(itemPosition);
        }

    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    myViewPager.setCurrentItem(0);
                    active = fragment1;
                    return true;

                case R.id.navigation_ticket:
                    myViewPager.setCurrentItem(1);
                    active = fragment3;
                    return true;

                case R.id.navigation_myself:
                    myViewPager.setCurrentItem(2);
                    active = fragment2;
                    return true;
            }
            return false;
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);


        return super.onCreateOptionsMenu(menu);
    }

}
