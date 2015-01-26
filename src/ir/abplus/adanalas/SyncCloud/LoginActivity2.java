package ir.abplus.adanalas.SyncCloud;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import ir.abplus.adanalas.Libraries.LodingDialog;
import ir.abplus.adanalas.R;
import ir.abplus.adanalas.Timeline.TimelineActivity;
import ir.abplus.adanalas.databaseConnections.LocalDBServices;
import org.apache.http.*;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Keyvan Sasani on 12/17/2014.
 */
public class LoginActivity2 extends Activity {
    EditText usernameEditText;
    EditText passwordEditText;
    ImageView logoImageView;
    Button enterButton;
    AsyncTask loginTask;
    LodingDialog dialog;
    private static int ERROR_CONNECTION=0;
    private static int ERROR_USERPASS=1;
    private static int ERROR_PFM_NOT_FOUND=2;
    private static int ERROR_UNKNOWN=3;
    private static int LOGIN_SUCSESS=4;

    private static String PISHKHAN_ADDRESS = "https://www.abplus.ir/panel/login/index/";

//    private static String PISHKHAN_ADDRESS = "https://www.abplus.ir/panel/login/index/";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        changeUserData();

        usernameEditText = (EditText) findViewById(R.id.editTextUsername);
        logoImageView = (ImageView) findViewById(R.id.imageView);
        passwordEditText = (EditText) findViewById(R.id.editTextPassword);
        enterButton = (Button) findViewById(R.id.buttonEnter);

        logoImageView.setImageDrawable(getResources().getDrawable(R.drawable.icon_96));
        enterButton.setTypeface(TimelineActivity.persianTypeface);

        enterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    loginTask=new DoLogin().execute();
//                    new DoAnimation().execute();
                dialog=new LodingDialog(LoginActivity2.this,getResources(),"ورود به سیستم");
                dialog.show();

//                doLogin(usernameEditText.getText().toString(), passwordEditText.getText().toString());
            }


        });

    }


    private int tryLogin(String username, String password) {
        int result;
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            int  pishkhanResInt = pishkhanLoginTask(LoginActivity2.PISHKHAN_ADDRESS, username, password);
            result=pishkhanResInt;
            if (pishkhanResInt==LOGIN_SUCSESS) {
                result=LOGIN_SUCSESS;
//                ConnectionManager.pfmCookie = pishkhanResObject.getPfmCookie();
//                ConnectionManager.pfmToken = pishkhanResObject.getPfmToken();
                LocalDBServices.addTokens(this,ConnectionManager.pfmToken ,ConnectionManager.pfmCookie);
                dialog.cancel();
                Intent intent = new Intent(LoginActivity2.this, TimelineActivity.class);
                finish();
                startActivity(intent);
                return result;
            } else {
                dialog.cancel();
//                result=ERROR_PFM_NOT_FOUND;
                return result;
            }

        } else {
            result=ERROR_CONNECTION;
            dialog.cancel();
            return result;
        }
//        return ERROR_UNKNOWN;
    }

    private int pishkhanLoginTask(String myurl, String username, String password) {

        final HttpParams httpParams = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParams, 15000);
        DefaultHttpClient httpclient = new DefaultHttpClient(httpParams);
        HttpGet httpGet = new HttpGet(myurl);
        if(username.length()<10){
            int i=10-username.length();
            while (i>-1){
                username="0"+username;
            }
        }

        HttpResponse response = null;
        try {
            response = httpclient.execute(httpGet);
            StatusLine statusLine = response.getStatusLine();

            if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                response.getEntity().writeTo(out);
                out.close();
                String responseString = out.toString();
                //trying to get pishkhan's login form csrf token
                String csrf_token = responseString.substring(responseString.indexOf("name=\"csrf_token\" value=\"") + 25, responseString.indexOf("name=\"csrf_token\" value=\"") + 57);

                //http login post
                HttpPost httpPost = new HttpPost(myurl);
                httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded");
                List<NameValuePair> postParams = new ArrayList<NameValuePair>();
                postParams.add(new BasicNameValuePair("username", username));
                postParams.add(new BasicNameValuePair("password", password));
                postParams.add(new BasicNameValuePair("csrf_token", csrf_token));
                httpPost.setEntity(new UrlEncodedFormEntity(postParams));
                response = httpclient.execute(httpPost);
                statusLine = response.getStatusLine();
                if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
                    out = new ByteArrayOutputStream();
                    response.getEntity().writeTo(out);
                    out.close();
                    responseString = out.toString();

                    String pfmVerifyUrl = responseString.substring(responseString.indexOf("https://pfm.abplus.ir"), responseString.indexOf("https://pfm.abplus.ir") + 87);
//                    Log.e("debug", "here is pfm token:" +pfmToken);
                    if (!pfmVerifyUrl.contains("verify?token")){
                        Log.e("debug", "user name or password is incorrect");
                        return ERROR_USERPASS;
                    }
                    else {
                        HttpGet pfmGet = new HttpGet(pfmVerifyUrl);
                        response = httpclient.execute(pfmGet);
                        statusLine = response.getStatusLine();
                        if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
                            out = new ByteArrayOutputStream();
                            response.getEntity().writeTo(out);
                            out.close();
                            responseString = out.toString();
                            String pfmCSRFToken = responseString.substring(responseString.indexOf("csrfToken") + 18, responseString.indexOf("csrfToken") + 54);
                            String pfmcookie = "";
                            Header[] headers = response.getAllHeaders();
                            for (Header header : headers) {
                                if (header.getName().equals("set-cookie")) {
                                    HeaderElement[] headerElements = header.getElements();
                                    for (HeaderElement headerElement : headerElements) {
                                        if (headerElement.getName().equals("sid")) {
                                            pfmcookie = headerElement.getValue();
                                            break;
                                        }
                                    }
                                }
                            }

//                            result.setPfmToken(pfmCSRFToken);
//                            result.setPfmCookie(pfmcookie);
                            ConnectionManager.pfmToken=pfmCSRFToken;
                            ConnectionManager.pfmCookie=pfmcookie;
                            return LOGIN_SUCSESS;
//                            System.out.println("pfm cookie is : " + pfmcookie);
//                            System.out.println("trying to get /me");

                        }
                    }
                }
            } else {
                //Closes the connection.
                Log.e("debug", "there is a problem on getting main connection result!");
                response.getEntity().getContent().close();
                throw new IOException(statusLine.getReasonPhrase());
            }
        } catch (IOException e) {
            e.printStackTrace();
            return ERROR_UNKNOWN;
        }
        return ERROR_UNKNOWN;
    }

    private class DoLogin extends AsyncTask{

        int result;
        @Override
        protected Object doInBackground(Object[] objects) {
//                doLogin(usernameEditText.getText().toString(), passwordEditText.getText().toString());
            result=tryLogin(usernameEditText.getText().toString(), passwordEditText.getText().toString());
            cancel(true);
            return null;
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            if(result==ERROR_CONNECTION){
                Toast.makeText(LoginActivity2.this, "اتصال به اینترنت وجود ندارد", Toast.LENGTH_SHORT).show();
            }
            else if(result==ERROR_PFM_NOT_FOUND){
                Toast.makeText(LoginActivity2.this, "خطا در اتصال به سرور", Toast.LENGTH_SHORT).show();
            }
            else if(result==ERROR_USERPASS){
                Toast.makeText(LoginActivity2.this, "نام کاربری یا رمز عبور نامعتبر می باشد", Toast.LENGTH_SHORT).show();
            }
            else if(result==ERROR_UNKNOWN){
                Toast.makeText(LoginActivity2.this, "خطای نامشخص، لطفا دوباره تلاش کنید", Toast.LENGTH_SHORT).show();
            }
        }

//        @Override
//        protected void onPostExecute(Object o) {
//            super.onPostExecute(o);
//            System.out.println(result);
//            Toast.makeText(LoginActivity2.this, "اتصال به اینترنت وجود ندارد", Toast.LENGTH_SHORT).show();
//        }
    }
    private void changeUserData(){
        new Declaration(LoginActivity2.this);
        LocalDBServices.clearDatabase(LoginActivity2.this);
        ConnectionManager.pfmCookie="";
        ConnectionManager.pfmToken="";
    }

}