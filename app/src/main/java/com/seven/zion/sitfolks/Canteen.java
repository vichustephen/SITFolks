package com.seven.zion.sitfolks;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Canteen extends AppCompatActivity {
    private static Canteen instance = null;

    private RecyclerView mItemList;
    //static int stock;
    private DatabaseReference mDatabaseReference;
    private DatabaseReference UserReference;
    public String Uid;
    public static Item order;
    public static String Bal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.canteen);
        this.instance = this;

        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Canteen");
        Uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        UserReference = FirebaseDatabase.getInstance().getReference().child("Users").child(Uid).child("money");
        UserReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Bal = dataSnapshot.getValue(String.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        mItemList = (RecyclerView)findViewById(R.id.itemlist);
        mItemList.setHasFixedSize(true);
        mItemList.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerAdapter<Item,ItemViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Item, ItemViewHolder>(
                Item.class,
                R.layout.item_list,
        ItemViewHolder.class,
                mDatabaseReference
                ) {
            @Override
            protected void populateViewHolder(ItemViewHolder viewHolder, Item model, int position) {

                viewHolder.setDesc(model.getDesc());
                viewHolder.setP(model.getPrice());
                viewHolder.setB(Integer.parseInt(model.getStock()),model);
            }
        };
        mItemList.setAdapter(firebaseRecyclerAdapter);

    }
    public static class ItemViewHolder extends RecyclerView.ViewHolder{

        View view;
        public ItemViewHolder(View itemView) {
            super(itemView);
            view= itemView;

        }
        public void setDesc (String desc)
        {
            TextView Tdesc = (TextView)view.findViewById(R.id.viewEdit);
            Tdesc.setText(desc);
        }
        public void setP(String price){
            TextView Pt = (TextView)view.findViewById(R.id.price);
            Pt.setText(price);
        }
        public void setB(final int st,final Item model){
            final Button buyButton = (Button)view.findViewById(R.id.buy);
            buyButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    order = new Item(model.getDesc(),model.getImage(),model.getPrice(),model.getIcode(),"null");
                    if (st == 0) {
                        Toast.makeText(Canteen.getInstance(), "No Stock", Toast.LENGTH_LONG).show();
                    } else {
                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(Canteen.getInstance());
                        alertDialog.setMessage("Place Order ?").setNegativeButton("Cancel", null);
                        alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                int bal = Integer.parseInt(Bal);
                                if (bal <= 200) {
                                    Toast.makeText(Canteen.getInstance(), "Low Balance", Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(Canteen.getInstance(), "Order Placed", Toast.LENGTH_LONG).show();

                                    Place_order(order);
                                }
                            }
                        });
                        alertDialog.show();
                    }
                }
            });

        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.orders_menu,menu);
        return true;
    }

    public static Canteen getInstance() {
        return instance;
    }
    public static void Place_order(Item order)
    {
        DatabaseReference PlaceOrder = FirebaseDatabase.getInstance().getReference().child("Pending_orders").child("Users");
        String user = FirebaseAuth.getInstance().getCurrentUser().getUid();
        PlaceOrder.child(user).push().setValue(order);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()==R.id.orders)
        {
        }
        return super.onOptionsItemSelected(item);
    }

}
