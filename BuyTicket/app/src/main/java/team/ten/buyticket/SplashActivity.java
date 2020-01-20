package team.ten.buyticket;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import team.ten.buyticket.Activity_login.Login_Activity;

public class SplashActivity extends AppCompatActivity {

    private ImageView logo;
    private TextView copyright;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        logo = findViewById(R.id.splash_logo);
        copyright = findViewById(R.id.copyright);
        Animation myanim = AnimationUtils.loadAnimation(SplashActivity.this, R.anim.fadein);
        logo.startAnimation(myanim);
        copyright.startAnimation(myanim);



        final Intent aftersplash = new Intent(SplashActivity.this, Login_Activity.class);
        Thread timer = new Thread(){
            @Override
            public void run() {


                try{
                    sleep(800);
                }catch (InterruptedException e){
                    e.printStackTrace();
                }finally {
                    //check Network status
                    boolean isNetwork = isHasNetwork();

                    if(isNetwork == false){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                showErrow_NoNet();
                            }
                        });
                    }else {
                        startActivity(aftersplash);
                        finish();
                    }
                }
            }
        };
        timer.start();
    }

    public boolean isHasNetwork(){
        ConnectivityManager mConnectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();

        //如果未連線的話，mNetworkInfo會等於null
        if(mNetworkInfo != null){
            return true;
        }

        return false;
    }

    public void showErrow_NoNet(){
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
        builder.setTitle("缺乏網路連線")//設定視窗標題
                .setIcon(R.drawable.ic_warning_black_24dp)//設定對話視窗圖示
                .setMessage("未偵測到網路連線\n請再次確認手機連網狀態")
                .setCancelable(false)
                .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                        finish();
                    }
                });
        android.support.v7.app.AlertDialog alert = builder.create();
        alert.show();
    }
}
