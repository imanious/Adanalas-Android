package ir.abplus.adanalas.Charts;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import ir.abplus.adanalas.Libraries.Currency;
import ir.abplus.adanalas.R;
import ir.abplus.adanalas.Timeline.TimelineActivity;

import java.util.ArrayList;

/********* Adapter class extends with BaseAdapter and implements with OnClickListener ************/
public class ChartListCustomAdapter extends BaseAdapter implements View.OnClickListener {

    /*********** Declare Used Variables *********/
    private Activity activity;
    private ArrayList data;
    private static LayoutInflater inflater=null;
    public Resources res;
    ChartListModel tempValues=null;
    int i=0;
    private GradientDrawable shape;

    /*************  CustomAdapter Constructor *****************/
    public ChartListCustomAdapter(Activity a, ArrayList d, Resources resLocal) {

        /********** Take passed values **********/
        activity = a;
        data=d;
        res = resLocal;

        /***********  Layout inflator to call external xml layout () ***********/
        inflater = ( LayoutInflater )activity.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    /******** What is the size of Passed Arraylist Size ************/
    public int getCount() {

        if(data.size()<=0)
            return 1;
        return data.size();
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    /********* Create a holder Class to contain inflated xml file elements *********/
    public static class ViewHolder{

//        public TextView text;
//        public TextView text1;
//        public TextView textWide;
//        public ImageView image;
          public TextView amountText;
          public ImageView ImageIcon;
          public Button textCategory;
          public TextView transCounter;

    }

    /****** Depends upon data size called for each row , Create each ListView row *****/
    public View getView(int position, View convertView, ViewGroup parent) {

        View vi = convertView;
        ViewHolder holder;

        if(convertView==null){

            /****** Inflate chart_list_item.xml file for each row ( Defined below ) *******/
            vi = inflater.inflate(R.layout.chart_list_item, null);

            /****** View Holder Object to contain tabitem.xml file elements ******/

            holder = new ViewHolder();
            holder.amountText = (TextView) vi.findViewById(R.id.amount_text_view);
            holder.ImageIcon=(ImageView)vi.findViewById(R.id.item_icon);
            holder.textCategory=(Button)vi.findViewById(R.id.item_category_text);
            holder.transCounter=(TextView)vi.findViewById(R.id.trans_counter);
            shape = (GradientDrawable) res.getDrawable(R.drawable.roundshapebtn);
//            System.out.println("it gets here !");
//            holder.amountText.setText("www");

            /************  Set holder with LayoutInflater ************/
            vi.setTag( holder );
        }
        else
            holder=(ViewHolder)vi.getTag();

        if(data.size()<=0)
        {
            holder.ImageIcon.setImageDrawable(null);
            holder.textCategory.setText("");
            holder.textCategory.setBackground(null);
            holder.amountText.setText("");
            holder.transCounter.setText("");
//            shape.setColor();
//            row = (TableRow)vi.findViewById(R.id.);
//            table.removeView(row);
//            holder.amountText.setText("No Data");

        }
        else
        {
            /***** Get each Model object from Arraylist ********/
            tempValues=null;
            tempValues = ( ChartListModel ) data.get( position );

            /************  Set Model values in Holder elements ***********/
//            System.out.println(data.size());
//            holder.amountText.setText("rrrr");
//            System.out.println(tempValues.getAmount());
            shape.setColor(res.getColor(tempValues.getIconColorID()));
            holder.textCategory.setBackground(shape);
            holder.amountText.setText(Currency.getStdAmount(tempValues.getAmount()) + " "+ Currency.getCurrencyString());
            holder.amountText.setTypeface(TimelineActivity.persianTypeface);
            holder.textCategory.setTypeface(TimelineActivity.persianTypeface);
            holder.textCategory.setTextColor(Color.WHITE);
            holder.textCategory.setText(tempValues.getTextOnButton());
            holder.amountText.setTextColor(res.getColor(tempValues.getIconColorID()));
            holder.ImageIcon.setImageResource(tempValues.getIconID());
            holder.transCounter.setTypeface(TimelineActivity.persianTypeface);
            holder.transCounter.setTextColor(res.getColor(tempValues.getIconColorID()));
            holder.transCounter.setText(tempValues.getTransCounter()+" تراکنش");

            /******** Set Item Click Listner for LayoutInflater for each row *******/
//            vi.setOnClickListener(new OnItemClickListener( position ));
        }
        return vi;
    }

    @Override
    public void onClick(View v) {
        Log.v("CustomAdapter", "=====Row button clicked=====");
    }

    /********* Called when Item click in ListView ************/
    private class OnItemClickListener  implements View.OnClickListener {
        private int mPosition;

        OnItemClickListener(int position){
            mPosition = position;
        }

        @Override
        public void onClick(View arg0) {


//            CustomListViewAndroidExample sct = (CustomListViewAndroidExample)activity;

            /****  Call  onItemClick Method inside CustomListViewAndroidExample Class ( See Below )****/

//            sct.onItemClick(mPosition);
        }
    }
}