package com.example.qingzhong.bluemixphototshare;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.example.qingzhong.bluemixphototshare.Constants.IdentityCons;
import com.example.qingzhong.bluemixphototshare.bluemixutil.BluemixUtl;
import com.ibm.mobile.services.data.IBMData;
import com.ibm.mobile.services.data.IBMDataClientManager;
import com.ibm.mobile.services.data.IBMDataClientManagerCallback;
import com.ibm.mobile.services.data.IBMDataFileException;
import com.ibm.mobile.services.data.IBMDataFileSystem;
import com.ibm.mobile.services.data.file.IBMFileSync;
import com.ibm.mobile.services.push.IBMPushNotificationListener;
import com.ibm.mobile.services.push.IBMSimplePushNotification;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;


public class MainActivity extends ActionBarActivity  implements IBMDataClientManagerCallback{

    private SwipeRefreshLayout refreshLayout;
    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;

    private IBMData ibmData;
    private IBMFileSync ibmFileSync;
    private IBMDataClientManager ibmDataClientManager;
    private IBMDataFileSystem syncedFiles ;

    private  RecyclerViewAdapter adapter;
    private Intent sendIntent;
    private EditText editText;







    private IBMPushNotificationListener notificationListener;
    //private IBMB

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        refreshLayout=(SwipeRefreshLayout)findViewById(R.id.swiperefreshlayout);
        recyclerView=(RecyclerView)findViewById(R.id.recyclerview);
        BluemixUtl.initBluemixService(this);
        layoutManager=new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                Toast.makeText(getApplicationContext(),"f",Toast.LENGTH_LONG).show();

                Handler handler=new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        refreshLayout.setRefreshing(false);
                    }
                },2000);

                new SyncTask(adapter).execute();


            }
        });


        ArrayList<UserDataModel> list=new ArrayList<UserDataModel>();
        adapter=new RecyclerViewAdapter(list);

        recyclerView.setAdapter(adapter);

        notificationListener=new IBMPushNotificationListener() {
            @Override
            public void onReceive(IBMSimplePushNotification ibmSimplePushNotification) {

                Log.e("PUSH RESULT",ibmSimplePushNotification.toString());
                Toast.makeText(getApplicationContext(),ibmSimplePushNotification.toString(),Toast.LENGTH_SHORT).show();

            }
        };

        new SyncTask(adapter).execute();
        BluemixUtl.push.listen(this.notificationListener);


    }


    @Override
    protected void onResume() {
        super.onResume();
        if(BluemixUtl.push!=null){

            BluemixUtl.push.listen(this.notificationListener);

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            if(IdentityCons.connected) {
                startActivityForResult(intent, 1);
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onKeyValuesRemoved(String[] strings) {

    }

    @Override
    public void onKeyValuesChanged(String[] strings) {

    }

    @Override
    public void onFilesUpdated(String[] strings) {


    }


    @Override
    public void onFilesCreated(String[] strings) {

    }

    @Override
    public void onFilesRemoved(String[] strings) {

    }

    @Override
    public void onError(IBMDataFileException e) {

    }





    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);


        this.sendIntent=data;

        if(requestCode==1){

            try {

                editText=new EditText(this);



                //AlertDialog
                new AlertDialog.Builder(this).setTitle("请输入").setIcon(
                          android.R.drawable.ic_dialog_info).setView(editText).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        try {

                            String comment = editText.getText().toString();
                            InputStream inputStream = getApplicationContext().getContentResolver().openInputStream(sendIntent.getData());
                            ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
                            byte[] buff = new byte[100];
                            int rc = 0;
                            while ((rc = inputStream.read(buff, 0, 100)) > 0) {
                                swapStream.write(buff, 0, rc);
                            }
                            byte[] in2b = swapStream.toByteArray();

                            BluemixUtl.uploadFile(in2b, comment, getApplicationContext());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                }).setNegativeButton("取消", null).show();




            }

            catch(Exception e){
                e.printStackTrace();
            }

        }
    }






}
