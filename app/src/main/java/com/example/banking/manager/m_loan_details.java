package com.example.banking.manager;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
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
import com.example.banking.cashier.c_manage_cheque;
import com.example.banking.home;
import com.example.banking.u_apply_loan;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class m_loan_details extends AppCompatActivity {
    private TextView acc,name,bal,mob,l_type,l_rate,tv_l_amount,l_dur,l_emi,l_inte,l_total,l_status,e_emp,e_pro,e_income;
    private Button confirm,reject;
    private ProgressDialog progressDialog;
    private DecimalFormat formatter;
    private String st_bal,st_id,st_l_status;
    private String st_dura,st_emi,st_total,st_l_amount,st_acc,st_pro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_m_loan_details);

        if(!SharedPrefManager.getInstance(this).isLoggedMan()){
            finish();
            startActivity(new Intent(this, home.class));
            return;
        }

        getSupportActionBar().setTitle(Html.fromHtml("<font color=#000000>Loan Details :</font>"));
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#ADF48004")));

        name=findViewById(R.id.tv_name);
        acc=findViewById(R.id.tv_acc);
        bal=findViewById(R.id.tv_bal);
        mob=findViewById(R.id.tv_mob);
        confirm=findViewById(R.id.bt_conf);
        reject=findViewById(R.id.bt_rej);

        l_type=findViewById(R.id.tv_l_type);
        l_rate=findViewById(R.id.tv_l_rate);
        tv_l_amount=findViewById(R.id.tv_l_amount);
        l_dur=findViewById(R.id.tv_l_dura);
        l_emi=findViewById(R.id.tv_l_emi);
        l_inte=findViewById(R.id.tv_l_inte);
        l_total=findViewById(R.id.tv_l_total);
        l_status=findViewById(R.id.tv_l_status);
        e_emp=findViewById(R.id.tv_emp);
        e_pro=findViewById(R.id.tv_e_pro);
        e_income=findViewById(R.id.tv_e_income);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);

        formatter =new DecimalFormat("##,##,###.00");

        st_id = getIntent().getStringExtra("id");

        progressDialog.show();
        StringRequest stringRequest=new StringRequest(Request.Method.POST,Constants.URL_LOAN_OPERATIONS, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();
                try {
                    JSONObject obj = new JSONObject(response);
                    if (!obj.getBoolean("error")) {
                        st_acc=obj.getString("acc");
                        acc.setText(st_acc);
                        name.setText(obj.getString("name"));
                        mob.setText(obj.getString("mob"));
                        st_bal=obj.getString("bal");

                        l_type.setText(obj.getString("type"));
                        l_rate.setText(Html.fromHtml(obj.getString("rate") + "%"));
                        st_l_amount=obj.getString("l_amount");
                        tv_l_amount.setText("₹ "+formatter.format(Long.parseLong(st_l_amount))+"/-");
                        st_dura=obj.getString("dura");
                        l_dur.setText(st_dura);
                        st_emi=obj.getString("emi");
                        l_emi.setText("₹ "+formatter.format(Long.parseLong(st_emi))+"/-");
                        l_inte.setText("₹ "+formatter.format(Long.parseLong(obj.getString("inte")))+"/-");
                        st_total=obj.getString("total");
                        l_total.setText("₹ "+formatter.format(Long.parseLong(st_total))+"/-");
                        st_l_status=obj.getString("status");
                        e_emp.setText(obj.getString("emp"));
                        st_pro=obj.getString("pro");
                        e_income.setText("₹ "+formatter.format(Long.parseLong(obj.getString("income")))+"/-");

                        if (st_pro.equals("null"))
                            e_pro.setText("NA");
                        else
                            e_pro.setText(st_pro);

                        if (st_bal.equals("0")){
                            bal.setText("0.00/-");
                        }else {
                            bal.setText("₹ "+formatter.format(Long.parseLong(st_bal))+"/-");
                        }

                        if (st_l_status.equals("In")){
                            l_status.setText(Html.fromHtml("<font color=#2EB83D><b>Pending</font></b>"));
                        }
                    }else{
                        AlertDialog.Builder builderDel = new AlertDialog.Builder(m_loan_details.this);
                        builderDel.setMessage(obj.getString("message"));
                        builderDel.setCancelable(false);
                        builderDel.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                                finishAffinity();
                                startActivity(new Intent(m_loan_details.this, MainActivity.class));
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
                AlertDialog.Builder builderDel = new AlertDialog.Builder(m_loan_details.this);
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
                params.put("txt","details");
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(m_loan_details.this);
        requestQueue.add(stringRequest);

        requestQueue.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<Object>() {
            @Override
            public void onRequestFinished(Request<Object> request) {
                requestQueue.getCache().clear();
            }
        });

        confirm.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                int total1 = Integer.parseInt(st_dura);
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                Date date = new Date();
                Calendar c = Calendar.getInstance();
                c.setTime(date);
                c.add(Calendar.YEAR, total1);
                Date currentDatePlusOne = c.getTime();
//                t_payment.setText(dateFormat.format(currentDatePlusOne));

                String timeStamp = new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime());

                AlertDialog.Builder builderDel = new AlertDialog.Builder(m_loan_details.this);
                builderDel.setMessage("Are you sure, you want to confirm request");
                builderDel.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        progressDialog.show();
                        StringRequest stringRequest=new StringRequest(Request.Method.POST,Constants.URL_CONFIRM_LOAN, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                progressDialog.dismiss();
                                try {
                                    JSONObject obj = new JSONObject(response);
                                    if (!obj.getBoolean("error")) {
                                        AlertDialog.Builder builderDel = new AlertDialog.Builder(m_loan_details.this);
                                        builderDel.setMessage(obj.getString("message"));
                                        builderDel.setCancelable(false);
                                        builderDel.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                PendingFragement.lr.refresh_list();
                                                FinishedFragement.ff.refresh_list();
                                                finish();
                                            }
                                        });
                                        builderDel.create().show();
                                    } else {
                                        AlertDialog.Builder builderDel = new AlertDialog.Builder(m_loan_details.this);
                                        builderDel.setMessage(obj.getString("message"));
                                        builderDel.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                dialogInterface.dismiss();
                                            }
                                        });
                                        builderDel.create().show();
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
                                  AlertDialog.Builder builderDel = new AlertDialog.Builder(m_loan_details.this);
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
                                params.put("r_id", st_id);
                                params.put("l_emi", st_emi);
                                params.put("l_total", st_total);
                                params.put("s_date", timeStamp);
                                params.put("w_date", timeStamp);
                                params.put("e_date", dateFormat.format(currentDatePlusOne));
                                params.put("l_amo", st_l_amount);
                                params.put("acc", st_acc);
                                return params;
                            }
                        };
                        RequestQueue requestQueue = Volley.newRequestQueue(m_loan_details.this);
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

        reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builderDel = new AlertDialog.Builder(m_loan_details.this);
                builderDel.setMessage("Are you sure, you want to reject request");
                builderDel.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                       dialogInterface.dismiss();
                        progressDialog.show();
                        StringRequest stringRequest=new StringRequest(Request.Method.POST,Constants.URL_LOAN_OPERATIONS, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                progressDialog.dismiss();
                                try {
                                    JSONObject obj = new JSONObject(response);
                                    if (!obj.getBoolean("error")) {
                                        AlertDialog.Builder builderDel = new AlertDialog.Builder(m_loan_details.this);
                                        builderDel.setMessage(obj.getString("message"));
                                        builderDel.setCancelable(false);
                                        builderDel.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                PendingFragement.lr.refresh_list();
                                                finish();
                                            }
                                        });
                                        builderDel.create().show();
                                    } else {
                                        AlertDialog.Builder builderDel = new AlertDialog.Builder(m_loan_details.this);
                                        builderDel.setMessage(obj.getString("message"));
                                        builderDel.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                dialogInterface.dismiss();
                                            }
                                        });
                                        builderDel.create().show();
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
                                        AlertDialog.Builder builderDel = new AlertDialog.Builder(m_loan_details.this);
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
                                params.put("id", st_id);
                                params.put("txt", "reject");
                                return params;
                            }
                        };
                        RequestQueue requestQueue = Volley.newRequestQueue(m_loan_details.this);
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
    }
}