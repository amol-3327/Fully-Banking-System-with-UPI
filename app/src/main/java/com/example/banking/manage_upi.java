package com.example.banking;

import androidx.appcompat.app.ActionBar;
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
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
import com.google.android.material.navigation.NavigationView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

public class manage_upi extends AppCompatActivity {
    private CardView saved,account,tran,change,forgot;
    private TextView upi,name,ifsc,acno;
    private ImageView im_qr;
    private static final int PICK_IMAGE = 1;
    private ProgressDialog progressDialog;
    private Button deregister;
    private String st_img,st_uid,st_name,st_acno,st_ifsc,st_card,st_exp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_upi);

        if(!SharedPrefManager.getInstance(this).isLoggedIn()){
            finish();
            startActivity(new Intent(this, home.class));
            return;
        }
        ActionBar ab=getSupportActionBar();
        ab.setTitle("Manage UPI:");
        ab.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00801EBD")));
        getWindow().setBackgroundDrawableResource(R.mipmap.back2);

        saved=findViewById(R.id.c_sc);
        account=findViewById(R.id.c_ba);
        tran=findViewById(R.id.c_tr);
        change=findViewById(R.id.c_cp);
        forgot=findViewById(R.id.c_fp);
        upi=findViewById(R.id.tv_up);
        im_qr=findViewById(R.id.im_qr);
        deregister=findViewById(R.id.bt_dere);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            upi.setText(bundle.getString("mob")+"@amo");
        }

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);

        getData();

        forgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), u_forgot_u_pin.class));
            }
        });
        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(manage_upi.this, u_change_u_pin.class);
                intent.putExtra("id",upi.getText().toString().trim());
                startActivity(intent);
            }
        });
        tran.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), u_my_trans.class));
            }
        });
        saved.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(manage_upi.this);
                final View customLayout = getLayoutInflater().inflate(R.layout.alert_qr, null);
                builder.setView(customLayout);

                LinearLayout saved=(LinearLayout) customLayout.findViewById(R.id.ll_saved);
                saved.setVisibility(View.VISIBLE);

                final TextView card= (TextView) customLayout.findViewById(R.id.tv_card);
                final TextView exp= (TextView) customLayout.findViewById(R.id.tv_exp);

                String lastFourDigits = "";     //substring containing last 4 characters

                if (st_card.length() > 4)
                {
                    lastFourDigits = st_card.substring(st_card.length() - 4);
                }
                else
                {
                    lastFourDigits = st_card;
                }

                int half;
                half = st_exp.length() / 2;
                String str2Part1 = st_exp.substring(0, half);
                String str2Part2 = st_exp.substring(half);

                card.setText(Html.fromHtml("<font color=#FF000000> Debit Card: *****"+lastFourDigits+"</font>"));
                exp.setText(Html.fromHtml("<font color=#FF000000> Expires: "+str2Part1 + "/20" + str2Part2+"</font>"));

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
        account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(manage_upi.this);
                final View customLayout = getLayoutInflater().inflate(R.layout.alert_qr, null);
                builder.setView(customLayout);

                LinearLayout account=(LinearLayout) customLayout.findViewById(R.id.ll_account);
                account.setVisibility(View.VISIBLE);

                final TextView name= (TextView) customLayout.findViewById(R.id.tv_name);
                final TextView acc= (TextView) customLayout.findViewById(R.id.tv_ano);
                final TextView ifsc= (TextView) customLayout.findViewById(R.id.tv_ifsc);

                name.setText(st_name);
                acc.setText(Html.fromHtml("<font color=#FF000000> Account Number: "+st_acno+"</font>"));
                ifsc.setText(Html.fromHtml("<font color=#FF000000> IFSC: "+st_ifsc+"</font>"));

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

         im_qr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GetXMLTask task = new GetXMLTask();
                task.execute(new String[] { st_img });
            }
        });

        deregister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(manage_upi.this);
                builder.setTitle("Deactivate UPI");
                builder.setMessage("Are you sure, You want to Deactivate UPI?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        progressDialog.show();
                        StringRequest stringRequest = new StringRequest(
                                Request.Method.POST,
                                Constants.URL_DEACTIVATE_UPI,
                                new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        progressDialog.dismiss();
                                        try {
                                            JSONObject obj = new JSONObject(response);
                                            if (!obj.getBoolean("error")) {
                                                AlertDialog.Builder builderDel = new AlertDialog.Builder(manage_upi.this);
                                                builderDel.setCancelable(false);
                                                builderDel.setMessage(obj.getString("message"));
                                                builderDel.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int i) {
                                                        finishAffinity();
                                                        startActivity(new Intent(manage_upi.this,MainActivity.class));
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
                                        AlertDialog.Builder builderDel = new AlertDialog.Builder(manage_upi.this);
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
                                params.put("upi", upi.getText().toString().trim());
                                return params;
                            }
                        };
                        RequestQueue requestQueue = Volley.newRequestQueue(manage_upi.this);
                        requestQueue.add(stringRequest);

                        requestQueue.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<Object>() {
                            @Override
                            public void onRequestFinished(Request<Object> request) {
                                requestQueue.getCache().clear();
                            }
                        });
                    }
                });
                builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                AlertDialog dialog = builder.create();
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
    }

    private class GetXMLTask extends AsyncTask<String, Void, Bitmap> {
        @Override
        protected Bitmap doInBackground(String... urls) {
            Bitmap map = null;
            for (String url : urls) {
                map = downloadImage(url);
            }
            return map;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            AlertDialog.Builder builder = new AlertDialog.Builder(manage_upi.this);
            final View customLayout = getLayoutInflater().inflate(R.layout.alert_qr, null);
            builder.setView(customLayout);

            LinearLayout qr=(LinearLayout) customLayout.findViewById(R.id.ll_qr);
            qr.setVisibility(View.VISIBLE);

            final ImageView imageview= (ImageView) customLayout.findViewById(R.id.t_image);
            final TextView id= (TextView) customLayout.findViewById(R.id.tv_uid);
            id.setText(st_uid);
            imageview.setImageBitmap(result);

            AlertDialog dialog = builder.create();
            dialog.show();
        }

        private Bitmap downloadImage(String url) {
            Bitmap bitmap = null;
            InputStream stream = null;
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            bmOptions.inSampleSize = 1;
            try {
                stream = getHttpConnection(url);
                bitmap = BitmapFactory.decodeStream(stream, null, bmOptions);
                stream.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            return bitmap;
        }

        private InputStream getHttpConnection(String urlString) throws IOException {
            InputStream stream = null;
            URL url = new URL(urlString);
            URLConnection connection = url.openConnection();
            try {
                HttpURLConnection httpConnection = (HttpURLConnection) connection;
                httpConnection.setRequestMethod("GET");
                httpConnection.connect();
                if (httpConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    stream = httpConnection.getInputStream();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return stream;
        }
    }

    public void getData(){
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                Constants.URL_GET_QR,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        try {
                            JSONObject obj = new JSONObject(response);
                            if (!obj.getBoolean("error")) {
                                st_img=obj.getString("image");
                                st_uid=obj.getString("uid");
                                st_name=obj.getString("name");
                                st_acno=obj.getString("acc");
                                st_ifsc=obj.getString("ifsc");
                                st_card=obj.getString("card");
                                st_exp=obj.getString("exp");
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
                        AlertDialog.Builder builderDel = new AlertDialog.Builder(manage_upi.this);
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
                params.put("acc",  SharedPrefManager.getInstance(manage_upi.this).getAccNo());
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(manage_upi.this);
        requestQueue.add(stringRequest);

        requestQueue.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<Object>() {
            @Override
            public void onRequestFinished(Request<Object> request) {
                requestQueue.getCache().clear();
            }
        });
    }
}