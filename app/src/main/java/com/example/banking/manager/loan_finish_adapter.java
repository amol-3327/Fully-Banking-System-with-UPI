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

public class loan_finish_adapter extends RecyclerView.Adapter<loan_finish_adapter.ViewHolder>{
    private List<loan_finish_model> listItems;
    private Context context;
    private DecimalFormat formatter;
    private SimpleDateFormat dateFormat;

    public loan_finish_adapter(List<loan_finish_model> listItems, Context context) {
        this.listItems = listItems;
        this.context = context;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView id;
        public TextView acc;
        public TextView status;
        public TextView amount;
        public CardView card_view;
        public ViewHolder(View itemView ) {
            super(itemView);
            id = (TextView) itemView.findViewById(R.id.id);
            acc = (TextView) itemView.findViewById(R.id.tv_acc);
            status = (TextView) itemView.findViewById(R.id.tv_status);
            amount = (TextView) itemView.findViewById(R.id.tv_amount);
            card_view = (CardView) itemView.findViewById(R.id.card_loan_finish);
            formatter =new DecimalFormat("##,##,###.00");
            dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_finish_loan, parent, false);
        return new ViewHolder(v);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final loan_finish_model listItem = listItems.get(position);

        holder.id.setText(listItem.getId());
        holder.acc.setText(listItem.getAcc());
        if ((listItem.getAmount()).equals("0")){
            holder.status.setText(Html.fromHtml("<b><font color=#2EB83D> Finished </font></b>"));
            holder.amount.setText("₹ 0.00/-");
        }else {
            String timeStamp = new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime());
            Date d1 = null,d2=null;
            try {
                d1 = dateFormat.parse(timeStamp);
                d2 = dateFormat.parse(listItem.getDate());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            LocalDate date1 =d1.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            LocalDate date2 = d2.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            Period p = Period.between(date1, date2);
            int year=p.getYears();
            int month=p.getMonths();
            if((year == 0 && month == 0) || (year < 0 || month < 0)) {
                holder.status.setText(Html.fromHtml("<b><font color=#2EB83D> Date exhausted </font></b>"));
                holder.amount.setText("₹ " + formatter.format(Long.parseLong(listItem.getAmount())) + "/-");
            }else {
                holder.status.setText(Html.fromHtml("<b><font color=#2EB83D> Running </font></b>"));
                holder.amount.setText("₹ " + formatter.format(Long.parseLong(listItem.getAmount())) + "/-");
            }
        }

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
                                Intent intent = new Intent(view.getContext(),m_finish_details.class);
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
