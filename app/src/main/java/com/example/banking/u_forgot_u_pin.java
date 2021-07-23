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
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class u_forgot_u_pin extends AppCompatActivity {
    private Spinner spin_que;
    private String selected_que="Select Security Question:",st_pin1="Amol",st_pin2="shinde",q1;
    private EditText card,ans;
    private Button verify,submit;
    private ProgressDialog progressDialog;
    private SquarePinField pin1,pin2;
    private ArrayList<String> arrayList;
    private ArrayAdapter<String> arrayAdapter;
    private LinearLayout LL,l1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_u_forgot_u_pin);

        if(!SharedPrefManager.getInstance(this).isLoggedIn()){
            finish();
            startActivity(new Intent(this, home.class));
            return;
        }
        ActionBar ab=getSupportActionBar();
        ab.setTitle("Forgot UPI PIN:");
        ab.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00801EBD")));
        getWindow().setBackgroundDrawableResource(R.mipmap.back2);

        spin_que = findViewById(R.id.spin_que);
        card=findViewById(R.id.et_cardno);
        ans=findViewById(R.id.et_ans);
        verify=findViewById(R.id.bt_verify);

        submit=findViewById(R.id.submit);
        pin1=findViewById(R.id.s_n_pin1);
        pin2=findViewById(R.id.s_n_pin2);
        LL=findViewById(R.id.LL);
        l1=findViewById(R.id.L1);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);

        arrayList= new ArrayList<String>();
        arrayAdapter= new ArrayAdapter<String>(u_forgot_u_pin.this, R.layout.spin_text, arrayList);
        arrayAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown);

        spin_que.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selected_que = adapterView.getSelectedItem().toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {  }
        });

        card.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                final  String st_cno=card.getText().toString().trim();
                if (st_cno.length() != 16) {
                    arrayList.clear();
                    card.setError("Enter 16 digit Number");
                }else {
                    progressDialog.show();
                    StringRequest stringRequest = new StringRequest(
                            Request.Method.POST,
                            Constants.URL_CHECK_UPI_DETAILS,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    progressDialog.dismiss();
                                    try {
                                        JSONObject obj = new JSONObject(response);
                                        if (!obj.getBoolean("error")) {
                                            q1=obj.getString("que");
                                            arrayList.add("Select Security Question:");
                                            arrayList.add(q1);
                                            spin_que.setAdapter(arrayAdapter);
                                        } else {
                                            arrayList.clear();
                                            spin_que.setAdapter(arrayAdapter);
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
                                    AlertDialog.Builder builderDel = new AlertDialog.Builder(u_forgot_u_pin.this);
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
                            params.put("card", st_cno);
                            params.put("acc",SharedPrefManager.getInstance(u_forgot_u_pin.this).getAccNo());
                            return params;
                        }
                    };
                    RequestQueue requestQueue = Volley.newRequestQueue(u_forgot_u_pin.this);
                    requestQueue.add(stringRequest);

                    requestQueue.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<Object>() {
                        @Override
                        public void onRequestFinished(Request<Object> request) {
                            requestQueue.getCache().clear();
                        }
                    });
                }
            }
            public void afterTextChanged(Editable s) { }
        });

        verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final  String st_card1=card.getText().toString().trim();
                final  String st_ans=ans.getText().toString().trim();
                if (selected_que.equals("Select Security Question:")){
                    Toast.makeText(u_forgot_u_pin.this,"Please Select Question",Toast.LENGTH_SHORT).show();
                }else if (st_ans.isEmpty()){
                    ans.setError("Please Enter Answer");
                }else {
                    progressDialog.show();
                    StringRequest stringRequest = new StringRequest(
                            Request.Method.POST,
                            Constants.URL_CHECK_U_QUE,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    progressDialog.dismiss();
                                    try {
                                        JSONObject obj = new JSONObject(response);
                                        if (!obj.getBoolean("error")) {
                                            l1.setVisibility(View.GONE);
                                            LL.setVisibility(View.VISIBLE);
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
                                    AlertDialog.Builder builderDel = new AlertDialog.Builder(u_forgot_u_pin.this);
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
                            params.put("card", st_card1);
                            params.put("que", selected_que);
                            params.put("ans", st_ans);
                            return params;
                        }
                    };
                    RequestQueue requestQueue = Volley.newRequestQueue(u_forgot_u_pin.this);
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

        pin1.setOnTextCompleteListener(new PinField.OnTextCompleteListener() {
            @Override
            public boolean onTextComplete(@NotNull String enteredText1) {
                st_pin1=enteredText1;
                return true; // Return false to keep the keyboard open else return true to close the keyboard
            }
        });

        pin2.setOnTextCompleteListener(new PinField.OnTextCompleteListener() {
            @Override
            public boolean onTextComplete(@NotNull String enteredText2) {
                st_pin2=enteredText2;
                return true; // Return false to keep the keyboard open else return true to close the keyboard
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final  String st_card2=card.getText().toString().trim();
                if (st_pin1.equals("Amol") || st_pin2.equals("shinde")){
                    Toast.makeText(u_forgot_u_pin.this,"Enter PIN.",Toast.LENGTH_SHORT).show();
                }else if(st_pin1.equals(st_pin2)) {

                    progressDialog.show();
                    StringRequest stringRequest = new StringRequest(
                            Request.Method.POST,
                            Constants.URL_FORGOT_U_PIN,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    progressDialog.dismiss();
                                    try {
                                        JSONObject obj = new JSONObject(response);
                                        if (!obj.getBoolean("error")) {
                                            AlertDialog.Builder builderDel = new AlertDialog.Builder(u_forgot_u_pin.this);
                                            builderDel.setCancelable(false);
                                            builderDel.setMessage(obj.getString("message"));
                                            builderDel.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    finishAffinity();
                                                    startActivity(new Intent(u_forgot_u_pin.this,MainActivity.class));
                                                    dialogInterface.dismiss();
                                                }
                                            });
                                            builderDel.create().show();
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
                                    AlertDialog.Builder builderDel = new AlertDialog.Builder(u_forgot_u_pin.this);
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
                            params.put("card",st_card2);
                            params.put("pin", st_pin1);
                            return params;
                        }
                    };
                    RequestQueue requestQueue = Volley.newRequestQueue(u_forgot_u_pin.this);
                    requestQueue.add(stringRequest);

                    requestQueue.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<Object>() {
                        @Override
                        public void onRequestFinished(Request<Object> request) {
                            requestQueue.getCache().clear();
                        }
                    });
                }else {
                    pin1.setText("");
                    pin2.setText("");
                    Toast.makeText(u_forgot_u_pin.this,"PIN doesn't Match...",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}