package team.ten.buyticket;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

public class InductionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_induction);


        Bundle bundle =this.getIntent().getExtras();
        String type = bundle.getString("type");

        ((TextView) this.findViewById(R.id.textType)).setText(type);
        TextView tvInduc = (TextView) this.findViewById(R.id.textView);
        TextView tvDesc = (TextView) this.findViewById(R.id.touch_name);
        TextView tvKey = (TextView) this.findViewById(R.id.touch_key);
        ImageView img = (ImageView) this.findViewById(R.id.imageView);


        if(type.equals(getString(R.string.induction_key_take))){
            String title = bundle.getString("title");
            String id = bundle.getString("id");
            String key = bundle.getString("key");

            if(title.length() > 0 && id.length() > 0){
                tvDesc.setText(title);
            }

            if(key.length() > 0){
                tvKey.setText("");
            }

            tvInduc.setText("請感應");
            img.setImageResource(R.drawable.induction_image);
        }else if(type.equals(getString(R.string.induction_balance_deposit)) || type.equals(getString(R.string.induction_balance_withdraw))){
            tvDesc.setText("");
            tvInduc.setText("請感應");
            img.setImageResource(R.drawable.induction_image);
        }
    }


}
