package team.ten.buyticket.Activity_login;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
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

import java.util.Calendar;
import team.ten.buyticket.R;



public class Register_Activity extends Activity implements View.OnClickListener {


    private EditText edit_register, edit_setpassword, edit_name,edit_resetpassword, select_birthday;
    private Button btn_yes;
//    TextView dateText ;

    String query ;

    public static final String HOST = "https://jaspersui.pw/api/";
    public static final String FORMAT = "/?format=json";

    public static final String getUser = "user";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        init();
//        dbHelper = new DBHelper(this, "Data.db", null, 1);
    }

    public void init() {
        edit_register = (EditText) findViewById(R.id.edit_register);

//        edit_register.setFilters(new InputFilter[]{
//                new InputFilter() {
//                    @Override
//                    public CharSequence filter(CharSequence source, int start, int end,
//                                               Spanned dest, int dstart, int dend) {
//                        for (int i = start; i < end; i++) {
//                            if (!Character.isLetterOrDigit(source.charAt(i)) &&
//                                    !Character.toString(source.charAt(i)).equals("_")) {
//                                Toast.makeText(Register_Activity.this, "只能使用'_'、字母、数字、汉字注册！", Toast.LENGTH_SHORT).show();
//                                return "";
//                            }
//                        }
//                        return null;
//                    }
//                }
//        });
        edit_register.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    edit_register.clearFocus();
                    InputMethodManager imm =
                            (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(edit_register.getWindowToken(), 0);
                }
                return false;
            }
        });
        edit_setpassword = (EditText) findViewById(R.id.edit_setpassword);
        edit_setpassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    String s = v.getText().toString();
                    //设置密码长度有问题，判断editText的输入长度需要重新理解
                    System.out.println(" v: ****** v :"+ s.length());
                    if (s.length() >= 6) {
                        System.out.println(" ****** s :"+ s.length());
                        edit_setpassword.clearFocus();
                        InputMethodManager imm =
                                (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(edit_setpassword.getWindowToken(), 0);
                    } else {
                        Toast.makeText(Register_Activity.this, "密码设置最少为6位！", Toast.LENGTH_SHORT).show();
                    }
                }
                return false;
            }
        });

//        -----------------------姓名-------------------------------------
        edit_name = (EditText) findViewById(R.id.edit_name);
        edit_name.setFilters(new InputFilter[]{
            new InputFilter() {
                @Override
                public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                    for (int i = start; i < end; i++) {
                        if (!Character.isLetterOrDigit(source.charAt(i)) &&
                                !Character.toString(source.charAt(i)).equals("_")) {
                            Toast.makeText(Register_Activity.this, "只能使用'_'、英文、數字、中文註冊！", Toast.LENGTH_SHORT).show();
                            return "";
                        }
                    }
                    return null;
                }
            }
        });
        edit_register.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    edit_register.clearFocus();
                    InputMethodManager imm =
                            (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(edit_register.getWindowToken(), 0);
                }
                return false;
            }
        });


//--------------------------出生日期選單-------------------------------------------------------------
//        final TextView dateText = (TextView)findViewById(R.id.select_birthday);
        //Button dateButton = (Button)findViewById(R.id.btn_selectbirthday);
       // final TextView
        select_birthday = (EditText) findViewById(R.id.select_birthday);
        select_birthday.setKeyListener(null);
        select_birthday.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                int mYear, mMonth, mDay;

                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);
                new DatePickerDialog(Register_Activity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int day) {
                        select_birthday.setText(setDateFormat(year,month,day));
                    }

                }, mYear,mMonth, mDay).show();

            }

        });

        btn_yes = (Button) findViewById(R.id.btn_yes);
        btn_yes.setOnClickListener(this);
//        btn_cancel = (Button) findViewById(R.id.btn_cancle);
//        btn_cancel.setOnClickListener(this);
    }
//    -------------------------出生日期轉換成字串--------------------------------------------
    private String setDateFormat(int year,int monthOfYear,int dayOfMonth){
        return String.valueOf(year) + "-"
                + String.valueOf(monthOfYear +1) + "-"
                + String.valueOf(dayOfMonth);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_yes:
                postData();

                break;
//            case R.id.btn_cancle:
//                Intent login_intent = new Intent(Register_Activity.this, Login_Activity.class);
//                startActivity(login_intent);
//                break;
            default:
                break;
        }
    }


    /**
     * 利用SharedPreferences进行默认登陆设置
     */
//    private void saveUsersInfo() {
//        SharedPreferences sharedPreferences = getSharedPreferences("UsersInfo", MODE_APPEND);
//        SharedPreferences.Editor editor = sharedPreferences.edit();
//        editor.putString("username", edit_register.getText().toString());
//        //判断注册时的两次密码是否相同
//        if (edit_setpassword.getText().toString().equals(edit_resetpassword.getText().toString())) {
//            editor.putString("passw*ord", edit_setpassword.getText().toString());
//        }
//        editor.commit();
//    }

    /**
     * 利用sql创建嵌入式数据库进行注册访问
     */
//    private void registerUserInfo(String username, String userpassword) {
//        SQLiteDatabase db = dbHelper.getWritableDatabase();
//        ContentValues values = new ContentValues();
//        values.put("username", username);
//        values.put("password", userpassword);
//        db.insert("usertable", null, values);
//        db.close();
//    }

    /**
     * 检验用户名是否已经注册
     */
//    public boolean CheckIsDataAlreadyInDBorNot(String value) {
//        SQLiteDatabase db = dbHelper.getWritableDatabase();
//        String Query = "Select * from usertable where username =?";
//        Cursor cursor = db.rawQuery(Query, new String[]{value});
//        if (cursor.getCount() > 0) {cursor.close();
//            return true;
//        }
//        cursor.close();
//        return false;
//    }
    public void postData(){
        String url = HOST + getUser + "/";
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("请求结果:" ,response);
                Toast.makeText(getApplicationContext(), "註冊成功", Toast.LENGTH_SHORT).show();
                /*Intent i = new Intent(getApplicationContext(),Login_Activity.class);
                startActivity(i);*/
                finish();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("请求错误:", error.toString());
//                Log.d("d",dateText.getText().toString());
                Toast.makeText(getApplicationContext(), "註冊失敗", Toast.LENGTH_SHORT).show();

                //Toast.makeText(getApplicationContext(), "註冊失敗", Toast.LENGTH_LONG).show();
            }
        })

        {
            @Override
            public byte[] getBody() throws AuthFailureError {
               // String date = dateText.getText().toString()+"T00:00:00Z";
                String d = select_birthday.getText().toString()+ "T00:00:00Z";
                query = "{\"email\":\""+  edit_register.getText().toString()  + "\",\n" +
                        "\"password\":\"" + edit_setpassword.getText().toString() +"\",\n" +
                        "\"name\": \"" + edit_name.getText().toString() + "\" ,\n" +
                        "\"birthday\":\""+ d +"\"} ";
                return query.getBytes();
            }

            @Override
            public String getBodyContentType() {
                return "application/json";
            }

        };

        RequestQueue rQueue = Volley.newRequestQueue(Register_Activity.this);
        rQueue.add(request);
    }
}
