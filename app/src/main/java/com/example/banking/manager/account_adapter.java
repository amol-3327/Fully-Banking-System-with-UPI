package com.example.banking.manager;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.banking.R;
import com.example.banking.cashier.c_dashboard;
import com.example.banking.cashier.c_profile_change;
import com.example.banking.u_apply_loan;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class account_adapter extends RecyclerView.Adapter<account_adapter.ViewHolder>{
    private List<account_model> listItems;
    private Context context;

    public account_adapter(List<account_model> listItems, Context context) {
        this.listItems = listItems;
        this.context = context;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView id;
        public TextView acc;
        public TextView mob;
        public CardView card_view;
        public ViewHolder(View itemView ) {
            super(itemView);
            id = (TextView) itemView.findViewById(R.id.id);
            acc = (TextView) itemView.findViewById(R.id.tv_acc);
            mob = (TextView) itemView.findViewById(R.id.tv_mob);
            card_view = (CardView) itemView.findViewById(R.id.card_account);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_account, parent, false);
        return new ViewHolder(v);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final account_model listItem = listItems.get(position);

        holder.id.setText(listItem.getAcc());
        holder.acc.setText(listItem.getAcc());
        holder.mob.setText(listItem.getMob());

        holder.card_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                final CharSequence[] dialogitem = {"View Account Details"};
                builder.setTitle(listItem.getAcc());
                builder.setItems(dialogitem, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        switch (i){
                            case 0:
                                Intent intent = new Intent(view.getContext(), m_a_e_details.class);
                                intent.putExtra("acc", listItem.getAcc());
                                intent.putExtra("rmk", "user");
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
