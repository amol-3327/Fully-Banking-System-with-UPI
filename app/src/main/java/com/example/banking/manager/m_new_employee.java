package com.example.banking.manager;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.Html;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.example.banking.R;
import com.example.banking.cashier.Constants;
import com.example.banking.cashier.c_dashboard;
import com.example.banking.cashier.c_new_account;
import com.example.banking.home;
import com.github.chrisbanes.photoview.PhotoView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class m_new_employee extends AppCompatActivity {
    private Spinner post,gender;
    private DatePickerDialog picker;
    private TextView acc,ed_dob,photo,sign;
    private ImageView i_photo,i_sign;
    private EditText name,mob,email,address,amount,sal,l_id,l_pass;
    private Button create;
    private ProgressDialog progressDialog;
    private String st_gender="Select Gender",st_pot="Select Post",st_dob1;
    private int PICK_IMAGE;
    Uri imageUri;
    Bitmap bitmap;
    private double img_p_len=1000000000,img_s_len=1000000000;
    private LinearLayout ll_credential;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_m_new_employee);

        if(!SharedPrefManager.getInstance(this).isLoggedMan()){
            finish();
            startActivity(new Intent(this, home.class));
            return;
        }

        getSupportActionBar().setTitle(Html.fromHtml("<font color=#000000>New Employee:</font>"));
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#ADF48004")));

        acc=findViewById(R.id.tv_ac);
        name=findViewById(R.id.et_name);
        post = findViewById(R.id.spin_post);
        gender = findViewById(R.id.spin_gender);
        ed_dob=findViewById(R.id.dob);
        ed_dob.setInputType(InputType.TYPE_NULL);
        mob=findViewById(R.id.et_mob);
        email=findViewById(R.id.et_email);
        address=findViewById(R.id.et_address);
        amount=findViewById(R.id.et_amount);
        i_photo=findViewById(R.id.iv_photo);
        i_sign=findViewById(R.id.iv_sign);
        photo=findViewById(R.id.tv_uphoto);
        sign=findViewById(R.id.tv_usign);
        create=findViewById(R.id.bt_create);
        sal=findViewById(R.id.et_sal);
        l_id=findViewById(R.id.et_l_id);
        l_pass=findViewById(R.id.et_l_pass);
        ll_credential=findViewById(R.id.ll_credential);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);

        final String[] types = {"Select Gender","Male", "Female"};
        ArrayAdapter<CharSequence> langAdapter = new ArrayAdapter<CharSequence>(m_new_employee.this, R.layout.spin_text, types);
        langAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown);
        gender.setAdapter(langAdapter);

        final String[] types1 = {"Select Post","Cashier", "Watchman"};
        ArrayAdapter<CharSequence> langAdapter1 = new ArrayAdapter<CharSequence>(m_new_employee.this, R.layout.spin_text, types1);
        langAdapter1.setDropDownViewResource(R.layout.simple_spinner_dropdown);
        post.setAdapter(langAdapter1);

        refresh();

        ed_dob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar cldr = Calendar.getInstance();
                int day = cldr.get(Calendar.DAY_OF_MONTH);
                int month = cldr.get(Calendar.MONTH);
                int year = cldr.get(Calendar.YEAR);

                picker = new DatePickerDialog(m_new_employee.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                ed_dob.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                            }
                        }, year, month, day);
                picker.show();
            }
        });

        gender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (adapterView.getId()==R.id.spin_gender) {
                    st_gender = adapterView.getSelectedItem().toString();
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {  }
        });
        post.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (adapterView.getId()==R.id.spin_post) {
                    st_pot = adapterView.getSelectedItem().toString();
                    if (st_pot.equals("Cashier")){
                        ll_credential.setVisibility(View.VISIBLE);
                    }else {
                        ll_credential.setVisibility(View.GONE);
                    }
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {  }
        });

        photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setImage("photo");
            }
        });
        sign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setImage("sign");
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

        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String st_acc= acc.getText().toString().trim();
                final String st_name = name.getText().toString().trim();
                final String st_dob = ed_dob.getText().toString().trim();
                final String st_mob = mob.getText().toString().trim();
                final String st_email = email.getText().toString().trim();
                final String st_address = address.getText().toString().trim();
                final String st_l_id = l_id.getText().toString().trim();
                final String st_l_pass = l_pass.getText().toString().trim();
                final String st_amount = amount.getText().toString().trim();
                final String st_amount1=st_amount.replaceAll(",", "");
                final String st_sal = sal.getText().toString().trim();
                final String st_sal1=st_sal.replaceAll(",", "");
                final String encoded_photo;
                final String encoded_sign;
                final String st_txt;
                final String st_txt1;

                if (st_acc.isEmpty()) {
                    acc.setError("Require Account Number");
                }else if ((i_photo.getTag()).equals("amol")){
                    Toast.makeText(getApplicationContext(),"Upload user photo", Toast.LENGTH_SHORT).show();
                }else if ((i_sign.getTag()).equals("amol1")){
                    Toast.makeText(getApplicationContext(),"Upload user sign", Toast.LENGTH_SHORT).show();
                }else if (st_name.isEmpty()){
                    name.setError("Enter Accountant Name");
                }else if (st_gender.equals("Select Gender")){
                    Toast.makeText(getApplicationContext(),"Select Gender", Toast.LENGTH_SHORT).show();
                }else if (st_pot.equals("Select Post")){
                    Toast.makeText(getApplicationContext(),"Select Post", Toast.LENGTH_SHORT).show();

                }else if (st_pot.equals("Cashier") && st_l_id.isEmpty()){
                    l_id.setError("Enter login id");
                }else if (st_pot.equals("Cashier") && st_l_pass.isEmpty()){
                    l_pass.setError("Enter login password");

                }else if (st_dob.equals("Select date of birth*")){
                    ed_dob.setError("Select DOB");
                }else if (st_mob.length() != 10){
                    mob.setError("Enter Mobile Number");
                }else if (st_email.isEmpty()){
                    email.setError("Enter Email ID");
                }else if (st_address.isEmpty()){
                    address.setError("Enter Address");
                }else if (st_amount1.equals("0") || st_amount1.isEmpty()){
                    amount.setError("Enter Amount greater than 0");
                }else if (st_sal1.equals("0") || st_sal1.isEmpty()){
                    sal.setError("Enter Amount greater than 0");
                }else {
                    progressDialog.show();
                    ed_dob.setError(null);

                    if (st_pot.equals("Cashier")){
                        st_txt=st_l_id;
                        st_txt1=st_l_pass;
                    }else {
                        st_txt="null";
                        st_txt1="null";
                    }

                    try {
                        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
                        Date fechaNueva = format.parse(st_dob);
                        format = new SimpleDateFormat("yyyy-MM-dd");
                        st_dob1=format.format(fechaNueva);
                    } catch (ParseException parseException) {
                        parseException.printStackTrace();
                    }

                    BitmapDrawable drawable = (BitmapDrawable) i_photo.getDrawable();
                    Bitmap bitmap = drawable.getBitmap();
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                    byte[] imageBytes = stream.toByteArray();
                    encoded_photo = android.util.Base64.encodeToString(imageBytes, Base64.DEFAULT);

                    BitmapDrawable drawable1 = (BitmapDrawable) i_sign.getDrawable();
                    Bitmap bitmap1 = drawable1.getBitmap();
                    ByteArrayOutputStream stream1 = new ByteArrayOutputStream();
                    bitmap1.compress(Bitmap.CompressFormat.JPEG, 100, stream1);
                    byte[] imageBytes1 = stream1.toByteArray();
                    encoded_sign = android.util.Base64.encodeToString(imageBytes1, Base64.DEFAULT);

                    String url="https://easywaytosolve.000webhostapp.com/Banking/cashier/new_account.php";

                    StringRequest stringRequest=new StringRequest(Request.Method.POST,url, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                                    try {
                                        JSONObject obj = new JSONObject(response);
                                        if (!obj.getBoolean("error")) {
                                            saveEmployee(st_name,st_gender,st_dob1,st_mob,st_address,st_email,st_pot,st_acc,st_sal1,st_l_id,st_l_pass);
                                        } else {
                                            AlertDialog.Builder builderDel = new AlertDialog.Builder(m_new_employee.this);
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
                                    AlertDialog.Builder builderDel = new AlertDialog.Builder(m_new_employee.this);
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
                            params.put("name", st_name);
                            params.put("gen", st_gender);
                            params.put("dob",st_dob1);
                            params.put("mob", st_mob);
                            params.put("email", st_email);
                            params.put("address", st_address);
                            params.put("amount", st_amount1);
                            params.put("photo", encoded_photo);
                            params.put("sign", encoded_sign);
                            return params;
                        }
                    };
                    RequestQueue requestQueue = Volley.newRequestQueue(m_new_employee.this);
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
    public void refresh(){
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                Constants.URL_GET_NO,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        try {
                            JSONObject obj = new JSONObject(response);
                            if (!obj.getBoolean("error")) {
                                acc.setText(obj.getString("acc"));
                            } else {
                                acc.setText(obj.getString("acc"));
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
                        AlertDialog.Builder builderDel = new AlertDialog.Builder(m_new_employee.this);
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
                params.put("acc","hi");
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(m_new_employee.this);
        requestQueue.add(stringRequest);

        requestQueue.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<Object>() {
            @Override
            public void onRequestFinished(Request<Object> request) {
                requestQueue.getCache().clear();
            }
        });
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
                        i_photo.setImageBitmap(bitmap);
                        i_photo.setTag("photo");
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
                        i_sign.setImageBitmap(bitmap);
                        i_sign.setTag("sign");
                        create.setEnabled(true);
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

    public void setPhoto(String s){
        AlertDialog.Builder builder = new AlertDialog.Builder(m_new_employee.this);
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
    public void saveEmployee(String e_name,String e_gender,String e_dob,String e_mob,String e_address,String e_email,String e_post,String e_accno,String e_salary,String e_l_id,String e_l_pass){
        StringRequest stringRequest=new StringRequest(Request.Method.POST, com.example.banking.manager.Constants.URL_NEW_EMPLOYEE, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();
                try {
                    JSONObject obj = new JSONObject(response);
                    if (!obj.getBoolean("error")) {
                        AlertDialog.Builder builderDel = new AlertDialog.Builder(m_new_employee.this);
                        builderDel.setCancelable(false);
                        builderDel.setMessage(obj.getString("message"));
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
                    } else {
                        AlertDialog.Builder builderDel = new AlertDialog.Builder(m_new_employee.this);
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
                        AlertDialog.Builder builderDel = new AlertDialog.Builder(m_new_employee.this);
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
                params.put("acc", e_accno);
                params.put("name", e_name);
                params.put("gen", e_gender);
                params.put("dob",e_dob);
                params.put("mob", e_mob);
                params.put("email", e_email);
                params.put("address", e_address);
                params.put("post", e_post);
                params.put("sal", e_salary);
                params.put("l_id", e_l_id);
                params.put("l_pass", e_l_pass);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(m_new_employee.this);
        requestQueue.add(stringRequest);

        requestQueue.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<Object>() {
            @Override
            public void onRequestFinished(Request<Object> request) {
                requestQueue.getCache().clear();
            }
        });
    }
}
