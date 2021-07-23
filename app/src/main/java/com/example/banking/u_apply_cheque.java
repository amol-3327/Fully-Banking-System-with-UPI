package com.example.banking;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.example.banking.cashier.c_withdraw;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

public class u_apply_cheque extends AppCompatActivity {
    private EditText pages;
    private TextView name,ifsc,number,status;
    private Button apply,re_apply;
    private ProgressDialog progressDialog;
    private LinearLayout ll_rec,ll_pa;
    private String st_status;
    private CardView delere_r;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_u_apply_cheque);

        if(!SharedPrefManager.getInstance(this).isLoggedIn()){
            finish();
            startActivity(new Intent(this, home.class));
            return;
        }
        ActionBar ab=getSupportActionBar();
        ab.setTitle("Request Chequebook");
        ab.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00801EBD")));
        getWindow().setBackgroundDrawableResource(R.mipmap.back2);

        pages=findViewById(R.id.cheque_p);
        name=findViewById(R.id.tv_name);
        ifsc=findViewById(R.id.tv_ifsc);
        number=findViewById(R.id.tv_pages);
        status=findViewById(R.id.tv_c_status);
        apply=findViewById(R.id.bt_apply);
        re_apply=findViewById(R.id.bt_re_apply);
        ll_rec=findViewById(R.id.ll_rec);
        ll_pa=findViewById(R.id.ll_pa);
        delere_r=findViewById(R.id.c_delere_r);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);

        getPrevious();

        apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String st_pages=pages.getText().toString().trim();
                        if (st_pages.equals("0") || st_pages.isEmpty()){
                            pages.setError("Enter Page Number greater than 0");
                        } else if ((Long.parseLong(st_pages)) > 20){
                            pages.setText("");
                         Toast.makeText(u_apply_cheque.this,"Only 20 Pages Possible. ",Toast.LENGTH_SHORT).show();
                        } else {
                            progressDialog.show();
                            StringRequest stringRequest = new StringRequest(
                                    Request.Method.POST,
                                    Constants.URL_APPLY_CHECK,
                                    new Response.Listener<String>() {
                                        @Override
                                        public void onResponse(String response) {
                                            progressDialog.dismiss();
                                            try {
                                                JSONObject obj = new JSONObject(response);
                                                if (!obj.getBoolean("error")) {
                                                    AlertDialog.Builder builderDel = new AlertDialog.Builder(u_apply_cheque.this);
                                                    builderDel.setCancelable(false);
                                                    builderDel.setMessage(obj.getString("message"));
                                                    builderDel.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialogInterface, int i) {
                                                            getPrevious();
                                                            dialogInterface.dismiss();
                                                        }
                                                    });
                                                    builderDel.create().show();
                                                } else {
                                                    AlertDialog.Builder builderDel = new AlertDialog.Builder(u_apply_cheque.this);
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
                                            AlertDialog.Builder builderDel = new AlertDialog.Builder(u_apply_cheque.this);
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
                                    params.put("acc", SharedPrefManager.getInstance(u_apply_cheque.this).getAccNo());
                                    params.put("pages", st_pages);
                                    return params;
                                }
                            };
                            RequestQueue requestQueue = Volley.newRequestQueue(u_apply_cheque.this);
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

        re_apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { changeS("delete_r");  }
        });

        delere_r.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builderDel = new AlertDialog.Builder(u_apply_cheque.this);
                builderDel.setMessage("Are you sure, You want to delete cheque request?");
                builderDel.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        changeS("delete_r");
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

    public void getPrevious(){
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                Constants.URL_PREVIOUS_REQUEST,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        try {
                            JSONObject obj = new JSONObject(response);
                            if (!obj.getBoolean("error")) {
                                ll_rec.setVisibility(View.VISIBLE);
                                ll_pa.setVisibility(View.GONE);
                                name.setText(obj.getString("name"));
                                ifsc.setText(obj.getString("ifsc"));
                                number.setText(obj.getString("number"));
                                st_status=obj.getString("status");
                                if (st_status.equals("In")){
                                    delere_r.setVisibility(View.VISIBLE);
                                    status.setText(Html.fromHtml("<font color=#2EB83D><b>Pending</font></b>"));
                                }else if (st_status.equals("Out")) {
                                    status.setText(Html.fromHtml("<font color=#2EB83D><b>Ready to Pickup</font></b>"));
                                }else {
                                    re_apply.setVisibility(View.VISIBLE);
                                    status.setText(Html.fromHtml("<font color=#FF0000><b>Request Rejected</font></b>"));
                                }
                            } else {
                                ll_rec.setVisibility(View.GONE);
                                ll_pa.setVisibility(View.VISIBLE);
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
                        AlertDialog.Builder builderDel = new AlertDialog.Builder(u_apply_cheque.this);
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
                params.put("acc", SharedPrefManager.getInstance(u_apply_cheque.this).getAccNo());
                params.put("txt","previous");
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(u_apply_cheque.this);
        requestQueue.add(stringRequest);

        requestQueue.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<Object>() {
            @Override
            public void onRequestFinished(Request<Object> request) {
                requestQueue.getCache().clear();
            }
        });
    }

    public void changeS(String s){
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                Constants.URL_PREVIOUS_REQUEST,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        try {
                            JSONObject obj = new JSONObject(response);
                            if (!obj.getBoolean("error")) {
                                AlertDialog.Builder builderDel = new AlertDialog.Builder(u_apply_cheque.this);
                                builderDel.setCancelable(false);
                                builderDel.setMessage(obj.getString("message"));
                                builderDel.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                        finish();
                                        startActivity(new Intent(u_apply_cheque.this, u_apply_cheque.class));
                                    }
                                });
                                builderDel.create().show();
                            } else {
                                AlertDialog.Builder builderDel = new AlertDialog.Builder(u_apply_cheque.this);
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
                        AlertDialog.Builder builderDel = new AlertDialog.Builder(u_apply_cheque.this);
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
                params.put("acc", SharedPrefManager.getInstance(u_apply_cheque.this).getAccNo());
                params.put("txt", s);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(u_apply_cheque.this);
        requestQueue.add(stringRequest);

        requestQueue.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<Object>() {
            @Override
            public void onRequestFinished(Request<Object> request) {
                requestQueue.getCache().clear();
            }
        });
    }
}