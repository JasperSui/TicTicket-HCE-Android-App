package team.ten.buyticket.function;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class getJson {

    public ArrayList<JSONObject> parseJson(String str) throws JSONException{
        JSONArray jArray;
        try{
            jArray= new JSONArray(str);
        }catch(Exception e){
            jArray = new JSONArray("[" + str + "]");
        }

        ArrayList<JSONObject> list = new ArrayList<JSONObject>();
        for(int i = 0; i < jArray.length(); i ++){
            list.add(jArray.getJSONObject(i));
        }

        // JSONObject[] jObject = new JSONObject(jArray);
        //return jObject;

        return list;
    }
}