package team.ten.buyticket;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

import team.ten.buyticket.Activity_login.Login_Activity;

public class CreditCardActivity extends AppCompatActivity {

    int amount = 0;
    TextView tv_money;
    TextView tv_orderNum;
    EditText cardNum1,cardNum2,cardNum3,cardNum4,cardDate_M,cardDate_Y,cardCode;
    Button bt_submit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credit_card);

        Bundle bundle =this.getIntent().getExtras();
        int amount = bundle.getInt("deposit_amount",0);

        this.amount =amount;
        bindData(amount);
    }

    public void bindData(int amount){
        tv_money = (TextView)findViewById(R.id.credit_value_orderMoney);
        tv_money.setText( String.valueOf(amount));

        tv_orderNum = (TextView)findViewById(R.id.credit_value_orderNum);
        tv_orderNum.setText(getTradeNum());

        cardNum1= (EditText)findViewById(R.id.credit_value_creditNum_1);
        cardNum2= (EditText)findViewById(R.id.credit_value_creditNum_2);
        cardNum3= (EditText)findViewById(R.id.credit_value_creditNum_3);
        cardNum4= (EditText)findViewById(R.id.credit_value_creditNum_4);
        cardDate_M= (EditText)findViewById(R.id.credit_value_expire_month);
        cardDate_Y= (EditText)findViewById(R.id.credit_value_expire_year);
        cardCode= (EditText)findViewById(R.id.credit_value_code);

        cardNum1.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start,int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,int before, int count) {
                String text = cardNum1.getText().toString();
                if(text.length() == 4){
                    cardNum2.requestFocus();
                }
            }
        });

        cardNum2.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start,int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,int before, int count) {
                String text = cardNum2.getText().toString();
                if(text.length() == 4){
                    cardNum3.requestFocus();
                }
            }
        });

        cardNum3.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start,int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,int before, int count) {
                String text = cardNum3.getText().toString();
                if(text.length() == 4){
                    cardNum4.requestFocus();
                }
            }
        });

        bt_submit = (Button)findViewById(R.id.credit_bt_submit);
        bt_submit.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(final View v){
                String str_cardNum_1 = cardNum1.getText().toString();
                String str_cardNum_2 = cardNum2.getText().toString();
                String str_cardNum_3 = cardNum3.getText().toString();
                String str_cardNum_4 = cardNum4.getText().toString();
                String str_cardCode = cardCode.getText().toString();

                int int_cardMonth = 0;
                try{
                    int_cardMonth = Integer.parseInt(cardDate_M.getText().toString());
                }catch (Exception e){
                }

                int int_cardYear = 0;
                try{
                    int_cardYear = Integer.parseInt(cardDate_Y.getText().toString());
                }catch (Exception e){
                }


                if(str_cardNum_1.length() != 4 || str_cardNum_2.length() != 4 || str_cardNum_3.length() != 4 || str_cardNum_4.length() != 4 ){
                    Toast.makeText(getApplicationContext(), "信用卡卡號長度不符", Toast.LENGTH_SHORT).show();
                }else if(str_cardCode.length() != 3){
                    Toast.makeText(getApplicationContext(), "信用卡驗證碼長度不符", Toast.LENGTH_SHORT).show();
                }else if(int_cardMonth < 1 || int_cardMonth > 12 || int_cardYear < 2018 || int_cardYear > 2099){
                    Toast.makeText(getApplicationContext(), "信用卡到期日格式不符", Toast.LENGTH_SHORT).show();
                }else {
                    //我沒有存資料 我甚麼都沒做...
                    ProgressDialog dialog = ProgressDialog.show(CreditCardActivity.this, "驗證中", "請稍後", true);
                    final DialogInterface Dialog = dialog;
                    postData();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(1500);
                            } catch (Exception e) {
                                e.printStackTrace();
                            } finally {
                                Dialog.dismiss();
                            }
                            CreditCardActivity.this.runOnUiThread(new Runnable() {
                                public void run() {


                                    Toast.makeText(getApplicationContext(), "加值完成!", Toast.LENGTH_SHORT).show();

                                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);

                                    Bundle bundle = new Bundle();
                                    bundle.putString("activity", "Myself");
                                    intent.putExtras(bundle);


                                    startActivity(intent);
                                    finish();
                                }
                            });

                        }
                    }).start();
                }


            }
        });

    }

    public String getTradeNum(){
        String a = "";

        Date date = new Date();
        a = new SimpleDateFormat("yyyyMMdd").format(date);

        for(int i = 1; i <= 8; i++){
            int ran = (int)(Math.random()*9+0);
            a += String.valueOf(ran);
        }

        return a;
    }

    public void postData(){
        String url = "https://jaspersui.pw/api/user/deposit/";
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("请求结果:" ,response);
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if(response.contains("success")){


                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("請求錯誤:", error.toString());
                Toast.makeText(getApplicationContext(), "網路異常，加值失敗", Toast.LENGTH_SHORT).show();
            }
        })

        {
            @Override
            public byte[] getBody() throws AuthFailureError {
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                String email = prefs.getString("email", "");
                String query = "{\"user_email\": \""+email+"\",\"money\":"+ Integer.parseInt(tv_money.getText().toString()) + "}";
                return query.getBytes();
            }

            @Override
            public String getBodyContentType() {
                return "application/json";
            }

        };

        RequestQueue rQueue = Volley.newRequestQueue(getApplicationContext().getApplicationContext());
        rQueue.add(request);
    }
}
