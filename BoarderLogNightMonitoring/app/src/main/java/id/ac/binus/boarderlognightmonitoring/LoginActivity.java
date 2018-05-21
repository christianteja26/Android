package id.ac.binus.boarderlognightmonitoring;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Locale;

import id.ac.binus.boarderlognightmonitoring.litehelper.LiteHelper;
import id.ac.binus.boarderlognightmonitoring.services.WsManager;
import id.ac.binus.boarderlognightmonitoring.util.Util;

public class LoginActivity extends AppCompatActivity{
    private Button btnLogin;
    TextToSpeech tts;
    EditText txtUsername;
    EditText txtPassword;
    String uName;
    String uPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(Util.flags);
        setContentView(R.layout.activity_login);

        final LiteHelper db = new LiteHelper(this);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        txtUsername = (EditText) findViewById(R.id.txtUsername);
        txtPassword = (EditText) findViewById(R.id.txtPassword);

        tts=new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    tts.setLanguage(Locale.ENGLISH);
                }
            }
        });

        //untuk TESTING
        /*txtUsername.setText("");
        txtPassword.setText("");*/

        Util.setContext(this);
        WsManager.init(this);

        db.open();
        Cursor c = db.getAdmin(Util.AppConfigID);
        if (c.moveToFirst()){
            String valConfig = c.getString(c.getColumnIndex(LiteHelper.KEY_ADMIN_VALUE));
            String[] valArr = valConfig.split(";");
            uName = valArr[0]; uPass = valArr[1];
        }
        else {
            uName = "admin"; uPass = "admin123";
        }
        db.close();

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(String.valueOf(txtUsername.getText()).equals(uName) && String.valueOf(txtPassword.getText()).equals(uPass))
                {
                    Intent intent = new Intent(LoginActivity.this, SynchronizeActivity.class);
                    startActivity(intent);
                    finish();
                }
                else
                {
                    tts.speak("Login failed", TextToSpeech.QUEUE_FLUSH, null);
                    Toast.makeText(getBaseContext(),"LOGIN FAILED", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    protected void onPause() {
        if(tts !=null){
            tts.stop();
            tts.shutdown();
        }
        super.onPause();
    }
}
