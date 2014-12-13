package ir.abplus.adanalas.AddEditTransactions;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.view.*;
import android.view.inputmethod.EditorInfo;
import android.widget.*;
import com.viewpagerindicator.CirclePageIndicator;
import ir.abplus.adanalas.Timeline.TimelineActivity;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Keyvan Sasani on 11/19/2014.
 */
public class TagAdapterLayout extends LinearLayout implements View.OnClickListener{
    private myViewPager pager = null;
    private ViewPagerAdapterLayout pagerAdapter = null;
    private CirclePageIndicator title;
//    private Activity activity;
    private Context context;

    private ArrayList<LinearLayout> tag_layouts2 = new ArrayList<LinearLayout>();
    private ArrayList<TagLayout> showedTagButton = new ArrayList<TagLayout>();
    private ArrayList<String> userTags = new ArrayList<String>();
    private ArrayList<String> userTagsToShow = new ArrayList<String>();
    public ArrayList<String> selectedTags = new ArrayList<String>();

    private AutoCompleteTextView actv;
    private ArrayAdapter actv_adapter;
    private int screenWidth=0;
    private int layoutHight=0;


//    public static void setParams(ArrayList<String> selectedTag,int width){
//        screenWidth=width;
//        selectedTags = selectedTag;
//    }
    public TagAdapterLayout(Context context, ArrayList<String> inputSelectedTags,int tagLayoutHeight) {
        super(context);
        this.context=context;
        this.selectedTags=inputSelectedTags;
        showedTagButton = new ArrayList<TagLayout>();
        userTags = new ArrayList<String>();
        userTagsToShow = new ArrayList<String>();
        layoutHight=tagLayoutHeight-300;

        pager = new myViewPager(context);
        title = new CirclePageIndicator(context);

        pagerAdapter = new ViewPagerAdapterLayout();

        title.setFillColor(0xff484848);
        title.setStrokeColor(0xff303030);
        pager.setAdapter(pagerAdapter);
        title.setViewPager(pager);


        Display display = ((Activity) getContext()).getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        screenWidth = size.x;

        setWeightSum(2);
        pager.setLayoutParams(new ViewGroup.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

        actv = new AutoCompleteTextView(context);
        actv.setTypeface(TimelineActivity.persianTypeface);
        updateSuggestionAdapter();



        ViewTreeObserver viewTreeObserver = actv.getViewTreeObserver();
        viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                actv.setDropDownHeight(actv.getHeight() * 4);
                if(screenWidth==0)
                screenWidth=actv.getWidth();
            }
        });
        actv.setThreshold(1);
        actv.setSingleLine();
        actv.setHint("برچسب");
        actv.setGravity(Gravity.RIGHT);
        actv.setImeOptions(EditorInfo.IME_ACTION_DONE);


        actv.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView exampleView, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    String tmpTag = actv.getText().toString();
                    tmpTag=removeSpaces(tmpTag);
                    if(!tmpTag.equals("")){
                        makeTagLayout(tmpTag,true);
                        selectedTags.add(tmpTag);
                        title.setCurrentItem(0);
                    }
                    actv.setText("");
                    updatePageAdaptorButtons();
                    return true;
                }
                return false;
            }
        });


        actv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
                String input=(String) ((TextView) view).getText();
                makeTagLayout(input,true);
                selectedTags.add(input);
                title.setCurrentItem(0);
                actv.setText("");
                if(userTagsToShow.contains(input))
                    userTagsToShow.remove(input);
                updateSuggestionAdapter();
                updatePageAdaptorButtons();

            }
        });


        userTags=new ArrayList<String>(Arrays.asList("تاکسی","بی آر تی","صابون","تئاتر","سینما","قلهک","بنزین","پول تو جیبی","کتاب","قبض برق","صدقات"));
        userTagsToShow.addAll(userTags);
        addMostUsedTags();
        addSelectedTagsToPageAdaptor();
//        pager.setLayoutParams(new ViewPager.LayoutParams(ViewPager.));


        setOrientation(LinearLayout.VERTICAL);
        title.setPadding(0,3,0,0);

        addView(actv);
        addView(pager);
        addView(title);
    }


    private String removeSpaces(String inputString){
//        String outPutString=inputString;
        while (inputString.startsWith(" "))
            inputString=inputString.substring(1);
        while (inputString.endsWith(" "))
            inputString=inputString.substring(0,inputString.length()-1);

        return inputString;
    }
    public void makeTagLayout(String input_string,boolean shouldSelected){
        int hasButton=-1;
        for(int i=0;i<showedTagButton.size();i++){
            if(showedTagButton.get(i).tagString.equalsIgnoreCase(input_string))
            {
                hasButton=i;
            }
        }
        if(hasButton==-1){
            if(shouldSelected) {
                TagLayout tagLayout = new TagLayout(context, input_string, true);
                tagLayout.setSelected(true);
                tagLayout.rightButton.setOnClickListener(this);
                tagLayout.leftButton.setOnClickListener(this);
                tagLayout.middleButton.setOnClickListener(this);
                tagLayout.setPadding(10,0,10,0);
                showedTagButton.add(0, tagLayout);
            }
            else {
                TagLayout tagLayout = new TagLayout(getContext(), input_string, false);
                tagLayout.setSelected(false);
                tagLayout.rightButton.setOnClickListener(this);
                tagLayout.leftButton.setOnClickListener(this);
                tagLayout.middleButton.setOnClickListener(this);
                tagLayout.setPadding(10,0,10,0);
                showedTagButton.add(0, tagLayout);
            }
        }
        else {
            showedTagButton.get(hasButton).setSelected(shouldSelected);
        }
    }
    public TagLayout makeTagLayout2(String input_string,boolean shouldSelected){
        int hasButton=-1;
//        for(int i=0;i<showedTagButton.size();i++){
//            if(showedTagButton.get(i).tagString.equalsIgnoreCase(input_string))
//            {
//                hasButton=i;
//            }
//        }
        if(hasButton==-1){
            if(shouldSelected) {
                TagLayout tagLayout = new TagLayout(context, input_string, true);
                tagLayout.setSelected(true);
                tagLayout.rightButton.setOnClickListener(this);
                tagLayout.leftButton.setOnClickListener(this);
                tagLayout.middleButton.setOnClickListener(this);
                return tagLayout;
            }
            else {
                TagLayout tagLayout = new TagLayout(getContext(), input_string, false);
                tagLayout.setSelected(false);
                tagLayout.rightButton.setOnClickListener(this);
                tagLayout.leftButton.setOnClickListener(this);
                tagLayout.middleButton.setOnClickListener(this);
                return tagLayout;
            }
        }
//        else {
//            showedTagButton.get(hasButton).setSelected(shouldSelected);
//        }
        return null;
    }
    private void updateSuggestionAdapter() {
        actv_adapter = new ArrayAdapter(getContext(),android.R.layout.simple_list_item_1,userTagsToShow);
        actv.setAdapter(actv_adapter);
    }

    @Override
    public void onClick(View view) {
        int pageCount=pager.getCurrentItem();

        if(view.getClass()==ImageButton.class||view.getClass()==Button.class){
            int index=-1;
            for(int i=0;i<showedTagButton.size();i++){

                if(((TagLayout)view.getParent()).getTag()==showedTagButton.get(i).getTag()){
                    index=i;}}
            if(index!=-1)
            {
                if(view.isSelected())
                {
                    userTagsToShow.add(showedTagButton.get(index).tagString);
                    selectedTags.remove(showedTagButton.get(index).tagString);
                    showedTagButton.get(index).setSelected(false);
                    updateSuggestionAdapter();
                    updatePageAdaptorButtons();
                }
                else
                {
                    showedTagButton.get(index).setSelected(true);
                    selectedTags.add(showedTagButton.get(index).tagString);
                    userTagsToShow.remove(showedTagButton.get(index).tagString);
                    updateSuggestionAdapter();
                    updatePageAdaptorButtons();

                }
                pager.setCurrentItem(pageCount);

            }
            //            System.out.println("selected tags ARE  :"+Arrays.toString(selectedTags.toArray()));

        }
    }
    private void updatePageAdaptorButtons() {
        for(TagLayout tmp:showedTagButton){
            if(tmp.getParent()!=null)
                ((LinearLayout)tmp.getParent()).removeAllViews();
        }


//        putChild(screenWidth);
        put_child2(screenWidth);
    }
    private void addMostUsedTags(){
        ArrayList<String> mostUsedTags=new ArrayList<String>();
        // !!!!!   an algorithm to find most used tags and add to mostUsedTags
        mostUsedTags=new ArrayList<String>(Arrays.asList("تاکسی", "بی آر تی", "صابون", "تئاتر", "سینما", "قلهک", "بنزین"));

        for(String s:mostUsedTags){
            //            if(userTagsToShow.contains(s))
            //        userTagsToShow.remove(s);
            makeTagLayout(s,false);
        }
        updateSuggestionAdapter();
//        updatePageAdaptorButtons();

    }
    private void addSelectedTagsToPageAdaptor() {
        for(String s:selectedTags){
            makeTagLayout(s,true);
            if(userTagsToShow.contains(s))
                userTagsToShow.remove(s);
        }
        updateSuggestionAdapter();
        updatePageAdaptorButtons();


    }

    private void put_child2(int screenWidth){
        int currentWidth=screenWidth;
        int currentHight=layoutHight;
//        currentHight-=300;
//        currentHight=pager.measuredHeightSize;
        while (pagerAdapter.getCount()>0){
            pagerAdapter.removeView(pager,0);
        }

        LinearLayout linearLayout=new LinearLayout(context);
        LinearLayout linearLayoutVertical=new LinearLayout(context);
        linearLayoutVertical.setOrientation(VERTICAL);
        linearLayoutVertical.addView(linearLayout);
        pagerAdapter.addView(linearLayoutVertical);
        for (int i = 0; i < showedTagButton.size(); i++) {
            showedTagButton.get(i).measure(0, 0);
            showedTagButton.get(i).setTag(i);
            if(currentWidth>showedTagButton.get(i).getMeasuredWidth())
            {
            linearLayout.addView(showedTagButton.get(i));
            currentWidth-=showedTagButton.get(i).getMeasuredWidth();
            }
            else{

                if(currentHight>showedTagButton.get(i).getMeasuredHeight()){
                    linearLayout=new LinearLayout(context);
                    linearLayoutVertical.addView(linearLayout);
                    linearLayout.addView(showedTagButton.get(i));
                    currentWidth=screenWidth-showedTagButton.get(i).getMeasuredWidth();
                    currentHight-=showedTagButton.get(i).getMeasuredHeight();
                }
                else {
                    currentHight=layoutHight;
                    linearLayout=new LinearLayout(context);
                    linearLayoutVertical=new LinearLayout(context);
                    linearLayoutVertical.setOrientation(VERTICAL);
                    linearLayoutVertical.addView(linearLayout);
                    linearLayout.addView(showedTagButton.get(i));
                    pagerAdapter.addView(linearLayoutVertical);
                    currentWidth=screenWidth-showedTagButton.get(i).getMeasuredWidth();
                }
            }
        }
        title.notifyDataSetChanged();
        pagerAdapter.notifyDataSetChanged();
    }

}
