package ir.abplus.adanalas.SyncCloud;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import ir.abplus.adanalas.R;
import ir.abplus.adanalas.Timeline.TimelineActivity;
import org.apache.http.*;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.BasicNameValuePair;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Keyvan Sasani on 12/17/2014.
 */
public class LoginActivity extends Activity {
    EditText usernameEditText;
    EditText passwordEditText;
    ImageView logoImageView;
    Button enterButton;
    private static String PISHKHAN_ADDRESS = "https://www.abplus.ir/panel/login/index/";
    private static String PFM_ME_ADDRESS = "https://pfm.abplus.ir/me";
//    private static String PISHKHAN_ADDRESS = "https://www.abplus.ir/panel/login/index/";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        usernameEditText = (EditText) findViewById(R.id.editTextUsername);
        logoImageView = (ImageView) findViewById(R.id.imageView);
        passwordEditText = (EditText) findViewById(R.id.editTextPassword);
        enterButton = (Button) findViewById(R.id.buttonEnter);

        logoImageView.setImageDrawable(getResources().getDrawable(R.drawable.icon_96));
        enterButton.setTypeface(TimelineActivity.persianTypeface);

        enterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doLogin(usernameEditText.getText().toString(), passwordEditText.getText().toString());
            }


        });

    }

    private void doLogin(String username, String password) {
        String loginStatus = tryLogin(username, password);
        if (loginStatus != null) {
            //try connect to pfm.abplus.ir and get all transactions using getTransactions
            Log.e("debug", "login success");
        } else {
            Log.e("debug", "login failed");
            Toast.makeText(LoginActivity.this, "نام کاربری یا رمز عبور نامعتبر می باشد.", Toast.LENGTH_SHORT).show();
        }
    }

    private String tryLogin(String username, String password) {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            PFMResObject pishkhanResObject=pishkhanLoginTask(LoginActivity.PISHKHAN_ADDRESS, username, password);
            if(pishkhanResObject.getPfmCookie()!=null){
                ConnectionManager.pfmCookie=pishkhanResObject.getPfmCookie();
                ConnectionManager.pfmToken=pishkhanResObject.getPfmToken();
                Intent intent=new Intent(LoginActivity.this, TimelineActivity.class);
                finish();
                startActivity(intent);
                return "login ok";
            }
            else{
                Toast.makeText(LoginActivity.this, "اشکال در دریافت اطلاعات از آدانالس", Toast.LENGTH_SHORT).show();
                Log.e("debug","pfm cookie does not get!");
            }

        } else {
            Toast.makeText(LoginActivity.this, "اتصال به اینترنت وجود ندارد", Toast.LENGTH_SHORT).show();
        }
        return null;
    }

    private PFMResObject pishkhanLoginTask(String myurl, String username, String password) {

        PFMResObject result=new PFMResObject();
        DefaultHttpClient httpclient = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(myurl);
//        todo add related headers to http Post
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
                String csrf_token=responseString.substring(responseString.indexOf("name=\"csrf_token\" value=\"")+25,responseString.indexOf("name=\"csrf_token\" value=\"")+57);
//                Log.e("login debug","pishkhan csrfToken:"+csrf_token);

                //http login post
                HttpPost httpPost = new HttpPost(myurl);
                httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded");
                List<NameValuePair> postParams=new ArrayList<NameValuePair>();
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

                    System.out.println(responseString.toString());
                    Log.e("debug", "here is the index of keyvan:" + responseString.indexOf("کیوان"));

//                    String pfmToken=responseString.substring(responseString.indexOf("verify?token")+13,responseString.indexOf("verify?token")+57);
                    String pfmVerifyUrl=responseString.substring(responseString.indexOf("https://pfm.abplus.ir"),responseString.indexOf("https://pfm.abplus.ir")+87);
//                    Log.e("debug", "here is pfm token:" +pfmToken);
                    Log.e("debug", "here is pfm url:" +pfmVerifyUrl);
                    if(!pfmVerifyUrl.contains("verify?token"))
                        Log.e("debug","user name or password is incorrect");
                    else
                    {

                        HttpGet pfmGet=new HttpGet(pfmVerifyUrl);
                    response=httpclient.execute(pfmGet);
                    statusLine = response.getStatusLine();
                    if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
                        out = new ByteArrayOutputStream();
                        response.getEntity().writeTo(out);
                        out.close();
                        responseString = out.toString();
                        System.out.println(responseString);
                        String pfmCSRFToken = responseString.substring(responseString.indexOf("csrfToken") + 18, responseString.indexOf("csrfToken") + 54);
//                        String pfmcookie=responseString.substring(responseString.indexOf("set")+18,responseString.indexOf("csrfToken")+54);
                        String pfmcookie = "";
                        Header[] headers = response.getAllHeaders();
                        for (Header header : headers) {
                            if (header.getName().equals("set-cookie")) {
                                HeaderElement[] headerElements = header.getElements();
                                for(HeaderElement headerElement : headerElements){
                                    if(headerElement.getName().equals("sid")){
                                        pfmcookie = headerElement.getValue();
                                        break;
                                    }
                                }
//                                pfmcookie = header.getValue();
                            }
                            System.out.println("Key : " + header.getName()
                                    + " ,Value : " + header.getValue());
                        }

//                        pfmcookie = pfmcookie.substring(0, pfmcookie.indexOf(";"));
//                        pfmcookie = pfmcookie.substring(0, pfmcookie.indexOf(";"));
                        result.setPfmToken(pfmCSRFToken);
                        result.setPfmCookie(pfmcookie);
                        System.out.println("pfm cookie is : " + pfmcookie);
                        System.out.println("trying to get /me");
                        httpclient = new DefaultHttpClient();
                        HttpGet pfmGetMe = new HttpGet(PFM_ME_ADDRESS);
//                        pfmGetMe.setHeader("Cookie", pfmcookie);
                        CookieStore cookieStore = httpclient.getCookieStore();
                        BasicClientCookie cookie = new BasicClientCookie("sid", pfmcookie);
//                        cookie.setPath("/me");
                        cookie.setPath("/");
                        cookie.setDomain("pfm.abplus.ir");

                        // Prepare a request object
//                        HttpGet httpget = new HttpGet("http://abc.net/restofurl");

                        cookieStore.addCookie(cookie);
                        httpclient.setCookieStore(cookieStore);

                        pfmGetMe.setHeader("Host", "pfm.abplus.ir");
                        pfmGetMe.setHeader("Connection", "keep-alive");
                        pfmGetMe.setHeader("Cache-Control" ,"no-cache");
                        pfmGetMe.setHeader("User-Agent"," Mozilla/5.0 (Windows NT 6.2; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/39.0.2171.95 Safari/537.36");
                        pfmGetMe.setHeader("Accept", "*/*");
                        pfmGetMe.setHeader("Accept-Encoding", "gzip, deflate, sdch");
                        pfmGetMe.setHeader("Accept-Language", "en-GB,en-US;q=0.8,en;q=0.6,fa;q=0.4");
                        pfmGetMe.setHeader("Content-Type","application/json");

                        response = httpclient.execute(pfmGet);

                        headers = response.getAllHeaders();
                        for (Header header : headers) {

                            System.out.println("Key : " + header.getName()
                                    + " ,Value : " + header.getValue());
                        }

                        statusLine = response.getStatusLine();
                        if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
                            out = new ByteArrayOutputStream();
                            response.getEntity().writeTo(out);
                            out.close();
                        }
                        responseString = out.toString();
                        System.out.println(responseString);
                    }
                    }
                }
            } else {
                //Closes the connection.
                Log.e("debug","there is a problem on getting main connection result!");
                response.getEntity().getContent().close();
                throw new IOException(statusLine.getReasonPhrase());

            }
            return result;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}