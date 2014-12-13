package ir.abplus.adanalas.AddEditTransactions;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.view.View;

/**
 * Created by Keyvan Sasani on 11/23/2014.
 */
public class myViewPager extends ViewPager {
    static int height = 0;
    public myViewPager(Context context) {
        super(context);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {


        for(int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            child.measure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
            int h = child.getMeasuredHeight();
            if(h > height) height = h;
        }

        heightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
