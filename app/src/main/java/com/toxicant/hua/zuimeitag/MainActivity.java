package com.toxicant.hua.zuimeitag;

import android.animation.ObjectAnimator;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    TAGRecyclerView rv;
    Button btn1;
    Button btn2;
    ArrayList<String> datas;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn1= (Button) findViewById(R.id.button);
        btn2= (Button) findViewById(R.id.button2);
        rv= (TAGRecyclerView) findViewById(R.id.rv1);
        rv.setLayoutManager(new MyLayoutManager());
       datas=new ArrayList<>();
        for (int i=0;i<20;i++){
            datas.add("item==>"+i);
        }
       // final int temp=height/7;
        final RvAdapter adapter = new RvAdapter();
        rv.setAdapter(adapter);
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rv.selectNextItem();

            }
        });
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rv.selectPreviousItem();
            }
        });
        rv.setSwitchListener(new TAGRecyclerView.SwitchListener() {
            @Override
            public void onSwitch(int realIndex) {
                Toast.makeText(MainActivity.this,"当前选择=>"+realIndex,Toast.LENGTH_SHORT).show();
            }
        });
    }

    class RvAdapter extends RecyclerView.Adapter<ViewHoler>{

        @Override
        public ViewHoler onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHoler(LayoutInflater.from(parent.getContext()).inflate(R.layout.item,null,false));
        }

        @Override
        public void onBindViewHolder(final ViewHoler holder, int position) {
            holder.tv1.setText(datas.get(position));
        }

        @Override
        public int getItemCount() {
            return datas.size();
        }

    }
    class ViewHoler extends RecyclerView.ViewHolder{
        ImageView ivl;
        TextView tv1;
        LinearLayout ll;
        public ViewHoler(View itemView) {
            super(itemView);
            ivl= (ImageView) itemView.findViewById(R.id.iv);
            tv1= (TextView) itemView.findViewById(R.id.tv1);
            ll= (LinearLayout) itemView.findViewById(R.id.linear_layout);
            itemView.setClickable(false);
        }
    }


}




