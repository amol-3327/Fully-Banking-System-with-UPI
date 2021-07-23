package com.example.banking.manager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.banking.R;
import com.example.banking.cashier.c_dashboard;
import com.example.banking.cashier.c_new_account;
import com.example.banking.cashier.c_profile_change;
import com.example.banking.home;
import com.google.android.material.navigation.NavigationView;

import java.util.Timer;

public class m_dashboard extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawer_m;
    private NavigationView navigationView;
    private View headerView;
    private ProgressDialog progressDialog;
    private Timer timer;
    private CardView loan_r,inte,all,loan_t;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_m_dashboard);

        if(!SharedPrefManager.getInstance(this).isLoggedMan()){
            finish();
            startActivity(new Intent(this, home.class));
            return;
        }

        Toolbar toolbar = findViewById(R.id.toolbar_m);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Welcome to Bank");

        navigationView = findViewById(R.id.nav_view_m);
        navigationView.setNavigationItemSelectedListener(this);
        headerView = navigationView.getHeaderView(0);

        drawer_m = findViewById(R.id.drawer_layout_m);
        loan_r=findViewById(R.id.c_loan_r);
        inte=findViewById(R.id.c_inte);
        all=findViewById(R.id.c_all);
        loan_t=findViewById(R.id.c_loan_c);

        loan_t.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(m_dashboard.this, m_loan_types.class));
            }
        });
        loan_r.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(m_dashboard.this, m_loan_requests.class));
            }
        });
        inte.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(m_dashboard.this, m_calculate_interest.class));
            }
        });
        all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(m_dashboard.this, m_list_emp_user.class));
            }
        });

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer_m, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer_m.addDrawerListener(toggle);
        toggle.syncState();
    }

    public void setActionBarTitle(String title) {
        getSupportActionBar().setTitle(title);
    }
    @Override
    public void onBackPressed() {
        if (drawer_m.isDrawerOpen(GravityCompat.START)) {
            drawer_m.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_logout:
                u_logout();
                break;
            case R.id.nav_profile:
                Intent intent = new Intent(m_dashboard.this, m_profile_change.class);
                intent.putExtra("rmk", "profile");
                startActivity(intent);
                break;
            case R.id.nav_cpin:
                Intent intent1 = new Intent(m_dashboard.this, m_profile_change.class);
                intent1.putExtra("rmk", "change");
                startActivity(intent1);
                break;
        }
        drawer_m.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_l, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_logout:
                u_logout();
                break;
        }
        return true;
    }

//    @Override
//    protected void onPause() {
//        super.onPause();
//        timer = new Timer();
//        LogOutTimerTask logoutTimeTask = new LogOutTimerTask();
//        timer.schedule(logoutTimeTask, 500000); //auto logout in 5 minutes
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        if (timer != null) {
//            timer.cancel();
//            timer = null;
//        }
//    }
//
//    private class LogOutTimerTask extends TimerTask {
//        @Override
//        public void run() {
//            SharedPrefManager.getInstance(c_dashboard.this).logout1();
//            finishAffinity();
//            startActivity(new Intent(c_dashboard.this, home.class));
//        }
//    }

    public void u_logout(){
        AlertDialog.Builder builder1 = new AlertDialog.Builder(m_dashboard.this);
        builder1.setTitle("Logout");
        builder1.setMessage("Are you sure, You want to logout?");
        builder1.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                SharedPrefManager.getInstance(m_dashboard.this).logout_man();
                finishAffinity();
                startActivity(new Intent(m_dashboard.this, home.class));
            }
        });
        builder1.setNegativeButton("no", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        AlertDialog dialog = builder1.create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface arg0) {
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.alert));
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.alert));
            }
        });
        dialog.show();
    }
}