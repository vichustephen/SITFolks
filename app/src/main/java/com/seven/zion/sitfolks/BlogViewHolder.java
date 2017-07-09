package com.seven.zion.sitfolks;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * Created by Stephen on 26-May-17.
 */

public class BlogViewHolder extends RecyclerView.ViewHolder  {

    View view;

    public BlogViewHolder(View itemView) {
        super(itemView);
        view = itemView;

    }

    public void setDesc(String desc) {
        TextView Pdesc = (TextView) view.findViewById(R.id.post_desc);
        Pdesc.setText(desc);
    }

    public void setT(String title) {
        TextView Pt = (TextView) view.findViewById(R.id.p_title);
        Pt.setText(title);
    }
    public void setImg(Context context, String sImg) {
        ImageView imageView = (ImageView) view.findViewById(R.id.post_img);
        if (sImg.equals("null")) {
            imageView.setVisibility(view.GONE);
        } else {
            Picasso.with(context).load(sImg).into(imageView);
        }
    }

}
 class RecyclerItemClickListener implements RecyclerView.OnItemTouchListener {
    private OnItemClickListener mListener;

    public interface OnItemClickListener {
        public void onItemClick(View view, int position);

        public void onLongItemClick(View view, int position);
    }

    GestureDetector mGestureDetector;

    public RecyclerItemClickListener(Context context, final RecyclerView recyclerView, OnItemClickListener listener) {
        mListener = listener;
        mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }

            @Override
            public void onLongPress(MotionEvent e) {
                View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                if (child != null && mListener != null) {
                    mListener.onLongItemClick(child, recyclerView.getChildAdapterPosition(child));
                }
            }
        });
    }

    @Override public boolean onInterceptTouchEvent(RecyclerView view, MotionEvent e) {
        View childView = view.findChildViewUnder(e.getX(), e.getY());
        if (childView != null && mListener != null && mGestureDetector.onTouchEvent(e)) {
            mListener.onItemClick(childView, view.getChildAdapterPosition(childView));
            return true;
        }
        return false;
    }

    @Override public void onTouchEvent(RecyclerView view, MotionEvent motionEvent) { }

    @Override
    public void onRequestDisallowInterceptTouchEvent (boolean disallowIntercept){}
}