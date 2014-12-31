package ir.abplus.adanalas.Libraries;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;
import ir.abplus.adanalas.Timeline.TimelineActivity;

/**
 * Created by Keyvan Sasani on 12/27/2014.
 */
public class TextViewWithFont extends TextView {
    public TextViewWithFont(Context context) {
        super(context);
        setTypeface(TimelineActivity.persianTypeface);
    }

    public TextViewWithFont(Context context, AttributeSet attrs) {
        super(context, attrs);
        setTypeface(TimelineActivity.persianTypeface);
    }

    public TextViewWithFont(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setTypeface(TimelineActivity.persianTypeface);
    }
}
