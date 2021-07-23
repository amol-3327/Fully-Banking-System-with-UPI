package com.example.banking;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class u_fund_transfer extends AppCompatActivity {
    private RadioGroup radioGroup;
    private RadioButton r_phone;
    private LinearLayout phone,bank;
    private EditText mob,amo,ifsc,acc,cacc,a_name,amount;
    private TextView name;
    private Button send,sendB,button;
    private String s1,st_upi;
    private ProgressDialog progressDialog;
    private TextInputLayout til;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_u_fund_transfer);

        if(!SharedPrefManager.getInstance(this).isLoggedIn()){
            finish();
            startActivity(new Intent(this, home.class));
            return;
        }
        ActionBar ab=getSupportActionBar();
        ab.setTitle("Send Money:");
        ab.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00801EBD")));
        getWindow().setBackgroundDrawableResource(R.mipmap.back2);

        radioGroup = findViewById(R.id.group);
        r_phone = findViewById(R.id.r_phone);
        phone = findViewById(R.id.ll_phone);
        bank = findViewById(R.id.ll_bank);
        mob=findViewById(R.id.et_phone);
        name=findViewById(R.id.tv_name);
        amo=findViewById(R.id.et_amo);
        send=findViewById(R.id.bt_sendP);

        ifsc=findViewById(R.id.et_ifsc);
        acc=findViewById(R.id.et_acno);
        cacc =findViewById(R.id.et_cac);
        a_name=findViewById(R.id.et_hname);
        sendB=findViewById(R.id.bt_sendB);
        button=findViewById(R.id.bt_s);
        amount=findViewById(R.id.et_am);
        til=findViewById(R.id.til);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);

        r_phone.setChecked(true);
        phone.setVisibility(View.VISIBLE);

        mob.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                final  String st_mno=mob.getText().toString().trim();
                if (st_mno.length() !=10){
                    mob.setError("Enter 10 digit Number");
                    name.setText("");
                    send.setEnabled(false);
                }else {
                    s1=st_mno+"@amo";
                    progressDialog.show();
                    StringRequest stringRequest = new StringRequest(
                            Request.Method.POST,
                            Constants.URL_CHECK_PHONE,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    progressDialog.dismiss();
                                    try {
                                        JSONObject obj = new JSONObject(response);
                                        if (!obj.getBoolean("error")) {
                                            name.setText(obj.getString("name"));
                                            st_upi=obj.getString("upi");
                                            send.setEnabled(true);
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
                                    AlertDialog.Builder builderDel = new AlertDialog.Builder(u_fund_transfer.this);
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
                            params.put("mob", s1);
                            params.put("acc", SharedPrefManager.getInstance(u_fund_transfer.this).getAccNo());
                            return params;
                        }
                    };
                    RequestQueue requestQueue = Volley.newRequestQueue(u_fund_transfer.this);
                    requestQueue.add(stringRequest);

                    requestQueue.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<Object>() {
                        @Override
                        public void onRequestFinished(Request<Object> request) {
                            requestQueue.getCache().clear();
                        }
                    });

                }
            }public void afterTextChanged(Editable s) { }
        });

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String st_mob = mob.getText().toString().trim();
                final String st_name = name.getText().toString().trim();
                final  String st_amount = amo.getText().toString().trim();
                if (st_mob.isEmpty()){
                    mob.setError("Enter 10 digit Number");
                }else if (st_name.isEmpty()){
                    Toast.makeText(getApplicationContext(),"Enter Valid Mobile Number.", Toast.LENGTH_SHORT).show();
                }else if (st_amount.equals("0") || st_amount.isEmpty()){
                    amo.setError("Enter Amount greater than 0");
                }else if (s1.equals(st_upi)){
                    mob.setText("");
                    Toast.makeText(getApplicationContext(),"Self Transfer not possible.", Toast.LENGTH_SHORT).show();
                }else {
                    Intent intent = new Intent(u_fund_transfer.this, payment_gateway.class);
                    intent.putExtra("id", s1);
                    intent.putExtra("name", st_name);
                    intent.putExtra("amount", st_amount.replaceAll(",", ""));
                    intent.putExtra("remark", "User-Send Phone");
                    startActivity(intent);
                }
            }
        });

       button.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               final String st_acc= acc.getText().toString().trim();
               final  String st_name = a_name.getText().toString().trim();
               final  String st_amount = amount.getText().toString().trim();
                   Intent intent = new Intent(u_fund_transfer.this, payment_gateway.class);
                   intent.putExtra("id", st_acc);
                   intent.putExtra("name", st_name);
                   intent.putExtra("amount", st_amount.replaceAll(",", ""));
                   intent.putExtra("remark", "User-Bank Transfer");
                   startActivity(intent);

           }
       });

        sendB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final  String st_ifsc=ifsc.getText().toString().trim();
                final  String st_acc=acc.getText().toString().trim();
                final  String st_cacc=cacc.getText().toString().trim();
                final  String st_name=a_name.getText().toString().trim();

                if (st_ifsc.isEmpty()) {
                    ifsc.setError("Enter IFSC Code");
                } else if (st_acc.isEmpty()) {
                    acc.setError("Enter Account Number");
                } else if (st_cacc.isEmpty()) {
                    cacc.setError("Re-enter Account Number");
                }else if (st_name.isEmpty()){
                    a_name.setError("Enter Account Holder Name");
                }else if (st_acc.equals(SharedPrefManager.getInstance(u_fund_transfer.this).getAccNo())){
                    Toast.makeText(getApplicationContext(),"Self Transfer not possible.", Toast.LENGTH_SHORT).show();
                }else if(st_acc.equals(st_cacc)) {

                    progressDialog.show();
                    StringRequest stringRequest = new StringRequest(
                            Request.Method.POST,
                            Constants.URL_CHECK_BANK,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    progressDialog.dismiss();
                                    try {
                                        JSONObject obj = new JSONObject(response);
                                        if (!obj.getBoolean("error")) {
                                            bank.setVisibility(View.GONE);
                                            til.setVisibility(View.VISIBLE);
                                            button.setVisibility(View.VISIBLE);
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
                                    AlertDialog.Builder builderDel = new AlertDialog.Builder(u_fund_transfer.this);
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
                            params.put("ifsc", st_ifsc);
                            params.put("acc", st_acc);
                            return params;
                        }
                    };
                    RequestQueue requestQueue = Volley.newRequestQueue(u_fund_transfer.this);
                    requestQueue.add(stringRequest);

                    requestQueue.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<Object>() {
                        @Override
                        public void onRequestFinished(Request<Object> request) {
                            requestQueue.getCache().clear();
                        }
                    });
                }else {
                    Toast.makeText(u_fund_transfer.this,"Account Number doesn't Match",Toast.LENGTH_SHORT).show();
                }
            }
        });

        amo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void onTextChanged(CharSequence sw, int i, int i1, int i2) {
                amo.removeTextChangedListener(this);
                try {
                    String originalString = sw.toString();
                    Long longval;
                    if (originalString.contains(",")) {
                        originalString = originalString.replaceAll(",", "");
                    }
                    longval = Long.parseLong(originalString);
                    DecimalFormat formatter = (DecimalFormat) DecimalFormat.getCurrencyInstance(new Locale("en", "in"));
                    formatter.applyPattern("#,###,###,###");
                    String formattedString = formatter.format(longval);
                    amo.setText(formattedString);
                    amo.setSelection(amo.getText().length());
                }catch (NumberFormatException nfe){
                    nfe.printStackTrace();
                }amo.addTextChangedListener(this);
            }
            @Override
            public void afterTextChanged(Editable editable) { }
        });

        amount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void onTextChanged(CharSequence sw, int i, int i1, int i2) {
                amount.removeTextChangedListener(this);
                try {
                    String originalString = sw.toString();
                    Long longval;
                    if (originalString.contains(",")) {
                        originalString = originalString.replaceAll(",", "");
                    }
                    longval = Long.parseLong(originalString);
                    DecimalFormat formatter = (DecimalFormat) DecimalFormat.getCurrencyInstance(new Locale("en", "in"));
                    formatter.applyPattern("#,###,###,###");
                    String formattedString = formatter.format(longval);
                    amount.setText(formattedString);
                    amount.setSelection(amount.getText().length());
                }catch (NumberFormatException nfe){
                    nfe.printStackTrace();
                }amount.addTextChangedListener(this);
            }
            @Override
            public void afterTextChanged(Editable editable) { }
        });

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.r_phone:
                        phone.setVisibility(View.VISIBLE);
                        bank.setVisibility(View.GONE);
                        til.setVisibility(View.GONE);
                        button.setVisibility(View.GONE);
                        break;
                    case R.id.r_bank:
                        bank.setVisibility(View.VISIBLE);
                        phone.setVisibility(View.GONE);
                        til.setVisibility(View.GONE);
                        button.setVisibility(View.GONE);
                        break;
                }
            }
        });
    }
}