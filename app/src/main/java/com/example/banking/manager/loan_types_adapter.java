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

public class loan_types_adapter extends RecyclerView.Adapter<loan_types_adapter.ViewHolder>{
    private List<loan_types_model> listItems;
    private Context context;

    public loan_types_adapter(List<loan_types_model> listItems, Context context) {
        this.listItems = listItems;
        this.context = context;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView id;
        public TextView type;
        public TextView rate;
        public CardView card_view;
        public ViewHolder(View itemView ) {
            super(itemView);
            id = (TextView) itemView.findViewById(R.id.id);
            type = (TextView) itemView.findViewById(R.id.tv_type);
            rate = (TextView) itemView.findViewById(R.id.tv_rate);
            card_view = (CardView) itemView.findViewById(R.id.card_l_types);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_loan_types, parent, false);
        return new ViewHolder(v);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final loan_types_model listItem = listItems.get(position);

        holder.id.setText(listItem.getId());
        holder.type.setText(listItem.getType());
        holder.rate.setText(listItem.getRate());

        holder.card_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                final CharSequence[] dialogitem = {"View Details"};
                builder.setTitle(listItem.getType());
                builder.setItems(dialogitem, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        switch (i){
                            case 0:
                                Intent intent = new Intent(view.getContext(), m_details_new_type.class);
                                intent.putExtra("id", listItem.getId());
                                intent.putExtra("type", listItem.getType());
                                intent.putExtra("rate", listItem.getRate());
                                intent.putExtra("rmk", "type");
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
