package com.example.banking.manager;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.Editable;
import android.text.Html;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.banking.R;
import com.example.banking.home;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class m_details_new_type extends AppCompatActivity {
    private String st_id,st_type,st_rate,st_rmk;
    private TextView type,rate;
    private ProgressDialog progressDialog;
    private LinearLayout ll_type,ll_new_type;
    private CardView c_rate;
    Dialog dialog;
    private ScrollView back;
    private EditText et_type,et_rate;
    private Button add;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_m_details_new_type);

        if(!SharedPrefManager.getInstance(this).isLoggedMan()){
            finish();
            startActivity(new Intent(this, home.class));
            return;
        }

        ActionBar ab=getSupportActionBar();
        ab.setTitle(Html.fromHtml("<font color=#000000>Loan Categories :</font>"));
        ab.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#ADF48004")));

        type=findViewById(R.id.tv_type);
        rate=findViewById(R.id.tv_rate);
        c_rate=findViewById(R.id.c_rate);
        ll_type=findViewById(R.id.ll_type);
        ll_new_type=findViewById(R.id.ll_new_type);
        back=findViewById(R.id.s_back);
        et_type=findViewById(R.id.et_type);
        et_rate=findViewById(R.id.et_rate);
        add=findViewById(R.id.bt_add);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            st_id = getIntent().getStringExtra("id");
            st_type = getIntent().getStringExtra("type");
            st_rate = getIntent().getStringExtra("rate");
            st_rmk = getIntent().getStringExtra("rmk");
        }

        if (st_rmk.equals("type")){
            getSupportActionBar().setTitle(Html.fromHtml("<font color=#000000>Loan Type Details </font>"));
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#ADF48004")));
            back.setBackgroundResource(R.drawable.gradient);
            ll_type.setVisibility(View.VISIBLE);

            type.setText(st_type);
            rate.setText(st_rate);
        }else {
            getSupportActionBar().setTitle(Html.fromHtml("<font color=#000000>New Loan Type </font>"));
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#ADF48004")));
            ll_new_type.setVisibility(View.VISIBLE);
        }
        c_rate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog = new Dialog(m_details_new_type.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setCancelable(false);
                final View customLayout = getLayoutInflater().inflate(R.layout.alert_update_details, null);
                dialog.setContentView(customLayout);

                CardView emp_user= (CardView) customLayout.findViewById(R.id.c_emp_user);
                CardView c_type= (CardView) customLayout.findViewById(R.id.c_type);
                TextView type= (TextView) customLayout.findViewById(R.id.tv_type1);
                TextView value= (TextView) customLayout.findViewById(R.id.tv_value1);
                EditText input= (EditText) customLayout.findViewById(R.id.et_input1);
                Button update=customLayout.findViewById(R.id.bt_update1);
                ImageView close=(customLayout).findViewById(R.id.i_close1);

                emp_user.setVisibility(View.GONE);
                c_type.setVisibility(View.VISIBLE);

                type.setText("Current Rate : ");
                value.setText(st_rate);

                close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });

                update.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        AlertDialog.Builder builder1 = new AlertDialog.Builder(m_details_new_type.this);
                        builder1.setMessage("Are you sure, you want to update?");
                        builder1.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                final String data=input.getText().toString().trim();
                                if (data.equals("0") || data.isEmpty()){
                                    input.setError("Enter rate");
                                }else {
                                    progressDialog.show();
                                    StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.URL_NEW_LOAN_TYPE, new Response.Listener<String>() {
                                        @Override
                                        public void onResponse(String response) {
                                            progressDialog.dismiss();
                                            try {
                                                JSONObject obj = new JSONObject(response);
                                                if (!obj.getBoolean("error")) {
                                                    dialog.dismiss();
                                                    AlertDialog.Builder builderDel = new AlertDialog.Builder(m_details_new_type.this);
                                                    builderDel.setMessage(obj.getString("message"));
                                                    builderDel.setCancelable(false);
                                                    builderDel.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialogInterface, int i) {
                                                            dialogInterface.dismiss();
                                                            m_loan_types.mc.refresh_list();
                                                            finish();
                                                        }
                                                    });
                                                    builderDel.create().show();
                                                } else {
                                                    AlertDialog.Builder builderDel = new AlertDialog.Builder(m_details_new_type.this);
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
                                    }, new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {
                                            progressDialog.hide();
                                            AlertDialog.Builder builderDel = new AlertDialog.Builder(m_details_new_type.this);
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
                                    }) {
                                        protected Map<String, String> getParams() throws AuthFailureError {
                                            Map<String, String> params = new HashMap<>();
                                            params.put("txt","update");
                                            params.put("data",data);
                                            params.put("id",st_id);
                                            return params;
                                        }
                                    };
                                    RequestQueue requestQueue = Volley.newRequestQueue(m_details_new_type.this);
                                    requestQueue.add(stringRequest);

                                    requestQueue.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<Object>() {
                                        @Override
                                        public void onRequestFinished(Request<Object> request) {
                                            requestQueue.getCache().clear();
                                        }
                                    });
                                }
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
                });

                dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                dialog.show();
            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String s_type=et_type.getText().toString().trim();
                final String s_rate=et_rate.getText().toString().trim();
                if (s_type.isEmpty()) {
                    et_type.setError("Enter type");
                }else if (s_rate.equals("0") || s_rate.isEmpty()){
                    et_rate.setError("Enter rate");
                }else {
                    progressDialog.show();
                    StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.URL_NEW_LOAN_TYPE, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            progressDialog.dismiss();
                            try {
                                JSONObject obj = new JSONObject(response);
                                if (!obj.getBoolean("error")) {
                                    AlertDialog.Builder builderDel = new AlertDialog.Builder(m_details_new_type.this);
                                    builderDel.setMessage(obj.getString("message"));
                                    builderDel.setCancelable(false);
                                    builderDel.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            dialogInterface.dismiss();
                                            m_loan_types.mc.refresh_list();
                                            finish();
                                        }
                                    });
                                    builderDel.create().show();
                                } else {
                                    AlertDialog.Builder builderDel = new AlertDialog.Builder(m_details_new_type.this);
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
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            progressDialog.hide();
                            AlertDialog.Builder builderDel = new AlertDialog.Builder(m_details_new_type.this);
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
                    }) {
                        protected Map<String, String> getParams() throws AuthFailureError {
                            Map<String, String> params = new HashMap<>();
                            params.put("txt","insert");
                            params.put("data",s_type);
                            params.put("id",s_rate);
                            return params;
                        }
                    };
                    RequestQueue requestQueue = Volley.newRequestQueue(m_details_new_type.this);
                    requestQueue.add(stringRequest);

                    requestQueue.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<Object>() {
                        @Override
                        public void onRequestFinished(Request<Object> request) {
                            requestQueue.getCache().clear();
                        }
                    });
                }
            }
        });
    }
}