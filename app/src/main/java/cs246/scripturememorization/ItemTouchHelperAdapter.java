package cs246.scripturememorization;

/**
 * adds methods to the adapter to utilize an Item Touch Helper
 * method from tutorial: https://medium.com/@ipaulpro/drag-and-swipe-with-recyclerview-b9456d2b1aaf
 */
public interface ItemTouchHelperAdapter {
    void onItemMove(int fromPosition, int toPosition);

    void onItemDismiss(int position);
}
