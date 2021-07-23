package com.example.banking.manager;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.banking.MainActivity;
import com.example.banking.R;
import com.example.banking.cashier.c_cheque_details;
import com.example.banking.home;
import com.example.banking.u_apply_cheque;
import com.example.banking.u_apply_loan;
import com.example.banking.u_change_l_pin;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class m_calculate_interest extends AppCompatActivity {
    private TextView t_acc,t_emp,t_acc1,t_emp1,rate;
    private Button daily,monthly,pay_sal;
    private ProgressDialog progressDialog;
    private String d_date,m_date,s_date,timeStamp,st_rate;
    private SimpleDateFormat dateFormat;
    private ImageView im_d,im_m,im_sal;
    private CardView c_t_emp,c_t_acc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_m_calculate_interest);

        if(!SharedPrefManager.getInstance(this).isLoggedMan()){
            finish();
            startActivity(new Intent(this, home.class));
            return;
        }

        getSupportActionBar().setTitle(Html.fromHtml("<font color=#000000>Interest & Salary:</font>"));
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#ADF48004")));

        t_acc1=findViewById(R.id.tv_t_acc1);
        t_emp1=findViewById(R.id.tv_t_emp1);
        t_acc=findViewById(R.id.tv_t_acc);
        t_emp=findViewById(R.id.tv_t_emp);
        daily=findViewById(R.id.bt_c_d);
        monthly=findViewById(R.id.bt_c_m);
        pay_sal=findViewById(R.id.bt_pay_sal);
        im_d=findViewById(R.id.im_d);
        im_m=findViewById(R.id.im_m);
        im_sal=findViewById(R.id.im_sal);
        c_t_emp=findViewById(R.id.c_t_emp);
        c_t_acc=findViewById(R.id.c_t_acc);
        rate=findViewById(R.id.tv_rate);

        t_acc1.setPaintFlags(t_acc.getPaintFlags() |  Paint.UNDERLINE_TEXT_FLAG);
        t_emp1.setPaintFlags(t_emp.getPaintFlags() |  Paint.UNDERLINE_TEXT_FLAG);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);

        dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        timeStamp = new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime());

        refresh();

        c_t_emp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),m_list_emp_user.class));
            }
        });
        c_t_acc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),m_list_emp_user.class));
            }
        });

        daily.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builderDel = new AlertDialog.Builder(m_calculate_interest.this);
                builderDel.setMessage("Are you sure, you want to calculate daily interest?");
                builderDel.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        c_interest("daily");
                    }
                });
                builderDel.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                builderDel.create().show();
            }
        });

        monthly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name1 = ((Button) monthly).getText().toString();
                AlertDialog.Builder builderDel = new AlertDialog.Builder(m_calculate_interest.this);
                builderDel.setMessage("Are you sure, you want to calculate monthly interest?");
                builderDel.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        c_interest("monthly");
                    }
                });
                builderDel.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                builderDel.create().show();
            }
        });

        pay_sal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builderDel = new AlertDialog.Builder(m_calculate_interest.this);
                builderDel.setMessage("Are you sure, you want to pay salary?");
                builderDel.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        c_interest("salary");
                    }
                });
                builderDel.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                builderDel.create().show();
            }
        });
    }
    public void refresh(){
        progressDialog.show();
        StringRequest stringRequest=new StringRequest(Request.Method.POST,Constants.URL_TOTAL_COUNT, new Response.Listener<String>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();
                try {
                    JSONObject obj = new JSONObject(response);
                    if (!obj.getBoolean("error")) {
                        t_acc.setText(obj.getString("count"));
                        t_emp.setText(obj.getString("count1"));
                        d_date=obj.getString("d_date");
                        m_date=obj.getString("m_date");
                        s_date=obj.getString("s_date");
                        st_rate=obj.getString("rate");

                        rate.setText(Html.fromHtml("Current rate of interest :<b>"+" "+st_rate+"%</b>"));

                        Calendar c1 = Calendar.getInstance();
                        Calendar c2 = Calendar.getInstance();
                        c1.setTime(dateFormat.parse(d_date));
                        c2.setTime(dateFormat.parse(timeStamp));
                        int dayDiff = c1.get(Calendar.DAY_OF_MONTH) - c2.get(Calendar.DAY_OF_MONTH);
                        if(dayDiff == 0 ) {
                            daily.setEnabled(false);
                            im_d.setImageResource(R.drawable.tick4);
                        }else {
                            im_d.setImageResource(R.drawable.tick3);
                            daily.setEnabled(true);
                        }

                        Calendar c3 = Calendar.getInstance();
                        Calendar c4 = Calendar.getInstance();
                        c3.setTime(dateFormat.parse(m_date));
                        c4.setTime(dateFormat.parse(timeStamp));
                        int dayMonth = c3.get(Calendar.MONTH) - c4.get(Calendar.MONTH);
                        if(dayMonth == 0 ) {
                            monthly.setEnabled(false);
                            im_m.setImageResource(R.drawable.tick4);
                        }else {
                            im_m.setImageResource(R.drawable.tick3);
                            monthly.setEnabled(true);
                        }

                        Calendar c5 = Calendar.getInstance();
                        Calendar c6 = Calendar.getInstance();
                        c5.setTime(dateFormat.parse(s_date));
                        c6.setTime(dateFormat.parse(timeStamp));
                        int c_dayMonth = c5.get(Calendar.MONTH) - c6.get(Calendar.MONTH);

                        if(c_dayMonth == 0 ) {
                            pay_sal.setEnabled(false);
                            im_sal.setImageResource(R.drawable.tick4);
                        }else {
                            im_sal.setImageResource(R.drawable.tick3);
                            pay_sal.setEnabled(true);
                        }
                    }
                }catch (JSONException | ParseException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.hide();
                AlertDialog.Builder builderDel = new AlertDialog.Builder(m_calculate_interest.this);
                builderDel.setCancelable(false);
                builderDel.setMessage("Network Error, Try Again Later.");
                builderDel.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        finish();
                    }
                });
                builderDel.create().show();
            }
        }){
            protected Map<String , String> getParams() throws AuthFailureError {
                Map<String , String> params = new HashMap<>();
                params.put("id",SharedPrefManager.getInstance(m_calculate_interest.this).getMId());
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(m_calculate_interest.this);
        requestQueue.add(stringRequest);

        requestQueue.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<Object>() {
            @Override
            public void onRequestFinished(Request<Object> request) {
                requestQueue.getCache().clear();
            }
        });
    }

    public void c_interest(String st){
        progressDialog.show();
        StringRequest stringRequest=new StringRequest(Request.Method.POST,Constants.URL_CALCULATE_INTEREST, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();
                try {
                    JSONObject obj = new JSONObject(response);
                    if (!obj.getBoolean("error")) {
                        AlertDialog.Builder builderDel = new AlertDialog.Builder(m_calculate_interest.this);
                        builderDel.setMessage(obj.getString("message"));
                        builderDel.setCancelable(false);
                        builderDel.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });
                        builderDel.create().show();
                    }else{
                        AlertDialog.Builder builderDel = new AlertDialog.Builder(m_calculate_interest.this);
                        builderDel.setMessage(obj.getString("message"));
                        builderDel.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });
                        builderDel.create().show();
                    }
                }catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.hide();
                AlertDialog.Builder builderDel = new AlertDialog.Builder(m_calculate_interest.this);
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
        }){
            protected Map<String , String> getParams() throws AuthFailureError {
                Map<String , String> params = new HashMap<>();
                params.put("date",timeStamp);
                params.put("txt",st);
                params.put("rate",st_rate);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(m_calculate_interest.this);
        requestQueue.add(stringRequest);

        requestQueue.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<Object>() {
            @Override
            public void onRequestFinished(Request<Object> request) {
                requestQueue.getCache().clear();
            }
        });
    }
}