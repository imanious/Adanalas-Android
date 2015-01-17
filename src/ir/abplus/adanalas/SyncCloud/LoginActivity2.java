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
import ir.abplus.adanalas.databaseConnections.LocalDBServices;
import org.apache.http.*;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

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
    private static String PISHKHAN_ADDRESS = "https://www.abplus.ir/panel/login/index/";

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
            Toast.makeText(LoginActivity2.this, "نام کاربری یا رمز عبور نامعتبر می باشد.", Toast.LENGTH_SHORT).show();
        }
    }

    private String tryLogin(String username, String password) {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            PFMResObject pishkhanResObject = pishkhanLoginTask(LoginActivity2.PISHKHAN_ADDRESS, username, password);
            if (pishkhanResObject.getPfmCookie() != null) {
                ConnectionManager.pfmCookie = pishkhanResObject.getPfmCookie();
                ConnectionManager.pfmToken = pishkhanResObject.getPfmToken();
                LocalDBServices.addTokens(this,pishkhanResObject.getPfmToken(),pishkhanResObject.getPfmCookie());
                Intent intent = new Intent(LoginActivity2.this, TimelineActivity.class);
                finish();
                startActivity(intent);
                return "login ok";
            } else {
                Toast.makeText(LoginActivity2.this, "اشکال در دریافت اطلاعات از آدانالس", Toast.LENGTH_SHORT).show();
                Log.e("debug", "pfm cookie does not get!");
            }

        } else {
            Toast.makeText(LoginActivity2.this, "اتصال به اینترنت وجود ندارد", Toast.LENGTH_SHORT).show();
        }
        return null;
    }

    private PFMResObject pishkhanLoginTask(String myurl, String username, String password) {

        PFMResObject result = new PFMResObject();
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
                String csrf_token = responseString.substring(responseString.indexOf("name=\"csrf_token\" value=\"") + 25, responseString.indexOf("name=\"csrf_token\" value=\"") + 57);
//                Log.e("login debug","pishkhan csrfToken:"+csrf_token);

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

//                    System.out.println(responseString.toString());
                    Log.e("debug", "here is the index of keyvan:" + responseString.indexOf("کیوان"));

//                    String pfmToken=responseString.substring(responseString.indexOf("verify?token")+13,responseString.indexOf("verify?token")+57);
                    String pfmVerifyUrl = responseString.substring(responseString.indexOf("https://pfm.abplus.ir"), responseString.indexOf("https://pfm.abplus.ir") + 87);
//                    Log.e("debug", "here is pfm token:" +pfmToken);
                    Log.e("debug", "here is pfm url:" + pfmVerifyUrl);
                    if (!pfmVerifyUrl.contains("verify?token"))
                        Log.e("debug", "user name or password is incorrect");
                    else {

                        HttpGet pfmGet = new HttpGet(pfmVerifyUrl);
                        response = httpclient.execute(pfmGet);
                        statusLine = response.getStatusLine();
                        if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
                            out = new ByteArrayOutputStream();
                            response.getEntity().writeTo(out);
                            out.close();
                            responseString = out.toString();
//                            System.out.println(responseString);
                            String pfmCSRFToken = responseString.substring(responseString.indexOf("csrfToken") + 18, responseString.indexOf("csrfToken") + 54);
//                        String pfmcookie=responseString.substring(responseString.indexOf("set")+18,responseString.indexOf("csrfToken")+54);
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
//                                pfmcookie = header.getValue();
                                }
                                System.out.println("Key : " + header.getName()
                                        + " ,Value : " + header.getValue());
                            }

                            result.setPfmToken(pfmCSRFToken);
                            result.setPfmCookie(pfmcookie);
                            System.out.println("pfm cookie is : " + pfmcookie);
                            System.out.println("trying to get /me");



//                                                        URL url = new URL("https://pfm.abplus.ir/me");
//                            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
//                            urlConnection.setRequestProperty("Cookie", "sid=" + pfmcookie);
//                            urlConnection.connect();
//                            try {
//                                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
//                                StringBuffer sb = new StringBuffer();
//                                InputStreamReader isr = new InputStreamReader(in);
//                                int numCharsRead;
//                                char[] charArray = new char[1024];
//                                while ((numCharsRead = isr.read(charArray)) > 0) {
//                                    sb.append(charArray, 0, numCharsRead);
//                                }
//                                System.out.println("CLIENT RECEIVED: " + sb.toString());
//
//                            } catch (Exception e) {
//                                System.out.println(e);
//                            } finally {
//                                urlConnection.disconnect();
//                            }

//
//                            String transUrl="https://pfm.abplus.ir/transactions?offset=0&limit=100&sortField=date&sortReverse=1&type=d&hidden=false&deposits=0201434050006&deposits=0017494550*%D9%86%D9%82%D8%AF%DB%8C&deposits=0017494550*%D9%86%D9%82%D8%AF%DB%8C%204&from=&to=&categories=household&categories=food&categories=transportation&categories=education&categories=bill&categories=hobby&categories=healthcare&categories=hygiene&categories=clothing&categories=personal&categories=present&categories=lend&categories=commitment&categories=investment&categories=expense&categories=bonus&categories=salary&categories=subsidy&categories=gift&categories=rent&categories=interest&categories=compensation&categories=sale&categories=trust&categories=borrow&categories=loan&categories=income&min=&max=&importName=&_=1419681104559";
//                            url = null;
//                            urlConnection = null;
//                            String accountResultString="";
//                            try {
//                                url = new URL(transUrl);
//                                urlConnection = (HttpURLConnection) url.openConnection();
//                                urlConnection.setRequestProperty("Cookie", "sid=" + pfmcookie);
//                                urlConnection.connect();
//                                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
//                                StringBuffer sb = new StringBuffer();
//                                InputStreamReader isr = new InputStreamReader(in);
//                                int numCharsRead;
//                                char[] charArray = new char[100000];
//                                while ((numCharsRead = isr.read(charArray)) > 0) {
//                                    sb.append(charArray, 0, numCharsRead);
//                                }
//                                System.out.println("CLIENT RECEIVED: " + sb.toString());
////                                tranactionsResultString=sb.toString();
//
//                            } catch (MalformedURLException e) {
//                                e.printStackTrace();
//                            } catch (IOException e) {
//                                e.printStackTrace();
//                            }
//                            finally {
//                                urlConnection.disconnect();
//                            }
//
//


//
//                            URL url = new URL(PFM_ME_ADDRESS);
//                            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
//                            urlConnection.setRequestProperty("Cookie", "sid=" + pfmcookie);
//                            urlConnection.connect();
//                            try {
//                                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
//                                StringBuffer sb = new StringBuffer();
//                                InputStreamReader isr = new InputStreamReader(in);
//                                int numCharsRead;
//                                char[] charArray = new char[1024];
//                                while ((numCharsRead = isr.read(charArray)) > 0) {
//                                    sb.append(charArray, 0, numCharsRead);
//                                }
//                                System.out.println("CLIENT RECEIVED: " + sb.toString());
//
//                            } catch (Exception e) {
//                                System.out.println(e);
//                            } finally {
//                                urlConnection.disconnect();
//                            }

                        }
                    }
                }
            } else {
                //Closes the connection.
                Log.e("debug", "there is a problem on getting main connection result!");
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