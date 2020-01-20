package team.ten.buyticket;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import team.ten.buyticket.Fragment.InputNumberDialog;
import team.ten.buyticket.function.MyStringRequest;
import team.ten.buyticket.model.aConcert;
import team.ten.buyticket.function.getJson;

public class    BuyActivity extends AppCompatActivity {


    public static final String HOST = "https://jaspersui.pw/api/";
    public static final String FORMAT = "/?format=json";

    public static final String getTicket = "ticket_type";
    public static final String buyTempTicket = "temp_ticket";
    public static final String buyTicket = "ticket";

    String buyMode;

    aConcert thisConcert;
    String url;
    String selectedId;
    int thePrice =0;
    String selectedName;
    String selectedNumber;

    ProgressDialog dialog;
    Button bt_buy;
    Button bt_cancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acticity_buyinfo);

        bt_buy = (Button)findViewById(R.id.button_ok);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        buyMode = prefs.getString("BuyMode", "Online");
        dialog = new ProgressDialog(BuyActivity.this);
        dialog.setMessage("Loading....");
        dialog.show();


        //===================================== 抓取主頁傳送資料 =====================================
        Intent intent = getIntent();
        Bundle b = intent.getExtras();
        TextView label_id = (TextView) findViewById(R.id.label_hide);
        label_id.setText(String.valueOf(b.get("id")));
        //===================================== 抓取主頁傳送資料 =====================================



        url  = HOST + getTicket + "/"+ b.get("id") + FORMAT;
        pageLinkData();


    }

    public static JSONObject getTheConcert (ArrayList<JSONObject> jObject, int id) throws JSONException {
        for(int i = 0; i < jObject.size(); i ++){
            int thisID = Integer.parseInt(jObject.get(i).get("id").toString());
            if(thisID == id){
                return jObject.get(i);
            }
        }

        return null;
    }

    public void pageLinkData(){
        //===================================== 抓取資料 =====================================
        MyStringRequest request = new MyStringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {

                ArrayList<JSONObject> jObject = null;
                try {
                    jObject = new getJson().parseJson(s);

                    TextView label_id = (TextView)findViewById(R.id.label_hide);
                    String id = label_id.getText().toString();

                    aConcert concert = new aConcert(getTheConcert(jObject,Integer.parseInt(id)));
                    thisConcert = concert;
                    setLabelText(concert);

                    thePrice = Integer.parseInt(concert.cPrice);

                    bt_buy.setEnabled(true);
                    dialog.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

            }
        });


        RequestQueue rQueue = Volley.newRequestQueue(BuyActivity.this);
        rQueue.add(request);
        //===================================== 抓取資料 =====================================
    }

    public void setLabelText(aConcert concert){

        //TextView label_name = (TextView)findViewById(R.id.label_concertBuy_title);
        CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.buyinfo_collapsing_toolbar_layout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.buyinfo_toolbar);

        TextView label_date_start = (TextView)findViewById(R.id.label_concertBuy_start);
        TextView label_date_end = (TextView)findViewById(R.id.label_concertBuy_stop);
        TextView label_seat = (TextView)findViewById(R.id.label_concertBuy_seat);
        TextView label_money = (TextView)findViewById(R.id.label_concertBuy_money);
        TextView label_description = (TextView)findViewById(R.id.label_concertBuy_description);
        ImageView imgViewIcon = (ImageView) findViewById(R.id.img_concertBuy_pic);


        selectedId = String.valueOf(concert.cId);
//        label_name.setText(concert.cName);
        collapsingToolbarLayout.setTitle(concert.cName);
        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.CollapsedAppBar);
        collapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.CollapsedAppBar);

        selectedName = concert.cName;
        label_money.setText("NT. "+ concert.cPrice);
        label_date_start.setText(concert.cTime_start_str);
        label_date_end.setText(concert.cTime_end_str);
        label_seat.setText(concert.cSeat_remain + "/" + concert.cSeat_number);
        label_description.setText(concert.cContent);

        Picasso.get()
                .load(concert.cUrl)
                .centerCrop()
                .fit()
                .into(imgViewIcon);




        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                go_back(v);
            }

        });

    }

    public void go_back(View view) {

        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        this.finish();
    }


    public void okay(View view) {
        Date now = new Date();
        if((now.after(thisConcert.cTime_start_date) && now.before(thisConcert.cTime_end_date)) || true) {


            //取得剩餘位置數
            TextView label_seat = (TextView) findViewById(R.id.label_concertBuy_seat);
            String strSeat = label_seat.getText().toString();
            int peopleRemain = Integer.parseInt(strSeat.substring(0, strSeat.indexOf("/")));


            if(peopleRemain > 0){
                showNumberDialog(peopleRemain);
            }else{

                AlertDialog.Builder builder1 = new AlertDialog.Builder(view.getContext());
                builder1.setTitle("無法購買");
                builder1.setMessage("目前票券已全數售盡，無法購買");
                builder1.setIcon(getResources().getDrawable(android.R.drawable.ic_dialog_alert));

                builder1.setPositiveButton(
                        "確定",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Toast.makeText(getApplicationContext(), "已取消購票", Toast.LENGTH_SHORT).show();
                                dialog.cancel();
                            }
                        });

                AlertDialog alert11 = builder1.create();
                alert11.show();
            }


        }else{
            Toast.makeText(getApplicationContext(), "此票目前無法購買，請於購票期間再行操作，謝謝", Toast.LENGTH_LONG).show();

        }
    }


    public void showNumberDialog(int peopleRemain){
        new InputNumberDialog(BuyActivity.this, new InputNumberDialog.InputNumberDialogListener() {
            @Override
            public void onOK(final String number) {

                selectedNumber = number;
                confirmInfo();
            }

            @Override
            public void onCancel(String number) {
                Toast.makeText(getApplicationContext(), "已取消購票", Toast.LENGTH_SHORT).show();
            }
        },peopleRemain).setNumber("1").show();
    }



    public void confirmInfo(){
        if(Integer.parseInt(selectedNumber) > 0){

            //取得剩餘位置數
            TextView label_seat = (TextView) findViewById(R.id.label_concertBuy_seat);
            String strSeat = label_seat.getText().toString();
            int peopleRemain = Integer.parseInt(strSeat.substring(0, strSeat.indexOf("/")));

            if(Integer.parseInt(selectedNumber) > peopleRemain){
                AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
                builder1.setTitle("無法購買");
                builder1.setMessage("選擇數量不在範圍內，無法購買");
                builder1.setIcon(getResources().getDrawable(android.R.drawable.ic_dialog_alert));

                builder1.setPositiveButton(
                        "確定",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Toast.makeText(getApplicationContext(), "已取消購票", Toast.LENGTH_SHORT).show();
                                dialog.cancel();
                            }
                        });

                AlertDialog alert11 = builder1.create();
                alert11.show();
            }else {
                int totalM = 0;
                totalM = thePrice * Integer.parseInt(selectedNumber);

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("購票確認");
                builder.setMessage("您目前設定的購票資訊如下\n購票名稱：" + selectedName + "\n購票張數：" + selectedNumber + "\n\n總金額為：NT."+totalM+"\n\n您確定要購買嗎？");
                builder.setCancelable(false);
                builder.setPositiveButton("確認", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        postData();
                    }
                });

                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getApplicationContext(), "已取消購票", Toast.LENGTH_SHORT).show();
                    }
                });

                builder.show();
            }
        }else {
            Toast.makeText(getApplicationContext(), "購買數量不得為0", Toast.LENGTH_SHORT).show();
        }
    }



    public void postData(){
        String url = "";
        if (buyMode.equals("Online")){
            url = HOST + buyTicket + "/";
        }else {
            url = HOST + buyTempTicket + "/";
        }
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("請求結果:" ,response);
                ArrayList<JSONObject> jObjectArray = null;
                JSONObject jObject = new JSONObject();
                try {
                    jObjectArray = new getJson().parseJson(response);
                    jObject = jObjectArray.get(0);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (buyMode.equals("Offline")) {
                    Toast.makeText(getApplicationContext(), "票券預訂成功，請進行扣款結帳", Toast.LENGTH_LONG).show();

                    Intent i = new Intent(getApplicationContext(), InductionActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                    Bundle bundle = new Bundle();
                    bundle.putString("type", getApplicationContext().getString(R.string.induction_balance_withdraw));
                    i.putExtras(bundle);

                    startActivity(i);
                    finish();
                }else if(buyMode.equals("Online")){

                    try {
                        String ticketKey = jObject.getString("ticket_key");
                        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                        String sKey = prefs.getString("wallet_key", "");

                        if(sKey.length()>3){
                            sKey = ticketKey + "@KEY@" + sKey;
                        }else{
                            sKey =  ticketKey;
                        }

                        prefs.edit().putString("wallet_key", sKey).commit();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(getApplicationContext(), "票券購買成功", Toast.LENGTH_LONG).show();
                    Intent i = new Intent(getApplicationContext(), MainActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                    Bundle bundle = new Bundle();
                    bundle.putString("Activity", "Myself");
                    i.putExtras(bundle);
                    startActivity(i);
                    finish();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("請求錯誤:", error.toString());
                Toast.makeText(getApplicationContext(), "購票失敗", Toast.LENGTH_LONG).show();
                pageLinkData();
            }
        }) {

            @Override
            public byte[] getBody() throws AuthFailureError {
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                String user_email = prefs.getString("email", "");
                String buyTime = getPythonTime();
                String query = "{\"ticket_type_id\":"+ selectedId + ",\"number_of_people\":"+ selectedNumber +",\"user_email\":\""+ user_email + "\",\"buy_time\":\""+ buyTime + "\"} ";
                return query.getBytes();
            }

            @Override
            public String getBodyContentType() {
                return "application/json";
            }

        };

        RequestQueue rQueue = Volley.newRequestQueue(BuyActivity.this);
        rQueue.add(request);
    }

    private String getPythonTime() {
        return (new SimpleDateFormat("yyyy-MM-dd").format(new Date())) + "T" + (new SimpleDateFormat("HH:mm:ss").format(new Date())) + "Z";
    }

}
