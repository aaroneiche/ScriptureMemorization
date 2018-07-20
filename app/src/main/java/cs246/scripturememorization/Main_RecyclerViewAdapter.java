package cs246.scripturememorization;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;

/**
 * A custom adapter for the recycler view in the main activity.
 * Extends a standard recycler view adapter
 * implements Item Touch Helper Adapter to add swipe and drag functionality
 */

public class Main_RecyclerViewAdapter extends RecyclerView.Adapter <Main_RecyclerViewAdapter.ViewHolder> implements ItemTouchHelperAdapter {
    private List<Scripture> mScriptures;
    private LayoutInflater mInflater;
    private ItemClickListener mListener;
    private final OnStartDragListener mDragListenter;
    private static final String TAG = "main_RVA";

    Main_RecyclerViewAdapter(Context context, OnStartDragListener dragListener, List<Scripture> scriptures) {
        mScriptures = scriptures;
        mDragListenter = dragListener;
        mInflater = LayoutInflater.from(context);
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.main_recyclerview_row, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        String reference = sfHelper.getReference(mScriptures.get(position));
        holder.reference.setText(reference);
        reference = sfHelper.getTextShort(mScriptures.get(position));
        holder.percent.setText(sfHelper.getPercent(mScriptures.get(position)));
        holder.tag.setText(reference);
        if (mScriptures.get(position).memorized) {
            holder.check.setImageResource(R.drawable.check_small);
        }
        else {
            holder.check.setImageResource(R.drawable.box_small);
        }
        holder.handle.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
                    mDragListenter.onStartDrag(holder);
                }
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return mScriptures.size();
    }

    /**
    * holds all the pieces of the view from main_recyclerview_row
     **/
    public class ViewHolder extends RecyclerView.ViewHolder implements RecyclerView.OnClickListener {
        TextView reference;
        TextView tag;
        TextView percent;
        ImageView check;
        ImageView handle;

        ViewHolder(View itemView) {
            super(itemView);
            reference = itemView.findViewById(R.id.text_reference);
            tag = itemView.findViewById(R.id.text_scripture);
            percent = itemView.findViewById(R.id.text_percent);
            check = itemView.findViewById(R.id.image_checkBox);
            itemView.setOnClickListener(this);
            handle = itemView.findViewById(R.id.image_handle);
        }

        @Override
        public void onClick(View view) {
            if (mListener != null) mListener.onItemClick(view, getAdapterPosition());
        }
    }

    // convenience method for getting data at click position
    Scripture getItem(int id) {
        return mScriptures.get(id);
    }

    // allows clicks events to be caught
    void setClickListener(ItemClickListener itemClickListener) {
       this.mListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }

    /**
     * handles drag functionality to re-order list of scriptures
     * @param fromPosition - old position in the list
     * @param toPosition - new position in the list
     */

    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        Log.d(TAG, "from: " + fromPosition + " to: " + toPosition);

        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(mScriptures, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(mScriptures, i, i - 1);
            }
        }
        notifyItemMoved(fromPosition, toPosition);
       // return true;
    }

    /**
     * handles swipe functionality, removes a scripture from the list.
     * @param position - the position in the list
     */
    @Override
    public void onItemDismiss(int position) {
        mScriptures.remove(position);
        notifyItemRemoved(position);
    }
}
