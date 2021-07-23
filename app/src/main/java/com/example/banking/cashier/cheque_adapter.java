package com.example.banking.cashier;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.banking.R;
import com.example.banking.manager.m_a_e_details;
import com.example.banking.u_new_user;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class cheque_adapter extends RecyclerView.Adapter<cheque_adapter.ViewHolder>{
    private List<cheque_model> listItems;
    private Context context;
    private ProgressDialog progressDialog;

    public cheque_adapter(List<cheque_model> listItems, Context context) {
        this.listItems = listItems;
        this.context = context;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView id;
        public TextView acc;
        public TextView pages;
        public TextView status;
        public TextView date;
        public CardView card_view;
        public ViewHolder(View itemView ) {
            super(itemView);
            id = (TextView) itemView.findViewById(R.id.id);
            acc = (TextView) itemView.findViewById(R.id.tv_acc);
            pages = (TextView) itemView.findViewById(R.id.tv_pages);
            date = (TextView) itemView.findViewById(R.id.tv_date);
            status = (TextView) itemView.findViewById(R.id.tv_status);
            card_view = (CardView) itemView.findViewById(R.id.card_cheque);

        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_cheque, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final cheque_model listItem = listItems.get(position);

        holder.id.setText(listItem.getId());
        holder.acc.setText(Html.fromHtml("<b><font color=#990000>" + listItem.getAcc() + "</font></b>"));
        holder.pages.setText(Html.fromHtml("<b><font color=#990000>" + listItem.getPages() + "</font></b>"));
        holder.date.setText(Html.fromHtml("<b><font color=#990000>" + listItem.getDate() + "</font></b>"));

        holder.card_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                progressDialog = new ProgressDialog(view.getContext());
                progressDialog.setMessage("Fetching Data...");
                progressDialog.setCancelable(false);

                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                final CharSequence[] dialogitem = {"View Request Details","Deliver to Customer"};
                builder.setTitle(listItem.getAcc());
                builder.setItems(dialogitem, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        switch (i){
                            case 0:
                                Intent intent = new Intent(view.getContext(),c_cheque_details.class);
                                intent.putExtra("id", listItem.getId());
                                intent.putExtra("rmk", "cheque");
                                view.getContext().startActivity(intent);
                                break;

                            case 1:
                                if ((listItem.getStatus()).equals("Out")) {

                                    AlertDialog.Builder builderDel = new AlertDialog.Builder(view.getContext());
                                    builderDel.setMessage("Are you sure, chequebook deliver to customer");
                                    builderDel.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            dialogInterface.dismiss();
                                            progressDialog.show();
                                            StringRequest stringRequest = new StringRequest(
                                                    Request.Method.POST,
                                                    Constants.URL_CHEQUE_DELIVER,
                                                    new Response.Listener<String>() {
                                                        @Override
                                                        public void onResponse(String response) {
                                                            progressDialog.dismiss();
                                                            try {
                                                                JSONObject obj = new JSONObject(response);
                                                                if (!obj.getBoolean("error")) {
                                                                    AlertDialog.Builder builderDel = new AlertDialog.Builder(view.getContext());
                                                                    builderDel.setMessage(obj.getString("message"));
                                                                    builderDel.setCancelable(false);
                                                                    builderDel.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                                        @Override
                                                                        public void onClick(DialogInterface dialogInterface, int i) {
                                                                            c_manage_cheque.mc.refresh_list();
                                                                        }
                                                                    });
                                                                    builderDel.create().show();
                                                                } else {
                                                                    AlertDialog.Builder builderDel = new AlertDialog.Builder(view.getContext());
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
                                                            AlertDialog.Builder builderDel = new AlertDialog.Builder(view.getContext());
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
                                                    params.put("acc", listItem.getAcc());
                                                    params.put("pages", listItem.getPages());
                                                    return params;
                                                }
                                            };
                                            RequestQueue requestQueue = Volley.newRequestQueue(view.getContext());
                                            requestQueue.add(stringRequest);

                                            requestQueue.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<Object>() {
                                                @Override
                                                public void onRequestFinished(Request<Object> request) {
                                                    requestQueue.getCache().clear();
                                                }
                                            });
                                        }
                                    });
                                    builderDel.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            dialogInterface.dismiss();
                                        }
                                    });
                                    builderDel.create().show();
                                }else {
                                    Toast.makeText(view.getContext(), "Chequebook not available",Toast.LENGTH_SHORT).show();
                                }
                                break;
                        }
                    }
                });
                builder.create().show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return listItems.size();
    }
}
