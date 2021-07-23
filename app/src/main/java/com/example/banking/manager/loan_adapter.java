package com.example.banking.manager;
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
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.banking.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class loan_adapter extends RecyclerView.Adapter<loan_adapter.ViewHolder>{
    private List<loan_model> listItems;
    private Context context;
    private DecimalFormat formatter;


    public loan_adapter(List<loan_model> listItems, Context context) {
        this.listItems = listItems;
        this.context = context;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView id;
        public TextView acc;
        public TextView type;
        public TextView amount;
        public CardView card_view;
        public ViewHolder(View itemView ) {
            super(itemView);
            id = (TextView) itemView.findViewById(R.id.id);
            acc = (TextView) itemView.findViewById(R.id.tv_acc);
            type = (TextView) itemView.findViewById(R.id.tv_type);
            amount = (TextView) itemView.findViewById(R.id.tv_amount);
            card_view = (CardView) itemView.findViewById(R.id.card_cheque);
            formatter =new DecimalFormat("##,##,###.00");
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_loan, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final loan_model listItem = listItems.get(position);

        holder.id.setText(listItem.getId());
        holder.acc.setText(listItem.getAcc() );
        holder.type.setText(listItem.getType());
        holder.amount.setText(Html.fromHtml("<b><font color=#000000>₹ "+formatter.format(Long.parseLong(listItem.getAmount()))+"/-</font></b>"));

        holder.card_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                final CharSequence[] dialogitem = {"View Request Details"};
                builder.setTitle(listItem.getAcc());
                builder.setItems(dialogitem, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        switch (i){
                            case 0:
                                Intent intent = new Intent(view.getContext(),m_loan_details.class);
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
