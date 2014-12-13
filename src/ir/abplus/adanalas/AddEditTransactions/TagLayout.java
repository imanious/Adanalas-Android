package ir.abplus.adanalas.AddEditTransactions;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import ir.abplus.adanalas.R;
import ir.abplus.adanalas.Timeline.TimelineActivity;;

/**
 * Created by Keyvan Sasani on 8/20/2014.
 */
public class TagLayout extends LinearLayout implements View.OnClickListener {

    ImageButton leftButton;
    Button middleButton;
    ImageButton rightButton;
    boolean isSelected;
    String tagString;
    static int tagID=0;

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public TagLayout(Context context,String tagText,boolean selected) {
        super(context);

        isSelected=selected;
        tagString=tagText;
        setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        leftButton=new ImageButton(context);
        middleButton=new Button(context);
        rightButton=new ImageButton(context);
        if(isSelected){
            leftButton.setImageDrawable(getResources().getDrawable(R.drawable.tag_left_selected));
            middleButton. setBackground(getResources().getDrawable(R.drawable.tag_middle_selected));
            rightButton.setImageDrawable(getResources().getDrawable(R.drawable.tag_right_selected));

        }
        else {
            leftButton.setImageDrawable(getResources().getDrawable(R.drawable.tag_left));
            middleButton.setBackground(getResources().getDrawable(R.drawable.tag_middle));
            rightButton.setImageDrawable(getResources().getDrawable(R.drawable.tag_right));
        }
        middleButton.setText(tagText);
        leftButton.setBackground(null);
        leftButton.setPadding(0, 0, 0, 0);
        middleButton.setPadding(0,0,0,0);
        rightButton.setPadding(0,0,0,0);
        rightButton.setBackground(null);
        middleButton.setTypeface(TimelineActivity.persianTypeface);

        leftButton.setTag(tagID);
        middleButton.setTag(tagID);
        rightButton.setTag(tagID);
        setTag(tagID);
        tagID++;

        addView(leftButton);
        addView(middleButton);
        addView(rightButton);
        leftButton.setOnClickListener(this);
        middleButton.setOnClickListener(this);
        rightButton.setOnClickListener(this);
//        setOnClickListener(this);


    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onClick(View view) {
//        System.out.println("hi!"+view.getClass().toString());
        if(view.isSelected()){
            setSelected(false);
            leftButton.setSelected(false);
            middleButton.setSelected(false);
            rightButton.setSelected(false);
            leftButton.setImageDrawable(getResources().getDrawable(R.drawable.tag_left));
            middleButton. setBackground(getResources().getDrawable(R.drawable.tag_middle));
            rightButton.setImageDrawable(getResources().getDrawable(R.drawable.tag_right));

        }
        else{
            setSelected(true);
            leftButton.setSelected(true);
            middleButton.setSelected(true);
            rightButton.setSelected(true);
            leftButton.setImageDrawable(getResources().getDrawable(R.drawable.tag_left_selected));
            middleButton. setBackground(getResources().getDrawable(R.drawable.tag_middle_selected));
            rightButton.setImageDrawable(getResources().getDrawable(R.drawable.tag_right_selected));

        }
    }

    @Override
    public void setSelected(boolean isSelected) {
        this.isSelected = isSelected;
        leftButton.setSelected(isSelected);
        rightButton.setSelected(isSelected);
        middleButton.setSelected(isSelected);
        if(isSelected){
            leftButton.setImageDrawable(getResources().getDrawable(R.drawable.tag_left_selected));
            middleButton. setBackground(getResources().getDrawable(R.drawable.tag_middle_selected));
            rightButton.setImageDrawable(getResources().getDrawable(R.drawable.tag_right_selected));

        }
        else {
            leftButton.setImageDrawable(getResources().getDrawable(R.drawable.tag_left));
            middleButton.setBackground(getResources().getDrawable(R.drawable.tag_middle));
            rightButton.setImageDrawable(getResources().getDrawable(R.drawable.tag_right));
        }

    }
}
