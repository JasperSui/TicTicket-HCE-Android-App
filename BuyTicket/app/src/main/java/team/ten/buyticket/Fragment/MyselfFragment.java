package team.ten.buyticket.Fragment;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import team.ten.buyticket.Activity_login.Login_Activity;
import team.ten.buyticket.R;

public class MyselfFragment extends Fragment {

    public static final String HOST = "https://jaspersui.pw/api/";
    public static final String FORMAT = "?format=json";
    public static final String GETINFO = "user/get-user-info-by-email";
    public static final String CHANGEPASSWORD = "user/change_password";
    public static final String EDITPROFILE = "user/edit_profile";

    public TextView personTextView,emailTextView,birthdayTextView,phoneTextView,addressTextView,modeTextView,versionTextView;
    public LinearLayout birthdayLayout,phoneLayout,addressLayout,changeProfileLayout,changePasswordLayout,changeModeLayout;

    public Button btn_logout;
    public TextView balance;

    private static final String TITLE_BALANCE = "wallet_balance";
    private static final String TITLE_KEY = "wallet_key";
    private static final String TITLE_USER = "email";
    private static final int DEFAULT_BALANCE = 0;

    static final String SEPARATOR_KEY = "@KEY@";
    ProgressDialog dialog;


    public void turnOnDialog(){
        dialog.show();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        dialog = new ProgressDialog(getContext());
        dialog.setMessage("Loading....");
        dialog.show();


        View view = inflater.inflate(R.layout.fragment_myself,container,false);
        linkComponent(view);

        linkListener();

        postData();
        loadData();


        //CARD
        TextView cardNum = (TextView)view.findViewById(R.id.card_account_label);
        balance = (TextView)view.findViewById(R.id.card_account_field);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this.getActivity());
        int account = prefs.getInt(TITLE_BALANCE, DEFAULT_BALANCE);
        String email = prefs.getString(TITLE_USER,getString(R.string.account_number));
        if(email.length() > 2){
            cardNum.setText(email);
        }else{
            cardNum.setText(getString(R.string.account_number));
        }

        return  view;
    }


    public void linkComponent(View view){
        personTextView = (TextView) view.findViewById(R.id.profile_Name);
        emailTextView = (TextView) view.findViewById(R.id.email);
        phoneTextView = (TextView) view.findViewById(R.id.profile_phone);
        addressTextView = (TextView) view.findViewById(R.id.profile_address);
        birthdayTextView = (TextView) view.findViewById(R.id.birthday);
        modeTextView = (TextView) view.findViewById(R.id.lb_profile_change_mode_value);
        versionTextView  = (TextView) view.findViewById(R.id.lb_profile_change_version_value);

        birthdayLayout = (LinearLayout)view.findViewById(R.id.myself_layout_birthday);
        phoneLayout = (LinearLayout)view.findViewById(R.id.myself_layout_phone);
        addressLayout = (LinearLayout)view.findViewById(R.id.myself_layout_address);
        changeProfileLayout = (LinearLayout)view.findViewById(R.id.myself_layout_setting_profile);
        changePasswordLayout = (LinearLayout)view.findViewById(R.id.myself_layout_setting_password);
        changeModeLayout = (LinearLayout)view.findViewById(R.id.myself_layout_setting_mode);


        btn_logout = view.findViewById(R.id.btn_logout);
    }

    public void linkListener(){
        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setMessage("您確定要登出嗎？")
                        .setCancelable(false)
                        .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getContext());
                                pref.edit().putString("email", null)
                                        .putString("password", null)
                                        .apply();

                                Intent intent = new Intent(getContext(), Login_Activity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                getActivity().finish();
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                Toast.makeText(getContext(), "取消登出", Toast.LENGTH_SHORT).show();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });

        changeProfileLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeProfile();
                //Toast.makeText(v.getContext(), "變更資料尚未完成", Toast.LENGTH_SHORT).show();
            }
        });

        changePasswordLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changePassword();
//                Toast.makeText(v.getContext(), "變更密碼尚未完成", Toast.LENGTH_SHORT).show();
            }
        });


        changeModeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] listItems = {"在線模式(推薦)", "離線模式"};

                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setTitle("模式變更");

                SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getContext());
                String buyMode = pref.getString("BuyMode", "Online");

                int checkedItem = 1; //this will checked the item when user open the dialog

                if(buyMode.equals("Online")){
                    checkedItem = 0; //this will checked the item when user open the dialog
                }


                builder.setSingleChoiceItems(listItems, checkedItem, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getContext());

                        if (which == 1) {
                            pref.edit().putString("changeMode", "Offline").commit();
                        }else{
                            pref.edit().putString("changeMode", "Online").commit();
                        }

                    }
                });

                builder.setPositiveButton("設定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();


                        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getContext());
                        String newMode = pref.getString("changeMode","");
                        if(newMode.length() > 0){
                            pref.edit().putString("BuyMode", newMode).commit();
                            pref.edit().putString("changeMode", null).commit();
                        }


                        String buyMode = pref.getString("BuyMode", "Online");
                        if(buyMode.equals("Online")){
                            modeTextView.setText("在線模式 >");
                        }else{
                            modeTextView.setText("離線模式 >");
                        }
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();

            }
        });


    }


    public void loadData(){
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getContext());

        String buyMode = pref.getString("BuyMode", "Online");
        if(buyMode.equals("Online")){
            modeTextView.setText("在線模式 >");
        }else{
            modeTextView.setText("離線模式 >");
        }

        try{
            PackageManager packageManager=getContext().getPackageManager();
            PackageInfo packageInfo=packageManager.getPackageInfo(getContext().getPackageName(),0);
            versionTextView.setText("Version "+packageInfo.versionName);
        }catch (Exception e){
            versionTextView.setText("Version 00000000.00");
        }

    }

    public static MyselfFragment newInstance() {
        MyselfFragment fragment = new MyselfFragment();
        return fragment;
    }

    public void changeProfile(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View view = inflater.inflate(R.layout.layout_change_profile,null);
        final TextView profile_email = (TextView)view.findViewById(R.id.change_profile_email);
        final EditText profile_name =(EditText) view.findViewById(R.id.change_profile_name);
        final TextView profile_birthday = (TextView)view.findViewById(R.id.change_profile_birthday);
        final EditText profile_phone =(EditText) view.findViewById(R.id.change_profile_phone);
        final EditText profile_address =(EditText) view.findViewById(R.id.change_profile_address);

        builder.setView(view)
                .setTitle("資料變更")
                .setIcon(R.drawable.ic_info_gray_24dp)
                .setPositiveButton("確定",null)
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Toast.makeText(getContext(), "您已取消操作", Toast.LENGTH_SHORT).show();
                    }
                });

        final AlertDialog mAlertDialog = builder.create();
        mAlertDialog.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(DialogInterface dialog) {

                getData(new VolleyCallback(){
                    @Override
                    public void onSuccess(ArrayList<String> dataArrayList) {
                        profile_email.setText(dataArrayList.get(0));
                        profile_name.setText(dataArrayList.get(1));
                        profile_birthday.setText(dataArrayList.get(2));
                        profile_address.setText(dataArrayList.get(3));
                        profile_phone.setText(dataArrayList.get(4));
                    }
                });


                Button b = mAlertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                b.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        String name = profile_name.getText().toString();
                        String phone = profile_phone.getText().toString();
                        String address = profile_address.getText().toString();
                        postEditProfile(name, address, phone);
                        mAlertDialog.dismiss();
                    }
                });

            }
        });

        mAlertDialog.show();

    }

    public void changePassword(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View view = inflater.inflate(R.layout.layout_change_password,null);
        final EditText pw_ori =(EditText) view.findViewById(R.id.ChangePassword_oriPW);
        final EditText pw_new =(EditText) view.findViewById(R.id.ChangePassword_newPW);
        final EditText pw_check =(EditText) view.findViewById(R.id.ChangePassword_checkPW);

        builder.setView(view)
                .setTitle("密碼變更")
                .setIcon(R.drawable.ic_security_black_24dp)
                .setPositiveButton("確定",null)
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Toast.makeText(getContext(), "您已取消操作", Toast.LENGTH_SHORT).show();
                    }
                });

        final AlertDialog mAlertDialog = builder.create();
        mAlertDialog.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(DialogInterface dialog) {

                Button b = mAlertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                b.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
                        String OriPW = prefs.getString("password","");
                        String str_ori = pw_ori.getText().toString();
                        String str_new = pw_new.getText().toString();
                        String str_check = pw_check.getText().toString();

                        if(str_new.length() < 6){
                            Toast.makeText(getContext(), "密碼長度需超過六位", Toast.LENGTH_SHORT).show();
                        }else if(!str_new.equals(str_check)){
                            Toast.makeText(getContext(), "新密碼確認不相符", Toast.LENGTH_SHORT).show();
                        }else if(!OriPW.equals(str_ori)){
                            Toast.makeText(getContext(), "原密碼驗證失敗", Toast.LENGTH_SHORT).show();
                        }else{
                            //這裡要變更密碼
                            String password = pw_new.getText().toString();
                            postChangePassword(password);
                            mAlertDialog.dismiss();

                            //logout
                            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getContext());
                            pref.edit().putString("email", null)
                                    .putString("password", null)
                                    .apply();

                            Intent intent = new Intent(getContext(), Login_Activity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            getActivity().finish();
                        }
                    }
                });
            }
        });

        mAlertDialog.show();
    }

    public void postData(){
        String url = HOST + GETINFO + "/";
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                dialog.show();
                Log.d("请求结果:" ,response);
                JSONObject responseJSON = null;
                try {
                    responseJSON = new JSONObject(response);
                    personTextView.setText(responseJSON.getString("name"));
                    emailTextView.setText(responseJSON.getString("email"));

                    if(responseJSON.getString("birthday").length() >= 1){
                        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
                        DateFormat dateFormat2 = new SimpleDateFormat("yyyy/MM/dd");
                        String birthday = responseJSON.getString("birthday").replace("T", " ").replace("Z", "");
                        Date birthdayDate = sdf2.parse(birthday);
                        String birthdayStr = dateFormat2.format(birthdayDate);


                        //calculate age
                        Date date = new Date();
                        String todayDate= sdf2.format(date);

                        Date firstDate = sdf2.parse(birthday);
                        Date secondDate = sdf2.parse(todayDate);

                        long diffInMillies = Math.abs(secondDate.getTime() - firstDate.getTime());
                        long diff = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
                        int diffInYear = (int)(diff / 365.25);


                        birthdayTextView.setText(birthdayStr + " ("+String.valueOf(diffInYear)+"歲)");
                    }else{
                        birthdayLayout.setVisibility(View.GONE);
                    }


                    if(responseJSON.getString("telephone").length() >= 1){
                        phoneTextView.setText(responseJSON.getString("telephone"));
                    }else{
                        phoneLayout.setVisibility(View.GONE);
                    }

                    if(responseJSON.getString("address").length() >= 1){
                        addressTextView.setText(responseJSON.getString("address"));
                    }else{
                        addressLayout.setVisibility(View.GONE);
                    }



                    int money = responseJSON.getInt("money");
                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
                    prefs.edit().putInt(TITLE_BALANCE, money);
                    balance.setText("$ " + String.valueOf(money));

                    dialog.dismiss();

                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (ParseException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("請求錯誤:", error.toString());
                Toast.makeText(getContext(), "登入失敗", Toast.LENGTH_SHORT).show();
            }
        })

        {
            @Override
            public byte[] getBody() throws AuthFailureError {
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
                String email = prefs.getString("email", "");
                String query = "{\"email\": \""+email+"\"}";
                return query.getBytes();
            }

            @Override
            public String getBodyContentType() {
                return "application/json";
            }

        };

        RequestQueue rQueue = Volley.newRequestQueue(getActivity().getApplicationContext());
        rQueue.add(request);
    }

    public void getData(final VolleyCallback callback){
        String url = HOST + GETINFO + "/";
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("请求结果:" ,response);
                JSONObject responseJSON = null;
                try {
                    ArrayList<String> strData = new ArrayList<String>();
                    responseJSON = new JSONObject(response);

                    SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
                    DateFormat dateFormat2 = new SimpleDateFormat("yyyy/MM/dd");
                    String birthday = responseJSON.getString("birthday").replace("T", " ").replace("Z", "");
                    Date birthdayDate = sdf2.parse(birthday);
                    String birthdayStr = dateFormat2.format(birthdayDate);

                    strData.add(responseJSON.getString("email"));
                    strData.add(responseJSON.getString("name"));
                    strData.add(birthdayStr);
                    strData.add(responseJSON.getString("address"));
                    strData.add(responseJSON.getString("telephone"));

                    callback.onSuccess(strData);


                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("請求錯誤:", error.toString());
                Toast.makeText(getContext(), "無法取得線上資料", Toast.LENGTH_SHORT).show();
            }
        })

        {
            @Override
            public byte[] getBody() throws AuthFailureError {
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
                String email = prefs.getString("email", "");
                String query = "{\"email\": \""+email+"\"}";
                return query.getBytes();
            }

            @Override
            public String getBodyContentType() {
                return "application/json";
            }

        };

        RequestQueue rQueue = Volley.newRequestQueue(getContext());
        rQueue.add(request);
    }

    public void postEditProfile(final String name, final String address, final String telephone){
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View view = inflater.inflate(R.layout.layout_change_profile,null);
        String url = HOST + EDITPROFILE + "/";
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("请求结果:" ,response);
                JSONObject responseJSON = null;
                try {
                    ArrayList<String> strData = new ArrayList<String>();
                    responseJSON = new JSONObject(response);
                    Toast.makeText(getContext(), "個人資料變更成功", Toast.LENGTH_LONG).show();

                    turnOnDialog();
                    postData();

                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("請求錯誤:", error.toString());
//                Toast.makeText(getContext(), "登入失敗", Toast.LENGTH_SHORT).show();
            }
        })

        {
            @Override
            public byte[] getBody() throws AuthFailureError {
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
                String email = prefs.getString("email", "");
                String query = "{\"user_email\": \""+email+ "\",\"name\":\""+ name+ "\",\"address\":\""+ address + "\",\"telephone\":\""+ telephone +"\"} ";
                Log.d("POST BODY", query);
                return query.getBytes();
            }

            @Override
            public String getBodyContentType() {
                return "application/json";
            }

        };

        RequestQueue rQueue = Volley.newRequestQueue(getContext());
        rQueue.add(request);
    }

    public void postChangePassword(final String password){
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View view = inflater.inflate(R.layout.layout_change_password,null);
        String url = HOST + CHANGEPASSWORD + "/";
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("请求结果:" ,response);
                JSONObject responseJSON = null;
                try {
                    ArrayList<String> strData = new ArrayList<String>();
                    responseJSON = new JSONObject(response);
                    Toast.makeText(getContext(), "密碼變更成功", Toast.LENGTH_LONG).show();

                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("請求錯誤:", error.toString());
                Toast.makeText(getContext(), "登入失敗", Toast.LENGTH_SHORT).show();
            }
        })

        {
            @Override
            public byte[] getBody() throws AuthFailureError {
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
                String email = prefs.getString("email", "");
                String query = "{\"user_email\": \""+email+ "\",\"password\":\""+ password + "\"} ";
                Log.d("POST BODY", query);
                return query.getBytes();

            }

            @Override
            public String getBodyContentType() {
                return "application/json";
            }

        };

        RequestQueue rQueue = Volley.newRequestQueue(getContext());
        rQueue.add(request);
    }

    public interface VolleyCallback{
        void onSuccess(ArrayList<String> dataArrayList);
    }
}
