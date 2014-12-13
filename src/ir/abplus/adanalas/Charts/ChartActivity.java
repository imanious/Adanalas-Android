package ir.abplus.adanalas.Charts;

import android.annotation.TargetApi;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.widget.*;
import com.fourmob.datetimepicker.date.PersianCalendar;
import com.fourmob.datetimepicker.date.PersianDatePickerDialog;
import com.fourmob.datetimepicker.date.PersianDatePickerDialog.OnDateSetListener;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import ir.abplus.adanalas.Libraries.Category;
import ir.abplus.adanalas.Libraries.PersianDate;
import ir.abplus.adanalas.Libraries.Time;
import ir.abplus.adanalas.Libraries.TransactionsContract;
import ir.abplus.adanalas.Libraries.TransactionsContract.TransactionEntry;
import ir.abplus.adanalas.R;
import ir.abplus.adanalas.Setting.SettingActivity;
import ir.abplus.adanalas.Timeline.FilterMenuAdapter;
import ir.abplus.adanalas.Timeline.FilterMenuItem;
import ir.abplus.adanalas.Timeline.TimelineActivity;
import ir.abplus.adanalas.Uncategoried.UncategoriedActivity;
import ir.abplus.adanalas.databaseConnections.LocalDBServices;
import za.co.immedia.pinnedheaderlistview.PinnedHeaderListView;

import java.util.ArrayList;

/**
 * Created by Keyvan Sasani on 8/27/2014.
 */
public class ChartActivity extends FragmentActivity implements OnDateSetListener {

    public static final int DAILY = 0;
	public static final int WEEKLY = 1;
    public static final int MONTHLY = 2;
    public static final int YEARLY = 3;
	
	private static final int PIE_CHART = 0;
	private static final int BAR_CHART = 1;
	private static final int LINE_CHART = 2;

	public static final String PERSIAN_DATEPICKER_TAG = "pdatepickerchart";
	private static final float TEXT_SIZE = 16;
	FragmentTransaction transaction;
	Button incomeButton;
	Button expenseButton;
	ImageButton barChartButton;
//	ImageButton lineChartButton;
	ImageButton pieChartButton;
    boolean isExpense=true;
	//////////////////////////////////////////
	private PersianCalendar calendar;
   	public static String startDate;
    public static String endDate;

	boolean[] expenseSelection = new boolean[Category.EXPENSE_SIZE];
	boolean[] incomeSelection = new boolean[Category.INCOME_SIZE];
	private Button toDateButton;
	private PersianDatePickerDialog datePicker;
	private int selectedYear;
	private int selectedMonth;
	private int selectedDay;
	private int selectedChart;
	///////////////////////////////////////////////////////////////////
	FilterMenuAdapter accountMenuAdapter;
	ArrayList<FilterMenuItem> accountsAndTimeFilter = new ArrayList<FilterMenuItem>();
	DisplayMetrics displayMetrics;
	float screenDpWidth;
	private int numberOfBars = 2;
	private TextView fromDateTextView;
    private PieChartFragment pieChartFragment;
    private LineChartFragment lineFragment;
    private BarChartFragment barFragment;

//
//    //todo remove below item
//    ListView list;
//    CustomAdapter adapter;
//    public  ChartActivity CustomListView = null;
//    public ArrayList<ListModel> CustomListViewValuesArr = new ArrayList<ListModel>();



    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.chart_activity);
//
//        CustomListView = this;
//
//        /******** Take some data in Arraylist ( CustomListViewValuesArr ) ***********/
//        setListData();
//
//        Resources res =getResources();
//        list= ( ListView )findViewById( R.id.list );  // List defined in XML ( See Below )
//
//        /**************** Create Custom Adapter *********/
//
//
//        View head = getLayoutInflater().inflate(R.layout.chart_activity, null);
//        list.addHeaderView(head);
//        adapter=new CustomAdapter( CustomListView, CustomListViewValuesArr,res );
//        list.setAdapter( adapter );



		displayMetrics = getResources().getDisplayMetrics();
		screenDpWidth = displayMetrics.widthPixels / displayMetrics.density;

        expenseSelection=TimelineActivity.expenseSelection;
        incomeSelection=TimelineActivity.incomeSelection;
//        System.out.println(TimelineActivity.accountSelection.toString());

        boolean[] tmpBool=new boolean[4];
        for(int i=0;i<tmpBool.length;i++)
            tmpBool[i]=false;
        int j=TimelineActivity.accountMenuAdapter.getRadioSelected();
        tmpBool[j]=true;
//		for(int i = 0; i < Category.EXPENSE_SIZE; i++)
//			expenseSelection[i] = true;
//
//		for(int i = 0; i < Category.INCOME_SIZE; i++)
//			incomeSelection[i] = true;

        addAccountsToList();

		accountsAndTimeFilter.add(new FilterMenuItem(getResources().getString(R.string.time_interval),
				tmpBool[0], getResources().getString(R.string.daily), true, 0));
		accountsAndTimeFilter.add(new FilterMenuItem("", tmpBool[1],
				getResources().getString(R.string.weekly), true, 0));
		accountsAndTimeFilter.add(new FilterMenuItem("", tmpBool[2],
				getResources().getString(R.string.monthly), true, 0));
		accountsAndTimeFilter.add(new FilterMenuItem("", tmpBool[3],
				getResources().getString(R.string.yearly), true, 0));

		// configure the SlidingMenu
		SlidingMenu menu = new SlidingMenu(this);
		menu.setMode(SlidingMenu.LEFT_RIGHT);
		menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		menu.setShadowWidthRes(R.dimen.shadow_width);
		menu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
		menu.setBehindOffsetRes(R.dimen.sliding_menu_behid_offset);
		menu.setOnOpenListener(new SlidingMenu.OnOpenListener()
		{
			@Override
			public void onOpen()
			{
				setFromDateTextview();
			}
		});
		
		menu.setOnCloseListener(new SlidingMenu.OnCloseListener()
		{
			@Override
			public void onClose()
			{
//                pieChartFragment.UpdateDatabaseCursor();
//                  barFragment.updateDatabaseCursor(false,-1);

                if(selectedChart==PIE_CHART)
                    pieChartFragment.UpdateDatabaseCursor();
                if(selectedChart==LINE_CHART)
                    pieChartFragment.UpdateDatabaseCursor();
                if(selectedChart==BAR_CHART)
                    barFragment.updateDatabaseCursor(false,-1);
				//                setPullToRefreshLabels();
				//                try {
				//                    immediateRefreshTimeline();
				//                } catch (NumberFormatException e) {
				//                    e.printStackTrace();
				//                } catch (IllegalArgumentException e) {
				//                    e.printStackTrace();
				//                } catch (ParseException e) {
				//                    e.printStackTrace();
				//                }
				//                setTitleText();
			}
		});

		// left menu:
		menu.setMenu(R.layout.left_menu_layout);
		menu.setShadowDrawable(R.drawable.left_shadow);
		PinnedHeaderListView accountMenu = (PinnedHeaderListView) findViewById(R.id.account_time_filter_listview);
		accountMenuAdapter = new FilterMenuAdapter(this, this);

		accountMenuAdapter.setItems(accountsAndTimeFilter);
		accountMenu.setAdapter(accountMenuAdapter);

		calendar = new PersianCalendar();
		

		View separator = findViewById(R.id.separator);
		separator.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 2));
		
		LinearLayout fromToLayout = (LinearLayout)findViewById(R.id.date_from_to);
		TextView fromTextView = new TextView(this);
		toDateButton = new Button(this);
		TextView toTextView = new TextView(this);
		fromDateTextView = new TextView(this);

		fromTextView.setLayoutParams(new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
		toTextView.setLayoutParams(new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
		fromDateTextView.setLayoutParams(new LinearLayout.LayoutParams(
				0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
		toDateButton.setLayoutParams(new LinearLayout.LayoutParams(
				0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
		
		fromTextView.setTypeface(TimelineActivity.persianTypeface);
		toTextView.setTypeface(TimelineActivity.persianTypeface);
		fromDateTextView.setTypeface(TimelineActivity.persianTypeface);
		toDateButton.setTypeface(TimelineActivity.persianTypeface);

		fromTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, TEXT_SIZE);
		toTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, TEXT_SIZE);
		fromDateTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, TEXT_SIZE);
		toDateButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, TEXT_SIZE);

		fromDateTextView.setGravity(Gravity.CENTER);
		
		toDateButton.setBackgroundResource(R.drawable.underlined_button);
		
		fromTextView.setText(getResources().getString(R.string.from));
		toTextView.setText(getResources().getString(R.string.to));

		toDateButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View arg0)
			{
				datePicker.show(getSupportFragmentManager(), PERSIAN_DATEPICKER_TAG);
			}
		});

		fromToLayout.setPadding(0, 0,
				getResources().getDimensionPixelSize(R.dimen.shadow_width)
				+getResources().getDimensionPixelSize(R.dimen.sliding_menu_right_padding), 0);
		fromTextView.setPadding(getResources().getDimensionPixelSize(R.dimen.sliding_menu_right_padding), 0, 0, 0);
		toTextView.setPadding(0, 0, getResources().getDimensionPixelSize(R.dimen.sliding_menu_right_padding), 0);
		
		
//		initializeDate();
		selectedDay = calendar.get(PersianCalendar.DAY_OF_MONTH);
		selectedMonth = calendar.get(PersianCalendar.MONTH);
		selectedYear = calendar.get(PersianCalendar.YEAR);
		datePicker = PersianDatePickerDialog.newInstance(this, selectedYear, selectedMonth, selectedDay, false, PersianDatePickerDialog.PERSIAN);
		datePicker.setYearRange(1390, 1400);
		datePicker.setCloseOnSingleTapDay(true);
		toDateButton.setText(selectedYear+"/"+(selectedMonth+1)+"/"+selectedDay);
		setFromDateTextview();
		
		fromToLayout.setGravity(Gravity.RIGHT);
		fromToLayout.addView(toDateButton);
		fromToLayout.addView(toTextView);
		fromToLayout.addView(fromDateTextView);
		fromToLayout.addView(fromTextView);

		// right menu:
		menu.setSecondaryMenu(R.layout.right_menu_layout);
		menu.setSecondaryShadowDrawable(R.drawable.right_shadow);

		RelativeLayout header = (RelativeLayout) findViewById(R.id.expense_header);
		((TextView)header.findViewById(R.id.side_menu_header))
		.setTypeface(TimelineActivity.persianTypeface);
		((TextView)header.findViewById(R.id.side_menu_header))
		.setText(getResources().getString(R.string.expenses));

		header = (RelativeLayout) findViewById(R.id.income_header);
		((TextView)header.findViewById(R.id.side_menu_header))
		.setTypeface(TimelineActivity.persianTypeface);
		((TextView)header.findViewById(R.id.side_menu_header))
		.setText(getResources().getString(R.string.expenses));


		GridLayout expenses = (GridLayout) findViewById(R.id.expense_grid);
		expenses.setPadding((int)(getResources().getDimension(R.dimen.shadow_width)/getResources().getDisplayMetrics().density)+5, 0,
				(int)(getResources().getDimension(R.dimen.shadow_width)/getResources().getDisplayMetrics().density)+5, 0);
		int column = getSlidingMenuNumberOfColumns(expenses, Category.getExpenseIconID(Category.EXPENSE_UNCATEGORIZED));
		expenses.setColumnCount(column);
		expenses.setPadding(getSlidingMenuRightPaddding(column, Category.getExpenseIconID(Category.EXPENSE_UNCATEGORIZED)), 0, getSlidingMenuRightPaddding(column, Category.getExpenseIconID(Category.EXPENSE_UNCATEGORIZED)), 0);
		for(int i = 0; i < Category.EXPENSE_SIZE; i++)
		{
			GridLayout.Spec row = GridLayout.spec(i/column);
			GridLayout.Spec col = GridLayout.spec(column-(i%column)-1);
			ImageButton imageButton = new ImageButton(this);
			final int tmpIndex = i;
			imageButton.setOnClickListener(new View.OnClickListener()
			{
				@Override
				public void onClick(View button)
				{
					expenseSelection[tmpIndex] = !expenseSelection[tmpIndex];
					button.setSelected(!button.isSelected());
					if(button.isSelected())
					{
						button.getBackground().setAlpha(TimelineActivity.HALF_TRANSPARENT);
					}
					else
					{
						button.getBackground().setAlpha(TimelineActivity.FULLY_OPAQUE);
					}
				}
			});
			imageButton.setLayoutParams(new GridLayout.LayoutParams(row, col));
			imageButton.setBackgroundResource(Category.getExpenseIconID(i));
			expenses.addView(imageButton);
		}

		GridLayout incomes = (GridLayout) findViewById(R.id.incomes_grid);
		incomes.setPadding((int)(getResources().getDimension(R.dimen.shadow_width)/getResources().getDisplayMetrics().density)+5, 0,
				(int)(getResources().getDimension(R.dimen.shadow_width)/getResources().getDisplayMetrics().density)+5, 0);
		column = getSlidingMenuNumberOfColumns(incomes, Category.getExpenseIconID(Category.EXPENSE_UNCATEGORIZED));
		incomes.setColumnCount(column);
		incomes.setPadding(getSlidingMenuRightPaddding(column, Category.getExpenseIconID(Category.EXPENSE_UNCATEGORIZED)), 0, getSlidingMenuRightPaddding(column, Category.getExpenseIconID(Category.EXPENSE_UNCATEGORIZED)), 0);
		for(int i = 0; i < Category.INCOME_SIZE; i++)
		{
			GridLayout.Spec row = GridLayout.spec(i/column);
			GridLayout.Spec col = GridLayout.spec(column-(i%column)-1);
			ImageButton imageButton = new ImageButton(this);
			final int tmpIndex = i;
			imageButton.setOnClickListener(new View.OnClickListener()
			{
				@Override
				public void onClick(View button)
				{
					incomeSelection[tmpIndex] = !incomeSelection[tmpIndex];
					button.setSelected(!button.isSelected());
					if(button.isSelected())
					{
						button.getBackground().setAlpha(TimelineActivity.HALF_TRANSPARENT);
					}
					else
					{
						button.getBackground().setAlpha(TimelineActivity.FULLY_OPAQUE);
					}
				}
			});
			imageButton.setLayoutParams(new GridLayout.LayoutParams(row, col));
			imageButton.setBackgroundResource(Category.getIncomeIconID(i));
			incomes.addView(imageButton);
		}

		selectedChart = PIE_CHART;
		incomeButton=(Button)findViewById(R.id.incomeb);
		expenseButton=(Button)findViewById(R.id.expenseb);
		barChartButton=(ImageButton)findViewById(R.id.barbutton);
//		lineChartButton=(ImageButton)findViewById(R.id.linebutton);
		pieChartButton=(ImageButton)findViewById(R.id.piebutton);

		incomeButton.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.income_icon, 0);
		expenseButton.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.expense_icon,0);
		incomeButton.setTypeface(TimelineActivity.persianTypeface);
		//TODO change to dp
		incomeButton.setCompoundDrawablePadding(10);
		expenseButton.setCompoundDrawablePadding(10);
		expenseButton.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.expense_icon_selected,0);
		expenseButton.setTypeface(TimelineActivity.persianTypeface);
		barChartButton.setBackground(getResources().getDrawable(R.drawable.bar_chart));
//		lineChartButton.setBackground(getResources().getDrawable(R.drawable.line_chart));
		pieChartButton.setBackground(getResources().getDrawable(R.drawable.pie_chart));

		pieChartFragment = new PieChartFragment();
		transaction = getSupportFragmentManager().beginTransaction();
		transaction.replace(R.id.fragment_content, pieChartFragment);
//		transaction.addToBackStack(null);
		transaction.commit();
		pieChartButton.setBackground(getResources().getDrawable(R.drawable.pie_chart_selected));
		//        BarFragment newFragment = new BarFragment();
		//        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		//        transaction.replace(R.id.fragment_content, newFragment);
		//        transaction.addToBackStack(null);
		//        transaction.commit();
		//

		barChartButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				selectedChart = BAR_CHART;
//				lineChartButton.setBackground(getResources().getDrawable(R.drawable.line_chart));
				pieChartButton.setBackground(getResources().getDrawable(R.drawable.pie_chart));
				barChartButton.setBackground(getResources().getDrawable(R.drawable.bar_chart_selected));
				barFragment = new BarChartFragment();
				transaction = getSupportFragmentManager().beginTransaction();
				transaction.replace(R.id.fragment_content, barFragment);
				//                transaction.addToBackStack(null);
				transaction.commit();
//                barFragment.updateDatabaseCursor(false,-1);

			}
		});

		pieChartButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				selectedChart = PIE_CHART;
//				lineChartButton.setBackground(getResources().getDrawable(R.drawable.line_chart));
				pieChartButton.setBackground(getResources().getDrawable(R.drawable.pie_chart_selected));
				barChartButton.setBackground(getResources().getDrawable(R.drawable.bar_chart));
//				pieChartFragment = new PieChartFragment();
				transaction = getSupportFragmentManager().beginTransaction();
				transaction.replace(R.id.fragment_content, pieChartFragment);
				//                transaction.addToBackStack(null);
				transaction.commit();


//                pieChartFragment.UpdateDatabaseCursor();

			}
		});

//		lineChartButton.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View view) {
//				selectedChart = LINE_CHART;
//				lineChartButton.setBackground(getResources().getDrawable(R.drawable.line_chart_selected));
//				pieChartButton.setBackground(getResources().getDrawable(R.drawable.pie_chart));
//				barChartButton.setBackground(getResources().getDrawable(R.drawable.bar_chart));
//				 lineFragment = new LineChartFragment();
//				transaction = getSupportFragmentManager().beginTransaction();
//				transaction.replace(R.id.fragment_content, lineFragment);
//				//                transaction.addToBackStack(null);
//				transaction.commit();
//
////                pieChartFragment.UpdateDatabaseCursor();
//
//
//			}
//		});

		expenseButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				expenseButton.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.expense_icon_selected,0);
				incomeButton.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.income_icon, 0);
                isExpense=true;
                if(selectedChart==PIE_CHART)
                    pieChartFragment.UpdateDatabaseCursor();
                if(selectedChart==LINE_CHART)
                    pieChartFragment.UpdateDatabaseCursor();
                if(selectedChart==BAR_CHART)
                    barFragment.updateDatabaseCursor(false,-1);
			}
		});

		incomeButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				expenseButton.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.expense_icon,0);
				incomeButton.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.income_icon_selected, 0);
                isExpense=false;
                if(selectedChart==PIE_CHART)
                pieChartFragment.UpdateDatabaseCursor();
                if(selectedChart==LINE_CHART)
                    pieChartFragment.UpdateDatabaseCursor();
                if(selectedChart==BAR_CHART)
                    barFragment.updateDatabaseCursor(false,-1);
			}
		});


    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main_charts, menu);
		return true;
	}
	@Override
	public boolean onOptionsItemSelected(android.view.MenuItem item) {
		//        System.out.println(item.toString());
		Intent intent = null;
		boolean doNothig=false;
		if(item.toString().equals(getResources().getString(R.string.action_timeline))){
			//            intent=new Intent(UncategoriedActivity.this, UncategoriedActivity.class);
		}
		else if(item.toString().equals(getResources().getString(R.string.action_settings))){
			intent=new Intent(ChartActivity.this, SettingActivity.class);
		}
		else if(item.toString().equals(getResources().getString(R.string.action_uncategorized))){
			intent=new Intent(ChartActivity.this, UncategoriedActivity.class);
		}
		else if(item.toString().equals(getResources().getString(R.string.action_charts))){
			doNothig=true;
		}
		if(!doNothig){
			if(intent!=null){
				finish();
				startActivity(intent);

			}
			else finish();}
		overridePendingTransition(0,0);
		return super.onOptionsItemSelected(item);
	}
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	private int getSlidingMenuRightPaddding(int column, int resID)
	{
		BitmapFactory.Options o = new BitmapFactory.Options();
		switch((int)(4*(getResources().getDisplayMetrics().density)))
		{
		case 3:
			o.inTargetDensity = DisplayMetrics.DENSITY_LOW;
			break;
		case 4:
			o.inTargetDensity = DisplayMetrics.DENSITY_MEDIUM;
			break;
		case 6:
			o.inTargetDensity = DisplayMetrics.DENSITY_HIGH;
			break;
		case 8:
			o.inTargetDensity = DisplayMetrics.DENSITY_XHIGH;
			break;
		case 12:
			o.inTargetDensity = DisplayMetrics.DENSITY_XXHIGH;
			break;
		}
		Bitmap bmp = BitmapFactory.decodeResource(getResources(), resID, o);
		int w = (int) (bmp.getWidth()/getResources().getDisplayMetrics().density);
		int dp = (int) (getResources().getDimension(R.dimen.sliding_menu_behid_offset) / getResources().getDisplayMetrics().density);
		return (int)(((screenDpWidth-dp)-(column*w)));
	}

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	private int getSlidingMenuNumberOfColumns(GridLayout gridLayout, int resourceID)
	{
		BitmapFactory.Options o = new BitmapFactory.Options();
		switch((int)(4*(getResources().getDisplayMetrics().density)))
		{
		case 3:
			o.inTargetDensity = DisplayMetrics.DENSITY_LOW;
			break;
		case 4:
			o.inTargetDensity = DisplayMetrics.DENSITY_MEDIUM;
			break;
		case 6:
			o.inTargetDensity = DisplayMetrics.DENSITY_HIGH;
			break;
		case 8:
			o.inTargetDensity = DisplayMetrics.DENSITY_XHIGH;
			break;
		case 12:
			o.inTargetDensity = DisplayMetrics.DENSITY_XXHIGH;
			break;
		}
		Bitmap bmp = BitmapFactory.decodeResource(getResources(), resourceID, o);
		int w = (int) (bmp.getWidth()/getResources().getDisplayMetrics().density);
		int dp = (int) (getResources().getDimension(R.dimen.sliding_menu_behid_offset) / getResources().getDisplayMetrics().density);
		int leftPadding = (int) (gridLayout.getPaddingLeft()/getResources().getDisplayMetrics().density);
		int rightPadding = (int) (gridLayout.getPaddingRight()/getResources().getDisplayMetrics().density);
		int column = (int)((screenDpWidth-dp-leftPadding-rightPadding)/(w));

		return column;
	}

	@Override
	public void onDateSet(PersianDatePickerDialog datePickerDialog, int year,
			int month, int day) {
		PersianCalendar tmpCal = new PersianCalendar(year, month, day);
		selectedYear = year;
		selectedMonth = month;
		selectedDay = day;

		toDateButton.setText(selectedYear+"/"+(selectedMonth+1)+"/"+selectedDay);
		setFromDateTextview();
	}
	
	private String getStartDate()
	{
		calendar.set(PersianCalendar.DATE, selectedDay);
		calendar.set(PersianCalendar.MONTH, selectedMonth);
		calendar.set(PersianCalendar.YEAR, selectedYear);

		switch(accountMenuAdapter.getRadioSelected())
		{
		case DAILY:
			switch(selectedChart)
			{
			case PIE_CHART:
				return new PersianDate((short)selectedDay, (short)selectedMonth, (short)selectedYear, "").getSTDString();
			default:
				calendar.add(PersianCalendar.DATE, 1-numberOfBars);
				return new PersianDate((short)calendar.get(PersianCalendar.DATE), (short)calendar.get(PersianCalendar.MONTH), (short)calendar.get(PersianCalendar.YEAR), "").getSTDString();
			}
		case WEEKLY:
			switch(selectedChart)
			{
			case PIE_CHART:
				calendar.add(PersianCalendar.DATE, -6);
				return new PersianDate((short)calendar.get(PersianCalendar.DATE), (short)calendar.get(PersianCalendar.MONTH), (short)calendar.get(PersianCalendar.YEAR), "").getSTDString();
			default:
				calendar.add(PersianCalendar.DATE, (-numberOfBars*7)+1);
                return new PersianDate((short)calendar.get(PersianCalendar.DATE), (short)calendar.get(PersianCalendar.MONTH), (short)calendar.get(PersianCalendar.YEAR), "").getSTDString();
			}
		case MONTHLY:
			switch(selectedChart)
			{
			case PIE_CHART:
				calendar.add(PersianCalendar.MONTH, -1);
				return new PersianDate((short)calendar.get(PersianCalendar.DATE), (short)calendar.get(PersianCalendar.MONTH), (short)calendar.get(PersianCalendar.YEAR), "").getSTDString();
			default:
				calendar.add(PersianCalendar.MONTH, -numberOfBars);
				return new PersianDate((short)calendar.get(PersianCalendar.DATE), (short)calendar.get(PersianCalendar.MONTH), (short)calendar.get(PersianCalendar.YEAR), "").getSTDString();
			}
		case YEARLY:
			switch(selectedChart)
			{
			case PIE_CHART:
				calendar.add(PersianCalendar.YEAR, -1);
				return new PersianDate((short)calendar.get(PersianCalendar.DATE), (short)calendar.get(PersianCalendar.MONTH), (short)calendar.get(PersianCalendar.YEAR), "").getSTDString();
			default:
				calendar.add(PersianCalendar.YEAR, -numberOfBars);
				return new PersianDate((short)calendar.get(PersianCalendar.DATE), (short)calendar.get(PersianCalendar.MONTH), (short)calendar.get(PersianCalendar.YEAR), "").getSTDString();
			}
		}
		return null;
	}

	public void setFromDateTextview()
	{
        getWhereClause();
//		Log.i("where", getWhereClause());
//		calendar.set(PersianCalendar.DATE, selectedDay);
//		calendar.set(PersianCalendar.MONTH, selectedMonth);
//		calendar.set(PersianCalendar.YEAR, selectedYear);
//
        int fromDate=Integer.parseInt(startDate.substring(6,8));
        int fromMonth=(Integer.parseInt(startDate.substring(4,6)))+1;
        int fromYear=Integer.parseInt(startDate.substring(0,4));
        fromDateTextView.setText(fromYear+"/"+fromMonth+"/"+fromDate);
//
//		switch(accountMenuAdapter.getRadioSelected())
//		{
//		case DAILY:
//			switch(selectedChart)
//			{
//			case PIE_CHART:
//				fromDateTextView.setText(selectedYear+"/"+(selectedMonth+1)+"/"+selectedDay);
//				return;
//			default:
//				calendar.add(PersianCalendar.DATE, 1-numberOfBars);
//				fromDateTextView.setText(calendar.get(PersianCalendar.YEAR)+"/"+(calendar.get(PersianCalendar.MONTH)+1)+"/"+calendar.get(PersianCalendar.DATE));
//				return;
//			}
//		case WEEKLY:
//			switch(selectedChart)
//			{
//			case PIE_CHART:
//				calendar.add(PersianCalendar.DATE, -6);
//				fromDateTextView.setText(calendar.get(PersianCalendar.YEAR)+"/"+(calendar.get(PersianCalendar.MONTH)+1)+"/"+calendar.get(PersianCalendar.DATE));
//				return;
//			default:
//				calendar.add(PersianCalendar.DATE, 1-numberOfBars*7);
//				fromDateTextView.setText(calendar.get(PersianCalendar.YEAR)+"/"+(calendar.get(PersianCalendar.MONTH)+1)+"/"+calendar.get(PersianCalendar.DATE));
//				return;
//			}
//		case MONTHLY:
//			switch(selectedChart)
//			{
//			case PIE_CHART:
//				calendar.add(PersianCalendar.MONTH, -1);
//				fromDateTextView.setText(calendar.get(PersianCalendar.YEAR)+"/"+(calendar.get(PersianCalendar.MONTH)+1)+"/"+calendar.get(PersianCalendar.DATE));
//				return;
//			default:
//				calendar.add(PersianCalendar.MONTH, -numberOfBars);
//				fromDateTextView.setText(calendar.get(PersianCalendar.YEAR)+"/"+(calendar.get(PersianCalendar.MONTH)+1)+"/"+calendar.get(PersianCalendar.DATE));
//				return;
//			}
//		case YEARLY:
//			switch(selectedChart)
//			{
//			case PIE_CHART:
//				calendar.add(PersianCalendar.YEAR, -1);
//				fromDateTextView.setText(calendar.get(PersianCalendar.YEAR)+"/"+(calendar.get(PersianCalendar.MONTH)+1)+"/"+calendar.get(PersianCalendar.DATE));
//				return;
//			default:
//				calendar.add(PersianCalendar.YEAR, -numberOfBars);
//				fromDateTextView.setText(calendar.get(PersianCalendar.YEAR)+"/"+(calendar.get(PersianCalendar.MONTH)+1)+"/"+calendar.get(PersianCalendar.DATE));
//				return;
//			}
//		}
	}
	
	public void initializeDate()
	{
		switch(accountMenuAdapter.getRadioSelected())
		{
		case DAILY:
			switch(selectedChart)
			{
			case PIE_CHART:
				return;
			default:
				calendar.add(PersianCalendar.DATE, -numberOfBars);
				return;
			}
		case WEEKLY:
			switch(selectedChart)
			{
			case PIE_CHART:
				calendar.add(PersianCalendar.DATE, -7);
				return;
			default:
				calendar.add(PersianCalendar.DATE, -numberOfBars*7);
				return;
			}
		case MONTHLY:
			switch(selectedChart)
			{
			case PIE_CHART:
				calendar.add(PersianCalendar.MONTH, -1);
				return;
			default:
				calendar.add(PersianCalendar.MONTH, -numberOfBars);
				return;
			}
		case YEARLY:
			switch(selectedChart)
			{
			case PIE_CHART:
				calendar.add(PersianCalendar.YEAR, -1);
				return;
			default:
				calendar.add(PersianCalendar.YEAR, -numberOfBars);
				return;
			}
		}
	}
	
	public String getWhereClause(){
		String where = "(";
		where += "("+TransactionEntry.COLUMN_NAME_IS_EXPENSE + "=" + 1 + ") and (";
		for(int i = 0; i < Category.EXPENSE_SIZE; i++)
			if(expenseSelection[i])
				where += TransactionEntry.COLUMN_NAME_CATEGORY + "=" + i + " or ";

		if(where.substring(where.length()-4, where.length()).equals(" or "))
		{
			where = where.substring(0, where.length()-4);
			where += ")";
		}
		else
		{
			where += "1=0)";
		}
		
		where += " or ("+TransactionEntry.COLUMN_NAME_IS_EXPENSE + "=" + 0 + ") and (";
		for(int i = 0; i < Category.INCOME_SIZE; i++)
			if(incomeSelection[i])
				where += TransactionEntry.COLUMN_NAME_CATEGORY + "=" + i + " or ";

		if(where.substring(where.length()-4, where.length()).equals(" or "))
		{
			where = where.substring(0, where.length()-4);
			where += ")"; 
		}
		else
		{
			where += "1=0)";
		}
		
		where += ")";

		Time time;
		time = new Time((short)0, (short)0);
		String startDate = getStartDate()+time.getSTDString();
		
		time = new Time((short)99, (short)99);
		String endDate = new PersianDate((short)selectedDay,
				(short)selectedMonth, (short)selectedYear, "").getSTDString()
				+time.getSTDString();

        this.startDate=startDate;
        this.endDate=endDate;
		
		where += " and "+TransactionEntry.COLUMN_NAME_DATE_TIME+">="+startDate; 
		where += " and "+TransactionEntry.COLUMN_NAME_DATE_TIME+"<="+endDate;

        // add accounts to where clause
        ArrayList<String> ar=new ArrayList<String>();
        for (int i=0;i<accountMenuAdapter.getAccountsSelection().length;i++){
            if(accountMenuAdapter.getAccountsSelection()[i])
                ar.add(accountMenuAdapter.getSelectedAccountString(i));
        }

        where += " and (";
        for(int i = 0; i < ar.size(); i++)
        {
            where += "( "+TransactionEntry.COLUMN_NAME_ACCOUNT_NAME+ " = '" + ar.get(i) +"' ) "+ " or ";
        }
        if(where.substring(where.length()-4, where.length()).equals(" or "))
        {
            where = where.substring(0, where.length()-4);
            where += ")";
        }
        else
        {
            where += "1=0)";
        }

		return where;
	}
//
////todo remove later
//    public void setListData()
//    {
//
//        for (int i = 0; i < 11; i++) {
//
//            final ListModel sched = new ListModel();
//
//            /******* Firstly take data in model object ******/
//            sched.setCompanyName("Company "+i);
//            sched.setImage("image"+i);
//            sched.setUrl("http:\\www."+i+".com");
//
//            /******** Take Model Object in ArrayList **********/
//            CustomListViewValuesArr.add( sched );
//        }
//
//    }

    private void addAccountsToList() {
//        TransactoinDatabaseHelper trHelper = new TransactoinDatabaseHelper(this);
//        SQLiteDatabase db = trHelper.getReadableDatabase();
        Cursor c2;
//        String query="select "+ TransactionsContract.Accounts.COLUMN_NAME_Account_Name+
//                " from "+ TransactionsContract.Accounts.TABLE_NAME;
//
//
//        Log.e("test", "trying to add accounts");
//        c2=db.rawQuery(query,null);
        c2=LocalDBServices.getAccountList(getBaseContext());
        c2.moveToFirst();

        int i=0;
        if(c2.getCount() != 0)
        {
            do
            {
                String accountName=c2.getString(c2.getColumnIndexOrThrow(TransactionsContract.Accounts.COLUMN_NAME_Account_Name));
                System.out.println("account name is : "+accountName);
                if(c2!=null){
                    if(c2.isFirst())
                        accountsAndTimeFilter.add(new FilterMenuItem("حساب‌ها", TimelineActivity.accountSelection[i],accountName, false, R.drawable.vaam_raw));
                    else
                        accountsAndTimeFilter.add(new FilterMenuItem("", TimelineActivity.accountSelection[i],accountName, false, R.drawable.vaam_raw));
                }
             i++;
            }while(c2.moveToNext());
        }
    }
}
