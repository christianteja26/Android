package id.ac.binus.boarderlognightmonitoring;

import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import id.ac.binus.boarderlognightmonitoring.adapter.ReasonAdapter;
import id.ac.binus.boarderlognightmonitoring.litehelper.LiteHelper;
import id.ac.binus.boarderlognightmonitoring.model.Reason;
import id.ac.binus.boarderlognightmonitoring.services.WsManager;
import id.ac.binus.boarderlognightmonitoring.util.Util;

public class MonitoringActivity extends AppCompatActivity implements ReasonAdapter.OnSelectedRadio {
    private ListView lvReasonList;
    private Button btnSubmit;
    private Button btnBack;
    private Reason selectedReason;
    private String cRegistrationID = "";
    private String cBoarderName = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(Util.flags);
        setContentView(R.layout.activity_monitoring);

        final TextView lblTime = (TextView) findViewById(R.id.lblTime);
        final MediaPlayer soundSaved = MediaPlayer.create(this, R.raw.solemn);
        btnSubmit = (Button) findViewById(R.id.btnSubmit);
        btnBack = (Button) findViewById(R.id.btnBack);

        final String NIM = getIntent().getStringExtra("KEY_NIM");
        final String btnStatus = getIntent().getStringExtra("KEY_BTN");

        TextView txtNIM = (TextView) findViewById(R.id.txtNIM);
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

        lvReasonList = (ListView) findViewById(R.id.listReason);
        WsManager.init(this);
        final LiteHelper db = new LiteHelper(this);
        db.open();
        //--- DATABASE TRANSACTION
        Cursor cReason = db.getAllReason();
        ArrayList<Reason> reasonArrayList = new ArrayList<>();
        Reason reason ;
        if (cReason.moveToFirst())
        {
            do {
                reason = new Reason();
                reason.setReasonID(cReason.getInt(cReason.getColumnIndex(LiteHelper.KEY_REASON_REASONID)));
                reason.setReasonName(cReason.getString(cReason.getColumnIndex(LiteHelper.KEY_REASON_REASONNAME)));
                reasonArrayList.add(reason);
            } while (cReason.moveToNext());
        }
        Cursor cBoarder = db.getActiveBoarder(NIM);
        if (cBoarder.moveToFirst())
        {
            String cNIM = cBoarder.getString(cBoarder.getColumnIndex(LiteHelper.KEY_BOARDER_NIM));
            String cBoarderName = cBoarder.getString(cBoarder.getColumnIndex(LiteHelper.KEY_BOARDER_BOARDERNAME));
            cRegistrationID = cBoarder.getString(cBoarder.getColumnIndex(LiteHelper.KEY_BOARDER_REGISTRATIONID));
            txtNIM.setText(cNIM+" / "+cBoarderName);
            this.cBoarderName = cBoarderName;
        }
        db.close();
        //--- Put Data into List Adapter
        final ReasonAdapter adapter = new ReasonAdapter(reasonArrayList, MonitoringActivity.this,this);
        lvReasonList.setAdapter(adapter);

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // get selected radio button from radioGroup
                if(selectedReason.getReasonID() == 0){
                    selectedReason.setReasonName(adapter.otherReason);
                }
                if(selectedReason.getReasonName() == null || selectedReason.getReasonName().trim().equals(""))
                {
                    Toast.makeText(MonitoringActivity.this, "Please fill the reason!!", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date d = new Date();
                    String time = timeFormat.format(d);
                    db.open();
                    db.insertLogNight(cRegistrationID,time,selectedReason.getReasonName(),btnStatus);
                    db.close();
                    soundSaved.start();

                    Intent intent = new Intent(MonitoringActivity.this, DetailActivity.class);
                    intent.putExtra("KEY_NIM", NIM);
                    intent.putExtra("KEY_NAME", cBoarderName);
                    intent.putExtra("KEY_REGISTRATIONID", cRegistrationID);
                    startActivity(intent);
                }
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MonitoringActivity.this, TapingActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    public void onSelected(Reason reason) {
        selectedReason = new Reason();
        this.selectedReason = reason;
        getWindow().getDecorView().setSystemUiVisibility(Util.flags);
    }

    @Override
    public void onFinishEditing() {
        selectedReason = new Reason();
        selectedReason.setReasonID(0);
        getWindow().getDecorView().setSystemUiVisibility(Util.flags);
    }
}
