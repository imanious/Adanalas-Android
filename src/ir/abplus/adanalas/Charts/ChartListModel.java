package ir.abplus.adanalas.Charts;

import android.app.Activity;
import ir.abplus.adanalas.Libraries.Category;

public class ChartListModel {


    private long amount;
//    private Button itemCategoryText;
//    private ImageView itemIcon;
    private String textOnButton;

    private int iconID;
    private int iconColorID;
    private boolean isExpense;
    private String transCounter;


    public ChartListModel(Activity a,boolean isExpense, int categoryID, long amount,String transCounter) {
        setAmount(amount);
        setTransCounter(transCounter);
//        itemIcon=new ImageView(a);
//        itemCategoryText=new Button(a);

        if(isExpense){
            setIconColorID(Category.getExpenseColorID(categoryID));
            setIconID((Category.getExpenseRawIconID(categoryID)));
//        itemIcon.setImageResource(Category.getExpenseRawIconID(categoryID));
//        itemCategoryText.setBackgroundColor(Category.getExpenseColorID(categoryID));
        setTextOnButton(Category.expenseCategories[categoryID]);
//            setItemIcon(itemIcon);
//            setItemCategoryText(itemCategoryText);
        }
        else {
              setIconID(Category.getIncomeRawIconID(categoryID));
              setIconColorID(Category.getIncomeColorID(categoryID));
//            itemIcon.setImageResource(Category.getIncomeRawIconID(categoryID));
//            itemCategoryText.setBackgroundColor(Category.getIncomeColorID(categoryID));
            setTextOnButton(Category.incomeCategories[categoryID]);
//            setItemIcon(itemIcon);
//            setItemCategoryText(itemCategoryText);
        }
    }

    /*********** Set Methods ******************/

    public void setAmount(long amount) {
        this.amount = amount;
    }

    /*********** Get Methods ****************/

    public long getAmount() {
        return amount;
    }


    public String getTextOnButton() {
        return textOnButton;
    }

    public void setTextOnButton(String textOnButton) {
        this.textOnButton = textOnButton;
    }

    public boolean isExpense() {
        return isExpense;
    }

    public void setExpense(boolean isExpense) {
        this.isExpense = isExpense;
    }



    public int getIconID() {
        return iconID;
    }

    public void setIconID(int iconID) {
        this.iconID = iconID;
    }

    public int getIconColorID() {
        return iconColorID;
    }

    public void setIconColorID(int iconColorID) {
        this.iconColorID = iconColorID;
    }

    public String getTransCounter() {
        return transCounter;
    }

    public void setTransCounter(String transCounter) {
        this.transCounter = transCounter;
    }
}