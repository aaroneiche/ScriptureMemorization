package cs246.scripturememorization;

import android.content.res.Resources;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

/**
 * This class adds a maximum height to a view.
 * It can be attached to a view using:
 *      View.getViewTreeObserver().addOnGlobalLayoutListener(scrollListener);
 * call update when the height needs to be changed.
 */

public class OnViewGlobalLayoutListener implements ViewTreeObserver.OnGlobalLayoutListener{
    private int maxHeight;
    private View view;
    private boolean change = false;
    private final String TAG = "GlobalListener";

    public OnViewGlobalLayoutListener(View view, int height) {
        maxHeight = height;
        this.view = view;
    }

    @Override
    public void onGlobalLayout() {
        if (change) {
            change = false;
            ViewGroup.LayoutParams params = view.getLayoutParams();
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            view.setLayoutParams(params);
            Log.d(TAG, "ViewHeight: " + view.getHeight());
            if (view.getHeight() > maxHeight) {
                params = view.getLayoutParams();
                params.height = maxHeight;
                view.setLayoutParams(params);
                Log.d(TAG, "Height: " + params.height);
            }
        }
    }

    public void update() {
        change = true;
    }

    public static int getHeight() {
        int h = Resources.getSystem().getDisplayMetrics().heightPixels;
        int w = Resources.getSystem().getDisplayMetrics().widthPixels;
        return (h > w ? w : h);
    }
}
