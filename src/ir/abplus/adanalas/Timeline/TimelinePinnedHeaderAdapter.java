package ir.abplus.adanalas.Timeline;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.lb.auto_fit_textview.AutoResizeTextView;
import ir.abplus.adanalas.Libraries.Category;
import ir.abplus.adanalas.Libraries.Currency;
import ir.abplus.adanalas.R;
import za.co.immedia.pinnedheaderlistview.SectionedBaseAdapter;

import java.util.ArrayList;
import java.util.List;

public class TimelinePinnedHeaderAdapter extends SectionedBaseAdapter{
    private static final boolean CURRENCY_IN_TIMELINE = true;

    private static final int INCOME = 0;
    private static final int EXPENSE = 1;
    private static final int INCOME_SELECTED = 3;
    private static final int EXPENSE_SELECTED = 4;
    private static final float MIN_DATE_FONT_SIZE = 15;
    private static final float MIN_AMOUNT_FONT_SIZE = 16;
    private static final float MAX_ICON_HEIGHT = 75/2;
    private static final float MAX_ICON_WIDTH = MAX_ICON_HEIGHT;

    private TimelineItem2[][] items;
    private LayoutInflater inflater;
    private Resources res;
    ViewHolder holder;
    HeaderViewHolder headerHolder;
    Context context;
    private LayerDrawable shapeINCl;
    private LayerDrawable shapeINCc;
    private LayerDrawable shapeINCr;
    private LayerDrawable shapeEXPl;
    private LayerDrawable shapeEXPc;
    private LayerDrawable shapeEXPr;

    //
    private int layoutWidth;
    GradientDrawable roundshapebtn;


    public TimelinePinnedHeaderAdapter(Context context, Resources resources, int layoutWidth)
    {
        this.context = context;
        inflater = LayoutInflater.from(context);
        res=resources;
        this.layoutWidth=layoutWidth;
    }

    public void setItems(ArrayList<TimelineItem2> items)	{
        ArrayList<ArrayList<TimelineItem2>> tmp = new ArrayList<ArrayList<TimelineItem2>>();
        int size = items.size();
        String tmpDate = "";
        int l = 0;
        List<TimelineItem2> current = null;
        for(int i = 0; i < size; i++)
        {
            TimelineItem2 cur = items.get(i);
            if(!tmpDate.equals(cur.getDateString().substring(0,8)))
            {
                tmpDate = cur.getDateString().substring(0,8);
                tmp.add(new ArrayList<TimelineItem2>());
                current = tmp.get(l);
                l++;
            }
            current.add(cur);
        }

        this.items = new TimelineItem2[tmp.size()][];
        size = tmp.size();
        int rSize = 0;
        for(int i = 0; i < size; i++)
        {
            current = tmp.get(i);
            rSize = tmp.get(i).size();
            this.items[i] = new TimelineItem2[rSize];
            for(int j = 0; j < rSize; j++)
                this.items[i][j] = current.get(j);
        }
    }

    @Override
    public int getItemViewTypeCount()
    {
        return 4;
    }

    @Override
    public int getItemViewType(int section, int position)
    {

        if(section >= items.length || position >= items[section].length)
            return -1;
//		return items[section][position].isExpence ? EXPENSE : INCOME;
            return items[section][position].isExpence() ? EXPENSE : INCOME;

    }

    @Override
    public int getCountForSection(int section) {
        return items[section].length;
    }

    @Override
    public Object getItem(int section, int position) {
        return items[section][position];
    }

    @Override
    public long getItemId(int section, int position) {
        return section*10000+position;
    }

    @Override
    public View getItemView(final int section, final int position, View convertView, ViewGroup parent) {
        if(items==null)
            return convertView;
        if (section >= items.length || position >= items[section].length)
            return convertView;
        int id = items[section][position].getCategoryID();
        holder = new ViewHolder();
        roundshapebtn=(GradientDrawable)res.getDrawable(R.drawable.roundshapebtn);

        switch (getItemViewType(section, position)) {
            case EXPENSE:
                convertView = inflater.inflate(R.layout.timeline_expense_row, parent, false);
                holder.categoryAndTime = (AutoResizeTextView) convertView.findViewById(R.id.expense_category_and_time);
                holder.amount = (AutoResizeTextView) convertView.findViewById(R.id.expense_amount);
                holder.currency = (LinearLayout) convertView.findViewById(R.id.expense_currency_layout);
                holder.tagsLayout = (LinearLayout) convertView.findViewById(R.id.expense_tags_layout);
                shapeEXPl = (LayerDrawable) res.getDrawable(R.drawable.left_background_expense);
                shapeEXPc = (LayerDrawable) res.getDrawable(R.drawable.simple_button_background_expense);
                shapeEXPr = (LayerDrawable) res.getDrawable(R.drawable.right_background_expense);
                holder.icon = (ImageView) convertView.findViewById(R.id.expense_icon);
                break;
            case INCOME:
                convertView = inflater.inflate(R.layout.timeline_income_row, parent, false);
                holder.categoryAndTime = (AutoResizeTextView) convertView.findViewById(R.id.income_category_and_time);
                holder.amount = (AutoResizeTextView) convertView.findViewById(R.id.income_amount);
                holder.currency = (LinearLayout) convertView.findViewById(R.id.income_currency_layout);
                holder.tagsLayout = (LinearLayout) convertView.findViewById(R.id.income_tags_layout);
                shapeINCl = (LayerDrawable) res.getDrawable(R.drawable.left_background_income);
                shapeINCc = (LayerDrawable) res.getDrawable(R.drawable.simple_button_background_income);
                shapeINCr = (LayerDrawable) res.getDrawable(R.drawable.right_background_income);
                holder.icon = (ImageView) convertView.findViewById(R.id.income_icon);
                break;
            default:
                Log.e("bug 101", "holder is not set!" + getItemViewType(section, position));
                break;
        }


            holder.categoryAndTime.setTypeface(TimelineActivity.persianTypeface);
            if (items[section][position].isExpence()) {
                holder.categoryAndTime.setMinTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, MIN_DATE_FONT_SIZE, context.getResources().getDisplayMetrics()));
                holder.categoryAndTime.setText(items[section][position].getFormatedTime());
                holder.icon.setImageResource(Category.getExpenseRawIconID(id));
                ((GradientDrawable) shapeEXPl.getDrawable(0)).setStroke(2, res.getColor(Category.getExpenseColorID(id)));
                ((GradientDrawable) shapeEXPc.getDrawable(0)).setStroke(2, res.getColor(Category.getExpenseColorID(id)));
                ((GradientDrawable) shapeEXPr.getDrawable(0)).setStroke(2, res.getColor(Category.getExpenseColorID(id)));

                //added by keyvan
                holder.currency.setBackground(shapeEXPr);
                holder.amount.setBackground(shapeEXPc);
                holder.icon.setBackground(shapeEXPl);
            }
            else {
                holder.categoryAndTime.setMinTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, MIN_DATE_FONT_SIZE, context.getResources().getDisplayMetrics()));
                holder.categoryAndTime.setText(items[section][position].getFormatedTime());
                holder.icon.setImageResource(Category.getIncomeRawIconID(id));
                ((GradientDrawable) shapeINCl.getDrawable(0)).setStroke(2, res.getColor(Category.getIncomeColorID(id)));
                ((GradientDrawable) shapeINCc.getDrawable(0)).setStroke(2, res.getColor(Category.getIncomeColorID(id)));
                ((GradientDrawable) shapeINCr.getDrawable(0)).setStroke(2, res.getColor(Category.getIncomeColorID(id)));

                //added by keyvan
                holder.currency.setBackground(shapeINCl);
                holder.amount.setBackground(shapeINCc);
                holder.icon.setBackground(shapeINCr);
            }

            if (CURRENCY_IN_TIMELINE) {
                Currency.setCurrencyLayout(holder.currency, context,
                        context.getResources().getColor(R.color.black), TimelineActivity.persianTypeface,
                        Currency.SMALL_TEXT_SIZE);
            }




            holder.icon.setMaxHeight((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, MAX_ICON_HEIGHT, context.getResources().getDisplayMetrics()));
            holder.icon.setMaxWidth((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, MAX_ICON_WIDTH, context.getResources().getDisplayMetrics()));
            holder.icon.setAdjustViewBounds(true);
            holder.amount.setTypeface(TimelineActivity.persianTypeface);
            holder.amount.setMinTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, MIN_AMOUNT_FONT_SIZE, context.getResources().getDisplayMetrics()));
            holder.amount.setText(Currency.getStdAmount(items[section][position].getAmount()));
        if (items[section][position].isExpence()) {
                if (Currency.getStdAmount(items[section][position].getAmount()).length() < 6)
                    holder.amount.setPadding(20, 0, 40, 0);
                else
                    holder.amount.setPadding(10, 0, 20, 0);
            } else {
                if (Currency.getStdAmount(items[section][position].getAmount()).length() < 6)
                    holder.amount.setPadding(40, 0, 20, 0);
                else
                    holder.amount.setPadding(20, 0, 10, 0);
            }

            addTagsToLayout3(section, position);

        return convertView;
    }

    @Override
    public int getSectionCount()
    {
        return items.length;
    }

    @Override
    public View getSectionHeaderView(int section, View convertView, ViewGroup parent)
    {
        if(section >= items.length)
            return convertView;

        if (convertView == null)
        {
            headerHolder = new HeaderViewHolder();
            convertView = inflater.inflate(R.layout.test_header, parent, false);
            headerHolder.date = (TextView) convertView.findViewById(R.id.header_text);
            convertView.setTag(headerHolder);
        }
        else
        {
            headerHolder = (HeaderViewHolder) convertView.getTag();
        }

        convertView.setOnClickListener(null);
        if(headerHolder.date!=null){
            headerHolder.date.setTypeface(TimelineActivity.persianBoldTypeface);
            headerHolder.date.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
            headerHolder.date.setText(items[section][0].getFormatedDate());
        }
        return convertView;
    }


    class ViewHolder
    {
        AutoResizeTextView categoryAndTime;
        ImageView icon;
        LinearLayout tagsLayout;
        LinearLayout currency;
        AutoResizeTextView amount;
        Boolean isMeasured=false;
    }

    class HeaderViewHolder
    {
        TextView date;
    }


    private void addTagsToLayout3(int section, int position) {

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(5, 0, 5, 0);
        params.gravity= Gravity.CENTER_VERTICAL;
       DisplayMetrics displayMetrics = res.getDisplayMetrics();

                if (holder.tagsLayout != null) {
                    holder.tagsLayout.removeAllViews();
                    if (items[section][position].getTags().get(0) != null) {
                        if (items[section][position].getTags().size() > 0) {
                            int tmpSize = layoutWidth;
                            holder.tagsLayout.setLayoutParams(params);
                                for (String s : items[section][position].getTags()) {
                                    Button tmpB=new Button(context);
                                    tmpB.setText(s);
                                    tmpB.setTypeface(TimelineActivity.persianTypeface);
                                    tmpB.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                                    tmpB.setMinimumWidth(0);
                                    tmpB.setMinimumHeight(0);
                                    tmpB.setMinHeight(0);
                                    tmpB.setMinWidth(0);
                                    tmpB.setFocusable(false);
                                    roundshapebtn.setColor(res.getColor(R.color.light_grey));
                                    tmpB.setBackground(roundshapebtn);
                                    tmpB.getBackground().setAlpha(255);
                                    tmpB.setLayoutParams(params);
                                    tmpB.setMaxLines(1);
                                    tmpB.measure(0, 0);
                                    int btnSize= (int) (tmpB.getMeasuredWidth()/displayMetrics.density);

                                    if (btnSize < tmpSize) {
                                        holder.tagsLayout.addView(tmpB);
                                        tmpSize -= btnSize;
                                    } else {
                                        ((Button)holder.tagsLayout.getChildAt(0)).setText("...");
                                        break;
                                    }


                                }

                        }
                    }
                }
    }
}
