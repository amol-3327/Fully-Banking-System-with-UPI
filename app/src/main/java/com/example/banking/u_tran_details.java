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

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

public class u_tran_details extends AppCompatActivity {
    private ProgressDialog progressDialog;
    private String st_id,st_a;
    private TextView tid,tdate,tpt,tpf,tstatus,tamount,tthe,txt,c_no;
    private DecimalFormat formatter;
    private LinearLayout llp,lla,llc;
    private View v,v1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_u_tran_details);

        if(!SharedPrefManager.getInstance(this).isLoggedIn()){
            finish();
            startActivity(new Intent(this, home.class));
        }

        ActionBar ab=getSupportActionBar();
        ab.setTitle("Transaction Details:");
        ab.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00801EBD")));
        getWindow().setBackgroundDrawableResource(R.mipmap.back2);

        tid=findViewById(R.id.tv_tid);
        tdate=findViewById(R.id.tv_tdate);
        tpt=findViewById(R.id.tv_tpt);
        tpf=findViewById(R.id.tv_tpf);
        tstatus=findViewById(R.id.tv_tstatus);
        tamount=findViewById(R.id.tv_tamount);
        tthe=findViewById(R.id.tv_the);
        txt=findViewById(R.id.txt);
        llp=findViewById(R.id.ll_p);
        lla=findViewById(R.id.ll_acc);
        llc=findViewById(R.id.ll_c);
        v=findViewById(R.id.v);
        v1=findViewById(R.id.v1);
        c_no=findViewById(R.id.tv_cheque);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);

        formatter =new DecimalFormat("##,##,###.00");
        st_a=SharedPrefManager.getInstance(this).getAccNo();

        st_id = getIntent().getStringExtra("id");

        progressDialog.show();
        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                Constants.URL_TRANS_DETAILS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        try {
                            JSONObject obj = new JSONObject(response);
                            if (!obj.getBoolean("error")) {
                                final String st_id1=obj.getString("id");
                                final String st_date=obj.getString("date");
                                final String st_status=obj.getString("status");
                                final String st_remark=obj.getString("remark");
                                final String st_send=obj.getString("send");
                                final String st_rece=obj.getString("rece");
                                final String st_upi=obj.getString("upi");
                                final String st_acc=obj.getString("acc");
                                final String st_amount=obj.getString("amount");
                                final String st_cheque=obj.getString("cheque");
                                final String st_wname=obj.getString("wname");

                                tid.setText(st_id1);
                                if (st_a.equals(st_acc)){
                                     if (st_send.equals(st_rece)){
                                         if (st_remark.equals("Withdraw Amount")){
                                             tthe.setText("Bank-Withdraw Transaction");
                                             lla.setVisibility(View.GONE);
                                         }else if(st_remark.equals("Deposit Amount")){
                                             tthe.setText("Bank-Deposit Transaction");
                                             lla.setVisibility(View.GONE);
                                         }else if (st_remark.equals("Cheque Withdraw1")){
                                             txt.setText("Paid to :");
                                             tpt.setText(st_wname);
                                             llp.setVisibility(View.GONE);
                                             v.setVisibility(View.GONE);
                                             tthe.setText("Cheque-Withdraw Transaction");
                                             c_no.setText(st_cheque);
                                             llc.setVisibility(View.VISIBLE);
                                             v1.setVisibility(View.VISIBLE);
                                         }else if (st_remark.equals("Loan Emi")){
                                             lla.setVisibility(View.GONE);
                                             tthe.setText("EMI Transaction");
                                         }else if (st_remark.equals("Balance Interest")){
                                             lla.setVisibility(View.GONE);
                                             tthe.setText("Interest");
                                         }else if (st_remark.equals("Salary")){
                                             lla.setVisibility(View.GONE);
                                             tthe.setText("Salary");
                                         }else if (st_remark.equals("loan amount")){
                                             lla.setVisibility(View.GONE);
                                             tthe.setText("Loan Amount");
                                         }else {
                                             lla.setVisibility(View.GONE);
                                             tthe.setText("Account Created Deposit");
                                         }
                                     }else {
                                         txt.setText("Received From :");
                                         tpt.setText(st_send);
                                         llp.setVisibility(View.GONE);
                                         v.setVisibility(View.GONE);
                                         tthe.setVisibility(View.GONE);
                                     }
                                }else{
                                    txt.setText("Paid to :");
//                                    tpt.setText(st_rece);
                                    if (st_remark.equals("User-Send Phone")){
                                        tpt.setText(st_upi.replaceAll("[^0-9]", ""));
                                        tthe.setText("Phone-Send Transaction");
                                    }else if (st_remark.equals("User-Bank Transfer")){
                                        tpt.setText(st_acc);
                                        tthe.setText("Money-Transfer Transaction");
                                    }else if (st_remark.equals("Cheque Withdraw")){
                                        tpt.setText(st_acc);
                                        c_no.setText(st_cheque);
                                        llc.setVisibility(View.VISIBLE);
                                        v1.setVisibility(View.VISIBLE);
                                        tthe.setText("Cheque-Withdraw Transaction");
                                    } else if (st_remark.equals("User-Scan & Pay")){
                                        tpt.setText(st_upi);
                                        tthe.setText("Scan & Pay Transaction");
                                    }
                                }

                                tdate.setText(st_date);
                                String lastFourDigits = "";     //substring containing last 4 characters
                                if (st_a.length() > 4) {
                                    lastFourDigits = st_a.substring(st_a.length() - 4);
                                } else {
                                    lastFourDigits = st_a;
                                }

                                tpf.setText("Amols Bank *****"+lastFourDigits);
                                if (st_status.equals("Successful")) {
                                    tstatus.setText(Html.fromHtml("<font color=#2EB83D><b>Successful</font></b>"));
                                } else {
                                    tstatus.setText(Html.fromHtml("<font color=#FF0000><b>Failed</font></b>"));
                                }
                                tamount.setText("₹ "+formatter.format(Long.parseLong(st_amount))+"/-");
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
                        AlertDialog.Builder builderDel = new AlertDialog.Builder(u_tran_details.this);
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
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(u_tran_details.this);
        requestQueue.add(stringRequest);

        requestQueue.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<Object>() {
            @Override
            public void onRequestFinished(Request<Object> request) {
                requestQueue.getCache().clear();
            }
        });
    }
}