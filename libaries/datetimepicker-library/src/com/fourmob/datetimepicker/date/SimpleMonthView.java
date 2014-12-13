package com.fourmob.datetimepicker.date;

import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.text.format.DateUtils;
import android.text.format.Time;
import android.view.MotionEvent;
import android.view.View;
import com.fourmob.datetimepicker.R;
import com.fourmob.datetimepicker.Utils;

import java.security.InvalidParameterException;
import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

public class SimpleMonthView extends View {

	public static final String VIEW_PARAMS_HEIGHT = "height";
	public static final String VIEW_PARAMS_MONTH = "month";
	public static final String VIEW_PARAMS_YEAR = "year";
	public static final String VIEW_PARAMS_SELECTED_DAY = "selected_day";
	public static final String VIEW_PARAMS_WEEK_START = "week_start";
	public static final String VIEW_PARAMS_NUM_DAYS = "num_days";
	public static final String VIEW_PARAMS_FOCUS_MONTH = "focus_month";
	public static final String VIEW_PARAMS_SHOW_WK_NUM = "show_wk_num";

	private static final int SELECTED_CIRCLE_ALPHA = 60;
	protected static int DEFAULT_HEIGHT = 32;
	protected static final int DEFAULT_NUM_ROWS = 6;
	private static final int NUMBER_OF_ROWS = 6;
	protected static int DAY_SELECTED_CIRCLE_SIZE;
	protected static int DAY_SEPARATOR_WIDTH = 1;
	protected static int MINI_DAY_NUMBER_TEXT_SIZE;
	protected static int MIN_HEIGHT = 15;
	protected static int MONTH_DAY_LABEL_TEXT_SIZE;
	protected static int MONTH_HEADER_SIZE;
	protected static int MONTH_LABEL_TEXT_SIZE;

	protected static float mScale = 0.0F;
	protected int mPadding = 0;

	protected Paint mMonthDayLabelPaint;
	protected Paint mMonthNumPaint;
	protected Paint mMonthTitleBGPaint;
	protected Paint mMonthTitlePaint;
	protected Paint mSelectedCirclePaint;
	protected int mDayTextColor;
	protected int mMonthTitleBGColor;
	protected int mMonthTitleColor;
	protected int mTodayNumberColor;

	private final StringBuilder mStringBuilder;

	protected Typeface mTypeface;
	protected int mMode = PersianDatePickerDialog.PERSIAN;

	protected int mFirstJulianDay = -1;
	protected int mFirstMonth = -1;
	protected int mLastMonth = -1;
	protected boolean mHasToday = false;
	protected int mSelectedDay = -1;
	protected int mToday = -1;
	protected int mWeekStart = 1;
	protected int mNumDays = 7;
	protected int mNumCells = mNumDays;
	protected int mSelectedLeft = -1;
	protected int mSelectedRight = -1;
	private int mDayOfWeekStart = 0;
	protected int mMonth;
	protected int mRowHeight = DEFAULT_HEIGHT;
	protected int mWidth;
	protected int mYear;

	private final PersianCalendar mPersianCalendar = new PersianCalendar();
	private final PersianCalendar mDayLabelPersianCalendar = new PersianCalendar();

	private final Calendar mGregorianCalendar = Calendar.getInstance();
	private final Calendar mDayLabelGregorianCalendar = Calendar.getInstance();

	private DateFormatSymbols mDateFormatSymbols = new DateFormatSymbols();

	private int mNumRows = DEFAULT_NUM_ROWS;


	private OnDayClickListener mOnDayClickListener;

	public SimpleMonthView(Context context) {
		super(context);
		Resources resources = context.getResources();
		mDayTextColor = resources.getColor(R.color.date_picker_text_normal);
		mTodayNumberColor = resources.getColor(R.color.blue);
		mMonthTitleColor = resources.getColor(R.color.white);
		mMonthTitleBGColor = resources.getColor(R.color.circle_background);

		mStringBuilder = new StringBuilder(50);

		MINI_DAY_NUMBER_TEXT_SIZE = resources.getDimensionPixelSize(R.dimen.day_number_size);
		MONTH_LABEL_TEXT_SIZE = resources.getDimensionPixelSize(R.dimen.month_label_size);
		MONTH_DAY_LABEL_TEXT_SIZE = resources.getDimensionPixelSize(R.dimen.month_day_label_text_size);
		MONTH_HEADER_SIZE = resources.getDimensionPixelOffset(R.dimen.month_list_item_header_height);
		DAY_SELECTED_CIRCLE_SIZE = resources.getDimensionPixelSize(R.dimen.day_number_select_circle_radius);

		mRowHeight = ((resources.getDimensionPixelOffset(R.dimen.date_picker_view_animator_height) - MONTH_HEADER_SIZE) / NUMBER_OF_ROWS);

		initView();
	}

	private int calculateNumRows() {
		int offset = findDayOffset();
		int dividend = (offset + mNumCells) / mNumDays;
		int remainder = (offset + mNumCells) % mNumDays;
		return (dividend + (remainder > 0 ? 1 : 0));
	}

	private void drawMonthTitle(Canvas canvas) {
		int x = (mWidth + 2 * mPadding) / 2;
		int y = (MONTH_HEADER_SIZE - MONTH_DAY_LABEL_TEXT_SIZE) / 2 + (MONTH_LABEL_TEXT_SIZE / 3);

		mMonthTitlePaint.setTypeface(mTypeface);
		canvas.drawText(getMonthAndYearString(), x, y, mMonthTitlePaint);
	}

	private int findDayOffset() {
		return (mDayOfWeekStart < mWeekStart ? (mDayOfWeekStart + mNumDays) : mDayOfWeekStart)
				- mWeekStart;
	}

	private String getMonthAndYearString() {
		if(mMode == PersianDatePickerDialog.PERSIAN)
			return mPersianCalendar.getMonthYearString();
		else
		{
			int flags = DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR | DateUtils.FORMAT_NO_MONTH_DAY;
			mStringBuilder.setLength(0);
			long millis = mGregorianCalendar.getTimeInMillis();
			return DateUtils.formatDateRange(getContext(), millis, millis, flags);
		}
	}

	private void onDayClick(SimpleMonthAdapter.CalendarDay calendarDay) {
		if (mOnDayClickListener != null) {
			mOnDayClickListener.onDayClick(this, calendarDay);
		}
	}

	private boolean sameDay(int monthDay, Time time) {
		return (mYear == time.year) && (mMonth == time.month) && (monthDay == time.monthDay);
	}

	private boolean sameDay(int monthDay, PersianCalendar cal) {
		return (mYear == cal.get(PersianCalendar.YEAR)) && (mMonth == cal.get(PersianCalendar.MONTH)) && (monthDay == cal.get(PersianCalendar.DAY_OF_MONTH));
	}

	private void drawMonthDayLabels(Canvas canvas) {
		int y = MONTH_HEADER_SIZE - (MONTH_DAY_LABEL_TEXT_SIZE / 2);
		int dayWidthHalf = (mWidth - mPadding * 2) / (mNumDays * 2);

		PersianCalendar tmpCal = new PersianCalendar();
		tmpCal.set(PersianCalendar.DAY_OF_WEEK, PersianCalendar.FRIDAY);
		int rightX = mWidth;
		for (int i = 0; i < mNumDays; i++) {
			int calendarDay = (i + mWeekStart) % mNumDays;
			int x = (2 * i + 1) * dayWidthHalf + mPadding;
			if(mMode == PersianDatePickerDialog.PERSIAN)
			{
				x = rightX - x;
				mDayLabelPersianCalendar.set(PersianCalendar.DAY_OF_WEEK, calendarDay);
				canvas.drawText(PersianCalendar.weekdayShortNames[mDayLabelPersianCalendar.get(PersianCalendar.DAY_OF_WEEK)], x, y, mMonthDayLabelPaint);
			}
			else if(mMode == PersianDatePickerDialog.GREGORIAN)
			{
				mDayLabelGregorianCalendar.set(Calendar.DAY_OF_WEEK, calendarDay);
				canvas.drawText(mDateFormatSymbols.getShortWeekdays()[mDayLabelGregorianCalendar.get(PersianCalendar.DAY_OF_WEEK)].toUpperCase(Locale.getDefault()), x, y, mMonthDayLabelPaint);
			}
		}
	}

	protected void drawMonthNums(Canvas canvas) {
		int y = (mRowHeight + MINI_DAY_NUMBER_TEXT_SIZE) / 2 - DAY_SEPARATOR_WIDTH + MONTH_HEADER_SIZE;
		int paddingDay = (mWidth - 2 * mPadding) / (2 * mNumDays);
		int dayOffset = findDayOffset();
		int day = 1;

		int rightX = mWidth;
		while (day <= mNumCells) {
			int x = paddingDay * (1 + dayOffset * 2) + mPadding;
			if(mMode == PersianDatePickerDialog.PERSIAN)
				x = rightX - x;
			if (mSelectedDay == day) {
				canvas.drawCircle(x, y - MINI_DAY_NUMBER_TEXT_SIZE / 2.7f, DAY_SELECTED_CIRCLE_SIZE, mSelectedCirclePaint);
			}
			if (mHasToday && (mToday == day)) {
				mMonthNumPaint.setColor(mTodayNumberColor);
			} else {
				mMonthNumPaint.setColor(mDayTextColor);
			}

			if(mMode == PersianDatePickerDialog.PERSIAN)
				canvas.drawText(Utils.toPersianNumbers(String.format("%d", day)), x, y, mMonthNumPaint);
			else if(mMode == PersianDatePickerDialog.GREGORIAN)
				canvas.drawText(String.format("%d", day), x, y, mMonthNumPaint);

			dayOffset++;
			if (dayOffset == mNumDays) {
				dayOffset = 0;
				y += mRowHeight;
			}
			day++;
		}
	}

	public SimpleMonthAdapter.CalendarDay getDayFromLocation(float x, float y) {
		int padding = mPadding;
		if ((x < padding) || (x > mWidth - mPadding)) {
			return null;
		}

		if(y < MONTH_HEADER_SIZE)
			return null;
		int rightX = mWidth;
		if(mMode == PersianDatePickerDialog.PERSIAN)
			x = rightX - x;
		int yDay = (int) (y - MONTH_HEADER_SIZE) / mRowHeight;
		int day = 1 + ((int) ((x - padding) * mNumDays / (mWidth - padding - mPadding)) - findDayOffset()) + yDay * mNumDays;

		return new SimpleMonthAdapter.CalendarDay(mYear, mMonth, day);
	}

	protected void initView() {
		mMonthTitlePaint = new Paint();
		mMonthTitlePaint.setFakeBoldText(true);
		mMonthTitlePaint.setAntiAlias(true);
		mMonthTitlePaint.setTextSize(MONTH_LABEL_TEXT_SIZE);
		mMonthTitlePaint.setColor(mDayTextColor);
		mMonthTitlePaint.setTextAlign(Align.CENTER);
		mMonthTitlePaint.setStyle(Style.FILL);

		mMonthTitleBGPaint = new Paint();
		mMonthTitleBGPaint.setFakeBoldText(true);
		mMonthTitleBGPaint.setAntiAlias(true);
		mMonthTitleBGPaint.setColor(mMonthTitleBGColor);
		mMonthTitleBGPaint.setTextAlign(Align.CENTER);
		mMonthTitleBGPaint.setStyle(Style.FILL);

		mSelectedCirclePaint = new Paint();
		mSelectedCirclePaint.setFakeBoldText(true);
		mSelectedCirclePaint.setAntiAlias(true);
		mSelectedCirclePaint.setColor(mTodayNumberColor);
		mSelectedCirclePaint.setTextAlign(Align.CENTER);
		mSelectedCirclePaint.setStyle(Style.FILL);
		mSelectedCirclePaint.setAlpha(SELECTED_CIRCLE_ALPHA);

		mMonthDayLabelPaint = new Paint();
		mMonthDayLabelPaint.setAntiAlias(true);
		mMonthDayLabelPaint.setTextSize(MONTH_DAY_LABEL_TEXT_SIZE);
		mMonthDayLabelPaint.setColor(mDayTextColor);
		mMonthDayLabelPaint.setStyle(Style.FILL);
		mMonthDayLabelPaint.setTextAlign(Align.CENTER);
		mMonthDayLabelPaint.setFakeBoldText(true);

		mMonthNumPaint = new Paint();
		mMonthNumPaint.setAntiAlias(true);
		mMonthNumPaint.setTextSize(MINI_DAY_NUMBER_TEXT_SIZE);
		mMonthNumPaint.setStyle(Style.FILL);
		mMonthNumPaint.setTextAlign(Align.CENTER);
		mMonthNumPaint.setFakeBoldText(false);
	}

	protected void onDraw(Canvas canvas) {
		drawMonthTitle(canvas);
		drawMonthDayLabels(canvas);
		drawMonthNums(canvas);
	}

	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		setMeasuredDimension(View.MeasureSpec.getSize(widthMeasureSpec), mRowHeight * mNumRows + MONTH_HEADER_SIZE);
	}

	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		mWidth = w;
	}

	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_UP) {
			SimpleMonthAdapter.CalendarDay calendarDay = getDayFromLocation(event.getX(), event.getY());
			if (calendarDay != null) {
				onDayClick(calendarDay);
			}
		}
		return true;
	}

	public void reuse() {
		mNumRows = DEFAULT_NUM_ROWS;
		requestLayout();
	}

	public void setMonthParams(HashMap<String, Integer> params) {
		if (!params.containsKey(VIEW_PARAMS_MONTH) && !params.containsKey(VIEW_PARAMS_YEAR)) {
			throw new InvalidParameterException("You must specify month and year for this view");
		}
		setTag(params);

		if (params.containsKey(VIEW_PARAMS_HEIGHT)) {
			mRowHeight = params.get(VIEW_PARAMS_HEIGHT);
			if (mRowHeight < MIN_HEIGHT) {
				mRowHeight = MIN_HEIGHT;
			}
		}
		if (params.containsKey(VIEW_PARAMS_SELECTED_DAY)) {
			mSelectedDay = params.get(VIEW_PARAMS_SELECTED_DAY);
		}

		mMonth = params.get(VIEW_PARAMS_MONTH);
		mYear = params.get(VIEW_PARAMS_YEAR);

		final Time today = new Time(Time.getCurrentTimezone());
		today.setToNow();
		PersianCalendar todayCal = new PersianCalendar();
		mHasToday = false;
		mToday = -1;

		if(mMode == PersianDatePickerDialog.PERSIAN)
		{
			mPersianCalendar.set(PersianCalendar.MONTH, mMonth);
			mPersianCalendar.set(PersianCalendar.YEAR, mYear);
			mPersianCalendar.set(PersianCalendar.DAY_OF_MONTH, 1);
			mDayOfWeekStart = mPersianCalendar.get(PersianCalendar.DAY_OF_WEEK);
		}
		else if(mMode == PersianDatePickerDialog.GREGORIAN)
		{
			mGregorianCalendar.set(Calendar.MONTH, mMonth);
			mGregorianCalendar.set(Calendar.YEAR, mYear);
			mGregorianCalendar.set(Calendar.DAY_OF_MONTH, 1);
			mDayOfWeekStart = mGregorianCalendar.get(Calendar.DAY_OF_WEEK);
		}

		if (params.containsKey(VIEW_PARAMS_WEEK_START)) {
			mWeekStart = params.get(VIEW_PARAMS_WEEK_START);
		} else {
			if(mMode == PersianDatePickerDialog.PERSIAN)
				mWeekStart = mPersianCalendar.getFirstDayOfWeek();
			else if(mMode == PersianDatePickerDialog.GREGORIAN)
				mWeekStart = mGregorianCalendar.getFirstDayOfWeek();
		}

		if(mMode == PersianDatePickerDialog.PERSIAN)
			mNumCells = Utils.getDaysInMonthPersian(mMonth, mYear);
		else if(mMode == PersianDatePickerDialog.GREGORIAN)
			mNumCells = Utils.getDaysInMonthGregorian(mMonth, mYear);

		for (int i = 0; i < mNumCells; i++)
		{
			final int day = i + 1;
			if(mMode == PersianDatePickerDialog.PERSIAN)
			{
				if (sameDay(day, todayCal))
				{
					mHasToday = true;
					mToday = day;
				}
			}
			else if(mMode == PersianDatePickerDialog.GREGORIAN)
			{
				if (sameDay(day, today))
				{
					mHasToday = true;
					mToday = day;
				}
			}
		}

		mNumRows = calculateNumRows();
	}

	public void setOnDayClickListener(OnDayClickListener onDayClickListener) {
		mOnDayClickListener = onDayClickListener;
	}

	public static abstract interface OnDayClickListener {
		public abstract void onDayClick(SimpleMonthView simpleMonthView, SimpleMonthAdapter.CalendarDay calendarDay);
	}

	public void setTypeface(Typeface mTypeface)
	{
		this.mTypeface = mTypeface;
		mMonthNumPaint.setTypeface(this.mTypeface);
		mMonthDayLabelPaint.setTypeface(this.mTypeface);
		mMonthTitlePaint.setTypeface(this.mTypeface);
	}

	public void setMode(int mMode)
	{
		this.mMode = mMode;
	}
}