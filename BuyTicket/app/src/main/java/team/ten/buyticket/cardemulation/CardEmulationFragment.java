/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package team.ten.buyticket.cardemulation;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import team.ten.buyticket.Activity_login.Login_Activity;
import team.ten.buyticket.CreditCardActivity;
import team.ten.buyticket.InductionActivity;
import team.ten.buyticket.R;

public class CardEmulationFragment extends Fragment {




    public static final String TAG = "CardEmulationFragment";
    public static EditText deposit_money;
    public TextView balancetx;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.layout_card, container, false);

        balancetx = (TextView)v.findViewById(R.id.card_account_field);
        Button button = (Button) v.findViewById(R.id.cardButton);
        button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(final View v)
            {
                new AlertDialog.Builder(v.getContext())
                        .setTitle("餘額操作")
                        .setMessage("請選擇您要針對卡片餘額進行的動作。")
                        .setPositiveButton("加值", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                Dialog();


                            }
                        })
                        .setNegativeButton("結帳", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(getContext(), InductionActivity.class);

                                Bundle bundle = new Bundle();
                                bundle.putString("type", getContext().getString(R.string.induction_balance_withdraw));
                                intent.putExtras(bundle);

                                getContext().startActivity(intent);
                            }
                        })
                        .setNeutralButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(getContext(), "您已取消操作", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .show();
            }
        });
        return v;
    }

            public void Dialog() {

                final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
                dialogBuilder.setTitle("加值方式")
                .setMessage("請選擇您要針對卡片餘額進行的動作。")

                .setPositiveButton("感應加值", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Intent intent = new Intent(getContext(), InductionActivity.class);

                        Bundle bundle = new Bundle();
                        bundle.putString("type", getContext().getString(R.string.induction_balance_deposit));
                        intent.putExtras(bundle);

                        getContext().startActivity(intent);

                    }
                });

                dialogBuilder.setNegativeButton("線上加值", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        onDepositDialog();
                    }
                }).show();
            }

            public void onDepositDialog () {

               // final EditText deposit_money= (EditText) getView().findViewById(R.id.deposit_money);

                final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                LayoutInflater inflater = getActivity().getLayoutInflater();
                final View view = inflater.inflate(R.layout.layout_deposit,null);
                deposit_money =(EditText) view.findViewById(R.id.deposit_money);
                builder.setView(view)
                        .setTitle("信用卡加值")
                        .setIcon(R.drawable.ic_money_black_24dp)
                        .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
//                                Toast.makeText(getContext(), deposit_money.getText().toString(), Toast.LENGTH_SHORT).show();

                                int amount = 0;
                                try{
                                    amount = Integer.parseInt(deposit_money.getText().toString());
                                }catch (Exception e){
                                    amount = 0;
                                }

                                if(amount > 0){
                                    Intent intent = new Intent(getContext(), CreditCardActivity.class);

                                    Bundle bundle = new Bundle();
                                    bundle.putInt("deposit_amount", amount);
                                    intent.putExtras(bundle);

                                    getContext().startActivity(intent);

                                }else{

                                    android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getContext());
                                    builder.setTitle("加值失敗")//設定視窗標題
                                            .setIcon(R.drawable.ic_warning_black_24dp)//設定對話視窗圖示
                                            .setMessage("必須輸入大於零的整數！")
                                            .setCancelable(false)
                                            .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                    Toast.makeText(getContext(), "您已取消操作", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                    android.support.v7.app.AlertDialog alert = builder.create();
                                    alert.show();
                                }
                            }

                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Toast.makeText(getContext(), "您已取消操作", Toast.LENGTH_SHORT).show();
                            }
                        }).show();
            }






}
