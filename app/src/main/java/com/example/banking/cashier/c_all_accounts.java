package com.example.banking.cashier;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
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
import com.example.banking.R;
import com.example.banking.home;
import com.example.banking.manager.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class c_all_accounts extends AppCompatActivity {
    public static c_all_accounts mc;
    List<account_model> listItems;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_c_all_accounts);

        if(!SharedPrefManager.getInstance(this).isLoggedIn1()){
            finish();
            startActivity(new Intent(this, home.class));
            return;
        }

        ActionBar ab=getSupportActionBar();
        ab.setTitle(Html.fromHtml("<font color=#000000>All Accounts :</font>"));
        ab.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#ADF48004")));

        recyclerView = (RecyclerView) findViewById(R.id.list_all);
        recyclerView.setLayoutManager(new LinearLayoutManager(c_all_accounts.this));
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Fetching Data...");
        progressDialog.setCancelable(false);
        listItems = new ArrayList<>();
        mc = this;
        refresh_list();
    }

    protected void refresh_list(){
        listItems.clear();
        adapter = new account_adapter(listItems,c_all_accounts.this);
        recyclerView.setAdapter(adapter);

        recyclerView.setItemAnimator(new DefaultItemAnimator());

        progressDialog.show();
        StringRequest stringRequest=new StringRequest(Request.Method.POST,Constants.URL_ALL_ACCOUNTS, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();
                try{
                    JSONObject jsonObject = new JSONObject(response);
                    if(!jsonObject.getBoolean("error")) {
                        JSONArray jsonArray = jsonObject.getJSONArray("data");
                        for (int i = 0; i<jsonArray.length(); i++){
                            JSONObject o = jsonArray.getJSONObject(i);
                            account_model item = new account_model(
                                    o.getString("acc"),
                                    o.getString("mob")
                            );
                            listItems.add(item);
                            adapter = new account_adapter(listItems,c_all_accounts.this);
                            recyclerView.setAdapter(adapter);
                        }
                    }
                }catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.hide();
                AlertDialog.Builder builderDel = new AlertDialog.Builder(c_all_accounts.this);
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
        RequestQueue requestQueue = Volley.newRequestQueue(c_all_accounts.this);
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
        adapter = new account_adapter(listItems,c_all_accounts.this);
        recyclerView.setAdapter(adapter);

        recyclerView.setItemAnimator(new DefaultItemAnimator());

        progressDialog.show();
        StringRequest stringRequest=new StringRequest(Request.Method.POST, Constants.URL_ALL_ACCOUNTS, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();
                try{
                    JSONObject jsonObject = new JSONObject(response);
                    if(!jsonObject.getBoolean("error")) {
                        JSONArray jsonArray = jsonObject.getJSONArray("data");
                        for (int i = 0; i<jsonArray.length(); i++){
                            JSONObject o = jsonArray.getJSONObject(i);
                            account_model item = new account_model(
                                    o.getString("acc"),
                                    o.getString("mob")
                            );
                            listItems.add(item);
                            adapter = new account_adapter(listItems,c_all_accounts.this);
                            recyclerView.setAdapter(adapter);
                        }
                    }
                }catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                AlertDialog.Builder builderDel = new AlertDialog.Builder(c_all_accounts.this);
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
        RequestQueue requestQueue = Volley.newRequestQueue(c_all_accounts.this);
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
        MenuItem searchItem = menu.findItem(R.id.menu_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);
        searchView.setQueryHint("account no");
        searchView.setInputType(InputType.TYPE_CLASS_NUMBER);
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
}