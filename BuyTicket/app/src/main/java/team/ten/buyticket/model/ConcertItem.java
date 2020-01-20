package team.ten.buyticket.model;
//首頁RecyclerView Item 資訊


public class ConcertItem {
    private String title = "";
    private String imageUrl = "";
    private int id = 0;
    private int price = 0;
    private String info = "";

    public ConcertItem(String title, String imageUrl, int id, int price, String info){

        if(title != null){this.title = title;}

        if(imageUrl != null){this.imageUrl = imageUrl;}

        this.id = id;

        this.price = price;

        this.info = info;
    }

    public String getTitle() {
        return title;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public int getId() {
        return id;
    }

    public int getPrice() {
        return price;
    }

    public String getInfo() { return info;}

    public void setTitle(String title) {
        this.title = title;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public  void  setInfo(String info) {this.info = info;}
}
