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
import com.fourmob.datetimepicker.Utils;
import com.fourmob.datetimepicker.date.PersianCalendar;
import com.viewpagerindicator.CirclePageIndicator;
import ir.abplus.adanalas.Libraries.*;
import ir.abplus.adanalas.Libraries.TransactionsContract.TransactionEntry;
import ir.abplus.adanalas.R;
import ir.abplus.adanalas.Setting.SettingActivity;
import ir.abplus.adanalas.Timeline.TimelineActivity;
import ir.abplus.adanalas.databaseConnections.LocalDBServices;

import java.util.ArrayList;
import java.util.Calendar;

public class AddPage1 extends Activity implements View.OnClickListener, View.OnLongClickListener {

    public static final String TRANSACTION_ID_KEY = "trans_id_key";

    boolean flagTree=true;
    private static final int FONT_SIZE = 36;
    private static final int DOT = 10;
    private static final int BACK_SPACE = 11;


    int iconPerPage=0;

    private static int category_index;
    @SuppressWarnings("deprecation")
    private ArrayList<AbsoluteLayout> expense_layouts = new ArrayList<AbsoluteLayout>();
    @SuppressWarnings("deprecation")
    private ArrayList<AbsoluteLayout> income_layouts = new ArrayList<AbsoluteLayout>();

    private String id = "null";



    boolean decimalPointEntered = false;
    int decimalDigits = 0;
    int currentState = 0;
    int[][] transition = new int[4][12];
    String cost_string;

    Button[] buttons;
    ImageButton back_button;
    TextView cost_text;

    private ImageButton[] category_expense_buttons;
    private ImageButton[] category_income_buttons;
    //	private int[] category_background;
    private int widthsize;
    private int heightsize;
    private int layoutHeight;
    private CirclePageIndicator title;
    private CirclePageIndicator title2;
    private int[] category_income_background;
    private int[] category_expense_backgroud;
    private boolean isExpense=true;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_page1);

        getActionBar().setDisplayHomeAsUpEnabled(true);



        LinearLayout currency_layout= (LinearLayout) findViewById(R.id.currency_layout);
        Currency.setCurrencyLayout(currency_layout, this, getResources().getColor(R.color.black), TimelineActivity.persianTypeface, Currency.LARGE_TEXT_SIZE);


        category_index= Category.EXPENSE_UNCATEGORIZED;

        cost_string="0";
        for(int i = 0; i < 10; i++)
        {
            transition[0][i] = 0;
            transition[1][i] = 2;
            transition[2][i] = 3;
            transition[3][i] = 3;
        }

        transition[0][DOT] = 1;
        transition[0][BACK_SPACE] = 0;

        transition[1][DOT] = 1;
        transition[1][BACK_SPACE] = 0;

        transition[2][DOT] = 2;
        transition[2][BACK_SPACE] = 1;

        transition[3][DOT] = 3;
        transition[3][BACK_SPACE] = 2;


        buttons=new Button[11];
        buttons[0]=(Button)findViewById(R.id.button_zero);
        buttons[1]=(Button)findViewById(R.id.button1);
        buttons[2]=(Button)findViewById(R.id.button2);
        buttons[3]=(Button)findViewById(R.id.button3);
        buttons[4]=(Button)findViewById(R.id.button4);
        buttons[5]=(Button)findViewById(R.id.button5);
        buttons[6]=(Button)findViewById(R.id.button6);
        buttons[7]=(Button)findViewById(R.id.button7);
        buttons[8]=(Button)findViewById(R.id.button8);
        buttons[9]=(Button)findViewById(R.id.button9);
        buttons[10]=(Button)findViewById(R.id.button_dot);
        back_button=(ImageButton)findViewById(R.id.button_backspace);



        for(int i=0;i<11;i++)
        {
            buttons[i].setOnClickListener(this);
            buttons[i].setTextSize(TypedValue.COMPLEX_UNIT_SP, FONT_SIZE);
            buttons[i].setTypeface(TimelineActivity.persianTypeface);
            buttons[i].setText(Utils.toPersianNumbers(i+""));
        }

        buttons[DOT].setText(Currency.decimalPoint);
        back_button.setOnTouchListener(new RepeatListener(400, 100, this));

        if(Currency.getCurrency()!=Currency.THOUSAND_TOMAN)
            buttons[DOT].setEnabled(false);
        cost_text=(TextView)findViewById(R.id.cost_text);
        cost_text.setText("0");
        cost_text.setTypeface(TimelineActivity.persianTypeface);




        Display  display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        widthsize = size.x;
        layoutHeight = size.y;
//        layoutHeight= ((int) (heightsize * (((LinearLayout.LayoutParams) .getLayoutParams()).weight / getWeightSum())));



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

//        put2(widthsize);
        LinearLayout ll=(LinearLayout)findViewById(R.id.expense_tab);
        putCategories(widthsize,300,ll,category_expense_buttons);
        ll=(LinearLayout)findViewById(R.id.income_tab);
        putCategories(widthsize,300,ll,category_income_buttons);

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



        TabHost tabHost=(TabHost)findViewById(R.id.tabhost_add);
        tabHost.setup();
        tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String s) {
                if (s.contains("tag1")) {
                    isExpense = true;
                    category_index = Category.EXPENSE_UNCATEGORIZED;
                    for(int k = 0; k < Category.EXPENSE_SIZE-1; k++){
                        category_expense_buttons[k].setSelected(false);
                        category_expense_buttons[k].setBackground(null);
                    }
                } else if (s.contains("tag2")) {
                    isExpense = false;
                    category_index = Category.INCOME_UNCATEGORIZED;
                    for(int k = 0; k < Category.INCOME_SIZE-1; k++){
                        category_income_buttons[k].setSelected(false);
                        category_income_buttons[k].setBackground(null);
                    }
                } else {
//                	System.out.println(s);
                }
            }
        });


        TabHost.TabSpec ts=tabHost.newTabSpec("tag1");
        ts.setContent(R.id.expense_tab);

        ts.setIndicator("برداشت");

        tabHost.addTab(ts);

        TabHost.TabSpec ts2=tabHost.newTabSpec("tag2");
        ts2.setContent(R.id.income_tab);
        ts2.setIndicator("واریز");
        tabHost.addTab(ts2);

        for (int i = 0; i < tabHost.getTabWidget().getChildCount(); i++) {
            TextView tv = (TextView) tabHost.getTabWidget().getChildAt(i).findViewById(android.R.id.title);
            tv.setTypeface(TimelineActivity.persianBoldTypeface);
            tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        }



        Intent intent = getIntent();
        if(intent != null)
        {
            Bundle extras = intent.getExtras();
            if(extras != null)
            {
                setTitle(R.string.edit_transaction);
                id = extras.getString(TRANSACTION_ID_KEY, "null");
                if(!id.equals("null"))
                {
                    Cursor c;
                    Log.e("debug","id in intent extra"+id);
                    c= LocalDBServices.getTransactionFromID(id);
                    c.moveToFirst();

                    Long amount = c.getLong(c.getColumnIndexOrThrow(TransactionEntry.COLUMN_NAME_AMOUNT));
                    int category = c.getInt(c.getColumnIndexOrThrow(TransactionEntry.COLUMN_NAME_CATEGORY));
                    isExpense = c.getInt(c.getColumnIndexOrThrow(TransactionEntry.COLUMN_NAME_IS_EXPENSE))==0? false: true;
                    c.close();
                    //TODO set is expense

                    cost_text.setText(Currency.getStdAmount(amount));
                    cost_string = String.format("%.2f", Double.parseDouble(Currency.getStdAmountWithoutSeparation(amount)));
                    if(cost_string.charAt(cost_string.length()-1) == '0')
                    {
                        cost_string = cost_string.substring(0, cost_string.length()-1);
                        if(cost_string.charAt(cost_string.length()-1) == '0')
                        {
                            cost_string = cost_string.substring(0, cost_string.length()-2);
                        }
                    }

                    setCurrentState();
                    System.out.println("category : "+category);
                    if(!isExpense)
                        tabHost.setCurrentTabByTag("tag2");
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
                }
            }

        }




    }

//	//-----------------------------------------------------------------------------
//	// Here's what the app should do to add a view to the ViewPager.
//	public void addView (View newPage)
//	{
//		int pageIndex = pagerAdapter.addView (newPage);
//		// You might want to make "newPage" the currently displayed page:
//		pager.setCurrentItem (pageIndex, true);
//	}
//
//	//-----------------------------------------------------------------------------
//	// Here's what the app should do to remove a view from the ViewPager.
//	public void removeView (View defunctPage)
//	{
//		int pageIndex = pagerAdapter.removeView (pager, defunctPage);
//		// You might want to choose what page to display, if the current page was "defunctPage".
//		if (pageIndex == pagerAdapter.getCount())
//			pageIndex--;
//		pager.setCurrentItem (pageIndex);
//	}
//
//	//-----------------------------------------------------------------------------
//	// Here's what the app should do to get the currently displayed page.
//	public View getCurrentPage ()
//	{
//		return pagerAdapter.getView (pager.getCurrentItem());
//	}
//
//	//-----------------------------------------------------------------------------
//	// Here's what the app should do to set the currently displayed page.  "pageToShow" must
//	// currently be in the adapter, or this will crash.
//	public void setCurrentPage (View pageToShow)
//	{
//		pager.setCurrentItem (pagerAdapter.getItemPosition (pageToShow), true);
//	}
//

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
            int input = 0;
            if (view == back_button)
                input = BACK_SPACE;
            else {
                for (int i = 0; i < 11; i++)
                    if (view == buttons[i]) {
                        input = i;
                        break;
                    }
            }

            switch (currentState) {
                case 0:
                    //adad gheyre sefr tahala nazade
                {

                    if (input <= 9)
                    {

                        if (cost_string.equalsIgnoreCase("0"))
                            cost_string = cost_string.substring(0, cost_string.length() - 1);
                        cost_string = cost_string + input;
                        cost_text.setText(Currency.separateThousand(cost_string));
                    }
                    else if (input == DOT)
                    {
                        cost_text.setText(Currency.separateThousand(cost_string) + Currency.decimalPoint);
                    }
                    else if (input == BACK_SPACE)
                    {
                        if (cost_string.length() > 1)
                        {
                            cost_string = cost_string.substring(0, cost_string.length() - 1);
                            cost_text.setText(Currency.separateThousand(cost_string));
                        }
                        else if (cost_string.length() > 0)
                        {
                            cost_string = "0";
                            cost_text.setText(Currency.separateThousand(cost_string));
                        }
                    }

                }
                break;
                case 1:
                    //faghat ye momayez zade
                {
                    if (input == 0)
                    {
                        cost_string += ".0";
                        cost_text.setText(cost_text.getText() + "0");
                    }
                    else if (input <= 9)
                    {
                        cost_string += "." + input;

                        cost_text.setText(cost_text.getText() + "" + input);
                    }
                    else if (input == DOT)
                    {
                        //nothing
                    }
                    else if (input == BACK_SPACE)
                    {
                        cost_text.setText(cost_text.getText().subSequence(0, cost_text.getText().length() - 1));
                    }
                }
                break;
                case 2:
                    //1 adad bade momayez zade
                {
                    if (input == 0)
                    {
                        cost_string += "0";
                        cost_text.setText(cost_text.getText() + "0");
                    }
                    else if (input <= 9)
                    {
                        cost_string += input;
                        cost_text.setText(cost_text.getText() + "" + input);
                    }
                    else if (input == DOT)
                    {
                        //nothing
                    }
                    else if (input == BACK_SPACE)
                    {
                        cost_string = cost_string.substring(0, cost_string.length() - 2);
                        //                    f = (float)((long)f);
                        cost_text.setText(cost_text.getText().subSequence(0, cost_text.getText().length() - 1));
                    }
                }
                break;
                case 3:
                    // 2 adad bade momayez zade
                {
                    if (input != BACK_SPACE)
                    {
                        //nothing
                    }
                    else if (input == BACK_SPACE)
                    {
                        //                    f = (double)((long)(10*f) / 10.0);
                        cost_string = cost_string.substring(0, cost_string.length() - 1);
                        cost_text.setText(cost_text.getText().subSequence(0, cost_text.getText().length() - 1));
                    }
                }
                break;
            }

            currentState = transition[currentState][input];
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.add_page1_actions, menu);
        final MenuItem item = menu.findItem(R.id.next_button);
        TextView actionBarTextView = (TextView) item.getActionView();
        actionBarTextView.setTypeface(TimelineActivity.persianBoldTypeface);
        actionBarTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                getActionBar().getHeight()-actionBarTextView.getPaddingTop()-actionBarTextView.getPaddingBottom()-24);
        actionBarTextView.setText(R.string.new_transaction_more_details);
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
        double amountvalue = Double.parseDouble(cost_string);
        if(item.getItemId()==R.id.next_button) {
            if(amountvalue==0)
                Toast.makeText(this,"لطفا مبلغ تراکنش را وارد کنید", Toast.LENGTH_SHORT).show();
            else if ((isExpense==true&&category_index == Category.EXPENSE_UNCATEGORIZED )|| (isExpense==false&&category_index == Category.INCOME_UNCATEGORIZED))
                Toast.makeText(this, "لطفا دسته بندی تراکنش را معلوم کنید", Toast.LENGTH_SHORT).show();
            else {
                Intent myIntent = new Intent(AddPage1.this, AddPage2.class);
                if (id.equals("null")){
//				System.out.println("intent started");
                    Log.e("debug","add page2 with null value : "+id);
                    myIntent.putExtra(AddPage2.AMOUNT_KEY, amountvalue);
                    myIntent.putExtra(AddPage2.CATEGORY_KEY, category_index);
                    myIntent.putExtra(AddPage2.IS_EXPENSE_KEY, isExpense);

                } else {
                    Log.e("debug","add page2 with id value : "+id);
                    myIntent.putExtra(TRANSACTION_ID_KEY, id);
                    myIntent.putExtra(AddPage2.AMOUNT_KEY, amountvalue);
                    myIntent.putExtra(AddPage2.CATEGORY_KEY, category_index);
                    myIntent.putExtra(AddPage2.IS_EXPENSE_KEY, isExpense);
                }

                AddPage1.this.startActivityForResult(myIntent, 1);
                overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
            }
        }
        else if(item.getItemId() == android.R.id.home)
        {
            finish();
        }
        return super.onOptionsItemSelected(item);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == AddPage2.DONE)
        {
            finish();
        }
        else if(resultCode == AddPage2.BACK_OR_UP)
        {

        }
    }

    private void setCurrentState()
    {
        for(int i = 0; i < cost_string.length(); i++)
        {
            char c = cost_string.charAt(i);
            int input = 0;

            if(c == '.')
                input = DOT;
            else
                input = (int)(c - '0');

            currentState = transition[currentState][input];
        }
    }


    @SuppressWarnings("deprecation")
    public void putChild(int widthSize, int hightSize) {
//        System.out.println("witdhsize : "+widthSize);
        int mVerticalSpacing=0;
        int mHorizontalSpacing=0;
        int tag_id=0;
        int neededPadding=0;
        boolean has_layout=false;
        int line_count = 1;
        boolean isNewPage=false;


        AbsoluteLayout al = new AbsoluteLayout(this);
        al.setTag(tag_id);

        int width = 0;
        int height = al.getPaddingTop();
        int currentWidth = al.getPaddingLeft();
        int currentHeight = 0;

        final int count = category_expense_buttons.length;

//        System.out.println("child count = " + count);

        for (int i = 0; i < count; i++) {
            View child = category_expense_buttons[i];
            category_expense_buttons[i].measure(0, 0);
            AbsoluteLayout.LayoutParams lp = new AbsoluteLayout.LayoutParams(child.getMeasuredWidth(), child.getMeasuredHeight(), 5, 5);
//            System.out.println("currentWidth + child.getMeasuredWidth() : "+currentWidth + child.getMeasuredWidth());


            if (currentWidth + child.getMeasuredWidth() > widthSize) {
                line_count++;
                neededPadding=widthSize-currentWidth;
                al.setPadding(neededPadding/2,0,0,0);

                height += currentHeight + mVerticalSpacing;
                currentHeight = 0;
                if (currentWidth > width) width = currentWidth;
                currentWidth = al.getPaddingLeft();
            }

            int spacing = mHorizontalSpacing;

            if(line_count>2){
                isNewPage=true;
                al=new AbsoluteLayout(this);

                tag_id++;
                al.setTag(tag_id);
                has_layout=false;
                line_count=1;
                height=0;
            }
            if(!isNewPage)
                iconPerPage++;

            lp.x = currentWidth;
            lp.y = height;

            category_expense_buttons[i].setLayoutParams(lp);


//            System.out.println(showed_tag_button.get(i).TagText+" added to this layout : "+al.getTag());
            al.addView(category_expense_buttons[i]);

            currentWidth += child.getMeasuredWidth() + spacing;
            int childHeight = child.getMeasuredHeight();
            if (childHeight > currentHeight) currentHeight = childHeight;


            for(int j=0;j<expense_layouts.size();j++)
                if(expense_layouts.get(j).getTag()==al.getTag())
                {has_layout=true;
                    break;
                }
            if(has_layout==false)
            {

                expense_layouts.add(al);
            }
        }
        {
            height += currentHeight;
            if (currentWidth > width) width = currentWidth;
        }

        for(AbsoluteLayout absoluteLayout:expense_layouts){
//            pagerAdapter.addView(absoluteLayout);
        }
    }

//    public void put2(int widthsize){
//
//        int buttonWidth=0;
//        if(category_income_buttons.length>0)
//        {
//            category_expense_buttons[0].measure(0, 0);
//            buttonWidth=category_expense_buttons[0].getMeasuredWidth();
//        }
//
//
//        int nums=getNumberOfCulomns(widthsize,buttonWidth);
//        Log.e("number of buttons: ",nums+"");
//        Log.e("number of buttons2: ",widthsize+"  "+buttonWidth);
//
//        GridLayout gridLayout=new GridLayout(this);
//        gridLayout.setColumnCount(nums);
//
//        for(int i = 0; i < category_expense_buttons.length; i++)
//        {
//            GridLayout.Spec row = GridLayout.spec(i/nums);
//            GridLayout.Spec col = GridLayout.spec(nums-(i%nums)-1);
////            ImageButton imageButton = new ImageButton(this);
////            final int tmpIndex = i;
//            category_expense_buttons[i].setLayoutParams(new GridLayout.LayoutParams(row, col));
////            imageButton.setBackgroundResource(Category.getExpenseIconID(i));
//            gridLayout.addView(category_expense_buttons[i]);
//        }
//
//        LinearLayout ll=(LinearLayout)findViewById(R.id.expense_tab);
//        ll.addView(gridLayout);
//
//        gridLayout=new GridLayout(this);
//        gridLayout.setColumnCount(nums);
//
//        for(int i = 0; i < category_income_buttons.length; i++)
//        {
//            GridLayout.Spec row = GridLayout.spec(i/nums);
//            GridLayout.Spec col = GridLayout.spec(nums-(i%nums)-1);
////            ImageButton imageButton = new ImageButton(this);
////            final int tmpIndex = i;
//            category_income_buttons[i].setLayoutParams(new GridLayout.LayoutParams(row, col));
////            imageButton.setBackgroundResource(Category.getExpenseIconID(i));
//            gridLayout.addView(category_income_buttons[i]);
//        }
//
//        LinearLayout ll2=(LinearLayout)findViewById(R.id.income_tab);
//        ll2.addView(gridLayout);
//
//    }

    @SuppressWarnings("deprecation")
    public void putChild_2(int widthSize, int hightSize) {

        int mVerticalSpacing=0;
        int mHorizontalSpacing=0;
        int tag_id=0;
        boolean has_layout=false;
        int line_count = 1;
        int neededPadding=0;
        boolean isNewPage=false;

//        for(AbsoluteLayout absoluteLayout:tag_layouts) {
//
//            absoluteLayout.removeAllViews();
//            pagerAdapter.removeView(pager, absoluteLayout);
//        }
//        tag_layouts=new ArrayList<AbsoluteLayout>();

//        for(TagButton tagButton:showed_tag_button)
//        {
//            AbsoluteLayout r=(AbsoluteLayout)(tagButton).getParent();
//            if(r!=null)
//                r.removeAllViews();
//        }

//        title2.notifyDataSetChanged();
//        pagerAdapter2.notifyDataSetChanged();

        AbsoluteLayout al = new AbsoluteLayout(this);
        al.setTag(tag_id);

        int width = 0;
        int height = al.getPaddingTop();
        int currentWidth = al.getPaddingLeft();
        int currentHeight = 0;

        final int count = category_income_buttons.length;

//        System.out.println("child count = " + count);

        for (int i = 0; i < count; i++) {
            View child = category_income_buttons[i];
            category_income_buttons[i].measure(0, 0);
            AbsoluteLayout.LayoutParams lp = new AbsoluteLayout.LayoutParams(child.getMeasuredWidth(), child.getMeasuredHeight(), 5, 5);


            if (currentWidth + child.getMeasuredWidth() > widthSize) {
                line_count++;

                neededPadding=widthSize-currentWidth;
                al.setPadding(neededPadding/2,0,0,0);
                height += currentHeight + mVerticalSpacing;
                currentHeight = 0;
                if (currentWidth > width) width = currentWidth;
                currentWidth = al.getPaddingLeft();
            }

            int spacing = mHorizontalSpacing;

            if(line_count>2){
                isNewPage=true;
                al=new AbsoluteLayout(this);

                tag_id++;
                al.setTag(tag_id);
                has_layout=false;
                line_count=1;
                height=0;
            }

            lp.x = currentWidth;
            lp.y = height;

            category_income_buttons[i].setLayoutParams(lp);


//            System.out.println(showed_tag_button.get(i).TagText+" added to this layout : "+al.getTag());
            al.addView(category_income_buttons[i]);

            currentWidth += child.getMeasuredWidth() + spacing;
            int childHeight = child.getMeasuredHeight();
            if (childHeight > currentHeight) currentHeight = childHeight;


            for(int j=0;j<income_layouts.size();j++)
                if(income_layouts.get(j).getTag()==al.getTag())
                {has_layout=true;
                    break;
                }
            if(has_layout==false)
            {
//                System.out.println("!!!!!!!!!"+al.getTag());
                income_layouts.add(al);
                //                pagerAdapter.addView(al);
            }
//            else System.out.println("we have this layout already : "+al.getTag());
        }
        // after last row (patched by yuku)
        {
            height += currentHeight;
            if (currentWidth > width) width = currentWidth;
        }
        width += al.getPaddingRight();
        height += al.getPaddingBottom();
        //        pagerAdapter.addView(al);

        for(AbsoluteLayout absoluteLayout:income_layouts){
//            pagerAdapter2.addView(absoluteLayout);
//            System.out.println(absoluteLayout.getTag()+" layout added");
            for(int i=0;i<absoluteLayout.getChildCount();i++){
//                System.out.println(absoluteLayout.getChildCount());
//                System.out.println((((TagButton)absoluteLayout.getChildAt(i)).TagText));
            }
        }
//        title2.notifyDataSetChanged();
//        pagerAdapter2.notifyDataSetChanged();
        //        title.forceLayout();
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
        double amountValue=Double.parseDouble(cost_string);
//        SQLiteDatabase db = trHelper.getWritableDatabase();

        PersianCalendar calendar = new PersianCalendar();
        int selectedDay = calendar.get(PersianCalendar.DAY_OF_MONTH);
        int selectedMonth = calendar.get(PersianCalendar.MONTH);
        int selectedYear = calendar.get(PersianCalendar.YEAR);
        int selectedWeekday = calendar.get(PersianCalendar.DAY_OF_WEEK);

        Calendar cal = Calendar.getInstance();
        int selectedHour = cal.get(PersianCalendar.HOUR_OF_DAY);
        int selectedMinute = cal.get(PersianCalendar.MINUTE);
        int selectedSecond=cal.get(PersianCalendar.SECOND);
        PersianDate date = new PersianDate((short)selectedDay, (short)selectedMonth, (short)selectedYear, PersianCalendar.weekdayFullNames[selectedWeekday]);
        Time time = new Time((short)selectedHour, (short)selectedMinute);
        String dateTime = date.getSTDString()+time.getSTDString()+"00";



//        ContentValues values = new ContentValues();
//        values.put(TransactionEntry.COLUMN_NAME_DATE_TIME, dateTime);
//        values.put(TransactionEntry.COLUMN_NAME_AMOUNT, amountValue);
//        values.put(TransactionEntry.COLUMN_NAME_IS_EXPENSE, isExpense);
//        values.put(TransactionEntry.COLUMN_NAME_ACCOUNT_NAME,SettingActivity.defaultAccount);
//        values.put(TransactionEntry.COLUMN_NAME_CATEGORY, category_index);
//        values.put(TransactionEntry.COLUMN_NAME_DESCRIPTION, description);

//        LocalDBServices.addNewTransaction(this,dateTime,amountValue,isExpense,SettingActivity.defaultAccount,category_index);

        if(id.equals("null"))
        {
//            db.insert(TransactionEntry.TABLE_NAME, null, values);
            LocalDBServices.addNewTransaction(this,dateTime,amountValue,isExpense, SettingActivity.defaultAccount,category_index,null,null,true);
        }
        else
        {
//
//            String selection = TransactionEntry._ID + " LIKE ?";
//            String[] selectionArgs = { String.valueOf(id) };
//            db.update(TransactionEntry.TABLE_NAME,values,selection,selectionArgs);
//            String selectionTags = TransactionsContract.TagsEntry.COLUMN_NAME_TRANSACTION_ID + " LIKE ?";
//            String[] selectionArgsTags = { String.valueOf(id) };
//            db.delete(TransactionsContract.TagsEntry.TABLE_NAME, selectionTags, selectionArgsTags);

            LocalDBServices.editHandyTransaction(this,null,amountValue,isExpense,null,category_index,id,null,null);
        }
    }


    @Override
    public boolean onLongClick(View view) {
        double amountValue = Double.parseDouble(cost_string);
        if (amountValue == 0) {
            Toast.makeText(this,"لطفا مبلغ تراکنش را وارد کنید",Toast.LENGTH_SHORT).show();
        }
        else{
            boolean is_cat = false;
            for (int j = 0; j < 11; j++)
                if (view == category_income_buttons[j]) {
                    is_cat = true;
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

                    is_cat = true;
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
        }
        return false;
    }
    @Override
    protected void onResume()
    {

        System.out.println("iconperpage  :  "+iconPerPage);
        System.out.println("!@#category index: " +category_index);

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
