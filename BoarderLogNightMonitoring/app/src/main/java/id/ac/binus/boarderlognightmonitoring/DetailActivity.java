package id.ac.binus.boarderlognightmonitoring;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import id.ac.binus.boarderlognightmonitoring.litehelper.LiteHelper;
import id.ac.binus.boarderlognightmonitoring.util.Util;

public class DetailActivity extends AppCompatActivity {
    private ImageView imgPhoto;
    private TextView popNIM;
    private TextView popName;
    private TextView popCheckOut;
    private TextView popCheckIn;
    private TextView popReason;
    private static int SPLASH_TIME_OUT = 5000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().getDecorView().setSystemUiVisibility(Util.flags);
        setContentView(R.layout.activity_detail);

        final LiteHelper db = new LiteHelper(this);
        final String pxNIM = getIntent().getStringExtra("KEY_NIM");
        final String pxBoarderName = getIntent().getStringExtra("KEY_NAME");
        final String pxRegistrationID = getIntent().getStringExtra("KEY_REGISTRATIONID");
        String CheckOutTemp = "";
        String CheckInTemp = "";
        String ReasonTemp = "";

        imgPhoto = (ImageView) findViewById(R.id.imgPhoto);
        popNIM = (TextView) findViewById(R.id.popNIM);
        popName = (TextView) findViewById(R.id.popName);
        popCheckIn = (TextView) findViewById(R.id.popCheckIn);
        popCheckOut = (TextView) findViewById(R.id.popCheckOut);
        popReason = (TextView) findViewById(R.id.popReason);

        db.open();
        Cursor cBoarderPhoto = db.getActiveBoarderPhotoByNIM(pxNIM);
        String cTagBoarderString = "";
        if (cBoarderPhoto.moveToFirst())
        {
            cTagBoarderString = cBoarderPhoto.getString(cBoarderPhoto.getColumnIndex(LiteHelper.KEY_BOARDER_PHOTO));
        }
        Cursor cLogNight = db.getLogNightByRegistrationID(pxRegistrationID);
        if (cLogNight.moveToLast())
        {
            CheckOutTemp = cLogNight.getString(cLogNight.getColumnIndex(LiteHelper.KEY_LOG_CHECKOUTDATE));
            CheckInTemp = cLogNight.getString(cLogNight.getColumnIndex(LiteHelper.KEY_LOG_CHECKINDATE));
            ReasonTemp = cLogNight.getString(cLogNight.getColumnIndex(LiteHelper.KEY_LOG_REASONNAME));
        }
        db.close();

        byte[] blob = Base64.decode(cTagBoarderString,0);
        imgPhoto.setImageBitmap(BitmapFactory.decodeByteArray(blob,0,blob.length));
        imgPhoto.invalidate();
        popNIM.setText(pxNIM);
        popName.setText(pxBoarderName);
        popCheckOut.setText("Check Out : " + (CheckOutTemp != null ? CheckOutTemp : "-"));
        popCheckIn.setText("Check In : " + (CheckInTemp != null ? CheckInTemp : "-"));
        popReason.setText(ReasonTemp);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        }, SPLASH_TIME_OUT);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Intent intent = new Intent(DetailActivity.this, TapingActivity.class);
        startActivity(intent);
        finish();
    }
}
