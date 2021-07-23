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
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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
import com.poovam.pinedittextfield.PinField;
import com.poovam.pinedittextfield.SquarePinField;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

public class payment_gateway extends AppCompatActivity {
    public  static payment_gateway pg;
    private TextView name,amount,forgot;
    private SquarePinField pin;
    private String st_pin="amol",st_id,st_name,st_amount,st_status,t_status,st_desc,st_det,st_remark,a,b,i,c,t_date,tid;
    private ProgressDialog progressDialog;
    private DecimalFormat formatter;
    long am;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_gateway);

        if(!SharedPrefManager.getInstance(this).isLoggedIn()){
            finish();
            startActivity(new Intent(this, home.class));
            return;
        }
        ActionBar ab=getSupportActionBar();
        ab.setTitle("Payment Gateway:");
        ab.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#2F20C6")));

        name=findViewById(R.id.name);
        amount=findViewById(R.id.amount);
        pin=findViewById(R.id.u_pin);
        forgot=findViewById(R.id.tv_forgot);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);

        formatter =new DecimalFormat("##,##,###.00");

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            st_id=bundle.getString("id");
            st_name=bundle.getString("name");
            name.setText(st_name);
            st_amount=bundle.getString("amount");
            am=Long.parseLong(st_amount);
            amount.setText(formatter.format(am));
            st_remark=bundle.getString("remark");
        }

        pin.setOnTextCompleteListener(new PinField.OnTextCompleteListener() {
            @Override
            public boolean onTextComplete(@NotNull String enteredText) {
                if (st_remark.equals("User-Send Phone") || st_remark.equals("User-Scan & Pay") ) {
                    progressDialog.show();
                    StringRequest stringRequest = new StringRequest(
                            Request.Method.POST,
                            Constants.URL_SEND_PHONE,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    progressDialog.dismiss();
                                    try {
                                        JSONObject obj = new JSONObject(response);
                                        if (!obj.getBoolean("error")) {
                                            pin.setText("");
                                            trans(obj.getString("message"));
                                        } else {
                                            if ((obj.getString("message")).equals("Invalid UPI PIN...")) {
                                                pin.setText("");
                                                AlertDialog.Builder builderDel = new AlertDialog.Builder(payment_gateway.this);
                                                builderDel.setMessage(obj.getString("message"));
                                                builderDel.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int i) {
                                                        dialogInterface.dismiss();
                                                    }
                                                });
                                                builderDel.create().show();
                                            } else if ((obj.getString("message")).equals("Card is blocked. Please contact to branch...")) {
                                                pin.setText("");
                                                AlertDialog.Builder builderDel = new AlertDialog.Builder(payment_gateway.this);
                                                builderDel.setMessage(obj.getString("message"));
                                                builderDel.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int i) {
                                                        dialogInterface.dismiss();
                                                    }
                                                });
                                                builderDel.create().show();
                                            } else {
                                                trans(obj.getString("message"));
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
                                    pin.setText("");
                                    AlertDialog.Builder builderDel = new AlertDialog.Builder(payment_gateway.this);
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
                            params.put("uid", st_id);
                            params.put("amo", st_amount);
                            params.put("pin", enteredText);
                            params.put("acc", SharedPrefManager.getInstance(payment_gateway.this).getAccNo());
                            return params;
                        }
                    };
                    RequestQueue requestQueue = Volley.newRequestQueue(payment_gateway.this);
                    requestQueue.add(stringRequest);

                    requestQueue.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<Object>() {
                        @Override
                        public void onRequestFinished(Request<Object> request) {
                            requestQueue.getCache().clear();
                        }
                    });
                }else {
                    progressDialog.show();
                    StringRequest stringRequest = new StringRequest(
                            Request.Method.POST,
                            Constants.URL_SEND_BANK,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    progressDialog.dismiss();
                                    try {
                                        JSONObject obj = new JSONObject(response);
                                        if (!obj.getBoolean("error")) {
                                            pin.setText("");
                                            trans(obj.getString("message"));
                                        } else {
                                            if ((obj.getString("message")).equals("Invalid UPI PIN...")) {
                                                pin.setText("");
                                                AlertDialog.Builder builderDel = new AlertDialog.Builder(payment_gateway.this);
                                                builderDel.setMessage(obj.getString("message"));
                                                builderDel.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int i) {
                                                        dialogInterface.dismiss();
                                                    }
                                                });
                                                builderDel.create().show();
                                            } else if ((obj.getString("message")).equals("Card is blocked. Please contact to branch...")) {
                                                pin.setText("");
                                                AlertDialog.Builder builderDel = new AlertDialog.Builder(payment_gateway.this);
                                                builderDel.setMessage(obj.getString("message"));
                                                builderDel.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int i) {
                                                        dialogInterface.dismiss();
                                                    }
                                                });
                                                builderDel.create().show();
                                            } else {
                                                trans(obj.getString("message"));
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
                                    pin.setText("");
                                    AlertDialog.Builder builderDel = new AlertDialog.Builder(payment_gateway.this);
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
                            params.put("uid", st_id);
                            params.put("amo", st_amount);
                            params.put("pin", enteredText);
                            params.put("acc", SharedPrefManager.getInstance(payment_gateway.this).getAccNo());
                            return params;
                        }
                    };
                    RequestQueue requestQueue = Volley.newRequestQueue(payment_gateway.this);
                    requestQueue.add(stringRequest);

                    requestQueue.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<Object>() {
                        @Override
                        public void onRequestFinished(Request<Object> request) {
                            requestQueue.getCache().clear();
                        }
                    });
                }
                return true; // Return false to keep the keyboard open else return true to close the keyboard
            }
        });

        forgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(payment_gateway.this,u_forgot_u_pin.class));
            }
        });
    }

    public void trans(String abc){
        if (abc.equals("Low Account Balance...")){
            t_status="Failed";
        }else if (abc.equals("Transactions  Successfully...")){
            t_status="Successful";
        }
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                Constants.URL_SEND_TRANS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        try {
                            JSONObject obj = new JSONObject(response);
                            if (!obj.getBoolean("error")) {
                                t_date=obj.getString("date");
                                tid=obj.getString("id");
                                if((obj.getString("status")).equals("Successful")){
                                    trans_succ(t_date,tid);
                                }else {
                                    trans_failed(t_date,tid);
                                }

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
                        pin.setText("");
                        AlertDialog.Builder builderDel = new AlertDialog.Builder(payment_gateway.this);
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
                params.put("send",SharedPrefManager.getInstance(payment_gateway.this).getAccNo());
                params.put("rece", st_id);
                params.put("amount", st_amount);
                params.put("rem", st_remark);
                params.put("status", t_status);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(payment_gateway.this);
        requestQueue.add(stringRequest);

        requestQueue.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<Object>() {
            @Override
            public void onRequestFinished(Request<Object> request) {
                requestQueue.getCache().clear();
            }
        });
    }

    public void trans_failed(String result,String id) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            final View customLayout = getLayoutInflater().inflate(R.layout.alert_design, null);
            builder.setView(customLayout);
            builder.setCancelable(false);

            final ImageView imageview= (ImageView) customLayout.findViewById(R.id.t_image);
            final TextView status= (TextView) customLayout.findViewById(R.id.t_status);
            final TextView desc= (TextView) customLayout.findViewById(R.id.t_desc);
            final TextView details= (TextView) customLayout.findViewById(R.id.t_det);
            final Button bt=customLayout.findViewById(R.id.bt_back);

            imageview.setImageResource(R.drawable.failed);

            st_status = "<font color=#FF0000>&nbsp;Transaction Failed</font>";
            status.setText(Html.fromHtml(st_status));
            desc.setText("You do not have sufficient funds in your bank account to make this payment.");

            b = "\nTransaction ID: " + id;
            details.setText(result + b);

            bt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finishAffinity();
                    startActivity(new Intent(payment_gateway.this,MainActivity.class));
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
    }

    public void trans_succ(String date1,String id1) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final View customLayout = getLayoutInflater().inflate(R.layout.alert_design,null);
        builder.setView(customLayout);
        builder.setCancelable(false);

        final ImageView imageview= (ImageView) customLayout.findViewById(R.id.t_image);
        final TextView status= (TextView) customLayout.findViewById(R.id.t_status);
        final TextView desc= (TextView) customLayout.findViewById(R.id.t_desc);
        final TextView details= (TextView) customLayout.findViewById(R.id.t_det);
        final Button bt=customLayout.findViewById(R.id.bt_back);

        imageview.setImageResource(R.drawable.done);

        st_status = "<font color=#2EB83D>&nbsp;Transaction Successful</font>";
        status.setText(Html.fromHtml(st_status));

        desc.setText(Html.fromHtml("<big><b>"+formatter.format(am)+"</b></big><br><small>Paid to</small><br><big>"+st_name+"</big>"));

        b="\nTransaction ID: " +id1;
        details.setText(date1+b);

        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finishAffinity();
                startActivity(new Intent(payment_gateway.this,MainActivity.class));
            }
        });
        AlertDialog dialog= builder.create();
        dialog.show();
    }
}
