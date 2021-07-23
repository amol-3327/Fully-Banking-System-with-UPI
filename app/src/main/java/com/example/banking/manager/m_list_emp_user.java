package com.example.banking.manager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;

import com.example.banking.R;
import com.example.banking.home;
import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;

public class m_list_emp_user extends AppCompatActivity {
    TabLayout tabLayout;
    TabItem tabItem1,tabItem2;
    ViewPager viewPager;
    m_list_emp_user.PageAdapter pageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_m_list_emp_user);


        if(!SharedPrefManager.getInstance(this).isLoggedMan()){
            finish();
            startActivity(new Intent(this, home.class));
            return;
        }

//        getSupportActionBar().setTitle(Html.fromHtml("<font color=#000000>Loan Requests :</font>"));
//        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#687BFFBC")));


        tabLayout=findViewById(R.id.tablayout1);
        tabItem1=findViewById(R.id.tab1);
        tabItem2=findViewById(R.id.tab2);
        viewPager=findViewById(R.id.vpager);

        Toolbar toolbar = findViewById(R.id.t_t);
        setSupportActionBar(toolbar);

        pageAdapter=new m_list_emp_user.PageAdapter(getSupportFragmentManager(),tabLayout.getTabCount());
        viewPager.setAdapter(pageAdapter);

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                if(tab.getPosition()==0 || tab.getPosition()==1)
                    pageAdapter.notifyDataSetChanged();
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }
            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
    }

    public class PageAdapter extends FragmentPagerAdapter {
        int tabcount;

        public PageAdapter(@NonNull FragmentManager fm, int behavior) {
            super(fm, behavior);
            tabcount=behavior;
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0 : return new AccountFragement();
                case 1 : return new EmployeeFragement();
                default: return null;
            }
        }

        @Override
        public int getCount() {
            return tabcount;
        }
    }
}