package ir.abplus.adanalas.Libraries;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import ir.abplus.adanalas.R;

/**
 * Created by Keyvan Sasani on 1/18/2015.
 */
public class LodingDialog extends Dialog {
    ImageView img;
    TextView stateText;
    TextView messageText;
    String stateString;
    String message;
    Resources res;

    public LodingDialog(Context context,Resources resources, String message) {
        super(context);
        this.message=message;
        res=resources;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.loading_dialog);
        img=(ImageView)findViewById(R.id.loadingImg);
        img.setImageDrawable(res.getDrawable(R.drawable.hadaf_logo_100));
        messageText=(TextView)findViewById(R.id.messageTextView);
        stateText=(TextView)findViewById(R.id.stateTextView);
        stateText.setText("لطفا منتظر بمانید");
        messageText.setText(message);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        setCancelable(false);
//        new DoAnimation().execute();
        doAnim();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.e("debug","dialog paused");
        img.clearAnimation();
    }

    private void doAnim(){
        Log.e("debug", "animation start");
        RotateAnimation rAnim = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        //, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rAnim.setRepeatCount(RotateAnimation.INFINITE);
        rAnim.setDuration(1000);
        rAnim.setInterpolator(getContext(),android.R.anim.linear_interpolator);
//        rAnim.set
//        while (true){
        img.startAnimation(rAnim);
//        }
    }
}
