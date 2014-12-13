package ir.abplus.adanalas.Uncategoried;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import ir.abplus.adanalas.Charts.ChartActivity;
import ir.abplus.adanalas.R;
import ir.abplus.adanalas.Setting.SettingActivity;

/**
 * Created by Keyvan Sasani on 9/3/2014.
 */
public class UncategoriedActivity extends Activity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.uncategoried_activity);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_uncategoried, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
//        System.out.println(item.toString());
        Intent intent = null;
        boolean doNothig=false;
        if(item.toString().equals(getResources().getString(R.string.action_timeline))){
//            intent=new Intent(UncategoriedActivity.this, UncategoriedActivity.class);
        }
        else if(item.toString().equals(getResources().getString(R.string.action_settings))){
            intent=new Intent(UncategoriedActivity.this, SettingActivity.class);
        }
        else if(item.toString().equals(getResources().getString(R.string.action_charts))){
            intent=new Intent(UncategoriedActivity.this, ChartActivity.class);
        }
        else if(item.toString().equals(getResources().getString(R.string.action_uncategorized))){
            doNothig=true;
        }
        if(!doNothig){
            if(intent!=null){
                finish();
                startActivity(intent);

            }
            else finish();}
        overridePendingTransition(0,0);
        return super.onOptionsItemSelected(item);
    }
}
