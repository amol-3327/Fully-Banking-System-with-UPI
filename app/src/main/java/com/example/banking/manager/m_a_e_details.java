package com.example.banking.manager;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.Html;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.View;
import android.view.Window;
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
import com.example.banking.MainActivity;
import com.example.banking.R;
import com.example.banking.cashier.c_withdraw;
import com.example.banking.home;
import com.example.banking.payment_gateway;
import com.example.banking.u_apply_loan;
import com.example.banking.u_change_u_pin;
import com.github.chrisbanes.photoview.PhotoView;
import com.google.android.material.textfield.TextInputLayout;
import com.poovam.pinedittextfield.SquarePinField;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class m_a_e_details extends AppCompatActivity {
    private String st_acc,st_mob,st_address,st_email,st_u_date,st_u_dob,st_rmk,st_e_sal,st_photo,st_sign,encodedImage,st_post;
    private TextView acc,name,address,email,mob,dob,date,c_status,e_name,e_dob,e_mob,e_email,e_address,e_salary,e_j_date,e_post;
    private TextView upload_photo,upload_sign,l_id,l_pass,card,exp,csv;
    private ProgressDialog progressDialog;
    private SimpleDateFormat dateFormat,dateFormat1;
    private LinearLayout ll_account,ll_employee,ll_photos,ll_details;
    private DecimalFormat formatter;
    private CardView c_mob,c_email,c_address,c_e_mob,c_e_email,c_e_address,c_e_sal,u_photo,u_sign,c_details;
    Dialog dialog;
    private ImageView i_photo,i_sign,i_e_photo,n_photo,n_sign;
    private int PICK_IMAGE;
    Uri imageUri;
    Bitmap bitmap;
    private double img_p_len=1000000000,img_s_len=1000000000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_m_a_e_details);

        if(!SharedPrefManager.getInstance(this).isLoggedMan()){
            finish();
            startActivity(new Intent(this, home.class));
            return;
        }

        i_photo=findViewById(R.id.iv_photo);
        i_e_photo=findViewById(R.id.iv_photo1);
        i_sign=findViewById(R.id.iv_sign);
        acc=findViewById(R.id.tv_acc);
        name=findViewById(R.id.tv_name);
        dob=findViewById(R.id.tv_dob);
        mob=findViewById(R.id.tv_mob);
        email=findViewById(R.id.tv_email);
        address=findViewById(R.id.tv_address);
        date=findViewById(R.id.tv_c_date);
        c_status=findViewById(R.id.tv_card_status);
        u_photo=findViewById(R.id.c_u_photo);
        u_sign=findViewById(R.id.c_u_sign);
        n_photo=findViewById(R.id.iv_n_photo);
        n_sign=findViewById(R.id.iv_n_sign);
        upload_photo=findViewById(R.id.tv_u_photo);
        upload_sign=findViewById(R.id.tv_u_sign);
        card=findViewById(R.id.tv_card);
        exp =findViewById(R.id.tv_exp);
        csv=findViewById(R.id.tv_csv);
        c_details=findViewById(R.id.c_details);
        ll_account=findViewById(R.id.ll_account);
        ll_employee=findViewById(R.id.ll_employee);
        ll_photos=findViewById(R.id.ll_photos);
        ll_details=findViewById(R.id.ll_l_details);

        c_mob=findViewById(R.id.c_mob);
        c_email=findViewById(R.id.c_email);
        c_address=findViewById(R.id.c_address);
        c_e_mob=findViewById(R.id.c_e_mob);
        c_e_email=findViewById(R.id.c_e_email);
        c_e_address=findViewById(R.id.c_e_address);
        c_e_sal=findViewById(R.id.c_e_sal);

        e_name=findViewById(R.id.tv_e_name);
        e_dob=findViewById(R.id.tv_e_dob);
        e_mob=findViewById(R.id.tv_e_mob);
        e_email=findViewById(R.id.tv_e_email);
        e_address=findViewById(R.id.tv_e_address);
        e_salary=findViewById(R.id.tv_e_salary);
        e_j_date=findViewById(R.id.tv_j_date);
        e_post=findViewById(R.id.tv_e_post);
        l_id=findViewById(R.id.tv_e_l_id);
        l_pass=findViewById(R.id.tv_e_pass);

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
            st_acc = getIntent().getStringExtra("acc");
            st_rmk=bundle.getString("rmk");
        }

        if (st_rmk.equals("user")){
            getSupportActionBar().setTitle(Html.fromHtml("<font color=#000000>Account Details :</font>"));
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#ADF48004")));
            ll_account.setVisibility(View.VISIBLE);
            users();
        }else {
            getSupportActionBar().setTitle(Html.fromHtml("<font color=#000000>Employee Details :</font>"));
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#ADF48004")));
            ll_employee.setVisibility(View.VISIBLE);
            employee();
        }
        i_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                final CharSequence[] dialogitem = {"View photo","Change photo"};
                builder.setItems(dialogitem, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        switch (i){
                            case 0:
                                setPhoto("photo");
                            break;
                            case 1:
                                u_photo.setVisibility(View.VISIBLE);
                                ll_photos.setVisibility(View.GONE);
                            break;
                        }
                    }
                });
                builder.create().show();
            }
        });
        i_e_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setPhoto("e_photo");
            }
        });
        i_sign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                final CharSequence[] dialogitem = {"View photo","Change photo"};
                builder.setItems(dialogitem, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        switch (i){
                            case 0:
                                setPhoto("sign");
                            break;
                            case 1:
                                u_sign.setVisibility(View.VISIBLE);
                                ll_photos.setVisibility(View.GONE);
                                break;
                        }
                    }
                });
                builder.create().show();
            }
        });
        c_mob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { edit_alert("mob"); }
        });
        c_email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { edit_alert("email"); }
        });
        c_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { edit_alert("address"); }
        });
        c_e_mob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { edit_alert("e_mob"); }
        });
        c_e_email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { edit_alert("e_email"); }
        });
        c_e_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { edit_alert("e_address"); }
        });
        c_e_sal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { edit_alert("e_sal"); }
        });
        upload_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setImage("photo");
            }
        });
        upload_sign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setImage("sign");
            }
        });
        n_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setPhoto("photo");
            }
        });
        n_sign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setPhoto("sign");
            }
        });
    }

    public void users(){
        ll_photos.setVisibility(View.VISIBLE);
        u_photo.setVisibility(View.GONE);
        u_sign.setVisibility(View.GONE);
        progressDialog.show();
        StringRequest stringRequest=new StringRequest(Request.Method.POST,Constants.URL_U_E_DETAILS, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject obj = new JSONObject(response);
                    if (!obj.getBoolean("error")) {
                        name.setText(obj.getString("name"));
                        st_u_dob=obj.getString("dob");
                        st_mob=obj.getString("mob");
                        mob.setText(st_mob);
                        st_email=obj.getString("email");
                        email.setText(st_email);
                        st_address=obj.getString("address");
                        address.setText(st_address);
                        st_u_date=obj.getString("date");
                        if ((obj.getString("card")).equals("null")){
                            c_status.setText(Html.fromHtml("<font color=#FF0000><b>Don't Have Card</font></b>"));
                            c_details.setVisibility(View.GONE);
                        }else {
                            c_status.setText(Html.fromHtml("<font color=#2EB83D><b>Already Have Card</font></b>"));
                            c_details.setVisibility(View.VISIBLE);
                        }
                        st_photo=obj.getString("photo");
                        st_sign=obj.getString("sign");
                        card.setText(obj.getString("card"));
                        exp.setText(obj.getString("exp"));
                        csv.setText(obj.getString("csv"));

                        Date fechaNueva = dateFormat.parse(st_u_date);
                        date.setText(dateFormat1.format(fechaNueva));

                        Date fechaNueva1 = dateFormat.parse(st_u_dob);
                        dob.setText(dateFormat1.format(fechaNueva1));
                        acc.setText(st_acc);

                        URL url = new URL(st_photo);
                        Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                        i_photo.setImageBitmap(bmp);

                        URL url1 = new URL(st_sign);
                        Bitmap bmp1 = BitmapFactory.decodeStream(url1.openConnection().getInputStream());
                        i_sign.setImageBitmap(bmp1);
                        progressDialog.dismiss();
                    }else{
                        progressDialog.dismiss();
                        AlertDialog.Builder builderDel = new AlertDialog.Builder(m_a_e_details.this);
                        builderDel.setMessage(obj.getString("message"));
                        builderDel.setCancelable(false);
                        builderDel.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                                finishAffinity();
                                startActivity(new Intent(m_a_e_details.this, MainActivity.class));
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
                AlertDialog.Builder builderDel = new AlertDialog.Builder(m_a_e_details.this);
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
                params.put("acc",st_acc);
                params.put("txt","user");
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(m_a_e_details.this);
        requestQueue.add(stringRequest);

        requestQueue.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<Object>() {
            @Override
            public void onRequestFinished(Request<Object> request) {
                requestQueue.getCache().clear();
            }
        });
    }
    private void employee() {
        progressDialog.show();
        StringRequest stringRequest=new StringRequest(Request.Method.POST,Constants.URL_U_E_DETAILS, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject obj = new JSONObject(response);
                    if (!obj.getBoolean("error")) {
                        e_name.setText(obj.getString("name"));
                        st_u_dob=obj.getString("dob");
                        st_mob=obj.getString("mob");
                        e_mob.setText(st_mob);
                        st_email=obj.getString("email");
                        e_email.setText(st_email);
                        st_address=obj.getString("address");
                        e_address.setText(st_address);
                        st_e_sal=obj.getString("salary");
                        st_u_date=obj.getString("date");
                        st_post=obj.getString("post");
                        e_post.setText(st_post);
                        st_photo=obj.getString("photo");
                        l_id.setText(obj.getString("id"));
                        l_pass.setText(obj.getString("pass"));
                        if (st_post.equals("Watchman")){
                            ll_details.setVisibility(View.GONE);
                        }else {
                            ll_details.setVisibility(View.VISIBLE);
                        }

                        Date fechaNueva = dateFormat.parse(st_u_date);
                        e_j_date.setText(dateFormat1.format(fechaNueva));

                        Date fechaNueva1 = dateFormat.parse(st_u_dob);
                        e_dob.setText(dateFormat1.format(fechaNueva1));
                        e_salary.setText("₹ "+formatter.format(Long.parseLong(st_e_sal))+"/-");

                        URL url1 = new URL(st_photo);
                        Bitmap bmp1 = BitmapFactory.decodeStream(url1.openConnection().getInputStream());
                        i_e_photo.setImageBitmap(bmp1);
                        progressDialog.dismiss();
                    }else{
                        progressDialog.dismiss();
                        AlertDialog.Builder builderDel = new AlertDialog.Builder(m_a_e_details.this);
                        builderDel.setMessage(obj.getString("message"));
                        builderDel.setCancelable(false);
                        builderDel.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                                finishAffinity();
                                startActivity(new Intent(m_a_e_details.this, MainActivity.class));
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
                AlertDialog.Builder builderDel = new AlertDialog.Builder(m_a_e_details.this);
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
                params.put("acc",st_acc);
                params.put("txt","employee");
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(m_a_e_details.this);
        requestQueue.add(stringRequest);

        requestQueue.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<Object>() {
            @Override
            public void onRequestFinished(Request<Object> request) {
                requestQueue.getCache().clear();
            }
        });
    }

    public void edit_alert(String st) {
        dialog = new Dialog(m_a_e_details.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        final View customLayout = getLayoutInflater().inflate(R.layout.alert_update_details, null);
        dialog.setContentView(customLayout);

        ImageView close=(customLayout).findViewById(R.id.i_close);
        CardView emp_user= (CardView) customLayout.findViewById(R.id.c_emp_user);
        CardView c_type= (CardView) customLayout.findViewById(R.id.c_type);
        TextView type= (TextView) customLayout.findViewById(R.id.tv_type);
        TextView value= (TextView) customLayout.findViewById(R.id.tv_value);
        EditText input= (EditText) customLayout.findViewById(R.id.et_input);
        EditText sal= (EditText) customLayout.findViewById(R.id.et_sal);
        Button update=customLayout.findViewById(R.id.bt_update);
        TextInputLayout ll_input=customLayout.findViewById(R.id.ll_input);
        TextInputLayout ll_sal=customLayout.findViewById(R.id.ll_sal);

        emp_user.setVisibility(View.VISIBLE);
        c_type.setVisibility(View.GONE);

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        sal.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void onTextChanged(CharSequence sw, int i, int i1, int i2) {
                sal.removeTextChangedListener(this);
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
                    sal.setText(formattedString);
                    sal.setSelection(sal.getText().length());
                }catch (NumberFormatException nfe){
                    nfe.printStackTrace();
                }sal.addTextChangedListener(this);
            }
            @Override
            public void afterTextChanged(Editable editable) { }
        });

        if (st.equals("mob")) {
            type.setText("Current Number :");
            value.setText(st_mob);
            input.setHint("Enter New Mobile No.*");
            input.setInputType(InputType.TYPE_CLASS_NUMBER);
            input.setFilters(new InputFilter[]{new InputFilter.LengthFilter(10)});
        }else if (st.equals("email")){
            type.setText("Current Email :");
            value.setText(st_email);
            input.setHint("Enter New Email ID.*");
            input.setInputType(InputType.TYPE_CLASS_TEXT);
        }else if (st.equals("address")){
            type.setText("Current Address :");
            value.setText(st_address);
            input.setHint("Enter New Address.*");
            input.setInputType(InputType.TYPE_CLASS_TEXT);
        } else if (st.equals("e_mob")) {
            type.setText("Current Number :");
            value.setText(st_mob);
            input.setHint("Enter New Mobile No.*");
            input.setInputType(InputType.TYPE_CLASS_NUMBER);
            input.setFilters(new InputFilter[]{new InputFilter.LengthFilter(10)});
        }else if (st.equals("e_email")){
            type.setText("Current Email :");
            value.setText(st_email);
            input.setHint("Enter New Email ID.*");
            input.setInputType(InputType.TYPE_CLASS_TEXT);
        }else if (st.equals("e_address")){
            type.setText("Current Address :");
            value.setText(st_address);
            input.setHint("Enter New Address.*");
            input.setInputType(InputType.TYPE_CLASS_TEXT);
        } else if (st.equals("e_sal")){
            ll_input.setVisibility(View.GONE);
            ll_sal.setVisibility(View.VISIBLE);
            type.setText("Current Salary :");
            value.setText("₹ "+formatter.format(Long.parseLong(st_e_sal))+"/-");
            sal.setHint("Enter New Salary Amount.*");
            sal.setInputType(InputType.TYPE_CLASS_NUMBER);
        }

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder1 = new AlertDialog.Builder(m_a_e_details.this);
                builder1.setMessage("Are you sure, you want to update?");
                builder1.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        final String data=input.getText().toString().trim();
                        if (st.equals("mob")) {
                            if (data.length() != 10) {
                                input.setError("Enter Mobile Number");
                            } else {
                                update_data(st, data, st_acc);
                            }
                        }else if (st.equals("email")){
                            if (data.isEmpty()) {
                                input.setError("Enter Email Id");
                            }else {
                                update_data(st,data,st_acc);
                            }
                        }else if (st.equals("address")){
                            if (data.isEmpty()) {
                                input.setError("Enter Address");
                            }else {
                                update_data(st,data,st_acc);
                            }
                        }else if (st.equals("e_mob")) {
                            if (data.length() != 10) {
                                input.setError("Enter Mobile Number");
                            } else {
                                update_data(st, data, st_acc);
                            }
                        }else if (st.equals("e_email")){
                            if (data.isEmpty()) {
                                input.setError("Enter Email Id");
                            }else {
                                update_data(st,data,st_acc);
                            }
                        }else if (st.equals("e_address")){
                            if (data.isEmpty()) {
                                input.setError("Enter Address");
                            }else {
                                update_data(st,data,st_acc);
                            }
                        } else if (st.equals("e_sal")){
                            final String st_sal=sal.getText().toString().trim();
                            final String st_sal1=st_sal.replaceAll(",", "");
                            if (st_sal1.equals("0") || st_sal1.isEmpty()) {
                                sal.setError("Enter amount is greater than 0 ");
                            }else {
                                update_data(st,st_sal1,st_acc);
                            }
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
        });
        dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        dialog.show();
    }

    public void update_data(String st1,String data1,String acc_no){
        progressDialog.show();
        StringRequest stringRequest=new StringRequest(Request.Method.POST,Constants.URL_UPDATE_EMP_USER, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();
                try {
                    JSONObject obj = new JSONObject(response);
                    if (!obj.getBoolean("error")) {
                        dialog.dismiss();
                        AlertDialog.Builder builderDel = new AlertDialog.Builder(m_a_e_details.this);
                        builderDel.setMessage(obj.getString("message"));
                        builderDel.setCancelable(false);
                        builderDel.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                                AccountFragement.ff.refresh_list();
                                EmployeeFragement.ff.refresh_list();
                                finish();
                            }
                        });
                        builderDel.create().show();
                    }else{
                        AlertDialog.Builder builderDel = new AlertDialog.Builder(m_a_e_details.this);
                        builderDel.setMessage(obj.getString("message"));
                        builderDel.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });
                        builderDel.create().show();
                    }
                }catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.hide();
                AlertDialog.Builder builderDel = new AlertDialog.Builder(m_a_e_details.this);
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
                params.put("txt",st1);
                params.put("data",data1);
                params.put("acc",acc_no);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(m_a_e_details.this);
        requestQueue.add(stringRequest);

        requestQueue.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<Object>() {
            @Override
            public void onRequestFinished(Request<Object> request) {
                requestQueue.getCache().clear();
            }
        });
    }
    public void setPhoto(String s){
        AlertDialog.Builder builder = new AlertDialog.Builder(m_a_e_details.this);
        final View customLayout = getLayoutInflater().inflate(R.layout.full_image, null);
        builder.setView(customLayout);

        final PhotoView photoView=customLayout.findViewById(R.id.photo_view);
        if (s.equals("photo")){
            photoView.setScaleType(PhotoView.ScaleType.CENTER_CROP);
            photoView.setImageDrawable(i_photo.getDrawable());
        }else if (s.equals("e_photo")){
            photoView.setScaleType(PhotoView.ScaleType.CENTER_CROP);
            photoView.setImageDrawable(i_e_photo.getDrawable());
        }else {
            photoView.setScaleType(PhotoView.ScaleType.CENTER_CROP);
            photoView.setImageDrawable(i_sign.getDrawable());
        }
        AlertDialog dialog = builder.create();
        dialog.show();
    }
    public void setImage(String s){
        if (s.equals("photo")) {
            PICK_IMAGE=1;
        }else {
            PICK_IMAGE=2;
        }
        Intent gallery1 = new Intent();
        gallery1.setType("image/*");
        gallery1.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(gallery1, "Select Picture1"), PICK_IMAGE);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && data != null) {
            if(resultCode == RESULT_OK) {
                imageUri = data.getData();
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                    File file = new File(imageUri.getLastPathSegment());
                    img_p_len=file.length();
                    if (img_p_len < 400000) {
                        n_photo.setImageBitmap(bitmap);
                        savePhoto((imageStore(bitmap)),"photo");
                    }else {
                        Toast.makeText(getApplicationContext(),"File size should be in between 15 KB to 400 KB", Toast.LENGTH_LONG).show();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (resultCode == RESULT_CANCELED) { }
        }
        if (requestCode == 2 && data != null) {
            if(resultCode == RESULT_OK) {
                imageUri = data.getData();
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                    File file1 = new File(imageUri.getLastPathSegment());
                    img_s_len=file1.length();
                    if (img_s_len < 400000) {
                        n_sign.setImageBitmap(bitmap);
                        savePhoto((imageStore(bitmap)),"sign");
                    }else {
                        Toast.makeText(getApplicationContext(),"File size should be in between 15 KB to 400 KB", Toast.LENGTH_LONG).show();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (resultCode == RESULT_CANCELED) { }
        }
    }
    public String imageStore(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,stream);
        byte[] imageBytes = stream.toByteArray();
        encodedImage = android.util.Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }
    public void savePhoto(String img,String st){
        progressDialog.show();
        StringRequest stringRequest=new StringRequest(Request.Method.POST,Constants.URL_UPDATE_IMAGES, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();
                try {
                    JSONObject obj = new JSONObject(response);
                    if (!obj.getBoolean("error")) {
                        AlertDialog.Builder builderDel = new AlertDialog.Builder(m_a_e_details.this);
                        builderDel.setMessage(obj.getString("message"));
                        builderDel.setCancelable(false);
                        builderDel.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                                AccountFragement.ff.refresh_list();
                                users();
                            }
                        });
                        builderDel.create().show();
                    }else{
                        AlertDialog.Builder builderDel = new AlertDialog.Builder(m_a_e_details.this);
                        builderDel.setMessage(obj.getString("message"));
                        builderDel.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });
                        builderDel.create().show();
                    }
                }catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.hide();
                AlertDialog.Builder builderDel = new AlertDialog.Builder(m_a_e_details.this);
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
                params.put("txt",st);
                params.put("img",img);
                params.put("acc",st_acc);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(m_a_e_details.this);
        requestQueue.add(stringRequest);

        requestQueue.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<Object>() {
            @Override
            public void onRequestFinished(Request<Object> request) {
                requestQueue.getCache().clear();
            }
        });
    }
}