package com.example.banking;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.banking.cashier.c_withdraw;
import com.example.banking.manager.m_finish_details;
import com.poovam.pinedittextfield.PinField;
import com.poovam.pinedittextfield.SquarePinField;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigInteger;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class u_apply_loan extends AppCompatActivity {
    private Spinner spin_loan,spin_no,spin_emp,spin_pro;
    private String selected_type="Select Loan Type:",st_rate,selected_emp="Type of employment:",selected_no,selected_pro="Type of Profession:";
    private TextView rate,emi,interest,t_payment,l_acc,l_type,l_rate,tv_l_amount,l_dur,l_emi,l_inte,l_total,l_status,out,e_date;
    private ProgressDialog progressDialog;
    private Button a_loan,c_emi,re_apply,e_pay;
    private RelativeLayout rl_pro;
    private View v;
    private EditText amount,l_amount,reason,e_amount;
    private DecimalFormat formatter;
    private LinearLayout ll_det,ll_app,ll_rec;
    private String ss_emi,ss_inte,ss_total,st_l_status,st_out,st_l_r_id,st_emi,w_date,st_e_date;
    private CardView delete_r,l_pay;
    private SimpleDateFormat dateFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_u_apply_loan);

        if(!SharedPrefManager.getInstance(this).isLoggedIn()){
            finish();
            startActivity(new Intent(this, home.class));
            return;
        }
        ActionBar ab=getSupportActionBar();
        ab.setTitle("Loan Application:");
        ab.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00801EBD")));
        getWindow().setBackgroundDrawableResource(R.mipmap.back2);

        spin_loan = findViewById(R.id.spin_loan);
        spin_no = findViewById(R.id.spin_no);
        spin_emp = findViewById(R.id.spin_emp);
        spin_pro = findViewById(R.id.spin_pro);
        rate = findViewById(R.id.tv_rate);
        a_loan = findViewById(R.id.bt_loan);
        rl_pro = findViewById(R.id.rl_pro);
        v = findViewById(R.id.v_v);
        amount=findViewById(R.id.et_amount);
        l_amount=findViewById(R.id.et_loan_amount);
        emi=findViewById(R.id.tv_emi);
        interest=findViewById(R.id.tv_inter);
        t_payment=findViewById(R.id.tv_pay);
        c_emi=findViewById(R.id.bt_emi);
        ll_det=findViewById(R.id.ll_det);
        reason=findViewById(R.id.et_reason);
        ll_app=findViewById(R.id.ll_app);
        ll_rec=findViewById(R.id.ll_rec);
        l_acc=findViewById(R.id.tv_acc);
        l_type=findViewById(R.id.tv_l_type);
        l_rate=findViewById(R.id.tv_l_rate);
        tv_l_amount=findViewById(R.id.tv_l_amount);
        l_dur=findViewById(R.id.tv_l_dura);
        l_emi=findViewById(R.id.tv_l_emi);
        l_inte=findViewById(R.id.tv_l_inte);
        l_total=findViewById(R.id.tv_l_total);
        l_status=findViewById(R.id.tv_l_status);
        delete_r=findViewById(R.id.c_delete_r);
        re_apply=findViewById(R.id.bt_re_apply);
        l_pay=findViewById(R.id.c_l_pay);
        out=findViewById(R.id.tv_out);
        e_amount=findViewById(R.id.et_e_amount);
        e_pay=findViewById(R.id.bt_e_pay);
        e_date=findViewById(R.id.tv_e_date);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);

        formatter =new DecimalFormat("##,##,###.00");
        dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        final String[] type = {"Select Loan Type:","Home Loan","Car Loan","Personal Loan"};
        ArrayAdapter<CharSequence> langAdapter = new ArrayAdapter<CharSequence>(u_apply_loan.this, R.layout.spin_text, type);
        langAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown);
        spin_loan.setAdapter(langAdapter);

        final String[] no = {"1","2","3","4","5","6","7","8","9","10"};
        ArrayAdapter<CharSequence> langAdapter1 = new ArrayAdapter<CharSequence>(u_apply_loan.this, R.layout.spin_text, no);
        langAdapter1.setDropDownViewResource(R.layout.simple_spinner_dropdown);
        spin_no.setAdapter(langAdapter1);

        final String[] emp = {"Type of employment:","Salaried","Self Employed Prof."};
        ArrayAdapter<CharSequence> langAdapter2 = new ArrayAdapter<CharSequence>(u_apply_loan.this, R.layout.spin_text, emp);
        langAdapter2.setDropDownViewResource(R.layout.simple_spinner_dropdown);
        spin_emp.setAdapter(langAdapter2);

        final String[] pro = {"Type of Profession:","Architect","Chartered Accountants","Consultants","Dentist","Doctor","Engineer","Lawyers"};
        ArrayAdapter<CharSequence> langAdapter3 = new ArrayAdapter<CharSequence>(u_apply_loan.this, R.layout.spin_text, pro);
        langAdapter3.setDropDownViewResource(R.layout.simple_spinner_dropdown);
        spin_pro.setAdapter(langAdapter3);

        getPrevious();

        spin_pro.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selected_pro = adapterView.getSelectedItem().toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {  }
        });

        spin_no.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selected_no = adapterView.getSelectedItem().toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {  }
        });

        spin_emp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selected_emp = adapterView.getSelectedItem().toString();
                if (selected_emp.equals("Self Employed Prof.")){
                    rl_pro.setVisibility(View.VISIBLE);
                    v.setVisibility(View.VISIBLE);
                }else {
                    rl_pro.setVisibility(View.GONE);
                    v.setVisibility(View.GONE);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {  }
        });

        amount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void onTextChanged(CharSequence sw, int i, int i1, int i2) {
                amount.removeTextChangedListener(this);
                try {
                    String originalString = sw.toString();
                    Long longval;
                    if (originalString.contains(",")) {
                        originalString = originalString.replaceAll(",", "");
                    }
                    longval = Long.parseLong(originalString);
                    DecimalFormat formatter = (DecimalFormat) DecimalFormat.getCurrencyInstance(new Locale("en", "in"));
                    formatter.applyPattern("#,###,###,###");
                    String formattedString = formatter.format(longval);
                    amount.setText(formattedString);
                    amount.setSelection(amount.getText().length());
                }catch (NumberFormatException nfe){
                    nfe.printStackTrace();
                }amount.addTextChangedListener(this);
            }
            @Override
            public void afterTextChanged(Editable editable) { }
        });
        l_amount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void onTextChanged(CharSequence sw, int i, int i1, int i2) {
                l_amount.removeTextChangedListener(this);
                try {
                    ll_det.setVisibility(View.GONE);
                    String originalString = sw.toString();
                    Long longval;
                    if (originalString.contains(",")) {
                        originalString = originalString.replaceAll(",", "");
                    }
                    longval = Long.parseLong(originalString);
                    DecimalFormat formatter = (DecimalFormat) DecimalFormat.getCurrencyInstance(new Locale("en", "in"));
                    formatter.applyPattern("#,###,###,###");
                    String formattedString = formatter.format(longval);
                    l_amount.setText(formattedString);
                    l_amount.setSelection(l_amount.getText().length());
                }catch (NumberFormatException nfe){
                    nfe.printStackTrace();
                }l_amount.addTextChangedListener(this);
            }
            @Override
            public void afterTextChanged(Editable editable) { }
        });
        e_amount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void onTextChanged(CharSequence sw, int i, int i1, int i2) {
                e_amount.removeTextChangedListener(this);
                try {
                    String originalString = sw.toString();
                    Long longval;
                    if (originalString.contains(",")) {
                        originalString = originalString.replaceAll(",", "");
                    }
                    longval = Long.parseLong(originalString);
                    DecimalFormat formatter = (DecimalFormat) DecimalFormat.getCurrencyInstance(new Locale("en", "in"));
                    formatter.applyPattern("#,###,###,###");
                    String formattedString = formatter.format(longval);
                    e_amount.setText(formattedString);
                    e_amount.setSelection(e_amount.getText().length());
                }catch (NumberFormatException nfe){
                    nfe.printStackTrace();
                }e_amount.addTextChangedListener(this);
            }
            @Override
            public void afterTextChanged(Editable editable) { }
        });

        spin_loan.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selected_type = adapterView.getSelectedItem().toString();
                if (selected_type.equals("Select Loan Type:")){
                }else {
                    ll_det.setVisibility(View.GONE);
                    progressDialog.show();
                    StringRequest stringRequest = new StringRequest(
                            Request.Method.POST,
                            Constants.URL_GET_RATE,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    progressDialog.dismiss();
                                    try {
                                        JSONObject obj = new JSONObject(response);
                                        if (!obj.getBoolean("error")) {
                                            st_rate = obj.getString("rate");
                                            rate.setText(Html.fromHtml(st_rate + "%"));
                                        } else {
                                            AlertDialog.Builder builderDel = new AlertDialog.Builder(u_apply_loan.this);
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
                                    AlertDialog.Builder builderDel = new AlertDialog.Builder(u_apply_loan.this);
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
                            params.put("type", selected_type);
                            return params;
                        }
                    };
                    RequestQueue requestQueue = Volley.newRequestQueue(u_apply_loan.this);
                    requestQueue.add(stringRequest);

                    requestQueue.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<Object>() {
                        @Override
                        public void onRequestFinished(Request<Object> request) {
                            requestQueue.getCache().clear();
                        }
                    });
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {  }
        });

        c_emi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String st_l_amount=l_amount.getText().toString().trim();
                final String st_l_amount1=st_l_amount.replaceAll(",", "");
                if (selected_type.equals("Select Loan Type:")){
                    Toast.makeText(u_apply_loan.this,"Please Select Loan type.",Toast.LENGTH_SHORT).show();
                }else if (st_l_amount1.length() < 4){
                    l_amount.setError("Please Enter loan amount");
                }else if (st_rate.isEmpty()){
                    Toast.makeText(u_apply_loan.this,"Please Select Loan type.",Toast.LENGTH_SHORT).show();
                }else {
                    float do_rate=Float.parseFloat(st_rate);
                    float do_l_amount=Float.parseFloat(st_l_amount1);
                    float do_duration=Float.parseFloat(selected_no);
                    float emi1;
                    int total,inte;

                    do_rate = do_rate / (12 * 100);
                    do_duration = do_duration * 12;
                    emi1 = (do_l_amount * do_rate * (float)Math.pow(1 + do_rate, do_duration))
                            / (float)(Math.pow(1 + do_rate, do_duration) - 1);

                    String result=String.valueOf(Math.round(emi1));
                    total=(Integer.parseInt(result)) *((Integer.parseInt(selected_no)) * 12) ;
                    inte=(total - (Integer.parseInt(st_l_amount1)));

                    emi.setText("₹ "+formatter.format(Long.parseLong(result)));
                    interest.setText("₹ "+formatter.format(inte));
                    t_payment.setText("₹ "+formatter.format(total));
                    ll_det.setVisibility(View.VISIBLE);

                    ss_emi=result;
                    ss_inte=String.valueOf(inte);
                    ss_total=String.valueOf(total);
                }
            }
        });

        a_loan.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                final String st_l_amount=l_amount.getText().toString().trim();
                final String st_l_amount1=st_l_amount.replaceAll(",", "");
                final String st_amount=amount.getText().toString().trim();
                final String st_amount1=st_amount.replaceAll(",", "");
                final String st_reason=reason.getText().toString().trim();
                final String st_txt;
                if (selected_type.equals("Select Loan Type:")){
                    Toast.makeText(u_apply_loan.this,"Please Select Loan type.",Toast.LENGTH_SHORT).show();
                }else if (st_l_amount1.equals("0") || st_l_amount1.isEmpty()){
                    l_amount.setError("Please Enter loan amount");
                }else if (st_rate.isEmpty()){
                    Toast.makeText(u_apply_loan.this,"Please Select Loan type.",Toast.LENGTH_SHORT).show();
                }else  if (selected_emp.equals("Type of employment:")){
                    Toast.makeText(u_apply_loan.this,"Please Select Type of employment.",Toast.LENGTH_SHORT).show();
                }else if (selected_emp.equals("Self Employed Prof.") && selected_pro.equals("Type of Profession:")){
                    Toast.makeText(u_apply_loan.this,"Please Select Type of Profession.",Toast.LENGTH_SHORT).show();
                }else if (st_amount1.equals("0") || st_amount1.isEmpty()){
                    amount.setError("Please Enter amount");
                }else if (st_reason.isEmpty()) {
                    reason.setError("Please Enter reason");
                }else {
                    progressDialog.show();
                    if (selected_pro.equals("Type of Profession:")){
                        st_txt="null";
                    }else {
                        st_txt=selected_pro;
                    }

                    StringRequest stringRequest = new StringRequest(
                            Request.Method.POST,
                            Constants.URL_APPLY_LOAN,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    progressDialog.dismiss();
                                    try {
                                        JSONObject obj = new JSONObject(response);
                                        if (!obj.getBoolean("error")) {
                                            AlertDialog.Builder builderDel = new AlertDialog.Builder(u_apply_loan.this);
                                            builderDel.setCancelable(false);
                                            builderDel.setMessage(obj.getString("message"));
                                            builderDel.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    getPrevious();
                                                    dialogInterface.dismiss();
                                                }
                                            });
                                            builderDel.create().show();
                                        } else {
                                            AlertDialog.Builder builderDel = new AlertDialog.Builder(u_apply_loan.this);
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
                                    AlertDialog.Builder builderDel = new AlertDialog.Builder(u_apply_loan.this);
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
                            params.put("acc", SharedPrefManager.getInstance(u_apply_loan.this).getAccNo());
                            params.put("type", selected_type);
                            params.put("rate", st_rate);
                            params.put("l_amount", st_l_amount1);
                            params.put("dura", selected_no);
                            params.put("emi", ss_emi);
                            params.put("inte", ss_inte);
                            params.put("total", ss_total);
                            params.put("emp", selected_emp);
                            params.put("pro", st_txt);
                            params.put("sal", st_amount1);
                            params.put("reason", st_reason);
                            return params;
                        }
                    };
                    RequestQueue requestQueue = Volley.newRequestQueue(u_apply_loan.this);
                    requestQueue.add(stringRequest);

                    requestQueue.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<Object>() {
                        @Override
                        public void onRequestFinished(Request<Object> request) {
                            requestQueue.getCache().clear();
                        }
                    });
                }
            }
        });

        re_apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeS("delete_l_r");
            }
        });

        delete_r.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builderDel = new AlertDialog.Builder(u_apply_loan.this);
                builderDel.setMessage("Are you sure, You want to delete loan request?");
                builderDel.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        changeS("delete_l_r");
                    }
                });
                builderDel.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                builderDel.create().show();
            }
        });
        e_pay.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                final String str_e_amount=e_amount.getText().toString().trim();
                final String str_e_amount1=str_e_amount.replaceAll(",", "");
                int am=0,ba=0;
                if (str_e_amount1.equals("0") || str_e_amount1.isEmpty()){
                    e_amount.setError("Please Enter EMI amount");
                } else {
                    am = Integer.parseInt(str_e_amount1);
                    ba = Integer.parseInt(st_out);
                }

                if (str_e_amount1.equals("0") || str_e_amount1.isEmpty()){
                e_amount.setError("Please Enter EMI amount");
                } else if (am > ba) {
                    Toast.makeText(u_apply_loan.this,"Entered amount is greater then outstanding balance.",Toast.LENGTH_SHORT).show();
                }else {
                    String timeStamp = new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime());

                    final Dialog dialog = new Dialog(u_apply_loan.this);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setCancelable(false);
                    final View customLayout = getLayoutInflater().inflate(R.layout.loan_payment, null);
                    dialog.setContentView(customLayout);
                    ImageView close=(customLayout).findViewById(R.id.i_close);
                    SquarePinField u_pin=(SquarePinField) customLayout.findViewById(R.id.loan_u_pin);

                    close.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
                        }
                      });

                    u_pin.setOnTextCompleteListener(new PinField.OnTextCompleteListener() {
                        @Override
                        public boolean onTextComplete(@NotNull String enteredText) {
                            progressDialog.show();
                                StringRequest stringRequest = new StringRequest(
                                        Request.Method.POST,
                                        Constants.URL_PAY_LOAN_EMI,
                                        new Response.Listener<String>() {
                                            @Override
                                            public void onResponse(String response) {
                                                progressDialog.dismiss();
                                                try {
                                                    JSONObject obj = new JSONObject(response);
                                                    if (!obj.getBoolean("error")) {
                                                        u_pin.setText("");
                                                        AlertDialog.Builder builderDel = new AlertDialog.Builder(u_apply_loan.this);
                                                        builderDel.setCancelable(false);
                                                        builderDel.setMessage(obj.getString("message"));
                                                        builderDel.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                                dialogInterface.dismiss();
                                                                finish();
                                                                startActivity(new Intent(u_apply_loan.this,u_apply_loan.class));
                                                            }
                                                        });
                                                        builderDel.create().show();
                                                    } else {
                                                        u_pin.setText("");
                                                        AlertDialog.Builder builderDel = new AlertDialog.Builder(u_apply_loan.this);
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
                                                u_pin.setText("");
                                                AlertDialog.Builder builderDel = new AlertDialog.Builder(u_apply_loan.this);
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
                                        params.put("amo", str_e_amount1);
                                        params.put("date",timeStamp);
                                        params.put("pin", enteredText);
                                        params.put("acc", SharedPrefManager.getInstance(u_apply_loan.this).getAccNo());
                                        params.put("r_id",st_l_r_id);
                                        return params;
                                    }
                                };
                            RequestQueue requestQueue = Volley.newRequestQueue(u_apply_loan.this);
                            requestQueue.add(stringRequest);

                            requestQueue.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<Object>() {
                                @Override
                                public void onRequestFinished(Request<Object> request) {
                                    requestQueue.getCache().clear();
                                }
                            });
                            return false;
                        }
                    });
                    dialog.show();
                    dialog.getWindow().getDecorView().setBackgroundResource(android.R.color.transparent);
                }
            }
        });
    }

    public void changeS(String s){
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                Constants.URL_PREVIOUS_REQUEST,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        try {
                            JSONObject obj = new JSONObject(response);
                            if (!obj.getBoolean("error")) {
                                AlertDialog.Builder builderDel = new AlertDialog.Builder(u_apply_loan.this);
                                builderDel.setCancelable(false);
                                builderDel.setMessage(obj.getString("message"));
                                builderDel.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                        finish();
                                        startActivity(new Intent(u_apply_loan.this, u_apply_loan.class));
                                    }
                                });
                                builderDel.create().show();
                            } else {
                                AlertDialog.Builder builderDel = new AlertDialog.Builder(u_apply_loan.this);
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
                        AlertDialog.Builder builderDel = new AlertDialog.Builder(u_apply_loan.this);
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
                params.put("acc", SharedPrefManager.getInstance(u_apply_loan.this).getAccNo());
                params.put("txt", s);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(u_apply_loan.this);
        requestQueue.add(stringRequest);

        requestQueue.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<Object>() {
            @Override
            public void onRequestFinished(Request<Object> request) {
                requestQueue.getCache().clear();
            }
        });
    }

    public void getPrevious(){
        ll_app.setVisibility(View.GONE);
        ll_rec.setVisibility(View.GONE);
        re_apply.setVisibility(View.GONE);
        l_pay.setVisibility(View.GONE);

        progressDialog.show();
        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                Constants.URL_PREVIOUS_REQUEST,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        try {
                            JSONObject obj = new JSONObject(response);
                            if (!obj.getBoolean("error")) {
                                ll_rec.setVisibility(View.VISIBLE);
                                ll_app.setVisibility(View.GONE);

                                l_acc.setText(obj.getString("acc"));
                                l_type.setText(obj.getString("type"));
                                l_rate.setText(Html.fromHtml(obj.getString("rate") + "%"));
                                tv_l_amount.setText("₹ "+formatter.format(Long.parseLong(obj.getString("l_amount")))+"/-");
                                l_dur.setText(obj.getString("dura"));
                                st_emi=obj.getString("emi");
                                l_emi.setText("₹ "+formatter.format(Long.parseLong(st_emi))+"/-");
                                l_inte.setText("₹ "+formatter.format(Long.parseLong(obj.getString("inte")))+"/-");
                                l_total.setText("₹ "+formatter.format(Long.parseLong(obj.getString("total")))+"/-");
                                st_l_status=obj.getString("status");
                                if (st_l_status.equals("In")){
                                    delete_r.setVisibility(View.VISIBLE);
                                    l_status.setText(Html.fromHtml("<font color=#2EB83D><b>Pending</font></b>"));
                                }else if (st_l_status.equals("Out")) {
                                    l_pay.setVisibility(View.VISIBLE);
                                    getData();
                                    l_status.setText(Html.fromHtml("<font color=#2EB83D><b>Approved</font></b>"));
                                }else {
                                    re_apply.setVisibility(View.VISIBLE);
                                    l_status.setText(Html.fromHtml("<font color=#FF0000><b>Request Rejected</font></b>"));
                                }
                            } else {
                                ll_rec.setVisibility(View.GONE);
                                ll_app.setVisibility(View.VISIBLE);
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
                        AlertDialog.Builder builderDel = new AlertDialog.Builder(u_apply_loan.this);
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
                params.put("acc", SharedPrefManager.getInstance(u_apply_loan.this).getAccNo());
                params.put("txt","previous_loan");
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(u_apply_loan.this);
        requestQueue.add(stringRequest);

        requestQueue.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<Object>() {
            @Override
            public void onRequestFinished(Request<Object> request) {
                requestQueue.getCache().clear();
            }
        });
    }
    public void getData(){
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                Constants.URL_PREVIOUS_REQUEST,
                new Response.Listener<String>() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        try {
                            JSONObject obj = new JSONObject(response);
                            if (!obj.getBoolean("error")) {
                                st_l_r_id=obj.getString("id");
                                w_date=obj.getString("date");
                                st_out=obj.getString("out");
                                st_e_date=obj.getString("e_date");
                                e_amount.setText(st_emi);
                                if (st_out.equals("0")){
                                    l_status.setText(Html.fromHtml("<font color=#2EB83D><b>Finished</font></b>"));
                                    l_pay.setVisibility(View.GONE);
                                    AlertDialog.Builder builderDel = new AlertDialog.Builder(u_apply_loan.this);
                                    builderDel.setCancelable(false);
                                    builderDel.setMessage("Your EMI pay for loan is completed, please inform the bank.");
                                    builderDel.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            dialogInterface.dismiss();
                                        }
                                    });
                                    builderDel.create().show();
                                }else {
                                    String timeStamp = new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime());
                                    Date d1 = dateFormat.parse(timeStamp);
                                    Date d2 = dateFormat.parse(st_e_date);
                                    LocalDate date1 =d1.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                                    LocalDate date2 = d2.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                                    Period p = Period.between(date1, date2);
                                    int year=p.getYears();
                                    int month=p.getMonths();

                                    if((year == 0 && month == 0) || (year < 0 || month < 0)) {
                                        e_pay.setEnabled(false);
                                        AlertDialog.Builder builderDel = new AlertDialog.Builder(u_apply_loan.this);
                                        builderDel.setCancelable(false);
                                        builderDel.setMessage("Your EMI pay date is exhausted please contact to bank.");
                                        builderDel.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                dialogInterface.dismiss();
                                            }
                                        });
                                        builderDel.create().show();
                                        e_date.setText(Html.fromHtml("<font color=#FF0000><b>Date exhausted</font></b>"));
                                        out.setText("₹ "+formatter.format(Long.parseLong(st_out))+"/-");
                                    }else {
                                        l_pay.setVisibility(View.VISIBLE);
                                        out.setText("₹ "+formatter.format(Long.parseLong(st_out))+"/-");
                                        e_date.setText(p.getYears() + " - Years   " + p.getMonths() + " - Months");
                                    }
                                }
                            } else {
                                l_pay.setVisibility(View.GONE);
                            }
                        } catch (JSONException | ParseException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        AlertDialog.Builder builderDel = new AlertDialog.Builder(u_apply_loan.this);
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
                params.put("acc", SharedPrefManager.getInstance(u_apply_loan.this).getAccNo());
                params.put("txt","get_out");
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(u_apply_loan.this);
        requestQueue.add(stringRequest);

        requestQueue.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<Object>() {
            @Override
            public void onRequestFinished(Request<Object> request) {
                requestQueue.getCache().clear();
            }
        });
    }
}