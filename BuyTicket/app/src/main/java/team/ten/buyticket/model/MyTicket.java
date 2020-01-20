package team.ten.buyticket.model;
////已購票券RecyclerView Item 資訊

public class MyTicket {

    private int ticketId = 0;
    private String ticketTitle = "";
    private String ticketKey = "";
    private String ticketImageUrl = "";


    public MyTicket(int id, String title, String key, String imageUrl){

        if(id > 0){ ticketId = id; }

        if(title != null){this.ticketTitle = title;}
        if(key != null){this.ticketKey = key;}
        if(imageUrl != null){this.ticketImageUrl = imageUrl;}

    }

    public int getId() {
        return ticketId;
    }
    public String getTitle() {
        return ticketTitle;
    }
    public String getKey() {
        return ticketKey;
    }
    public String getImageUrl() {
        return ticketImageUrl;
    }


    public void setId(int id) {
        this.ticketId = id;
    }
    public void setTitle(String title) {
        this.ticketTitle = title;
    }
    public void setKey(String key) {
        this.ticketKey = key;
    }
    public void setImageUrl(String imageUrl) {
        this.ticketImageUrl = imageUrl;
    }



}
