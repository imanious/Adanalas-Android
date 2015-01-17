package ir.abplus.adanalas.AddEditTransactions;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.*;
import android.widget.*;
import com.fourmob.datetimepicker.date.PersianCalendar;
import com.viewpagerindicator.CirclePageIndicator;
import ir.abplus.adanalas.Libraries.*;
import ir.abplus.adanalas.Libraries.TransactionsContract.TransactionEntry;
import ir.abplus.adanalas.R;
import ir.abplus.adanalas.Timeline.TimelineActivity;
import ir.abplus.adanalas.databaseConnections.LocalDBServices;

import java.util.ArrayList;

public class EditTransactionPage extends Activity implements View.OnClickListener, View.OnLongClickListener {


    private static int category_index;
    private String id = "null";
    private ArrayList<String> selectedTags = new ArrayList<String>();




    private ImageButton[] category_expense_buttons;
    private ImageButton[] category_income_buttons;
    private int widthsize;
    private int heightsize;
    TextView amountTextview;
    TextView dateTextView;



    private int[] category_income_background;
    private int[] category_expense_backgroud;
    private boolean isExpense=true;
    private TextView descriptionTextView;

    int tagLayoutHeight=0;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);


//        TagAdapterLayout.setParams(selectedTags,widthsize);

        setContentView(R.layout.edit_transaction_page);

//        FrameLayout rootLayout = (FrameLayout)findViewById(android.R.id.content);
//        View.inflate(this, R.layout.overlay_layout, rootLayout);
        getActionBar().setDisplayHomeAsUpEnabled(true);



        LinearLayout parentLayout= (LinearLayout) findViewById(R.id.parent_layout);
//        Currency.setCurrencyLayout(currency_layout,this,getResources().getColor(R.color.black),TimelineActivity.persianTypeface, Currency.LARGE_TEXT_SIZE);


        category_index= Category.EXPENSE_UNCATEGORIZED;



        amountTextview=(TextView)findViewById(R.id.amount_text);
        amountTextview.setTypeface(TimelineActivity.persianTypeface);
        descriptionTextView=(TextView)findViewById(R.id.description_text);
        dateTextView=(TextView)findViewById(R.id.date_text);
        descriptionTextView.setTypeface(TimelineActivity.persianTypeface);
        dateTextView.setTypeface(TimelineActivity.persianTypeface);
        final LinearLayout tagLayout=(LinearLayout)findViewById(R.id.tags_layout);


        Display  display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        widthsize = size.x;
        heightsize=size.y;


        category_expense_buttons=new ImageButton[14];
        for(int i=0;i<14;i++) {
            category_expense_buttons[i] = new ImageButton(this);
            category_expense_buttons[i].setBackground(null);
        }
        category_expense_buttons[0].setImageDrawable(getResources().getDrawable(Category.getExpenseIconID(0)));
        category_expense_buttons[1].setImageDrawable(getResources().getDrawable(Category.getExpenseIconID(1)));
        category_expense_buttons[2].setImageDrawable(getResources().getDrawable(Category.getExpenseIconID(2)));
        category_expense_buttons[3].setImageDrawable(getResources().getDrawable(Category.getExpenseIconID(3)));
        category_expense_buttons[4].setImageDrawable(getResources().getDrawable(Category.getExpenseIconID(4)));
        category_expense_buttons[5].setImageDrawable(getResources().getDrawable(Category.getExpenseIconID(5)));
        category_expense_buttons[6].setImageDrawable(getResources().getDrawable(Category.getExpenseIconID(6)));
        category_expense_buttons[7].setImageDrawable(getResources().getDrawable(Category.getExpenseIconID(7)));
        category_expense_buttons[8].setImageDrawable(getResources().getDrawable(Category.getExpenseIconID(8)));
        category_expense_buttons[9].setImageDrawable(getResources().getDrawable(Category.getExpenseIconID(9)));
        category_expense_buttons[10].setImageDrawable(getResources().getDrawable(Category.getExpenseIconID(10)));
        category_expense_buttons[11].setImageDrawable(getResources().getDrawable(Category.getExpenseIconID(11)));
        category_expense_buttons[12].setImageDrawable(getResources().getDrawable(Category.getExpenseIconID(12)));
        category_expense_buttons[13].setImageDrawable(getResources().getDrawable(Category.getExpenseIconID(13)));

        for(int i=0;i<14;i++) {
            category_expense_buttons[i].setOnClickListener(this);
            category_expense_buttons[i].setOnLongClickListener(this);
        }


        category_income_buttons=new ImageButton[11];

        for(int i=0;i<11;i++){
            category_income_buttons[i]=new ImageButton(this);
            category_income_buttons[i].setBackground(null);
        }

        category_income_buttons[0].setImageDrawable(getResources().getDrawable(Category.getIncomeIconID(0)));
        category_income_buttons[1].setImageDrawable(getResources().getDrawable(Category.getIncomeIconID(1)));
        category_income_buttons[2].setImageDrawable(getResources().getDrawable(Category.getIncomeIconID(2)));
        category_income_buttons[3].setImageDrawable(getResources().getDrawable(Category.getIncomeIconID(3)));
        category_income_buttons[4].setImageDrawable(getResources().getDrawable(Category.getIncomeIconID(4)));
        category_income_buttons[5].setImageDrawable(getResources().getDrawable(Category.getIncomeIconID(5)));
        category_income_buttons[6].setImageDrawable(getResources().getDrawable(Category.getIncomeIconID(6)));
        category_income_buttons[7].setImageDrawable(getResources().getDrawable(Category.getIncomeIconID(7)));
        category_income_buttons[8].setImageDrawable(getResources().getDrawable(Category.getIncomeIconID(8)));
        category_income_buttons[9].setImageDrawable(getResources().getDrawable(Category.getIncomeIconID(9)));
        category_income_buttons[10].setImageDrawable(getResources().getDrawable(Category.getIncomeIconID(10)));




        for(int i=0;i<11;i++){
            category_income_buttons[i].setOnClickListener(this);
            category_income_buttons[i].setOnLongClickListener(this);
        }



        category_income_background=new int[11];
        category_income_background[0]=R.drawable.frame1;
        category_income_background[1]=R.drawable.frame4;
        category_income_background[2]=R.drawable.frame3;
        category_income_background[3]=R.drawable.frame5;
        category_income_background[4]=R.drawable.frame6;
        category_income_background[5]=R.drawable.frame7;
        category_income_background[6]=R.drawable.frame8;
        category_income_background[7]=R.drawable.frame9;
        category_income_background[8]=R.drawable.frame10;
        category_income_background[9]=R.drawable.frame12;
        category_income_background[10]=R.drawable.frame13;

        category_expense_backgroud=new int[14];
        category_expense_backgroud[0]=R.drawable.frame4;
        category_expense_backgroud[1]=R.drawable.frame7;
        category_expense_backgroud[2]=R.drawable.frame2;
        category_expense_backgroud[3]=R.drawable.frame5;
        category_expense_backgroud[4]=R.drawable.frame1;
        category_expense_backgroud[5]=R.drawable.frame13;
        category_expense_backgroud[6]=R.drawable.frame3;
        category_expense_backgroud[7]=R.drawable.frame8;
        category_expense_backgroud[8]=R.drawable.frame12;
        category_expense_backgroud[9]=R.drawable.frame11;
        category_expense_backgroud[10]=R.drawable.frame6;
        category_expense_backgroud[11]=R.drawable.frame9;
        category_expense_backgroud[12]=R.drawable.frame14;
        category_expense_backgroud[13]=R.drawable.frame10;


//        pager = (ViewPager) findViewById (R.id.viewpager);
//        title = (CirclePageIndicator) findViewById(R.id.titles);
//        title.setFillColor(0xff484848);
//        title.setStrokeColor(0xff303030);

//        pager2 = (ViewPager) findViewById (R.id.viewpager2);
//        title2 = (CirclePageIndicator) findViewById(R.id.titles2);
//        title2.setFillColor(0xff484848);
//        title2.setStrokeColor(0xff303030);


//        pager.setAdapter (pagerAdapter);
//        title.setViewPager(pager);
//
//        pager2.setAdapter (pagerAdapter2);
//        title2.setViewPager(pager2);








//        pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
//            @Override
//            public void onPageScrolled(int i, float v, int i2) {
//                System.out.println(i+"  "+i2+"  "+v);
//                if(i2==0&&i==0)
//                    System.out.println("this is first");
//                if(i2==1&&i==1)
//                    System.out.println("this is last");
//            }
//
//            @Override
//            public void onPageSelected(int i) {
//
//            }
//
//            @Override
//            public void onPageScrollStateChanged(int i) {
//
//            }
//        });

        Intent intent = getIntent();
        if(intent != null)
        {
            Bundle extras = intent.getExtras();
            if(extras != null)
            {
                id = extras.getString(AddPage1.TRANSACTION_ID_KEY, "null");
                if(!id.equals("null"))
                {
//                    SQLiteDatabase db = trHelper.getReadableDatabase();
//                    String query;
                    Cursor c;

//                    query = "SELECT *" +
//                            " FROM "+TransactionEntry.TABLE_NAME +
//                            " WHERE "+TransactionEntry._ID + "=" + id;
//                    c = db.rawQuery(query, null);
                    c=LocalDBServices.getTransactionFromID(id);
                    c.moveToFirst();

                    double amount = c.getDouble(c.getColumnIndexOrThrow(TransactionEntry.COLUMN_NAME_AMOUNT));
                    int category = c.getInt(c.getColumnIndexOrThrow(TransactionEntry.COLUMN_NAME_CATEGORY));
                    isExpense = c.getInt(c.getColumnIndexOrThrow(TransactionEntry.COLUMN_NAME_IS_EXPENSE))==0? false: true;
                    String description=c.getString(c.getColumnIndexOrThrow(TransactionEntry.COLUMN_NAME_DESCRIPTION));
                    String dateTime=c.getString(c.getColumnIndexOrThrow(TransactionEntry.COLUMN_NAME_DATE_TIME));
                    c.close();


                    descriptionTextView.setText("توضیحات: "+description);
                    descriptionTextView.setTextSize(14);

                    amountTextview.setText("مبلغ: "+Currency.getStdAmount(amount)+" "+Currency.getCurrencyString());
                    amountTextview.setTextSize(16);
//                    accountNameTextView.setText(accountName);

                    int year = Integer.parseInt(dateTime.substring(0, 4));
                    int month = Integer.parseInt(dateTime.substring(4, 6));
                    int day = Integer.parseInt(dateTime.substring(6, 8));
                    int hour = Integer.parseInt(dateTime.substring(8, 10));
                    int minute = Integer.parseInt(dateTime.substring(10, 12));

                    PersianCalendar tmpCal = new PersianCalendar(year, month, day);
                    PersianDate date = new PersianDate((short)day, (short)(month+1), (short)year, PersianCalendar.weekdayFullNames[tmpCal.get(PersianCalendar.DAY_OF_WEEK)]);
                    Time time = new Time((short)hour, (short)minute);


                    dateTextView.setText(date.toString()+"     ساعت: "+time);
//					System.out.println("before:  "+cost_string);
//                    if(cost_string.charAt(cost_string.length()-1) == '0')
//                    {
//                        cost_string = cost_string.substring(0, cost_string.length()-1);
//                        if(cost_string.charAt(cost_string.length()-1) == '0')
//                        {
//                            cost_string = cost_string.substring(0, cost_string.length()-2);
//                        }
//                    }

//                    String std = Currency.getStandardAmount(amount);
//                    if(std.contains(Currency.decimalPoint))
//                    {
//                        if(std.charAt(std.length()-1) == '0')
//                            cost_text.setText(cost_text.getText().subSequence(0, cost_text.getText().length() - 1));
//                    }
//					System.out.println("after:  "+cost_string);
//                    setCurrentState();
//                    System.out.println("category : "+category);
//                    if(!isExpense)
//                        tabHost.setCurrentTabByTag("tag2");
                    category_index = category;
                    if(isExpense){
                        if(!(category_index<0||category_index>=Category.EXPENSE_UNCATEGORIZED)) {
                            category_expense_buttons[category_index].setSelected(true);
                            category_expense_buttons[category_index].setBackground(getResources().getDrawable(category_expense_backgroud[category_index]));
                        }
                    }
                    else{
                        if(!(category_index<0||category_index>=Category.INCOME_UNCATEGORIZED)) {
                            category_income_buttons[category_index].setSelected(true);
                            category_income_buttons[category_index].setBackground(getResources().getDrawable(category_income_background[category_index]));
                        }
                    }

//                    query = "SELECT "+ TransactionsContract.TagsEntry.COLUMN_NAME_TAG +
//                            " FROM " + TransactionsContract.TagsEntry.TABLE_NAME +
//                            " WHERE "+ TransactionsContract.TagsEntry.COLUMN_NAME_TRANSACTION_ID + "=" + id;
//                    c = db.rawQuery(query, null);
                    Cursor c2=LocalDBServices.getTagsFromID(id);
                    c2.moveToFirst();

                    if(c2.getCount() != 0)
                    {
                        do
                        {
                            selectedTags.add(c2.getString(c2.getColumnIndexOrThrow(TransactionsContract.TagsEntry.COLUMN_NAME_TAG)));
                        }while(c2.moveToNext());
                    }
                    c2.close();
                }

            }



        }



        LinearLayout ll=(LinearLayout)findViewById(R.id.category_layout);
//        put2(widthsize);
        if(isExpense)
            putCategories(widthsize,300,ll,category_expense_buttons);
        else
            putCategories(widthsize,300,ll,category_income_buttons);

        tagLayoutHeight= ((int) (heightsize * (((LinearLayout.LayoutParams) tagLayout.getLayoutParams()).weight / parentLayout.getWeightSum())));
//        tagLayoutHeight=((LinearLayout.LayoutParams) tagLayout.getLayoutParams()).weight) /(((LinearLayout.LayoutParams) tagLayout.getLayoutParams()).weight)*;
        System.out.println(heightsize+"^^^"+tagLayoutHeight);
        TagAdapterLayout tagAdapterLayout=new TagAdapterLayout(this,selectedTags,tagLayoutHeight);

        tagLayout.addView(tagAdapterLayout);
//        tagLayout.setBackgroundColor(getResources().getColor(R.color.blue));

    }


    @Override
    public void onClick(View view)
    {

        boolean is_cat=false;
        for(int j = 0; j < 11; j++)
            if(view == category_income_buttons[j])
            {
                Toast.makeText(this,"برای ذخیره سازی تراکنش نگه دارید",Toast.LENGTH_SHORT).show();
                is_cat=true;
                if(category_income_buttons[j].isSelected())
                {}
                else{
                    category_index=j;
                    for(int k = 0; k < Category.INCOME_SIZE-1; k++){
                        category_income_buttons[k].setSelected(false);
                        category_income_buttons[k].setBackground(null);
                    }
                    category_income_buttons[j].setSelected(true);
                    category_income_buttons[j].setBackground(getResources().getDrawable(category_income_background[j]));
                }

            }
        for(int j = 0; j < 14; j++)
            if(view == category_expense_buttons[j])
            {
                Toast.makeText(this,"برای ذخیره سازی تراکنش نگه دارید",Toast.LENGTH_SHORT).show();

                is_cat=true;
                if(category_expense_buttons[j].isSelected())
                {}
                else{
                    category_index=j;
                    for(int k = 0; k < Category.EXPENSE_SIZE-1; k++){
                        category_expense_buttons[k].setSelected(false);
                        category_expense_buttons[k].setBackground(null);
                    }
                    category_expense_buttons[j].setSelected(true);
                    category_expense_buttons[j].setBackground(getResources().getDrawable(category_expense_backgroud[j]));
                }

            }
        if(!is_cat)
        {
            Log.e("error","the button clicked is not category!");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.edit_transactions_type2_actions, menu);
        final MenuItem item = menu.findItem(R.id.next_button);
        TextView actionBarTextView = (TextView) item.getActionView();
        actionBarTextView.setTypeface(TimelineActivity.persianBoldTypeface);
        actionBarTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                getActionBar().getHeight()-actionBarTextView.getPaddingTop()-actionBarTextView.getPaddingBottom()-24);
        actionBarTextView.setText(R.string.new_transaction_done);
        actionBarTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_action_next_item, 0);
        actionBarTextView.setTextColor(getResources().getColor(R.color.next_button));
        actionBarTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                onOptionsItemSelected(item);
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
//        double amountvalue = Double.parseDouble(cost_string);
        if(item.getItemId()==R.id.next_button) {
//            if(amountvalue==0)
//                Toast.makeText(this,"لطفا مبلغ تراکنش را وارد کنید", Toast.LENGTH_SHORT).show();
            if (category_index == Category.EXPENSE_UNCATEGORIZED || category_index == Category.INCOME_UNCATEGORIZED)
                Toast.makeText(this, "لطفا دسته بندی تراکنش را معلوم کنید", Toast.LENGTH_SHORT).show();
            else {
                addTransationToDatabase();
                finish();
//                Intent myIntent = new Intent(EditTransactionPage.this, AddPage2.class);
//                if (id == Integer.MIN_VALUE) {
//				System.out.println("intent started");
//                    myIntent.putExtra(AddPage2.AMOUNT_KEY, amountvalue);
//                    myIntent.putExtra(AddPage2.CATEGORY_KEY, category_index);
//                    myIntent.putExtra(AddPage2.IS_EXPENSE_KEY, isExpense);
//
//                } else {
//                    myIntent.putExtra(TRANSACTION_ID_KEY, id);
//                    myIntent.putExtra(AddPage2.AMOUNT_KEY, amountvalue);
//                    myIntent.putExtra(AddPage2.CATEGORY_KEY, category_index);
//                    myIntent.putExtra(AddPage2.IS_EXPENSE_KEY, isExpense);
//                }
//
//                EditTransactionPage.this.startActivityForResult(myIntent, 1);
//                overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);

            }
        }
        else if(item.getItemId() == android.R.id.home)
        {
            finish();
        }
        return super.onOptionsItemSelected(item);

    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if(resultCode == AddPage2.DONE)
//        {
//            finish();
//        }
//        else if(resultCode == AddPage2.BACK_OR_UP)
//        {
//
//        }
//    }

//    private void setCurrentState()
//    {
//        for(int i = 0; i < cost_string.length(); i++)
//        {
//            char c = cost_string.charAt(i);
//            int input = 0;
//
//            if(c == '.')
//                input = DOT;
//            else
//                input = (int)(c - '0');
//
//            currentState = transition[currentState][input];
//        }
//    }

    public void put2(int widthsize){

        int buttonWidth=0;
        if(category_income_buttons.length>0)
        {
            category_expense_buttons[0].measure(0, 0);
            buttonWidth=category_expense_buttons[0].getMeasuredWidth();
        }


        int nums=getNumberOfCulomns(widthsize,buttonWidth);
        Log.e("number of buttons: ",nums+"");
        Log.e("number of buttons2: ",widthsize+"  "+buttonWidth);

        GridLayout gridLayout=new GridLayout(this);
        gridLayout.setColumnCount(nums);

        if(isExpense){
            for(int i = 0; i < category_expense_buttons.length; i++)
            {
                GridLayout.Spec row = GridLayout.spec(i/nums);
                GridLayout.Spec col = GridLayout.spec(nums-(i%nums)-1);
                category_expense_buttons[i].setLayoutParams(new GridLayout.LayoutParams(row, col));
                gridLayout.addView(category_expense_buttons[i]);
            }

            LinearLayout ll=(LinearLayout)findViewById(R.id.category_layout);
            ll.addView(gridLayout);
        }
        else {

            for(int i = 0; i < category_income_buttons.length; i++)
            {
                GridLayout.Spec row = GridLayout.spec(i/nums);
                GridLayout.Spec col = GridLayout.spec(nums-(i%nums)-1);
                category_income_buttons[i].setLayoutParams(new GridLayout.LayoutParams(row, col));
                gridLayout.addView(category_income_buttons[i]);
            }
            LinearLayout ll2=(LinearLayout)findViewById(R.id.category_layout);
            ll2.addView(gridLayout);
        }




    }

    public void putCategories(int screenWidth, int layoutHight,LinearLayout parentLayout,ImageButton[] inputButtons) {

        int currentWidth=screenWidth;
        int currentHight=layoutHight;
//        currentHight-=300;
//        currentHight=pager.measuredHeightSize;
        ViewPagerAdapterLayout pagerAdapter = new ViewPagerAdapterLayout();
        myViewPager pager=new myViewPager(this);
        CirclePageIndicator title = new CirclePageIndicator(this);
        pagerAdapter = new ViewPagerAdapterLayout();

        title.setFillColor(0xff484848);
        title.setStrokeColor(0xff303030);
        pager.setAdapter(pagerAdapter);
        title.setViewPager(pager);
        title.setPadding(0,3,0,0);
//        title.setLayoutParams(new ViewGroup.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        pager.setLayoutParams(new ViewGroup.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));


//        LinearLayout ll=(LinearLayout)findViewById(R.id.expense_tab);


        parentLayout.addView(pager);
        parentLayout.addView(title);

        while (pagerAdapter.getCount()>0){
            pagerAdapter.removeView(pager,0);
        }

        LinearLayout linearLayout=new LinearLayout(this);
        linearLayout.setGravity(LinearLayout.VERTICAL);
        LinearLayout linearLayoutVertical=new LinearLayout(this);
        linearLayoutVertical.setGravity(Gravity.CENTER_HORIZONTAL);
        linearLayoutVertical.setOrientation(LinearLayout.VERTICAL);
        linearLayoutVertical.addView(linearLayout);
        pagerAdapter.addView(linearLayoutVertical);
        for (int i = 0; i < inputButtons.length; i++) {
            inputButtons[i].measure(0, 0);
            inputButtons[i].setTag(i);
            if(currentWidth>inputButtons[i].getMeasuredWidth())
            {
                linearLayout.addView(inputButtons[i]);
                currentWidth-=inputButtons[i].getMeasuredWidth();
            }
            else{

                if(currentHight>inputButtons[i].getMeasuredHeight()){
                    linearLayout=new LinearLayout(this);
                    linearLayout.setGravity(LinearLayout.VERTICAL);
                    linearLayoutVertical.addView(linearLayout);
                    linearLayout.addView(inputButtons[i]);
                    currentWidth=screenWidth-inputButtons[i].getMeasuredWidth();
                    currentHight-=inputButtons[i].getMeasuredHeight();
                }
                else {
                    currentHight=layoutHight;
                    linearLayout=new LinearLayout(this);
                    linearLayout.setGravity(LinearLayout.VERTICAL);
                    linearLayoutVertical=new LinearLayout(this);
                    linearLayoutVertical.setOrientation(LinearLayout.VERTICAL);
                    linearLayoutVertical.setGravity(Gravity.CENTER_HORIZONTAL);
                    linearLayoutVertical.addView(linearLayout);
                    linearLayout.addView(inputButtons[i]);
                    pagerAdapter.addView(linearLayoutVertical);
                    currentWidth=screenWidth-inputButtons[i].getMeasuredWidth();
                }
            }
        }
        title.notifyDataSetChanged();
        pagerAdapter.notifyDataSetChanged();
    }


    public void addTransationToDatabase()
    {
//        double amountValue=Double.parseDouble(cost_string);
//        SQLiteDatabase db = trHelper.getWritableDatabase();

//        PersianCalendar calendar = new PersianCalendar();
//        int selectedDay = calendar.get(PersianCalendar.DAY_OF_MONTH);
//        int selectedMonth = calendar.get(PersianCalendar.MONTH);
//        int selectedYear = calendar.get(PersianCalendar.YEAR);
//        int selectedWeekday = calendar.get(PersianCalendar.DAY_OF_WEEK);
//
//        Calendar cal = Calendar.getInstance();
//        int selectedHour = cal.get(PersianCalendar.HOUR_OF_DAY);
//        int selectedMinute = cal.get(PersianCalendar.MINUTE);
//        PersianDate date = new PersianDate((short)selectedDay, (short)selectedMonth, (short)selectedYear, PersianCalendar.weekdayFullNames[selectedWeekday]);
//        Time time = new Time((short)selectedHour, (short)selectedMinute);
//        String dateTime = date.getSTDString()+time.getSTDString();

//        ContentValues values = new ContentValues();
//        values.put(TransactionEntry.COLUMN_NAME_DATE_TIME, dateTime);
//        values.put(TransactionEntry.COLUMN_NAME_AMOUNT, );
//        values.put(TransactionEntry.COLUMN_NAME_IS_EXPENSE, isExpense);
//        values.put(TransactionEntry.COLUMN_NAME_ACCOUNT_NAME,SettingActivity.defaultAccount);
//        description = descp.getText().toString();
//        values.put(TransactionEntry.COLUMN_NAME_CATEGORY, category_index);
//        values.put(TransactionEntry.COLUMN_NAME_DESCRIPTION,description.getText().toString());

        if(id.equals("null"))
        {
             Log.e("error","transaction not found");

        }
        else
        {
            LocalDBServices.editUnhandyTransaction(getBaseContext(),category_index,id,selectedTags);
//            String selection = TransactionEntry._ID + " LIKE ?";
//            String[] selectionArgs = { String.valueOf(id) };
//
//            db.update(
//                    TransactionEntry.TABLE_NAME,
//                    values,
//                    selection,
//                    selectionArgs);
//
//
//            String selectionTags = TransactionsContract.TagsEntry.COLUMN_NAME_TRANSACTION_ID + " LIKE ?";
//            String[] selectionArgsTags = { String.valueOf(id) };
//            db.delete(TransactionsContract.TagsEntry.TABLE_NAME, selectionTags, selectionArgsTags);
//
//            //TODO distinct
//            for(String tag: selectedTags)
//            {
//                values = new ContentValues();
//                values.put(TransactionsContract.TagsEntry.COLUMN_NAME_TRANSACTION_ID, id);
//                values.put(TransactionsContract.TagsEntry.COLUMN_NAME_TAG, tag);
//                db.insert(TransactionsContract.TagsEntry.TABLE_NAME, null, values);
//            }

        }
    }


    @Override
    public boolean onLongClick(View view) {
//        double amountValue = Double.parseDouble(cost_string);
//        if (amountValue == 0) {
//            Toast.makeText(this,"لطفا مبلغ تراکنش را وارد کنید",Toast.LENGTH_SHORT).show();
//        }
//        else{
//        boolean is_cat = false;
        for (int j = 0; j < 11; j++)
            if (view == category_income_buttons[j]) {
//                is_cat = true;
                if (category_income_buttons[j].isSelected()) {

                } else {
                    category_index = j;
                    for (int k = 0; k < Category.INCOME_SIZE - 1; k++) {
                        category_income_buttons[k].setSelected(false);
                        category_income_buttons[k].setBackground(null);
                    }
                    category_income_buttons[j].setSelected(true);
                    category_income_buttons[j].setBackground(getResources().getDrawable(category_income_background[j]));
                }

            }
        for (int j = 0; j < 14; j++)
            if (view == category_expense_buttons[j]) {

//                System.out.println("BUTTON ID IS : "+j);

//                is_cat = true;
                if (category_expense_buttons[j].isSelected()) {
//                    category_index=Category.EXPENSE_UNCATEGORIZED;
//                    for(int k = 0; k < Category.EXPENSE_SIZE-1; k++){
//                        category_expense_buttons[k].setSelected(false);
//                        category_expense_buttons[k].setBackground(null);
//                    }
                } else {
                    category_index = j;
                    for (int k = 0; k < Category.EXPENSE_SIZE - 1; k++) {
                        category_expense_buttons[k].setSelected(false);
                        category_expense_buttons[k].setBackground(null);
                    }
                    category_expense_buttons[j].setSelected(true);
                    category_expense_buttons[j].setBackground(getResources().getDrawable(category_expense_backgroud[j]));
                }

            }

//        toast.Text("Release to add transaction to database");
//        toast.show();
        addTransationToDatabase();
        finish();
//        System.out.println("Long click called !!!");

        return false;
    }
    @Override
    protected void onResume()
    {

//        System.out.println("iconperpage  :  "+iconPerPage);
//        System.out.println("!@#category index: " +category_index);

        if(category_index!=Category.EXPENSE_UNCATEGORIZED&&isExpense)
//            pager.setCurrentItem(category_index / iconPerPage);
        {}
        if(category_index!=Category.INCOME_UNCATEGORIZED&&!isExpense)
//            pager2.setCurrentItem(category_index / iconPerPage);
{}
        for(int i = 0; i < Category.EXPENSE_SIZE-1; i++)
        {
            category_expense_buttons[i].getDrawable().setAlpha(TimelineActivity.FULLY_OPAQUE);
        }

        for(int i = 0; i < Category.INCOME_SIZE-1; i++)
        {
            category_income_buttons[i].getDrawable().setAlpha(TimelineActivity.FULLY_OPAQUE);
        }
        super.onResume();
    }


    private int getNumberOfCulomns(int displayWidth,int oneButtonWidth){




        int column=(int)(displayWidth/oneButtonWidth);
        return column;
    }

}
