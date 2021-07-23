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
import com.example.banking.MainActivity;
import com.example.banking.R;
import com.example.banking.home;
import com.example.banking.trans_adapter;
import com.example.banking.trans_model;
import com.example.banking.u_my_trans;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class c_manage_cheque extends AppCompatActivity {
    public static c_manage_cheque mc;
    List<cheque_model> listItems;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_c_manage_cheque);

        if(!SharedPrefManager.getInstance(this).isLoggedIn1()){
            finish();
            startActivity(new Intent(this, home.class));
            return;
        }

        ActionBar ab=getSupportActionBar();
        ab.setTitle(Html.fromHtml("<font color=#000000>Chequebook Requests :</font>"));
        ab.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#ADF48004")));

        recyclerView = (RecyclerView) findViewById(R.id.list_re);
        recyclerView.setLayoutManager(new LinearLayoutManager(c_manage_cheque.this));
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Fetching Data...");
        progressDialog.setCancelable(false);
        listItems = new ArrayList<>();
        mc = this;
        refresh_list();
    }

    public void refresh_list(){
        listItems.clear();
        adapter = new cheque_adapter(listItems,getApplicationContext());
        recyclerView.setAdapter(adapter);

        recyclerView.setItemAnimator(new DefaultItemAnimator());

        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.URL_CHEQUE_REQUESTS, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();
                try{
                    JSONObject jsonObject = new JSONObject(response);
                    if(!jsonObject.getBoolean("error")) {
                        JSONArray jsonArray = jsonObject.getJSONArray("data");
                        for (int i = 0; i<jsonArray.length(); i++){
                            JSONObject o = jsonArray.getJSONObject(i);
                            cheque_model item = new cheque_model(
                                    o.getString("id"),
                                    o.getString("acc"),
                                    o.getString("pages"),
                                    o.getString("status"),
                                    o.getString("date")
                            );
                            listItems.add(item);
                            adapter = new cheque_adapter(listItems,getApplicationContext());
                            recyclerView.setAdapter(adapter);
                        }
                    }else{
                        AlertDialog.Builder builderDel = new AlertDialog.Builder(c_manage_cheque.this);
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
                AlertDialog.Builder builderDel = new AlertDialog.Builder(c_manage_cheque.this);
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
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(c_manage_cheque.this);
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
        adapter = new cheque_adapter(listItems,getApplicationContext());
        recyclerView.setAdapter(adapter);

        recyclerView.setItemAnimator(new DefaultItemAnimator());

        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.URL_SEARCH_REQUESTS, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();
                try{
                    JSONObject jsonObject = new JSONObject(response);
                    if(!jsonObject.getBoolean("error")) {
                        JSONArray jsonArray = jsonObject.getJSONArray("data");
                        for (int i = 0; i<jsonArray.length(); i++){
                            JSONObject o = jsonArray.getJSONObject(i);
                            cheque_model item = new cheque_model(
                                    o.getString("id"),
                                    o.getString("acc"),
                                    o.getString("pages"),
                                    o.getString("status"),
                                    o.getString("date")
                            );
                            listItems.add(item);
                            adapter = new cheque_adapter(listItems,getApplicationContext());
                            recyclerView.setAdapter(adapter);
                        }
                    }else{
                    }
                }catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                AlertDialog.Builder builderDel = new AlertDialog.Builder(c_manage_cheque.this);
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
                params.put("search",s);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(c_manage_cheque.this);
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
        searchView.setQueryHint("Search by account no. or mob no.");
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

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.menu_tran, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.action_menu1:
//                Toast.makeText(this, "Item 1 selected", Toast.LENGTH_SHORT).show();
//                return true;
//            case R.id.action_menu2:
//                Toast.makeText(this, "Item 2 selected", Toast.LENGTH_SHORT).show();
//                return true;
//            default:
//                return super.onOptionsItemSelected(item);
//        }
//    }
}