package com.example.banking.cashier;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
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
import com.example.banking.u_change_u_pin;
import com.example.banking.u_new_user;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

public class c_add_card extends AppCompatActivity {
    private EditText acc,card,exp,csv;
    private TextView name,ifsc,bal,status,b_status;
    private Button get,add,change_card,change_c,block;
    private ProgressDialog progressDialog;
    private DecimalFormat formatter;
    private LinearLayout ll_card,ll_c_card;
    private String st_acc1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_c_add_card);

        if(!SharedPrefManager.getInstance(this).isLoggedIn1()){
            finish();
            startActivity(new Intent(this, home.class));
            return;
        }

        getSupportActionBar().setTitle(Html.fromHtml("<font color=#000000>Add Debit Card:</font>"));
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#ADF48004")));

        acc=findViewById(R.id.et_acc);
        get=findViewById(R.id.bt_get);
        name=findViewById(R.id.tv_name);
        ifsc=findViewById(R.id.tv_ifsc);
        status=findViewById(R.id.tv_status);
        b_status=findViewById(R.id.tv_block);
        bal=findViewById(R.id.tv_bal);
        card=findViewById(R.id.et_card);
        exp=findViewById(R.id.et_exp);
        csv=findViewById(R.id.et_csv);
        add=findViewById(R.id.bt_add);
        change_card=findViewById(R.id.bt_change_card);
        change_c=findViewById(R.id.bt_change);
        block=findViewById(R.id.bt_block);
        ll_card=findViewById(R.id.ll_card);
        ll_c_card=findViewById(R.id.ll_c_card);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);

        formatter =new DecimalFormat("##,##,###.00");

        change_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                change_c.setVisibility(View.VISIBLE);
                ll_card.setVisibility(View.VISIBLE);
                add.setVisibility(View.GONE);
            }
        });

        block.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name1 = ((Button) block).getText().toString();

                if (st_acc1.isEmpty()) {
                    Toast.makeText(c_add_card.this,"Account Number Cannot be empty..",Toast.LENGTH_SHORT).show();
                } else {
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(c_add_card.this);
                    builder1.setMessage("Are you sure, You want to "+name1+" ?");
                    builder1.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            if (name1.equals("Block Card")){
                                cardOperation(st_acc1,"st_cno2","st_exp2","st_csv","block");
                            }else if (name1.equals("Unblock Card")){
                                cardOperation(st_acc1,"st_cno2","st_exp2","st_csv","unblock");
                            }
                        }
                    });
                    builder1.setNegativeButton("No", new DialogInterface.OnClickListener() {
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
        });


        get.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String st_acc= acc.getText().toString().trim();
                if (st_acc.isEmpty()){
                    acc.setError("Require Account Number");
                }else{
                    progressDialog.show();
                    StringRequest stringRequest = new StringRequest(
                            Request.Method.POST,
                            Constants.URL_GET_DETAILS,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    progressDialog.dismiss();
                                    try {
                                        JSONObject obj = new JSONObject(response);
                                        if (!obj.getBoolean("error")) {
                                            name.setText(obj.getString("name"));
                                            ifsc.setText(obj.getString("ifsc"));
                                            if ((obj.getString("status")).equals("null")){
                                                status.setText(Html.fromHtml("<font color=#FF0000><b>Don't Have Card</font></b>"));
                                                ll_card.setVisibility(View.VISIBLE);
                                                ll_c_card.setVisibility(View.GONE);
                                                add.setVisibility(View.VISIBLE);
                                                change_c.setVisibility(View.GONE);
                                            }else {
                                                status.setText(Html.fromHtml("<font color=#2EB83D><b>Already Have Card</font></b>"));
                                                ll_card.setVisibility(View.GONE);
                                                ll_c_card.setVisibility(View.VISIBLE);
                                                change_c.setVisibility(View.GONE);
                                            }
                                            if ((obj.getString("block")).equals("no")){
                                                b_status.setText(Html.fromHtml("<font color=#2EB83D><b>NO</font></b>"));
                                                block.setText("Block Card");
                                            }else if ((obj.getString("block")).equals("yes")){
                                                b_status.setText(Html.fromHtml("<font color=#FF0000><b>YES</font></b>"));
                                                block.setText("Unblock Card");
                                            }else {
                                                b_status.setText(Html.fromHtml("<font color=#2EB83D><b>NA</font></b>"));
                                            }
                                            bal.setText("₹ "+formatter.format(Long.parseLong(obj.getString("bal")))+"/-");
                                            st_acc1=obj.getString("acc");
                                        } else {
                                            ll_card.setVisibility(View.GONE);
                                            name.setText("");
                                            ifsc.setText("");
                                            status.setText("");
                                            bal.setText("");
                                            AlertDialog.Builder builderDel = new AlertDialog.Builder(c_add_card.this);
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
                                    AlertDialog.Builder builderDel = new AlertDialog.Builder(c_add_card.this);
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
                            params.put("acc", st_acc);
                            params.put("txt", "addcard");
                            return params;
                        }
                    };
                    RequestQueue requestQueue = Volley.newRequestQueue(c_add_card.this);
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

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final  String st_cno=card.getText().toString().trim();
                final  String st_cno1=st_cno.replaceAll("  ", "");
                final  String st_cno2=st_cno1.replaceAll("-", "");
                final  String st_exp=exp.getText().toString().trim();
                final  String st_exp1=st_exp.replaceAll("  ", "");
                final  String st_exp2=st_exp1.replaceAll("-", "");
                final  String st_csv=csv.getText().toString().trim();

                if (st_cno2.length() != 16) {
                    card.setError("Enter 16 digit Number");
                }else if (st_exp2.length() != 4) {
                    exp.setError("Enter Expiry Date");
                } else if (st_csv.isEmpty()) {
                    csv.setError("Enter CSV Number");
                } else if (st_acc1.isEmpty()) {
                    Toast.makeText(c_add_card.this,"Account Number Cannot be empty..",Toast.LENGTH_SHORT).show();
                } else {
                    cardOperation(st_acc1,st_cno2,st_exp2,st_csv,"add");
                }
            }
        });
        change_c.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final  String st_cno=card.getText().toString().trim();
                final  String st_cno1=st_cno.replaceAll("  ", "");
                final  String st_cno2=st_cno1.replaceAll("-", "");
                final  String st_exp=exp.getText().toString().trim();
                final  String st_exp1=st_exp.replaceAll("  ", "");
                final  String st_exp2=st_exp1.replaceAll("-", "");
                final  String st_csv=csv.getText().toString().trim();

                if (st_cno2.length() != 16) {
                    card.setError("Enter 16 digit Number");
                }else if (st_exp2.length() != 4) {
                    exp.setError("Enter Expiry Date");
                } else if (st_csv.isEmpty()) {
                    csv.setError("Enter CSV Number");
                } else if (st_acc1.isEmpty()) {
                    Toast.makeText(c_add_card.this,"Account Number Cannot be empty..",Toast.LENGTH_SHORT).show();
                } else {
                    cardOperation(st_acc1,st_cno2,st_exp2,st_csv,"change");
                }
            }
        });
    }
    public void cardOperation(String s_acc,String s_cno,String s_exp,String s_csv,String s_txt){
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                Constants.URL_ADD_CARD,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        try {
                            JSONObject obj = new JSONObject(response);
                            if (!obj.getBoolean("error")) {
                                ll_card.setVisibility(View.GONE);
                                ll_c_card.setVisibility(View.GONE);
                                AlertDialog.Builder builderDel = new AlertDialog.Builder(c_add_card.this);
                                builderDel.setMessage(obj.getString("message"));
                                builderDel.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                    }
                                });
                                builderDel.create().show();
                            } else {
                                ll_card.setVisibility(View.GONE);
                                change_card.setVisibility(View.GONE);
                                AlertDialog.Builder builderDel = new AlertDialog.Builder(c_add_card.this);
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
                        AlertDialog.Builder builderDel = new AlertDialog.Builder(c_add_card.this);
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
                params.put("acc", s_acc);
                params.put("card", s_cno);
                params.put("exp", s_exp);
                params.put("csv", s_csv);
                params.put("txt", s_txt);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(c_add_card.this);
        requestQueue.add(stringRequest);

        requestQueue.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<Object>() {
            @Override
            public void onRequestFinished(Request<Object> request) {
                requestQueue.getCache().clear();
            }
        });
    }
}