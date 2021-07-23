package com.example.banking;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class  trans_adapter extends RecyclerView.Adapter<trans_adapter.ViewHolder>{
    private List<trans_model> listItems;
    private Context context;
    private ProgressDialog dialog;
    private String st_acc,first;

    public trans_adapter(List<trans_model> listItems, Context context) {
        this.listItems = listItems;
        this.context = context;
        st_acc=SharedPrefManager.getInstance(context).getAccNo();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView id;
        public TextView name;
        public TextView date;
        public TextView status;
        public TextView amount;
        public CardView card_view;
        public ViewHolder(View itemView ) {
            super(itemView);
            id = (TextView) itemView.findViewById(R.id.id);
            name = (TextView) itemView.findViewById(R.id.name);
            date = (TextView) itemView.findViewById(R.id.date);
            status = (TextView) itemView.findViewById(R.id.status);
            amount = (TextView) itemView.findViewById(R.id.amount);
            card_view = (CardView) itemView.findViewById(R.id.card_view);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_trans, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final trans_model listItem = listItems.get(position);
        if (st_acc.equals(listItem.getAcc())){
            holder.id.setText(listItem.getId());
            if ((listItem.getSend()).equals(listItem.getRece())){
                if ((listItem.getRmk()).equals("Withdraw Amount")) {
                    holder.name.setText(Html.fromHtml("<font color=#7500B8><b>Withdraw Amount,</font><big><big>" +
                            "&#8655;</big></big>"));
                    holder.amount.setText(Html.fromHtml("<b><font color=#990000>Rs. " + listItem.getAmount() + ".00/-</font></b>"));
                    holder.date.setText(Html.fromHtml("<font color=#2EB83D><b>" + listItem.getDate() + "</font></b>"));
                    if ((listItem.getStatus()).equals("Successful")) {
                        holder.status.setText(Html.fromHtml("<font color=#2EB83D><b>Successful</font></b>"));
                    } else {
                        holder.status.setText(Html.fromHtml("<font color=#FF0000><b>Failed</font></b>"));
                    }
                }else if ((listItem.getRmk()).equals("Deposit Amount")){
                    holder.name.setText(Html.fromHtml("<font color=#7500B8><b>Deposit Amount,</font><big><big>" +
                            "&#8653;</big></big>"));
                    holder.amount.setText(Html.fromHtml("<b><font color=#990000>Rs. " + listItem.getAmount() + ".00/-</font></b>"));
                    holder.date.setText(Html.fromHtml("<font color=#2EB83D><b>" + listItem.getDate() + "</font></b>"));
                    if ((listItem.getStatus()).equals("Successful")) {
                        holder.status.setText(Html.fromHtml("<font color=#2EB83D><b>Successful</font></b>"));
                    } else {
                        holder.status.setText(Html.fromHtml("<font color=#FF0000><b>Failed</font></b>"));
                    }
                }else if((listItem.getRmk()).equals("Cheque Withdraw1")) {
                    holder.name.setText(Html.fromHtml("<font color=#7500B8><b>Cheque Withdraw, &nbsp;</font><big>&#65510;</big>"));
                    holder.amount.setText(Html.fromHtml("<b><font color=#990000>Rs. " + listItem.getAmount() + ".00/-</font></b>"));
                    holder.date.setText(Html.fromHtml("<font color=#2EB83D><b>" + listItem.getDate() + "</font></b>"));
                    if ((listItem.getStatus()).equals("Successful")) {
                        holder.status.setText(Html.fromHtml("<font color=#2EB83D><b>Successful</font></b>"));
                    } else {
                        holder.status.setText(Html.fromHtml("<font color=#FF0000><b>Failed</font></b>"));
                    }
                }else if((listItem.getRmk()).equals("Loan Emi")) {
                    holder.name.setText(Html.fromHtml("<font color=#7500B8><b>Loan EMI, &nbsp;</font><big>&#167;</big>"));
                    holder.amount.setText(Html.fromHtml("<b><font color=#990000>Rs. " + listItem.getAmount() + ".00/-</font></b>"));
                    holder.date.setText(Html.fromHtml("<font color=#2EB83D><b>" + listItem.getDate() + "</font></b>"));
                    if ((listItem.getStatus()).equals("Successful")) {
                        holder.status.setText(Html.fromHtml("<font color=#2EB83D><b>Successful</font></b>"));
                    } else {
                        holder.status.setText(Html.fromHtml("<font color=#FF0000><b>Failed</font></b>"));
                    }
                }else if((listItem.getRmk()).equals("Return Interest")) {
                    holder.name.setText(Html.fromHtml("<font color=#7500B8><b>Return Interest, &nbsp;</font><big>&#167;</big>"));
                    holder.amount.setText(Html.fromHtml("<b><font color=#990000>Rs. " + listItem.getAmount() + ".00/-</font></b>"));
                    holder.date.setText(Html.fromHtml("<font color=#2EB83D><b>" + listItem.getDate() + "</font></b>"));
                    if ((listItem.getStatus()).equals("Successful")) {
                        holder.status.setText(Html.fromHtml("<font color=#2EB83D><b>Successful</font></b>"));
                    } else {
                        holder.status.setText(Html.fromHtml("<font color=#FF0000><b>Failed</font></b>"));
                    }
                }else if((listItem.getRmk()).equals("Balance Interest")) {
                    holder.name.setText(Html.fromHtml("<font color=#7500B8><b>Interest, &nbsp;</font><big>&#167;</big>"));
                    holder.amount.setText(Html.fromHtml("<b><font color=#990000>Rs. " + listItem.getAmount() + ".00/-</font></b>"));
                    holder.date.setText(Html.fromHtml("<font color=#2EB83D><b>" + listItem.getDate() + "</font></b>"));
                    if ((listItem.getStatus()).equals("Successful")) {
                        holder.status.setText(Html.fromHtml("<font color=#2EB83D><b>Successful</font></b>"));
                    } else {
                        holder.status.setText(Html.fromHtml("<font color=#FF0000><b>Failed</font></b>"));
                    }
                }else if((listItem.getRmk()).equals("Salary")) {
                    holder.name.setText(Html.fromHtml("<font color=#7500B8><b>Salary, &nbsp;</font><big>&#167;</big>"));
                    holder.amount.setText(Html.fromHtml("<b><font color=#990000>Rs. " + listItem.getAmount() + ".00/-</font></b>"));
                    holder.date.setText(Html.fromHtml("<font color=#2EB83D><b>" + listItem.getDate() + "</font></b>"));
                    if ((listItem.getStatus()).equals("Successful")) {
                        holder.status.setText(Html.fromHtml("<font color=#2EB83D><b>Successful</font></b>"));
                    } else {
                        holder.status.setText(Html.fromHtml("<font color=#FF0000><b>Failed</font></b>"));
                    }
                }else if((listItem.getRmk()).equals("loan amount")) {
                    holder.name.setText(Html.fromHtml("<font color=#7500B8><b>Loan Amount, &nbsp;</font><big>&#167;</big>"));
                    holder.amount.setText(Html.fromHtml("<b><font color=#990000>Rs. " + listItem.getAmount() + ".00/-</font></b>"));
                    holder.date.setText(Html.fromHtml("<font color=#2EB83D><b>" + listItem.getDate() + "</font></b>"));
                    if ((listItem.getStatus()).equals("Successful")) {
                        holder.status.setText(Html.fromHtml("<font color=#2EB83D><b>Successful</font></b>"));
                    } else {
                        holder.status.setText(Html.fromHtml("<font color=#FF0000><b>Failed</font></b>"));
                    }
                } else{
                    holder.name.setText(Html.fromHtml("<font color=#7500B8><b>Account Created,</font><big>" +
                            "<big>&#x27F0;</big></big>"));
                    holder.amount.setText(Html.fromHtml("<b><font color=#990000>Rs. " + listItem.getAmount() + ".00/-</font></b>"));
                    holder.date.setText(Html.fromHtml("<font color=#2EB83D><b>" + listItem.getDate() + "</font></b>"));
                    if ((listItem.getStatus()).equals("Successful")) {
                        holder.status.setText(Html.fromHtml("<font color=#2EB83D><b>Successful</font></b>"));
                    } else {
                        holder.status.setText(Html.fromHtml("<font color=#FF0000><b>Failed</font></b>"));
                    }
                }
            }else if((listItem.getRmk()).equals("User-Bank Transfer")) {
                final String firstWord = listItem.getRece();
                final String firstWord1 = firstWord.replaceAll(" .*", "");
                holder.name.setText(Html.fromHtml("<font color=#7500B8><b>Transfer,&nbsp;&nbsp;</font><big><big>&#10230;</big></big>" +
                        "<font color=#990000>&nbsp;<font color=#990000>&nbsp;&nbsp;" + firstWord1 + "</font></b>"));
                holder.amount.setText(Html.fromHtml("<b><font color=#990000>Rs. " + listItem.getAmount() + ".00/-</font></b>"));
                holder.date.setText(Html.fromHtml("<font color=#2EB83D><b>" + listItem.getDate() + "</font></b>"));
                if ((listItem.getStatus()).equals("Successful")) {
                    holder.status.setText(Html.fromHtml("<font color=#2EB83D><b>Successful</font></b>"));
                } else {
                    holder.status.setText(Html.fromHtml("<font color=#FF0000><b>Failed</font></b>"));
                }
            }else if((listItem.getRmk()).equals("Cheque Withdraw")) {
                holder.name.setText(Html.fromHtml("<font color=#7500B8><b>Cheque Withdraw, &nbsp;</font><big>&#65510;</big>"));
                holder.amount.setText(Html.fromHtml("<b><font color=#990000>Rs. " + listItem.getAmount() + ".00/-</font></b>"));
                holder.date.setText(Html.fromHtml("<font color=#2EB83D><b>" + listItem.getDate() + "</font></b>"));
                if ((listItem.getStatus()).equals("Successful")) {
                    holder.status.setText(Html.fromHtml("<font color=#2EB83D><b>Successful</font></b>"));
                } else {
                    holder.status.setText(Html.fromHtml("<font color=#FF0000><b>Failed</font></b>"));
                }
            }else {
                String firstWord = listItem.getRece();
                String firstWord1 = firstWord.replaceAll(" .*", "");
                holder.name.setText(Html.fromHtml("<font color=#7500B8><b>Paid,&nbsp;&nbsp;</font><big><big>&#10230;</big></big>" +
                        "<font color=#990000>&nbsp;<font color=#990000>&nbsp;&nbsp;" + firstWord1 + "</font></b>"));
                holder.amount.setText(Html.fromHtml("<b><font color=#990000>Rs. " + listItem.getAmount() + ".00/-</font></b>"));
                holder.date.setText(Html.fromHtml("<font color=#2EB83D><b>" + listItem.getDate() + "</font></b>"));
                if ((listItem.getStatus()).equals("Successful")) {
                    holder.status.setText(Html.fromHtml("<font color=#2EB83D><b>Successful</font></b>"));
                } else {
                    holder.status.setText(Html.fromHtml("<font color=#FF0000><b>Failed</font></b>"));
                }
            }
        }else {
            if ((listItem.getStatus()).equals("Failed")){
                holder.card_view.setVisibility(View.GONE);
            }else {
                holder.id.setText(listItem.getId());
                String firstWord = listItem.getSend();
                String firstWord1 = firstWord.replaceAll(" .*", "");
                holder.name.setText(Html.fromHtml("<font color=#7500B8><b>Received,&nbsp;&nbsp;</font><big><big>&#10229;</big></big>" +
                        "<font color=#990000>&nbsp;<font color=#990000>&nbsp;&nbsp;" + firstWord1 + "</font></b>"));
                holder.amount.setText(Html.fromHtml("<b><font color=#990000>Rs. " + listItem.getAmount() + ".00/-</font></b>"));
                holder.date.setText(Html.fromHtml("<font color=#2EB83D><b>" + listItem.getDate() + "</font></b>"));
                if ((listItem.getStatus()).equals("Successful")) {
                    holder.status.setText(Html.fromHtml("<font color=#2EB83D><b>Successful</font></b>"));
                } else {
                    holder.status.setText(Html.fromHtml("<font color=#FF0000><b>Failed</font></b>"));
                }
            }
        }

        holder.card_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                Intent intent;
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                final ProgressDialog dialog = new ProgressDialog(view.getContext());
                dialog.setMessage("Loading Delete Data");
                final CharSequence[] dialogitem = {"View Transaction Details"};
                if (st_acc.equals(listItem.getAcc())){
                    first = listItem.getRece();
                }else {
                    first = listItem.getSend();
                }
                builder.setTitle(first);
                builder.setItems(dialogitem, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        switch (i){
                            case 0 :
                                Intent intent = new Intent(view.getContext(),u_tran_details.class);
                                intent.putExtra("id", listItem.getId());
                                view.getContext().startActivity(intent);
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
