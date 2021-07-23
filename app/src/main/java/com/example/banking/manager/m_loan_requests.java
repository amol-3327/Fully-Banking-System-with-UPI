package com.example.banking.manager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.BlendModeColorFilterCompat;
import androidx.core.graphics.BlendModeCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Html;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.widget.LinearLayout;
import android.widget.SearchView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.banking.MainActivity;
import com.example.banking.R;
import com.example.banking.home;
import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class m_loan_requests extends AppCompatActivity {
    TabLayout tabLayout;
    TabItem tabItem1,tabItem2;
    ViewPager viewPager;
    PageAdapter pageAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_m_loan_requests);

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


        pageAdapter=new PageAdapter(getSupportFragmentManager(),tabLayout.getTabCount());
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
                case 0 : return new PendingFragement();
                case 1 : return new FinishedFragement();
                default: return null;
            }
        }

        @Override
        public int getCount() {
            return tabcount;
        }
    }
}