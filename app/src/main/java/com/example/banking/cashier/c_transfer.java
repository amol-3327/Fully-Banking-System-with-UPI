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
import android.os.StrictMode;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
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
import com.example.banking.R;
import com.example.banking.home;
import com.github.chrisbanes.photoview.PhotoView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class c_transfer extends AppCompatActivity {
    private EditText amount,acc,acc1;
    private TextView name,name1,ifsc,ifsc1,bal;
    private Button get,get1,trans,visi;
    private ProgressDialog progressDialog;
    private DecimalFormat formatter;
    private String st_acc1,st_acc2,st_bal,st_name,st_name1,st_photo,st_sign;
    private LinearLayout ll_a1,ll_a;
    private ImageView i_photo,i_sign;
    private double am,ba;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_c_transfer);


        if(!SharedPrefManager.getInstance(this).isLoggedIn1()){
            finish();
            startActivity(new Intent(this, home.class));
            return;
        }

        getSupportActionBar().setTitle(Html.fromHtml("<font color=#000000>Transfer Money:</font>"));
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#ADF48004")));

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        acc=findViewById(R.id.et_acc);
        acc1=findViewById(R.id.et_acc1);
        get=findViewById(R.id.bt_get);
        get1=findViewById(R.id.bt_get1);
        name=findViewById(R.id.tv_name);
        name1=findViewById(R.id.tv_name1);
        ifsc=findViewById(R.id.tv_ifsc);
        ifsc1=findViewById(R.id.tv_ifsc1);
        bal=findViewById(R.id.tv_bal);
        amount=findViewById(R.id.et_amount);
        i_photo=findViewById(R.id.iv_photo);
        i_sign=findViewById(R.id.iv_sign);
        trans=findViewById(R.id.bt_trans);
        ll_a1=findViewById(R.id.ll_a1);
        ll_a=findViewById(R.id.ll_a);
        visi=findViewById(R.id.bt_visi);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);

        formatter =new DecimalFormat("##,##,###.00");

        visi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String st_name= name.getText().toString().trim();
                if (st_name.isEmpty()){
                    Toast.makeText(getApplicationContext(),"First Get Account Details.",Toast.LENGTH_SHORT).show();
                }else {
                    acc.setEnabled(false);
                    ll_a.setVisibility(View.GONE);
                    ll_a1.setVisibility(View.VISIBLE);
                }
            }
        });

        i_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setPhoto("photo");
            }
        });
        i_sign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setPhoto("sign");
            }
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
                                    try {
                                        JSONObject obj = new JSONObject(response);
                                        if (!obj.getBoolean("error")) {
                                            st_name=obj.getString("name");
                                            name.setText(st_name);
                                            ifsc.setText(obj.getString("ifsc"));
                                            st_photo=obj.getString("photo");
                                            st_sign=obj.getString("sign");
                                            st_bal=obj.getString("bal");
                                            if (st_bal.equals("0")){
                                                bal.setText("0.00/-");
                                            }else {
                                                bal.setText("₹ "+formatter.format(Long.parseLong(st_bal))+"/-");
                                            }
                                            st_acc1=obj.getString("acc");

                                            URL url = new URL(st_photo);
                                            Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                                            i_photo.setImageBitmap(bmp);

                                            URL url1 = new URL(st_sign);
                                            Bitmap bmp1 = BitmapFactory.decodeStream(url1.openConnection().getInputStream());
                                            i_sign.setImageBitmap(bmp1);
                                            progressDialog.dismiss();
                                        } else {
                                            progressDialog.dismiss();
                                            i_photo.setImageResource(R.drawable.user1);
                                            i_sign.setImageResource(R.drawable.sign1);
                                            name.setText("");
                                            ifsc.setText("");
                                            bal.setText("");
                                            AlertDialog.Builder builderDel = new AlertDialog.Builder(c_transfer.this);
                                            builderDel.setMessage(obj.getString("message"));
                                            builderDel.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    dialogInterface.dismiss();
                                                }
                                            });
                                            builderDel.create().show();
                                        }
                                    } catch (JSONException | IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    progressDialog.dismiss();
                                    AlertDialog.Builder builderDel = new AlertDialog.Builder(c_transfer.this);
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
                            params.put("txt", "withdraw");
                            return params;
                        }
                    };
                    RequestQueue requestQueue = Volley.newRequestQueue(c_transfer.this);
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

        get1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String st_acc= acc1.getText().toString().trim();
                if (st_acc.isEmpty()){
                    acc1.setError("Require Receiver Account Number");
                }else if (st_acc.equals(st_acc1)){
                    Toast.makeText(getApplicationContext(),"Self Transfer not possible.", Toast.LENGTH_SHORT).show();
                }else {
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
                                            st_name1 = obj.getString("name");
                                            name1.setText(st_name1);
                                            ifsc1.setText(obj.getString("ifsc"));
                                            st_acc2=obj.getString("acc");
                                        } else {
                                            AlertDialog.Builder builderDel = new AlertDialog.Builder(c_transfer.this);
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
                                    AlertDialog.Builder builderDel = new AlertDialog.Builder(c_transfer.this);
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
                            params.put("txt", "transfer");
                            return params;
                        }
                    };
                    RequestQueue requestQueue = Volley.newRequestQueue(c_transfer.this);
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

        trans.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final  String st_amount=amount.getText().toString().trim();
                final  String st_amount1=st_amount.replaceAll(",", "");
                if (st_amount1.equals("0") || st_amount1.isEmpty()) {
                    amount.setError("Enter Amount greater than 0");
                }else {
                    am=Double.parseDouble(st_amount1);
                    ba=Double.parseDouble(st_bal);
                }

                if (st_amount1.equals("0") || st_amount1.isEmpty()){
                    amount.setError("Enter Amount greater than 0");
                } else if (am > ba) {
                    AlertDialog.Builder builderDel = new AlertDialog.Builder(c_transfer.this);
                    builderDel.setMessage("Entered amount is greater then current balance.");
                    builderDel.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
                    builderDel.create().show();
                }
                else{
                    progressDialog.show();
                    StringRequest stringRequest = new StringRequest(
                            Request.Method.POST,
                            Constants.URL_TRANSFER,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    progressDialog.dismiss();
                                    try {
                                        JSONObject obj = new JSONObject(response);
                                        if (!obj.getBoolean("error")) {
                                            AlertDialog.Builder builderDel = new AlertDialog.Builder(c_transfer.this);
                                            builderDel.setMessage(obj.getString("message"));
                                            builderDel.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    dialogInterface.dismiss();
                                                }
                                            });
                                            builderDel.create().show();
                                        } else {
                                            AlertDialog.Builder builderDel = new AlertDialog.Builder(c_transfer.this);
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
                                    AlertDialog.Builder builderDel = new AlertDialog.Builder(c_transfer.this);
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
                            params.put("send", st_acc1);
                            params.put("rece", st_acc2);
                            params.put("amo", st_amount1);
                            params.put("sname", st_name);
                            params.put("rname", st_name1);
                            return params;
                        }
                    };
                    RequestQueue requestQueue = Volley.newRequestQueue(c_transfer.this);
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

    public void setPhoto(String s){
        AlertDialog.Builder builder = new AlertDialog.Builder(c_transfer.this);
        final View customLayout = getLayoutInflater().inflate(R.layout.full_image, null);
        builder.setView(customLayout);

        final PhotoView photoView=(PhotoView) customLayout.findViewById(R.id.photo_view);
        if (s.equals("photo")){
            photoView.setScaleType(PhotoView.ScaleType.CENTER_CROP);
            photoView.setImageDrawable(i_photo.getDrawable());
        }else {
            photoView.setScaleType(PhotoView.ScaleType.CENTER_CROP);
            photoView.setImageDrawable(i_sign.getDrawable());
        }

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}