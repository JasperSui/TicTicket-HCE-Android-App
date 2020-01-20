package team.ten.buyticket.model;
//演唱會資訊

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;



public class aConcert {

    public int cId;
    public String cName;
    public String cPrice;
    public String cUrl;
    public String cContent;
    public String cTime_start_str;
    public String cTime_end_str;
    public Date cTime_start_date;
    public Date cTime_end_date;
    public String cSeat_number;
    public String cSeat_remain;


    public aConcert(JSONObject jdata) throws JSONException{
        cId = jdata.getInt("id");
        cName = jdata.getString("name");
        cPrice = jdata.getString("price");
        cUrl = jdata.getString("image_url");
        cContent = jdata.getString("content");

        String strData_st = jdata.getString("begin_time").replace("T", " ").replace("Z", "");
        String strData_en = jdata.getString("end_time").replace("T", " ").replace("Z", "");

        try {
            SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            DateFormat dateFormat2 = new SimpleDateFormat("yyyy/MM/dd HH:mm");

            cTime_start_date = sdf2.parse(strData_st);
            cTime_end_date = sdf2.parse(strData_en);

            cTime_start_str = dateFormat2.format(cTime_start_date);
            cTime_end_str = dateFormat2.format(cTime_end_date);
        } catch (Exception e) {
            e.printStackTrace();
        }

        cSeat_number = jdata.getString("number_of_seat");
        cSeat_remain = jdata.getString("remaining_seat");
    }

}