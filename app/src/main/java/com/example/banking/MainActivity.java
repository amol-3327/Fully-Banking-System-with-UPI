package com.example.banking;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.banking.cashier.c_transfer;
import com.github.chrisbanes.photoview.PhotoView;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawer;
    private NavigationView navigationView;
    private View headerView;
    private ProgressDialog progressDialog;
    private Timer timer;
    private TextView acc,bal,manage,stmt;
    private DecimalFormat formatter;
    private CardView send,scan,pass,change,a_upi,check,loan,c_manage;
    private String st_mob,st_card,st_bal;
    private int CAMERA_PERMISSION_CODE = 1,STORAGE_PERMISSION_CODE  = 1;
    private int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(!SharedPrefManager.getInstance(this).isLoggedIn()){
            finish();
            startActivity(new Intent(this, home.class));
            return;
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Welcome to Bank");
        drawer = findViewById(R.id.drawer_layout);

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        headerView = navigationView.getHeaderView(0);
        acc=findViewById(R.id.tv_acc);
        bal=findViewById(R.id.tv_bal);
        manage=findViewById(R.id.tv_manage);
        stmt=findViewById(R.id.tv_stmt);
        send=findViewById(R.id.c_ft);
        scan=findViewById(R.id.c_sp);
        pass=findViewById(R.id.c_p);
        change=findViewById(R.id.c_cl);
        a_upi=findViewById(R.id.c_au);
        check=findViewById(R.id.c_ncb);
        loan=findViewById(R.id.c_loan);
        c_manage=findViewById(R.id.c_manage);

        acc.setText(SharedPrefManager.getInstance(MainActivity.this).getAccNo());
        formatter =new DecimalFormat("##,##,###.00");

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        u_dashboard();

        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, u_change_l_pin.class));
            }
        });
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                check_upi("send");
            }
        });
        scan.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                checkAndRequestPermissions();
            }
        });
        manage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                check_upi("manage");
            }
        });
        c_manage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                check_upi("manage");
            }
        });
        a_upi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                check_upi("register");
            }
        });
        pass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, u_my_trans.class));
            }
        });
        stmt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, u_my_trans.class));
            }
        });
        check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, u_apply_cheque.class));
            }
        });
        loan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                check_upi("loan");
            }
        });
    }

    public void setActionBarTitle(String title) { getSupportActionBar().setTitle(title);
    }
    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
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
                startActivity(new Intent(this,u_profile.class));
                break;
            case R.id.nav_pass:
                startActivity(new Intent(this,u_my_trans.class));
                break;
            case R.id.nav_unregister:
                u_unregister();
                break;
            case R.id.nav_cpin:
               startActivity(new Intent(this,u_change_l_pin.class));
                break;

        }
        drawer.closeDrawer(GravityCompat.START);
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

    private void u_unregister() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Deregister");
        builder.setMessage("Are you sure, You want to deregister?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                progressDialog.show();
                StringRequest stringRequest = new StringRequest(
                        Request.Method.POST,
                        Constants.URL_U_UNREGISTER,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                progressDialog.dismiss();
                                try {
                                    JSONObject obj = new JSONObject(response);
                                    if (!obj.getBoolean("error")) {
                                        AlertDialog.Builder builderDel = new AlertDialog.Builder(MainActivity.this);
                                        builderDel.setMessage(obj.getString("message"));
                                        builderDel.setCancelable(false);
                                        builderDel.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                dialogInterface.dismiss();
                                                SharedPrefManager.getInstance(MainActivity.this).unregister();
                                                finishAffinity();
                                                startActivity(new Intent(MainActivity.this, u_new_user.class));
                                            }
                                        });
                                        builderDel.create().show();
                                    } else {
                                        Toast.makeText(getApplicationContext(),obj.getString("message"),Toast.LENGTH_SHORT).show();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                progressDialog.dismiss();
                                AlertDialog.Builder builderDel = new AlertDialog.Builder(MainActivity.this);
                                builderDel.setCancelable(false);
                                builderDel.setMessage("Network Error, Try Again Later.");
                                builderDel.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                    }
                                });
                                builderDel.create().show();
                            }
                        }
                ) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> params = new HashMap<>();
                        params.put("acc", SharedPrefManager.getInstance(MainActivity.this).getAccNo());
                        return params;
                    }
                };
                RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
                requestQueue.add(stringRequest);

                requestQueue.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<Object>() {
                    @Override
                    public void onRequestFinished(Request<Object> request) {
                        requestQueue.getCache().clear();
                    }
                });
            }
        });
        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface arg0) {
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.alert));
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.alert));
            }
        });
        dialog.show();
    }

//    @Override
//    protected void onPause() {
//        super.onPause();
//        timer = new Timer();
//        LogOutTimerTask logoutTimeTask = new LogOutTimerTask();
//        timer.schedule(logoutTimeTask, 480000); //auto logout in 5 minutes
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
//            SharedPrefManager.getInstance(MainActivity.this).logout();
//            finishAffinity();
//            startActivity(new Intent(MainActivity.this, home.class));
//        }
//    }

    private void u_dashboard() {
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                Constants.URL_DASHBOARD,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        try {
                            JSONObject obj = new JSONObject(response);
                            if (!obj.getBoolean("error")) {
                                st_bal=obj.getString("amount");
                                if (st_bal.equals("0")){
                                    bal.setText("0.00");
                                }else {
                                    bal.setText(formatter.format(Long.parseLong(st_bal)));
                                }
                                st_mob=obj.getString("mob");
                                st_card=obj.getString("card");
                            } else {
                                Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        AlertDialog.Builder builderDel = new AlertDialog.Builder(MainActivity.this);
                        builderDel.setCancelable(false);
                        builderDel.setMessage("Network Error, Try Again Later.");
                        builderDel.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });
                        builderDel.create().show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("acc",SharedPrefManager.getInstance(MainActivity.this).getAccNo());
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
        requestQueue.add(stringRequest);

        requestQueue.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<Object>() {
            @Override
            public void onRequestFinished(Request<Object> request) {
                requestQueue.getCache().clear();
            }
        });
    }

    public void check_upi(String result){
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                Constants.URL_CHECK_UPI,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        try {
                            JSONObject obj = new JSONObject(response);
                            if (!obj.getBoolean("error")) {
                                if (result.equals("register")){
                                    Intent intent = new Intent(MainActivity.this, activate_upi.class);
                                    intent.putExtra("mob",st_mob);
                                    intent.putExtra("card",st_card);
                                    startActivity(intent);
                                }else {
                                    AlertDialog.Builder builderDel = new AlertDialog.Builder(MainActivity.this);
                                    builderDel.setMessage("You need to activate UPI before use...");
                                    builderDel.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            dialogInterface.dismiss();
                                        }
                                    });
                                    AlertDialog dialog = builderDel.create();
                                    dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                                        @Override
                                        public void onShow(DialogInterface arg0) {
                                            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.alert));
                                        }
                                    });
                                    dialog.show();
                                }
                            } else {
                                if (result.equals("manage")){
                                    Intent intent = new Intent(MainActivity.this, manage_upi.class);
                                    intent.putExtra("mob",st_mob);
                                    startActivity(intent);
                                }else  if (result.equals("send")){
                                    startActivity(new Intent(MainActivity.this, u_fund_transfer.class));
                                } else  if (result.equals("scan")){
                                    startActivity(new Intent(MainActivity.this, u_scan_qr.class));
                                } else  if (result.equals("loan")){
                                    startActivity(new Intent(MainActivity.this, u_apply_loan.class));
                                }else  if (result.equals("register")){
                                    AlertDialog.Builder builderDel = new AlertDialog.Builder(MainActivity.this);
                                    builderDel.setTitle("Activate UPI");
                                    builderDel.setMessage("UPI Already Activated...");
                                    builderDel.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            dialogInterface.dismiss();
                                        }
                                    });
                                    AlertDialog dialog = builderDel.create();
                                    dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                                        @Override
                                        public void onShow(DialogInterface arg0) {
                                            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.alert));
                                        }
                                    });
                                    dialog.show();
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        AlertDialog.Builder builderDel = new AlertDialog.Builder(MainActivity.this);
                        builderDel.setCancelable(false);
                        builderDel.setMessage("Network Error, Try Again Later.");
                        builderDel.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });
                        builderDel.create().show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("acc", SharedPrefManager.getInstance(MainActivity.this).getAccNo());
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
        requestQueue.add(stringRequest);

        requestQueue.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<Object>() {
            @Override
            public void onRequestFinished(Request<Object> request) {
                requestQueue.getCache().clear();
            }
        });
    }

    public void u_logout(){
        AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);
        builder1.setTitle("Logout");
        builder1.setMessage("Are you sure, You want to logout?");
        builder1.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                SharedPrefManager.getInstance(MainActivity.this).logout();
                finishAffinity();
                startActivity(new Intent(MainActivity.this, home.class));
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == CAMERA_PERMISSION_CODE)  {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            } else {
                permi();
            }
        }
        else if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            } else {
                permi();
            }
        }
    }

    private  boolean checkAndRequestPermissions() {
        if (ContextCompat.checkSelfPermission(MainActivity.this,Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(MainActivity.this,Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
//            Toast.makeText(home.this, "You have already granted this permission!",Toast.LENGTH_SHORT).show();
            check_upi("scan");
            return true;
        } else {
            int camera = ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA);
            int storage = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
            List<String> listPermissionsNeeded = new ArrayList<>();

            if (camera != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(android.Manifest.permission.CAMERA);
            }
            if (storage != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE);
            }
            if (!listPermissionsNeeded.isEmpty()) {
                ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray
                        (new String[listPermissionsNeeded.size()]), REQUEST_ID_MULTIPLE_PERMISSIONS);
                return false;
            }
            return true;
        }
    }

    public void permi(){
        AlertDialog.Builder builderDel = new AlertDialog.Builder(MainActivity.this);
        builderDel.setCancelable(false);
        builderDel.setTitle("Permissions requires");
        builderDel.setMessage("Requires permission before use.");
        builderDel.setPositiveButton("ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                intent.setData(Uri.parse("package:" + getPackageName()));
                startActivity(intent);
                MainActivity.super.onBackPressed();
            }
        });
        builderDel.create().show();
    }
}