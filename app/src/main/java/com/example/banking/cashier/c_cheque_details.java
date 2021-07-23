package com.example.banking.cashier;

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
import android.os.StrictMode;
import android.text.Html;
import android.view.View;
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
import com.example.banking.MainActivity;
import com.example.banking.R;
import com.example.banking.home;
import com.example.banking.manager.m_a_e_details;
import com.github.chrisbanes.photoview.PhotoView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class c_cheque_details extends AppCompatActivity {
    private TextView acc,name,r_pages,bal,mob,status,pages;
    private Button confirm,reject;
    private ProgressDialog progressDialog;
    private DecimalFormat formatter;
    private String st_bal,st_id,st_status,st_acc,st_rmk,st_u_date,st_u_dob,st_photo,st_sign;;
    private LinearLayout ll_but,ll_account,ll_details;
    private ScrollView back;
    private TextView a_acc,a_name,a_address,a_email,a_mob,a_dob,a_date,a_c_status,a_card,a_exp,a_csv;
    private ImageView i_photo,i_sign;
    private CardView c_details;
    private SimpleDateFormat dateFormat,dateFormat1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_c_cheque_details);

        if(!SharedPrefManager.getInstance(this).isLoggedIn1()){
            finish();
            startActivity(new Intent(this, home.class));
            return;
        }

        getSupportActionBar().setTitle(Html.fromHtml("<font color=#000000>Cheque Request Details :</font>"));
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#ADF48004")));

        name=findViewById(R.id.tv_name);
        acc=findViewById(R.id.tv_acc);
        r_pages=findViewById(R.id.tv_r_pages);
        bal=findViewById(R.id.tv_bal);
        mob=findViewById(R.id.tv_mob);
        pages=findViewById(R.id.tv_c_pages);
        status=findViewById(R.id.tv_status);
        confirm=findViewById(R.id.bt_conf);
        reject=findViewById(R.id.bt_rej);
        ll_but=findViewById(R.id.ll_but);
        ll_account=findViewById(R.id.ll_account);
        ll_details=findViewById(R.id.ll_details);
        back=findViewById(R.id.s_back);

        i_photo=findViewById(R.id.iv_photo);
        i_sign=findViewById(R.id.iv_sign);
        a_acc=findViewById(R.id.tv_acc1);
        a_name=findViewById(R.id.tv_name1);
        a_dob=findViewById(R.id.tv_dob);
        a_mob=findViewById(R.id.tv_mob1);
        a_email=findViewById(R.id.tv_email);
        a_address=findViewById(R.id.tv_address);
        a_c_status=findViewById(R.id.tv_card_status);
        a_date=findViewById(R.id.tv_c_date);
        a_card=findViewById(R.id.tv_card);
        a_exp =findViewById(R.id.tv_exp);
        a_csv=findViewById(R.id.tv_csv);
        c_details=findViewById(R.id.c_details);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat1 = new SimpleDateFormat("dd-MMM-yyyy");
        formatter =new DecimalFormat("##,##,###.00");

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            st_id = getIntent().getStringExtra("id");
            st_rmk=bundle.getString("rmk");
        }

        if (st_rmk.equals("user")){
            getSupportActionBar().setTitle(Html.fromHtml("<font color=#000000>Account Details :</font>"));
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#ADF48004")));
            back.setBackgroundResource(R.drawable.gradient);
            ll_account.setVisibility(View.VISIBLE);
            accounts();
        }else {
            getSupportActionBar().setTitle(Html.fromHtml("<font color=#000000>Cheque Details :</font>"));
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#ADF48004")));
            ll_details.setVisibility(View.VISIBLE);
            details();
        }

        i_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                setPhoto("photo");
            }
        });
        i_sign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setPhoto("sign");
            }
        });

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builderDel = new AlertDialog.Builder(c_cheque_details.this);
                builderDel.setMessage("Are you sure, you want to confirm request");
                builderDel.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        change("confirm");
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

        reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builderDel = new AlertDialog.Builder(c_cheque_details.this);
                builderDel.setMessage("Are you sure, you want to reject request");
                builderDel.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        change("reject");
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

    public void change(String s){
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                Constants.URL_CHEQUE_STATUS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        try {
                            JSONObject obj = new JSONObject(response);
                            if (!obj.getBoolean("error")) {
                                AlertDialog.Builder builderDel = new AlertDialog.Builder(c_cheque_details.this);
                                builderDel.setMessage(obj.getString("message"));
                                builderDel.setCancelable(false);
                                builderDel.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        c_manage_cheque.mc.refresh_list();
                                        finish();
                                    }
                                });
                                builderDel.create().show();
                            } else {
                                AlertDialog.Builder builderDel = new AlertDialog.Builder(c_cheque_details.this);
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
                        AlertDialog.Builder builderDel = new AlertDialog.Builder(c_cheque_details.this);
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
                params.put("txt", s);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(c_cheque_details.this);
        requestQueue.add(stringRequest);

        requestQueue.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<Object>() {
            @Override
            public void onRequestFinished(Request<Object> request) {
                requestQueue.getCache().clear();
            }
        });
    }

    public void getCount(String sacc){
        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                Constants.URL_GET_CHEQUE_COUNT,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            if (!obj.getBoolean("error")) {
                                pages.setText(obj.getString("count"));
                                progressDialog.dismiss();
                            } else {
                                pages.setText(obj.getString("count"));
                                progressDialog.dismiss();
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
                        AlertDialog.Builder builderDel = new AlertDialog.Builder(c_cheque_details.this);
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
                params.put("acc",sacc);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(c_cheque_details.this);
        requestQueue.add(stringRequest);

        requestQueue.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<Object>() {
            @Override
            public void onRequestFinished(Request<Object> request) {
                requestQueue.getCache().clear();
            }
        });
    }
    public void accounts(){
        progressDialog.show();
        StringRequest stringRequest=new StringRequest(Request.Method.POST,com.example.banking.manager.Constants.URL_U_E_DETAILS, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject obj = new JSONObject(response);
                    if (!obj.getBoolean("error")) {
                        a_name.setText(obj.getString("name"));
                        st_u_dob=obj.getString("dob");
                        a_mob.setText(obj.getString("mob"));
                        a_email.setText(obj.getString("email"));
                        a_address.setText(obj.getString("address"));
                        st_u_date=obj.getString("date");
                        if ((obj.getString("card")).equals("null")){
                            a_c_status.setText(Html.fromHtml("<font color=#FF0000><b>Don't Have Card</font></b>"));
                            c_details.setVisibility(View.GONE);
                        }else {
                            a_c_status.setText(Html.fromHtml("<font color=#2EB83D><b>Already Have Card</font></b>"));
                            c_details.setVisibility(View.VISIBLE);
                        }
                        st_photo=obj.getString("photo");
                        st_sign=obj.getString("sign");
                        a_card.setText(obj.getString("card"));
                        a_exp.setText(obj.getString("exp"));
                        a_csv.setText(obj.getString("csv"));

                        Date fechaNueva = dateFormat.parse(st_u_date);
                        a_date.setText(dateFormat1.format(fechaNueva));

                        Date fechaNueva1 = dateFormat.parse(st_u_dob);
                        a_dob.setText(dateFormat1.format(fechaNueva1));
                        a_acc.setText(st_id);

                        URL url = new URL(st_photo);
                        Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                        i_photo.setImageBitmap(bmp);

                        URL url1 = new URL(st_sign);
                        Bitmap bmp1 = BitmapFactory.decodeStream(url1.openConnection().getInputStream());
                        i_sign.setImageBitmap(bmp1);

                        progressDialog.dismiss();
                    }else{
                        progressDialog.dismiss();
                        AlertDialog.Builder builderDel = new AlertDialog.Builder(c_cheque_details.this);
                        builderDel.setMessage(obj.getString("message"));
                        builderDel.setCancelable(false);
                        builderDel.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                               finish();
                            }
                        });
                        builderDel.create().show();
                    }
                }catch (JSONException | ParseException | IOException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.hide();
                AlertDialog.Builder builderDel = new AlertDialog.Builder(c_cheque_details.this);
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
        }){
            protected Map<String , String> getParams() throws AuthFailureError {
                Map<String , String> params = new HashMap<>();
                params.put("acc",st_id);
                params.put("txt","user");
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(c_cheque_details.this);
        requestQueue.add(stringRequest);

        requestQueue.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<Object>() {
            @Override
            public void onRequestFinished(Request<Object> request) {
                requestQueue.getCache().clear();
            }
        });
    }
    public void details(){
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                Constants.URL_CHEQUE_DETAILS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            if (!obj.getBoolean("error")) {
                                st_acc=obj.getString("acc");
                                acc.setText(st_acc);
                                r_pages.setText(obj.getString("r_pages"));
                                name.setText(obj.getString("name"));
                                mob.setText(obj.getString("mob"));
                                st_bal=obj.getString("bal");
                                st_status=obj.getString("status");
                                if (st_bal.equals("0")){
                                    bal.setText("0.00/-");
                                }else {
                                    bal.setText("₹ "+formatter.format(Long.parseLong(st_bal))+"/-");
                                }
                                if (st_status.equals("In")){
                                    ll_but.setVisibility(View.VISIBLE);
                                    status.setText(Html.fromHtml("<font color=#2EB83D><b>Pending</font></b>"));
                                }else if (st_status.equals("Out")){
                                    ll_but.setVisibility(View.GONE);
                                    status.setText(Html.fromHtml("<font color=#2EB83D><b>Ready to Deliver</font></b>"));
                                }
                                getCount(st_acc);
                            } else {
                                AlertDialog.Builder builderDel = new AlertDialog.Builder(c_cheque_details.this);
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
                        AlertDialog.Builder builderDel = new AlertDialog.Builder(c_cheque_details.this);
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
        RequestQueue requestQueue = Volley.newRequestQueue(c_cheque_details.this);
        requestQueue.add(stringRequest);

        requestQueue.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<Object>() {
            @Override
            public void onRequestFinished(Request<Object> request) {
                requestQueue.getCache().clear();
            }
        });
    }
    public void setPhoto(String s){
        AlertDialog.Builder builder = new AlertDialog.Builder(c_cheque_details.this);
        final View customLayout = getLayoutInflater().inflate(R.layout.full_image, null);
        builder.setView(customLayout);

        final PhotoView photoView=customLayout.findViewById(R.id.photo_view);
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