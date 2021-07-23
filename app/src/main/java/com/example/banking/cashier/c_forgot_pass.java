package com.example.banking.cashier;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
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
import com.example.banking.home;
import com.example.banking.u_new_user;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

public class c_forgot_pass extends AppCompatActivity {
    private EditText mob,otp,new_p,c_p;
    private Button send_otp,verify_otp,forgot;
    private ProgressDialog progressDialog;
    private TextView tv_otp;
    private TextInputLayout otp_v;
    private String selected="Select Type...",st_mob;
    private LinearLayout ll_verify,ll_change;
    private Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_c_forgot_pass);

        if(SharedPrefManager.getInstance(this).isLoggedIn1()){
            finish();
            startActivity(new Intent(this, c_dashboard.class));
            return;
        }

        getSupportActionBar().setTitle(Html.fromHtml("<font color=#000000>Forgot Password :</font>"));
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#ADF48004")));

        mob=findViewById(R.id.et_mob);
        otp=findViewById(R.id.et_otp);
        send_otp=findViewById(R.id.bt_send_otp);
        tv_otp=findViewById(R.id.tv_otp);
        otp_v=findViewById(R.id.otp_v);
        verify_otp=findViewById(R.id.bt_verify_otp);
        ll_verify=findViewById(R.id.ll_verify);
        ll_change=findViewById(R.id.ll_change);
        new_p=findViewById(R.id.et_new_p);
        c_p=findViewById(R.id.et_c_p);
        forgot=findViewById(R.id.bt_forgot);
        spinner = findViewById(R.id.spin_bank);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);

        final String[] types = {"Select Type...","Manager", "Cashier"};
        ArrayAdapter<CharSequence> langAdapter = new ArrayAdapter<CharSequence>(c_forgot_pass.this, R.layout.spin_text, types);
        langAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown);
        spinner.setAdapter(langAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (adapterView.getId()==R.id.spin_bank) {
                    selected = adapterView.getSelectedItem().toString();
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {  }
        });

        send_otp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                st_mob= mob.getText().toString().trim();
                if (st_mob.length() != 10){
                mob.setError("Enter Mobile Number");
                }else if (selected.equals("Select Type...")){
                    Toast.makeText(c_forgot_pass.this,"Please Select Type.", Toast.LENGTH_SHORT).show();
                }else{
                    progressDialog.show();
                    StringRequest stringRequest = new StringRequest(
                            Request.Method.POST,
                            Constants.URL_FORGOT_PASS,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    progressDialog.dismiss();
                                    try {
                                        JSONObject obj = new JSONObject(response);
                                        if (!obj.getBoolean("error")) {
                                            tv_otp.setText(obj.getString("otp"));
                                            otp_v.setVisibility(View.VISIBLE);
                                            send_otp.setVisibility(View.GONE);
                                            verify_otp.setVisibility(View.VISIBLE);
                                            mob.setEnabled(false);
                                        } else {
                                            verify_otp.setVisibility(View.GONE);
                                            otp_v.setVisibility(View.GONE);
                                            send_otp.setVisibility(View.VISIBLE);
                                            AlertDialog.Builder builderDel = new AlertDialog.Builder(c_forgot_pass.this);
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
                                    AlertDialog.Builder builderDel = new AlertDialog.Builder(c_forgot_pass.this);
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
                            params.put("mob", st_mob);
                            params.put("otp", "st_mob");
                            params.put("txt", "get_otp");
                            params.put("txt1", selected);
                            return params;
                        }
                    };
                    RequestQueue requestQueue = Volley.newRequestQueue(c_forgot_pass.this);
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
        verify_otp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final  String st_otp=otp.getText().toString().trim();
                final  String st_otp1=st_otp.replaceAll("  ", "");
                final  String st_otp2=st_otp1.replaceAll("-", "");
                if (st_otp2.length() != 6){
                otp.setError("Enter Received OTP");
                }else{
                    progressDialog.show();
                    StringRequest stringRequest = new StringRequest(
                            Request.Method.POST,
                            Constants.URL_FORGOT_PASS,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    progressDialog.dismiss();
                                    try {
                                        JSONObject obj = new JSONObject(response);
                                        if (!obj.getBoolean("error")) {
                                            ll_verify.setVisibility(View.GONE);
                                            ll_change.setVisibility(View.VISIBLE);
                                        } else {
                                            otp.setText("");
                                            AlertDialog.Builder builderDel = new AlertDialog.Builder(c_forgot_pass.this);
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
                                    AlertDialog.Builder builderDel = new AlertDialog.Builder(c_forgot_pass.this);
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
                            params.put("mob", st_mob);
                            params.put("otp", st_otp2);
                            params.put("txt", "verify");
                            params.put("txt1", selected);
                            return params;
                        }
                    };
                    RequestQueue requestQueue = Volley.newRequestQueue(c_forgot_pass.this);
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
        forgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final  String st_pass=new_p.getText().toString().trim();
                final  String st_cpass=c_p.getText().toString().trim();
                if (st_pass.isEmpty()){
                    new_p.setError("Enter Password");
                }else if (st_cpass.isEmpty()){
                    new_p.setError("Enter Confirm Password");
                }else if (st_cpass.equals(st_pass)){

                    progressDialog.show();
                    StringRequest stringRequest = new StringRequest(
                            Request.Method.POST,
                            Constants.URL_FORGOT_PASS,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    progressDialog.dismiss();
                                    try {
                                        JSONObject obj = new JSONObject(response);
                                        if (!obj.getBoolean("error")) {
                                            AlertDialog.Builder builderDel = new AlertDialog.Builder(c_forgot_pass.this);
                                            builderDel.setCancelable(false);
                                            builderDel.setMessage(obj.getString("message"));
                                            builderDel.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    dialogInterface.dismiss();
                                                    finishAffinity();
                                                    startActivity(new Intent(getApplicationContext(), home.class));
                                                }
                                            });
                                            builderDel.create().show();
                                        } else {
                                            otp.setText("");
                                            AlertDialog.Builder builderDel = new AlertDialog.Builder(c_forgot_pass.this);
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
                                    AlertDialog.Builder builderDel = new AlertDialog.Builder(c_forgot_pass.this);
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
                            params.put("mob", st_mob);
                            params.put("otp", st_pass);
                            params.put("txt", "forgot");
                            params.put("txt1", selected);
                            return params;
                        }
                    };
                    RequestQueue requestQueue = Volley.newRequestQueue(c_forgot_pass.this);
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
                    Toast.makeText(c_forgot_pass.this,"Password doesn't Match",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}