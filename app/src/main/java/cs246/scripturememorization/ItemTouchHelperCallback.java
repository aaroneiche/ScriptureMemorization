package cs246.scripturememorization;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;

/**
 * From tutorial https://medium.com/@ipaulpro/drag-and-swipe-with-recyclerview-b9456d2b1aaf
 * See tutorial for details, but the basic deal is: this is attached the recycle view adapter
 * when the recycle view is being touched the callback updates the position and if the position
 * changed significantly right or left it activates the swipe method in the adapter.
 * If the position is held for a second, then goes up or down (long click, drag) it will
 * call the move method in the adapter.
 * The drag can also be called by clicking and dragging the handle button on the row.
 */

public class ItemTouchHelperCallback extends ItemTouchHelper.Callback {
    private final ItemTouchHelperAdapter mAdapter;

    public ItemTouchHelperCallback(ItemTouchHelperAdapter adapter) {
        mAdapter = adapter;
    }

    @Override
    public boolean isLongPressDragEnabled() {
        return true;
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        return true;
    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
        return makeMovementFlags(dragFlags, swipeFlags);

    }


    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                          RecyclerView.ViewHolder target) {
        Log.d("adapter", "Move");
        mAdapter.onItemMove(viewHolder.getAdapterPosition(), target.getAdapterPosition());
        return true;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        Log.d("adapter", "Swipe");
        mAdapter.onItemDismiss(viewHolder.getAdapterPosition());
    }
}
