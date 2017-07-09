package com.seven.zion.sitfolks;

import android.app.Dialog;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements ConfirmDialog.onCompleteList {

    private FirebaseAuth mAuth;
    private StorageReference imgref;
    private DatabaseReference DelRef;
    public boolean admin = false;
    public RecyclerView blogView;
    private DatabaseReference mDatabase;
    public TextView name;
    private TextView vEdit;
    public static final String Uid_for_intent = "Uid to be passed";
    public static final String Img_Source = "Img resource to be passed";
    private FirebaseAuth.AuthStateListener stateListener;
    private static String TAG = MainActivity.class.getSimpleName();

    ListView mDrawerList;
    private DatabaseReference mDatabaseReference;
    private DatabaseReference AdminReference;
    private DatabaseReference UserReference;
    public String Uid;
    RelativeLayout mDrawerPane;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    FirebaseRecyclerAdapter<Blog,BlogViewHolder> firebaseRecyclerAdapter;

    ArrayList<NavItem> mNavItems = new ArrayList<NavItem>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        stateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user =firebaseAuth.getCurrentUser();
                if (user==null)
                {
                    startActivity(new Intent(MainActivity.this,LoginActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
                    finish();

                }
            }
        };
        name = (TextView)findViewById(R.id.userName);
        vEdit = (TextView)findViewById(R.id.viewEdit);
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Blog");
        mDatabase.keepSynced(true);
        vEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent(MainActivity.this,editProfile.class);
                in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                in.putExtra(Uid_for_intent,Uid);
                startActivity(in);
            }
        });
        blogView = (RecyclerView)findViewById(R.id.picList);
        blogView.setHasFixedSize(true);
        blogView.setLayoutManager(new LinearLayoutManager(this));
        blogView.addOnItemTouchListener(
                new RecyclerItemClickListener(getApplicationContext(), blogView ,new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, final int position) {
                        Toast.makeText(MainActivity.this,"Double Touch on image to View it",Toast.LENGTH_SHORT).show();

                    }

                    @Override public void onLongItemClick(View view, final int position) {

                        Log.d("Long Press", "Entered LongPressListener");
                        if (admin) {
                            DelRef = FirebaseDatabase.getInstance().getReference().child("Blog");
                            AlertDialog.Builder delD = new AlertDialog.Builder(MainActivity.this);
                            delD.setTitle("Confirm Delete ").setMessage("Are you sure wanna delete this post ?")
                                    .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            dialogInterface.dismiss();
                                        }
                                    }).setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                    Toast.makeText(MainActivity.this, "Post Deleted", Toast.LENGTH_LONG).show();
                                    DelRef = firebaseRecyclerAdapter.getRef(position).child("imgSref");
                                    Log.d("imgSref", DelRef.toString());
                                    DelRef.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            String ref = dataSnapshot.getValue(String.class);
                                            try {
                                                imgref = FirebaseStorage.getInstance().getReferenceFromUrl(ref);
                                                imgref.delete();
                                            } catch (IllegalArgumentException e) {
                                                e.printStackTrace();
                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });
                                    firebaseRecyclerAdapter.getRef(position).removeValue();
                                }

                            }).create().show();
                        }
                    }
                })
        );

        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        boolean dontShowDialog = sharedPref.getBoolean("DONT_SHOW_DIALOG", false);
        if (!dontShowDialog) {
            ConfirmDialog confirmDialog = new ConfirmDialog();
            confirmDialog.setNotice(true);
            confirmDialog.setMessage("Note:Posts here are limited and will be deleted accordingly "+
            " due to the Limited number of resources.Only admins can post as of now. Check out the \"About us\" section to know more ");
            confirmDialog.show(getFragmentManager(),"Notice");
           // new ConfirmDialog().show(getFragmentManager(), "Notice");
        }
        mNavItems.add(new NavItem("Home", "View Posts", R.mipmap.ic_home_black_24dp));
        mNavItems.add(new NavItem("Results", "Check Your Results", R.mipmap.ic_scanner_black_24dp));
        mNavItems.add(new NavItem("Portal","Student Portal",R.mipmap.ic_supervisor_account_black_24dp));
        mNavItems.add(new NavItem("CGPA","Calculate your GPA",R.mipmap.ic_add_to_queue_black_24dp));
        mNavItems.add(new NavItem("My QRCode","View Your Unique QRcode",R.mipmap.ic_graphic_eq_black_24dp));
        mNavItems.add(new NavItem("About us", "Know us and Report Feedbacks", R.mipmap.ic_nature_people_black_24dp));
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);


       /* AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
        alertDialog.setMessage("Application is Not yet Completed," +
                " App Will be updated Soon" ).setNegativeButton("ok",null).create().show();*/

        // DrawerLayout
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);

        // Populate the Navigtion Drawer with options
        mDrawerPane = (RelativeLayout) findViewById(R.id.drawerPane);
        mDrawerList = (ListView) findViewById(R.id.navList);
        DrawerListAdapter adapter = new DrawerListAdapter(this, mNavItems);
        mDrawerList.setAdapter(adapter);

        // Drawer Item click listeners
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position==0)
                {
                    /*Intent intent = new Intent(MainActivity.this,Canteen.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);*/
                    mDrawerLayout.closeDrawer(mDrawerPane);
                }
                if(position==1)
                {
                    Intent intent = new Intent(MainActivity.this,Results.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    mDrawerLayout.closeDrawer(mDrawerPane);
                }
                if (position==2)
                {
                    Intent intent = new Intent(MainActivity.this,portal.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    mDrawerLayout.closeDrawer(mDrawerPane);
                }
                if(position==4)
                {
                    Intent intent = new Intent(MainActivity.this,QRcode.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    mDrawerLayout.closeDrawer(mDrawerPane);
                }
                if(position==3)
                {
                    Intent intent = new Intent(MainActivity.this,CGPA_calculator.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    mDrawerLayout.closeDrawer(mDrawerPane);
                }
                if (position==5)
                {
                    Intent intent = new Intent(MainActivity.this,About_us.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    mDrawerLayout.closeDrawer(mDrawerPane);
                }


            }
        });
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);

                invalidateOptionsMenu();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                Log.d(TAG, "onDrawerClosed: " + getTitle());

                invalidateOptionsMenu();
            }
        };

        mDrawerLayout.setDrawerListener(mDrawerToggle);

    }

    private void selectItemFromDrawer(int position) {

        Fragment fragment = new PreferencesFragment();

        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.mainContent, fragment)
                .commit();

        mDrawerList.setItemChecked(position, true);
        setTitle(mNavItems.get(position).mTitle);

        // Close the drawer
        mDrawerLayout.closeDrawer(mDrawerPane);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        if (item.getItemId()==R.id.logout)
        {
            mAuth.signOut();
        }
        if(item.getItemId()==R.id.addbtn)
        {
            if(!admin)
                Toast.makeText(this,"Sorry only Admins can post! You are not an admin!",Toast.LENGTH_LONG).show();
            else
            {
                startActivity(new Intent(MainActivity.this,post.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        if (firebaseUser==null)
        {
            startActivity(new Intent(MainActivity.this,LoginActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
            this.finish();
        }
        else
           Initialize();

        mAuth.addAuthStateListener(stateListener);
       firebaseRecyclerAdapter  = new FirebaseRecyclerAdapter<Blog, BlogViewHolder>(
                Blog.class,
                R.layout.blog_xml,
                BlogViewHolder.class,
                mDatabaseReference
        ) {
            @Override
            protected void populateViewHolder(BlogViewHolder viewHolder, final Blog model, final int position) {

                viewHolder.setDesc(model.getDesc());
                viewHolder.setT(model.getTitle());
                //viewHolder.setImg(getApplicationContext(),model.getImage());
                String img = model.getImage();
                View imgView = viewHolder.itemView;
                final ImageView imageView = (ImageView) imgView.findViewById(R.id.post_img);
                if (img.equals("null")) {
                    imageView.setVisibility(imgView.GONE);
                } else {
                    Picasso.with(getApplicationContext()).load(img).into(imageView);
                }
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent in = new Intent(MainActivity.this,ImageviewScreen.class);
                        in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        in.putExtra(Img_Source,model.getImage());
                        startActivity(in);
                    }
                });
              /*viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.d("you clicked on",Integer.toString(position));
                        Toast.makeText(MainActivity.this,"You clicked on "+Integer.toString(position),Toast.LENGTH_SHORT).show();
                    }
                });*/
            }
        };
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        blogView.setLayoutManager(layoutManager);
        blogView.setAdapter(firebaseRecyclerAdapter);
    }

    private void Initialize() {
            Uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            UserReference = FirebaseDatabase.getInstance().getReference().child("Users").child(Uid).child("name");
            AdminReference = FirebaseDatabase.getInstance().getReference().child("Users").child(Uid).child("admin");
            UserReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    name.setText(dataSnapshot.getValue(String.class));
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            AdminReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    try {
                        if ((dataSnapshot.getValue(String.class).equals("true")))
                            admin = true;
                    }
                     catch (NullPointerException e)
                    {

                        e.printStackTrace();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


    }

    @Override
    protected void onResume() {
        super.onResume();
        mAuth.addAuthStateListener(stateListener);

    }

    @Override
    protected void onPause() {
        super.onPause();
        mAuth.removeAuthStateListener(stateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(stateListener);
    }

    @Override
    public void gettGPcp(Double totalcp, Double totalCPGP) {

    }
}
class NavItem {
    String mTitle;
    String mSubtitle;
    int mIcon;

    public NavItem(String title, String subtitle, int icon) {
        mTitle = title;
        mSubtitle = subtitle;
        mIcon = icon;
    }
}


class DrawerListAdapter extends BaseAdapter {

    Context mContext;
    ArrayList<NavItem> mNavItems;

    public DrawerListAdapter(Context context, ArrayList<NavItem> navItems) {
        mContext = context;
        mNavItems = navItems;
    }

    @Override
    public int getCount() {
        return mNavItems.size();
    }

    @Override
    public Object getItem(int position) {
        return mNavItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.drawer_item, null);
        } else {
            view = convertView;
        }

        TextView titleView = (TextView) view.findViewById(R.id.title);
        TextView subtitleView = (TextView) view.findViewById(R.id.subTitle);
        ImageView iconView = (ImageView) view.findViewById(R.id.icon);

        titleView.setText(mNavItems.get(position).mTitle);
        subtitleView.setText(mNavItems.get(position).mSubtitle);
        iconView.setImageResource(mNavItems.get(position).mIcon);

        return view;
    }

}

