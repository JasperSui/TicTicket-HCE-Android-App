package team.ten.buyticket.Fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import team.ten.buyticket.model.MyTicket;
import team.ten.buyticket.R;
import team.ten.buyticket.TicketDataAdapter;
import team.ten.buyticket.model.keyGroup;
import team.ten.buyticket.model.aConcert;
import team.ten.buyticket.function.getUrlData;


public class TicketFragment extends Fragment implements View.OnClickListener{

    private static final String TITLE_BALANCE = "wallet_balance";
    private static final String TITLE_KEY = "wallet_key";
    private static final String TITLE_USER = "email";
    private static final int DEFAULT_BALANCE = 0;

    static final String SEPARATOR_KEY = "@KEY@";
    private ImageView topImageView;

    public static TicketFragment newInstance() {
        TicketFragment fragment = new TicketFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ticket, container, false);

        //Card
        /*
        TextView cardNum = (TextView)view.findViewById(R.id.card_account_label);
        TextView balance = (TextView)view.findViewById(R.id.card_account_field);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this.getActivity());
        int account = prefs.getInt(TITLE_BALANCE, DEFAULT_BALANCE);
        String email = prefs.getString(TITLE_USER,getString(R.string.account_number));
        if(email.length() > 2){
            cardNum.setText(email);
        }else{
            cardNum.setText(getString(R.string.account_number));
        }

        balance.setText("NT."+account);*/
//        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
//        topImageView = (ImageView) view.findViewById(R.id.iv);
//        topImageView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                    String buyMode = prefs.getString("BuyMode", "Online");
//                    if (buyMode.equals("Online")) {
//                        prefs.edit().putString("BuyMode", "Offline").commit();
//                    }else{
//                        prefs.edit().putString("BuyMode", "Online").commit();
//                    }
//                    Toast.makeText(getContext(), prefs.getString("BuyMode", "Online"), Toast.LENGTH_SHORT).show();
//                }
//            });

        //Tickets
        RecyclerView recyclerView = (RecyclerView)view.findViewById(R.id.recycler_view);
        TextView nonTicket = (TextView)view.findViewById(R.id.ticket_nonTicket);

        ArrayList<keyGroup> KGs = transKeyGroup(this.getActivity());

        MyTicket ticketsData[]={};

        try{

            if(KGs.get(0).key_ID > 0){
                ticketsData = new MyTicket[KGs.size()];


                for(int i = 0; i < KGs.size(); i++){
                    if(KGs.get(i).key_ID > 0) {
                        getUrlData getHost = new getUrlData();
                        String tempStr = getHost.httpConnectionPost("https://jaspersui.pw/api/ticket_type/"+KGs.get(i).key_ID+"/?format=json");
                        aConcert concertByID = getHost.transConcerByString(tempStr,KGs.get(i).key_ID);

                        if(concertByID.cName != null){
                            ticketsData[i] = new MyTicket(concertByID.cId,concertByID.cName, KGs.get(i).key_KEY, concertByID.cUrl);
                        }else {
                            ticketsData[i] = new MyTicket(0,KGs.get(i).key_ID + "-" + KGs.get(i).key_KEY, "","");
                        }

                    }
                }
                nonTicket.setText("");

            }else{
                //目前無購買票券
                nonTicket.setText("目前無購買票券");
            }

        }catch (Exception e){


        }


        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        TicketDataAdapter mAdapter = new TicketDataAdapter(ticketsData,getContext());
        recyclerView.setAdapter(mAdapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        return view;
    }

    public static ArrayList<keyGroup> transKeyGroup(Context c){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(c);
        String sKey = prefs.getString(TITLE_KEY, "");


        String[] keyGroup_str = sKey.split(SEPARATOR_KEY);
        ArrayList<keyGroup> KGs = new ArrayList<keyGroup>();

        if(keyGroup_str.length > 0){
            for(int i = 0; i < keyGroup_str.length; i++){
                KGs.add(new keyGroup(keyGroup_str[i]));
            }

            return KGs;
        }else{
            return null;
        }

    }


    @Override
    public void onClick(View view) {
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        if(view.getId() == R.id.iv){
            String buyMode = prefs.getString("BuyMode", "Online");
            if (buyMode.equals("Online")) {
                prefs.edit().putString("BuyMode", "Offline").commit();
            }else{
                prefs.edit().putString("BuyMode", "Online").commit();
            }
            Toast.makeText(getContext(), prefs.getString("BuyMode", "Online"), Toast.LENGTH_SHORT).show();
        }
    }
}
