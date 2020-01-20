package team.ten.buyticket;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import team.ten.buyticket.common.logger.Log;
import team.ten.buyticket.model.ConcertItem;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder>{

    private static final String TAG = "RecyclerViewAdapter";

    private ConcertItem[] itemsData;
    private Context mContext;

    private int hostIndex = 0;

    public MyAdapter(ConcertItem[] itemsData, Context context) {
        this.itemsData = itemsData;
        this.mContext = context;
}
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_item, viewGroup,false);
        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int position) {


        if(itemsData[position].getTitle().length() > 20){
            viewHolder.txtViewTitle.setText(itemsData[position].getTitle().substring(0,20)+"...");
        }else{
            viewHolder.txtViewTitle.setText(itemsData[position].getTitle());
        }


        viewHolder.txt_id.setText(String.valueOf(itemsData[position].getId()));
        viewHolder.txtViewMoney.setText("NT."+String.valueOf(itemsData[position].getPrice()));
        Picasso.get().load(itemsData[position].getImageUrl()).fit().into(viewHolder.imgViewIcon);


        viewHolder.bt_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder i_dialog = new AlertDialog.Builder(mContext);
                AlertDialog dialog = i_dialog.show();
                i_dialog.setTitle("場次介紹")//設定視窗標題
                        .setIcon(R.drawable.icon_information)//設定對話視窗圖示
                        .setMessage(itemsData[position].getInfo())//設定顯示的文字
                        .setPositiveButton("OK",new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        }) //設定結束的子視窗
                        .show();//呈現對話視窗
                dialog.cancel();
            }
        });


        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(mContext, BuyActivity.class);
                intent.putExtra("id",itemsData[position].getId());
                mContext.startActivity(intent);

            }
        });

    }



    // Return the size of your itemsData (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return itemsData.length;
    }

    // inner class to hold a reference to each item of RecyclerView
    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView txtViewTitle;
        public TextView txt_id;
        public TextView txtViewMoney;
        public ImageView imgViewIcon;
        public Button bt_info;

        public ViewHolder(View itemView) {
            super(itemView);

            txtViewTitle = (TextView) itemView.findViewById(R.id.item_title);
            txt_id = (TextView)itemView.findViewById(R.id.item_lab);
            imgViewIcon = (ImageView) itemView.findViewById(R.id.item_icon);
            txtViewMoney = (TextView) itemView.findViewById(R.id.item_money);
            bt_info = (Button)itemView.findViewById(R.id.button_introuduce);
        }

    }

    

}
