package ir.abplus.adanalas.Timeline;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.*;
import android.widget.GridLayout.Spec;
import com.fourmob.datetimepicker.Utils;
import com.fourmob.datetimepicker.date.PersianCalendar;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnPullEventListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshBase.State;
import com.handmark.pulltorefresh.library.PullToRefreshPinnedHeaderListView;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import ir.abplus.adanalas.AddEditTransactions.AddPage1;
import ir.abplus.adanalas.Charts.ChartActivity;
import ir.abplus.adanalas.Libraries.*;
import ir.abplus.adanalas.Libraries.TransactionsContract.TransactionEntry;
import ir.abplus.adanalas.R;
import ir.abplus.adanalas.Setting.SettingActivity;
import ir.abplus.adanalas.SyncCloud.ConnectionManager;
import ir.abplus.adanalas.SyncCloud.Declaration;
import ir.abplus.adanalas.SyncCloud.JsonParser;
import ir.abplus.adanalas.SyncCloud.LoginActivity2;
import ir.abplus.adanalas.Uncategoried.UncategoriedActivity;
import ir.abplus.adanalas.databaseConnections.LocalDBServices;
import za.co.immedia.pinnedheaderlistview.PinnedHeaderListView;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class TimelineActivity extends Activity implements OnClickListener, OnPullEventListener<PinnedHeaderListView>, OnRefreshListener2<PinnedHeaderListView>
{
	public static final int DAILY = 0;
    public static final int WEEKLY = 1;
    public static final int MONTHLY = 2;
    public static final int YEARLY = 3;
	
	private static String[] ordinal = {"", "اول", "دوم", "سوم", "چهارم", "پنجم", "ششم", "هفتم"};


	private static final String TYPEFACE_NAME = "nassim-regular.ttf";
	private static final String BOLD_TYPEFACE_NAME = "nassim-bold.ttf";
	public static final int HALF_TRANSPARENT = 50;
	public static final int FULLY_OPAQUE = 255;
	public static Typeface persianTypeface;
	public static Typeface persianBoldTypeface;
	boolean firstTime = true;

    int counter = 0;

	public static boolean[] expenseSelection = new boolean[Category.EXPENSE_SIZE];
	public static boolean[] incomeSelection = new boolean[Category.INCOME_SIZE];
	public static boolean[] accountSelection;
	String totalIncomeString;
	String totalExpenseString;
	
	ImageButton addButton;
	LinearLayout totalExpenseLayout;
	TextView totalIncome;
	LinearLayout totalIncomeLayout;
	TextView totalExpense;
	ArrayList<TimelineItem2> listItems = new ArrayList<TimelineItem2>();
	ArrayList<FilterMenuItem> accountTimeFilterMenuItem = new ArrayList<FilterMenuItem>();

	PullToRefreshPinnedHeaderListView phList;
	TimelinePinnedHeaderAdapter pHAdapter;
	public static FilterMenuAdapter accountMenuAdapter;

	String titleText;
	TextView title;
	PersianCalendar filterDate = new PersianCalendar();
    LodingDialog dialog;

	DisplayMetrics displayMetrics;
	float screenDpWidth;
	private String lastUpdateString = "";
    private LinearLayout totalIncomeCurrencyLayout;
    private LinearLayout totalExpenseCurrencyLayout;
    private String PREFRENCES_FILE ="preferences";

    @Override
	protected void onCreate(Bundle savedInstanceState){


		super.onCreate(savedInstanceState);
        if(getTokensFromDB()){
        }
        else {
            return;
        }

		setContentView(R.layout.timeline_layout);

		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM); 
		getActionBar().setCustomView(R.layout.timeline_actionbar_layout);


		Currency.setCurrency(Currency.TOMAN);
		persianTypeface = Typeface.createFromAsset(this.getAssets(), TYPEFACE_NAME);
		persianBoldTypeface = Typeface.createFromAsset(this.getAssets(), BOLD_TYPEFACE_NAME);
		Category.initialize();

		title = (TextView) findViewById(R.id.timeline_title);
		title.setTypeface(persianTypeface);
//		final SearchView searchView = (SearchView) findViewById(R.id.search_view);
//		searchView.setQueryHint("جست‌وجو");
//		searchView.setOnSearchClickListener(new OnClickListener()
//		{
//			@Override
//			public void onClick(View search)
//			{
//				title.setText("");
//				RelativeLayout.LayoutParams p = new RelativeLayout.LayoutParams(
//						RelativeLayout.LayoutParams.MATCH_PARENT,
//						RelativeLayout.LayoutParams.MATCH_PARENT);
//				search.setLayoutParams(p);
//				Toast.makeText(context, "listener", Toast.LENGTH_SHORT).show();
//			}
//		});
//		
//		searchView.setOnCloseListener(new SearchView.OnCloseListener()
//		{
//			@Override
//			public boolean onClose()
//			{
//				setTitleText();
//				RelativeLayout.LayoutParams p = new RelativeLayout.LayoutParams(
//						RelativeLayout.LayoutParams.WRAP_CONTENT,
//						RelativeLayout.LayoutParams.MATCH_PARENT);
//				p.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
//				searchView.setLayoutParams(p);
//				return false;
//			}
//		});
//		
//		searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener()
//		{
//			@Override
//			public boolean onQueryTextSubmit(String arg0)
//			{
//				String[] tokens = arg0.split(TAG_SEPARATOR);
//				return false;
//			}
//			
//			@Override
//			public boolean onQueryTextChange(String arg0)
//			{
//				SQLiteDatabase db = trHelper.getReadableDatabase();
//				
//				String query;
//				Cursor c;
//				
//				query = "SELECT DISTINCT(" + TagsEntry.COLUMN_NAME_TAG + ")"+
//						" FROM "+TagsEntry.TABLE_NAME;
//				
//				c = db.rawQuery(query, null);
//				c.moveToFirst();
//				ArrayList<String> tags = new ArrayList<String>();
//				
//				do
//					tags.add(c.getString(c.getColumnIndexOrThrow(TagsEntry.COLUMN_NAME_TAG)));
//				while(c.moveToNext());
//
//				SearchableInfo si = 
//				searchView.setSuggestionsAdapter(adapter);
//				return false;
//			}
//		});

//		for(int i = 0; i < Category.EXPENSE_SIZE; i++)
//			expenseSelection[i] = true;
//
//		for(int i = 0; i < Category.INCOME_SIZE; i++)
//			incomeSelection[i] = true;
		
		totalExpenseLayout = (LinearLayout) findViewById(R.id.total_expense_layout);
		totalExpense = (TextView) totalExpenseLayout.findViewById(R.id.total_expense_text);
		totalExpense.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
		totalExpenseCurrencyLayout = (LinearLayout) totalExpenseLayout.findViewById(R.id.currency_layout_exp);
		Currency.setCurrencyLayout(totalExpenseCurrencyLayout, this, getResources().getColor(R.color.red), persianTypeface, Currency.SMALL_TEXT_SIZE);
		totalExpense.setTypeface(persianTypeface);
		totalExpense.setTextColor(getResources().getColor(R.color.red));

		totalIncomeLayout = (LinearLayout) findViewById(R.id.total_income_layout);
		totalIncome = (TextView) totalIncomeLayout.findViewById(R.id.total_income_text);
		totalIncome.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
        totalIncomeCurrencyLayout = (LinearLayout) totalIncomeLayout.findViewById(R.id.currency_layout_inc);
		Currency.setCurrencyLayout(totalIncomeCurrencyLayout , this, getResources().getColor(R.color.green), persianTypeface, Currency.SMALL_TEXT_SIZE);
		totalIncome.setTypeface(persianTypeface);
		totalIncome.setTextColor(getResources().getColor(R.color.green));

        addButton = (ImageButton) findViewById(R.id.add_button);
        addButton.setOnClickListener(this);



        displayMetrics = getResources().getDisplayMetrics();

		screenDpWidth = displayMetrics.widthPixels / displayMetrics.density;

        updateAccountTimeFilterMenuItem();

		// timeline listview
		phList = (PullToRefreshPinnedHeaderListView) findViewById(R.id.pinnedHeaderListView1);
		pHAdapter = new TimelinePinnedHeaderAdapter(this,getResources(),(int)(screenDpWidth/2)-50);
		pHAdapter.setItems(listItems);
		phList.setPullToRefreshOverScrollEnabled(false);
		phList.setMode(Mode.BOTH);
		phList.setAdapter(pHAdapter);
		phList.setScrollingWhileRefreshingEnabled(true);
		phList.setScrollContainer(false);
		phList.getLoadingLayoutProxy().setTextTypeface(persianBoldTypeface);
		phList.getLoadingLayoutProxy().setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f);
		phList.getLoadingLayoutProxy().setTextInclueFontPadding(false);
		phList.getLoadingLayoutProxy().setSubTextTypeface(persianTypeface);
		phList.getLoadingLayoutProxy().setSubTextInclueFontPadding(false);
		phList.getLoadingLayoutProxy().setSubTextSize(TypedValue.COMPLEX_UNIT_SP, 17f);
		phList.setOnPullEventListener(this);
		phList.setOnRefreshListener(this);

        phList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                OnClickedDialog onClickedDialog=new OnClickedDialog(TimelineActivity.this,((TimelineItem2)adapterView.getAdapter().getItem(i)),getResources());
                onClickedDialog.show();
            }
        });


//
//        dialog=new LodingDialog(this,getResources(),"در حال دریافت اطلاعات از سرور");
//        dialog.show();



//        getALLTransFromServer();
        doSync(getBaseContext());
        updateAccountTimeFilterMenuItem();
            addSlidingMenu();

            setTitleText();
            setPullToRefreshLabels();
            getSharedPreferences();


    }

    @Override
    protected void onResume() {

        try {

//            updateAccountTimeFilterMenuItem();
//            addSlidingMenu();
//
//            setTitleText();
//            setPullToRefreshLabels();
//            getSharedPreferences();


            immediateRefreshTimeline();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Log.e("debug","onResume Called");
//		//TODO change to dip
        GridLayout expenses = (GridLayout) findViewById(R.id.expense_grid);

        int count = expenses.getChildCount();
        for(int i = 0; i < count; i++)
            if(expenseSelection[i])
                expenses.getChildAt(i).getBackground().setAlpha(FULLY_OPAQUE);
            else
                expenses.getChildAt(i).getBackground().setAlpha(HALF_TRANSPARENT);

        GridLayout incomes = (GridLayout) findViewById(R.id.incomes_grid);

        count = incomes.getChildCount();
        for(int i = 0; i < count; i++)
            if(incomeSelection[i])
                incomes.getChildAt(i).getBackground().setAlpha(FULLY_OPAQUE);
            else
                incomes.getChildAt(i).getBackground().setAlpha(HALF_TRANSPARENT);

        super.onResume();
        ((ImageButton)(findViewById(R.id.add_button))).getDrawable().setAlpha(FULLY_OPAQUE);
//		new RefreshTask().execute();


    }

    @Override
    protected void onStop(){
        super.onStop();
        SharedPreferences settings = getSharedPreferences(PREFRENCES_FILE, 0);
        SharedPreferences.Editor editor = settings.edit();

        for(int i=0;i< accountTimeFilterMenuItem.size();i++){
            if (!accountTimeFilterMenuItem.get(i).isRadioButton)
                editor.putBoolean("AccountsAndTime:"+ accountTimeFilterMenuItem.get(i).text, accountTimeFilterMenuItem.get(i).isChecked);
            else
                editor.putBoolean("AccountsAndTime:"+ accountTimeFilterMenuItem.get(i).text,false);
        }
        editor.putBoolean("AccountsAndTime:"+getSelectedRadioTimeString(),true);
        for(int i=0;i<expenseSelection.length;i++){
            editor.putBoolean("expenseCatId:"+i,expenseSelection[i]);
        }
        for(int i=0;i<incomeSelection.length;i++){
            editor.putBoolean("incomeCatId:"+i,incomeSelection[i]);
        }
        // Commit the edits!
        editor.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_timeline, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        Intent intent = null;
        boolean doNothig=false;
        if(item.toString().equals(getResources().getString(R.string.action_uncategorized))){
            intent=new Intent(TimelineActivity.this, UncategoriedActivity.class);
        }
        else if(item.toString().equals(getResources().getString(R.string.action_settings))){
            intent=new Intent(TimelineActivity.this, SettingActivity.class);
        }
        else if(item.toString().equals(getResources().getString(R.string.action_charts))){
            intent=new Intent(TimelineActivity.this, ChartActivity.class);
        }
        else if(item.toString().equals(getResources().getString(R.string.action_timeline))){
            doNothig=true;
        }
        if(!doNothig){
            if(intent!=null){
//            finish();
                startActivityForResult(intent,100);
                overridePendingTransition(0, 0);
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==SettingActivity.LOGOUT_CODE)
        {
            LocalDBServices.invalidTokens(this);
            Intent intent= new Intent(TimelineActivity.this,LoginActivity2.class);
            finish();
            startActivity(intent);
            return;
        }
    }

    @Override
    public void onClick(View button){
        if(button.getId() == addButton.getId())
        {
            button.setSelected(!button.isSelected());
            Intent intent = new Intent(TimelineActivity.this, AddPage1.class);
            TimelineActivity.this.startActivity(intent);
        }
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        finish();
    }



	private void refreshTimeline() throws ParseException{

        if(isDatabaseChanged()){
            System.out.println("refreshTimeline called");
            Cursor cursor=LocalDBServices.getTransactionsFromDB(this, expenseSelection, incomeSelection, accountMenuAdapter.getRadioSelected(), accountMenuAdapter, filterDate);
            makeItemFromCursor(cursor);
            cursor.close();
            String colName = "totalExpense";
            Cursor c=LocalDBServices.getTotalExpense(this);
            c.moveToFirst();
            final Long totalExp = c.getLong(c.getColumnIndexOrThrow(colName));
            String totalst=c.getString(c.getColumnIndexOrThrow(colName));
            Log.e("debug","total expense: "+totalExp);
            Log.e("debug","total!!expense: "+totalst);
            c.close();

            colName = "totalIncome";
            c=LocalDBServices.getTotalIncome(this);
            c.moveToFirst();
            final Long totalInc = c.getLong(c.getColumnIndexOrThrow(colName));
            c.close();

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.e("debug","totalExp string  "+Utils.toPersianNumbers(Currency.getStdAmount(totalExp)));
                    totalExpense.setText("  " + Utils.toPersianNumbers(Currency.getStdAmount(totalExp)));
                    totalIncome.setText("  "+Utils.toPersianNumbers(Currency.getStdAmount(totalInc)));
                }
            });

        }



	}

	public void immediateRefreshTimeline() throws IllegalArgumentException, ParseException{
        accountSelection=new boolean[accountMenuAdapter.getAccountsSelection().length];
        System.arraycopy(accountMenuAdapter.getAccountsSelection(),0,accountSelection,0,accountMenuAdapter.getAccountsSelection().length);
        pHAdapter.notifyDataSetChanged();
        if(isDatabaseChanged()){
            System.out.println("immediateRefreshTimeline called");
            Cursor cursor=LocalDBServices.getTransactionsFromDB(this,expenseSelection,incomeSelection,accountMenuAdapter.getRadioSelected(),accountMenuAdapter,filterDate);
            makeItemFromCursor(cursor);
            cursor.close();

            pHAdapter.notifyDataSetChanged();
			String colName = "totalExpense";
            Cursor c=LocalDBServices.getTotalExpense(this);
			c.moveToFirst();
			Long total = c.getLong(c.getColumnIndexOrThrow(colName));
            c.close();
            totalExpense.setText("  "+Utils.toPersianNumbers(Currency.getStdAmount(total)));
            Currency.setCurrencyLayout(totalExpenseCurrencyLayout, this, getResources().getColor(R.color.red), persianTypeface, Currency.SMALL_TEXT_SIZE);

			colName = "totalIncome";
            c=LocalDBServices.getTotalIncome(this);
			c.moveToFirst();
			total = c.getLong(c.getColumnIndexOrThrow(colName));
            c.close();
			totalIncome.setText("  "+Utils.toPersianNumbers(Currency.getStdAmount(total)));
            Currency.setCurrencyLayout(totalIncomeCurrencyLayout, this, getResources().getColor(R.color.green), persianTypeface, Currency.SMALL_TEXT_SIZE);
		}
	}

	private boolean isDatabaseChanged() throws ParseException{
        return true;
	}

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	private int getSlidingMenuRightPaddding(int column, int resID){
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
	private int getSlidingMenuNumberOfColumns(GridLayout gridLayout, int resourceID){
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
            if(column==0)
                column=1;
		return column;
	}


	private class SyncTask extends AsyncTask<Void, Void, Void>{
        // every time we need to get data from server and refresh timeline after it

		@Override
		protected Void doInBackground(Void... params)
		{
			// Simulates a background job.
			try
			{
//                Log.e("debug","try to declare!");
//                new Declaration(TimelineActivity.this);
//                Log.e("debug","try to get data!");
//                getALLTransFromServer();

                Log.e("debug","try to declare!");
                new Declaration(TimelineActivity.this);
                Log.e("debug","try to get data!");
//                new ConnectionManager().doSync(getBaseContext());
//                doSync(getBaseContext());
                doSync(getBaseContext());

            }
			catch (Exception e1)
			{
				e1.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result)
		{
//			totalIncome.setText(totalIncomeString);
//			totalExpense.setText(totalExpenseString);
//            totalExpense.setText("##");
			pHAdapter.setItems(listItems);
			pHAdapter.notifyDataSetChanged();
			phList.onRefreshComplete();
			setPullToRefreshLabels();
			setTitleText();
			super.onPostExecute(result);
		}
	}

	private class RefreshTask extends AsyncTask<Void, Void, Void>{
            //every time we pull or push that should change the items
            // it only calls refreshTimeline()
		@Override
		protected Void doInBackground(Void... params)
		{
			// Simulates a background job.
			try
			{
				refreshTimeline();
			}
			catch (ParseException e1)
			{
				e1.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result)
		{
//			totalIncome.setText(totalIncomeString);
//			totalExpense.setText(totalExpenseString);
			pHAdapter.setItems(listItems);
			pHAdapter.notifyDataSetChanged();
			phList.onRefreshComplete();
			setTitleText();
			//TODO scroll up
			super.onPostExecute(result);
		}
	}

    private class FirstSynchTask extends AsyncTask<Void, Void, Void>{
        //every time we pull or push that should change the items
        // it only calls refreshTimeline()
        @Override
        protected Void doInBackground(Void... params)
        {
            // Simulates a background job.
            try
            {

        dialog=new LodingDialog(TimelineActivity.this,getResources(),"در حال دریافت اطلاعات از سرور");
        dialog.show();
                doSync(getBaseContext());



        updateAccountTimeFilterMenuItem();
            addSlidingMenu();

            setTitleText();
            setPullToRefreshLabels();
            getSharedPreferences();

            }
            catch (Exception e1)
            {
                e1.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result)
        {
//			totalIncome.setText(totalIncomeString);
//			totalExpense.setText(totalExpenseString);
            pHAdapter.setItems(listItems);
            pHAdapter.notifyDataSetChanged();
            phList.onRefreshComplete();
            setTitleText();
            //TODO scroll up
            super.onPostExecute(result);
        }
    }

	@Override
	public void onPullDownToRefresh(PullToRefreshBase<PinnedHeaderListView> refreshView){
		if(shouldSync())
		{
			// Do work to refresh the list here.
			new SyncTask().execute();


            try {
                updateAccountTimeFilterMenuItem();
                addSlidingMenu();
                immediateRefreshTimeline();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return;
		}
		
		switch(accountMenuAdapter.getRadioSelected())
		{
		case DAILY:
			filterDate.add(PersianCalendar.DAY_OF_MONTH, +1);
			break;
		case WEEKLY:
			filterDate.add(PersianCalendar.DAY_OF_MONTH, +7);
			break;
		case MONTHLY:
			filterDate.add(PersianCalendar.MONTH, +1);
			break;
		case YEARLY:
			filterDate.add(PersianCalendar.YEAR, +1);
			break;
		}

//		caltext.setText(getFilterDate()+"\n"+counter);
		counter++;
		{
			// Do work to refresh the list here.
			new RefreshTask().execute();
		}
	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<PinnedHeaderListView> refreshView){
		switch(accountMenuAdapter.getRadioSelected())
		{
		case DAILY:
			filterDate.add(PersianCalendar.DAY_OF_MONTH, -1);
			break;
		case WEEKLY:
			filterDate.add(PersianCalendar.DAY_OF_MONTH, -7);
			break;
		case MONTHLY:
			filterDate.add(PersianCalendar.MONTH, -1);
			break;
		case YEARLY:
			filterDate.add(PersianCalendar.YEAR, -1);
			break;
		}

//		caltext.setText(getFilterDate()+"\n"+counter);
		counter++;
		// Do work to refresh the list here.
		new RefreshTask().execute();
	}

	@Override
	public void onPullEvent(PullToRefreshBase<PinnedHeaderListView> refreshView,State state,Mode direction){
		if(state == State.PULL_TO_REFRESH && direction == Mode.PULL_FROM_START)
		{
			if(shouldSync()){
				PersianCalendar tmpCal = new PersianCalendar();
				Calendar tmpTime = Calendar.getInstance();

				int dayOfWeek = tmpCal.get(PersianCalendar.DAY_OF_WEEK);
				int dayOfMonth = tmpCal.get(PersianCalendar.DAY_OF_MONTH);
				int month = tmpCal.get(PersianCalendar.MONTH);
//				String date = PersianCalendar.weekdayFullNames[dayOfWeek]+" "+dayOfMonth+" "+PersianCalendar.months[month];
                String tmpDate=LocalDBServices.getSyncTime(this);
                String date="";
                if(tmpDate.length()>7) {
                    date = tmpDate.substring(0, 4) + "/"+tmpDate.substring(4,6)+ "/" + tmpDate.substring(6, 8);
                }
//				int minute = tmpTime.get(Calendar.MINUTE);
//				int hour = tmpTime.get(Calendar.HOUR_OF_DAY);
//				String time = hour+":"+String.format("%02d", minute);

				refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(lastUpdateString);
				// Update the lastUpdatedString
				lastUpdateString = Utils.toPersianNumbers(date);

				refreshView.getLoadingLayoutProxy().setPullLabelUp(
						getResources().getString(R.string.sync_string));

				refreshView.getLoadingLayoutProxy().setReleaseLabelUp(
						getResources().getString(R.string.sync_string));
			}
			else
				refreshView.getLoadingLayoutProxy().setLastUpdatedLabel("");
		}
		else if(state == State.PULL_TO_REFRESH && direction == Mode.PULL_FROM_END)
			refreshView.getLoadingLayoutProxy().setLastUpdatedLabel("");
	}

    private void updateAccountTimeFilterMenuItem() {
        accountTimeFilterMenuItem = new ArrayList<FilterMenuItem>();
        Cursor c2;
        c2= LocalDBServices.getAccountList(this);
        c2.moveToFirst();
        if(c2.getCount() != 0)
        {
            do
            {

                String accountName=c2.getString(c2.getColumnIndexOrThrow(TransactionsContract.Accounts.COLUMN_NAME_Account_Name));
                if(c2!=null){
                    if(c2.isFirst())
                        accountTimeFilterMenuItem.add(new FilterMenuItem("حساب‌ها", true,accountName, false, R.drawable.vaam_raw));
                    else
                        accountTimeFilterMenuItem.add(new FilterMenuItem("", true,accountName, false, R.drawable.vaam_raw));
                }
            }while(c2.moveToNext());
        }
        c2.close();

        accountTimeFilterMenuItem.add(new FilterMenuItem(getResources().getString(R.string.time_interval),
                false, getResources().getString(R.string.daily), true, 0));
        accountTimeFilterMenuItem.add(new FilterMenuItem("", false,
                getResources().getString(R.string.weekly), true, 0));
        accountTimeFilterMenuItem.add(new FilterMenuItem("", false,
                getResources().getString(R.string.monthly), true, 0));
        accountTimeFilterMenuItem.add(new FilterMenuItem("", true,
                getResources().getString(R.string.yearly), true, 0));
    }

    private void setTitleText(){
        switch(accountMenuAdapter.getRadioSelected())
        {
            case DAILY:
                titleText = new PersianDate((short)filterDate.get(PersianCalendar.DATE),
                        (short)filterDate.get(PersianCalendar.MONTH),
                        (short)filterDate.get(PersianCalendar.YEAR),
                        PersianCalendar.weekdayFullNames[filterDate.get(PersianCalendar.DAY_OF_WEEK)])
                        .toString();
                break;
            case WEEKLY:
                titleText = "هفته" + " " + ordinal[filterDate.get(PersianCalendar.WEEK_OF_MONTH)]
                        + " " + PersianCalendar.months[filterDate.get(PersianCalendar.MONTH)];
                break;
            case MONTHLY:
                titleText = PersianCalendar.months[filterDate.get(PersianCalendar.MONTH)] + " " +
                        filterDate.get(PersianCalendar.YEAR);
                break;
            case YEARLY:
                titleText = filterDate.get(PersianCalendar.YEAR)+"";
                break;
        }
        title.setText(Utils.toPersianNumbers(titleText));
    }

    private boolean shouldSync(){
        // TODO set to greatest date
        PersianCalendar tmpCal = new PersianCalendar();
        switch(accountMenuAdapter.getRadioSelected())
        {
            case DAILY:
                return tmpCal.equalDay(filterDate);
            case WEEKLY:
                return tmpCal.equalWeek(filterDate);
            case MONTHLY:
                return tmpCal.equalMonth(filterDate);
            case YEARLY:
                return tmpCal.equalYear(filterDate);
        }

        return false;
    }

	private void setPullToRefreshLabels(){
		String upLabel = "";
		String bottomLabel = "";
		switch (accountMenuAdapter.getRadioSelected())
		{
		case DAILY:
			upLabel = "روز بعد";
			bottomLabel = "روز قبل";
			break;
		case WEEKLY:
			upLabel = "هفته‌ی بعد";
			bottomLabel = "هفته‌ی قبل";
			break;
		case MONTHLY:
			upLabel = "ماه بعد";
			bottomLabel = "ماه قبل";
			break;
		case YEARLY:
			upLabel = "سال بعد";
			bottomLabel = "سال قبل";
			break;
		}
		phList.getLoadingLayoutProxy().setPullLabelUp(upLabel);
		phList.getLoadingLayoutProxy().setPullLabelBottom(bottomLabel);
		phList.getLoadingLayoutProxy().setReleaseLabelUp(upLabel);
		phList.getLoadingLayoutProxy().setReleaseLabelBottom(bottomLabel);
		phList.getLoadingLayoutProxy().setLastUpdatedLabel(" ");
	}

    private void makeItemFromCursor(Cursor c){

       c.moveToFirst();
       listItems.clear();
       if(c.getCount() == 0)
       {
           listItems = new ArrayList<TimelineItem2>();
           pHAdapter.setItems(listItems);
           totalExpenseString = "  "+Utils.toPersianNumbers(Currency.getStdAmount((long) 0.0));
           totalIncomeString = "  "+Utils.toPersianNumbers(Currency.getStdAmount((long) 0.0));
           return;
       }

       PersianCalendar tmpCal;
       String lastTransID="null";
       ArrayList<String> tags=new ArrayList<String>();
       String id="null";
       String dateTime="";
       long amount=-1;
       boolean isExpense=false;
       boolean isHandy=false;
       String descp="";
       String accountName="";
       int category_index = -1;
       int year;
       int month;
       int day;
       int hour;
       int minute;
       do
       {
           String transID=c.getString(c.getColumnIndexOrThrow(TransactionEntry.COLUMN_NAME_TRANSACTION_ID));
           if(c.isLast()){
               if(!lastTransID.equals(transID)){
                   if(!lastTransID.equals("null"))
                   listItems.add(new TimelineItem2(id,isExpense,amount,dateTime,category_index,tags,descp,accountName,null,null,false,false,null));
                   tags=new ArrayList<String>();
               }
               String tagName=c.getString(c.getColumnIndexOrThrow(TransactionsContract.TagsEntry.COLUMN_NAME_TAG));
               tags.add(tagName);
               id = c.getString(c.getColumnIndexOrThrow(TransactionEntry.COLUMN_NAME_TRANSACTION_ID));
               dateTime = c.getString(c.getColumnIndexOrThrow(TransactionEntry.COLUMN_NAME_DATE_TIME));
               amount = c.getLong(c.getColumnIndexOrThrow(TransactionEntry.COLUMN_NAME_AMOUNT));
               isExpense = c.getInt(c.getColumnIndexOrThrow(TransactionEntry.COLUMN_NAME_IS_EXPENSE))==0? false: true;
               isHandy= c.getInt(c.getColumnIndexOrThrow(TransactionEntry.COLUMN_NAME_IS_HANDY))==0? false: true;
               category_index = c.getInt(c.getColumnIndexOrThrow(TransactionEntry.COLUMN_NAME_CATEGORY));
               descp=c.getString(c.getColumnIndexOrThrow(TransactionEntry.COLUMN_NAME_DESCRIPTION));
               accountName=c.getString(c.getColumnIndexOrThrow(TransactionEntry.COLUMN_NAME_ACCOUNT_NAME));


               listItems.add(new TimelineItem2(id,isExpense,amount,dateTime,category_index,tags,descp,accountName,null,null,isHandy,false,null));
           }
           else {
               if(!lastTransID.equals(transID)){
                   if(!lastTransID.equals("null")){
                       listItems.add(new TimelineItem2(id,isExpense,amount,dateTime,category_index,tags,descp,accountName,null,null,isHandy,false,null));
                   }
                   tags=new ArrayList<String>();
                   String tagName=c.getString(c.getColumnIndexOrThrow(TransactionsContract.TagsEntry.COLUMN_NAME_TAG));
                   tags.add(tagName);
                   id = c.getString(c.getColumnIndexOrThrow(TransactionEntry.COLUMN_NAME_TRANSACTION_ID));
                   dateTime = c.getString(c.getColumnIndexOrThrow(TransactionEntry.COLUMN_NAME_DATE_TIME));
                   amount = c.getLong(c.getColumnIndexOrThrow(TransactionEntry.COLUMN_NAME_AMOUNT));
                   isExpense = c.getInt(c.getColumnIndexOrThrow(TransactionEntry.COLUMN_NAME_IS_EXPENSE))==0? false: true;
                   isHandy = c.getInt(c.getColumnIndexOrThrow(TransactionEntry.COLUMN_NAME_IS_HANDY))==0? false: true;
                   category_index = c.getInt(c.getColumnIndexOrThrow(TransactionEntry.COLUMN_NAME_CATEGORY));
                   descp=c.getString(c.getColumnIndexOrThrow(TransactionEntry.COLUMN_NAME_DESCRIPTION));
                   accountName=c.getString(c.getColumnIndexOrThrow(TransactionEntry.COLUMN_NAME_ACCOUNT_NAME));

//                   year = Integer.parseInt(dateTime.substring(0, 4));
//                   month = Integer.parseInt(dateTime.substring(4, 6));
//                   day = Integer.parseInt(dateTime.substring(6, 8));
//                   hour = Integer.parseInt(dateTime.substring(8, 10));
//                   minute = Integer.parseInt(dateTime.substring(10, 12));

//                   tmpCal = new PersianCalendar(year, month, day);
//                   date = new PersianDate((short)day, (short)(month+1), (short)year, PersianCalendar.weekdayFullNames[tmpCal.get(PersianCalendar.DAY_OF_WEEK)]);
//                   time = new Time((short)hour, (short)minute);
                   lastTransID=transID;
               }
               else {
                   String tagName=c.getString(c.getColumnIndexOrThrow(TransactionsContract.TagsEntry.COLUMN_NAME_TAG));
                   tags.add(tagName);
               }
           }
       }while(c.moveToNext());
       c.close();
       pHAdapter.setItems(listItems);
   }

    private void addSlidingMenu(){

       // configure the SlidingMenu
       Log.d("debug","addSliding menu called");
       SlidingMenu menu = new SlidingMenu(this);
       menu.setMode(SlidingMenu.LEFT_RIGHT);
       menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
       menu.setShadowWidthRes(R.dimen.shadow_width);
       menu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
       menu.setBehindOffsetRes(R.dimen.sliding_menu_behid_offset);
       menu.setOnCloseListener(new SlidingMenu.OnCloseListener()
       {
           @Override
           public void onClose()
           {
               setPullToRefreshLabels();
               try {
                   immediateRefreshTimeline();
               } catch (NumberFormatException e) {
                   e.printStackTrace();
               } catch (IllegalArgumentException e) {
                   e.printStackTrace();
               } catch (ParseException e) {
                   e.printStackTrace();
               }
               setTitleText();
           }
       });

       // left menu:
       menu.setMenu(R.layout.left_menu_layout);
       menu.setShadowDrawable(R.drawable.left_shadow);
       PinnedHeaderListView accountMenu = (PinnedHeaderListView) findViewById(R.id.account_time_filter_listview);
       accountMenuAdapter = new FilterMenuAdapter(this, null);
       accountMenuAdapter.setItems(accountTimeFilterMenuItem);
       accountMenu.setAdapter(accountMenuAdapter);

       // right menu:
       menu.setSecondaryMenu(R.layout.right_menu_layout);
       menu.setSecondaryShadowDrawable(R.drawable.right_shadow);

       RelativeLayout header = (RelativeLayout) findViewById(R.id.expense_header);
       ((TextView)header.findViewById(R.id.side_menu_header)).setTypeface(persianTypeface);
       ((TextView)header.findViewById(R.id.side_menu_header))
               .setText(getResources().getString(R.string.expenses));

       header = (RelativeLayout) findViewById(R.id.income_header);
       ((TextView)header.findViewById(R.id.side_menu_header)).setTypeface(persianTypeface);
       ((TextView)header.findViewById(R.id.side_menu_header))
               .setText(getResources().getString(R.string.incomes));

       GridLayout expenses = (GridLayout) findViewById(R.id.expense_grid);
       expenses.setPadding((int)(getResources().getDimension(R.dimen.shadow_width)/getResources().getDisplayMetrics().density)+5, 0,
               (int)(getResources().getDimension(R.dimen.shadow_width)/getResources().getDisplayMetrics().density)+5, 0);
       int column = getSlidingMenuNumberOfColumns(expenses, Category.getExpenseIconID(Category.EXPENSE_UNCATEGORIZED));
       expenses.setColumnCount(column);
       expenses.setPadding(getSlidingMenuRightPaddding(column, Category.getExpenseIconID(Category.EXPENSE_UNCATEGORIZED)), 0, getSlidingMenuRightPaddding(column, Category.getExpenseIconID(Category.EXPENSE_UNCATEGORIZED)), 0);
       for(int i = 0; i < Category.EXPENSE_SIZE; i++)
       {
           Spec row = GridLayout.spec(i/column);
           Spec col = GridLayout.spec(column-(i%column)-1);
           ImageButton imageButton = new ImageButton(this);
           final int tmpIndex = i;
           imageButton.setOnClickListener(new OnClickListener()
           {
               @Override
               public void onClick(View button)
               {
                   expenseSelection[tmpIndex] = !expenseSelection[tmpIndex];
                   if(!expenseSelection[tmpIndex])
                   {
                       button.getBackground().setAlpha(HALF_TRANSPARENT);
                   }
                   else
                   {
                       button.getBackground().setAlpha(FULLY_OPAQUE);
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
           Spec row = GridLayout.spec(i/column);
           Spec col = GridLayout.spec(column-(i%column)-1);
           ImageButton imageButton = new ImageButton(this);
           final int tmpIndex = i;
           imageButton.setOnClickListener(new OnClickListener()
           {
               @Override
               public void onClick(View button)
               {
                   incomeSelection[tmpIndex] = !incomeSelection[tmpIndex];
//                   button.setSelected(!button.isSelected());
                   if(!incomeSelection[tmpIndex])
                   {
                       button.getBackground().setAlpha(HALF_TRANSPARENT);
                   }
                   else
                   {
                       button.getBackground().setAlpha(FULLY_OPAQUE);
                   }
               }
           });
           imageButton.setLayoutParams(new GridLayout.LayoutParams(row, col));
           imageButton.setBackgroundResource(Category.getIncomeIconID(i));
           incomes.addView(imageButton);
       }

   }

    private String getSelectedRadioTimeString(){
    if(accountMenuAdapter.getRadioSelected()==YEARLY)
        return getResources().getString(R.string.yearly);
    else if(accountMenuAdapter.getRadioSelected()==MONTHLY)
        return getResources().getString(R.string.monthly);
    else if(accountMenuAdapter.getRadioSelected()==WEEKLY)
        return getResources().getString(R.string.weekly);
    else
        return getResources().getString(R.string.daily);
}

    private  void getSharedPreferences(){
    SharedPreferences preferences=getSharedPreferences(PREFRENCES_FILE,0);


    for(int i=0;i< accountTimeFilterMenuItem.size();i++){
        accountTimeFilterMenuItem.get(i).isChecked=preferences.getBoolean("AccountsAndTime:"+ accountTimeFilterMenuItem.get(i).text,true);
    }
    accountMenuAdapter.setRadioSelected();
    for(int i=0;i<expenseSelection.length;i++){
        expenseSelection[i]=preferences.getBoolean("expenseCatId:"+i,true);
    }
    for(int i=0;i<incomeSelection.length;i++){
        incomeSelection[i]=preferences.getBoolean("incomeCatId:"+i,true);
    }

}

    private boolean getTokensFromDB(){
        Boolean res=LocalDBServices.getTokens(this);
        if(res){
            return true;
        }
        else {

            LocalDBServices.invalidTokens(this);
            Intent intent= new Intent(TimelineActivity.this,LoginActivity2.class);
            finish();
            startActivity(intent);
            return false;
        }

    }

    private void getNewTransFromServer(){

        try {
            Log.e("debug","getNewTransFromServer called");
            JsonParser jsonParser=JsonParser.getInstance();
//            Account account= jsonParser.getUserAccount();
//            String transExpenseIn=jsonParser.getAllTransaction(account, "d", "0");
            String accountIn=jsonParser.getAccountInfo();
            Account account=jsonParser.readAndParseAccountJSON(accountIn);
            LocalDBServices.addJsonAccounts(getBaseContext(),account);


            String transExpenseIn=jsonParser.getnewTransactions(this,account, "d", "0");
            jsonParser.readAndParseTransactionJSON(transExpenseIn);
            ArrayList <TimelineItem2> t2=jsonParser.getTransItems();
            for(int i=0;i<t2.size();i++){
                LocalDBServices.addJsonTransactionForce(getBaseContext(), t2.get(i).getTransactionID(), t2.get(i).getDateString(), t2.get(i).getAmount(), t2.get(i).isExpence(), t2.get(i).getAccountName(), t2.get(i).getCategoryID(), t2.get(i).getTags(), t2.get(i).getDescription(),t2.get(i).getHandy());
            }
            if(t2.size()==100){
                int j=1;
                transExpenseIn=jsonParser.getnewTransactions(this,account, "d", j * 100 + "");
                jsonParser.readAndParseTransactionJSON(transExpenseIn);
                t2=jsonParser.getTransItems();
                for(int i=0;i<t2.size();i++){
                    LocalDBServices.addJsonTransactionForce(getBaseContext(), t2.get(i).getTransactionID(), t2.get(i).getDateString(), t2.get(i).getAmount(), t2.get(i).isExpence(), t2.get(i).getAccountName(), t2.get(i).getCategoryID(), t2.get(i).getTags(), t2.get(i).getDescription(),t2.get(i).getHandy());
                }
            }
            String transIncomeIn=jsonParser.getnewTransactions(this,account, "c", "0");
            jsonParser.readAndParseTransactionJSON(transIncomeIn);
            t2=jsonParser.getTransItems();
            for(int i=0;i<t2.size();i++){
                LocalDBServices.addJsonTransactionForce(getBaseContext(), t2.get(i).getTransactionID(), t2.get(i).getDateString(), t2.get(i).getAmount(), t2.get(i).isExpence(), t2.get(i).getAccountName(), t2.get(i).getCategoryID(), t2.get(i).getTags(), t2.get(i).getDescription(),t2.get(i).getHandy());
            }
            if(t2.size()==100){
                int j=1;
                transExpenseIn=jsonParser.getnewTransactions(this,account, "c", j * 100 + "");
                jsonParser.readAndParseTransactionJSON(transExpenseIn);
                t2=jsonParser.getTransItems();
                for(int i=0;i<t2.size();i++){
                    LocalDBServices.addJsonTransactionForce(getBaseContext(), t2.get(i).getTransactionID(), t2.get(i).getDateString(), t2.get(i).getAmount(), t2.get(i).isExpence(), t2.get(i).getAccountName(), t2.get(i).getCategoryID(), t2.get(i).getTags(), t2.get(i).getDescription(),t2.get(i).getHandy());
                }
            }

            PersianCalendar calendar = new PersianCalendar();
            int selectedDay = calendar.get(PersianCalendar.DAY_OF_MONTH);
            int selectedMonth = calendar.get(PersianCalendar.MONTH);
            int selectedYear = calendar.get(PersianCalendar.YEAR);
            int selectedWeekday = calendar.get(PersianCalendar.DAY_OF_WEEK);

            PersianDate date = new PersianDate((short)selectedDay, (short)selectedMonth, (short)selectedYear, PersianCalendar.weekdayFullNames[selectedWeekday]);
            int monthInt=Integer.parseInt(date.getSTDString().substring(4,6))+1;
            String dateString=date.getSTDString().substring(0,4)+monthInt+date.getSTDString().substring(6,8);
            LocalDBServices.updateSyncTime(getBaseContext(),dateString);
            Log.e("debug","sync time updated "+ LocalDBServices.getSyncTime(getBaseContext()));
        }
        catch (Exception e){
            e.printStackTrace();
            Log.e("debug","there is a problem on posting cookie, should try login again");
            LocalDBServices.invalidTokens(getBaseContext());
//            ConnectionManager.pfmCookie="";
//            ConnectionManager.pfmToken="";
            finish();
        }
    }

    private void getALLTransFromServer(){

        try {
            Log.e("debug performance","getNewTransFromServer called");
            JsonParser jsonParser=JsonParser.getInstance();
//            Account account= jsonParser.getUserAccount();
//            String transExpenseIn=jsonParser.getAllTransaction(account, "d", "0");
            String accountIn=jsonParser.getAccountInfo();
            Account account=jsonParser.readAndParseAccountJSON(accountIn);
            LocalDBServices.addJsonAccounts(getBaseContext(),account);

            Log.e("debug performance","1");
            String transExpenseIn=jsonParser.getAllTransaction(account, "d", "0");
            jsonParser.readAndParseTransactionJSON(transExpenseIn);
            ArrayList <TimelineItem2> t2=jsonParser.getTransItems();
            for(int i=0;i<t2.size();i++){
                LocalDBServices.addJsonTransactionForce(getBaseContext(), t2.get(i).getTransactionID(), t2.get(i).getDateString(), t2.get(i).getAmount(), t2.get(i).isExpence(), t2.get(i).getAccountName(), t2.get(i).getCategoryID(), t2.get(i).getTags(), t2.get(i).getDescription(),t2.get(i).getHandy());
            }
            Log.e("debug performance","2");
            if(t2.size()==100){
                int j=1;
                transExpenseIn=jsonParser.getAllTransaction(account, "d", j * 100 + "");
                jsonParser.readAndParseTransactionJSON(transExpenseIn);
                t2=jsonParser.getTransItems();
                for(int i=0;i<t2.size();i++){
                    LocalDBServices.addJsonTransactionForce(getBaseContext(), t2.get(i).getTransactionID(), t2.get(i).getDateString(), t2.get(i).getAmount(), t2.get(i).isExpence(), t2.get(i).getAccountName(), t2.get(i).getCategoryID(), t2.get(i).getTags(), t2.get(i).getDescription(),t2.get(i).getHandy());
                }
            }
            Log.e("debug performance","3");
            String transIncomeIn=jsonParser.getAllTransaction(account, "c", "0");
            jsonParser.readAndParseTransactionJSON(transIncomeIn);
            t2=jsonParser.getTransItems();
            for(int i=0;i<t2.size();i++){
                LocalDBServices.addJsonTransactionForce(getBaseContext(), t2.get(i).getTransactionID(), t2.get(i).getDateString(), t2.get(i).getAmount(), t2.get(i).isExpence(), t2.get(i).getAccountName(), t2.get(i).getCategoryID(), t2.get(i).getTags(), t2.get(i).getDescription(),t2.get(i).getHandy());
            }
            Log.e("debug performance","4");
            if(t2.size()==100){
                int j=1;
                transExpenseIn=jsonParser.getAllTransaction(account, "c", j * 100 + "");
                jsonParser.readAndParseTransactionJSON(transExpenseIn);
                t2=jsonParser.getTransItems();
                for(int i=0;i<t2.size();i++){
                    LocalDBServices.addJsonTransactionForce(getBaseContext(), t2.get(i).getTransactionID(), t2.get(i).getDateString(), t2.get(i).getAmount(), t2.get(i).isExpence(), t2.get(i).getAccountName(), t2.get(i).getCategoryID(), t2.get(i).getTags(), t2.get(i).getDescription(),t2.get(i).getHandy());
                }
            }

            Log.e("debug performance","5");
            PersianCalendar calendar = new PersianCalendar();
            int selectedDay = calendar.get(PersianCalendar.DAY_OF_MONTH);
            int selectedMonth = calendar.get(PersianCalendar.MONTH);
            int selectedYear = calendar.get(PersianCalendar.YEAR);
            int selectedWeekday = calendar.get(PersianCalendar.DAY_OF_WEEK);

            PersianDate date = new PersianDate((short)selectedDay, (short)selectedMonth, (short)selectedYear, PersianCalendar.weekdayFullNames[selectedWeekday]);
            int monthInt=Integer.parseInt(date.getSTDString().substring(4,6))+1;
            String dateString=date.getSTDString().substring(0,4)+monthInt+date.getSTDString().substring(6,8);
            LocalDBServices.updateSyncTime(getBaseContext(),dateString);
            Log.e("debug","sync time updated "+ LocalDBServices.getSyncTime(getBaseContext()));
        }
        catch (Exception e){
            e.printStackTrace();
            Log.e("debug","there is a problem on posting cookie, should try login again");
            LocalDBServices.invalidTokens(getBaseContext());
//            ConnectionManager.pfmCookie="";
//            ConnectionManager.pfmToken="";
            finish();
        }
    }

    public boolean doSync(Context context){

        try {
            Log.e("debug", "getNewTransFromServer called");
            JsonParser jsonParser=JsonParser.getInstance();
            String accountIn=jsonParser.getAccountInfo();
            Account account=jsonParser.readAndParseAccountJSON(accountIn);
            LocalDBServices.addJsonAccounts(context, account);

            Log.e("debug", "expense called");
            new GetAllTrans(account,context,"d").execute();
            Log.e("debug", "income called");
            new GetAllTrans(account,context,"c").execute();

            Log.e("debug", "update time called");
            PersianCalendar calendar = new PersianCalendar();
            int selectedDay = calendar.get(PersianCalendar.DAY_OF_MONTH);
            int selectedMonth = calendar.get(PersianCalendar.MONTH);
            int selectedYear = calendar.get(PersianCalendar.YEAR);
            int selectedWeekday = calendar.get(PersianCalendar.DAY_OF_WEEK);

            PersianDate date = new PersianDate((short)selectedDay, (short)selectedMonth, (short)selectedYear, PersianCalendar.weekdayFullNames[selectedWeekday]);
            int monthInt=Integer.parseInt(date.getSTDString().substring(4,6))+1;
            String dateString=date.getSTDString().substring(0,4)+monthInt+date.getSTDString().substring(6,8);
            LocalDBServices.updateSyncTime(context,dateString);
            Log.e("debug","sync time updated "+ LocalDBServices.getSyncTime(context));
        }
        catch (Exception e){
            e.printStackTrace();
            Log.e("debug","there is a problem on posting cookie, should try login again");
            LocalDBServices.invalidTokens(context);
//            ConnectionManager.pfmCookie="";
//            ConnectionManager.pfmToken="";
            return false;
        }
        return true;
    }

    private class GetAllTrans extends AsyncTask<Void, Void, Void> {
        Account account;
        Context context;
        String type;
        public GetAllTrans(Account account,Context context,String type){
            this.account=account;
            this.context=context;
            this.type=type;

        }

        @Override
        protected Void doInBackground(Void... voids) {

            JsonParser jsonParser=JsonParser.getInstance();
            String transExpenseIn=jsonParser.getAllTransaction(account,type, "0");
            jsonParser.readAndParseTransactionJSON(transExpenseIn);
            ArrayList<TimelineItem2> t2=jsonParser.getTransItems();
            for(int i=0;i<t2.size();i++){
                LocalDBServices.addJsonTransactionForce(context,t2.get(i).getTransactionID(), t2.get(i).getDateString(), t2.get(i).getAmount(), t2.get(i).isExpence(), t2.get(i).getAccountName(), t2.get(i).getCategoryID(), t2.get(i).getTags(), t2.get(i).getDescription(),t2.get(i).getHandy());
            }
            Log.e("debug", "try to get more on type: "+type);
            if(t2.size()==100){
                int j=1;
                transExpenseIn=jsonParser.getAllTransaction(account, type, j * 100 + "");
                jsonParser.readAndParseTransactionJSON(transExpenseIn);
                t2=jsonParser.getTransItems();
                for(int i=0;i<t2.size();i++){
                    LocalDBServices.addJsonTransactionForce(context,t2.get(i).getTransactionID(), t2.get(i).getDateString(), t2.get(i).getAmount(), t2.get(i).isExpence(), t2.get(i).getAccountName(), t2.get(i).getCategoryID(), t2.get(i).getTags(), t2.get(i).getDescription(),t2.get(i).getHandy());
                }
            }
            Log.e("debug", "thread finished on type: "+type);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            try {
                Log.e("debug performance", "try to immmediate refresh timeline");
//                immediateRefreshTimeline();
                updateAccountTimeFilterMenuItem();
                addSlidingMenu();
            immediateRefreshTimeline();
            } catch (ParseException e){
                    e.printStackTrace();
            }
        }
    }


}

