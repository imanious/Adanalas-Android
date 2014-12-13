package com.fourmob.datetimepicker.date;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.SystemClock;
import android.os.Vibrator;
import android.support.v4.app.DialogFragment;
import android.text.format.DateUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fourmob.datetimepicker.R;
import com.fourmob.datetimepicker.Utils;
import com.nineoldandroids.animation.ObjectAnimator;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
//import java.text.DateFormatSymbols;
//import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;

public class PersianDatePickerDialog extends DialogFragment implements View.OnClickListener, DatePickerController {

	public static final int PERSIAN = 0;
	public static final int GREGORIAN = 1;
	
	private static final String KEY_SELECTED_YEAR = "year";
	private static final String KEY_SELECTED_MONTH = "month";
	private static final String KEY_SELECTED_DAY = "day";
	private static final String KEY_VIBRATE = "vibrate";

	// https://code.google.com/p/android/issues/detail?id=13050
	private static final int MAX_YEAR_PERSIAN = 2037-621;
	private static final int MIN_YEAR_PERSIAN = 1902-621;

	private static final int MAX_YEAR_GREGORIAN = 2037;
	private static final int MIN_YEAR_GREGORIAN = 1902;

	private static final int UNINITIALIZED = -1;
	private static final int MONTH_AND_DAY_VIEW = 0;
	private static final int YEAR_VIEW = 1;

	public static final int ANIMATION_DELAY = 500;
	public static final String KEY_WEEK_START = "week_start";
	public static final String KEY_YEAR_START = "year_start";
	public static final String KEY_YEAR_END = "year_end";
	public static final String KEY_CURRENT_VIEW = "current_view";
	public static final String KEY_LIST_POSITION = "list_position";
	public static final String KEY_LIST_POSITION_OFFSET = "list_position_offset";

	private static SimpleDateFormat DAY_FORMAT = new SimpleDateFormat("dd", Locale.getDefault());
	private static SimpleDateFormat YEAR_FORMAT = new SimpleDateFormat("yyyy", Locale.getDefault());
	private DateFormatSymbols mDateFormatSymbols = new DateFormatSymbols();
	
	private int mMode = PERSIAN;
	
	private final Calendar mGregorianCalendar = Calendar.getInstance();
	private final PersianCalendar mPersianCalendar = new PersianCalendar();
	private HashSet<OnDateChangedListener> mListeners = new HashSet<OnDateChangedListener>();
	private OnDateSetListener mCallBack;

	private AccessibleDateAnimator mAnimator;
	private boolean mDelayAnimation = true;
	private long mLastVibrate;
	private int mCurrentView = UNINITIALIZED;

	private int mWeekStart = mPersianCalendar.getFirstDayOfWeek();
	private int mMaxYear = MAX_YEAR_PERSIAN;
	private int mMinYear = MIN_YEAR_PERSIAN;

	private String mDayPickerDescription;
	private String mYearPickerDescription;
	private String mSelectDay;
	private String mSelectYear;

	private TextView mDayOfWeekView;
	private DayPickerView mDayPickerView;
	private Button mDoneButton;
	private LinearLayout mMonthAndDayView;
	private TextView mSelectedDayTextView;
	private TextView mSelectedMonthTextView;
	private Vibrator mVibrator;
	private YearPickerView mYearPickerView;
	private TextView mYearView;
	private Typeface mTypeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL);

	private boolean mVibrate = true;
	private boolean mCloseOnSingleTapDay;

	private String mDoneButtonText;

	private void adjustDayInMonthIfNeeded(int month, int year) {
		int day = 0;
		if(mMode == PERSIAN)
			day = mPersianCalendar.get(Calendar.DAY_OF_MONTH);
		else if(mMode == GREGORIAN)
			day = mGregorianCalendar.get(Calendar.DAY_OF_MONTH);
		
		int daysInMonth = 30;
		if(mMode == PERSIAN)
			daysInMonth = Utils.getDaysInMonthPersian(month, year);
		else if(mMode == GREGORIAN)
			daysInMonth = Utils.getDaysInMonthGregorian(month, year);
		if (day > daysInMonth)
		{
			if(mMode == PERSIAN)
				mPersianCalendar.set(Calendar.DAY_OF_MONTH, daysInMonth);
			else if(mMode == GREGORIAN)
				mGregorianCalendar.set(Calendar.DAY_OF_MONTH, daysInMonth);
			
		}
	}

	public PersianDatePickerDialog() {
		// Empty constructor required for dialog fragment. DO NOT REMOVE
	}

	public static PersianDatePickerDialog newInstance(OnDateSetListener onDateSetListener, int year, int month, int day, int mMode) {
		return newInstance(onDateSetListener, year, month, day, true, mMode);
	}

	public static PersianDatePickerDialog newInstance(OnDateSetListener onDateSetListener, int year, int month, int day, boolean vibrate, int mMode) {
		PersianDatePickerDialog datePickerDialog = new PersianDatePickerDialog();
		datePickerDialog.initialize(onDateSetListener, year, month, day, vibrate, mMode);
		return datePickerDialog;
	}


	public void setVibrate(boolean vibrate) {
		mVibrate = vibrate;
	}

	private void setCurrentView(int currentView) {
		setCurrentView(currentView, false);
	}

	private void setCurrentView(int currentView, boolean forceRefresh) {
		long timeInMillis = 0L;
		if(mMode == PERSIAN)
			timeInMillis = mPersianCalendar.getTimeInMillis();
		else if(mMode == GREGORIAN)
			timeInMillis = mGregorianCalendar.getTimeInMillis();
		
		switch (currentView) {
		case MONTH_AND_DAY_VIEW:
			ObjectAnimator monthDayAnim = Utils.getPulseAnimator(mMonthAndDayView, 0.9F, 1.05F);
			if (mDelayAnimation) {
				monthDayAnim.setStartDelay(ANIMATION_DELAY);
				mDelayAnimation = false;
			}
			mDayPickerView.onDateChanged();
			if (mCurrentView != currentView || forceRefresh) {
				mMonthAndDayView.setSelected(true);
				mYearView.setSelected(false);
				mAnimator.setDisplayedChild(MONTH_AND_DAY_VIEW);
				mCurrentView = currentView;
			}
			monthDayAnim.start();
			String monthDayDesc = DateUtils.formatDateTime(getActivity(), timeInMillis, DateUtils.FORMAT_SHOW_DATE);
			mAnimator.setContentDescription(mDayPickerDescription + ": " + monthDayDesc);
			Utils.tryAccessibilityAnnounce(mAnimator, mSelectDay);
			break;
		case YEAR_VIEW:
			ObjectAnimator yearAnim = Utils.getPulseAnimator(mYearView, 0.85F, 1.1F);
			if (mDelayAnimation) {
				yearAnim.setStartDelay(ANIMATION_DELAY);
				mDelayAnimation = false;
			}
			mYearPickerView.onDateChanged();
			if (mCurrentView != currentView  || forceRefresh) {
				mMonthAndDayView.setSelected(false);
				mYearView.setSelected(true);
				mAnimator.setDisplayedChild(YEAR_VIEW);
				mCurrentView = currentView;
			}
			yearAnim.start();
			String dayDesc = null;
			if(mMode == PERSIAN)
				dayDesc = Utils.toPersianNumbers(mPersianCalendar.get(Calendar.YEAR)+"");
			else if(mMode == GREGORIAN)
				dayDesc = YEAR_FORMAT.format(timeInMillis);
			mAnimator.setContentDescription(mYearPickerDescription + ": " + dayDesc);
			Utils.tryAccessibilityAnnounce(mAnimator, mSelectYear);
			break;
		}
	}

	private void updateDisplay(boolean announce) {
		/*if (mDayOfWeekView != null) {
            mDayOfWeekView.setText(mCalendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG,
                    Locale.getDefault()).toUpperCase(Locale.getDefault()));
        }

        mSelectedMonthTextView.setText(mCalendar.getDisplayName(Calendar.MONTH, Calendar.SHORT,
                Locale.getDefault()).toUpperCase(Locale.getDefault()));*/

		if (this.mDayOfWeekView != null)
		{
			if(mMode == PERSIAN)
			{
				this.mPersianCalendar.setFirstDayOfWeek(mWeekStart);
				int tmpDayOfWeek = this.mPersianCalendar.get(Calendar.DAY_OF_WEEK);
//				if(this.mPersianCalendar.get(Calendar.DAY_OF_WEEK) > 1)
//					tmpDayOfWeek = this.mPersianCalendar.get(Calendar.DAY_OF_WEEK)-1;
//				else
//					tmpDayOfWeek = this.mPersianCalendar.get(Calendar.DAY_OF_WEEK)+7-1;
				this.mDayOfWeekView.setText(PersianCalendar.weekdayFullNames[tmpDayOfWeek]);
			}
			else if(mMode == GREGORIAN)
			{
				this.mGregorianCalendar.setFirstDayOfWeek(mWeekStart);
				this.mDayOfWeekView.setText(mDateFormatSymbols.getWeekdays()[this.mGregorianCalendar.get(Calendar.DAY_OF_WEEK)].toUpperCase(Locale.getDefault()));
			}
		}

		if(mMode == PERSIAN)
		{
			this.mSelectedMonthTextView.setText(PersianCalendar.months[this.mPersianCalendar.get(Calendar.MONTH)]);
			mSelectedDayTextView.setText(Utils.toPersianNumbers(mPersianCalendar.get(Calendar.DAY_OF_MONTH)+""));
			mYearView.setText(Utils.toPersianNumbers(mPersianCalendar.get(Calendar.YEAR)+""));
		}
		else if(mMode == GREGORIAN)
		{
			this.mSelectedMonthTextView.setText(mDateFormatSymbols.getMonths()[this.mGregorianCalendar.get(Calendar.MONTH)].toUpperCase(Locale.getDefault()));
			mSelectedDayTextView.setText(DAY_FORMAT.format(mGregorianCalendar.getTime()));
			mYearView.setText(YEAR_FORMAT.format(mGregorianCalendar.getTime()));
		}

		// Accessibility.
		long millis = 0L;
		if(mMode == PERSIAN)
		{
			millis = mPersianCalendar.getTimeInMillis();
		}
		else if(mMode == GREGORIAN)
		{
			millis = mGregorianCalendar.getTimeInMillis();
		}
		
		mAnimator.setDateMillis(millis);
		int flags = DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_NO_YEAR;
		String monthAndDayText = DateUtils.formatDateTime(getActivity(), millis, flags);
		mMonthAndDayView.setContentDescription(monthAndDayText);

		if (announce) {
			flags = DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR;
			String fullDateText = DateUtils.formatDateTime(getActivity(), millis, flags);
			Utils.tryAccessibilityAnnounce(mAnimator, fullDateText);
		}
	}

	private void updatePickers() {
		Iterator<OnDateChangedListener> iterator = mListeners.iterator();
		while (iterator.hasNext()) {
			iterator.next().onDateChanged();
		}
	}

	public int getFirstDayOfWeek() {
		return mWeekStart;
	}

	public int getMaxYear() {
		return mMaxYear;
	}

	public int getMinYear() {
		return mMinYear;
	}

	public SimpleMonthAdapter.CalendarDay getSelectedDay() {
		if(mMode == PERSIAN)
			return new SimpleMonthAdapter.CalendarDay(mPersianCalendar);
		else
			return new SimpleMonthAdapter.CalendarDay(mGregorianCalendar);
	}

	public void initialize(OnDateSetListener onDateSetListener, int year, int month, int day, boolean vibrate, int mMode) {
		this.setMode(mMode);
		if(mMode == PERSIAN)
		{
			mMinYear = MIN_YEAR_PERSIAN;
			mMaxYear = MAX_YEAR_PERSIAN;
			mWeekStart = mPersianCalendar.getFirstDayOfWeek();
			mDoneButtonText = "تایید";
			
			if (year > MAX_YEAR_PERSIAN)
				throw new IllegalArgumentException("year end must < " + MAX_YEAR_PERSIAN);
			if (year < MIN_YEAR_PERSIAN)
				throw new IllegalArgumentException("year end must > " + MIN_YEAR_PERSIAN);
			mPersianCalendar.set(Calendar.YEAR, year);
			mPersianCalendar.set(Calendar.MONTH, month);
			mPersianCalendar.set(Calendar.DAY_OF_MONTH, day);
		}
		else if(mMode == GREGORIAN)
		{
			
			mMinYear = MIN_YEAR_GREGORIAN;
			mMaxYear = MAX_YEAR_GREGORIAN;
			mWeekStart = mGregorianCalendar.getFirstDayOfWeek();
			mDoneButtonText = "Done";
			
			if (year > MAX_YEAR_GREGORIAN)
				throw new IllegalArgumentException("year end must < " + MAX_YEAR_GREGORIAN);
			if (year < MIN_YEAR_GREGORIAN)
				throw new IllegalArgumentException("year end must > " + MIN_YEAR_GREGORIAN);
			mGregorianCalendar.set(Calendar.YEAR, year);
			mGregorianCalendar.set(Calendar.MONTH, month);
			mGregorianCalendar.set(Calendar.DAY_OF_MONTH, day);
		}
		mCallBack = onDateSetListener;
		mVibrate = vibrate;
	}

	public void onClick(View view) {
		tryVibrate();
		if (view.getId() == R.id.date_picker_year)
			setCurrentView(YEAR_VIEW);
		else if (view.getId() == R.id.date_picker_month_and_day)
			setCurrentView(MONTH_AND_DAY_VIEW);
	}

	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		Activity activity = getActivity();
		activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		mVibrator = ((Vibrator) activity.getSystemService("vibrator"));
		if (bundle != null) {
			if(mMode == PERSIAN)
			{
				mPersianCalendar.set(Calendar.YEAR, bundle.getInt(KEY_SELECTED_YEAR));
				mPersianCalendar.set(Calendar.MONTH, bundle.getInt(KEY_SELECTED_MONTH));
				mPersianCalendar.set(Calendar.DAY_OF_MONTH, bundle.getInt(KEY_SELECTED_DAY));
			}
			else if(mMode == GREGORIAN)
			{
				mGregorianCalendar.set(Calendar.YEAR, bundle.getInt(KEY_SELECTED_YEAR));
				mGregorianCalendar.set(Calendar.MONTH, bundle.getInt(KEY_SELECTED_MONTH));
				mGregorianCalendar.set(Calendar.DAY_OF_MONTH, bundle.getInt(KEY_SELECTED_DAY));
			}
			mVibrate = bundle.getBoolean(KEY_VIBRATE);
		}
	}

	public View onCreateView(LayoutInflater layoutInflater, ViewGroup parent, Bundle bundle) {
		getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

		View view = layoutInflater.inflate(R.layout.date_picker_dialog, null);

		mDayOfWeekView = ((TextView) view.findViewById(R.id.date_picker_header));
		mMonthAndDayView = ((LinearLayout) view.findViewById(R.id.date_picker_month_and_day));
		mMonthAndDayView.setOnClickListener(this);
		mSelectedMonthTextView = ((TextView) view.findViewById(R.id.date_picker_month));
		mSelectedDayTextView = ((TextView) view.findViewById(R.id.date_picker_day));
		mYearView = ((TextView) view.findViewById(R.id.date_picker_year));
		mYearView.setOnClickListener(this);

		mDayOfWeekView.setTypeface(mTypeface);
		mSelectedDayTextView.setTypeface(mTypeface);
		mSelectedMonthTextView.setTypeface(mTypeface);
		mYearView.setTypeface(mTypeface);

		int listPosition = -1;
		int currentView = MONTH_AND_DAY_VIEW;
		int listPositionOffset = 0;
		if (bundle != null) {
			mWeekStart = bundle.getInt(KEY_WEEK_START);
			mMinYear = bundle.getInt(KEY_YEAR_START);
			mMaxYear = bundle.getInt(KEY_YEAR_END);
			currentView = bundle.getInt(KEY_CURRENT_VIEW);
			listPosition = bundle.getInt(KEY_LIST_POSITION);
			listPositionOffset = bundle.getInt(KEY_LIST_POSITION_OFFSET);
		}

		Activity activity = getActivity();
		mDayPickerView = new DayPickerView(activity, this, mMode, this.getTypeface());
		mDayPickerView.setTypeface(this.getTypeface());
		mDayPickerView.setMode(mMode);
		mDayPickerView.mAdapter.notifyDataSetChanged();
		mYearPickerView = new YearPickerView(activity, this, mMode);
		mYearPickerView.setTypeface(this.getTypeface());
		mYearPickerView.setMode(mMode);

		Resources resources = getResources();
		mDayPickerDescription = resources.getString(R.string.day_picker_description);
		mSelectDay = resources.getString(R.string.select_day);
		mYearPickerDescription = resources.getString(R.string.year_picker_description);
		mSelectYear = resources.getString(R.string.select_year);

		mAnimator = ((AccessibleDateAnimator) view.findViewById(R.id.animator));
		mAnimator.addView(mDayPickerView);
		mAnimator.addView(mYearPickerView);
		if(mMode == PERSIAN)
		{
			mAnimator.setDateMillis(mPersianCalendar.getTimeInMillis());
			
			//Set bottom margin for selected month TextView
			LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
			DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
			int density = 1;
			switch((int)(4*(getResources().getDisplayMetrics().density)))
			{
			case 3:
				density = DisplayMetrics.DENSITY_LOW;
				break;
			case 4:
				density = DisplayMetrics.DENSITY_MEDIUM;
				break;
			case 6:
				density = DisplayMetrics.DENSITY_HIGH;
				break;
			case 8:
				density = DisplayMetrics.DENSITY_XHIGH;
				break;
			case 12:
				density = DisplayMetrics.DENSITY_XXHIGH;
				break;
			}

		    int px = Math.round(20 * (displayMetrics.xdpi / density));
			p.setMargins(0, 0, 0, px);
			mSelectedMonthTextView.setLayoutParams(p);
		}
		else if(mMode == GREGORIAN)
			mAnimator.setDateMillis(mGregorianCalendar.getTimeInMillis());

		AlphaAnimation inAlphaAnimation = new AlphaAnimation(0.0F, 1.0F);
		inAlphaAnimation.setDuration(300L);
		mAnimator.setInAnimation(inAlphaAnimation);

		AlphaAnimation outAlphaAnimation = new AlphaAnimation(1.0F, 0.0F);
		outAlphaAnimation.setDuration(300L);
		mAnimator.setOutAnimation(outAlphaAnimation);

		mDoneButton = ((Button) view.findViewById(R.id.done));
		mDoneButton.setText(mDoneButtonText);
		mDoneButton.setTypeface(mTypeface);
		mDoneButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				onDoneButtonClick();
			}
		});

		updateDisplay(false);
		setCurrentView(currentView, true);

		if (listPosition != -1) {
			if (currentView == MONTH_AND_DAY_VIEW) {
				mDayPickerView.postSetSelection(listPosition);
			}
			if (currentView == YEAR_VIEW) {
				mYearPickerView.postSetSelectionFromTop(listPosition, listPositionOffset);
			}
		}
		return view;
	}

	private void onDoneButtonClick() {
		tryVibrate();
		if (mCallBack != null) {
			if(mMode == PERSIAN)
				mCallBack.onDateSet(this, mPersianCalendar.get(Calendar.YEAR), mPersianCalendar.get(Calendar.MONTH), mPersianCalendar.get(Calendar.DAY_OF_MONTH));
			else if(mMode == GREGORIAN)
				mCallBack.onDateSet(this, mGregorianCalendar.get(Calendar.YEAR), mGregorianCalendar.get(Calendar.MONTH), mGregorianCalendar.get(Calendar.DAY_OF_MONTH));
		}
		dismiss();
	}

	public void onDayOfMonthSelected(int year, int month, int day) {
		if(mMode == PERSIAN)
		{
			mPersianCalendar.set(Calendar.YEAR, year);
			mPersianCalendar.set(Calendar.MONTH, month);
			mPersianCalendar.set(Calendar.DAY_OF_MONTH, day);
		}
		else if(mMode == GREGORIAN)
		{
			mGregorianCalendar.set(Calendar.YEAR, year);
			mGregorianCalendar.set(Calendar.MONTH, month);
			mGregorianCalendar.set(Calendar.DAY_OF_MONTH, day);
		}
		updatePickers();
		updateDisplay(true);

		if(mCloseOnSingleTapDay) {
			onDoneButtonClick();
		}
	}

	public void onSaveInstanceState(Bundle bundle) {
		super.onSaveInstanceState(bundle);
		System.out.println("date save");
		if(mMode == PERSIAN)
		{
			bundle.putInt(KEY_SELECTED_YEAR, mPersianCalendar.get(Calendar.YEAR));
			bundle.putInt(KEY_SELECTED_MONTH, mPersianCalendar.get(Calendar.MONTH));
			bundle.putInt(KEY_SELECTED_DAY, mPersianCalendar.get(Calendar.DAY_OF_MONTH));
		}
		else if(mMode == GREGORIAN)
		{
			bundle.putInt(KEY_SELECTED_YEAR, mGregorianCalendar.get(Calendar.YEAR));
			bundle.putInt(KEY_SELECTED_MONTH, mGregorianCalendar.get(Calendar.MONTH));
			bundle.putInt(KEY_SELECTED_DAY, mGregorianCalendar.get(Calendar.DAY_OF_MONTH));
		}
		bundle.putInt(KEY_WEEK_START, mWeekStart);
		bundle.putInt(KEY_YEAR_START, mMinYear);
		bundle.putInt(KEY_YEAR_END, mMaxYear);
		bundle.putInt(KEY_CURRENT_VIEW, mCurrentView);

		int listPosition = -1;
		if (mCurrentView == 0) {
			listPosition = mDayPickerView.getMostVisiblePosition();
		} if (mCurrentView == 1) {
			listPosition = mYearPickerView.getFirstVisiblePosition();
			bundle.putInt(KEY_LIST_POSITION_OFFSET, mYearPickerView.getFirstPositionOffset());
		}
		bundle.putInt(KEY_LIST_POSITION, listPosition);
		bundle.putBoolean(KEY_VIBRATE, mVibrate);
	}

	public void onYearSelected(int year) {
		if(mMode == PERSIAN)
		{
			adjustDayInMonthIfNeeded(mPersianCalendar.get(Calendar.MONTH), year);
			mPersianCalendar.set(Calendar.YEAR, year);
		}
		else if(mMode == GREGORIAN)
		{
			adjustDayInMonthIfNeeded(mGregorianCalendar.get(Calendar.MONTH), year);
			mGregorianCalendar.set(Calendar.YEAR, year);
		}
		updatePickers();
		setCurrentView(MONTH_AND_DAY_VIEW);
		updateDisplay(true);
	}

	public void registerOnDateChangedListener(OnDateChangedListener onDateChangedListener) {
		mListeners.add(onDateChangedListener);
	}

	public void setFirstDayOfWeek(int startOfWeek) {
		if (startOfWeek < Calendar.SUNDAY || startOfWeek > Calendar.SATURDAY) {
			throw new IllegalArgumentException("Value must be between Calendar.SUNDAY and " +
					"Calendar.SATURDAY");
		}
		mWeekStart = startOfWeek;
		if (mDayPickerView != null) {
			mDayPickerView.onChange();
		}
	}

	public void setOnDateSetListener(OnDateSetListener onDateSetListener) {
		mCallBack = onDateSetListener;
	}

	public void setYearRange(int minYear, int maxYear) {
		if (maxYear <= minYear)
			throw new IllegalArgumentException("Year end must be larger than year start");
		if(mMode == PERSIAN)
		{
			if (maxYear > MAX_YEAR_PERSIAN)
				throw new IllegalArgumentException("max year end must < " + MAX_YEAR_PERSIAN);
			if (minYear < MIN_YEAR_PERSIAN)
				throw new IllegalArgumentException("min year end must > " + MIN_YEAR_PERSIAN);
		}
		else if(mMode == GREGORIAN)
		{
			if (maxYear > MAX_YEAR_GREGORIAN)
				throw new IllegalArgumentException("max year end must < " + MAX_YEAR_GREGORIAN);
			if (minYear < MIN_YEAR_GREGORIAN)
				throw new IllegalArgumentException("min year end must > " + MIN_YEAR_GREGORIAN);
		}
		
		mMinYear = minYear;
		mMaxYear = maxYear;
		if (mDayPickerView != null)
			mDayPickerView.onChange();
	}

	public void tryVibrate() {
		if (mVibrator != null && mVibrate) {
			long timeInMillis = SystemClock.uptimeMillis();
			if (timeInMillis - mLastVibrate >= 125L) {
				mVibrator.vibrate(5L);
				mLastVibrate = timeInMillis;
			}
		}
	}

	public void setCloseOnSingleTapDay(boolean closeOnSingleTapDay) {
		mCloseOnSingleTapDay = closeOnSingleTapDay;
	}

	public Typeface getTypeface()
	{
		if(mTypeface == null)
			return Typeface.create(Typeface.DEFAULT, Typeface.NORMAL);
		return mTypeface;
	}

	public void setTypeface(Typeface mTypeface)
	{
		this.mTypeface = mTypeface;
	}
	
	public void setDoneButtonText(String mDoneButtonText)
	{
		this.mDoneButtonText = mDoneButtonText;
	}
	
	/**
	 * @param mMode Should be either DatePickerDialog.PERSIAN or DatePickerDialog.GREGORIAN
	 */
	private void setMode(int mMode)
	{
		if(mMode != PERSIAN && mMode != GREGORIAN)
			throw new IllegalArgumentException("Invalid Mode");
		this.mMode = mMode;
	}
	
	static abstract interface OnDateChangedListener {
		public abstract void onDateChanged();
	}

	public static abstract interface OnDateSetListener {
		public abstract void onDateSet(PersianDatePickerDialog datePickerDialog, int year, int month, int day);
	}
	/**
	 * Replaces the full name of the weekdays.
	 * @param weekdayNumber Weekday number whose name should be replaced. Must be Calendar.SATURDAY, Calendar.SUNDAY and so on. 
	 * @param weekdayShortName the string that should be replaced for weekday weekdayNumber. 
	 */
	public static void setWeekdayShortName(int weekdayNumber, String weekdayShortName)
	{
		PersianCalendar.weekdayShortNames[weekdayNumber] = weekdayShortName;
	}
	
	
	/**
	 * Replaces the full name of the weekdays.
	 * @param weekdayNumber Weekday number whose name should be replaced. Must be Calendar.SATURDAY, Calendar.SUNDAY and so on. 
	 * @param weekdayFullName the string that should be replaced for weekday weekdayNumber. 
	 */
	public static void setWeekdayFullName(int weekdayNumber, String weekdayFullName)
	{
		PersianCalendar.weekdayFullNames[weekdayNumber] = weekdayFullName;
	}
	
	/**
	 * Replaces the name of the months.
	 * @param monthNumber Month number whose name should be replaced. Must be PersianCalendar.FARVARDIN, PersianCalendar.ORDIBEHESHT and so on. 
	 * @param monthName the string that should be replaced for month monthNumber. 
	 */
	public static void setMonthName(int monthNumber, String monthName)
	{
		PersianCalendar.months[monthNumber] = monthName;
	}

	public void setInitialDate(int year, int month, int day)
	{
		if(mMode == PERSIAN)
		{
			if (year > MAX_YEAR_PERSIAN)
				throw new IllegalArgumentException("year end must < " + MAX_YEAR_PERSIAN);
			if (year < MIN_YEAR_PERSIAN)
				throw new IllegalArgumentException("year end must > " + MIN_YEAR_PERSIAN);
			mPersianCalendar.set(Calendar.YEAR, year);
			mPersianCalendar.set(Calendar.MONTH, month);
			mPersianCalendar.set(Calendar.DAY_OF_MONTH, day);
		}
		else if(mMode == GREGORIAN)
		{
			if (year > MAX_YEAR_GREGORIAN)
				throw new IllegalArgumentException("year end must < " + MAX_YEAR_GREGORIAN);
			if (year < MIN_YEAR_GREGORIAN)
				throw new IllegalArgumentException("year end must > " + MIN_YEAR_GREGORIAN);
			mGregorianCalendar.set(Calendar.YEAR, year);
			mGregorianCalendar.set(Calendar.MONTH, month);
			mGregorianCalendar.set(Calendar.DAY_OF_MONTH, day);
		}		
	}
}