/*
 * 	   Created by Daniel Nadeau
 * 	   daniel.nadeau01@gmail.com
 * 	   danielnadeau.blogspot.com
 * 
 * 	   Licensed to the Apache Software Foundation (ASF) under one
       or more contributor license agreements.  See the NOTICE file
       distributed with this work for additional information
       regarding copyright ownership.  The ASF licenses this file
       to you under the Apache License, Version 2.0 (the
       "License"); you may not use this file except in compliance
       with the License.  You may obtain a copy of the License at

         http://www.apache.org/licenses/LICENSE-2.0

       Unless required by applicable law or agreed to in writing,
       software distributed under the License is distributed on an
       "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
       KIND, either express or implied.  See the License for the
       specific language governing permissions and limitations
       under the License.
 */

package ir.abplus.adanalas.Charts;

import android.animation.Animator;
import android.annotation.TargetApi;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import com.echo.holographlibrary.Bar;
import com.echo.holographlibrary.BarGraph;
import com.echo.holographlibrary.BarGraph.OnBarClickedListener;
import com.echo.holographlibrary.HoloGraphAnimate;
import com.fourmob.datetimepicker.date.PersianCalendar;
import ir.abplus.adanalas.Libraries.*;
import ir.abplus.adanalas.R;
import ir.abplus.adanalas.Timeline.*;
import ir.abplus.adanalas.databaseConnections.LocalDBServices;

import java.util.ArrayList;

public class BarChartFragment extends Fragment {

    BarGraph bg;
    View v ;
    Resources resources ;
    ArrayList<Bar> aBars ;
    BarGraph barGraph;
    TransactoinDatabaseHelper trHelper;
    Cursor c;
    boolean isDetailed=false;
    private PersianCalendar calendar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.bar_chart_fragment, container, false);
        resources = getResources();
        aBars = new ArrayList<Bar>();

        barGraph = (BarGraph) v.findViewById(R.id.bargraph);
        bg = barGraph;
        bg.setPersianTypeface(TimelineActivity.persianTypeface);
        barGraph.setPersianTypeface(TimelineActivity.persianTypeface);

        updateDatabaseCursor(false, -1);
        updateBarChart();


        barGraph.setOnBarClickedListener(new OnBarClickedListener() {

            @Override
            public void onClick(int index) {

                if(index==-1){
//                    System.out.println("@@@@@");
//                    if(isDetailed){
//                    isExpense=true;
//                    printWholeDatabase(false);
//                    updateBarChart();
//                    barGraph.setBars(aBars);
//                    }
                }
                else{
                    if(!isDetailed) {
                        isDetailed=true;
//                        System.out.println(index);
//                        System.out.println(barGraph.getBars().get(index).getValue());
//                        System.out.println("$$$$"+barGraph.getBars().get(index).getDateString());
//                        for(Bar b:barGraph.getBars())
//                            System.out.println("help: "+b.getDateString());
                        updateDatabaseCursor(true, index);
//                        updateBarChart();
                    }
                    else {
                        updateDatabaseCursor(false,-1);
//                        updateBarChart();
//                        deleteBarChart(in);
                    }
//                barGraph.setBars(aBars);
//
//                Toast.makeText(getActivity(),
//                        "Bar " + index + " clicked " + String.valueOf(barGraph.getBars().get(index).getValue()),
//                        Toast.LENGTH_SHORT)
//                        .show();
                }
            }
        });
//        Button animateBarButton = (Button) v.findViewById(R.id.animateBarButton);
//        Button animateInsertBarButton = (Button) v.findViewById(R.id.animateInsertBarButton);
//        Button animateDelteBarButton = (Button) v.findViewById(R.id.animateDeleteBarButton);

        //animate to random values
//        animateBarButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                for (Bar b : barGraph.getBars()) {
//                    b.setGoalValue((float) Math.random() * 1000);
//                    b.setValuePrefix("$");//display the prefix throughout the animation
////                    Log.d("goal val", String.valueOf(b.getGoalValue()));
//                }
//                barGraph.setDuration(1200);//default if unspecified is 300 ms
//                barGraph.setInterpolator(new AccelerateDecelerateInterpolator());//Only use over/undershoot  when not inserting/deleting
//                barGraph.setAnimationListener(getAnimationListener());
//                barGraph.animateToGoalValues();
//
//            }
//        });

        //insert a bar
//        animateInsertBarButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                barGraph.cancelAnimating(); //must clear existing to call onAnimationEndListener cleanup BEFORE adding new bars
//                int newPosition = barGraph.getBars().size() == 0 ? 0 : new Random().nextInt(barGraph.getBars().size());
//                Bar bar = new Bar();
//                bar.setColor(Color.parseColor("#AA0000FF"));
//                bar.setName("Insert bar " + String.valueOf(barGraph.getBars().size()));
//                bar.setValue(0);
//                bar.mAnimateSpecial = HoloGraphAnimate.ANIMATE_INSERT;
//                barGraph.getBars().add(newPosition,bar);
////                for (Bar b : barGraph.getBars()) {
//                bar.setGoalValue((float) Math.random() * 1000);
//                bar.setValuePrefix("$");//display the prefix throughout the animation
////                    Log.d("goal val", String.valueOf(b.getGoalValue()));
////                }
//                barGraph.setDuration(1200);//default if unspecified is 300 ms
//                barGraph.setInterpolator(new AccelerateDecelerateInterpolator());//Don't use over/undershoot interpolator for insert/delete
//                barGraph.setAnimationListener(getAnimationListener());
//                barGraph.animateToGoalValues();
//
//            }
//        });
//
//        //delete a bar
//        animateDelteBarButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                System.out.println("%%%%"+barGraph.getBars().size());
//
//                barGraph.cancelAnimating(); //must clear existing to call onAnimationEndListener cleanup BEFORE adding new bars
//                if (barGraph.getBars().size() == 0) return;
//
//                for (Bar b : barGraph.getBars()) {
//                    b.setGoalValue((float) Math.random() * 1000);
//                    b.setValuePrefix("$");//display the prefix throughout the animation
////                    Log.d("goal val", String.valueOf(b.getGoalValue()));
//                }
//                int newPosition = new Random().nextInt(barGraph.getBars().size());
//                Bar bar = barGraph.getBars().get(newPosition);
//                bar.mAnimateSpecial = HoloGraphAnimate.ANIMATE_DELETE;
//                bar.setGoalValue(0);//animate to 0 then delete
//                barGraph.setDuration(1200);//default if unspecified is 300 ms
//                barGraph.setInterpolator(new AccelerateInterpolator());//Don't use over/undershoot interpolator for insert/delete
//                barGraph.setAnimationListener(getAnimationListener());
//                barGraph.animateToGoalValues();
//            }
//        });
        return v;
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
                    ArrayList<Bar> newBars = new ArrayList<Bar>();
                    //Keep bars that were not deleted
                    for (Bar b : bg.getBars()){
                        if (b.mAnimateSpecial != HoloGraphAnimate.ANIMATE_DELETE){
                            b.mAnimateSpecial = HoloGraphAnimate.ANIMATE_NORMAL;
                            newBars.add(b);
                        }
                    }
                    bg.setBars(newBars);
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            };
        else return null;

    }

    public void updateDatabaseCursor(boolean wantDetail,int clickedBarIndex) {


//        SQLiteDatabase db = trHelper.getReadableDatabase();
        String query="";

        if(wantDetail){

            query = "SELECT " + TransactionsContract.TransactionEntry.COLUMN_NAME_CATEGORY + " as A, " +
                    "SUM(" + TransactionsContract.TransactionEntry.COLUMN_NAME_AMOUNT + ") as B, " +
                    TransactionsContract.TransactionEntry.COLUMN_NAME_IS_EXPENSE+" as C "+
                    "FROM " + TransactionsContract.TransactionEntry.TABLE_NAME ;
            query+=" WHERE "+((ChartActivity)getActivity()).getWhereClause();
            query=query.substring(0,query.indexOf(">="));
            query+=">="+barGraph.getBars().get(clickedBarIndex).getDateString();
            query=query+" GROUP BY A, C" + " ORDER BY " + TransactionsContract.TransactionEntry.COLUMN_NAME_AMOUNT + " DESC";

        }
        else {
            isDetailed=false;
            query = "SELECT SUBSTR( "+TransactionsContract.TransactionEntry.COLUMN_NAME_DATE_TIME +" , ";
            switch (((ChartActivity)getActivity()).accountMenuAdapter.getRadioSelected()) {
                case ChartActivity.DAILY:
                    query=query+"1,8)";
                    break;
                case ChartActivity.WEEKLY:
                    query=query+"1,8)";
                    break;
                case ChartActivity.MONTHLY:
                    query=query+"1,6)";
                    break;
                case ChartActivity.YEARLY:
                    query=query+"1,4)";
                    break;
            }
            query=query+" as A, SUM(" + TransactionsContract.TransactionEntry.COLUMN_NAME_AMOUNT +") as B, "+TransactionsContract.TransactionEntry.COLUMN_NAME_IS_EXPENSE+" as C FROM " + TransactionsContract.TransactionEntry.TABLE_NAME +
                    " WHERE " + ((ChartActivity)getActivity()).getWhereClause();

            query += " GROUP BY A, C"  +" ORDER BY " + TransactionsContract.TransactionEntry.COLUMN_NAME_DATE_TIME + " ASC";

            if(((ChartActivity)getActivity()).accountMenuAdapter.getRadioSelected()==ChartActivity.WEEKLY){
                        query="SELECT "+TransactionsContract.TransactionEntry.COLUMN_NAME_DATE_TIME+" as A, SUM("
                        + TransactionsContract.TransactionEntry.COLUMN_NAME_AMOUNT +") as B FROM "
                        + TransactionsContract.TransactionEntry.TABLE_NAME
                        +" WHERE " + ((ChartActivity)getActivity()).getWhereClause();
                if(((ChartActivity)getActivity()).isExpense){
                        query+=" AND "+TransactionsContract.TransactionEntry.COLUMN_NAME_IS_EXPENSE +" =1";}
                else { query+=" AND "+TransactionsContract.TransactionEntry.COLUMN_NAME_IS_EXPENSE +" =0";}
                        query+=" GROUP BY A"  +" ORDER BY " + TransactionsContract.TransactionEntry.COLUMN_NAME_DATE_TIME + " ASC";
            }
        }

        System.out.println(query);
//        c = db.rawQuery(query, null);
         c= LocalDBServices.getTransactionsForBarChart(query);
        if(c.getCount() == 0)
            System.out.println("empty");

        updateBarChart();
    }




    private void addBarChart(Bar bar){

        Log.e("tara",bar.getValue()+" "+Currency.getCurrencyString());
        barGraph.cancelAnimating();
        Bar bar2=new Bar();
        bar2.setColor(bar.getColor());
        bar2.setValue(bar.getValue()/ 2);
        bar2.setDateString(bar.getDateString());
        bar2.setName(bar.getName());

        bar2.mAnimateSpecial = HoloGraphAnimate.ANIMATE_INSERT;
        barGraph.getBars().add(0,bar2);
        bar2.setGoalValue(bar.getValue());
//        bar2.setValueString(Currency.getStdAmount(bar.getValue()));
//        bar2.setValueString("asdlj;");
        barGraph.setDuration(500);//default if unspecified is 300 ms
        barGraph.setInterpolator(new AccelerateDecelerateInterpolator());//Don't use over/undershoot interpolator for insert/delete
        barGraph.setAnimationListener(getAnimationListener());
        barGraph.animateToGoalValues();

    }

    private void deleteBarChart(int index){
//        Log.e("S","bar "+index+" deleted");
        barGraph.cancelAnimating();
        Bar bar = barGraph.getBars().get(index);
        bar.mAnimateSpecial = HoloGraphAnimate.ANIMATE_DELETE;
        bar.setGoalValue(0);//animate to 0 then delete
        barGraph.setDuration(1200);//default if unspecified is 300 ms
        barGraph.setInterpolator(new AccelerateInterpolator());//Don't use over/undershoot interpolator for insert/delete
        barGraph.setAnimationListener(getAnimationListener());
        barGraph.animateToGoalValues();


    }
    private void updateBarChart(){

        boolean isExpense=((ChartActivity)getActivity()).isExpense;

//        System.out.println("&&&&"+barGraph.getBars().size());
        for(int i=barGraph.getBars().size()-1;i>=0;i--) {
            deleteBarChart(i);

        }
//        deleteBarChart();

//        Log.e("w","barsize : "+barGraph.getBars().size());
//
//        System.out.println("hi jack!!");
//        try {
//            Thread.sleep(300);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        aBars= new ArrayList<Bar>();
        int week_counter=1;
        double week_amount=0;

        if(isDetailed){

            if(c.getCount() != 0) {
                c.moveToFirst();
                do {
                    int name = c.getInt(c.getColumnIndexOrThrow("A"));
                    double amount = c.getDouble(c.getColumnIndexOrThrow("B"));
                    int boolExpense = c.getInt(c.getColumnIndexOrThrow("C"));

                    if(isExpense){
                        if(boolExpense==1){
                            Bar bar = new Bar();
                            bar.setValue( Float.parseFloat(Currency.getStdAmountWithoutSeparation(amount)));
                            bar.setValueString(Currency.getStdAmount(amount));
                            bar.setSelectedColor(resources.getColor(R.color.red));
                            bar.setColor(resources.getColor(Category.getExpenseColorID(name)));
                            bar.setName(Category.expenseCategories[name]);
                            aBars.add(bar);
                        }

                    }
                    else {
                        if(boolExpense==0){
                            Log.i("W", "here is a bug amount: " + amount);
                            Bar bar = new Bar();
                            bar.setValue(Float.parseFloat(Currency.getStdAmountWithoutSeparation(amount)));
                            bar.setValueString(Currency.getStdAmount(amount));
                            bar.setSelectedColor(resources.getColor(R.color.red));
                            bar.setColor(resources.getColor(Category.getIncomeColorID((name))));
                            bar.setName(Category.incomeCategories[name]);
                            aBars.add(bar);
                        }
                    }


                } while (c.moveToNext());

            }
        }
        else {
            int startDate=Integer.parseInt(ChartActivity.startDate.substring(0,8));
            int endDate=Integer.parseInt(ChartActivity.endDate.substring(0,8));

            String startDateString =startDate+"";

            if(c.getCount() != 0) {
                c.moveToFirst();
                do {
                    String name = c.getString(c.getColumnIndexOrThrow("A"));
                    double amount = c.getDouble(c.getColumnIndexOrThrow("B"));

                    int boolExpense;
                    if(((ChartActivity)getActivity()).accountMenuAdapter.getRadioSelected()==ChartActivity.WEEKLY){
                        if (isExpense)
                            boolExpense=1;
                        else boolExpense=0;}
                    else
                    boolExpense= c.getInt(c.getColumnIndexOrThrow("C"));

//                    System.out.println(name+"   "+amount);
                    if(isExpense){
                        if(boolExpense==1){
                            if(((ChartActivity)getActivity()).accountMenuAdapter.getRadioSelected()==ChartActivity.WEEKLY){
                                int transDate=Integer.parseInt(name.substring(0,8));
                                calendar=new PersianCalendar();
                                calendar.set(Integer.parseInt(startDateString.substring(0,4)),Integer.parseInt(startDateString.substring(4,6)),Integer.parseInt(startDateString.substring(6,8)));
                                calendar.add(PersianCalendar.DATE, week_counter*7);
                                String  p= new PersianDate((short)calendar.get(PersianCalendar.DATE), (short)calendar.get(PersianCalendar.MONTH), (short)calendar.get(PersianCalendar.YEAR), "").getSTDString();
                                if(transDate<=Integer.parseInt(p)){
                                    week_amount+=amount;
                                }
                                else  {
                                    Bar bar = new Bar();
                                    bar.setValue(Float.parseFloat(Currency.getStdAmountWithoutSeparation(week_amount)));
                                    bar.setValueString(Currency.getStdAmount(amount));
                                    bar.setSelectedColor(resources.getColor(R.color.red));
                                    bar.setColor(resources.getColor(R.color.bar_chart));
                                    bar.setName( "هفته"+week_counter);

                                    calendar.set(Integer.parseInt(p.substring(0,4)),Integer.parseInt(p.substring(4,6)),Integer.parseInt(p.substring(6,8)));
                                    calendar.add(PersianCalendar.DATE, -7);
                                    String tmp= new PersianDate((short)calendar.get(PersianCalendar.DATE), (short)calendar.get(PersianCalendar.MONTH), (short)calendar.get(PersianCalendar.YEAR), "").getSTDString();
                                    String tmp2=p;
                                    while (tmp.length()<12){
                                        tmp+="0";
                                        tmp2+="9";}
                                    bar.setDateString(tmp+" and "+ TransactionsContract.TransactionEntry.COLUMN_NAME_DATE_TIME+"<="+tmp2);
                                    aBars.add(bar);
                                    week_counter++;
                                    week_amount=0;
                                    c.moveToPrevious();
                                }
                                System.out.println("size : "+c.getCount());
                                if(c.isLast()){
                                    Bar bar = new Bar();
                                    bar.setValue(Float.parseFloat(Currency.getStdAmountWithoutSeparation(amount)));
                                    bar.setValueString(Currency.getStdAmount(amount));
                                    bar.setSelectedColor(resources.getColor(R.color.red));
                                    bar.setColor(resources.getColor(R.color.bar_chart));
                                    bar.setName(week_counter + "هفته");

                                    calendar.set(Integer.parseInt(p.substring(0,4)),Integer.parseInt(p.substring(4,6)),Integer.parseInt(p.substring(6,8)));
                                    calendar.add(PersianCalendar.DATE, -7);
                                    String tmp= new PersianDate((short)calendar.get(PersianCalendar.DATE), (short)calendar.get(PersianCalendar.MONTH), (short)calendar.get(PersianCalendar.YEAR), "").getSTDString();
                                    String tmp2=name;
                                    while (tmp.length()<12){
                                        tmp+="0";
                                        tmp2+="9";}
                                    bar.setDateString(tmp + " and " + TransactionsContract.TransactionEntry.COLUMN_NAME_DATE_TIME + "<=" + tmp2);
                                    aBars.add(bar);
                                    week_amount=0;
                                }
                            }

                            else {
                                Bar bar = new Bar();
                                bar.setValue(Float.parseFloat(Currency.getStdAmountWithoutSeparation(amount)));
                                bar.setValueString(Currency.getStdAmount(amount));
                                bar.setSelectedColor(resources.getColor(R.color.red));
                                bar.setColor(resources.getColor(R.color.bar_chart));
                                switch (((ChartActivity) getActivity()).accountMenuAdapter.getRadioSelected()) {
                                    case ChartActivity.DAILY:
                                        bar.setName(name.substring(0, 4) + "/" + name.substring(4, 6) + "/" + name.substring(6, 8));
                                        break;
                                    case ChartActivity.MONTHLY:
                                        bar.setName(name.substring(0, 4) + "/" + name.substring(4, 6));
                                        break;
                                    case ChartActivity.YEARLY:
                                        bar.setName(name + "");
                                        break;
                                }
                                String tmp=name;
                                String tmp2=name;
                                while (tmp.length()<12){
                                    tmp+="0";
                                    tmp2+="9";}
                                bar.setDateString(tmp+" and "+ TransactionsContract.TransactionEntry.COLUMN_NAME_DATE_TIME+"<="+tmp2);
//                            Log.i("masih",bar.getDateString());
                                aBars.add(bar);
                            }
                        }

                    }
                    else {
                        if(boolExpense==0){
                            if(((ChartActivity)getActivity()).accountMenuAdapter.getRadioSelected()==ChartActivity.WEEKLY){
                                int transDate=Integer.parseInt(name.substring(0,8));
                                calendar=new PersianCalendar();
                                calendar.set(Integer.parseInt(startDateString.substring(0,4)),Integer.parseInt(startDateString.substring(4,6)),Integer.parseInt(startDateString.substring(6,8)));
                                calendar.add(PersianCalendar.DATE, week_counter*7);
                                String  p= new PersianDate((short)calendar.get(PersianCalendar.DATE), (short)calendar.get(PersianCalendar.MONTH), (short)calendar.get(PersianCalendar.YEAR), "").getSTDString();
                                if(transDate<=Integer.parseInt(p)){
                                    week_amount+=amount;
                                    Log.i("debug0",transDate+"  "+p+"  "+week_amount);
                                }
                                else  {
                                    Bar bar = new Bar();
                                    bar.setValue(Float.parseFloat(Currency.getStdAmountWithoutSeparation(amount)));
                                    bar.setValueString(Currency.getStdAmount(amount));
                                    bar.setSelectedColor(resources.getColor(R.color.red));
                                    bar.setColor(resources.getColor(R.color.green));
                                    bar.setName( "هفته"+week_counter);
                                    Log.i("debug1",transDate+"  "+p+"  "+week_amount);
                                    calendar.set(Integer.parseInt(p.substring(0,4)),Integer.parseInt(p.substring(4,6)),Integer.parseInt(p.substring(6,8)));
                                    calendar.add(PersianCalendar.DATE, -7);
                                    String tmp= new PersianDate((short)calendar.get(PersianCalendar.DATE), (short)calendar.get(PersianCalendar.MONTH), (short)calendar.get(PersianCalendar.YEAR), "").getSTDString();
                                    String tmp2=p;
                                    while (tmp.length()<12){
                                        tmp+="0";
                                        tmp2+="9";}
                                    bar.setDateString(tmp+" and "+ TransactionsContract.TransactionEntry.COLUMN_NAME_DATE_TIME+"<="+tmp2);
                                    aBars.add(bar);
                                    week_counter++;
                                    week_amount=0;
                                    c.moveToPrevious();
                                }
                                System.out.println("size : "+c.getCount());
                                if(c.isLast()){
                                    Bar bar = new Bar();
                                    bar.setValue(Float.parseFloat(Currency.getStdAmountWithoutSeparation(amount)));
                                    bar.setValueString(Currency.getStdAmount(amount));
                                    bar.setSelectedColor(resources.getColor(R.color.red));
                                    bar.setColor(resources.getColor(R.color.green));
                                    bar.setName(week_counter + "هفته");

                                    calendar.set(Integer.parseInt(p.substring(0,4)),Integer.parseInt(p.substring(4,6)),Integer.parseInt(p.substring(6,8)));
                                    calendar.add(PersianCalendar.DATE, -7);
                                    String tmp= new PersianDate((short)calendar.get(PersianCalendar.DATE), (short)calendar.get(PersianCalendar.MONTH), (short)calendar.get(PersianCalendar.YEAR), "").getSTDString();
                                    String tmp2=name;
                                    while (tmp.length()<12){
                                        tmp+="0";
                                        tmp2+="9";}
                                    bar.setDateString(tmp + " and " + TransactionsContract.TransactionEntry.COLUMN_NAME_DATE_TIME + "<=" + tmp2);
                                    Log.i("debug2", week_amount + " " + week_counter);
                                    aBars.add(bar);
                                    week_amount=0;
                                }
                            }
                            else {
                                Bar bar = new Bar();
                                bar.setValue(Float.parseFloat(Currency.getStdAmountWithoutSeparation(amount)));
                                bar.setValueString(Currency.getStdAmount(amount));
                                bar.setSelectedColor(resources.getColor(R.color.red));
                                bar.setColor(resources.getColor(R.color.green));

                                switch (((ChartActivity) getActivity()).accountMenuAdapter.getRadioSelected()) {
                                        case ChartActivity.DAILY:
                                            bar.setName(name.substring(0, 4) + "/" + name.substring(4, 6) + "/" + name.substring(6, 8));
                                            break;
                                        case ChartActivity.MONTHLY:
                                            bar.setName(name.substring(0, 4) + "/" + name.substring(4, 6));
                                            break;
                                        case ChartActivity.YEARLY:
                                            bar.setName(name + "");
                                            break;
                                    }
                                String tmp=name;
                                String tmp2=name;
                                while (tmp.length()<12){
                                    tmp+="0";
                                    tmp2+="9";}
                                bar.setDateString(tmp+" and "+ TransactionsContract.TransactionEntry.COLUMN_NAME_DATE_TIME+"<="+tmp2);
//                            Log.i("masih",bar.getDateString());
                                aBars.add(bar);
                            }
                        }
                    }

                } while (c.moveToNext());

            }

        }

        for(Bar b :aBars){
            addBarChart(b);
        }
//        barGraph.cancelAnimating(); //must clear existing to call onAnimationEndListener cleanup BEFORE adding new bars
////        int newPosition = barGraph.getBars().size() == 0 ? 0 : new Random().nextInt(barGraph.getBars().size());
//        Bar bar = new Bar();
//        bar.setColor(Color.parseColor("#AA0000FF"));
//        bar.setName("Insert bar " + String.valueOf(barGraph.getBars().size()));
//        bar.setValue(0);
//        bar.mAnimateSpecial = HoloGraphAnimate.ANIMATE_INSERT;
//        barGraph.getBars().add(1,bar);
////        for (Bar b : barGraph.getBars()) {
////            b.setGoalValue((float) Math.random() * 1000);
////            b.setValuePrefix("$");//display the prefix throughout the animation
////            Log.d("goal val", String.valueOf(b.getGoalValue()));
////        }
//        barGraph.setDuration(1200);//default if unspecified is 300 ms
//        barGraph.setInterpolator(new AccelerateDecelerateInterpolator());//Don't use over/undershoot interpolator for insert/delete
//        barGraph.setAnimationListener(getAnimationListener());
//        barGraph.animateToGoalValues();



    }

}
