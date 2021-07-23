package com.example.banking.manager;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.banking.R;
import com.example.banking.cashier.c_dashboard;
import com.example.banking.home;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class m_profile_change extends AppCompatActivity {
    private String st_rmk;
    private TextView b_name,b_branch,b_ifsc,b_address,m_name,m_mob,m_addres,m_email;
    private Button change;
    private EditText old_p,new_p,c_p;
    private ProgressDialog progressDialog;
    private LinearLayout ll_profile,ll_change;
    private ScrollView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_m_profile_change);

        if(!SharedPrefManager.getInstance(this).isLoggedMan()){
            finish();
            startActivity(new Intent(this, home.class));
            return;
        }

        b_name=findViewById(R.id.tv_b_name);
        b_branch=findViewById(R.id.tv_b_branch);
        b_ifsc=findViewById(R.id.tv_b_ifsc);
        b_address=findViewById(R.id.tv_b_address);
        m_name=findViewById(R.id.tv_m_name);
        m_mob=findViewById(R.id.tv_m_mob);
        m_addres=findViewById(R.id.tv_m_address);
        m_email=findViewById(R.id.tv_m_email);
        ll_profile=findViewById(R.id.ll_profile);
        ll_change=findViewById(R.id.ll_change);
        old_p=findViewById(R.id.et_old_p);
        new_p=findViewById(R.id.et_new_p);
        c_p=findViewById(R.id.et_c_p);
        change=findViewById(R.id.bt_change);
        back=findViewById(R.id.s_back);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            st_rmk=bundle.getString("rmk");
        }

        if (st_rmk.equals("change")){
            getSupportActionBar().setTitle(Html.fromHtml("<font color=#000000>Change Password </font>"));
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#ADF48004")));
            ll_change.setVisibility(View.VISIBLE);
        }else {
            getSupportActionBar().setTitle(Html.fromHtml("<font color=#000000>Profile </font>"));
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#ADF48004")));
            back.setBackgroundResource(R.drawable.gradient);
            ll_profile.setVisibility(View.VISIBLE);
            profile();
        }

        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final  String st_opass=old_p.getText().toString().trim();
                final  String st_npass=new_p.getText().toString().trim();
                final  String st_cpass=c_p.getText().toString().trim();
                if (st_opass.isEmpty()){
                    old_p.setError("Enter Old Password");
                }else if (st_npass.isEmpty()){
                    new_p.setError("Enter New Password");
                }else if (st_cpass.isEmpty()){
                    c_p.setError("Enter Confirm Password");
                }else if (st_npass.equals(st_cpass)){

                    progressDialog.show();
                    StringRequest stringRequest=new StringRequest(Request.Method.POST,Constants.URL_CHANGE_PROFILE, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            progressDialog.dismiss();
                            try {
                                        JSONObject obj = new JSONObject(response);
                                        if (!obj.getBoolean("error")) {
                                            AlertDialog.Builder builderDel = new AlertDialog.Builder(m_profile_change.this);
                                            builderDel.setCancelable(false);
                                            builderDel.setMessage(obj.getString("message"));
                                            builderDel.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    dialogInterface.dismiss();
                                                    finish();
                                                }
                                            });
                                            builderDel.create().show();
                                        } else {
                                            old_p.setText("");
                                            AlertDialog.Builder builderDel = new AlertDialog.Builder(m_profile_change.this);
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
                                    AlertDialog.Builder builderDel = new AlertDialog.Builder(m_profile_change.this);
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
                            params.put("mob", SharedPrefManager.getInstance(m_profile_change.this).getMId());
                            params.put("opass", st_opass);
                            params.put("npass", st_npass);
                            params.put("txt", "change");
                            return params;
                        }
                    };
                    RequestQueue requestQueue = Volley.newRequestQueue(m_profile_change.this);
                    requestQueue.add(stringRequest);

                    requestQueue.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<Object>() {
                        @Override
                        public void onRequestFinished(Request<Object> request) {
                            requestQueue.getCache().clear();
                        }
                    });
                }else {
                    new_p.setText("");
                    c_p.setText("");
                    Toast.makeText(m_profile_change.this,"Password doesn't Match",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void profile(){
        progressDialog.show();
        StringRequest stringRequest=new StringRequest(Request.Method.POST,Constants.URL_CHANGE_PROFILE, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();
                try {
                            JSONObject obj = new JSONObject(response);
                            if (!obj.getBoolean("error")) {
                                b_name.setText(obj.getString("b_name"));
                                b_branch.setText(obj.getString("b_branch"));
                                b_ifsc.setText(obj.getString("b_ifsc"));
                                b_address.setText(obj.getString("b_address"));
                                m_name.setText(obj.getString("m_name"));
                                m_mob.setText(obj.getString("m_mob"));
                                m_addres.setText(obj.getString("m_addres"));
                                m_email.setText(obj.getString("m_email"));
                            } else {
                                AlertDialog.Builder builderDel = new AlertDialog.Builder(m_profile_change.this);
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
                        AlertDialog.Builder builderDel = new AlertDialog.Builder(m_profile_change.this);
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
                params.put("mob", SharedPrefManager.getInstance(m_profile_change.this).getMId());
                params.put("opass", "st_npass");
                params.put("npass", "st_npass");
                params.put("txt", "profile");
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(m_profile_change.this);
        requestQueue.add(stringRequest);

        requestQueue.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<Object>() {
            @Override
            public void onRequestFinished(Request<Object> request) {
                requestQueue.getCache().clear();
            }
        });
    }
}