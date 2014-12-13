package com.fourmob.datetimepicker;

import java.util.Calendar;

import android.annotation.SuppressLint;
import android.os.Build;
import android.view.View;
import android.view.accessibility.AccessibilityManager;

import com.fourmob.datetimepicker.date.PersianCalendar;
import com.nineoldandroids.animation.Keyframe;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.animation.PropertyValuesHolder;

public class Utils {

    public static final int PULSE_ANIMATOR_DURATION = 544;

    public static String toPersianNumbers(String str)
    {
    	char[] persianNumbers = {'\u06F0', '\u06F1', '\u06F2', '\u06F3', '\u06F4', '\u06F5', '\u06F6', '\u06F7', '\u06F8', '\u06F9'};
    	StringBuilder builder = new StringBuilder();
    	for(int i = 0; i<str.length(); i++)
    	{
    	    if(Character.isDigit(str.charAt(i)))
    	    {
    	        builder.append(persianNumbers[(int)(str.charAt(i))-48]);
    	    }
    	    else
    	    {
    	        builder.append(str.charAt(i));
    	    }
    	}
    	
    	return builder.toString();
    }
    
    public static int getDaysInMonthPersian(int month, int year)
    {
    	if(month < 6 && month > -1)
    		return 31;
    	if(month < 11)
    		return 30;
    	if(month == 11)
    	{
    		return PersianCalendar.isLeapYear(year)? 30: 29;
    	}
    	throw new IllegalArgumentException("Invalid Month");
    }
    
    public static int getDaysInMonthGregorian(int month, int year)
    {
    	switch (month) {
    	case Calendar.JANUARY:
    	case Calendar.MARCH:
    	case Calendar.MAY:
    	case Calendar.JULY:
    	case Calendar.AUGUST:
    	case Calendar.OCTOBER:
    	case Calendar.DECEMBER:
    		return 31;
    	case Calendar.APRIL:
    	case Calendar.JUNE:
    	case Calendar.SEPTEMBER:
    	case Calendar.NOVEMBER:
    		return 30;
    	case Calendar.FEBRUARY:
    		return (year % 4 == 0) ? 29 : 28;
    	default:
    		throw new IllegalArgumentException("Invalid Month");
    	}
    }

	public static ObjectAnimator getPulseAnimator(View labelToAnimate, float decreaseRatio, float increaseRatio) {
        Keyframe k0 = Keyframe.ofFloat(0f, 1f);
        Keyframe k1 = Keyframe.ofFloat(0.275f, decreaseRatio);
        Keyframe k2 = Keyframe.ofFloat(0.69f, increaseRatio);
        Keyframe k3 = Keyframe.ofFloat(1f, 1f);

        PropertyValuesHolder scaleX = PropertyValuesHolder.ofKeyframe("scaleX", k0, k1, k2, k3);
        PropertyValuesHolder scaleY = PropertyValuesHolder.ofKeyframe("scaleY", k0, k1, k2, k3);
        ObjectAnimator pulseAnimator = ObjectAnimator.ofPropertyValuesHolder(labelToAnimate, scaleX, scaleY);
        pulseAnimator.setDuration(PULSE_ANIMATOR_DURATION);

        return pulseAnimator;
    }

	public static boolean isJellybeanOrLater() {
		return Build.VERSION.SDK_INT >= 16;
	}

    /**
     * Try to speak the specified text, for accessibility. Only available on JB or later.
     * @param text Text to announce.
     */
    @SuppressLint("NewApi")
    public static void tryAccessibilityAnnounce(View view, CharSequence text) {
        if (isJellybeanOrLater() && view != null && text != null) {
            view.announceForAccessibility(text);
        }
    }

    @SuppressLint("NewApi")
	public static boolean isTouchExplorationEnabled(AccessibilityManager accessibilityManager) {
        if (Build.VERSION.SDK_INT >= 14) {
            return accessibilityManager.isTouchExplorationEnabled();
        } else {
            return false;
        }
    }
}
