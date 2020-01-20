package team.ten.buyticket.function;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import team.ten.buyticket.function.getJson;
import team.ten.buyticket.model.aConcert;

import static team.ten.buyticket.BuyActivity.getTheConcert;

public class getUrlData {

    public static String httpConnectionPost(String apiUrl) {
        URL url = null;
        BufferedReader reader = null;
        StringBuilder stringBuilder;

        try
        {
            // create the HttpURLConnection
            url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // 使用甚麼方法做連線
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Connection", "close");

            // 是否添加參數(ex : json...等)
            //connection.setDoOutput(true);

            // 設定TimeOut時間
            connection.setReadTimeout(15*1000);
            connection.connect();

            // 伺服器回來的參數
            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            stringBuilder = new StringBuilder();

            String line = null;
            while ((line = reader.readLine()) != null)
            {
                stringBuilder.append(line + "\n");
            }
            return stringBuilder.toString();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            // close the reader; this can throw an exception too, so
            // wrap it in another try/catch block.
            if (reader != null)
            {
                try
                {
                    reader.close();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }

        return null;
    }

    public aConcert transConcerByString(String strData, int dataID){
        try {
            ArrayList<JSONObject> jObject = null;
            jObject = jObject = new getJson().parseJson(strData);
            aConcert concert = new aConcert(getTheConcert(jObject,dataID));

            return concert;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
