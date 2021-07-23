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
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.poovam.pinedittextfield.PinField;
import com.poovam.pinedittextfield.SquarePinField;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class u_change_u_pin extends AppCompatActivity {
    private Button update;
    private ProgressDialog progressDialog;
    private SquarePinField o_p,n_p1,n_p2;
    private String o_pin="Amol",n_pin1="M",n_pin2="S",id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_u_change_u_pin);

        if(!SharedPrefManager.getInstance(this).isLoggedIn()){
            finish();
            startActivity(new Intent(this, home.class));
            return;
        }
        ActionBar ab=getSupportActionBar();
        ab.setTitle("Change UPI PIN:");
        ab.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00801EBD")));
        getWindow().setBackgroundDrawableResource(R.mipmap.back2);

        o_p=findViewById(R.id.s_o_pin);
        n_p1=findViewById(R.id.s_n_pin1);
        n_p2=findViewById(R.id.s_n_pin2);
        update=findViewById(R.id.bt_update_pin);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Updating UPI PIN...");
        progressDialog.setCancelable(false);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            id=bundle.getString("id");
        }

        o_p.setOnTextCompleteListener(new PinField.OnTextCompleteListener() {
            @Override
            public boolean onTextComplete(@NotNull String enteredText) {
                o_pin=enteredText;
                return true; // Return false to keep the keyboard open else return true to close the keyboard
            }
        });

        n_p1.setOnTextCompleteListener(new PinField.OnTextCompleteListener() {
            @Override
            public boolean onTextComplete(@NotNull String enteredText1) {
                n_pin1=enteredText1;
                return true; // Return false to keep the keyboard open else return true to close the keyboard
            }
        });

        n_p2.setOnTextCompleteListener(new PinField.OnTextCompleteListener() {
            @Override
            public boolean onTextComplete(@NotNull String enteredText2) {
                n_pin2=enteredText2;
                return true; // Return false to keep the keyboard open else return true to close the keyboard
            }
        });

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (o_pin.equals("Amol")) {
                    Toast.makeText(u_change_u_pin.this,"Enter Old UPI PIN",Toast.LENGTH_SHORT).show();
                }else if (n_pin1.equals("M")){
                    Toast.makeText(u_change_u_pin.this,"Enter New UPI PIN",Toast.LENGTH_SHORT).show();
                }else if (n_pin2.equals("S")){
                    Toast.makeText(u_change_u_pin.this,"Enter Confirm PIN",Toast.LENGTH_SHORT).show();
                }else if (n_pin1.equals(n_pin2)){

                    progressDialog.show();
                    StringRequest stringRequest = new StringRequest(
                            Request.Method.POST,
                            Constants.URL_CHANGE_UPI_PIN,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    progressDialog.dismiss();
                                    try {
                                        JSONObject obj = new JSONObject(response);
                                        if (!obj.getBoolean("error")) {
                                            AlertDialog.Builder builderDel = new AlertDialog.Builder(u_change_u_pin.this);
                                            builderDel.setCancelable(false);
                                            builderDel.setMessage(obj.getString("message"));
                                            builderDel.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    finishAffinity();
                                                    startActivity(new Intent(u_change_u_pin.this,MainActivity.class));
                                                    dialogInterface.dismiss();
                                                }
                                            });
                                            builderDel.create().show();
                                        } else {
                                            o_p.setText("");
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
                                    AlertDialog.Builder builderDel = new AlertDialog.Builder(u_change_u_pin.this);
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
                            params.put("o_pin", o_pin);
                            params.put("n_pin", n_pin1);
                            params.put("id", id);
                            return params;
                        }
                    };
                    RequestQueue requestQueue = Volley.newRequestQueue(u_change_u_pin.this);
                    requestQueue.add(stringRequest);

                    requestQueue.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<Object>() {
                        @Override
                        public void onRequestFinished(Request<Object> request) {
                            requestQueue.getCache().clear();
                        }
                    });
                }else {
                    n_p1.setText("");
                    n_p2.setText("");
                    Toast.makeText(u_change_u_pin.this,"PIN Doesn't Match..",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}