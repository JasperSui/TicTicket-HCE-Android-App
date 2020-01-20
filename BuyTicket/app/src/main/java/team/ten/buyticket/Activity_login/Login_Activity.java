package team.ten.buyticket.Activity_login;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import team.ten.buyticket.MainActivity;
import team.ten.buyticket.R;

public class Login_Activity extends AppCompatActivity implements View.OnClickListener {

    private EditText edit_account, edit_password;
    private TextView text_msg, text_register;
    private Button btn_login;
    private ImageButton openpwd;
    private boolean flag = false;
    private String account, password;
    public static final String HOST = "https://jaspersui.pw/api/";
    public static final String FORMAT = "/?format=json";
    public static final String Login = "user/login";
    public SharedPreferences settings;

//    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String email = prefs.getString("email", null);
        String password = prefs.getString("password", null);
        if (email != null && password != null){
            Intent intent = new Intent(Login_Activity.this, MainActivity.class);
            startActivity(intent);
            this.finish();
        }
        init();

    }


    private void init() {
        edit_account = (EditText) findViewById(R.id.edit_account);
        edit_account.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    edit_account.clearFocus();
                }
                return false;
            }
        });
        edit_password = (EditText) findViewById(R.id.edit_password);
        edit_password.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    edit_password.clearFocus();
                    InputMethodManager imm =
                            (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(edit_password.getWindowToken(), 0);
                }
                return false;
            }
        });
        text_msg = (TextView) findViewById(R.id.text_forget);
        btn_login = (Button) findViewById(R.id.btn_login);
        text_register = (TextView) findViewById(R.id.text_register);
//        openpwd = (ImageButton) findViewById(R.id.btn_openpwd);
        text_msg.setOnClickListener(this);
        btn_login.setOnClickListener(this);
        text_register.setOnClickListener(this);
//        openpwd.setOnClickListener(this);
//        dbHelper = new DBHelper(this, "Data.db", null, 1);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login:
                if (edit_account.getText().toString().trim().equals("") | edit_password.getText().
                        toString().trim().equals("")) {
                    Toast.makeText(this, "請輸入帳號或註冊帳號！", Toast.LENGTH_SHORT).show();
                } else {
                    postData();

                }
                break;
            case R.id.text_register:
                Intent intent = new Intent(Login_Activity.this, Register_Activity.class);
                startActivity(intent);
                break;
//            case R.id.btn_openpwd:
//                if (flag == true) {//不可见
//                    edit_password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
//                    flag = false;
//                    openpwd.setBackgroundResource(R.drawable.invisible);
//                } else {
//                    edit_password.setTransformationMethod(PasswordTransformationMethod.getInstance());
//                    flag = true;
//                    openpwd.setBackgroundResource(R.drawable.visible);
//                }
//                break;
            case R.id.text_forget:
                Intent i = new Intent(Login_Activity.this, ForgotInfo_Activity.class);
                startActivity(i);
                break;
        }
    }

    /**
     * 读取SharedPreferences存储的键值对
     * */

    /**
     * 读取UserData.db中的用户信息
     * */
//    protected void readUserInfo() {
//        if (login(edit_account.getText().toString(), edit_password.getText().toString())) {
//            Toast.makeText(this, "登入成功！", Toast.LENGTH_SHORT).show();
//            Intent intent = new Intent(Login_Activity.this, MyActivity.class);
//            intent.putExtra("Username",edit_account.getText().toString());
//            startActivity(intent);
//        } else {
//            Toast.makeText(this, "帳號或密碼錯誤，請重新输入！！", Toast.LENGTH_SHORT).show();
//        }
//    }
//
//    /**
//     * 验证登录信息
//     * */
//    public boolean login(String username, String password) {
//        SQLiteDatabase db = dbHelper.getWritableDatabase();
//        String sql = "Select * from usertable where username=? and password=?";
//        Cursor cursor = db.rawQuery(sql, new String[]{username, password});
//        if (cursor.moveToFirst()) {
//            cursor.close();
//            return true;
//        }
//        return false;
//    }
    public void postData(){
        String url = HOST + Login + "/";
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("请求结果:" ,response);
                Toast.makeText(getApplicationContext(), "登入成功", Toast.LENGTH_SHORT).show();
                /*Intent i = new Intent(getApplicationContext(),Login_Activity.class);
                startActivity(i);*/
                finish();

                //2019-04-21 Jasper
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                prefs.edit().putString("email", edit_account.getText().toString())
                            .putString("password", edit_password.getText().toString())
                            .apply();
                //*

                Intent intent = new Intent(Login_Activity.this, MainActivity.class);
                startActivity(intent);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("请求错误:", error.toString());
//                Log.d("d",dateText.getText().toString());
                Toast.makeText(getApplicationContext(), "登入失敗", Toast.LENGTH_SHORT).show();

                //Toast.makeText(getApplicationContext(), "註冊失敗", Toast.LENGTH_LONG).show();
            }
        })

        {
            @Override
            public byte[] getBody() throws AuthFailureError {
                // String date = dateText.getText().toString()+"T00:00:00Z";
//                String d = dateText.getText().toString()+ "T00:00:00Z";
                String query = "{\"email\":\""+  edit_account.getText().toString()  + "\",\n" +
                        "\"password\":\"" + edit_password.getText().toString() +"\"} ";
                return query.getBytes();
            }
//{
//    "email": "abc8787@gmail.com", //str
//    "password": "8787pwd", //str
//}
            @Override
            public String getBodyContentType() {
                return "application/json";
            }

        };

        RequestQueue rQueue = Volley.newRequestQueue(Login_Activity.this);
        rQueue.add(request);
    }
}
