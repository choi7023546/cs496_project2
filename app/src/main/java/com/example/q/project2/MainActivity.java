package com.example.q.project2;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.AsyncTask;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.example.q.project2.Retrofit.IMyInterface;
import com.example.q.project2.Retrofit.RetroficClient;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.github.javiersantos.materialstyleddialogs.MaterialStyledDialog;
import com.rengwuxian.materialedittext.MaterialEditText;

import io.reactivex.Observer;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;


public class MainActivity extends AppCompatActivity {
    private CallbackManager callbackManager;
    LoginButton facebook_login;
    TextView text;
    Button button;
    String user = "abce";
    Button button_login;
    Button button_register;
    EditText email = null;
    EditText password = null;
    TextFileManager mTextFileMgr = new TextFileManager();

    CompositeDisposable compositeDisposable = new CompositeDisposable();
    IMyInterface iMyInterface;

    @Override
    protected void onStop() {
        compositeDisposable.clear();
        super.onStop();
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //facebook login service
        FacebookSdk.sdkInitialize(getApplicationContext());

        AppEventsLogger.activateApp(this);
        callbackManager = CallbackManager.Factory.create();

        facebook_login = findViewById(R.id.facebook_login_button);

        facebook_login.setReadPermissions("email");

        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {

                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        // App code
                        Log.d("TAG","onSucces LoginResult="+loginResult);
                    }

                    @Override
                    public void onCancel() {
                        // App code
                        Log.d("TAG","onCancel");
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        // App code
                        Log.d("TAG","onError");
                    }
                });

        // own log-in service
        Retrofit retrofitClient = RetroficClient.getInstance();
        iMyInterface = retrofitClient.create(IMyInterface.class);


        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        button_login = findViewById(R.id.login);
        button_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                final String strEmail = email.getText().toString();
//                final String strPassword = password.getText().toString();
//                try {
//                    JSONObject user = mTextFileMgr.save(strEmail, strPassword);
//                    Log.d("LOG", user.toString());
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//                email.setText("");
//                password.setText("");
////                Intent intent = new Intent(MainActivity.this, main_app.class);
////                startActivity(intent);
                try {
                    loginUser(email.getText().toString(), password.getText().toString());
                    email.setText("");
                    password.setText("");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        button_register = findViewById(R.id.register);
        button_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final View register_layout = LayoutInflater.from(MainActivity.this)
                        .inflate(R.layout.register_layout, null);
                new MaterialStyledDialog.Builder(MainActivity.this)
                        .setIcon(R.drawable.ic_home_black_24dp)
                        .setTitle("REGISTRATION")
                        .setDescription("Please fill all fields")
                        .setCustomView(register_layout)
                        .setNegativeText("CANCEL")
                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                dialog.dismiss();
                            }
                        })
                        .setPositiveText("REGISTER")
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                MaterialEditText edit_email = register_layout.findViewById(R.id.email);
                                MaterialEditText edit_name = register_layout.findViewById(R.id.name);
                                MaterialEditText edit_password = register_layout.findViewById(R.id.password);

                                if(TextUtils.isEmpty(edit_email.getText().toString())) {
                                    Toast.makeText(MainActivity.this, "Email cannot be null or empty", Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                if(TextUtils.isEmpty(edit_name.getText().toString())) {
                                    Toast.makeText(MainActivity.this, "Name cannot be null or empty", Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                if(TextUtils.isEmpty(edit_password.getText().toString())) {
                                    Toast.makeText(MainActivity.this, "Password cannot be null or empty", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                
                                registerUser(edit_email.getText().toString(), edit_name.getText().toString(), edit_password.getText().toString());
                            }
                        }).show();

            }
        });

    }

    private void registerUser(String email, String name, String password) {

        compositeDisposable.add(iMyInterface.registerUser(email, name, password)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {

                    @Override
                    public void accept(String response) throws Exception {
                        Toast.makeText(MainActivity.this, ""+response, Toast.LENGTH_SHORT).show();
                    }
                }));
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        final Intent intent = new Intent(this, main_app.class);
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        GraphRequest request = GraphRequest.newMeRequest(
                accessToken,
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        // Insert your code here
//                        text = findViewById(R.id.textview);
//                        text.setText(object.toString());
                        if(object != null) {
                            user = object.toString();
                            intent.putExtra("user_info", user);
                            startActivity(intent);
                        }


                    }
                });

        Bundle parameters = new Bundle();
        parameters.putString("fields", "email");
        request.setParameters(parameters);
        request.executeAsync();


    }


    @Override
    protected void onResume() {
        super.onResume();
        AppEventsLogger.activateApp(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        AppEventsLogger.deactivateApp(this);
    }

    private void loginUser(final String email, String password) throws JSONException {
        if(TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Email cannot be null or empty", Toast.LENGTH_SHORT).show();
            return;
        }

        if(TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Password cannot be null or empty", Toast.LENGTH_SHORT).show();
            return;
        }
        compositeDisposable.add(iMyInterface.loginUser(email, password)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Consumer<String>() {

            @Override
            public void accept(String response) throws Exception {
                Toast.makeText(MainActivity.this, ""+response, Toast.LENGTH_SHORT).show();
                JSONObject information = new JSONObject();
                information.put("email", email);
                Intent intent = new Intent(MainActivity.this, main_app.class);
                intent.putExtra("user_info", information.toString());
                startActivity(intent);
            }
        }));

    }
}
