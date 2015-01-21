package ir.abplus.adanalas.Timeline;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import ir.abplus.adanalas.AddEditTransactions.AddPage1;
import ir.abplus.adanalas.AddEditTransactions.EditTransactionPage;
import ir.abplus.adanalas.Libraries.Category;
import ir.abplus.adanalas.Libraries.Currency;
import ir.abplus.adanalas.R;
import ir.abplus.adanalas.Setting.SettingActivity;
import ir.abplus.adanalas.databaseConnections.LocalDBServices;

import java.text.ParseException;

public class OnClickedDialog extends Dialog implements
        android.view.View.OnClickListener {

    public Activity c;
    ImageView iconImage;
    TextView amount;
    TextView account;
    TimelineItem2 item;
    LinearLayout tagVerticalLayout;
    LinearLayout currencyLayout;
    TextView descpTextVIew;
    TextView dateTextView;
    Resources res;
    Button backButton;
    Button deleteButton;
    Button editButton;
    GradientDrawable roundButton;

    public OnClickedDialog(Activity a,TimelineItem2 timelineItem,Resources resources) {
        super(a);
        // TODO Auto-generated constructor stub
        this.c = a;
        this.item=timelineItem;
        this.res=resources;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.selected_dialog);

        iconImage=(ImageView)findViewById(R.id.icon_image);
        amount=(TextView) findViewById(R.id.amount_text);

        dateTextView=(TextView) findViewById(R.id.date_text);
        roundButton=(GradientDrawable)res.getDrawable(R.drawable.roundshapebtn);
        backButton=(Button)findViewById(R.id.back_button);
        editButton=(Button)findViewById(R.id.edit_button);
        account=(TextView)findViewById(R.id.account_text);
        deleteButton=(Button)findViewById(R.id.delete_button);
        currencyLayout=(LinearLayout)findViewById(R.id.currency_layout);
        roundButton.setColor(res.getColor(R.color.light_grey));

        if(item.isExpence())
        {
            iconImage.setImageResource(Category.getExpenseIconID(item.getCategoryID()));
//            amount.setTextColor(res.getColor(Category.getExpenseColorID(item.categoryID)));
        }
        else
        {
            iconImage.setImageResource(Category.getIncomeIconID(item.getCategoryID()));
//            amount.setTextColor(res.getColor(Category.getIncomeColorID(item.categoryID)));
        }


        amount.setTypeface(TimelineActivity.persianTypeface);
        amount.setText("مبلغ: "+Currency.getStdAmount(item.getAmount()));
        amount.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);


        Currency.setCurrencyLayout(currencyLayout,getContext(),res.getColor(R.color.black), TimelineActivity.persianTypeface, Currency.SMALL_TEXT_SIZE);

        account.setTypeface(TimelineActivity.persianTypeface);
        account.setText("حساب: "+item.getAccountName());

        dateTextView.setText("تاریخ: "+item.getFormatedDate()+"     ساعت: "+item.getFormatedTime());
        dateTextView.setTypeface(TimelineActivity.persianTypeface);
        descpTextVIew=(TextView)findViewById(R.id.description_text);

        tagVerticalLayout =(LinearLayout)findViewById(R.id.tags_horiz_layout);


        TextView tv=new TextView(getContext());
        tv.setText("برچسب:");
        tv.setPadding(0,0,10,0);
        tv.setTypeface(TimelineActivity.persianTypeface);
        tagVerticalLayout.addView(tv);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params2.gravity= Gravity.RIGHT;
        params.setMargins(10, 0, 10, 5);



        if(item.getTags()!=null)
            if(item.getTags().size()>0){
                LinearLayout a=new LinearLayout(getContext());
                a.setLayoutParams(params2);
                a.setGravity(Gravity.RIGHT);
                for(int i=0;i<item.getTags().size();i++)
                {
                    if(item.getTags().get(i)!=null) {
                        Button b = new Button(getContext());
                        b.setText(item.getTags().get(i));
                        b.setTypeface(TimelineActivity.persianTypeface);
                        b.setBackground(roundButton);
                        b.setLayoutParams(params);
                        b.setMinHeight(0);
                        b.setMinWidth(0);
                        b.setIncludeFontPadding(false);
                        b.setMinimumHeight(0);
                        b.setMinimumWidth(0);
                        b.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
                        b.setPadding(10, 5,10, 5);
                        a.addView(b);
                    }
                }
                tagVerticalLayout.addView(a);
            }


        descpTextVIew.setTypeface(TimelineActivity.persianTypeface);
        if(item.getDescription()!=null&&!item.getDescription().isEmpty()){
            descpTextVIew.setText("توضیحات: \n   "+item.getDescription());
        }
        else
            descpTextVIew.setText("توضیحات: \n   "+"ندارد");


        backButton.setCompoundDrawablesWithIntrinsicBounds(null,null,res.getDrawable(R.drawable.back),null);
        deleteButton.setCompoundDrawablesWithIntrinsicBounds(null,null,res.getDrawable(R.drawable.delete),null);
        editButton.setCompoundDrawablesWithIntrinsicBounds(null,null,res.getDrawable(R.drawable.edit),null);

        backButton.setTypeface(TimelineActivity.persianTypeface);
        backButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        backButton.setBackground(roundButton);
        backButton.setOnClickListener(this);
        deleteButton.setTypeface(TimelineActivity.persianTypeface);
        deleteButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        deleteButton.setBackground(roundButton);
        deleteButton.setOnClickListener(this);
        editButton.setTypeface(TimelineActivity.persianTypeface);
        editButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        editButton.setBackground(roundButton);
        editButton.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        System.out.println(v.toString());
        switch (v.getId()) {
            case R.id.edit_button:
                if(SettingActivity.getAccountType(item.getAccountName(),getContext()).equals("1"))
                {Intent intent = new Intent(getContext(), AddPage1.class);
                intent.putExtra(AddPage1.TRANSACTION_ID_KEY, item.getTransactionID());
                getContext().startActivity(intent);}
                else if(SettingActivity.getAccountType(item.getAccountName(),getContext()).equals("0")){
                    {Intent intent = new Intent(getContext(), EditTransactionPage.class);
                        intent.putExtra(AddPage1.TRANSACTION_ID_KEY, item.getTransactionID());
                        getContext().startActivity(intent);}
                }
                break;
            case R.id.back_button:
                dismiss();
                break;
            case R.id.delete_button:
                deleteTransaction();
                break;
            default:
                break;
        }
        dismiss();
    }
    public void deleteTransaction(){
//        TransactoinDatabaseHelper trHelper= new TransactoinDatabaseHelper(getContext());
//        SQLiteDatabase db = trHelper.getWritableDatabase();
//        String selection = TransactionsContract.TransactionEntry._ID + " LIKE ?";
        String[] selectionArgs = { String.valueOf(item.getTransactionID()) };
//        db.delete(TransactionsContract.TransactionEntry.TABLE_NAME,selection,selectionArgs);
        LocalDBServices.deleteTransactionFromDB(getContext(),selectionArgs);
        try {
            ((TimelineActivity)c).immediateRefreshTimeline();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}