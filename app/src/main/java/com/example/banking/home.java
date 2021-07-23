package com.example.banking;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Cache;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.banking.cashier.c_dashboard;
import com.example.banking.cashier.c_deposit;
import com.example.banking.cashier.c_forgot_pass;
import com.example.banking.manager.m_dashboard;
import com.poovam.pinedittextfield.LinePinField;
import com.poovam.pinedittextfield.PinField;
import com.poovam.pinedittextfield.SquarePinField;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class home extends AppCompatActivity{
    private RadioGroup radioSexGroup;
    private RadioButton r_user;
    private LinearLayout user,bank;
    private Spinner spinner;
    private SquarePinField squarePin;
    private TextView n_register,forgot,forgot_pass;
    private String selected="Login By...",text1;
    private EditText id,pass;
    private Button login;
    private ProgressDialog progressDialog;
    private int CAMERA_PERMISSION_CODE = 1,STORAGE_PERMISSION_CODE  = 1;
    private int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        if(SharedPrefManager.getInstance(this).isLoggedIn()){
            finish();
            startActivity(new Intent(this, MainActivity.class));
            return;
        }

        if(com.example.banking.cashier.SharedPrefManager.getInstance(this).isLoggedIn1()){
            finish();
            startActivity(new Intent(this, c_dashboard.class));
            return;
        }
        if(com.example.banking.manager.SharedPrefManager.getInstance(this).isLoggedMan()){
            finish();
            startActivity(new Intent(this, m_dashboard.class));
            return;
        }

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        checkAndRequestPermissions();

        user = findViewById(R.id.l_user);
        bank = findViewById(R.id.l_bank);
        spinner = findViewById(R.id.spin_bank);
        radioSexGroup = findViewById(R.id.gender_group);
        r_user = findViewById(R.id.r_user);
        n_register = findViewById(R.id.tv_new_register);
        forgot = findViewById(R.id.tv_forgot_pin);
        forgot_pass = findViewById(R.id.tv_forgot_pass);
        id=findViewById(R.id.l_id);
        pass=findViewById(R.id.l_pass);
        login=findViewById(R.id.bt_login);
        squarePin = findViewById(R.id.log_pin);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);

        r_user.setChecked(true);
        user.setVisibility(View.VISIBLE);
        text1 = "<font color=#FF0000>Don't have a merchant account?</font> <font color=#0000FF>&nbsp;SIGN UP</font>";
        n_register.setText(Html.fromHtml(text1));

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(home.this, c_dashboard.class));
            }
        });

        n_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(home.this,u_new_user.class));
            }
        });

        forgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(home.this,u_forgot_pin.class));
            }
        });

        squarePin.setOnTextCompleteListener(new PinField.OnTextCompleteListener() {
            @Override
            public boolean onTextComplete(@NotNull String enteredText) {
                final String st_acc=SharedPrefManager.getInstance(home.this).getAccNo();
                if (st_acc.equals("amol")){
                    AlertDialog.Builder builderDel = new AlertDialog.Builder(home.this);
                    builderDel.setMessage("Please Register Before Login.");
                    builderDel.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
                    builderDel.create().show();
                    squarePin.setText("");
                }else {
                    progressDialog.show();
                    StringRequest stringRequest = new StringRequest(
                            Request.Method.POST,
                            Constants.URL_USER_LOGIN,
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
                                            Toast.makeText(home.this, obj.getString("message"), Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                            finish();
                                        } else {
                                            squarePin.setText("");
                                            AlertDialog.Builder builderDel = new AlertDialog.Builder(home.this);
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
                                    squarePin.setText("");
                                    AlertDialog.Builder builderDel = new AlertDialog.Builder(home.this);
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
                            params.put("pin", enteredText);
                            params.put("acc", SharedPrefManager.getInstance(home.this).getAccNo());
                            return params;
                        }
                    };
                    RequestQueue requestQueue = Volley.newRequestQueue(home.this);
                    requestQueue.add(stringRequest);

                    requestQueue.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<Object>() {
                        @Override
                        public void onRequestFinished(Request<Object> request) {
                            requestQueue.getCache().clear();
                        }
                    });
                }
                return true;
            }
        });

        radioSexGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.r_user:
                        user.setVisibility(View.VISIBLE);
                        bank.setVisibility(View.GONE);
                        break;
                    case R.id.r_bank:
                        bank.setVisibility(View.VISIBLE);
                        user.setVisibility(View.GONE);
                        break;
                }
            }
        });

        final String[] types = {"Login By...","Manager", "Cashier"};
        ArrayAdapter<CharSequence> langAdapter = new ArrayAdapter<CharSequence>(home.this, R.layout.spin_text, types);
        langAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown);
        spinner.setAdapter(langAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (adapterView.getId()==R.id.spin_bank) {
                    selected = adapterView.getSelectedItem().toString();
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {  }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String st_id=id.getText().toString().trim();
                final String st_pass=pass.getText().toString().trim();
                if (st_id.isEmpty()){
                    id.setError("Login ID is required");
                }else if (st_pass.isEmpty()){
                    pass.setError("Login Password is required");
                }else if (selected.equals("Login By...")){
                    Toast.makeText(home.this,"Please Select Login Type.", Toast.LENGTH_SHORT).show();
                }else {
                    progressDialog.show();
                    StringRequest stringRequest = new StringRequest(
                            Request.Method.POST,
                            Constants.URL_BANK_LOGIN,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    progressDialog.dismiss();
                                    try {
                                        JSONObject obj = new JSONObject(response);
                                        if (!obj.getBoolean("error")) {
                                            final String st_id = obj.getString("id");
                                            final String st_txt = obj.getString("txt");
                                            if (st_txt.equals("Cashier")) {
                                                com.example.banking.cashier.SharedPrefManager.getInstance(getApplicationContext())
                                                        .cashLogin(st_id);
                                                Toast.makeText(home.this, obj.getString("message"), Toast.LENGTH_SHORT).show();
                                                startActivity(new Intent(getApplicationContext(), c_dashboard.class));
                                                finish();
                                            } else {
                                                com.example.banking.manager.SharedPrefManager.getInstance(getApplicationContext())
                                                        .manLogin(st_id);
                                                Toast.makeText(home.this, obj.getString("message"), Toast.LENGTH_SHORT).show();
                                                startActivity(new Intent(getApplicationContext(), m_dashboard.class));
                                                finish();
                                            }
                                        } else {
                                            AlertDialog.Builder builderDel = new AlertDialog.Builder(home.this);
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
                                    AlertDialog.Builder builderDel = new AlertDialog.Builder(home.this);
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
                            params.put("pass", st_pass);
                            params.put("sel", selected);
                            return params;
                        }
                    };
                    RequestQueue requestQueue = Volley.newRequestQueue(home.this);
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

        forgot_pass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), c_forgot_pass.class));
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == CAMERA_PERMISSION_CODE)  {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            } else {
               permi();
            }
        }
        else if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            } else {
                permi();
            }
        }
    }

    private  boolean checkAndRequestPermissions() {
        if (ContextCompat.checkSelfPermission(home.this,Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(home.this,Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
//            Toast.makeText(home.this, "You have already granted this permission!",Toast.LENGTH_SHORT).show();
            return true;
        } else {
            int camera = ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA);
            int storage = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
            List<String> listPermissionsNeeded = new ArrayList<>();

            if (camera != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(android.Manifest.permission.CAMERA);
            }
            if (storage != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE);
            }
            if (!listPermissionsNeeded.isEmpty()) {
                ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray
                        (new String[listPermissionsNeeded.size()]), REQUEST_ID_MULTIPLE_PERMISSIONS);
                return false;
            }
            return true;
        }
    }

    public void permi(){
        AlertDialog.Builder builderDel = new AlertDialog.Builder(home.this);
        builderDel.setCancelable(false);
        builderDel.setTitle("Permissions requires");
        builderDel.setMessage("Requires permission before use.");
        builderDel.setPositiveButton("ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                intent.setData(Uri.parse("package:" + getPackageName()));
                startActivity(intent);
                home.super.onBackPressed();
            }
        });
        builderDel.create().show();
    }
}