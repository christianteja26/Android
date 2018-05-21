package id.ac.binus.boarderlognightmonitoring;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

import id.ac.binus.boarderlognightmonitoring.litehelper.LiteHelper;
import id.ac.binus.boarderlognightmonitoring.model.service.AdminResponse;
import id.ac.binus.boarderlognightmonitoring.model.service.BoarderResponse;
import id.ac.binus.boarderlognightmonitoring.model.service.ReasonResponse;
import id.ac.binus.boarderlognightmonitoring.model.service.SyncDateResponse;
import id.ac.binus.boarderlognightmonitoring.model.service.WsResponse;
import id.ac.binus.boarderlognightmonitoring.services.BoarderWs;
import id.ac.binus.boarderlognightmonitoring.services.WsListener;
import id.ac.binus.boarderlognightmonitoring.services.WsManager;
import id.ac.binus.boarderlognightmonitoring.util.Util;

public class SynchronizeActivity extends AppCompatActivity {
    ProgressDialog mProgressDialog;
    int synchData = 1;
    LiteHelper db = new LiteHelper(SynchronizeActivity.this);

    private int maxProgress;
    private int currProgress;
    private String progressPrefix;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(Util.flags);
        setContentView(R.layout.activity_synchronize);

        final TextView lblTime = (TextView) findViewById(R.id.lblTime);
        final Button btnSync = (Button) findViewById(R.id.btnSync);
        final Button btnMonitoring = (Button) findViewById(R.id.btnMonitoring);
        final CountDownTimer newtimer = new CountDownTimer(1000000000, 1000) {
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

        btnSync.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                WsManager.init(SynchronizeActivity.this);
                BoarderWs ws = new BoarderWs(SynchronizeActivity.this);
                mProgressDialog = new ProgressDialog(SynchronizeActivity.this);
                mProgressDialog.setTitle(R.string.loading);
                mProgressDialog.setMessage("Synchronizing Data ...");
                mProgressDialog.setCancelable(false);
                mProgressDialog.show();
                ws.getReasonData(new WsListener<ReasonResponse>() {
                    @Override
                    public void onSuccess(ReasonResponse response) {
                        mProgressDialog.setMessage("Synchronizing Data ..."+synchData+"/5");
                        if(synchData == 5){
                            mProgressDialog.dismiss();
                            Toast.makeText(SynchronizeActivity.this, "Synchronize Successful...", Toast.LENGTH_SHORT).show();
                            synchData = 1;
                        }
                        else
                        {
                            synchData++;
                        }
                    }

                    @Override
                    public void onError(String error) {
                        mProgressDialog.dismiss();
                        Toast.makeText(SynchronizeActivity.this, "Synchronize Failed...", Toast.LENGTH_SHORT).show();
                        synchData = 1;
                    }
                });
                ws.getAdminData(new WsListener<AdminResponse>() {
                    @Override
                    public void onSuccess(AdminResponse response) {
                        mProgressDialog.setMessage("Synchronizing Data ..."+synchData+"/5");
                        if(synchData == 5){
                            mProgressDialog.dismiss();
                            Toast.makeText(SynchronizeActivity.this, "Synchronize Successful...", Toast.LENGTH_SHORT).show();
                            synchData = 1;
                        }
                        else
                        {
                            synchData++;
                        }
                    }

                    @Override
                    public void onError(String error) {
                        mProgressDialog.dismiss();
                        Toast.makeText(SynchronizeActivity.this, "Synchronize Failed...", Toast.LENGTH_SHORT).show();
                        synchData = 1;
                    }
                });
                ws.postToGetCheckOutList(new WsListener() {
                    @Override
                    public void onSuccess(WsResponse response) {
                        mProgressDialog.setMessage("Synchronizing Data ..."+synchData+"/5");
                        if(synchData == 5){
                            mProgressDialog.dismiss();
                            Toast.makeText(SynchronizeActivity.this, "Synchronize Successful...", Toast.LENGTH_SHORT).show();
                            synchData = 1;
                        }
                        else
                        {
                            synchData++;
                        }
                    }
                    @Override
                    public void onError(String error) {
                        mProgressDialog.dismiss();
                        Toast.makeText(SynchronizeActivity.this, "Synchronize Failed...", Toast.LENGTH_SHORT).show();
                        synchData = 1;
                    }
                });
                ws.postLocalData(new WsListener() {
                    @Override
                    public void onSuccess(WsResponse response) {
                        db.open();
                        db.deleteBoarderLogReport();
                        db.close();
                        mProgressDialog.setMessage("Synchronizing Data ..."+synchData+"/5");
                        if(synchData == 5){
                            mProgressDialog.dismiss();
                            Toast.makeText(SynchronizeActivity.this, "Synchronize Successful...", Toast.LENGTH_SHORT).show();
                            synchData = 1;
                        }
                        else
                        {
                            synchData++;
                        }
                    }

                    @Override
                    public void onError(String error) {
                        mProgressDialog.dismiss();
                        Toast.makeText(SynchronizeActivity.this, "Synchronize Failed...", Toast.LENGTH_SHORT).show();
                        synchData = 1;
                    }
                });
                ws.postLastSyncDate(new WsListener<SyncDateResponse>() {
                    @Override
                    public void onSuccess(SyncDateResponse response) {
                        int totalRec = response.getTotalRec();
                        mProgressDialog.setMessage("Synchronizing Data ..."+synchData+"/5");
                        if(synchData == 5){
                            mProgressDialog.dismiss();
                            synchData = 1;
                        }
                        else
                        {
                            synchData++;
                        }
                        if (totalRec != 0)
                        {
                            hasCountData(totalRec);
                        }
                        else
                        {
                            Toast.makeText(SynchronizeActivity.this, "Synchronize Successful...", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(String error) {
                        mProgressDialog.dismiss();
                        Toast.makeText(SynchronizeActivity.this, "Synchronize Failed...", Toast.LENGTH_SHORT).show();
                        synchData = 1;
                    }
                });
            }
        });
        btnMonitoring.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WsManager.init(SynchronizeActivity.this);
                Intent intent = new Intent(SynchronizeActivity.this, TapingActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    public void startProgress (int maxprogress,  String message, String prefix) {
        this.maxProgress = maxprogress*3;
        this.currProgress = 3;
        this.progressPrefix = prefix;
        if (mProgressDialog == null)
            mProgressDialog = new ProgressDialog(SynchronizeActivity.this);
        else
            mProgressDialog.show();
        mProgressDialog.setTitle(R.string.loading);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setMessage(message);
    }

    public void incProgress () {
        this.currProgress+=3;
        this.mProgressDialog.setMessage(this.progressPrefix + this.currProgress + "/" + this.maxProgress);
        if (this.currProgress >= this.maxProgress) {
            this.mProgressDialog.dismiss();
            Toast.makeText(SynchronizeActivity.this, "Synchronize Successful...", Toast.LENGTH_SHORT).show();
        }
    }

    public void cancelProgress() {
        mProgressDialog.dismiss();
    }

    public void hasCountData(int totalRec){
        BoarderWs ws = new BoarderWs(SynchronizeActivity.this);
        final Button btnSync = (Button) findViewById(R.id.btnSync);
        //get Old Sync Date
        String tempSyncDate;
        db.open();
        Cursor cOldSyncDate = db.getLastSyncDate(1);
        try{
            tempSyncDate = cOldSyncDate.getString(cOldSyncDate.getColumnIndex(LiteHelper.KEY_SYNC_LASTSYNCDATE));
        }catch (Exception e){
            tempSyncDate = "";
        }
        final String oldSyncDate = tempSyncDate;
        db.close();
        int maxBatch = (int)Math.ceil(totalRec/3.0);
        this.startProgress(maxBatch, "Importing data", "Import Data ... ");
        for (int batch = 1; batch <= maxBatch; batch++){
            ws.postParamToGetBoarderData(new WsListener<BoarderResponse>() {
                @Override
                public void onSuccess(BoarderResponse response) {
                    SynchronizeActivity.this.incProgress();
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                    Date currSyncDate = new Date();
                    db.open();
                    db.updateLastSyncDate(1,sdf.format(currSyncDate));
                    db.insertSyncDate(1,sdf.format(currSyncDate));
                    db.close();
                }

                @Override
                public void onError(String error) {
                    SynchronizeActivity.this.cancelProgress();
                    BoarderWs.clearQueue();
                    db.open();
                    db.updateLastSyncDate(1,oldSyncDate);
                    db.close();
                    AlertDialog.Builder alert = new AlertDialog.Builder(SynchronizeActivity.this);
                    alert.setTitle("Error");
                    alert.setMessage("Unexpected Error Has Occured.");
                    alert.setNegativeButton("Retry", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            btnSync.callOnClick();
                        }
                    });
                    alert.show();
                    /*Log.e("asdasd", "onError: ");*/
                    Toast.makeText(SynchronizeActivity.this, "ERROR DOWNLOADING", Toast.LENGTH_SHORT).show();
                }
            },batch,3);
        }
    }
}
