package cs246.scripturememorization;

import android.support.v7.widget.RecyclerView;

public interface OnStartDragListener {

    /**
     * Called when a view is requesting a start of a drag.
     * method from: https://medium.com/@ipaulpro/drag-and-swipe-with-recyclerview-6a6f0c422efd
     * @param viewHolder The holder of the view to drag.
     */
    void onStartDrag(RecyclerView.ViewHolder viewHolder);

}
