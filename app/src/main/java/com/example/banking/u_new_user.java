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
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
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

public class u_new_user extends AppCompatActivity {
    private Spinner spin_que;
    private String selected_que="Select Security Question:",st_pin1="Amol",st_pin2="shinde",st_acc;
    private EditText card,accno,exp,csv,ans;
    private Button register,verify;
    private ProgressDialog progressDialog;
    private SquarePinField pin1,pin2;
    private LinearLayout v,s;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_u_new_user);

        if(SharedPrefManager.getInstance(this).isRegistered()){
            Toast.makeText(u_new_user.this,"Already Registered...", Toast.LENGTH_SHORT).show();
            finishAffinity();
            startActivity(new Intent(this, home.class));
        }

        ActionBar ab = getSupportActionBar();
        ab.setTitle("New User Register:");
        ab.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00801EBD")));
        getWindow().setBackgroundDrawableResource(R.mipmap.bank8);

        spin_que = findViewById(R.id.spin_que);
        card=findViewById(R.id.et_cardno);
        exp=findViewById(R.id.et_exp);
        csv=findViewById(R.id.et_csv);
        accno=findViewById(R.id.et_accno);
        ans=findViewById(R.id.et_ans);
        register=findViewById(R.id.bt_register);
        verify=findViewById(R.id.bt_v);
        pin1=findViewById(R.id.l_pin1);
        pin2=findViewById(R.id.l_pin2);
        v=findViewById(R.id.l_v);
        s=findViewById(R.id.l_s);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);

        final String[] que = {
                "Select Security Question:",
                "What is your mother's maiden name?",
                "What is the name of your first pet?",
                "What was your first car?",
                "What elementary school did you attend?",
                "What is the name of the town where you were born?"
        };
        ArrayAdapter<CharSequence> langAdapter = new ArrayAdapter<CharSequence>(u_new_user.this, R.layout.spin_text, que);
        langAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown);
        spin_que.setAdapter(langAdapter);

        spin_que.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    selected_que = adapterView.getSelectedItem().toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {  }
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

        verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final  String st_cno=card.getText().toString().trim();
                final  String st_cno1=st_cno.replaceAll("  ", "");
                final  String st_cno2=st_cno1.replaceAll("-", "");
                final  String st_exp=exp.getText().toString().trim();
                final  String st_exp1=st_exp.replaceAll("  ", "");
                final  String st_exp2=st_exp1.replaceAll("-", "");
                final  String st_csv=csv.getText().toString().trim();
                st_acc=accno.getText().toString().trim();

                if (st_cno2.length() != 16) {
                    card.setError("Enter 16 digit Number");
                }else if (st_exp2.length() != 4) {
                    exp.setError("Enter Expiry Date");
                } else if (st_csv.isEmpty()) {
                    csv.setError("Enter CSV Number");
                } else if (st_acc.isEmpty()) {
                    accno.setError("Enter Account Number");
                } else {
                    progressDialog.show();
                    StringRequest stringRequest = new StringRequest(
                            Request.Method.POST,
                            Constants.URL_CHECK_CARD,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    progressDialog.dismiss();
                                    try {
                                        JSONObject obj = new JSONObject(response);
                                        if (!obj.getBoolean("error")) {
                                            v.setVisibility(View.GONE);
                                            s.setVisibility(View.VISIBLE);
                                        } else {
                                            AlertDialog.Builder builderDel = new AlertDialog.Builder(u_new_user.this);
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
                                    AlertDialog.Builder builderDel = new AlertDialog.Builder(u_new_user.this);
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
                            params.put("acc", st_acc);
                            return params;
                        }
                    };
                    RequestQueue requestQueue = Volley.newRequestQueue(u_new_user.this);
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

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final  String st_ans=ans.getText().toString().trim();

                if (st_pin1.equals("Amol") || st_pin2.equals("shinde")){
                    Toast.makeText(u_new_user.this,"Enter Login PIN",Toast.LENGTH_SHORT).show();
                }else if (st_ans.isEmpty()){
                    ans.setError("Enter Answer");
                }else if (selected_que.equals("Select Security Question:")){
                    Toast.makeText(u_new_user.this,"Please Select Question.",Toast.LENGTH_SHORT).show();
                }else if(st_pin1.equals(st_pin2)) {

                    progressDialog.show();
                    StringRequest stringRequest = new StringRequest(
                            Request.Method.POST,
                            Constants.URL_NEW_USER,
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
                                            AlertDialog.Builder builderDel = new AlertDialog.Builder(u_new_user.this);
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
                                            AlertDialog.Builder builderDel = new AlertDialog.Builder(u_new_user.this);
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
                                    AlertDialog.Builder builderDel = new AlertDialog.Builder(u_new_user.this);
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
                            params.put("que", selected_que);
                            params.put("ans", st_ans);
                            params.put("pin", st_pin1);
                            params.put("acc", st_acc);
                            return params;
                        }
                    };
                    RequestQueue requestQueue = Volley.newRequestQueue(u_new_user.this);
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
                    Toast.makeText(u_new_user.this,"PIN doesn't Match",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}
