package ir.abplus.adanalas.Setting;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import com.fourmob.datetimepicker.date.PersianCalendar;
import ir.abplus.adanalas.Charts.ChartActivity;
import ir.abplus.adanalas.Libraries.*;
import ir.abplus.adanalas.R;
import ir.abplus.adanalas.SyncCloud.JsonParser;
import ir.abplus.adanalas.Timeline.TimelineItem;
import ir.abplus.adanalas.Timeline.TimelineItem2;
import ir.abplus.adanalas.Uncategoried.UncategoriedActivity;
import ir.abplus.adanalas.databaseConnections.LocalDBServices;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Keyvan Sasani on 9/3/2014.
 */
public class SettingActivity extends Activity {
    Button commitButton;
    Button generateButton;
    Button accountButton;
    Button tomanButton;
    Button toasandButton;
    Button rialButton;
    Button jsontodbButton;
    Spinner accountSpinner;
    TransactoinDatabaseHelper trHelper;
    public static String defaultAccount;
    static ArrayList<String> accountsList;
    ArrayList<TimelineItem> listItems=new ArrayList<TimelineItem>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_activity);
        commitButton=(Button)findViewById(R.id.commit_button);
        generateButton=(Button)findViewById(R.id.generate_button);
        accountButton=(Button)findViewById(R.id.test_account_button);
        accountSpinner=(Spinner) findViewById(R.id.account_spinner);
        tomanButton=(Button)findViewById(R.id.toman_button);
        toasandButton=(Button)findViewById(R.id.tousandtoman_button);
        rialButton=(Button)findViewById(R.id.rial_button);
        jsontodbButton=(Button)findViewById(R.id.urltobutton);



        addAccountsToList();
        ArrayAdapter dataAdapter=new ArrayAdapter(this,android.R.layout.simple_spinner_item,accountsList);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        accountSpinner.setAdapter(dataAdapter);

        accountSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                defaultAccount=((String)accountSpinner.getSelectedItem());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });




        commitButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View arg0)
            {
                try {
                    commitData();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });
//
        generateButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View arg0)
            {
                int n = (int) (Math.random()*10) + 1;
                PersianDate date = new PersianDate((short)(Math.random()*29+1), (short)(Math.random()*4), (short)(1393+Math.random()*2), PersianCalendar.weekdayFullNames[(int)(1+Math.random()*6)]);
                for(int i = 0; i < n; i++)
                {
                    Random r = new Random();
                    int Low = 100;
                    int High = 1000;
                    int R = r.nextInt(High-Low) + Low;

                    R*=100;
//                    double amount=Double.parseDouble(String.format("%." + 2 + "f", Math.random()*100));
                    double amount=(double)R;
                    Time time = new Time((short)(Math.random()*24), (short)(Math.random()*60));
                    TimelineItem t = new TimelineItem("0", Math.random()<0.2? false: true, amount, date, time, (int)(Math.random()*11),null,false,"",(String)accountSpinner.getSelectedItem());
                    listItems.add(t);
                }
            }
        });

        accountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addTestAccountToDb();
            }
        });

        rialButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Currency.setCurrency(Currency.RIAL);
            }
        });

        tomanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Currency.setCurrency(Currency.TOMAN);
            }
        });

        toasandButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Currency.setCurrency(Currency.THOUSAND_TOMAN);
            }
        });

        jsontodbButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url="";
                String transExpenseIn="[{\"amount\":1234,\"category\":\"clothing\",\"date\":\"13931003101700\",\"deposit\":\"0017494550*نقدی 4\",\"description\":null,\"detail\":\"دستی\",\"handy\":true,\"hidden\":false,\"type\":\"d\",\"tags\":[],\"id\":\"9afe9143-b17f-4833-9aef-87ca8c36114b\"},{\"amount\":500,\"category\":\"food\",\"date\":\"13931001105000\",\"deposit\":\"0017494550*نقدی\",\"description\":\"شرح ندارد\",\"detail\":\"دستی\",\"handy\":true,\"hidden\":false,\"type\":\"d\",\"target\":null,\"guild\":null,\"store\":null,\"rule\":null,\"parentId\":\"bb1858aa-6d6d-4912-815d-af64510a47f3\",\"tags\":[\"وندینگ ماشین دانشگاه\"],\"id\":\"60a46f11-2308-49b9-b793-e5c0b2b059c3\"},{\"amount\":500,\"category\":\"food\",\"date\":\"13931001105000\",\"deposit\":\"0017494550*نقدی\",\"description\":\"شرح ندارد\",\"detail\":\"دستی\",\"handy\":true,\"hidden\":false,\"type\":\"d\",\"target\":null,\"guild\":null,\"store\":null,\"rule\":null,\"parentId\":\"bb1858aa-6d6d-4912-815d-af64510a47f3\",\"tags\":[\"وندینگ ماشین دانشگاه\"],\"id\":\"4c80d194-715b-4d22-bb46-c3a768586647\"},{\"acquirer\":\"581672141\",\"amount\":12000,\"category\":\"food\",\"date\":\"13930930143855\",\"deposit\":\"0201434050006\",\"detail\":\"خرید\",\"hidden\":false,\"merchant\":\"000000000623039\",\"operation\":\"purchase\",\"rule\":\"وندینگ ماشین دانشگاه\",\"terminal\":\"00623860\",\"type\":\"d\",\"tags\":[\"وندینگ ماشین دانشگاه\"],\"id\":\"375ceb64-a4fc-4b6a-938d-c0cf7d01d632\"},{\"acquirer\":\"581672141\",\"amount\":10000,\"category\":\"food\",\"date\":\"13930930143822\",\"deposit\":\"0201434050006\",\"detail\":\"خرید\",\"hidden\":false,\"merchant\":\"000000000623039\",\"operation\":\"purchase\",\"rule\":\"وندینگ ماشین دانشگاه\",\"terminal\":\"00623860\",\"type\":\"d\",\"tags\":[\"وندینگ ماشین دانشگاه\"],\"id\":\"3325cf6b-e861-452e-a207-ce948a0daa69\"},{\"acquirer\":\"581672141\",\"amount\":40000,\"category\":\"food\",\"date\":\"13930930141414\",\"deposit\":\"0201434050006\",\"detail\":\"خرید\",\"hidden\":false,\"merchant\":\"000000000623039\",\"operation\":\"purchase\",\"rule\":\"وندینگ ماشین دانشگاه\",\"terminal\":\"00623860\",\"type\":\"d\",\"tags\":[\"وندینگ ماشین دانشگاه\"],\"id\":\"47eceebb-1cfb-41d9-912a-99f2d97c4086\"},{\"acquirer\":\"581672061\",\"amount\":784000,\"category\":\"present\",\"date\":\"13930929185034\",\"deposit\":\"0201434050006\",\"detail\":\"خرید\",\"hidden\":false,\"merchant\":\"000000251461991\",\"operation\":\"purchase\",\"terminal\":\"33394329\",\"type\":\"d\",\"tags\":[\"باشی زاده\"],\"id\":\"fc3c4189-2a23-43ed-9c56-9060281b2e50\"},{\"acquirer\":\"581672031\",\"amount\":530000,\"category\":\"food\",\"date\":\"13930929125654\",\"deposit\":\"0201434050006\",\"detail\":\"خرید\",\"hidden\":false,\"merchant\":\"505064\",\"operation\":\"purchase\",\"terminal\":\"00881602\",\"type\":\"d\",\"tags\":[],\"id\":\"50a2cc8a-67e0-4491-8521-80de62c6321f\"},{\"acquirer\":\"581672031\",\"amount\":310000,\"category\":\"food\",\"date\":\"13930927174106\",\"deposit\":\"0201434050006\",\"detail\":\"خرید\",\"hidden\":false,\"merchant\":\"354368\",\"operation\":\"purchase\",\"rule\":\"سیب زمینی \",\"terminal\":\"00716666\",\"type\":\"d\",\"tags\":[\"سیب زمینی\"],\"id\":\"c8356927-7344-4d6c-a6f6-5592a8382f67\"},{\"amount\":500000,\"category\":\"expense\",\"date\":\"13930926201422\",\"deposit\":\"0201434050006\",\"detail\":\"برداشت از خودپرداز\",\"hidden\":false,\"operation\":\"cash\",\"type\":\"d\",\"tags\":[],\"id\":\"e52ad554-023e-4181-bd7f-fe9a7c2699ab\"},{\"acquirer\":\"581672041\",\"amount\":445000,\"category\":\"food\",\"date\":\"13930924133338\",\"deposit\":\"0201434050006\",\"detail\":\"خرید\",\"hidden\":false,\"merchant\":\"000000010131680\",\"operation\":\"purchase\",\"rule\":\"کبابی طالقانی\",\"terminal\":\"10131680\",\"type\":\"d\",\"tags\":[\"کبابی طالقانی\"],\"id\":\"dea3a0f7-b52f-4119-8ff2-39719862ccd7\"},{\"acquirer\":\"581672061\",\"amount\":688000,\"category\":\"food\",\"date\":\"13930919161236\",\"deposit\":\"0201434050006\",\"detail\":\"خرید\",\"hidden\":false,\"merchant\":\"000000251830933\",\"operation\":\"purchase\",\"rule\":\"سیب زمینی\",\"terminal\":\"33976362\",\"type\":\"d\",\"tags\":[\"سیب زمینی\"],\"id\":\"d776b897-ea27-4819-9bd6-6fb57f02d6d4\"},{\"acquirer\":\"581672091\",\"amount\":540000,\"category\":\"present\",\"date\":\"13930918171114\",\"deposit\":\"0201434050006\",\"detail\":\"خرید\",\"hidden\":false,\"merchant\":\"000000021147717\",\"operation\":\"purchase\",\"rule\":\"سوییت بلیس\",\"terminal\":\"21158227\",\"type\":\"d\",\"tags\":[\"کیک\",\"سوییت بلیس\"],\"id\":\"6a68b548-50ce-41ef-a6be-cb92847ecf76\"},{\"amount\":2100000,\"category\":\"present\",\"date\":\"13930918093216\",\"deposit\":\"0201434050006\",\"detail\":\"برداشت از خودپرداز\",\"hidden\":false,\"operation\":\"cash\",\"type\":\"d\",\"tags\":[\"کارت هدیه\",\"نوژن\"],\"id\":\"91d96aee-2960-4e65-b35d-9fa357c3aae3\"},{\"amount\":525000,\"category\":\"commitment\",\"date\":\"13930918013308\",\"deposit\":\"0201434050006\",\"description\":\"5022291028962734 10000028 6\",\"detail\":\"انتقال از کارت + کارمزد\",\"hidden\":false,\"operation\":\"transfer\",\"rule\":\"محمد قریشی\",\"target\":\"5022291028962734\",\"type\":\"d\",\"tags\":[\"محمد قریشی\"],\"id\":\"c89e3f74-9e3f-452d-9aa8-6e8cbb029d39\"},{\"acquirer\":\"581672141\",\"amount\":30000,\"category\":\"food\",\"date\":\"13930917164256\",\"deposit\":\"0201434050006\",\"detail\":\"خرید\",\"hidden\":false,\"merchant\":\"000000000623039\",\"operation\":\"purchase\",\"rule\":\"وندینگ ماشین دانشگاه\",\"terminal\":\"00623860\",\"type\":\"d\",\"tags\":[\"وندینگ ماشین دانشگاه\"],\"id\":\"bcba2622-2a8f-4894-8079-24afad62748f\"},{\"acquirer\":\"581672061\",\"amount\":220000,\"category\":\"food\",\"date\":\"13930917140342\",\"deposit\":\"0201434050006\",\"detail\":\"خرید\",\"hidden\":false,\"merchant\":\"000000251830933\",\"operation\":\"purchase\",\"rule\":\"سیب زمینی\",\"terminal\":\"33485528\",\"type\":\"d\",\"tags\":[\"سیب زمینی\"],\"id\":\"2adec174-5754-4e37-b7eb-086971a56264\"},{\"amount\":300000,\"category\":\"personal\",\"date\":\"13930917091612\",\"deposit\":\"0201434050006\",\"detail\":\"برداشت از خودپرداز\",\"hidden\":false,\"operation\":\"cash\",\"type\":\"d\",\"tags\":[],\"id\":\"7e943ad5-746c-4bf9-a7b0-9d150f5031e1\"},{\"acquirer\":\"581672141\",\"amount\":40000,\"category\":\"food\",\"date\":\"13930916123713\",\"deposit\":\"0201434050006\",\"detail\":\"خرید\",\"hidden\":false,\"merchant\":\"000000000623039\",\"operation\":\"purchase\",\"rule\":\"وندینگ ماشین دانشگاه\",\"terminal\":\"00623860\",\"type\":\"d\",\"tags\":[\"وندینگ ماشین دانشگاه\"],\"id\":\"598c6c07-4394-4605-82ee-8c04f9bd338e\"},{\"acquirer\":\"581672031\",\"amount\":165000,\"category\":\"food\",\"date\":\"13930915124519\",\"deposit\":\"0201434050006\",\"detail\":\"خرید\",\"hidden\":false,\"merchant\":\"354368\",\"operation\":\"purchase\",\"rule\":\"سیب زمینی \",\"terminal\":\"00716666\",\"type\":\"d\",\"tags\":[\"سیب زمینی\"],\"id\":\"7f78ef07-ecfc-457a-9219-6893e8a6b690\"},{\"amount\":420000,\"billNumber\":\"000002888981109056\",\"category\":\"bill\",\"date\":\"13930914172805\",\"deposit\":\"0201434050006\",\"description\":\"2888981109056/42032830\",\"detail\":\"پرداخت قبض موبایل\",\"hidden\":false,\"operation\":\"bill\",\"type\":\"d\",\"tags\":[\"موبایل\"],\"id\":\"4a859cc6-4bfc-4f7d-ba33-d32744d7cb2e\"},{\"acquirer\":\"581672041\",\"amount\":194400,\"category\":\"bill\",\"date\":\"13930913235250\",\"deposit\":\"0201434050006\",\"detail\":\"خرید اینترنتی\",\"hidden\":false,\"merchant\":\"000000010000596\",\"operation\":\"purchase\",\"rule\":\"َشارژ اینترنت پارس آنلاین\",\"store\":\"پارس آنلاین\",\"terminal\":\"10000596\",\"type\":\"d\",\"tags\":[\"شارژ اینترنت\",\"پارس آنلاین\"],\"id\":\"e3d6954d-9358-4d1c-a94d-93969c016bab\"},{\"acquirer\":\"581672141\",\"amount\":10000,\"category\":\"food\",\"date\":\"13930913183328\",\"deposit\":\"0201434050006\",\"detail\":\"خرید\",\"hidden\":false,\"merchant\":\"000000000623039\",\"operation\":\"purchase\",\"rule\":\"وندینگ ماشین دانشگاه\",\"terminal\":\"00623860\",\"type\":\"d\",\"tags\":[\"وندینگ ماشین دانشگاه\"],\"id\":\"7395ee10-4d5c-4d83-a487-66a436c09d14\"},{\"acquirer\":\"581672141\",\"amount\":10000,\"category\":\"food\",\"date\":\"13930913183257\",\"deposit\":\"0201434050006\",\"detail\":\"خرید\",\"hidden\":false,\"merchant\":\"000000000623039\",\"operation\":\"purchase\",\"terminal\":\"00623860\",\"type\":\"d\",\"tags\":[\"وندینگ ماشین دانشگاه\"],\"id\":\"f93c16cd-1941-4c33-b68a-53bb114c2793\"},{\"acquirer\":\"581672141\",\"amount\":10000,\"category\":\"food\",\"date\":\"13930913183220\",\"deposit\":\"0201434050006\",\"detail\":\"خرید\",\"hidden\":false,\"merchant\":\"000000000623039\",\"operation\":\"purchase\",\"terminal\":\"00623860\",\"type\":\"d\",\"tags\":[\"وندینگ ماشین دانشگاه\"],\"id\":\"85973b5d-91d1-43d1-84f7-6fc25d509522\"},{\"acquirer\":\"581672111\",\"amount\":260000,\"category\":\"food\",\"date\":\"13930910131054\",\"deposit\":\"0201434050006\",\"detail\":\"خرید\",\"hidden\":false,\"merchant\":\"62006362057893\",\"operation\":\"purchase\",\"terminal\":\"62058206\",\"type\":\"d\",\"tags\":[\"چارمیز\"],\"id\":\"928a67f2-bcba-4a49-8c92-da6b5914a3c4\"},{\"acquirer\":\"581672041\",\"amount\":194400,\"category\":\"bill\",\"date\":\"13930906225310\",\"deposit\":\"0201434050006\",\"detail\":\"خرید اینترنتی\",\"hidden\":false,\"merchant\":\"000000010000596\",\"operation\":\"purchase\",\"rule\":\"َشارژ اینترنت پارس آنلاین\",\"store\":\"پارس آنلاین\",\"terminal\":\"10000596\",\"type\":\"d\",\"tags\":[\"شارژ اینترنت\",\"پارس آنلاین\"],\"id\":\"30e4df60-9051-4245-9944-03542d337a99\"},{\"amount\":2600000,\"category\":\"commitment\",\"date\":\"13930905101143\",\"deposit\":\"0201434050006\",\"detail\":\"برداشت از خودپرداز\",\"hidden\":false,\"operation\":\"cash\",\"type\":\"d\",\"tags\":[\"کارت هدیه کنسرت\"],\"id\":\"a4c2b84d-5aa6-4482-8a3b-cfc84c9dc46a\"},{\"acquirer\":\"581672061\",\"amount\":330000,\"category\":\"food\",\"date\":\"13930903143315\",\"deposit\":\"0201434050006\",\"detail\":\"خرید\",\"hidden\":false,\"merchant\":\"000000251830933\",\"operation\":\"purchase\",\"rule\":\"سیب زمینی\",\"terminal\":\"33485528\",\"type\":\"d\",\"tags\":[],\"id\":\"cf74a1d2-5c66-4a8f-8279-ef47a931eb34\"},{\"acquirer\":\"581672091\",\"amount\":187000,\"category\":\"food\",\"date\":\"13930830125011\",\"deposit\":\"0201434050006\",\"detail\":\"خرید\",\"hidden\":false,\"merchant\":\"000000041011190\",\"operation\":\"purchase\",\"terminal\":\"04116968\",\"type\":\"d\",\"tags\":[\"سودا\",\"نهار\"],\"id\":\"e031d551-4bb9-40b2-b04f-d5748e821f81\"},{\"acquirer\":\"581672031\",\"amount\":400000,\"category\":\"transportation\",\"date\":\"13930830123232\",\"deposit\":\"0201434050006\",\"detail\":\"خرید\",\"hidden\":false,\"merchant\":\"235602\",\"operation\":\"purchase\",\"rule\":\"پمپ بنزین شریعتی\",\"terminal\":\"01020354\",\"type\":\"d\",\"tags\":[\"بنزین\"],\"id\":\"341e80ae-55b2-427c-acc9-d0c7961512b2\"},{\"amount\":665000,\"category\":\"present\",\"date\":\"13930830120757\",\"deposit\":\"0201434050006\",\"description\":\"5022291024393298 10000028 6\",\"detail\":\"انتقال از کارت + کارمزد\",\"hidden\":false,\"operation\":\"transfer\",\"target\":\"5022291024393298\",\"type\":\"d\",\"tags\":[\"خرید قاب آیفون\",\"کارن چاپ\"],\"id\":\"750ed873-744b-4d7d-a7fa-ccf546b35dd9\"},{\"acquirer\":\"581672041\",\"amount\":139000,\"category\":\"food\",\"date\":\"13930828171904\",\"deposit\":\"0201434050006\",\"detail\":\"خرید\",\"hidden\":false,\"merchant\":\"000000000003393\",\"operation\":\"purchase\",\"terminal\":\"00002394\",\"type\":\"d\",\"tags\":[\"لمیز\"],\"id\":\"ddcabf5d-3d9c-4865-96f2-2a1e35d2df95\"},{\"acquirer\":\"581672031\",\"amount\":180000,\"category\":\"healthcare\",\"date\":\"13930824192057\",\"deposit\":\"0201434050006\",\"detail\":\"خرید\",\"hidden\":false,\"merchant\":\"843531\",\"operation\":\"purchase\",\"rule\":\"داروخونه سر کوچه\",\"terminal\":\"01319195\",\"type\":\"d\",\"tags\":[\"مسکن\"],\"id\":\"12c31841-a1ac-49e6-bf77-baf35869d3bd\"},{\"acquirer\":\"581672031\",\"amount\":300200,\"category\":\"transportation\",\"date\":\"13930821154818\",\"deposit\":\"0201434050006\",\"detail\":\"خرید\",\"guild\":\"تعمیرگاه خودرو\",\"hidden\":false,\"merchant\":\"919359\",\"operation\":\"purchase\",\"terminal\":\"01441536\",\"type\":\"d\",\"tags\":[],\"id\":\"f72ba40e-ad8e-481c-8539-2d9a9ae3335f\"},{\"acquirer\":\"581672031\",\"amount\":165000,\"category\":\"food\",\"date\":\"13930819141321\",\"deposit\":\"0201434050006\",\"detail\":\"خرید\",\"hidden\":false,\"merchant\":\"354368\",\"operation\":\"purchase\",\"terminal\":\"00716666\",\"type\":\"d\",\"tags\":[],\"id\":\"63d1e6ef-1a5a-4cd0-a4bb-d13b34343bf4\"},{\"acquirer\":\"581672031\",\"amount\":240000,\"category\":\"food\",\"date\":\"13930817131445\",\"deposit\":\"0201434050006\",\"detail\":\"خرید\",\"hidden\":false,\"merchant\":\"354368\",\"operation\":\"purchase\",\"terminal\":\"00716666\",\"type\":\"d\",\"tags\":[],\"id\":\"b7dbecac-b8d4-41d2-ad6d-176d0edfc498\"},{\"acquirer\":\"581672041\",\"amount\":835500,\"category\":\"transportation\",\"date\":\"13930816100310\",\"deposit\":\"0201434050006\",\"detail\":\"خرید اینترنتی\",\"hidden\":false,\"merchant\":\"000000010212891\",\"operation\":\"purchase\",\"terminal\":\"10212891\",\"type\":\"d\",\"tags\":[],\"id\":\"c9725abd-0cc6-40bf-9d43-90b3ae9f4c16\"},{\"acquirer\":\"581672041\",\"amount\":1920000,\"category\":\"food\",\"date\":\"13930815105831\",\"deposit\":\"0201434050006\",\"detail\":\"خرید\",\"hidden\":false,\"merchant\":\"000000010203513\",\"operation\":\"purchase\",\"store\":\"باغ موزه حسابی\",\"terminal\":\"10203513\",\"type\":\"d\",\"tags\":[],\"id\":\"b2ea8aa3-6588-4761-b7a4-a318e215da5b\"},{\"amount\":689000,\"billNumber\":\"000002888981109056\",\"category\":\"bill\",\"date\":\"13930813121731\",\"deposit\":\"0201434050006\",\"description\":\"2888981109056/68932790\",\"detail\":\"پرداخت قبض موبایل\",\"hidden\":false,\"operation\":\"bill\",\"type\":\"d\",\"tags\":[\"موبایل\"],\"id\":\"5d7bc503-dd43-4089-a0d6-3426dc2a68d3\"},{\"acquirer\":\"581672141\",\"amount\":35000,\"category\":\"food\",\"date\":\"13930808112716\",\"deposit\":\"0201434050006\",\"detail\":\"خرید\",\"hidden\":false,\"merchant\":\"000000000623039\",\"operation\":\"purchase\",\"rule\":\"وندینگ ماشین دانشگاه\",\"terminal\":\"00623860\",\"type\":\"d\",\"tags\":[\"وندینگ ماشین دانشگاه\"],\"id\":\"91a44f19-0d32-44ee-abf6-60c00ca6c580\"},{\"amount\":3005000,\"category\":\"commitment\",\"date\":\"13930803215159\",\"deposit\":\"0201434050006\",\"description\":\"6104337247454570 10000028 6\",\"detail\":\"انتقال از کارت + کارمزد\",\"hidden\":false,\"operation\":\"transfer\",\"target\":\"6104337247454570\",\"type\":\"d\",\"tags\":[\"شورا\",\"کنسرت\",\"باقری\",\"بدهکاری\"],\"id\":\"a80e5b53-323b-4907-8fe8-36573eb7968b\"},{\"acquirer\":\"581672111\",\"amount\":600000,\"category\":\"present\",\"date\":\"13930803205418\",\"deposit\":\"0201434050006\",\"detail\":\"خرید\",\"guild\":\"کامپيوتر(نرم افزار و سخت افزار)\",\"hidden\":false,\"merchant\":\"573490701709\",\"operation\":\"purchase\",\"terminal\":\"90727725\",\"type\":\"d\",\"tags\":[\"خرید قاب آیفون\",\"پگاه\",\"هدیه\"],\"id\":\"d4b659d2-92f3-44bf-8209-50ed7b231230\"},{\"acquirer\":\"581672031\",\"amount\":205000,\"category\":\"food\",\"date\":\"13930803193529\",\"deposit\":\"0201434050006\",\"detail\":\"خرید\",\"hidden\":false,\"merchant\":\"911154\",\"operation\":\"purchase\",\"terminal\":\"01426944\",\"type\":\"d\",\"tags\":[],\"id\":\"0229759e-9b06-4bc1-8fbd-46c87a5e3648\"},{\"acquirer\":\"581672031\",\"amount\":850000,\"category\":\"food\",\"date\":\"13930801100404\",\"deposit\":\"0201434050006\",\"detail\":\"خرید\",\"hidden\":false,\"merchant\":\"987066\",\"operation\":\"purchase\",\"terminal\":\"01555237\",\"type\":\"d\",\"tags\":[],\"id\":\"fe045de8-8e21-4664-86e4-ac570c6324ce\"},{\"acquirer\":\"581672141\",\"amount\":187000,\"category\":\"food\",\"date\":\"13930730183024\",\"deposit\":\"0201434050006\",\"detail\":\"خرید\",\"hidden\":false,\"merchant\":\"000000000588854\",\"operation\":\"purchase\",\"terminal\":\"00589606\",\"type\":\"d\",\"tags\":[],\"id\":\"13c37c1d-5c9e-4f19-b2a4-506e5b133e73\"},{\"acquirer\":\"581672041\",\"amount\":300000,\"category\":\"food\",\"date\":\"13930730111418\",\"deposit\":\"0201434050006\",\"detail\":\"خرید\",\"hidden\":false,\"merchant\":\"000000010061582\",\"operation\":\"purchase\",\"terminal\":\"10061582\",\"type\":\"d\",\"tags\":[],\"id\":\"f2d5fc9d-1b71-4fa1-8a86-af9fa6f74d95\"},{\"acquirer\":\"581672031\",\"amount\":336000,\"category\":\"food\",\"date\":\"13930729212722\",\"deposit\":\"0201434050006\",\"detail\":\"خرید\",\"hidden\":false,\"merchant\":\"246109\",\"operation\":\"purchase\",\"terminal\":\"01114635\",\"type\":\"d\",\"tags\":[],\"id\":\"88613f5b-cf90-4aeb-8079-88b1f7465428\"},{\"acquirer\":\"581672031\",\"amount\":215000,\"category\":\"food\",\"date\":\"13930728134143\",\"deposit\":\"0201434050006\",\"detail\":\"خرید\",\"hidden\":false,\"merchant\":\"354368\",\"operation\":\"purchase\",\"terminal\":\"00716666\",\"type\":\"d\",\"tags\":[],\"id\":\"ea8eca46-496d-46a9-9e67-22798b0a1931\"},{\"acquirer\":\"581672031\",\"amount\":215000,\"category\":\"food\",\"date\":\"13930727153511\",\"deposit\":\"0201434050006\",\"detail\":\"خرید\",\"hidden\":false,\"merchant\":\"354368\",\"operation\":\"purchase\",\"terminal\":\"00716666\",\"type\":\"d\",\"tags\":[],\"id\":\"49f3e71f-1878-4cae-af37-b46ce124b4b6\"},{\"acquirer\":\"581672031\",\"amount\":212000,\"category\":\"food\",\"date\":\"13930726224551\",\"deposit\":\"0201434050006\",\"detail\":\"خرید\",\"hidden\":false,\"merchant\":\"681524\",\"operation\":\"purchase\",\"terminal\":\"01096656\",\"type\":\"d\",\"tags\":[],\"id\":\"728af8ca-88dd-4b36-8bfb-f73d126e90d3\"},{\"acquirer\":\"581672031\",\"amount\":2000000,\"category\":\"commitment\",\"date\":\"13930626204933\",\"deposit\":\"0201434050006\",\"detail\":\"خرید\",\"hidden\":false,\"merchant\":\"565935\",\"operation\":\"purchase\",\"terminal\":\"00955141\",\"type\":\"d\",\"tags\":[\"شورا\",\"بدهکاری\"],\"id\":\"a1354ed6-0e8c-4ba3-a401-ae67d5da9581\"},{\"amount\":300000,\"category\":\"personal\",\"date\":\"13930624101142\",\"deposit\":\"0201434050006\",\"detail\":\"برداشت از خودپرداز\",\"hidden\":false,\"operation\":\"cash\",\"type\":\"d\",\"tags\":[],\"id\":\"6dc9dcd0-1aad-4cc3-97f8-aa72a0351556\"},{\"amount\":200000,\"category\":\"personal\",\"date\":\"13930616133624\",\"deposit\":\"0201434050006\",\"detail\":\"برداشت از خودپرداز\",\"hidden\":false,\"operation\":\"cash\",\"type\":\"d\",\"tags\":[],\"id\":\"0aad54c3-1b6c-42a5-adba-85b28daaf0d7\"},{\"amount\":180000,\"billNumber\":\"\",\"category\":\"bill\",\"date\":\"13930612104605\",\"deposit\":\"0201434050006\",\"description\":\"/1\",\"detail\":\"خرید شارژ\",\"hidden\":false,\"operation\":\"bill\",\"type\":\"d\",\"tags\":[\"شارژ\",\"طرح ترافیک\"],\"id\":\"d221e9c4-e7d4-4a65-953d-8520a8133b3a\"},{\"amount\":200000,\"category\":\"investment\",\"date\":\"13930422100547\",\"deposit\":\"0201434050006\",\"detail\":\"برداشت از خودپرداز\",\"hidden\":false,\"operation\":\"cash\",\"type\":\"d\",\"tags\":[\"برداشت نقدی\"],\"id\":\"b9eaa216-bf2a-455e-8f83-54d20c8d11da\"},{\"amount\":180000,\"category\":\"bill\",\"date\":\"13930421111432\",\"deposit\":\"0201434050006\",\"description\":\"/1\",\"detail\":\"خرید شارژ\",\"hidden\":false,\"operation\":\"bill\",\"type\":\"d\",\"tags\":[\"شارژ\"],\"id\":\"5aa93754-a851-43a3-8489-5f5ce3cbf400\"},{\"acquirer\":\"581672031\",\"amount\":108000,\"category\":\"expense\",\"date\":\"13930417220727\",\"deposit\":\"0201434050006\",\"detail\":\"خرید اینترنتی\",\"hidden\":false,\"merchant\":\"758856\",\"operation\":\"purchase\",\"terminal\":\"01192699\",\"type\":\"d\",\"tags\":[\"شارژ پنل پیامک\",\"شورا\"],\"id\":\"bab351c5-3c8c-40db-8d42-dbc3a5b8be9b\"},{\"acquirer\":\"581672031\",\"amount\":32400,\"category\":\"expense\",\"date\":\"13930417213527\",\"deposit\":\"0201434050006\",\"detail\":\"خرید اینترنتی\",\"hidden\":false,\"merchant\":\"758856\",\"operation\":\"purchase\",\"terminal\":\"01192699\",\"type\":\"d\",\"tags\":[\"شارژ پنل پیامک\",\"شورا\"],\"id\":\"4bd7a80e-9b20-4478-936e-aac471ff2734\"},{\"amount\":20000,\"category\":\"healthcare\",\"date\":\"13930411075831\",\"deposit\":\"0201434050006\",\"description\":\"\",\"detail\":\"انتقالی\",\"hidden\":false,\"operation\":\"transfer\",\"rule\":\"باشی زاده\",\"target\":\"0201434364001\",\"type\":\"d\",\"tags\":[\"باشی زاده\"],\"id\":\"44eb931c-535d-48f1-adba-f356ce9982dd\"},{\"amount\":7500,\"category\":\"investment\",\"date\":\"13930410083110\",\"deposit\":\"0201434050006\",\"description\":\"\",\"detail\":\"نقدی\",\"hidden\":false,\"operation\":\"cash\",\"type\":\"d\",\"tags\":[],\"id\":\"9af16417-390a-4117-92b7-bdf0edb2231f\"},{\"amount\":5000,\"category\":\"food\",\"date\":\"13930410083032\",\"deposit\":\"0201434050006\",\"description\":\"تمبر\",\"detail\":\"انتقالی\",\"guild\":null,\"hidden\":false,\"operation\":\"transfer\",\"parentId\":\"c9702074-06cb-4456-a7ac-8e839da3ca2f\",\"rule\":null,\"store\":null,\"target\":null,\"type\":\"d\",\"tags\":[],\"id\":\"aaa864e7-5e73-4457-9920-cc242186aa3b\"},{\"amount\":23000,\"category\":\"expense\",\"date\":\"13930410082918\",\"deposit\":\"0201434050006\",\"description\":\"كارزت\",\"detail\":\"انتقالی\",\"hidden\":false,\"operation\":\"transfer\",\"type\":\"d\",\"tags\":[],\"id\":\"d2a09813-5417-4fb3-b5d5-aeec28f41a48\"}]";
                String transIncomeIn="[{\"amount\":300000,\"category\":\"borrow\",\"date\":\"13931001135427\",\"deposit\":\"0201434050006\",\"description\":\"5022291032575191 10001955 1\",\"detail\":\"انتقال به کارت\",\"hidden\":false,\"operation\":\"transfer\",\"target\":\"5022291032575191\",\"type\":\"c\",\"tags\":[],\"id\":\"f74f7059-ccba-4973-ab68-9f06bb98d414\"},{\"amount\":300000,\"category\":\"income\",\"date\":\"13930920201358\",\"deposit\":\"0201434050006\",\"description\":\"6037991883894093 00011011 5\",\"detail\":\"انتقال به کارت\",\"hidden\":false,\"operation\":\"transfer\",\"target\":\"6037991883894093\",\"type\":\"c\",\"tags\":[],\"id\":\"097ab80e-bedb-41f4-8154-ca41d79b23b0\"},{\"amount\":1000000,\"category\":\"borrow\",\"date\":\"13930912120405\",\"deposit\":\"0201434050006\",\"description\":\"6362141094599058 10000028\",\"detail\":\"انتقال به کارت\",\"hidden\":false,\"operation\":\"transfer\",\"type\":\"c\",\"tags\":[],\"id\":\"8cb2c74c-62c0-4129-9946-5513e8e47229\"},{\"amount\":34112,\"category\":\"interest\",\"date\":\"13930910000000\",\"deposit\":\"0201434050006\",\"description\":\"0000000000000\",\"detail\":\"سود سپرده کوتاه مدت\",\"hidden\":false,\"operation\":\"interest\",\"type\":\"c\",\"tags\":[],\"id\":\"26b5e86b-dbfb-4ed8-9969-4efcb62409c7\"},{\"amount\":5700000,\"category\":\"salary\",\"date\":\"13930904164321\",\"deposit\":\"0201434050006\",\"description\":\"از 0200997749006\",\"detail\":\"انتقال اینترنتی\",\"hidden\":false,\"operation\":\"transfer\",\"target\":\"0200997749006\",\"type\":\"c\",\"tags\":[\"حقوق ارتباط فردا\"],\"id\":\"c751cd54-5c8a-44af-a996-077aac061077\"},{\"amount\":4000000,\"category\":\"subsidy\",\"date\":\"13930902101221\",\"deposit\":\"0201434050006\",\"description\":\"6037691736285628 10102812 1\",\"detail\":\"انتقال به کارت\",\"hidden\":false,\"operation\":\"transfer\",\"rule\":\"حسین ساسانی\",\"target\":\"6037691736285628\",\"type\":\"c\",\"tags\":[\"پدرجون\",\"ماه مهر+100تومن\"],\"id\":\"a3e0a83c-876f-4609-b06d-86b5f1cea665\"},{\"amount\":2600000,\"category\":\"trust\",\"date\":\"13930901171633\",\"deposit\":\"0201434050006\",\"description\":\"6104337975220797 00000100 1\",\"detail\":\"انتقال به کارت\",\"hidden\":false,\"operation\":\"transfer\",\"target\":\"6104337975220797\",\"type\":\"c\",\"tags\":[\"کارت هدیه کنسرت\"],\"id\":\"e8b57a4c-3f3c-40b2-a6d2-1868ae154050\"},{\"amount\":3000000,\"category\":\"subsidy\",\"date\":\"13930830114223\",\"deposit\":\"0201434050006\",\"description\":\"6221061044009464 00000238 5\",\"detail\":\"انتقال به کارت\",\"hidden\":false,\"operation\":\"transfer\",\"rule\":\"زهرا حقیقت خواه\",\"target\":\"6221061044009464\",\"type\":\"c\",\"tags\":[\"مامان\",\"ماه آبان\"],\"id\":\"54f636c4-a909-406d-bd0b-0f4fdb0927a5\"},{\"amount\":1000000,\"category\":\"loan\",\"date\":\"13930815184409\",\"deposit\":\"0201434050006\",\"description\":\"6219861016446634 00011003 5\",\"detail\":\"انتقال به کارت\",\"hidden\":false,\"operation\":\"transfer\",\"target\":\"6219861016446634\",\"type\":\"c\",\"tags\":[],\"id\":\"a5766a5c-790e-4fb4-a79c-14c8aed040d7\"},{\"amount\":4647862,\"category\":\"loan\",\"date\":\"13930814183834\",\"deposit\":\"0201434050006\",\"description\":\"5022291032250068 115      5\",\"detail\":\"انتقال به کارت\",\"hidden\":false,\"operation\":\"transfer\",\"target\":\"5022291032250068\",\"type\":\"c\",\"tags\":[],\"id\":\"998c3287-948d-4ec1-b3aa-cb6ecb50c593\"},{\"amount\":49572,\"category\":\"interest\",\"date\":\"13930810000000\",\"deposit\":\"0201434050006\",\"description\":\"0000000000000\",\"detail\":\"سود سپرده کوتاه مدت\",\"hidden\":false,\"operation\":\"interest\",\"type\":\"c\",\"tags\":[],\"id\":\"dd13c206-ef9a-44ef-b3cb-72ba9888608b\"},{\"amount\":4900000,\"category\":\"salary\",\"date\":\"13930804072439\",\"deposit\":\"0201434050006\",\"description\":\"از 0200997749006\",\"detail\":\"انتقال اینترنتی\",\"hidden\":false,\"operation\":\"transfer\",\"target\":\"0200997749006\",\"type\":\"c\",\"tags\":[\"حقوق ارتباط فردا\"],\"id\":\"a45e162e-fbec-4fd3-a1f5-ffd30a8d04fe\"},{\"amount\":19184,\"category\":\"interest\",\"date\":\"13930710000000\",\"deposit\":\"0201434050006\",\"description\":\"0000000000000\",\"detail\":\"سود سپرده کوتاه مدت\",\"hidden\":false,\"operation\":\"interest\",\"type\":\"c\",\"tags\":[],\"id\":\"4038ebca-9daa-40ae-88ae-6eeb8a62feeb\"},{\"amount\":7000000,\"category\":\"bonus\",\"date\":\"13930705123622\",\"deposit\":\"0201434050006\",\"description\":\"6037991821306929 12610301 1\",\"detail\":\"انتقال به کارت\",\"hidden\":false,\"operation\":\"transfer\",\"target\":\"6037991821306929\",\"type\":\"c\",\"tags\":[\"بدهکاری به شورا\",\"پدرجون\"],\"id\":\"1a55ced6-797b-4ff2-828c-80c39ae50e76\"},{\"amount\":2060,\"category\":\"interest\",\"date\":\"13930610000000\",\"deposit\":\"0201434050006\",\"description\":\"0000000000000\",\"detail\":\"سود سپرده کوتاه مدت\",\"hidden\":false,\"operation\":\"interest\",\"type\":\"c\",\"tags\":[],\"id\":\"bbcb6411-98bd-4202-8871-94d5f535d58e\"},{\"amount\":3451,\"category\":\"interest\",\"date\":\"13930510000000\",\"deposit\":\"0201434050006\",\"description\":\"0000000000000\",\"detail\":\"سود سپرده کوتاه مدت\",\"hidden\":false,\"operation\":\"interest\",\"type\":\"c\",\"tags\":[],\"id\":\"05acdf11-02b2-4d4e-ac73-9065fc0ce07d\"},{\"amount\":20000,\"category\":\"income\",\"date\":\"13930411080117\",\"deposit\":\"0201434050006\",\"description\":\"\",\"detail\":\"انتقالی\",\"hidden\":false,\"operation\":\"transfer\",\"target\":\"0201434364001\",\"type\":\"c\",\"tags\":[\"hhjh\"],\"id\":\"0552d436-6984-4fcb-88f1-977a0f888903\"},{\"amount\":1000000,\"category\":\"income\",\"date\":\"13930410082825\",\"deposit\":\"0201434050006\",\"description\":\"خودش\",\"detail\":\"نقدی\",\"hidden\":false,\"operation\":\"cash\",\"type\":\"c\",\"tags\":[],\"id\":\"0534eece-4228-4fc0-9fa0-187181016c00\"}]";
                String accountIn="{\"created\":\"13930414095026\",\"email\":\"keyvan.sasani@gmail.com\",\"modified\":\"13930926000000\",\"filterConfig\":{\"from\":null,\"to\":null,\"withoutTags\":[],\"withTags\":[],\"deposits\":[\"0201434050006\",\"0017494550*نقدی\",\"0017494550*نقدی 4\"]},\"budget\":{\"from\":\"1393/1/1\",\"sendWarning\":true,\"to\":\"1393/12/29\",\"warningPercent\":80,\"categories\":[]},\"deposits\":[{\"code\":\"0201434050006\",\"type\":\"Short Term\",\"shared\":false},{\"code\":\"0017494550*نقدی\",\"type\":\"Handy\",\"shared\":false},{\"code\":\"0017494550*نقدی 4\",\"type\":\"Handy\"}],\"id\":\"0017494550\",\"firstName\":\"کیوان\",\"lastName\":\"ساسانی\",\"tags\":[\"وندینگ ماشین دانشگاه\",\"شورا\",\"سیب زمینی\",\"باشی زاده\",\"بدهکاری\",\"خرید قاب آیفون\",\"حقوق ارتباط فردا\",\"موبایل\",\"برداشت نقدی\",\"پدرجون\",\"نهار\",\"کارت هدیه کنسرت\",\"شارژ اینترنت\",\"پارس آنلاین\",\"شارژ پنل پیامک\",\"شارژ\",\"sc\",\"پگاه\",\"sz\",\"کافی میت\",\"سودا\",\"تاکسی\",\"sff\",\"لمیز\",\"hhjh\",\"sr\",\"محمد قریشی\",\"مسکن\",\"کنسرت\",\"قبض موبایل\",\"ds\",\"احسان سمیعی\",\"نوژن\",\"کیک\",\"چارمیز\",\"ماه مهر+100تومن\",\"کارت هدیه\",\"بنزین\",\"ماه آبان\",\"مامان\",\"کارن چاپ\",\"sddf\",\"سوییت بلیس\",\"sssssssssssssssssssssssss\",\"sqe\",\"d\",\"هدیه\",\"باقری\",\"کبابی طالقانی\",\"بدهکاری به شورا\",\"ثبت نام93\",\"طرح ترافیک\",\"کافه لورکا\",\"sq\"]}";
                PersianDate date;
                Time time;
                JsonParser jsonParser=new JsonParser(url,url);
                jsonParser.readAndParseAccountJSON(accountIn);


                Account account=jsonParser.getUserAccount();
                LocalDBServices.addJsonAccounts(getBaseContext(),account);


                jsonParser.readAndParseTransactionJSON(transExpenseIn);
                ArrayList <TimelineItem2> t2=jsonParser.getTransItems();
                for(int i=0;i<t2.size();i++){
                    LocalDBServices.addJsonTransaction(getBaseContext(),t2.get(i).getTransactionID(),t2.get(i).getDateString(),t2.get(i).getAmount(),t2.get(i).isExpence(),t2.get(i).getAccountName(),t2.get(i).getCategoryID(),t2.get(i).getTags(),t2.get(i).getDescription());
            }
                jsonParser.readAndParseTransactionJSON(transIncomeIn);
                t2=jsonParser.getTransItems();
                for(int i=0;i<t2.size();i++){
                    LocalDBServices.addJsonTransaction(getBaseContext(),t2.get(i).getTransactionID(),t2.get(i).getDateString(),t2.get(i).getAmount(),t2.get(i).isExpence(),t2.get(i).getAccountName(),t2.get(i).getCategoryID(),t2.get(i).getTags(),t2.get(i).getDescription());
                }
            }

        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_setting, menu);
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
        else if(item.toString().equals(getResources().getString(R.string.action_uncategorized))){
            intent=new Intent(SettingActivity.this, UncategoriedActivity.class);
        }
        else if(item.toString().equals(getResources().getString(R.string.action_charts))){
            intent=new Intent(SettingActivity.this, ChartActivity.class);
        }
        else if(item.toString().equals(getResources().getString(R.string.action_settings))){
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
    private void commitData() throws ParseException
    {

//            SQLiteDatabase db = trHelper.getWritableDatabase();

//            trHelper.onUpgrade(db, 1, 2);
            for(TimelineItem t: listItems)
            {
                PersianDate date = t.date;
                Time time = t.time;
                String dateTime = date.getSTDString()+time.getSTDString();

                ContentValues values = new ContentValues();
                values.put(TransactionsContract.TransactionEntry.COLUMN_NAME_DATE_TIME, dateTime);
                values.put(TransactionsContract.TransactionEntry.COLUMN_NAME_AMOUNT, t.amount);
                values.put(TransactionsContract.TransactionEntry.COLUMN_NAME_IS_EXPENSE, t.isExpence);
                values.put(TransactionsContract.TransactionEntry.COLUMN_NAME_CATEGORY, t.categoryID);
                values.put(TransactionsContract.TransactionEntry.COLUMN_NAME_ACCOUNT_NAME,t.accountName);

//                db.insert(TransactionsContract.TransactionEntry.TABLE_NAME, null, values);
                LocalDBServices.addNewTransaction(getBaseContext(),dateTime,t.amount,t.isExpence,t.accountName,t.categoryID,null,null);
            }

    }
    private void addTestAccountToDb(){
//        TransactoinDatabaseHelper trHelper=new TransactoinDatabaseHelper(this);
//        SQLiteDatabase db = trHelper.getWritableDatabase();
//        ContentValues values = new ContentValues();
//        values.put(TransactionsContract.Accounts.COLUMN_NAME_Account_Name,"نقدی 1");
//        values.put(TransactionsContract.Accounts.COLUMN_NAME_Account_Type,1);
//        db.insertOrThrow(TransactionsContract.Accounts.TABLE_NAME, null, values);
//
//
//        values = new ContentValues();
//        values.put(TransactionsContract.Accounts.COLUMN_NAME_Account_Name,"نقدی 2");
//        values.put(TransactionsContract.Accounts.COLUMN_NAME_Account_Type,1);
//        db.insert(TransactionsContract.Accounts.TABLE_NAME,null,values);
//
//        values = new ContentValues();
//        values.put(TransactionsContract.Accounts.COLUMN_NAME_Account_Name,"کوتاه مدت آینده");
//        values.put(TransactionsContract.Accounts.COLUMN_NAME_Account_Type,0);
//        db.insert(TransactionsContract.Accounts.TABLE_NAME,null,values);
        LocalDBServices.addTestAccounts(getBaseContext());
    }

    private void addAccountsToList() {
//        SQLiteDatabase db = trHelper.getReadableDatabase();
//        Cursor c2;
        accountsList=new ArrayList<String>();
//        String query="select "+ TransactionsContract.Accounts.COLUMN_NAME_Account_Name+
//                " from "+ TransactionsContract.Accounts.TABLE_NAME;
//
//        c2=db.rawQuery(query,null);
        Cursor c2=LocalDBServices.getAccountList(this);
        c2.moveToFirst();


        if(c2.getCount() != 0)
        {
            do
            {
                String accountName=c2.getString(c2.getColumnIndexOrThrow(TransactionsContract.Accounts.COLUMN_NAME_Account_Name));
                if(c2!=null){
//                    if(c2.isFirst())

                        accountsList.add(accountName);
//                    else
//                        accountsAndTimeFilter.add(new FilterMenuItem("", true,accountName, false, R.drawable.vaam_raw));
                }
            }while(c2.moveToNext());
        }
     c2.close();
    }

    public static String getAccountType(String accountName,Context context){
//        TransactoinDatabaseHelper trHelper=new TransactoinDatabaseHelper(context);
//        SQLiteDatabase db =trHelper.getReadableDatabase();
        Cursor c2;
        String accountType="-1";
        accountsList=new ArrayList<String>();
//        String query="select "+ TransactionsContract.Accounts.COLUMN_NAME_Account_Name+
//                " , "+TransactionsContract.Accounts.COLUMN_NAME_Account_Type+
//                " from "+ TransactionsContract.Accounts.TABLE_NAME;

        c2=LocalDBServices.getAccountList(context);
//        c2=db.rawQuery(query,null);
        c2.moveToFirst();

        if(c2.getCount() != 0)
        {
            do
            {
                String accountNameDB=c2.getString(c2.getColumnIndexOrThrow(TransactionsContract.Accounts.COLUMN_NAME_Account_Name));
                String accountTypeDB=c2.getString(c2.getColumnIndexOrThrow(TransactionsContract.Accounts.COLUMN_NAME_Account_Type));
                if(c2!=null){
                    if(accountNameDB.equals(accountName))
                    accountType=accountTypeDB;
                }
            }while(c2.moveToNext());
        }
        c2.close();
        if(accountType.equals("-1"))
            Log.e("error","an error happend on finding account type:setting activity.getaccountType");
        return accountType;
    }
}
