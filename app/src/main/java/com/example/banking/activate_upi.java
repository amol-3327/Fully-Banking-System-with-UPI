package com.example.banking;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
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
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.poovam.pinedittextfield.PinField;
import com.poovam.pinedittextfield.SquarePinField;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class activate_upi extends AppCompatActivity {
    private ArrayList<String> arrayList;
    private ArrayAdapter<String> arrayAdapter;
    private Spinner spin_que;
    private String selected_que="Select Security Question:",st_pin1="Amol",st_pin2="shinde",st_mob,st_card;
    private ProgressDialog progressDialog;
    private SquarePinField pin1,pin2;
    private EditText ans;
    private Button activate;
    private TextView id;
    String encodedImage;
    public final static int QRCodeWidth = 500;
    Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activate_upi);

        if(!SharedPrefManager.getInstance(this).isLoggedIn()){
            finish();
            startActivity(new Intent(this, home.class));
            return;
        }
        ActionBar ab=getSupportActionBar();
        ab.setTitle("Activate UPI:");
        ab.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00801EBD")));
        getWindow().setBackgroundDrawableResource(R.mipmap.back2);

        spin_que = findViewById(R.id.spin_que);
        ans=findViewById(R.id.et_ans);
        pin1=findViewById(R.id.l_pin1);
        pin2=findViewById(R.id.l_pin2);
        activate=findViewById(R.id.bt_activate);
        id=findViewById(R.id.tv_id);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            id.setText(bundle.getString("mob")+"@amo");
            st_card=bundle.getString("card");
        }

        final String[] que = {
                "Select Security Question:",
                "What is your mother's maiden name?",
                "What is the name of your first pet?",
                "What was your first car?",
                "What elementary school did you attend?",
                "What is the name of the town where you were born?"
        };
        ArrayAdapter<CharSequence> langAdapter = new ArrayAdapter<CharSequence>(activate_upi.this, R.layout.spin_text, que);
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

        activate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.show();
                final  String st_ans=ans.getText().toString().trim();
                final  String st_id=id.getText().toString().trim();
                if (st_pin1.equals("Amol") || st_pin2.equals("shinde")){
                    progressDialog.dismiss();
                    Toast.makeText(activate_upi.this,"Enter UPI PIN",Toast.LENGTH_SHORT).show();
                }else if (st_ans.isEmpty()){
                    progressDialog.dismiss();
                    ans.setError("Enter Answer");
                }else if (selected_que.equals("Select Security Question:")){
                    progressDialog.dismiss();
                    Toast.makeText(activate_upi.this,"Please Select Question.",Toast.LENGTH_SHORT).show();
                }else if(st_pin1.equals(st_pin2)) {
                    try {
                        bitmap = textToImageEncode(st_id);
                        imageStore(bitmap);
                    } catch (WriterException e) {
                        e.printStackTrace();
                     }
                    storeData();
                }else {
                    progressDialog.dismiss();
                    pin1.setText("");
                    pin2.setText("");
                    Toast.makeText(activate_upi.this,"PIN doesn't match",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void storeData(){
        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                Constants.URL_ACTIVATE_UPI,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        try {
                            JSONObject obj = new JSONObject(response);
                            if (!obj.getBoolean("error")) {
                                AlertDialog.Builder builderDel = new AlertDialog.Builder(activate_upi.this);
                                builderDel.setCancelable(false);
                                builderDel.setMessage(obj.getString("message"));
                                builderDel.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        finishAffinity();
                                        startActivity(new Intent(activate_upi.this,MainActivity.class));
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
                        AlertDialog.Builder builderDel = new AlertDialog.Builder(activate_upi.this);
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
                params.put("upi", id.getText().toString().trim());
                params.put("que", selected_que);
                params.put("ans", ans.getText().toString().trim());
                params.put("pin", st_pin1);
                params.put("card", st_card);
                params.put("img", encodedImage);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(activate_upi.this);
        requestQueue.add(stringRequest);

        requestQueue.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<Object>() {
            @Override
            public void onRequestFinished(Request<Object> request) {
                requestQueue.getCache().clear();
            }
        });
    }

    private Bitmap textToImageEncode(String value) throws WriterException {
        BitMatrix bitMatrix;
        try {
            bitMatrix = new MultiFormatWriter().encode(value,BarcodeFormat.DATA_MATRIX.QR_CODE, QRCodeWidth, QRCodeWidth, null);
        } catch (IllegalArgumentException e) {
            return null;
        }

        int bitMatrixWidth = bitMatrix.getWidth();
        int bitMatrixHeight = bitMatrix.getHeight();
        int[] pixels = new int[bitMatrixWidth * bitMatrixHeight];

        for (int y = 0; y < bitMatrixHeight; y++) {
            int offSet = y * bitMatrixWidth;
            for (int x = 0; x < bitMatrixWidth; x++) {
                pixels[offSet + x] = bitMatrix.get(x, y) ?
                        getResources().getColor(R.color.black) : getResources().getColor(R.color.white);
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(bitMatrixWidth, bitMatrixHeight, Bitmap.Config.ARGB_4444);
        bitmap.setPixels(pixels, 0, 500, 0, 0, bitMatrixWidth, bitMatrixHeight);
        return bitmap;
    }
    private void imageStore(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,stream);
        byte[] imageBytes = stream.toByteArray();
        encodedImage = android.util.Base64.encodeToString(imageBytes, Base64.DEFAULT);
    }
}