package team.ten.buyticket;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import team.ten.buyticket.model.MyTicket;

public class TicketDataAdapter extends RecyclerView.Adapter<TicketDataAdapter.ViewHolder> {

    private static final String TAG = "RecyclerViewAdapter";
    private Context mContext;

    private MyTicket[] ticketsData;

    public TicketDataAdapter(MyTicket[] ticketsData, Context context) {
        this.ticketsData = ticketsData;
        this.mContext = context;
    }

    @Override
    public TicketDataAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_ticket, null);

        // create ViewHolder

        TicketDataAdapter.ViewHolder viewHolder = new TicketDataAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder,int i) {

        String title1 = ticketsData[i].getTitle();
        if(title1.length() > 20){
            viewHolder.txtViewTitle.setText(title1.substring(0,20)+"...");
        }else{
            viewHolder.txtViewTitle.setText(title1);
        }


        Picasso.get()
                .load(ticketsData[i].getImageUrl())
                .centerCrop()
                .fit()
                .into(viewHolder.imgViewIcon);

        viewHolder.txtViewId.setText(String.valueOf(ticketsData[i].getId()));
        viewHolder.txtViewKey.setText(String.valueOf(ticketsData[i].getKey()));

        String key = ticketsData[i].getKey();
        if(key.substring(0,1).equals("!")){
            //viewHolder.myTicket.setBackground(mContext.getDrawable(R.drawable.border2_pressed));
            viewHolder.imgViewBG.setImageDrawable(mContext.getDrawable(R.drawable.border2_pressed));


            String title = "(已使用) "+ticketsData[i].getTitle();
            if(title.length() > 20){
                viewHolder.txtViewTitle.setText(title.substring(0,20)+"...");
            }else{
                viewHolder.txtViewTitle.setText(title);
            }



            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(view.getContext(), "此票券已使用完畢", Toast.LENGTH_SHORT).show();
                }
            });
        }else{
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent intent = new Intent(mContext, InductionActivity.class);
                    String title = ((TextView) view.findViewById(R.id.item_title)).getText().toString();
                    String id = ((TextView) view.findViewById(R.id.item_id)).getText().toString();
                    String key = ((TextView) view.findViewById(R.id.item_key)).getText().toString();

                    Bundle bundle = new Bundle();
                    bundle.putString("type", mContext.getString(R.string.induction_key_take));
                    bundle.putString("title", title);
                    bundle.putString("id", id);
                    bundle.putString("key", key);
                    intent.putExtras(bundle);

                    mContext.startActivity(intent);

                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return ticketsData.length;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {//

        public RelativeLayout myTicket;
        public TextView txtViewTitle;
        public TextView txtViewId;
        public ImageView imgViewIcon;
        public ImageView imgViewBG;
        public TextView txtViewKey;


        public ViewHolder(View itemLayoutView) {
            super(itemLayoutView);
            myTicket = (RelativeLayout) itemLayoutView.findViewById(R.id.layout_myTicket);
            txtViewTitle = (TextView) itemLayoutView.findViewById(R.id.item_title);
            imgViewIcon = (ImageView) itemLayoutView.findViewById(R.id.item_icon);
            txtViewId = (TextView) itemLayoutView.findViewById(R.id.item_id);
            imgViewBG = (ImageView) itemLayoutView.findViewById(R.id.ticketItem_bg);
            txtViewKey = (TextView) itemLayoutView.findViewById(R.id.item_key);
        }

    }
}
