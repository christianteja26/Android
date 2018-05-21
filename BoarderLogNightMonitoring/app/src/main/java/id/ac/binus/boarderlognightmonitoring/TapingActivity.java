package id.ac.binus.boarderlognightmonitoring;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import id.ac.binus.boarderlognightmonitoring.litehelper.LiteHelper;
import id.ac.binus.boarderlognightmonitoring.util.Util;

public class TapingActivity extends AppCompatActivity {
    private Button btnIn;
    private Button btnOut;
    private Button btnClear;
    private Button btnHome;
    private NfcAdapter mNfcAdapter;
    private ImageView tapingPhoto;
    TextToSpeech tts;
    EditText txtNIM;
    String NIM;
    LiteHelper db = new LiteHelper(TapingActivity.this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(Util.flags);
        setContentView(R.layout.activity_taping);

        final TextView lblTime = (TextView) findViewById(R.id.lblTime);
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        btnIn = (Button) findViewById(R.id.btnIn);
        btnOut = (Button) findViewById(R.id.btnOut);
        btnClear = (Button) findViewById(R.id.btnClear);
        btnHome = (Button) findViewById(R.id.btnHome);
        txtNIM = (EditText) findViewById(R.id.txtNIM);
        tapingPhoto = (ImageView) findViewById(R.id.tapingPhoto);

        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    tts.setLanguage(Locale.ENGLISH);
                }
            }
        });

        CountDownTimer newtimer = new CountDownTimer(1000000000, 1000) {
            public void onTick(long millisUntilFinished) {
                SimpleDateFormat sdf = new SimpleDateFormat("EEEE, MMMM dd, yyyy  -  HH:mm:ss");
                Date d = new Date();
                String dayOfTheWeek = sdf.format(d);
                lblTime.setText("Current time: "+dayOfTheWeek);
            }
            public void onFinish() {
                onTick(1000000000);
            }
        };
        newtimer.start();

        if (mNfcAdapter != null && mNfcAdapter.isEnabled()){

        }
        else{
            Toast.makeText(this, "Please TURN ON Your NFC !!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS));
        }

        btnOut.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(NIM == null)
                {
                    Toast.makeText(TapingActivity.this, "Please Tap Your Binusian Flazz Card!", Toast.LENGTH_SHORT).show();
                }
                else {
                    Intent intent = new Intent(TapingActivity.this, MonitoringActivity.class);
                    intent.putExtra("KEY_NIM", NIM);
                    intent.putExtra("KEY_BTN", "OUT");
                    startActivity(intent);
                    finish();
                }
            }
        });

        btnIn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if (NIM == null)
                {
                    Toast.makeText(TapingActivity.this, "Please Tap Your Binusian Flazz Card!", Toast.LENGTH_SHORT).show();
                }
                else if (isDoubleCheckIn(NIM))
                {
                    Toast.makeText(TapingActivity.this, "Cannot Double Check-in Within a Period", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Intent intent = new Intent(TapingActivity.this, MonitoringActivity.class);
                    intent.putExtra("KEY_NIM", NIM);
                    intent.putExtra("KEY_BTN", "IN");
                    startActivity(intent);
                    finish();
                }
            }
        });

        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtNIM.setText("");
                Toast.makeText(TapingActivity.this, "NIM Cleared...", Toast.LENGTH_SHORT).show();
                NIM = null;
            }
        });

        btnHome.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                NIM = null;
                Intent intent = new Intent(TapingActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
    @Override
    protected void onResume() {
        super.onResume();
        getWindow().getDecorView().setSystemUiVisibility(Util.flags);
        Intent intent = getIntent();
        String action = intent.getAction();
        final MediaPlayer soundError = MediaPlayer.create(this, R.raw.restricted);
        final MediaPlayer soundSuccess = MediaPlayer.create(this, R.raw.success);

        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
            if(status != TextToSpeech.ERROR) {
                tts.setLanguage(Locale.ENGLISH);
            }
            }
        });

        txtNIM.setOnEditorActionListener(new EditText.OnEditorActionListener(){
            @Override
            public boolean onEditorAction(TextView v, int actionID, KeyEvent event){
            if(actionID == EditorInfo.IME_ACTION_DONE){
                db.open();
                NIM = txtNIM.getText().toString().trim();
                Cursor cBoarder = db.getActiveBoarder(NIM);
                if(cBoarder.moveToFirst()){
                    NIM = cBoarder.getString(cBoarder.getColumnIndex(LiteHelper.KEY_BOARDER_NIM));
                    soundSuccess.start();
                    pushImage(NIM);
                    InputMethodManager imm = (InputMethodManager)v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    checkInValidate(NIM);
                }else{
                    soundError.start();
                }
                db.close();
                return true;
            }
            return false;
            }
        });

        if(mNfcAdapter.ACTION_TAG_DISCOVERED.equals(action)){
            Tag tag = intent.getParcelableExtra(mNfcAdapter.EXTRA_TAG);
            if(tag == null)
            {
                txtNIM.setText("");
            }
            else
            {
                byte[] tagId = tag.getId();
                db.open();
                Cursor cTagBoarder = db.getActiveBoarderByTagId(Util.bytesToHexString(tagId));
                String cTagNameString = "";
                String viewBoarderName = "";
                if (cTagBoarder.moveToFirst())
                {
                    NIM = cTagBoarder.getString(cTagBoarder.getColumnIndex(LiteHelper.KEY_BOARDER_NIM));
                    cTagNameString = cTagBoarder.getString(cTagBoarder.getColumnIndex(LiteHelper.KEY_BOARDER_BOARDERNAME));
                }
                if(cTagNameString.contains(" "))
                {
                    viewBoarderName = cTagNameString.substring(0, cTagNameString.indexOf(" ")+2);
                    viewBoarderName = viewBoarderName + ".";
                }
                if (NIM != null)
                {
                    soundSuccess.start();
                    pushImage(NIM);
                    txtNIM.setText(NIM + " / " + viewBoarderName);
                }
                else
                {
                    soundError.start();
                    Toast.makeText(this, "Card Is Not Registered...", Toast.LENGTH_SHORT).show();
                }
                checkInValidate (NIM);
                db.close();
            }
        }
        else{
            Toast.makeText(this, "Please TAP Your Binusian Flazz Card...", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onPause() {
        if(tts !=null){
            tts.stop();
            tts.shutdown();
        }
        super.onPause();
    }

    public void speech()
    {
        tts.speak("Fire In The Hole", TextToSpeech.QUEUE_FLUSH, null);
    }

    public void pushImage (String NIM){
        db.open();
        Cursor cBoarderPhoto = db.getActiveBoarderPhotoByNIM(NIM);
        String cTagBoarderString = "";
        if (cBoarderPhoto.moveToFirst())
        {
            cTagBoarderString = cBoarderPhoto.getString(cBoarderPhoto.getColumnIndex(LiteHelper.KEY_BOARDER_PHOTO));
        }
        db.close();

        byte[] blob = Base64.decode(cTagBoarderString,0);
        tapingPhoto.setImageBitmap(BitmapFactory.decodeByteArray(blob,0,blob.length));
        tapingPhoto.invalidate();
    }

    public void checkInValidate (String NIM){
        db.open();
        Cursor cBoarder = db.getActiveBoarder(NIM);
        if (cBoarder.moveToFirst())
        {
            String cTagRegistrationID = cBoarder.getString(cBoarder.getColumnIndex(LiteHelper.KEY_BOARDER_REGISTRATIONID));
            String cBoarderName = cBoarder.getString(cBoarder.getColumnIndex(LiteHelper.KEY_BOARDER_BOARDERNAME));

            //GET CURRENT TIME
            SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date d = new Date();
            String time = timeFormat.format(d);

            //AUTO CHECK-IN WHILE CHECK-OUT DATA != null
            Cursor cLogNight = db.getLogNightByRegistrationID(cTagRegistrationID);
            if (cLogNight.moveToLast())
            {
                String checkOutDate = cLogNight.getString(cLogNight.getColumnIndex(LiteHelper.KEY_LOG_CHECKOUTDATE));
                String checkInDate = cLogNight.getString(cLogNight.getColumnIndex(LiteHelper.KEY_LOG_CHECKINDATE));
                String logNightID = cLogNight.getString(cLogNight.getColumnIndex(LiteHelper.KEY_LOG_LOGID));
                long diff = 0;

                //Time Difference
                try {
                    Date date1 = timeFormat.parse(checkOutDate);
                    Date date2 = timeFormat.parse(time);
                    diff = (date2.getTime() - date1.getTime())/1000/60/60;
                }catch (Exception ex){
                    ex.printStackTrace();
                }

                if (checkOutDate != null && checkInDate == null && diff <= 12)
                {
                    // Update DB
                    db.updateCheckInLogNight(logNightID,time);
                    // Show POP UP
                    Intent intent = new Intent(TapingActivity.this, DetailActivity.class);
                    intent.putExtra("KEY_NIM", NIM);
                    intent.putExtra("KEY_NAME", cBoarderName);
                    intent.putExtra("KEY_REGISTRATIONID", cTagRegistrationID);
                    startActivity(intent);
                }
            }
        }
        db.close();
    }

    // Added: Juvita
    public boolean isDoubleCheckIn(String NIM) {
        db.open();
        Cursor cBoarder = db.getActiveBoarder(NIM);
        if (cBoarder.moveToFirst())
        {
            String cTagRegistrationID = cBoarder.getString(cBoarder.getColumnIndex(LiteHelper.KEY_BOARDER_REGISTRATIONID));
            String cBoarderName = cBoarder.getString(cBoarder.getColumnIndex(LiteHelper.KEY_BOARDER_BOARDERNAME));

            //GET CURRENT TIME
            SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date d = new Date();
            String time = timeFormat.format(d);

            //AUTO CHECK-IN WHILE CHECK-OUT DATA != null
            Cursor cLogNight = db.getLogNightByRegistrationID(cTagRegistrationID);
            if (cLogNight.moveToLast())
            {
                String checkOutDate = cLogNight.getString(cLogNight.getColumnIndex(LiteHelper.KEY_LOG_CHECKOUTDATE));
                String checkInDate = cLogNight.getString(cLogNight.getColumnIndex(LiteHelper.KEY_LOG_CHECKINDATE));
                String logNightID = cLogNight.getString(cLogNight.getColumnIndex(LiteHelper.KEY_LOG_LOGID));
                long diff = 0;

                //Time Difference
                try {
                    Date date1 = timeFormat.parse(checkOutDate);
                    Date date2 = timeFormat.parse(time);
                    diff = (date2.getTime() - date1.getTime())/1000/60/60;
                }catch (Exception ex){
                    ex.printStackTrace();
                }

                db.close();

                if (checkInDate != null && diff <= 12) {
                    return true;
                }
            }
        }

        return false;
    }


}
