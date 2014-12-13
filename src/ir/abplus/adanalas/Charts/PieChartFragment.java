package ir.abplus.adanalas.Charts;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.echo.holographlibrary.PieGraph;
import com.echo.holographlibrary.PieGraph.OnSliceClickedListener;
import com.echo.holographlibrary.PieSlice;
import ir.abplus.adanalas.Libraries.Category;
import ir.abplus.adanalas.Libraries.Currency;
import ir.abplus.adanalas.Libraries.TransactoinDatabaseHelper;
import ir.abplus.adanalas.R;
import ir.abplus.adanalas.Timeline.TimelineActivity;
import ir.abplus.adanalas.databaseConnections.LocalDBServices;

import java.util.ArrayList;

public class PieChartFragment extends Fragment {

    TransactoinDatabaseHelper trHelper;
    Cursor c;
    View v ;
    //    View listLayout ;
    Resources resources ;
    PieGraph pg ;
    TextView textView;

    ListView list;
    ChartListCustomAdapter adapter;
    public  ChartActivity CustomListView = null;
    public ArrayList<ChartListModel> CustomListViewValuesArr = new ArrayList<ChartListModel>();
    private boolean isExpense;
    private ScrollView scrollView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        v=View.inflate(getActivity(), R.layout.pie_chart_fragment, null);
        resources = getResources();
        pg = (PieGraph) v.findViewById(R.id.piegraph);
        textView=(TextView)v.findViewById(R.id.oncentertextView);
        scrollView = (ScrollView)v.findViewById(R.id.scrollV);



        CustomListView =(ChartActivity) getActivity();
        list= (ListView)v.findViewById(R.id.list);  // List defined in XML ( See Below )

        adapter=new ChartListCustomAdapter( CustomListView, CustomListViewValuesArr,getActivity().getResources() );

        UpdateDatabaseCursor();
        updatePieChart();

        if(pg.getSlices().size()>1)
            pg.setPadding(1);



        //animate to first slice
//         setDefultSelectedSlice();



        pg.setOnSliceClickedListener(new OnSliceClickedListener() {



            @Override
            public void onClick(final int index) {

                if(index!=-1){
                    ValueAnimator va = ValueAnimator.ofFloat(0,1);
                    va.setDuration(1000);
//                    int i=0;
//                    boolean havetoRemove=false;
//                    for(ChartListModel tmp:CustomListViewValuesArr){
//                        if(pg.getSlice(index).getTitle().contains(tmp.getTextOnButton())){
//                            havetoRemove=true;
//                            break;
//                        }
//                        i++;
//                    }
//                    if(havetoRemove) {
//                        ChartListModel tmp = CustomListViewValuesArr.get(i);
//                        CustomListViewValuesArr.remove(i);
//                        CustomListViewValuesArr.add(0, tmp);
//                    }
//                    adapter=new ChartListCustomAdapter( CustomListView, CustomListViewValuesArr,getActivity().getResources() );
//                    list.setAdapter( adapter );

                    ArrayList<ChartListModel> tmpCustomListViewValuesArr = new ArrayList<ChartListModel>();
                    int i=0;
                    boolean shouldChange=false;
                    for(ChartListModel tmp:CustomListViewValuesArr){
                        if(pg.getSlice(index).getTitle().contains(tmp.getTextOnButton())){
                            shouldChange=true;
                            break;
                        }
                        i++;
                    }
                    if(shouldChange) {
                        ChartListModel tmp = CustomListViewValuesArr.get(i);
                        tmpCustomListViewValuesArr.add(tmp);
                    }
                    adapter=new ChartListCustomAdapter( CustomListView, tmpCustomListViewValuesArr,getActivity().getResources() );
                    list.setAdapter( adapter );

                    va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator animation) {
                            float f = Math.max(animation.getAnimatedFraction(), 0.01f);//avoid blank frames; never multiply values by 0
                            float x = -1 * pg.angels.get(index) - pg.getRotation();
                            pg.setRotation(pg.getRotation() + (x * f));
                            pg.postInvalidate();
                        }
                    });
                    va.start();
                }
            }
        });


        scrollView.scrollTo(0, 0);
        return v;
    }

    private void setDefultSelectedSlice() {
        if(c.getCount()>0&&pg.getSlices().size()>0) {
            Log.i("www",pg.getSlices().size()+" "+pg.angels.size());
            final int index = 0;
            ValueAnimator va = ValueAnimator.ofFloat(0, 1);
            va.setDuration(1000);
            int i = 0;
            boolean havetoRemove = false;
            for (ChartListModel tmp : CustomListViewValuesArr) {
                if (pg.getSlice(index).getTitle().contains(tmp.getTextOnButton())) {
                    havetoRemove = true;
                    break;
                }
                i++;
            }
            if (havetoRemove) {
                ChartListModel tmp = CustomListViewValuesArr.get(i);
                CustomListViewValuesArr.remove(i);
                CustomListViewValuesArr.add(0, tmp);
            }
            adapter = new ChartListCustomAdapter(CustomListView, CustomListViewValuesArr, getActivity().getResources());
            list.setAdapter(adapter);
            va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float f = Math.max(animation.getAnimatedFraction(), 0.01f);//avoid blank frames; never multiply values by 0
                    float x = -1 * pg.angels.get(index) - pg.getRotation();
                    pg.setRotation(pg.getRotation() + (x * f));
                    pg.postInvalidate();
                }
            });
            va.start();
        }
    }

    private void setListData() {
        CustomListViewValuesArr.removeAll(CustomListViewValuesArr);
        if(c.getCount() != 0) {
            c.moveToFirst();
            do {
                int categoryID = c.getInt(c.getColumnIndexOrThrow("A"));
                double amount = c.getDouble(c.getColumnIndexOrThrow("B"));
                int boolExpense = c.getInt(c.getColumnIndexOrThrow("C"));
                int trans_count=c.getInt(c.getColumnIndexOrThrow("D"));

                if(isExpense){
                    if(boolExpense==1){
//                        counter++;
                        ChartListModel sched = new ChartListModel(getActivity(),true,categoryID,amount,trans_count+"");
                        CustomListViewValuesArr.add( sched );
                    }

                }
                else {
                    if(boolExpense==0){
                        ChartListModel sched = new ChartListModel(getActivity(),false,categoryID,amount,trans_count+"");
                        CustomListViewValuesArr.add( sched );                    }
                }


            } while (c.moveToNext());

        }
        adapter=new ChartListCustomAdapter( CustomListView, CustomListViewValuesArr,getActivity().getResources() );
        list.setAdapter( adapter );
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
    public Animator.AnimatorListener getAnimationListener(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1)
            return new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    Log.d("piefrag", "anim end");
                }

                @Override
                public void onAnimationCancel(Animator animation) {//you might want to call slice.setvalue(slice.getGoalValue)
                    Log.d("piefrag", "anim cancel");
                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            };
        else return null;

    }

    public void UpdateDatabaseCursor(){
//
//        SQLiteDatabase db = trHelper.getReadableDatabase();
//
//        String query="SELECT "+TransactionsContract.TransactionEntry.COLUMN_NAME_CATEGORY
//                    +" as A, SUM("+TransactionsContract.TransactionEntry.COLUMN_NAME_AMOUNT
//                    +") as B, "+TransactionsContract.TransactionEntry.COLUMN_NAME_IS_EXPENSE
//                    +" as C , COUNT (*) AS D "
//                    +"FROM "+ TransactionsContract.TransactionEntry.TABLE_NAME+ " WHERE "
//                    +((ChartActivity)getActivity()).getWhereClause()+" GROUP BY "
//                    +"A, C ORDER BY "
//                    +TransactionsContract.TransactionEntry.COLUMN_NAME_AMOUNT+" DESC";
//        c = db.rawQuery(query, null);

        c= LocalDBServices.getTransactionsFromDBGroupedBYCategory(((ChartActivity)getActivity()).getWhereClause());
//        System.out.println("%%%"+query);
        if(c.getCount() == 0)
            System.out.println("empty");
        isExpense=((ChartActivity)getActivity()).isExpense;
        updatePieChart();
        setListData();
//        setDefultSelectedSlice();
        setListViewHeightBasedOnChildren(list);
        scrollView.scrollTo(0, 0);
    }

    private void updatePieChart(){
        pg.removeSlices();

        if(c.getCount() != 0) {
            int counter=0;
            int amountSum=0;
            int trans_count=0;
            c.moveToFirst();
            do {


                int name = c.getInt(c.getColumnIndexOrThrow("A"));
                double amount = c.getDouble(c.getColumnIndexOrThrow("B"));
                int boolExpense = c.getInt(c.getColumnIndexOrThrow("C"));
                trans_count=c.getInt(c.getColumnIndexOrThrow("D"));

                if(isExpense){
                    if(boolExpense==1){
                        amountSum+=amount;
                        counter+=trans_count;
                        PieSlice slice = new PieSlice();
                        slice.setValue((float) amount);

                        slice.setTitle(Category.expenseCategories[name]);
                        slice.setColor(resources.getColor(Category.getExpenseColorID(name)));
                        pg.addSlice(slice);
                    }

                }
                else {
                    if(boolExpense==0){
                        amountSum+=amount;
                        counter+=trans_count;
                        PieSlice slice = new PieSlice();
                        slice.setValue((float) amount);
                        slice.setTitle(Category.incomeCategories[name]);
                        slice.setColor(resources.getColor(Category.getIncomeColorID((name))));
                        pg.addSlice(slice);
                    }
                }


            } while (c.moveToNext());
            textView.setText(counter+" تراکنش"+"\n مجموع: "+Currency.getStdAmount(amountSum)+" "+ Currency.getCurrencyString());

            textView.setTypeface(TimelineActivity.persianTypeface);
            if(counter==0)
                textView.setText("در بازه زمانی مورد نظر تراکنشی وجود ندارد");
        }
        else
            textView.setText("در بازه زمانی مورد نظر تراکنشی وجود ندارد");

    }

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null)
            return;

        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.UNSPECIFIED);
        int totalHeight = 0;
        View view = null;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            view = listAdapter.getView(i, view, listView);
            if (i == 0)
                view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth, LinearLayout.LayoutParams.WRAP_CONTENT));

            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }
}
