package ir.abplus.adanalas.Libraries;

import android.util.Log;
import ir.abplus.adanalas.R;

public class Category
{
	public static final int RAFTO_AMAD = 0;
	public static final int DARMAN = 1;
	public static final int KHORAK = 2;
	public static final int GHABZ = 3;
	public static final int SAKHTEMAN = 4;
	public static final int AGHSAAT = 5;
	public static final int AMOOZESH = 6;
	public static final int ARAYESH = 7;
	public static final int GHARZ = 8;
	public static final int HADIYE_KHEYRIE = 9;
	public static final int VARZESH = 10;
	public static final int POOSHAK = 11;
	public static final int SARMAYE = 12;
	public static final int SHAKHSHI = 13;
	public static final int EXPENSE_UNCATEGORIZED = 14;
	public static final int EXPENSE_SIZE = 15;
	public static final String[] expenseCategories = new String[EXPENSE_UNCATEGORIZED+1];// = {"رفت و آمد", "بهداشت و درمان", "خوراک", "قبض", "هزینه خانه", "دسته بندی نشده"};
	
	public static final int PADASH = 0;
	public static final int DASTMOZD = 1;
	public static final int YARANEH = 2;
	public static final int HADIYE = 3;
	public static final int EJAREH = 4;
	public static final int SOOD = 5;
	public static final int KHESARAT = 6;
	public static final int FOROOSH_DARAYI = 7;
	public static final int VADIE_DARYAFTI = 8;
	public static final int TALAB = 9;
	public static final int VAAM = 10;
	public static final int INCOME_UNCATEGORIZED = 11;
	public static final int INCOME_SIZE = 12;
	public static final String[] incomeCategories = new String[INCOME_UNCATEGORIZED+1];
	
	public static void initialize()
	{
		expenseCategories[SAKHTEMAN] = "ساختمان و لوازم خانه";
		expenseCategories[KHORAK] = "خوراک و روزمره";
		expenseCategories[RAFTO_AMAD] = "رفت و آمد";
		expenseCategories[AMOOZESH] = "آموزش";
		expenseCategories[GHABZ] = "قبض";
		expenseCategories[VARZESH] = "ورزش و سرگرمی";
		expenseCategories[DARMAN] = "دارو و درمان";
		expenseCategories[ARAYESH] = "آرایش و بهداشت";
		expenseCategories[POOSHAK] = "پوشاک";
		expenseCategories[SHAKHSHI] = "شخصی";
		expenseCategories[HADIYE_KHEYRIE] = "هدیه و خیریه";
		expenseCategories[GHARZ] = "قرض به دیگران";
		expenseCategories[AGHSAAT] = "اقساط و تعهدات";
		expenseCategories[SARMAYE] = "سرمایه گذاری";
		expenseCategories[EXPENSE_UNCATEGORIZED] = "دسته‌بندی‌نشده";
		
		incomeCategories[PADASH] = "پاداش و کارانه";
		incomeCategories[DASTMOZD] = "دستمزد";
		incomeCategories[HADIYE] = "جایزه و هدیه";
		incomeCategories[EJAREH] = "اجاره";
		incomeCategories[SOOD] = "سود و بهره";
		incomeCategories[KHESARAT] = "دریافت خسارت";
		incomeCategories[FOROOSH_DARAYI] = "فروش دارایی";
		incomeCategories[VADIE_DARYAFTI] = "ودیعه دریافتی";
		incomeCategories[TALAB] = "دریافت طلب";
		incomeCategories[VAAM] = "دریافت وام";
		incomeCategories[YARANEH] = "یارانه";
		incomeCategories[INCOME_UNCATEGORIZED] = "دسته‌بندی‌نشده";
	}

    public static int getExpenseCategoryID(String expenseString){
        if(expenseString.equals("household"))
            return SAKHTEMAN;
        else if(expenseString.equals("food"))
            return KHORAK;
        else if(expenseString.equals("transportation"))
            return RAFTO_AMAD;
        else if(expenseString.equals("education"))
            return AMOOZESH;
        else if(expenseString.equals("bill"))
            return GHABZ;
        else if(expenseString.equals("hobby"))
            return VARZESH;
        else if(expenseString.equals("healthcare"))
            return  DARMAN;
        else if(expenseString.equals("hygiene"))
            return ARAYESH;
        else if(expenseString.equals("clothing"))
            return POOSHAK;
        else if(expenseString.equals("personal"))
            return SHAKHSHI;
        else if(expenseString.equals("present"))
            return HADIYE_KHEYRIE;
        else if(expenseString.equals("lend"))
            return GHARZ;
        else if(expenseString.equals("commitment"))
            return AGHSAAT;
        else if(expenseString.equals("investment"))
            return SARMAYE;
        else if(expenseString.equals("expense"))
            return EXPENSE_UNCATEGORIZED;
        else{
            Log.e("Bug","Category.getExpenseID:Category String is not valid");
            return -1;}
    }
    public static int getIncomeCategoryID(String incomeString){
        if(incomeString.equals("bonus"))
            return PADASH;
        else if(incomeString.equals("salary"))
            return DASTMOZD;
        else if(incomeString.equals("subsidy"))
            return YARANEH;
        else if(incomeString.equals("gift"))
            return HADIYE;
        else if(incomeString.equals("rent"))
            return EJAREH;
        else if(incomeString.equals("interest"))
            return SOOD;
        else if(incomeString.equals("compensation"))
            return  KHESARAT;
        else if(incomeString.equals("sale"))
            return FOROOSH_DARAYI;
        else if(incomeString.equals("trust"))
            return VADIE_DARYAFTI;
        else if(incomeString.equals("borrow"))
            return TALAB;
        else if(incomeString.equals("loan"))
            return VAAM;
        else if(incomeString.equals("income"))
            return INCOME_UNCATEGORIZED;
        else{
            Log.e("Bug","Category.getIncomeID:Category String is not valid");
            return -1;}
    }


	public static int getExpenseIconID(int id)
	{
		switch (id) {
		case RAFTO_AMAD:
			return R.drawable.rafto_amad;
		case SAKHTEMAN:
			return R.drawable.sakhteman;
		case KHORAK:
			return R.drawable.khorak;
		case AMOOZESH:
			return R.drawable.amoozesh;
		case GHABZ:
			return R.drawable.ghabz;
		case VARZESH:
			return R.drawable.varzesh;
		case DARMAN:
			return R.drawable.darman;
		case ARAYESH:
			return R.drawable.arayesh;
		case POOSHAK:
			return R.drawable.pooshak;
		case SHAKHSHI:
			return R.drawable.shakhsi;
		case HADIYE_KHEYRIE:
			return R.drawable.hadiye_kheirie;
		case GHARZ:
			return R.drawable.gharz;
		case AGHSAAT:
			return R.drawable.aghsaat;
		case SARMAYE:
			return R.drawable.sarmaye;
		default:
			return R.drawable.uncategorized;
		}
	}

	public static int getIncomeIconID(int id)
	{
		switch (id)
		{
		case PADASH:
			return R.drawable.padash;
		case DASTMOZD:
			return R.drawable.dastmozd;
		case YARANEH:
			return R.drawable.yaraneh;
		case HADIYE:
			return R.drawable.hadiye;
		case EJAREH:
			return R.drawable.ejareh;
		case SOOD:
			return R.drawable.sood;
		case KHESARAT:
			return R.drawable.khesarat;
		case FOROOSH_DARAYI:
			return R.drawable.foroosh_darayi;
		case VADIE_DARYAFTI:
			return R.drawable.vadie_daryafti;
		case TALAB:
			return R.drawable.talab;
		case VAAM:
			return R.drawable.vaam;
		default:
			return R.drawable.uncategorized;
		}
	}


    public static int getExpenseColorID(int id)
    {
        switch (id) {
            case RAFTO_AMAD:
                return R.color.category_expense_RAFTO_AMAD;
            case SAKHTEMAN:
                return R.color.category_expense_SAKHTEMAN;
            case KHORAK:
                return R.color.category_expense_KHORAK;
            case AMOOZESH:
                return R.color.category_expense_AMOOZESH;
            case GHABZ:
                return R.color.category_expense_GHABZ;
            case VARZESH:
                return R.color.category_expense_VARZESH;
            case DARMAN:
                return R.color.category_expense_DARMAN;
            case ARAYESH:
                return R.color.category_expense_ARAYESH;
            case POOSHAK:
                return R.color.category_expense_POOSHAK;
            case SHAKHSHI:
                return R.color.category_expense_SHAKHSHI;
            case HADIYE_KHEYRIE:
                return R.color.category_expense_HADIYE_KHEYRIE;
            case GHARZ:
                return R.color.category_expense_GHARZ;
            case AGHSAAT:
                return R.color.category_expense_AGHSAAT;
            case SARMAYE:
                return R.color.category_expense_SARMAYE;
            default:
                return R.color.category_expense_UNCATEGORIZED;
        }
    }
    public static int getIncomeColorID(int id)
    {
        switch (id) {
            case PADASH:
                return R.color.category_income_PADASH;
            case DASTMOZD:
                return R.color.category_income_DASTMOZD;
            case YARANEH:
                return R.color.category_income_YARANEH;
            case HADIYE:
                return R.color.category_income_HADIYE;
            case EJAREH:
                return R.color.category_income_EJAREH;
            case SOOD:
                return R.color.category_income_SOOD;
            case KHESARAT:
                return R.color.category_income_KHESARAT;
            case FOROOSH_DARAYI:
                return R.color.category_income_FOROOSH_DARAYI;
            case VADIE_DARYAFTI:
                return R.color.category_income_VADIE_DARYAFTI;
            case TALAB:
                return R.color.category_income_TALAB;
            case VAAM:
                return R.color.category_income_VAAM;
            default:
                return R.color.category_income_UNCATEGORIZED;
        }
    }

	public static int getExpenseRawIconID(int id)
	{
		switch (id)
		{
		case RAFTO_AMAD:
			return R.drawable.rafto_amad_raw;
		case SAKHTEMAN:
			return R.drawable.sakhteman_raw;
		case KHORAK:
			return R.drawable.khorak_raw;
		case AMOOZESH:
			return R.drawable.amoozesh_raw;
		case GHABZ:
			return R.drawable.ghabz_raw;
		case VARZESH:
			return R.drawable.varzesh_raw;
		case DARMAN:
			return R.drawable.darman_raw;
		case ARAYESH:
			return R.drawable.arayesh_raw;
		case POOSHAK:
			return R.drawable.pooshak_raw;
		case SHAKHSHI:
			return R.drawable.shakhsi_raw;
		case HADIYE_KHEYRIE:
			return R.drawable.hadiye_kheirie_raw;
		case GHARZ:
			return R.drawable.gharz_raw;
		case AGHSAAT:
			return R.drawable.aghsaat_raw;
		case SARMAYE:
			return R.drawable.sarmaye_raw;
		default:
			return R.drawable.uncategorized_raw;
		}
	}

	public static int getIncomeRawIconID(int id)
	{
		switch (id)
		{
		case PADASH:
			return R.drawable.padash_raw;
		case DASTMOZD:
			return R.drawable.dastmozd_raw;
		case YARANEH:
			return R.drawable.yaraneh_raw;
		case HADIYE:
			return R.drawable.hadiye_raw;
		case EJAREH:
			return R.drawable.ejareh_raw;
		case SOOD:
			return R.drawable.sood_raw;
		case KHESARAT:
			return R.drawable.khesarat_raw;
		case FOROOSH_DARAYI:
			return R.drawable.foroosh_darayi_raw;
		case VADIE_DARYAFTI:
			return R.drawable.vadie_daryafti_raw;
		case TALAB:
			return R.drawable.talab_raw;
		case VAAM:
			return R.drawable.vaam_raw;
		default:
			return R.drawable.uncategorized_raw;
		}
	}
}
