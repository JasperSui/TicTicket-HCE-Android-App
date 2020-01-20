package team.ten.buyticket.Fragment;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import team.ten.buyticket.model.ConcertItem;
import team.ten.buyticket.MyAdapter;
import team.ten.buyticket.R;
import team.ten.buyticket.function.MyStringRequest;
import team.ten.buyticket.model.aConcert;
import team.ten.buyticket.function.getJson;


public class HomeFragment extends Fragment {


    //API SET
    public static final String HOST = "https://jaspersui.pw/api/";
    public static final String FORMAT = "/?format=json";
    public static final String getTicket = "ticket_type";
    ProgressDialog dialog;

    private SwipeRefreshLayout mSwipeLayout ;

    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_home, container, false);

        //API空間

        String url = HOST + getTicket + FORMAT;
        dialog = new ProgressDialog(getContext());
        dialog.setMessage("Loading....");
        dialog.show();

        //===================================== 抓取資料 =====================================
        final MyStringRequest request = new MyStringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                //Log.d("Testing Message-GetURL", s);

                ArrayList<JSONObject> jObject = null;
                try {
                    jObject = new getJson().parseJson(s);
                    final ArrayList<aConcert> concerts = getConcertInfo(jObject);

                    ConcertItem itemsData[] = new ConcertItem[concerts.size()];

                    for(int i =0 ; i<concerts.size(); i ++) {
                        itemsData[i] = new ConcertItem(concerts.get(i).cName, concerts.get(i).cUrl,concerts.get(i).cId,Integer.parseInt(concerts.get(i).cPrice), concerts.get(i).cContent);
                        //Picasso.get().load(concerts.get(i).cUrl).into(imageView);
                    }

                    // put into RecyclerView
                    RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.home_rv);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                    MyAdapter mAdapter = new MyAdapter(itemsData, getActivity());
                    recyclerView.setAdapter(mAdapter);
                    recyclerView.setItemAnimator(new DefaultItemAnimator());


                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

            }
        });

        RequestQueue rQueue = Volley.newRequestQueue(getContext());
        rQueue.add(request);

        dialog.dismiss();


        mSwipeLayout = (SwipeRefreshLayout) view.findViewById(R.id.refresh_layout);
        mSwipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSwipeLayout.setRefreshing(true);
                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        mSwipeLayout.setRefreshing(false);
                        Toast.makeText(getContext(), "Refresh done!", Toast.LENGTH_SHORT).show();
                    }
                }, 3000);
            }
        });




        return view;

    }

    public ArrayList<aConcert> getConcertInfo(ArrayList<JSONObject> jObject) throws JSONException{
        ArrayList<aConcert> tempData = new ArrayList<aConcert>();

        for(int i = 0; i < jObject.size(); i++){
            tempData.add(new aConcert(jObject.get(i)));
        }

        return tempData;
    }

}