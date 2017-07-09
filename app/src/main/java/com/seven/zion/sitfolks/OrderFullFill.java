package com.seven.zion.sitfolks;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class OrderFullFill extends AppCompatActivity {
    private RecyclerView mItemList;
    private DatabaseReference mDatabaseReference;
    public String id;
    public static Item order;
    public static String Bal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_full_fill);
        id = getIntent().getStringExtra(AdminActivity.Uid);
        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Pending_orders").child("Users").child(id);
        mItemList = (RecyclerView) findViewById(R.id.orderlist);
        mItemList.setHasFixedSize(true);
        mItemList.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerAdapter<Item, ItemViewHolder2> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Item,ItemViewHolder2>(
                Item.class,
                R.layout.orderlist,
                ItemViewHolder2.class,
                mDatabaseReference
        ) {
            @Override
            protected void populateViewHolder(ItemViewHolder2 viewHolder, Item model, int position) {

                viewHolder.setDesc(model.getDesc());
                viewHolder.setP(model.getPrice());

            }
        };
        mItemList.setAdapter(firebaseRecyclerAdapter);
    }
    public static class ItemViewHolder2 extends RecyclerView.ViewHolder{

        View view;
        public ItemViewHolder2(View itemView) {
            super(itemView);
            view= itemView;

        }
        public void setDesc (String desc)
        {
            TextView Tdesc = (TextView)view.findViewById(R.id.name1);
            Tdesc.setText(desc);
        }
        public void setP(String price){
            TextView Pt = (TextView)view.findViewById(R.id.price1);
            Pt.setText(price);
        }
        public void setB(final int st,final Item model){

        }
    }

}
