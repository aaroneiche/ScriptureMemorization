package cs246.scripturememorization;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;

public class Main_RecyclerViewAdapter extends RecyclerView.Adapter <Main_RecyclerViewAdapter.ViewHolder> implements ItemTouchHelperAdapter {
    private List<Scripture> _scriptures;
    private LayoutInflater _inflater;
    private ItemClickListener _listener;
    private static final String TAG = "main_RVA";

    Main_RecyclerViewAdapter(Context context, List<Scripture> scriptures) {
        _scriptures = scriptures;
        _inflater = LayoutInflater.from(context);

    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = _inflater.inflate(R.layout.main_recyclerview_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String reference = sfHelper.getReference(_scriptures.get(position));
        holder.reference.setText(reference);
        reference = sfHelper.getTextShort(_scriptures.get(position));
        holder.tag.setText(reference);
        if (_scriptures.get(position).memorized) {
            holder.star.setImageResource(R.drawable.check_small);;
        }
        else {
            holder.star.setImageResource(R.drawable.box_small);;
        }
    }

    @Override
    public int getItemCount() {
        return _scriptures.size();
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements RecyclerView.OnClickListener {
        TextView reference;
        TextView tag;
        ImageView star;

        ViewHolder(View itemView) {
            super(itemView);
            reference = itemView.findViewById(R.id.text_reference);
            tag = itemView.findViewById(R.id.text_scripture);
            star = itemView.findViewById(R.id.image_star);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (_listener != null) _listener.onItemClick(view, getAdapterPosition());
        }
    }

    // convenience method for getting data at click position
    Scripture getItem(int id) {
        return _scriptures.get(id);
    }

    // allows clicks events to be caught
    void setClickListener(ItemClickListener itemClickListener) {
       this._listener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        Log.d(TAG, "from: " + fromPosition + " to: " + toPosition);
        /*
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(_scriptures, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(_scriptures, i, i - 1);
            }
        }
        notifyItemMoved(fromPosition, toPosition);*/
        return true;
    }

    @Override
    public void onItemDismiss(int position) {
        _scriptures.remove(position);
        notifyItemRemoved(position);
    }
}
