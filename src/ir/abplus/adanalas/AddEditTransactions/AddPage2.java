package ir.abplus.adanalas.AddEditTransactions;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.util.TypedValue;
import android.view.*;
import android.view.inputmethod.EditorInfo;
import android.widget.*;
import com.fourmob.datetimepicker.Utils;
import com.fourmob.datetimepicker.date.PersianCalendar;
import com.fourmob.datetimepicker.date.PersianDatePickerDialog;
import com.sleepbot.datetimepicker.time.PersianTimePickerDialog;
import com.sleepbot.datetimepicker.time.RadialPickerLayout;
import com.viewpagerindicator.CirclePageIndicator;
import ir.abplus.adanalas.Libraries.*;
import ir.abplus.adanalas.Libraries.TransactionsContract.TagsEntry;
import ir.abplus.adanalas.Libraries.TransactionsContract.TransactionEntry;
import ir.abplus.adanalas.R;
import ir.abplus.adanalas.Timeline.TimelineActivity;
import ir.abplus.adanalas.databaseConnections.LocalDBServices;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AddPage2 extends FragmentActivity implements View.OnTouchListener,View.OnClickListener,
        PersianDatePickerDialog.OnDateSetListener, PersianTimePickerDialog.OnTimeSetListener{

    public static final int DONE = 0;
    public static final int BACK_OR_UP = 1;

    public static final String CATEGORY_KEY = "Category_index";
    public static final String AMOUNT_KEY = "Amount_value";
    public static final String IS_EXPENSE_KEY = "is_expense";

    public static final String PERSIAN_DATEPICKER_TAG = "pdatepicker";
    public static final String PERSIAN_TIMEPICKER_TAG = "ptimepicker";


    private PersianCalendar calendar;
    private PersianDatePickerDialog datePicker;
    private int selectedYear;
    private int selectedMonth;
    private int selectedDay;
    private int selectedWeekday;
    private PersianTimePickerDialog timePicker;
    private int selectedHour;
    private int selectedMinute;

    private ViewPager pager = null;
    private ViewPagerAdapterLayout pagerAdapter = null;
    private CirclePageIndicator title;

    @SuppressWarnings("deprecation")
    private ArrayList<AbsoluteLayout> tag_layouts = new ArrayList<AbsoluteLayout>();
    private ArrayList<TagLayout> showedTagButton = new ArrayList<TagLayout>();
    private ArrayList<String> userTags = new ArrayList<String>();
    private ArrayList<String> userTagsToShow = new ArrayList<String>();
    private ArrayList<String> selectedTags = new ArrayList<String>();
    private ArrayList<String> accountsList = new ArrayList<String>();

    private AutoCompleteTextView actv;
    @SuppressWarnings("rawtypes")
    private ArrayAdapter actv_adapter;

    private int mVerticalSpacing;
    private int mHorizontalSpacing;

    private int category_index;
    private double amount;
    private boolean isExpense;

    private int width ;
    private int height;

    private String description;
    private String accountName;
    private LinearLayout dateLayout;
    private TextView timeText;
    private TextView yearMonthTextview;
    private TextView dayOfMonthTextview;
    private TextView weekdayTextview;
    private TextView amPmTextview;
    private EditText descp;
    private Spinner accountSpinner;

    private String id = "null";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getWindow().requestFeature(Window.F)
        getActionBar().setDisplayHomeAsUpEnabled(true);
        showedTagButton = new ArrayList<TagLayout>();
        userTags = new ArrayList<String>();
        userTagsToShow = new ArrayList<String>();
        selectedTags = new ArrayList<String>();

        setContentView(R.layout.add_page2);


        calendar = new PersianCalendar();
        selectedDay = calendar.get(PersianCalendar.DAY_OF_MONTH);
        selectedMonth = calendar.get(PersianCalendar.MONTH);
        selectedYear = calendar.get(PersianCalendar.YEAR);
        selectedWeekday = calendar.get(PersianCalendar.DAY_OF_WEEK);

        Calendar cal = Calendar.getInstance();
        selectedHour = cal.get(PersianCalendar.HOUR_OF_DAY);
        selectedMinute = cal.get(PersianCalendar.MINUTE);

        datePicker = PersianDatePickerDialog.newInstance(this, selectedYear, selectedMonth, selectedDay, false, PersianDatePickerDialog.PERSIAN);
        timePicker = PersianTimePickerDialog.newInstance(this, selectedHour, selectedMinute, false, false, PersianTimePickerDialog.PERSIAN);
        datePicker.setYearRange(1390, 1400);
        datePicker.setCloseOnSingleTapDay(true);
        timePicker.setCloseOnSingleTapMinute(true);

        dateLayout = (LinearLayout) findViewById(R.id.dateLayout);
        dateLayout.setOnClickListener(this);
        yearMonthTextview = (TextView) findViewById(R.id.year_month_textview);
        dayOfMonthTextview = (TextView) findViewById(R.id.day_of_month_textview);
        weekdayTextview = (TextView) findViewById(R.id.weekday_textview);

        timeText = (TextView) findViewById(R.id.time_textview);
        timeText.setOnClickListener(this);

        amPmTextview = (TextView) findViewById(R.id.am_pm_textview);
        amPmTextview.setOnClickListener(this);

        //account spinner setting
        getAccountList();
        accountSpinner=(Spinner)findViewById(R.id.account_spinner);
        //todo change typeface of adapter to persiantypeface
//        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,accountsList);
        MySpinnerAdapter dataAdapter=new MySpinnerAdapter(this,android.R.layout.simple_spinner_item,accountsList);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        accountSpinner.setAdapter(dataAdapter);



        if (savedInstanceState != null)
        {
            PersianDatePickerDialog pdpd = (PersianDatePickerDialog) getSupportFragmentManager().findFragmentByTag(PERSIAN_DATEPICKER_TAG);
            if (pdpd != null) {
                pdpd.setOnDateSetListener(this);
            }

            PersianTimePickerDialog ptpd = (PersianTimePickerDialog) getSupportFragmentManager().findFragmentByTag(PERSIAN_TIMEPICKER_TAG);
            if (ptpd != null) {
                ptpd.setOnTimeSetListener(this);
            }
        }

        descp = (EditText)findViewById(R.id.descriptionText);
        descp.setTypeface(TimelineActivity.persianTypeface);

        Log.e("debug","id before intent extra"+id);
        Intent intent = getIntent();
        if(intent != null)
        {
            Bundle extras = intent.getExtras();
            if(extras != null)
            {
                id = extras.getString(AddPage1.TRANSACTION_ID_KEY, "null");
                if(!id.equals("null"))
                {
                    Log.e("debug","id in intent extra"+id);
                    Cursor c=LocalDBServices.getTransactionFromID(id);
                    c.moveToFirst();

                    amount = c.getDouble(c.getColumnIndexOrThrow(TransactionEntry.COLUMN_NAME_AMOUNT));
                    category_index = c.getInt(c.getColumnIndexOrThrow(TransactionEntry.COLUMN_NAME_CATEGORY));
                    isExpense = c.getInt(c.getColumnIndexOrThrow(TransactionEntry.COLUMN_NAME_IS_EXPENSE))==1? true: false;
                    String dateTime = c.getString(c.getColumnIndexOrThrow(TransactionEntry.COLUMN_NAME_DATE_TIME));


                    //TODO add new selected tags to usertags

                    int year = Integer.parseInt(dateTime.substring(0, 4));
                    int month = Integer.parseInt(dateTime.substring(4, 6));
                    int day = Integer.parseInt(dateTime.substring(6, 8));
                    int hour = Integer.parseInt(dateTime.substring(8, 10));
                    int minute = Integer.parseInt(dateTime.substring(10, 12));

                    datePicker.setInitialDate(year, month, day);
                    timePicker.setInitialTime(hour, minute);

                    String desctription = c.getString(c.getColumnIndexOrThrow(TransactionEntry.COLUMN_NAME_DESCRIPTION));
                    descp.setText(desctription);

                    selectedYear = year;
                    selectedMonth = month;
                    selectedDay = day;
                    calendar = new PersianCalendar(year, month, day);
                    selectedWeekday = calendar.get(PersianCalendar.DAY_OF_WEEK);

                    selectedHour = hour;
                    selectedMinute = minute;

                    accountName=c.getString(c.getColumnIndexOrThrow(TransactionEntry.COLUMN_NAME_ACCOUNT_NAME));
                    Log.e("account name:",accountName);
                    if(accountName!=null&&accountName!=""){
                        int position=((ArrayAdapter)accountSpinner.getAdapter()).getPosition(accountName);
//                        Log.e("position:",position+"");
                        accountSpinner.setSelection(position);
                    }




//                    query = "SELECT "+TagsEntry.COLUMN_NAME_TAG +
//                            " FROM " +TagsEntry.TABLE_NAME +
//                            " WHERE "+TagsEntry.COLUMN_NAME_TRANSACTION_ID + "=" + id;
//                    c = db.rawQuery(query, null);
                    c.close();

                    Cursor c2= LocalDBServices.getTagsFromID(id);
                    c2.moveToFirst();

                    if(c2.getCount() != 0)
                    {
                        do
                        {
                            selectedTags.add(c2.getString(c2.getColumnIndexOrThrow(TagsEntry.COLUMN_NAME_TAG)));
                        }while(c2.moveToNext());
                    }
                    c2.close();
                }
            }
        }

        category_index = intent.getIntExtra(CATEGORY_KEY, category_index);
        amount = intent.getDoubleExtra(AMOUNT_KEY, amount);
        isExpense = intent.getBooleanExtra(IS_EXPENSE_KEY, isExpense);

        TextView textViewAmount=(TextView)findViewById(R.id.textViewAmount1);
        textViewAmount.setTypeface(TimelineActivity.persianTypeface);
        textViewAmount.setText(Utils.toPersianNumbers(Currency.separateThousand("" + amount)));

        ImageView categoryImageView=(ImageView)findViewById(R.id.categoryImageView);
        if(isExpense)
            categoryImageView.setImageResource(Category.getExpenseIconID(category_index));
        else
            categoryImageView.setImageResource(Category.getIncomeIconID(category_index));

        LinearLayout linearLayout=(LinearLayout)findViewById(R.id.currency_layout);
        Currency.setCurrencyLayout(linearLayout,this,getResources().getColor(R.color.black),TimelineActivity.persianTypeface,Currency.LARGE_TEXT_SIZE);

        pager = (ViewPager) findViewById(R.id.viewpagerPage2);
        title = (CirclePageIndicator) findViewById(R.id.titlesPage2);

        pagerAdapter = new ViewPagerAdapterLayout();


        Display  display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        width = size.x;
        height = size.y;

        title.setFillColor(0xff484848);
        title.setStrokeColor(0xff303030);
        pager.setAdapter(pagerAdapter);
        title.setViewPager(pager);


        actv = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView1);
        actv.setTypeface(TimelineActivity.persianTypeface);
        updateSuggestionAdapter();

        ViewTreeObserver viewTreeObserver = actv.getViewTreeObserver();
        viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                actv.setDropDownHeight(actv.getHeight() * 4);
            }
        });
        actv.setThreshold(1);

        actv.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView exampleView, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    String tmpTag = actv.getText().toString();
                    tmpTag=removeSpaces(tmpTag);
                    if(!tmpTag.equals("")){
                        makeTagLayout(tmpTag,true);
                        selectedTags.add(tmpTag);
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
                actv.setText("");
                if(userTagsToShow.contains(input))
                    userTagsToShow.remove(input);
                updateSuggestionAdapter();
                updatePageAdaptorButtons();

            }
        });



        int tmpHour = selectedHour;
        String amPm = "قبل از ظهر";
        if(selectedHour > 11)
        {
            amPm = "بعد از ظهر";
            tmpHour %= 12;
        }

        if(tmpHour == 0)
            tmpHour = 12;




        yearMonthTextview.setText(PersianCalendar.months[selectedMonth]+" "+Utils.toPersianNumbers(selectedYear+""));
        weekdayTextview.setText(PersianCalendar.weekdayFullNames[selectedWeekday]);
        dayOfMonthTextview.setText(Utils.toPersianNumbers(selectedDay+""));

        timeText.setText(Utils.toPersianNumbers(tmpHour+":"+String.format("%02d", selectedMinute)));
        amPmTextview.setText(amPm);

        timeText.setTypeface(TimelineActivity.persianTypeface);
        yearMonthTextview.setTypeface(TimelineActivity.persianTypeface);
        dayOfMonthTextview.setTypeface(TimelineActivity.persianTypeface);
        weekdayTextview.setTypeface(TimelineActivity.persianTypeface);
        amPmTextview.setTypeface(TimelineActivity.persianTypeface);


//        userTags=new ArrayList<String>(Arrays.asList("تاکسی","بی آر تی","صابون","تئاتر","سینما","قلهک","بنزین","پول تو جیبی","کتاب","قبض برق","صدقات"));
        userTags=LocalDBServices.getPopularTags(getBaseContext());
        userTagsToShow.addAll(userTags);
        addMostUsedTags();
        addSelectedTagsToPageAdaptor();
        updateSuggestionAdapter();



    }



    public void addView(View newPage) {
        int pageIndex = pagerAdapter.addView(newPage);
        pager.setCurrentItem(pageIndex, true);
    }

    //-----------------------------------------------------------------------------
    // Here's what the app should do to remove a view from the ViewPager.
    public void removeView(View defunctPage) {
        int pageIndex = pagerAdapter.removeView(pager, defunctPage);
        // You might want to choose what page to display, if the current page was "defunctPage".
        if (pageIndex == pagerAdapter.getCount())
            pageIndex--;
        pager.setCurrentItem(pageIndex);
    }

    //-----------------------------------------------------------------------------
    // Here's what the app should do to get the currently displayed page.
    public View getCurrentPage() {
        return pagerAdapter.getView(pager.getCurrentItem());
    }

    //-----------------------------------------------------------------------------
    // Here's what the app should do to set the currently displayed page.  "pageToShow" must
    // currently be in the adapter, or this will crash.
    public void setCurrentPage(View pageToShow) {
        pager.setCurrentItem(pagerAdapter.getItemPosition(pageToShow), true);
    }

    private String removeSpaces(String inputString){
//        String outPutString=inputString;
        while (inputString.startsWith(" "))
            inputString=inputString.substring(1);
        while (inputString.endsWith(" "))
            inputString=inputString.substring(0,inputString.length()-1);

        return inputString;
    }


    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if(view.getId()==R.id.dateLayout) {
            datePicker.show(getSupportFragmentManager(), PERSIAN_DATEPICKER_TAG);
        }
        else if(view.getId()==R.id.time_textview){
            timePicker.show(getSupportFragmentManager(), PERSIAN_TIMEPICKER_TAG);
        }

        return true;
    }


    @SuppressWarnings("deprecation")
    public void putChild(int widthSize, int hightSize) {

        mVerticalSpacing=5;
        mHorizontalSpacing=20;
        int tag_id=0;
        boolean has_layout=false;
        int line_count = 1;

        for(AbsoluteLayout absoluteLayout:tag_layouts) {
            absoluteLayout.removeAllViews();
            pagerAdapter.removeView(pager, absoluteLayout);
        }
        tag_layouts=new ArrayList<AbsoluteLayout>();

        title.notifyDataSetChanged();
        pagerAdapter.notifyDataSetChanged();

        AbsoluteLayout al = new AbsoluteLayout(this);
        al.setTag(tag_id);

        int width = 0;
        int height = al.getPaddingTop();
        int currentWidth = al.getPaddingLeft();
        int currentHeight = 0;

        final int count = showedTagButton.size();


        for (int i = 0; i < count; i++) {
            View child = showedTagButton.get(i);
            showedTagButton.get(i).measure(0, 0);
            AbsoluteLayout.LayoutParams lp = new AbsoluteLayout.LayoutParams(child.getMeasuredWidth(), child.getMeasuredHeight(), 5, 5);


            if (currentWidth + child.getMeasuredWidth() > widthSize) {
                line_count++;

                height += currentHeight + mVerticalSpacing;
                currentHeight = 0;
                if (currentWidth > width) width = currentWidth;
                currentWidth = al.getPaddingLeft();
            }

            int spacing = mHorizontalSpacing;

            if(line_count>2){
                al=new AbsoluteLayout(this);

                tag_id++;
                al.setTag(tag_id);
                has_layout=false;
                line_count=1;
                height=0;
            }

            lp.x = currentWidth;
            lp.y = height;

            showedTagButton.get(i).setLayoutParams(lp);

            al.addView(showedTagButton.get(i));

            currentWidth += child.getMeasuredWidth() + spacing;
            int childHeight = child.getMeasuredHeight();
            if (childHeight > currentHeight) currentHeight = childHeight;


            for(int j=0;j<tag_layouts.size();j++)
                if(tag_layouts.get(j).getTag()==al.getTag())
                {has_layout=true;
                    break;
                }
            if(has_layout==false)
            {
                tag_layouts.add(al);
            }
        }

        for(AbsoluteLayout absoluteLayout:tag_layouts){
            pagerAdapter.addView(absoluteLayout);
        }
        title.notifyDataSetChanged();
        pagerAdapter.notifyDataSetChanged();
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
                TagLayout tagLayout = new TagLayout(this, input_string, true);
                tagLayout.setSelected(true);
                tagLayout.rightButton.setOnClickListener(this);
                tagLayout.leftButton.setOnClickListener(this);
                tagLayout.middleButton.setOnClickListener(this);
                showedTagButton.add(0, tagLayout);
            }
            else {
                TagLayout tagLayout = new TagLayout(this, input_string, false);
                tagLayout.setSelected(false);
                tagLayout.rightButton.setOnClickListener(this);
                tagLayout.leftButton.setOnClickListener(this);
                tagLayout.middleButton.setOnClickListener(this);
                showedTagButton.add(0, tagLayout);
            }
        }
        else {
            showedTagButton.get(hasButton).setSelected(shouldSelected);
        }
    }

    private void addMostUsedTags(){
        ArrayList<String> mostUsedTags=new ArrayList<String>();
        // !!!!!   an algorithm to find most used tags and add to mostUsedTags
//        mostUsedTags=new ArrayList<String>(Arrays.asList("تاکسی","بی آر تی","صابون","تئاتر","سینما","قلهک","بنزین"));
        mostUsedTags=LocalDBServices.getPopularTags(getBaseContext());

        for(String s:mostUsedTags){
            //            if(userTagsToShow.contains(s))
            //        userTagsToShow.remove(s);
            makeTagLayout(s,false);
        }
        updateSuggestionAdapter();
        updatePageAdaptorButtons();

    }
    private void updatePageAdaptorButtons() {
        //        ArrayList<TagLayout> tmp=new ArrayList<TagLayout>();
        //        tmp.addAll(showedTagButton);
        for(TagLayout tmp:showedTagButton){
            //            System.out.println(tmp.tagString+" has this parrent: "+tmp.getParent());
            if(tmp.getParent()!=null)
                ((AbsoluteLayout)tmp.getParent()).removeAllViews();
        }
        putChild(width, height);
    }

    private void addSelectedTagsToPageAdaptor() {
        for(String s:selectedTags){
            makeTagLayout(s,true);
            if(userTagsToShow.contains(s))
                userTagsToShow.remove(s);
            //            System.out.println("this is a selected tag from last time : "+s);
        }
        updateSuggestionAdapter();
        updatePageAdaptorButtons();


    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private void updateSuggestionAdapter() {
        actv_adapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,userTagsToShow);
        actv.setAdapter(actv_adapter);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.add_page2_actions, menu);
        final MenuItem item = menu.findItem(R.id.done_button);
        TextView actionBarTextView = (TextView) item.getActionView();
        actionBarTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                getActionBar().getHeight()-actionBarTextView.getPaddingTop()-actionBarTextView.getPaddingBottom()-24);
        actionBarTextView.setTypeface(TimelineActivity.persianBoldTypeface);
        actionBarTextView.setText(R.string.new_transaction_done);
        actionBarTextView.setTextColor(getResources().getColor(R.color.done_button));
        actionBarTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_action_accept, 0);
        actionBarTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                onOptionsItemSelected(item);
            }
        });

        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public void onBackPressed()
    {
        setResult(BACK_OR_UP);
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        if(item.getItemId()==R.id.done_button)
        {
            if(accountSpinner.getSelectedItem()==null ||accountSpinner.getSelectedItem()=="" ||accountSpinner.getSelectedItem()=="نوع حساب"){
                Toast.makeText(this,"لطفا نوع حساب را مشخص کنید",Toast.LENGTH_SHORT).show();
            }
            else {
                addTransationToDatabase();
                setResult(DONE);
                finish();
            }
        }
        else if(item.getItemId() == android.R.id.home)
        {
            setResult(BACK_OR_UP);
            finish();
            overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
        }
        return super.onOptionsItemSelected(item);
    }

    public void addTransationToDatabase(){
//        SQLiteDatabase db = trHelper.getWritableDatabase();

        PersianDate date = new PersianDate((short)selectedDay, (short)selectedMonth, (short)selectedYear, PersianCalendar.weekdayFullNames[selectedWeekday]);
        Time time = new Time((short)selectedHour, (short)selectedMinute);
        String dateTime = date.getSTDString()+time.getSTDString()+"00";

        ContentValues values = new ContentValues();
        values.put(TransactionEntry.COLUMN_NAME_DATE_TIME, dateTime);
        values.put(TransactionEntry.COLUMN_NAME_AMOUNT, amount);
        values.put(TransactionEntry.COLUMN_NAME_IS_EXPENSE, isExpense);
        description = descp.getText().toString();
        accountName=accountSpinner.getSelectedItem().toString();
        values.put(TransactionEntry.COLUMN_NAME_CATEGORY, category_index);
        values.put(TransactionEntry.COLUMN_NAME_DESCRIPTION, description);
        values.put(TransactionEntry.COLUMN_NAME_ACCOUNT_NAME,accountName);
        if(id.equals("null"))
        {
//            long transactionID;

//            transactionID = db.insert(TransactionEntry.TABLE_NAME, null, values);

            Log.e("debug","newTransactionCalled : "+id);
            LocalDBServices.addNewTransaction(this,dateTime,amount,isExpense,accountSpinner.getSelectedItem().toString(),category_index,selectedTags,description, true);
//
//            for(String tag: selectedTags)
//            {
//                values = new ContentValues();
//                values.put(TagsEntry.COLUMN_NAME_TRANSACTION_ID, transactionID);
//                values.put(TagsEntry.COLUMN_NAME_TAG, tag);
//                db.insert(TagsEntry.TABLE_NAME, null, values);
//            }
        }
        else
        {
            Log.e("debug","editHandyCalled : "+id);
            LocalDBServices.editHandyTransaction(this,dateTime,amount,isExpense,accountSpinner.getSelectedItem().toString(),category_index,id,selectedTags,description);
//            String selection = TransactionEntry._ID + " LIKE ?";
//            String[] selectionArgs = { String.valueOf(id) };
//
//            db.update(
//                    TransactionEntry.TABLE_NAME,
//                    values,
//                    selection,
//                    selectionArgs);
//
//            String selectionTags = TagsEntry.COLUMN_NAME_TRANSACTION_ID + " LIKE ?";
//            String[] selectionArgsTags = { String.valueOf(id) };
//            db.delete(TagsEntry.TABLE_NAME, selectionTags, selectionArgsTags);
//
//            //TODO distinct
//            for(String tag: selectedTags)
//            {
//                values = new ContentValues();
//                values.put(TagsEntry.COLUMN_NAME_TRANSACTION_ID, id);
//                values.put(TagsEntry.COLUMN_NAME_TAG, tag);
//                db.insert(TagsEntry.TABLE_NAME, null, values);
//            }
        }
    }

    public void getAccountList(){

        accountsList=new ArrayList<String>();
        Cursor c=LocalDBServices.getHandyAccountList(this);
        c.moveToFirst();
        if(c.getCount() != 0)
        {
            do
            {
                accountsList.add(c.getString(c.getColumnIndexOrThrow(TransactionsContract.Accounts.COLUMN_NAME_Account_Name)));
            }while(c.moveToNext());
        }
        c.close();
    }

    @Override
    public void onDateSet(PersianDatePickerDialog datePickerDialog, int year, int month, int day)
    {
        PersianCalendar tmpCal = new PersianCalendar(year, month, day);
        selectedYear = year;
        selectedMonth = month;
        selectedDay = day;
        selectedWeekday = tmpCal.get(PersianCalendar.DAY_OF_WEEK);

        yearMonthTextview.setText(PersianCalendar.months[month]+" "+Utils.toPersianNumbers(year+""));
        weekdayTextview.setText(PersianCalendar.weekdayFullNames[selectedWeekday]);
        dayOfMonthTextview.setText(Utils.toPersianNumbers(day+""));
    }

    @Override
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute)
    {
        selectedHour = hourOfDay;
        selectedMinute = minute;
        String amPm = "قبل از ظهر";
        if(hourOfDay > 11)
        {
            amPm = "بعد از ظهر";
            hourOfDay %= 12;
            if(hourOfDay == 0)
                hourOfDay = 12;
        }
        timeText.setText(Utils.toPersianNumbers(hourOfDay+":"+String.format("%02d", minute)));
        amPmTextview.setText(amPm);
    }


    @Override
    public void onClick(View view) {
        int pageCount=pager.getCurrentItem();

        if(view.getId()==R.id.dateLayout) {
            datePicker.show(getSupportFragmentManager(), PERSIAN_DATEPICKER_TAG);
        }
        else if(view.getId()==R.id.time_textview || view.getId() == R.id.am_pm_textview){
            timePicker.show(getSupportFragmentManager(), PERSIAN_TIMEPICKER_TAG);
        }

        if(view.getClass()==ImageButton.class||view.getClass()==Button.class){
            int index=-1;
            for(int i=0;i<showedTagButton.size();i++){
                if(view.getTag()==showedTagButton.get(i).getTag()){
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

    private static class MySpinnerAdapter extends ArrayAdapter<String> {
        // Initialise custom font, for example:


        // (In reality I used a manager which caches the Typeface objects)
        // Typeface font = FontManager.getInstance().getFont(getContext(), BLAMBOT);

        private MySpinnerAdapter(Context context, int resource, List<String> items) {
            super(context, resource, items);
        }

        // Affects default (closed) state of the spinner
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView view = (TextView) super.getView(position, convertView, parent);
            view.setTypeface(TimelineActivity.persianTypeface);
            return view;
        }

        // Affects opened state of the spinner
        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            TextView view = (TextView) super.getDropDownView(position, convertView, parent);
            view.setTypeface(TimelineActivity.persianTypeface);
            return view;
        }
    }
}
