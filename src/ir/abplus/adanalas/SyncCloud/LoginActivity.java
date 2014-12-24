package ir.abplus.adanalas.SyncCloud;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import ir.abplus.adanalas.R;
import ir.abplus.adanalas.Timeline.TimelineActivity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Keyvan Sasani on 12/17/2014.
 */
public class LoginActivity extends Activity {
    EditText usernameEditText;
    EditText passwordEditText;
    ImageView logoImageView;
    Button enterButton;
    private static String PISHKHAN_ADDRESS = "https://www.abplus.ir/panel/login/index/";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        usernameEditText = (EditText) findViewById(R.id.editTextUsername);
        logoImageView = (ImageView) findViewById(R.id.imageView);
        passwordEditText = (EditText) findViewById(R.id.editTextPassword);
        enterButton = (Button) findViewById(R.id.buttonEnter);

        logoImageView.setImageDrawable(getResources().getDrawable(R.drawable.icon_96));

        enterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doLogin(usernameEditText.getText().toString(), passwordEditText.getText().toString());
            }

            private void doLogin(String username, String password) {
                String loginStatus = tryLogin(username, password);
                if (loginStatus != null) {
                    //try connect to pfm.abplus.ir and get all transactions using getTransactions
                    Log.e("debug", "login success");
                } else {
                    Log.e("debug", "login failed");
                    Toast.makeText(LoginActivity.this, "نام کاربری یا رمز عبور نامعتبر می باشد.", Toast.LENGTH_SHORT);
                }
            }

            private String tryLogin(String username, String password) {
                ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
                if (networkInfo != null && networkInfo.isConnected()) {
                    PishkhanResObject pishkhanResObject=pishkhanLoginTask(LoginActivity.PISHKHAN_ADDRESS, username, password);
                    ConnectionManager.pfmCookie=pishkhanResObject.getPfmCookie();
                    ConnectionManager.pfmToken=pishkhanResObject.getPfmToken();
                    Intent intent=new Intent(LoginActivity.this, TimelineActivity.class);
                    finish();
                    startActivity(intent);
                } else {
                    Toast.makeText(LoginActivity.this, "اتصال به اینترنت وجود ندارد", Toast.LENGTH_SHORT);
                }
                return null;
            }
        });

    }

    //
//    private class PishkhanLoginTask extends AsyncTask<String, Void, String> {
//        @Override
//        protected String doInBackground(String... urls) {
//
//            // params comes from the execute() call: params[0] is the url.
//            try {
//                return downloadUrl(urls[0]);
//            } catch (IOException e) {
//                return "Unable to retrieve web page. URL may be invalid.";
//            }
//        }
//        // onPostExecute displays the results of the AsyncTask.
//        @Override
//        protected void onPostExecute(String result) {
//            textView.setText(result);
//        }
//    }
    private PishkhanResObject pishkhanLoginTask(String myurl, String username, String password) {
        InputStream is = null;
        // Only display the first 500 characters of the retrieved
        // web page content.
        int len = 500;


        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(myurl);
        //todo add related headers to http Post
        HttpResponse response = null;
        try {
            response = httpclient.execute(httpPost);
            StatusLine statusLine = response.getStatusLine();

            if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                response.getEntity().writeTo(out);
                out.close();
                String responseString = out.toString();
                //todo get required data
                PishkhanResObject pro = new PishkhanResObject();
                Log.e("debug", "CSRF Token is:" + responseString.substring(responseString.indexOf("csrf_token:")));

                return pro;

            } else {
                //Closes the connection.
                response.getEntity().getContent().close();
                throw new IOException(statusLine.getReasonPhrase());

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}