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

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.ViewPager;
import android.util.Log;

import java.util.ArrayList;

import team.ten.buyticket.MainActivity;
import team.ten.buyticket.model.keyGroup;

public class AccountStorage {

    private static final String TAG = "AccountStorage";

    private static final String TITLE_BALANCE = "wallet_balance";
    private static final String TITLE_KEY = "wallet_key";
    private static final String TITLE_USER = "email";
    private static final int DEFAULT_BALANCE = 0;
    static final String SEPARATOR_KEY = "@KEY@";
    private static int sBalance = 0;
    private static String sKey = "";
    private static final Object sAccountLock = new Object();
    private static final Object sBalanceLock = new Object();


    public static int GetBalance(Context c) {
        synchronized (sBalanceLock) {
            if (sBalance == 0) {
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(c);
                int account = prefs.getInt(TITLE_BALANCE, DEFAULT_BALANCE);
                sBalance = account;
            }
            return sBalance;
        }
    }

    public static void decreaseBalance(Context c, int s) {
        synchronized(sBalanceLock) {
            Log.i(TAG, "Setting account balance decrease: " + s);
//            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(c);
            sBalance -= s;
//            prefs.edit().putInt(TITLE_BALANCE, sBalance).commit();

            goBackTicket(c);
        }
    }

    public static void increaseBalance(Context c, int s) {
        synchronized(sBalanceLock) {
            Log.i(TAG, "Setting account balance decrease: " + s);
//            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(c);
            sBalance += s;
//            prefs.edit().putInt(TITLE_BALANCE, sBalance).commit();

            goBackMyself(c);
        }
    }

    //============================== KEY ==============================
    public static ArrayList<keyGroup> transKeyGroup(Context c){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(c);
        sKey = prefs.getString(TITLE_KEY, "");


        String[] keyGroup_str = sKey.split(SEPARATOR_KEY);
        ArrayList<keyGroup> KGs = new ArrayList<keyGroup>();

        for(int i = 0; i < keyGroup_str.length; i++){
            KGs.add(new keyGroup(keyGroup_str[i]));
        }

        return KGs;
    }

    public static void insertKey(Context c,String key){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(c);
        sKey = prefs.getString(TITLE_KEY, "");

        if(sKey.length()>3){
            sKey = key + SEPARATOR_KEY + sKey;
        }else{
            sKey =  key;
        }

        prefs.edit().putString(TITLE_KEY, sKey).commit();

        goBackTicket(c);
    }


    public static String takeoutKey_byID(Context c,int id){
        ArrayList<keyGroup> KGs = transKeyGroup(c);
        String theKey = "";

        for(int i = 0; i < KGs.size(); i++){
            if(KGs.get(i).key_ID == id && KGs.get(i).key_KEY.length() == 20){
                theKey = KGs.get(i).key_KEY;
                KGs.get(i).key_KEY = "!"+KGs.get(i).key_KEY;
                break;
            }
        }

        sKey = "";
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(c);
        prefs.edit().putString(TITLE_KEY, "").commit();

        //先加入已使用的(讓它在後面)
        for(int i = (KGs.size() -1) ; i >= 0 ; i--){
            if(KGs.get(i).key_KEY.length() == 21){
                insertKey(c,KGs.get(i).key_KEY);
            }
        }

        //後加入未使用的(讓它在前面)
        for(int i = (KGs.size() -1) ; i >= 0 ; i--){
            if(KGs.get(i).key_KEY.length() == 20){
                insertKey(c,KGs.get(i).key_KEY);
            }
        }


        goBackTicket(c);

        return theKey;
    }

    public static void getAllKey(Context c){
        ArrayList<keyGroup> KGs = transKeyGroup(c);

        for(int i = 0; i < KGs.size(); i++){
            Log.i("==>  ", "[ SHOW KEY ]" + KGs.get(i).key_ID + "," + KGs.get(i).key_KEY);
        }
    }

    public static void goBackTicket(Context c){
        Intent intent = new Intent();
        intent.setClass(c, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        Bundle bundle = new Bundle();
        bundle.putString("activity", "Ticket");
        intent.putExtras(bundle);
        c.startActivity(intent);
    }

    public static void goBackMyself(Context c){
        Intent intent = new Intent();
        intent.setClass(c, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        Bundle bundle = new Bundle();
        bundle.putString("activity", "Myself");
        intent.putExtras(bundle);
        c.startActivity(intent);
    }


    //============================== User ==============================
    public static String getUser(Context c){
        String strEmail = "";
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(c);
        strEmail = prefs.getString(TITLE_USER, "");

        return strEmail;
    }

}
