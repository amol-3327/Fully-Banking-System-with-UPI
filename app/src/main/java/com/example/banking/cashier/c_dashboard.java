package com.example.banking.cashier;

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
import android.widget.TextView;

import com.example.banking.MainActivity;
import com.example.banking.R;
import com.example.banking.home;
import com.example.banking.payment_gateway;
import com.example.banking.u_fund_transfer;
import com.google.android.material.navigation.NavigationView;

import java.text.DecimalFormat;
import java.util.Timer;
import java.util.TimerTask;

public class c_dashboard extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawer_c;
    private NavigationView navigationView;
    private View headerView;
    private ProgressDialog progressDialog;
    private Timer timer;
    private CardView n_acc,with,depo,tran,add,cheque,c_deposit,all_acc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_c_dashboard);

        if(!SharedPrefManager.getInstance(this).isLoggedIn1()){
            finish();
            startActivity(new Intent(this, home.class));
            return;
        }

        Toolbar toolbar = findViewById(R.id.toolbar_c);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Welcome to Bank");
        drawer_c = findViewById(R.id.drawer_layout_c);

        navigationView = findViewById(R.id.nav_view_c);
        navigationView.setNavigationItemSelectedListener(this);
        headerView = navigationView.getHeaderView(0);

        n_acc=findViewById(R.id.c_new);
        with=findViewById(R.id.c_with);
        depo=findViewById(R.id.c_depo);
        tran=findViewById(R.id.c_tran);
        add=findViewById(R.id.c_card);
        cheque=findViewById(R.id.c_cheque);
        c_deposit=findViewById(R.id.c_c_deposit);
        all_acc=findViewById(R.id.c_all);

        n_acc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(c_dashboard.this,c_new_account.class));
            }
        });
        with.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(c_dashboard.this,c_withdraw.class));
            }
        });
        depo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(c_dashboard.this,c_deposit.class));
            }
        });
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(c_dashboard.this,c_add_card.class));
            }
        });
        tran.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(c_dashboard.this,c_transfer.class));
            }
        });
        cheque.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(c_dashboard.this,c_manage_cheque.class));
            }
        });
        c_deposit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(c_dashboard.this,c_deposit_cheque.class));
            }
        });
        all_acc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(c_dashboard.this,c_all_accounts.class));
            }
        });

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer_c, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer_c.addDrawerListener(toggle);
        toggle.syncState();

    }


    public void setActionBarTitle(String title) {
        getSupportActionBar().setTitle(title);
    }
    @Override
    public void onBackPressed() {
        if (drawer_c.isDrawerOpen(GravityCompat.START)) {
            drawer_c.closeDrawer(GravityCompat.START);
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
                Intent intent = new Intent(c_dashboard.this, c_profile_change.class);
                intent.putExtra("rmk", "profile");
                startActivity(intent);
                break;
            case R.id.nav_cpin:
                Intent intent1 = new Intent(c_dashboard.this, c_profile_change.class);
                intent1.putExtra("rmk", "change");
                startActivity(intent1);
                break;
        }
        drawer_c.closeDrawer(GravityCompat.START);
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
        AlertDialog.Builder builder1 = new AlertDialog.Builder(c_dashboard.this);
        builder1.setTitle("Logout");
        builder1.setMessage("Are you sure, You want to logout?");
        builder1.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                SharedPrefManager.getInstance(c_dashboard.this).logout1();
                finishAffinity();
                startActivity(new Intent(c_dashboard.this, home.class));
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