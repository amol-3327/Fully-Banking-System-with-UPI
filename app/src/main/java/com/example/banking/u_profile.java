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
import android.view.View;
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
import org.w3c.dom.Text;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class u_profile extends AppCompatActivity {
    private ProgressDialog progressDialog;
    private TextView acc,ifsc,name,address,dob,mob;
    private SimpleDateFormat dateFormat,dateFormat1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_u_profile);

        if(!SharedPrefManager.getInstance(this).isLoggedIn()){
            finish();
            startActivity(new Intent(this, home.class));
            return;
        }
        ActionBar ab=getSupportActionBar();
        ab.setTitle("My Profile:");
        ab.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00801EBD")));
        getWindow().setBackgroundDrawableResource(R.mipmap.back2);

        acc=findViewById(R.id.tv_acc);
        ifsc=findViewById(R.id.tv_ifsc);
        name=findViewById(R.id.tv_hname);
        address=findViewById(R.id.tv_address);
        dob=findViewById(R.id.tv_dob);
        mob=findViewById(R.id.tv_mob);

        dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat1 = new SimpleDateFormat("dd-MMM-yyyy");

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);

        progressDialog.show();
        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                Constants.URL_PROFILE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        try {
                            JSONObject obj = new JSONObject(response);
                            if (!obj.getBoolean("error")) {
                                final String st_acc=obj.getString("acc");
                                final String st_ifsc=obj.getString("ifsc");
                                final String st_name=obj.getString("name");
                                final String st_address=obj.getString("address");
                                final String st_dob=obj.getString("dob");
                                final String st_mob=obj.getString("mob");

                                acc.setText(st_acc);
                                ifsc.setText(st_ifsc);
                                name.setText(st_name.toUpperCase());
                                address.setText(st_address);
                                Date fechaNueva = dateFormat.parse(st_dob);
                                dob.setText(dateFormat1.format(fechaNueva));

                                String lastFourDigits = "";     //substring containing last 4 characters
                                if (st_mob.length() > 4) {
                                    lastFourDigits = st_mob.substring(st_mob.length() - 4);
                                } else {
                                    lastFourDigits = st_mob;
                                }
                                mob.setText("XXXXXX"+lastFourDigits);
                            } else {
                                Toast.makeText(getApplicationContext(),obj.getString("message"),Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException | ParseException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        AlertDialog.Builder builderDel = new AlertDialog.Builder(u_profile.this);
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
                params.put("id", SharedPrefManager.getInstance(u_profile.this).getAccNo());
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(u_profile.this);
        requestQueue.add(stringRequest);

        requestQueue.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<Object>() {
            @Override
            public void onRequestFinished(Request<Object> request) {
                requestQueue.getCache().clear();
            }
        });
    }
}