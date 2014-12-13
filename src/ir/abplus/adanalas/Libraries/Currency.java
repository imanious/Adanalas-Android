package ir.abplus.adanalas.Libraries;

import android.content.Context;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.math.BigInteger;

public abstract class Currency
{
	public static final int SMALL_TEXT_SIZE = 12; //sp
	public static final int LARGE_TEXT_SIZE = 18; //sp
	private static final String separator = "\u066c";
	public static final String decimalPoint = "\u066b";
	private static final int decimalDigits = 2;
	private static final int decimal = (int)Math.pow(10, decimalDigits);
	public static final int RIAL = 0;
	public static final String RIAL_STRING = "ریال";
	public static final int TOMAN = 1;
	public static final String TOMAN_STRING = "تومان";
	public static final int THOUSAND_TOMAN = 2;
	public static final String THOUSAND_TOMAN_STRING = "هزار تومان";
	private static final String LONG_THOUSAND = "هـــزار";
	private static final String LONG_TOMAN = "تومان";
	private static int currency = RIAL;
	
	public static void setCurrency(int currency)
	{
		if(currency != RIAL && currency != TOMAN && currency != THOUSAND_TOMAN)
			throw new IllegalArgumentException("Invalid currency.");
		Currency.currency = currency;
	}
	
	public static int getCurrency()
	{
		return currency;
	}
	
	public static void setCurrencyLayout(LinearLayout currencyLayout, Context context, int color, Typeface typeface, float textSize)
	{
		currencyLayout.removeAllViews();
		LinearLayout linearLayout = currencyLayout;
		TextView firstLine = new TextView(context);
		firstLine.setGravity(Gravity.CENTER_VERTICAL);
		firstLine.setTextColor(color);
		firstLine.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
		firstLine.setTypeface(typeface);
		firstLine.setLayoutParams(new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
		
		firstLine.setText(getCurrencyString());
        linearLayout.addView(firstLine);
		if(getCurrency() != THOUSAND_TOMAN)
			return;
		firstLine.setGravity(Gravity.CENTER);
		firstLine.setText(LONG_THOUSAND);
		
		TextView secondLine = new TextView(context);
		secondLine.setTextColor(color);
		secondLine.setGravity(Gravity.CENTER);
		secondLine.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
		secondLine.setTypeface(typeface);
		secondLine.setLayoutParams(new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
		secondLine.setText(LONG_TOMAN);

		firstLine.setIncludeFontPadding(false);
		secondLine.setIncludeFontPadding(false);
		
		//TODO convert to dp
		firstLine.setPadding(0, 0, 0, -5);
		secondLine.setPadding(0, -5, 0, 0);
		linearLayout.addView(secondLine);
	}
	
	public static String getCurrencyString()
	{
		switch(currency)
		{
		case RIAL:
			return RIAL_STRING;
		case TOMAN:
			return TOMAN_STRING;
		case THOUSAND_TOMAN:
			return THOUSAND_TOMAN_STRING;
		}
		return RIAL_STRING;
	}
	

    //developed by armin
//	public static String getStandardAmount(double amount)
//	{
//		if(amount == 0)
//			return "0";
//		String res = "";
//
//		double dec = amount - (long)amount;
//		if((int)(dec*decimal) != 0)
//            if((int)(dec*decimal)>10)
//			res = decimalPoint + (int)(dec*decimal);
//        else
//                res = decimalPoint +"0"+ (int)(dec*decimal);
//		long c = (long) amount;
//
//		if(c == 0)
//			return "0"+res;
//
//		while(c != 0)
//		{
//			String tmp = "" + (c%1000);
//			if(c/1000 != 0)
//			{
//				if(c%1000 < 10)
//					tmp = "00" + (c%1000);
//				else if(c%1000 < 100)
//					tmp = "0" + (c%1000);
//				else
//					tmp = "" + (c%1000);
//			}
//			if(c/1000 != 0)
//				res = separator + tmp + res;
//			else
//				res = tmp + res;
//			c /= 1000;
//		}
//
//		return res;
//	}


    public static String getStdAmountWithoutSeparation(double amount)
    {
        if(getCurrency()==RIAL){
            return amount+"";
        }
        else if(getCurrency()==TOMAN)
            return rialToToman(amount);
        else {
            return rialToThousand(amount);
        }
    }

    public static String getStdAmount(double amount){
        if(getCurrency()==RIAL){
            return separateThousand(amount+"");
        }
        else if(getCurrency()==TOMAN)
            return separateThousand(rialToToman(amount));
        else {
            return separateThousand(rialToThousand(amount));
        }
    }
	
    public static String separateThousand(String input_seperate){

        String tmp1=input_seperate;
        String tmp2="";
        if(input_seperate.contains("."))
        {
            tmp1=input_seperate.substring(0,input_seperate.indexOf("."));
            if(input_seperate.endsWith(".0")||input_seperate.endsWith(".00"))
            {}
            else
            tmp2=decimalPoint+input_seperate.substring(input_seperate.indexOf(".")+1,input_seperate.length());
        }
        BigInteger tmp=new BigInteger(tmp1);
        String output;
        output=String.format("%,d",tmp);
        output = output.replaceAll(",", separator);
        if(tmp2.equals(decimalPoint+"0"))
            return output;
        else
            return output+tmp2;
    }

    private static Double thousandToRial(double input){
//        String output="";
//        if(input.contains("."))
//        {
//            output=input.substring(0,input.indexOf("."))+input.substring(input.indexOf(".")+1,input.length());
//            if(input.indexOf(".")<input.length()-2)
//                output+="00";
//            else
//                output+="000";
//        }
//        else
//            output=input+"0000";
//        return output;
        return input*10000;
    }

    public static String rialToThousand(double input){
        String output=input/10000+"";
        return output;
    }
    public static String rialToToman(double input){
        String output=input/10+"";
        return output;
    }

    public static double allToRial(double input){
        if (getCurrency()==RIAL)
            return input;
        else if(getCurrency()==TOMAN)
            return input*10;
        else if(getCurrency()==THOUSAND_TOMAN)
            return thousandToRial(input);

        else
            return -1;
    }

}