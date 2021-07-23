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
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class c_deposit_cheque extends AppCompatActivity {
    private RadioGroup radioChequeGroup;
    private RadioButton r_depoit;
    private EditText cheque_no,amount,acc,amount1,e_name;
    private Button get,get1,c_depo,c_with;
    private ProgressDialog progressDialog;
    private String st_acc,firstFourChars,st_sign,st_name,st_cheque;
    private DecimalFormat formatter;
    private ImageView i_sign;
    private TextView name,ifsc;
    LinearLayout ll_amo,ll_depo,ll_with;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_c_deposit_cheque);

        if(!SharedPrefManager.getInstance(this).isLoggedIn1()){
            finish();
            startActivity(new Intent(this, home.class));
            return;
        }

        getSupportActionBar().setTitle(Html.fromHtml("<font color=#000000>Deposit Cheque:</font>"));
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#ADF48004")));

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        cheque_no=findViewById(R.id.et_c_no);
        get=findViewById(R.id.bt_get);
        get1=findViewById(R.id.bt_get1);
        radioChequeGroup = findViewById(R.id.cheque_group);
        r_depoit = findViewById(R.id.r_deposit);
        i_sign=findViewById(R.id.iv_sign);
        acc=findViewById(R.id.et_acc);
        name=findViewById(R.id.tv_name);
        ifsc=findViewById(R.id.tv_ifsc);
        amount=findViewById(R.id.et_amount);
        c_depo=findViewById(R.id.bt_c_depo);
        ll_amo=findViewById(R.id.ll_amo);
        ll_depo=findViewById(R.id.ll_c_depo);
        ll_with=findViewById(R.id.ll_c_with);
        amount1=findViewById(R.id.et_amount1);
        c_with=findViewById(R.id.bt_c_with);
        e_name=findViewById(R.id.et_name);

        r_depoit.setChecked(true);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);

        formatter =new DecimalFormat("##,##,###.00");

        radioChequeGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.r_deposit:
                        ll_depo.setVisibility(View.VISIBLE);
                        ll_with.setVisibility(View.GONE);
                        break;
                    case R.id.r_withdraw:
                        ll_depo.setVisibility(View.GONE);
                        ll_with.setVisibility(View.VISIBLE);
                        break;
                }
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
        amount1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void onTextChanged(CharSequence sw, int i, int i1, int i2) {
                amount1.removeTextChangedListener(this);
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
                    amount1.setText(formattedString);
                    amount1.setSelection(amount1.getText().length());
                }catch (NumberFormatException nfe){
                    nfe.printStackTrace();
                }amount1.addTextChangedListener(this);
            }
            @Override
            public void afterTextChanged(Editable editable) { }
        });

        i_sign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setPhoto();
            }
        });

        get.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                st_cheque= cheque_no.getText().toString().trim();
                if (st_cheque.isEmpty() || st_cheque.length() != 15){
                    cheque_no.setError("Enter 15 digit cheque number");
                    radioChequeGroup.setVisibility(View.GONE);
                }else {
                    progressDialog.show();
                    if (st_cheque.length() > 10) {
                        firstFourChars = st_cheque.substring(0, 10);
                    } else{
                        firstFourChars = st_cheque;
                    }
                    StringRequest stringRequest = new StringRequest(
                            Request.Method.POST,
                            Constants.URL_CHECK_CHEQUE_NO,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    try {
                                        JSONObject obj = new JSONObject(response);
                                        if (!obj.getBoolean("error")) {
                                            st_sign=obj.getString("sign");

                                            URL url1 = new URL(st_sign);
                                            Bitmap bmp1 = BitmapFactory.decodeStream(url1.openConnection().getInputStream());
                                            i_sign.setImageBitmap(bmp1);

                                            radioChequeGroup.setVisibility(View.VISIBLE);
                                            cheque_no.setEnabled(false);
                                            ll_depo.setVisibility(View.VISIBLE);
                                            get.setEnabled(false);
                                            progressDialog.dismiss();
                                        } else {
                                            progressDialog.dismiss();
                                            radioChequeGroup.setVisibility(View.GONE);
                                            cheque_no.setEnabled(true);
                                            AlertDialog.Builder builderDel = new AlertDialog.Builder(c_deposit_cheque.this);
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
                                    AlertDialog.Builder builderDel = new AlertDialog.Builder(c_deposit_cheque.this);
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
                            params.put("acc", firstFourChars);
                            params.put("c_no", st_cheque);
                            return params;
                        }
                    };
                    RequestQueue requestQueue = Volley.newRequestQueue(c_deposit_cheque.this);
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
                st_acc= acc.getText().toString().trim();
                if (st_acc.isEmpty()){
                    acc.setError("Enter Account Number");
                }else if (firstFourChars.equals(st_acc)){
                    Toast.makeText(c_deposit_cheque.this,"Self Deposit Not Possible...",Toast.LENGTH_SHORT).show();
                } else {
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
                                            st_name=obj.getString("name");
                                            name.setText(st_name);
                                            ifsc.setText(obj.getString("ifsc"));
                                            ll_amo.setVisibility(View.VISIBLE);
                                        } else {
                                            ll_amo.setVisibility(View.GONE);
                                            name.setText("");
                                            ifsc.setText("");
                                            AlertDialog.Builder builderDel = new AlertDialog.Builder(c_deposit_cheque.this);
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
                                    AlertDialog.Builder builderDel = new AlertDialog.Builder(c_deposit_cheque.this);
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
                            params.put("txt", "c_deposit");
                            return params;
                        }
                    };
                    RequestQueue requestQueue = Volley.newRequestQueue(c_deposit_cheque.this);
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
        c_depo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final  String st_amount=amount.getText().toString().trim();
                final  String st_amount1=st_amount.replaceAll(",", "");
                if (st_amount1.equals("0") || st_amount1.isEmpty()) {
                    amount.setError("Enter Amount greater than 0");
                }else{
                    progressDialog.show();
                    StringRequest stringRequest = new StringRequest(
                            Request.Method.POST,
                            Constants.URL_CHEQUE_OPERATIONS,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    progressDialog.dismiss();
                                    try {
                                        JSONObject obj = new JSONObject(response);
                                        if (!obj.getBoolean("error")) {
                                            AlertDialog.Builder builderDel = new AlertDialog.Builder(c_deposit_cheque.this);
                                            builderDel.setCancelable(false);
                                            builderDel.setMessage(obj.getString("message"));
                                            builderDel.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    dialogInterface.dismiss();
                                                    i_sign.setImageResource(R.drawable.user1);
                                                    radioChequeGroup.setVisibility(View.GONE);
                                                    ll_depo.setVisibility(View.GONE);
                                                    ll_with.setVisibility(View.GONE);
                                                    cheque_no.setText("");
                                                    cheque_no.setEnabled(true);
                                                }
                                            });
                                            builderDel.create().show();
                                        } else {
                                            AlertDialog.Builder builderDel = new AlertDialog.Builder(c_deposit_cheque.this);
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
                                    AlertDialog.Builder builderDel = new AlertDialog.Builder(c_deposit_cheque.this);
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
                            params.put("s_acc", firstFourChars);
                            params.put("r_acc", st_acc);
                            params.put("amo", st_amount1);
                            params.put("name", st_name);
                            params.put("c_no", st_cheque);
                            params.put("txt", "c_deposit");
                            return params;
                        }
                    };
                    RequestQueue requestQueue = Volley.newRequestQueue(c_deposit_cheque.this);
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
        c_with.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final  String st_amount=amount1.getText().toString().trim();
                final  String st_amount1=st_amount.replaceAll(",", "");
                final  String st_name1=e_name.getText().toString().trim();
                if (st_amount1.equals("0") || st_amount1.isEmpty()) {
                    amount1.setError("Enter Amount greater than 0");
                }else if (st_name1.isEmpty()){
                    e_name.setError("Enter Name");
                }else {
                    progressDialog.show();
                    StringRequest stringRequest = new StringRequest(
                            Request.Method.POST,
                            Constants.URL_CHEQUE_OPERATIONS,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    progressDialog.dismiss();
                                    try {
                                        JSONObject obj = new JSONObject(response);
                                        if (!obj.getBoolean("error")) {
                                            AlertDialog.Builder builderDel = new AlertDialog.Builder(c_deposit_cheque.this);
                                            builderDel.setCancelable(false);
                                            builderDel.setMessage(obj.getString("message"));
                                            builderDel.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    dialogInterface.dismiss();
                                                    i_sign.setImageResource(R.drawable.user1);
                                                    radioChequeGroup.setVisibility(View.GONE);
                                                    ll_depo.setVisibility(View.GONE);
                                                    ll_with.setVisibility(View.GONE);
                                                    cheque_no.setText("");
                                                    cheque_no.setEnabled(true);
                                                    get.setEnabled(true);
                                                }
                                            });
                                            builderDel.create().show();
                                        } else {
                                            AlertDialog.Builder builderDel = new AlertDialog.Builder(c_deposit_cheque.this);
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
                                    AlertDialog.Builder builderDel = new AlertDialog.Builder(c_deposit_cheque.this);
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
                            params.put("s_acc", firstFourChars);
                            params.put("r_acc", "h");
                            params.put("amo", st_amount1);
                            params.put("name", st_name1);
                            params.put("c_no", st_cheque);
                            params.put("txt", "c_with");
                            return params;
                        }
                    };
                    RequestQueue requestQueue = Volley.newRequestQueue(c_deposit_cheque.this);
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
    public void setPhoto(){
        AlertDialog.Builder builder = new AlertDialog.Builder(c_deposit_cheque.this);
        final View customLayout = getLayoutInflater().inflate(R.layout.full_image, null);
        builder.setView(customLayout);

        final PhotoView photoView=(PhotoView) customLayout.findViewById(R.id.photo_view);
        photoView.setScaleType(PhotoView.ScaleType.CENTER_CROP);
        photoView.setImageDrawable(i_sign.getDrawable());

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}