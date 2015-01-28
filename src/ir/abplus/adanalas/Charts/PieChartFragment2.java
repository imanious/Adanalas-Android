package ir.abplus.adanalas.Charts;

import android.animation.ValueAnimator;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.*;
import com.echo.holographlibrary.PieGraph;
import com.echo.holographlibrary.PieGraph.OnSliceClickedListener;
import com.echo.holographlibrary.PieSlice;
import ir.abplus.adanalas.Libraries.Category;
import ir.abplus.adanalas.Libraries.Currency;
import ir.abplus.adanalas.Timeline.TimelineActivity;
import ir.abplus.adanalas.databaseConnections.LocalDBServices;

import java.util.ArrayList;

public class PieChartFragment2 extends Fragment {

    Cursor c;
//    View v ;
    //    View listLayout ;
    PieGraph pg ;
    TextView textView;

    ListView list;
    ChartListCustomAdapter adapter;
    public  ChartActivity CustomListView = null;
    public ArrayList<ChartListModel> CustomListViewValuesArr = new ArrayList<ChartListModel>();
    private boolean isExpense;
    public ScrollView scrollView;
    RelativeLayout relativeLayout;
    LinearLayout ll;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        scrollView=new ScrollView(getActivity());
        relativeLayout=new RelativeLayout(getActivity());
        pg=new PieGraph(getActivity());
        textView=new TextView(getActivity());
        ll=new LinearLayout(getActivity());
        ll.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT));
        ll.setOrientation(LinearLayout.VERTICAL);


//        v=View.inflate(getActivity(), R.layout.pie_chart_fragment, null);
//        resources = getResources();
//        pg = (PieGraph) v.findViewById(R.id.piegraph);
//        textView=(TextView)v.findViewById(R.id.oncentertextView);
//        scrollView = (ScrollView)v.findViewById(R.id.scrollV);


        pg.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));

        CustomListView =(ChartActivity) getActivity();
//        list= (ListView)v.findViewById(R.id.list);  // List defined in XML ( See Below )
        list=new ListView(getActivity());
        relativeLayout.addView(pg);
        relativeLayout.addView(textView);
        ll.addView(relativeLayout);
        ll.addView(list);

//        scrollView.addView(pg);
        scrollView.addView(ll);
        adapter=new ChartListCustomAdapter( CustomListView, CustomListViewValuesArr,getActivity().getResources() );

        UpdateDatabaseCursor();
        Log.e("debug","scroll"+scrollView.getScrollY());

//        updatePieChart();

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


    scrollView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                scrollView.post(new Runnable() {
                    public void run() {
                        scrollView.fullScroll(View.FOCUS_UP);
                    }
                });
            }
        });


        Log.e("debug", "scroll" + scrollView.getScrollY());

        return scrollView;
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
                long amount = c.getLong(c.getColumnIndexOrThrow("B"));
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


    public void UpdateDatabaseCursor(){
        Log.e("debug","scroll"+scrollView.getScrollY());
        c= LocalDBServices.getTransactionsFromDBGroupedBYCategory(((ChartActivity)getActivity()).getWhereClause());
        if(c.getCount() == 0)
            System.out.println("empty");
        isExpense=((ChartActivity)getActivity()).isExpense;
        updatePieChart();
        setListData();
        c.close();
        setListViewHeightBasedOnChildren(list);
//        scrollView.scrollTo(0, 0);
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
                        slice.setColor(getResources().getColor(Category.getExpenseColorID(name)));
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
                        slice.setColor(getResources().getColor(Category.getIncomeColorID((name))));
                        pg.addSlice(slice);
                    }
                }


            } while (c.moveToNext());
            textView.setText(counter+" تراکنش"+"\n مجموع: "+Currency.getStdAmount((long) amountSum)+" "+ Currency.getCurrencyString());

            textView.setTypeface(TimelineActivity.persianTypeface);
            if(counter==0)
                textView.setText("در بازه زمانی مورد نظر تراکنشی وجود ندارد");
        }
        else
            textView.setText("در بازه زمانی مورد نظر تراکنشی وجود ندارد");

    }

    public void setListViewHeightBasedOnChildren(ListView listView) {
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
        Log.e("debug","scroll"+scrollView.getScrollY());
    }
}
