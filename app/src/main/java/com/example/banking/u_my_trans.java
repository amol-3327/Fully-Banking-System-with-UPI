package com.example.banking;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class u_my_trans extends AppCompatActivity {
    List<trans_model> listItems;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_u_my_trans);

        if(!SharedPrefManager.getInstance(this).isLoggedIn()){
            finish();
            startActivity(new Intent(this, home.class));
            return;
        }
        ActionBar ab=getSupportActionBar();
        ab.setTitle("Transactions:");
        ab.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00801EBD")));
        getWindow().setBackgroundDrawableResource(R.mipmap.back2);

        recyclerView = (RecyclerView) findViewById(R.id.list_t);
        recyclerView.setLayoutManager(new LinearLayoutManager(u_my_trans.this));
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Fetching Data...");
        progressDialog.setCancelable(false);
        listItems = new ArrayList<>();
        refresh_list();
    }

    public void refresh_list(){
        listItems.clear();
        adapter = new trans_adapter(listItems,getApplicationContext());
        recyclerView.setAdapter(adapter);

        recyclerView.setItemAnimator(new DefaultItemAnimator());

        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST,Constants.URL_MY_TRANS, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();
                try{
                    JSONObject jsonObject = new JSONObject(response);
                    if(!jsonObject.getBoolean("error")) {
                        JSONArray jsonArray = jsonObject.getJSONArray("data");
                        for (int i = 0; i<jsonArray.length(); i++){
                            JSONObject o = jsonArray.getJSONObject(i);
                            trans_model item = new trans_model(
                                    o.getString("id"),
                                    o.getString("acc"),
                                    o.getString("send"),
                                    o.getString("rece"),
                                    o.getString("date"),
                                    o.getString("status"),
                                    o.getString("amount"),
                                    o.getString("rmk")
                            );
                            listItems.add(item);
                            adapter = new trans_adapter(listItems,getApplicationContext());
                            recyclerView.setAdapter(adapter);
                        }
                    }else{
                        AlertDialog.Builder builderDel = new AlertDialog.Builder(u_my_trans.this);
                        builderDel.setMessage(jsonObject.getString("message"));
                        builderDel.setCancelable(false);
                        builderDel.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                                finishAffinity();
                                startActivity(new Intent(u_my_trans.this,MainActivity.class));
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
                AlertDialog.Builder builderDel = new AlertDialog.Builder(u_my_trans.this);
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
                params.put("acc", SharedPrefManager.getInstance(u_my_trans.this).getAccNo());
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(u_my_trans.this);
        requestQueue.add(stringRequest);

        requestQueue.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<Object>() {
            @Override
            public void onRequestFinished(Request<Object> request) {
                requestQueue.getCache().clear();
            }
        });
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.menu_s, menu);
//        MenuItem searchItem = menu.findItem(R.id.menu_search);
//        SearchView searchView = (SearchView) searchItem.getActionView();
//        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);
//        searchView.setQueryHint("Search by Transaction ID:");
//        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//                return false;
//            }
//            @Override
//            public boolean onQueryTextChange(String newText) {
//                  search(newText);
//                return false;
//            }
//        });
//        return true;
//    }

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