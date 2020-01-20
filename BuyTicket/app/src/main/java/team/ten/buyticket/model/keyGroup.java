package team.ten.buyticket.model;
//一組Key資訊(包含Key、ID)

public class keyGroup {


    public int key_ID;
    public String key_KEY;

    public keyGroup(){}

    public keyGroup(String strKey){

        try {
            key_ID = getID(strKey);
        } catch (Exception e) {
            key_ID = 0;
        }

        try {
            key_KEY = strKey;
        } catch (Exception e) {
            key_KEY = "";
        }

    }

    public int getID(String key){
        int total_ID = 0;

        for(int i = 0; i< key.length(); i++){
            String theChar = key.substring(i, i+1);
            try{
                total_ID += Integer.valueOf(theChar);
            }catch(Exception e){

            }
        }

        return total_ID;
    }
}
