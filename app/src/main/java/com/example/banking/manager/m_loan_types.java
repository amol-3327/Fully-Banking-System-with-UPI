package com.example.banking.manager;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Html;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.widget.SearchView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.banking.MainActivity;
import com.example.banking.R;
import com.example.banking.cashier.c_manage_cheque;
import com.example.banking.cashier.cheque_adapter;
import com.example.banking.cashier.cheque_model;
import com.example.banking.home;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class m_loan_types extends AppCompatActivity {
    public static m_loan_types mc;
    List<loan_types_model> listItems;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_m_loan_types);

        if(!SharedPrefManager.getInstance(this).isLoggedMan()){
            finish();
            startActivity(new Intent(this, home.class));
            return;
        }

//        ActionBar ab=getSupportActionBar();
//        ab.setTitle(Html.fromHtml("<font color=#000000>Loan Categories :</font>"));
//        ab.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#ADF48004")));

        Toolbar toolbar = findViewById(R.id.t_t);
        setSupportActionBar(toolbar);

        recyclerView = (RecyclerView) findViewById(R.id.list_l_t);
        recyclerView.setLayoutManager(new LinearLayoutManager(m_loan_types.this));
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Fetching Data...");
        progressDialog.setCancelable(false);
        listItems = new ArrayList<>();
        mc = this;
        refresh_list();
    }

    public void refresh_list(){
        listItems.clear();
        adapter = new loan_types_adapter(listItems,getApplicationContext());
        recyclerView.setAdapter(adapter);

        recyclerView.setItemAnimator(new DefaultItemAnimator());

        progressDialog.show();
        StringRequest stringRequest=new StringRequest(Request.Method.POST, Constants.URL_LOAN_TYPES, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();
                   try{
                    JSONObject jsonObject = new JSONObject(response);
                    if(!jsonObject.getBoolean("error")) {
                        JSONArray jsonArray = jsonObject.getJSONArray("data");
                        for (int i = 0; i<jsonArray.length(); i++){
                            JSONObject o = jsonArray.getJSONObject(i);
                            loan_types_model item = new loan_types_model(
                                    o.getString("id"),
                                    o.getString("type"),
                                    o.getString("rate")
                            );
                            listItems.add(item);
                            adapter = new loan_types_adapter(listItems,getApplicationContext());
                            recyclerView.setAdapter(adapter);
                        }
                    } else{
                        AlertDialog.Builder builderDel = new AlertDialog.Builder(m_loan_types.this);
                        builderDel.setMessage(jsonObject.getString("message"));
                        builderDel.setCancelable(false);
                        builderDel.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                                finish();
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
                AlertDialog.Builder builderDel = new AlertDialog.Builder(m_loan_types.this);
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
                params.put("acc","1");
                params.put("txt","list");
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(m_loan_types.this);
        requestQueue.add(stringRequest);

        requestQueue.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<Object>() {
            @Override
            public void onRequestFinished(Request<Object> request) {
                requestQueue.getCache().clear();
            }
        });
    }

    public void search(String s){
        listItems.clear();
        adapter = new loan_types_adapter(listItems,getApplicationContext());
        recyclerView.setAdapter(adapter);

        recyclerView.setItemAnimator(new DefaultItemAnimator());

        progressDialog.show();
        StringRequest stringRequest=new StringRequest(Request.Method.POST, Constants.URL_LOAN_TYPES, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();
                try{
                    JSONObject jsonObject = new JSONObject(response);
                    if(!jsonObject.getBoolean("error")) {
                        JSONArray jsonArray = jsonObject.getJSONArray("data");
                        for (int i = 0; i<jsonArray.length(); i++){
                            JSONObject o = jsonArray.getJSONObject(i);
                            loan_types_model item = new loan_types_model(
                                    o.getString("id"),
                                    o.getString("type"),
                                    o.getString("rate")
                            );
                            listItems.add(item);
                            adapter = new loan_types_adapter(listItems,getApplicationContext());
                            recyclerView.setAdapter(adapter);
                        }
                    } else{
//                        AlertDialog.Builder builderDel = new AlertDialog.Builder(m_loan_types.this);
//                        builderDel.setMessage(jsonObject.getString("message"));
//                        builderDel.setCancelable(false);
//                        builderDel.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialogInterface, int i) {
//                                dialogInterface.dismiss();
//                                finish();
//                            }
//                        });
//                        builderDel.create().show();
                    }
                }catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.hide();
                AlertDialog.Builder builderDel = new AlertDialog.Builder(m_loan_types.this);
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
                params.put("acc","search");
                params.put("txt",s);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(m_loan_types.this);
        requestQueue.add(stringRequest);

        requestQueue.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<Object>() {
            @Override
            public void onRequestFinished(Request<Object> request) {
                requestQueue.getCache().clear();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_s, menu);
        inflater.inflate(R.menu.menu_a, menu);
        MenuItem searchItem = menu.findItem(R.id.menu_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);
//        searchView.setQueryHint("Search by account no. or mob no.");
        searchView.setQueryHint(Html.fromHtml("<font color = #000000>type...</font>"));
        searchView.setInputType(InputType.TYPE_CLASS_TEXT);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                search(newText);
                return false;
            }
        });
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_add) {
            Intent intent = new Intent(m_loan_types.this, m_details_new_type.class);
            intent.putExtra("id", "1");
            intent.putExtra("type", "2");
            intent.putExtra("rate", "3");
            intent.putExtra("rmk", "new_type");
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
}