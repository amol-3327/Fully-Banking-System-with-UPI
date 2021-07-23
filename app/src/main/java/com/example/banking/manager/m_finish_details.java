package com.example.banking.manager;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
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
import com.example.banking.home;
import com.example.banking.u_apply_loan;

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

public class m_finish_details extends AppCompatActivity {
    private TextView acc,mob,l_type,l_rate,tv_l_amount,l_status,l_start,l_end,l_out,emi,interest,t_payment;
    private Button finish,c_emi,extend;
    private ProgressDialog progressDialog;
    private DecimalFormat formatter;
    private String st_id,st_acc,st_rate,st_dura,st_emi,st_total,st_l_amount,st_inte,st_out,st_start,st_end,st_working,r_id,selected_no;
    private String ss_emi,ss_inte,ss_total;
    private SimpleDateFormat dateFormat;
    private LinearLayout hiddenView,ll_det;
    private ImageButton arrow;
    private CardView cardView,ll_extend;
    private Spinner spin_no;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_m_finish_details);

        if(!SharedPrefManager.getInstance(this).isLoggedMan()){
            finish();
            startActivity(new Intent(this, home.class));
            return;
        }

        getSupportActionBar().setTitle(Html.fromHtml("<font color=#000000>Running Loan Details :</font>"));
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#ADF48004")));

        acc=findViewById(R.id.tv_acc);
        mob=findViewById(R.id.tv_mob);
        l_type=findViewById(R.id.tv_l_type);
        l_rate=findViewById(R.id.tv_l_rate);
        tv_l_amount=findViewById(R.id.tv_l_amount);
        l_status=findViewById(R.id.tv_l_status);
        l_start=findViewById(R.id.tv_l_start);
        l_end=findViewById(R.id.tv_l_end);
        l_out=findViewById(R.id.tv_l_out);
        finish=findViewById(R.id.bt_finish);
        ll_extend=findViewById(R.id.ll_extend);
        cardView = findViewById(R.id.base_cardview);
        arrow = findViewById(R.id.arrow_button);
        hiddenView = findViewById(R.id.hidden_view);
        c_emi=findViewById(R.id.bt_emi);
        spin_no = findViewById(R.id.spin_no);
        emi=findViewById(R.id.tv_emi);
        interest=findViewById(R.id.tv_inter);
        t_payment=findViewById(R.id.tv_pay);
        ll_det=findViewById(R.id.ll_det);
        extend=findViewById(R.id.bt_extend);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);

        formatter =new DecimalFormat("##,##,###.00");
        dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        final String[] no = {"1","2","3","4","5","6","7","8","9","10"};
        ArrayAdapter<CharSequence> langAdapter1 = new ArrayAdapter<CharSequence>(m_finish_details.this, R.layout.spin_text, no);
        langAdapter1.setDropDownViewResource(R.layout.simple_spinner_dropdown);
        spin_no.setAdapter(langAdapter1);

        st_id = getIntent().getStringExtra("id");

        spin_no.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selected_no = adapterView.getSelectedItem().toString();
                ll_det.setVisibility(View.GONE);
                emi.setText("");
                interest.setText("");
                t_payment.setText("");
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {  }
        });

        arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (hiddenView.getVisibility() == View.VISIBLE) {
                    TransitionManager.beginDelayedTransition(cardView,new AutoTransition());
                    hiddenView.setVisibility(View.GONE);
                    arrow.setImageResource(R.drawable.ic_baseline_expand_more_24);
                }else {
                    TransitionManager.beginDelayedTransition(cardView,new AutoTransition());
                    hiddenView.setVisibility(View.VISIBLE);
                    arrow.setImageResource(R.drawable.ic_baseline_expand_less_24);
                }
            }
        });

        progressDialog.show();
        StringRequest stringRequest=new StringRequest(Request.Method.POST,Constants.URL_LOAN_OPERATIONS, new Response.Listener<String>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();
                try {
                    JSONObject obj = new JSONObject(response);
                    if (!obj.getBoolean("error")) {
                        st_acc=obj.getString("acc");
                        acc.setText(st_acc);
                        mob.setText(obj.getString("mob"));
                        l_type.setText(obj.getString("type"));
                        st_rate=obj.getString("rate");
                        l_rate.setText(Html.fromHtml( st_rate + "%"));
                        st_l_amount=obj.getString("l_amount");
                        tv_l_amount.setText("₹ "+formatter.format(Long.parseLong(st_l_amount))+"/-");

                        st_dura=obj.getString("dura");
                        st_emi=obj.getString("emi");
                        st_inte=obj.getString("inte");
                        st_total=obj.getString("total");
                        st_start=obj.getString("start");
                        st_end=obj.getString("end");
                        st_out=obj.getString("out");
                        st_working=obj.getString("working");
                        r_id=obj.getString("r_id");

                        String timeStamp = new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime());
                        Date d1 = dateFormat.parse(timeStamp);
                        Date d2 = dateFormat.parse(st_end);
                        Date d3 = dateFormat.parse(st_start);

                        SimpleDateFormat sdf1 = new SimpleDateFormat("dd-MMMM-yyyy");
                        l_start.setText(sdf1.format(d3));
                        l_end.setText(sdf1.format(d2));

                        LocalDate date1 =d1.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                        LocalDate date2 = d2.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                        Period p = Period.between(date1, date2);
                        int year=p.getYears();
                        int month=p.getMonths();
                        if (st_out.equals("0")){
                            l_status.setText(Html.fromHtml("<font color=#2EB83D><b>Finished</font></b>"));
                            l_out.setText("₹ 0.00/-");
                            finish.setVisibility(View.VISIBLE);
                        }else {
                            if((year == 0 && month == 0) || (year < 0 || month < 0)) {
                                l_status.setText(Html.fromHtml("<b><font color=#2EB83D> Date exhausted </font></b>"));
                                l_out.setText("₹ "+formatter.format(Long.parseLong(st_out))+"/-");
                                ll_extend.setVisibility(View.VISIBLE);
                            }else {
                                l_status.setText(Html.fromHtml("<b><font color=#2EB83D> Running </font></b>"));
                                l_out.setText("₹ "+formatter.format(Long.parseLong(st_out))+"/-");
                            }
                        }
                    }else{
                        AlertDialog.Builder builderDel = new AlertDialog.Builder(m_finish_details.this);
                        builderDel.setMessage(obj.getString("message"));
                        builderDel.setCancelable(false);
                        builderDel.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                                finishAffinity();
                                startActivity(new Intent(m_finish_details.this, MainActivity.class));
                            }
                        });
                        builderDel.create().show();
                    }
                }catch (JSONException | ParseException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.hide();
                AlertDialog.Builder builderDel = new AlertDialog.Builder(m_finish_details.this);
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
                params.put("id",st_id);
                params.put("txt","finish_details");
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(m_finish_details.this);
        requestQueue.add(stringRequest);

        requestQueue.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<Object>() {
            @Override
            public void onRequestFinished(Request<Object> request) {
                requestQueue.getCache().clear();
            }
        });

        c_emi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                float do_rate=Float.parseFloat(st_rate);
                float do_l_amount=Float.parseFloat(st_out);
                float do_duration=Float.parseFloat(selected_no);
                float emi1;
                int total,inte;

                do_rate = do_rate / (12 * 100);
                do_duration = do_duration * 12;
                emi1 = (do_l_amount * do_rate * (float)Math.pow(1 + do_rate, do_duration))
                        / (float)(Math.pow(1 + do_rate, do_duration) - 1);

                String result=String.valueOf(Math.round(emi1));
                total=(Integer.parseInt(result)) *((Integer.parseInt(selected_no)) * 12) ;
                inte=(total - (Integer.parseInt(st_out)));

                emi.setText("₹ "+formatter.format(Long.parseLong(result)));
                interest.setText("₹ "+formatter.format(inte));
                t_payment.setText("₹ "+formatter.format(total));
                ll_det.setVisibility(View.VISIBLE);

                ss_emi=result;
                ss_inte=String.valueOf(inte);
                ss_total=String.valueOf(total);
            }
        });

        extend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builderDel = new AlertDialog.Builder(m_finish_details.this);
                builderDel.setMessage("Are you sure, You want to extend date?");
                builderDel.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();

                        progressDialog.show();
                        Calendar c = Calendar.getInstance();
                        try {
                            c.setTime(dateFormat.parse(st_end));
                        } catch (ParseException parseException) {
                            parseException.printStackTrace();
                        }
                        c.add(Calendar.YEAR, Integer.parseInt(selected_no));
                        Date currentDatePlusOne = c.getTime();

                        StringRequest stringRequest=new StringRequest(Request.Method.POST,Constants.URL_EXTEND_LOAN_DATE, new Response.Listener<String>() {
                            @RequiresApi(api = Build.VERSION_CODES.O)
                            @Override
                            public void onResponse(String response) {
                                progressDialog.dismiss();
                                try {
                                    JSONObject obj = new JSONObject(response);
                                    if (!obj.getBoolean("error")) {
                                        AlertDialog.Builder builderDel = new AlertDialog.Builder(m_finish_details.this);
                                        builderDel.setMessage(obj.getString("message"));
                                        builderDel.setCancelable(false);
                                        builderDel.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                dialogInterface.dismiss();
                                                FinishedFragement.ff.refresh_list();
                                                finish();
                                            }
                                        });
                                        builderDel.create().show();
                                    }else{
                                        AlertDialog.Builder builderDel = new AlertDialog.Builder(m_finish_details.this);
                                        builderDel.setMessage(obj.getString("message"));
                                        builderDel.setCancelable(false);
                                        builderDel.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                dialogInterface.dismiss();
                                                finish();
                                                startActivity(new Intent(m_finish_details.this, MainActivity.class));
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
                                AlertDialog.Builder builderDel = new AlertDialog.Builder(m_finish_details.this);
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
                                params.put("dura",selected_no);
                                params.put("emi",ss_emi);
                                params.put("inte",ss_inte);
                                params.put("total",ss_total);
                                params.put("r_id",r_id);
                                params.put("e_date",dateFormat.format(currentDatePlusOne));
                                return params;
                            }
                        };
                        RequestQueue requestQueue = Volley.newRequestQueue(m_finish_details.this);
                        requestQueue.add(stringRequest);

                        requestQueue.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<Object>() {
                            @Override
                            public void onRequestFinished(Request<Object> request) {
                                requestQueue.getCache().clear();
                            }
                        });
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

        finish.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                progressDialog.show();
                Date d1 = null,d2=null;
                try {
                    d1 = dateFormat.parse(st_working);
                    d2 = dateFormat.parse(st_end);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                LocalDate date1 =d1.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                LocalDate date2 = d2.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                Period p = Period.between(date1, date2);
                int year=p.getYears();
                int month=p.getMonths();
                int re1 = 0,result1 = 0,interest1=0;

                if((year == 0 && month == 0) || (year < 0 || month < 0)) {
                    l_finish(String.valueOf(0),String.valueOf(st_inte));
                }else {
                    if (year > 0){
                        re1=year * 12;
                        result1=re1 + month;
                    }else {
                        result1=year + month;
                    }

                    float do_rate=Float.parseFloat(st_rate);
                    float do_l_amount=Float.parseFloat(st_l_amount);
                    float do_duration=result1;
                    float emi1;
                    int total,inte;

                    do_rate = do_rate / (12 * 100);
                    emi1 = (do_l_amount * do_rate * (float)Math.pow(1 + do_rate, do_duration))
                            / (float)(Math.pow(1 + do_rate, do_duration) - 1);

                    String result=String.valueOf(Math.round(emi1));
                    total=(Integer.parseInt(result)) * result1;
                    inte=(total - (Integer.parseInt(st_l_amount)));
                    interest1=(Integer.parseInt(st_inte)) - inte;

                    l_finish(String.valueOf(inte),String.valueOf(interest1));
                }
            }
        });
    }
    
    public void l_finish(String result1,String inte1){
        StringRequest stringRequest=new StringRequest(Request.Method.POST,Constants.URL_FINISH_LOAN, new Response.Listener<String>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();
                try {
                    JSONObject obj = new JSONObject(response);
                    if (!obj.getBoolean("error")) {
                        AlertDialog.Builder builderDel = new AlertDialog.Builder(m_finish_details.this);
                        builderDel.setMessage(obj.getString("message"));
                        builderDel.setCancelable(false);
                        builderDel.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                                FinishedFragement.ff.refresh_list();
                                finish();
                            }
                        });
                        builderDel.create().show();
                    }else{
                        AlertDialog.Builder builderDel = new AlertDialog.Builder(m_finish_details.this);
                        builderDel.setMessage(obj.getString("message"));
                        builderDel.setCancelable(false);
                        builderDel.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                                finish();
                                startActivity(new Intent(m_finish_details.this, MainActivity.class));
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
                AlertDialog.Builder builderDel = new AlertDialog.Builder(m_finish_details.this);
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
                params.put("acc",st_acc);
                params.put("inte",inte1);
                params.put("amo",result1);
                params.put("r_id",r_id);
                params.put("s_date",st_start);
                params.put("e_date",st_end);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(m_finish_details.this);
        requestQueue.add(stringRequest);

        requestQueue.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<Object>() {
            @Override
            public void onRequestFinished(Request<Object> request) {
                requestQueue.getCache().clear();
            }
        });
    }
}