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
import android.widget.RelativeLayout;
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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class u_forgot_pin extends AppCompatActivity {
    private Spinner spin_que;
    private String selected_que="Select Security Question:",st_pin1="Amol",st_pin2="shinde",q;
    private EditText card,mob,ans,exp,csv;;
    private Button details,submit;
    private ProgressDialog progressDialog;
    private SquarePinField pin1,pin2;
    private ArrayList<String> arrayList;
    private ArrayAdapter<String> arrayAdapter;
    private LinearLayout LL,l1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_u_forgot_pin);

        if(SharedPrefManager.getInstance(this).isLoggedIn()){
            finish();
            startActivity(new Intent(this, MainActivity.class));
        }

        ActionBar ab=getSupportActionBar();
        ab.setTitle("Forgot Login PIN:");
        ab.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00801EBD")));
        getWindow().setBackgroundDrawableResource(R.mipmap.back2);

        spin_que = findViewById(R.id.spin_que);
        card=findViewById(R.id.et_cardno);
        exp=findViewById(R.id.et_exp);
        csv=findViewById(R.id.et_csv);
        mob=findViewById(R.id.et_mobno);
        ans=findViewById(R.id.et_ans);
        details=findViewById(R.id.c_details);
        submit=findViewById(R.id.submit);
        pin1=findViewById(R.id.s_n_pin1);
        pin2=findViewById(R.id.s_n_pin2);
        LL=findViewById(R.id.LL);
        l1=findViewById(R.id.L1);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);

        arrayList= new ArrayList<String>();
        arrayAdapter= new ArrayAdapter<String>(u_forgot_pin.this, R.layout.spin_text, arrayList);
        arrayAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown);
        spin_que.setAdapter(arrayAdapter);

        spin_que.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selected_que = adapterView.getSelectedItem().toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {  }
        });

        mob.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                final  String st_cno=card.getText().toString().trim();
                final  String st_cno1=st_cno.replaceAll("  ", "");
                final  String st_cno2=st_cno1.replaceAll("-", "");
                final  String st_exp=exp.getText().toString().trim();
                final  String st_exp1=st_exp.replaceAll("  ", "");
                final  String st_exp2=st_exp1.replaceAll("-", "");
                final  String st_csv=csv.getText().toString().trim();
                final  String st_mob=mob.getText().toString().trim();

                if (st_cno2.length() != 16) {
                    card.setError("Enter 16 digit Number");
                }else if (st_exp2.length() != 4) {
                    exp.setError("Enter Expiry Date");
                } else if (st_csv.isEmpty()) {
                    csv.setError("Enter CSV Number");
                } else if (st_mob.length() !=10){
                    mob.setError("Enter 10 digit Number");
                }else {
                    arrayList.clear();
                        progressDialog.show();
                        StringRequest stringRequest = new StringRequest(
                                Request.Method.POST,
                                Constants.URL_CHECK_DETAILS,
                                new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        progressDialog.dismiss();
                                        try {
                                            JSONObject obj = new JSONObject(response);
                                            if (!obj.getBoolean("error")) {
                                                q=obj.getString("que");
                                                    arrayList.add("Select Security Question:");
                                                    arrayList.add(q);
                                                    spin_que.setAdapter(arrayAdapter);
                                                    card.setEnabled(false);
                                                    exp.setEnabled(false);
                                                    csv.setEnabled(false);
                                                    mob.setEnabled(false);
                                                    details.setEnabled(true);
                                            } else {
                                                arrayList.clear();
                                                spin_que.setAdapter(arrayAdapter);
                                                AlertDialog.Builder builderDel = new AlertDialog.Builder(u_forgot_pin.this);
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
                                        AlertDialog.Builder builderDel = new AlertDialog.Builder(u_forgot_pin.this);
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
                                params.put("card", st_cno2);
                                params.put("exp", st_exp2);
                                params.put("csv", st_csv);
                                params.put("mob",st_mob);
                                return params;
                            }
                        };
                    RequestQueue requestQueue = Volley.newRequestQueue(u_forgot_pin.this);
                    requestQueue.add(stringRequest);

                    requestQueue.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<Object>() {
                        @Override
                        public void onRequestFinished(Request<Object> request) {
                            requestQueue.getCache().clear();
                        }
                    });
                }
            }
            public void afterTextChanged(Editable s) {
            }
        });

        details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final  String st_cno=card.getText().toString().trim();
                final  String st_cno1=st_cno.replaceAll("  ", "");
                final  String st_cno2=st_cno1.replaceAll("-", "");
                final  String st_ans=ans.getText().toString().trim();
                if (selected_que.equals("Select Security Question:")){
                    Toast.makeText(u_forgot_pin.this,"Please Select Que",Toast.LENGTH_SHORT).show();
                }else if (st_ans.isEmpty()){
                    ans.setError("Please Enter Que Answer");
                }else {
                    progressDialog.show();
                    StringRequest stringRequest = new StringRequest(
                            Request.Method.POST,
                            Constants.URL_CHECK_QUE,
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
                                            AlertDialog.Builder builderDel = new AlertDialog.Builder(u_forgot_pin.this);
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
                                    AlertDialog.Builder builderDel = new AlertDialog.Builder(u_forgot_pin.this);
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
                            params.put("card", st_cno2);
                            params.put("que", selected_que);
                            params.put("ans", st_ans);
                            return params;
                        }
                    };
                    RequestQueue requestQueue = Volley.newRequestQueue(u_forgot_pin.this);
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
                final  String st_cno=card.getText().toString().trim();
                final  String st_cno1=st_cno.replaceAll("  ", "");
                final  String st_cno2=st_cno1.replaceAll("-", "");
                if (st_pin1.equals("Amol") || st_pin2.equals("shinde")){
                    Toast.makeText(u_forgot_pin.this,"Enter Pin",Toast.LENGTH_SHORT).show();
                }else if(st_pin1.equals(st_pin2)) {

                    progressDialog.show();
                    StringRequest stringRequest = new StringRequest(
                            Request.Method.POST,
                            Constants.URL_FORGOT_PIN,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    progressDialog.dismiss();
                                    try {
                                        JSONObject obj = new JSONObject(response);
                                        if (!obj.getBoolean("error")) {
                                            SharedPrefManager.getInstance(getApplicationContext())
                                                    .userLogin(
                                                            obj.getString("accno"),
                                                            obj.getString("accno1"),
                                                            obj.getString("name")
                                                    );
                                            AlertDialog.Builder builderDel = new AlertDialog.Builder(u_forgot_pin.this);
                                            builderDel.setMessage(obj.getString("message"));
                                            builderDel.setCancelable(false);
                                            builderDel.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    dialogInterface.dismiss();
                                                    finishAffinity();
                                                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                                }
                                            });
                                            builderDel.create().show();
                                        } else {
                                            AlertDialog.Builder builderDel = new AlertDialog.Builder(u_forgot_pin.this);
                                            builderDel.setMessage(obj.getString("message"));
                                            builderDel.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    dialogInterface.dismiss();
                                                    finishAffinity();
                                                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
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
                                    AlertDialog.Builder builderDel = new AlertDialog.Builder(u_forgot_pin.this);
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
                            params.put("card",st_cno2);
                            params.put("pin", st_pin1);
                            return params;
                        }
                    };
                    RequestQueue requestQueue = Volley.newRequestQueue(u_forgot_pin.this);
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
                    Toast.makeText(u_forgot_pin.this,"PIN doesn't Match",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}