package com.example.qingzhong.bluemixphototshare.bluemixutil;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.Toast;

import com.example.qingzhong.bluemixphototshare.Constants.IdentityCons;
import com.example.qingzhong.bluemixphototshare.MainActivity;
import com.ibm.mobile.services.core.IBMBluemix;
import com.ibm.mobile.services.data.IBMData;
import com.ibm.mobile.services.data.IBMDataClient;
import com.ibm.mobile.services.data.IBMDataClientManager;
import com.ibm.mobile.services.data.IBMDataFile;
import com.ibm.mobile.services.data.IBMDataFileException;
import com.ibm.mobile.services.data.IBMDataFileSystem;
import com.ibm.mobile.services.data.file.IBMFileSync;
import com.ibm.mobile.services.push.IBMPush;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONObject;

import java.util.Date;
import java.util.HashMap;

import bolts.Continuation;
import bolts.Task;

/**
 * Created by qingzhong on 26/7/15.
 */
public class BluemixUtl {

    private static  IBMData ibmData;
    private  static IBMFileSync ibmFileSync;
    private static IBMDataClientManager ibmDataClientManager;
    private static IBMDataFileSystem syncedFiles ;
    private static boolean downloaded=false;
    private static Bitmap bitmap;
    public static IBMPush push;

   // private static IBMPushNotificationListener pushNotificationListener;
    private  static String filename;
    //private static boolean init=false;

    public static void initBluemixService(final Context context) {

        IBMBluemix.initialize(context, IdentityCons.appid, IdentityCons.appsecret, IdentityCons.approute);
        ibmData=IBMData.initializeService();
        ibmFileSync=IBMFileSync.initializeService();
        ibmDataClientManager= IBMDataClient.getManager(context);
        ibmDataClientManager.registerCallback((MainActivity)context);
        syncedFiles=ibmDataClientManager.getFileSystem();
        HashMap para=new HashMap();
        para.put(IBMDataClient.IBMFileLiveSyncEnableKey,Boolean.TRUE);


        //push

        push= IBMPush.initializeService();
        push.register("zhong qing device","123").continueWith(new Continuation<String, Void>() {
            @Override
            public Void then(Task<String> stringTask) throws Exception {
                if(stringTask.isFaulted()){
                    Log.e("PUSH TAG","failed"+" "+stringTask.getResult());
                }
                else {
                    Log.e("PUSH TAG","godd~~~~~~");
                }
                return null;
            }
        });
      //  push=IBM
        ibmDataClientManager.connect(para,null).continueWith(new Continuation<IBMDataClientManager, Void>() {
            @Override
            public Void then(Task<IBMDataClientManager> ibmDataClientManagerTask) throws Exception {
                if(ibmDataClientManagerTask.isFaulted()){

                    Toast.makeText(context,"fuck wrong!",Toast.LENGTH_LONG).show();
                }
                else{


                    IdentityCons.connected=true;


                    Log.e("clientmanager connect", "good to go");


                }


                return null;
            }
        });

    }

    public  static void  uploadFile(byte[] bytes, final String comment,final Context context){

        if(!IdentityCons.connected){
            return;
        }

        IBMDataFile file=null;
        double randomFileSeed=System.currentTimeMillis()+Math.random()*10;
        filename=randomFileSeed+"."+"jpg";
        try {

            file = IBMDataFile.fileWithPath(context,filename, bytes);
            file.save().continueWith(new Continuation<IBMDataFile, Void>() {
                @Override
                public Void then(Task<IBMDataFile> ibmDataFileTask) throws Exception {
                    if(ibmDataFileTask.isFaulted()){
                        Toast.makeText(context, "fuck wrong1!", Toast.LENGTH_LONG).show();

                    }
                    else{

                        Log.e("clientmanager connect", "good to go1");

                       // Handler handler=new Handler();
                        new Thread(new Runnable() {
                            @Override
                            public void run() {

                                try {
//                                    URL url = new URL(Connection.insertURL);
//                                    HttpURLConnection connection=(HttpURLConnection)url.openConnection();
//                                    connection.setRequestMethod("POST");
//                                    connection.setDoInput(true);
//                                    connection.setDoOutput(true);
//                                    connection.setUseCaches(false);
//                                    connection.setRequestProperty("Content-Type",
//                                            "application/json");
//                                    connection.setInstanceFollowRedirects(true);
//                                    connection.connect();
//                                    DataOutputStream out=new DataOutputStream(connection.getOutputStream());
//                                  //  out.writeBytes();
//
//
//                                    String jsonRequest=constructJSONRequest(comment,filename);
//                                    Log.e("insert",jsonRequest);
//                                    out.writeBytes(jsonRequest);
//                                    out.flush();
//                                    out.close();
//
//
//                                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
//                                    String line;
//
//                                    while ((line = reader.readLine()) != null){
//                                        Log.e("insert response",line);
//                                    }
//
//                                    reader.close();
//                                    connection.disconnect();


                                    String jsonRequest=constructJSONRequest(comment,filename);


                                    OkHttpClient client = new OkHttpClient();

                                    MediaType mediaType = MediaType.parse("application/json");
                                    RequestBody body = RequestBody.create(mediaType, "{\n    \"username\":\""+comment+"\",\n    \"photofilename\":\""+filename+"\",\n     \"date\":\"20151101\"\n}");
                                    Request request = new Request.Builder()
                                            .url("http://bluemixphotoshare.mybluemix.net/api/hello/createPost")
                                            .post(body)
                                            .addHeader("content-type", "application/json")
                                            .build();

                                    Response response = client.newCall(request).execute();



                                 //   Response response = client.newCall(request).execute();

                                    Log.e("upload result",response.body().toString());



                                }

                                catch(Exception e){
                                    e.printStackTrace();
                                }

                            }
                        }).start();



                        //database;

                    }
                    return null;
                }
            });

        }


        catch(IBMDataFileException e){
            e.printStackTrace();
            Log.e("file created",e.toString());
        }

    }



    public static Bitmap downloadBitmap(String filename){

       // Bitmap bitmap=null;
        while(!IdentityCons.connected){

        }

        //filename="ttt.jpg";

        try {
            final IBMDataFile file = syncedFiles.file(filename);
            file.fetch(IBMDataFile.IBMDATAFILE_REQUEST_POLICY.IBMDataFileRequestReturnCachedIfExists).continueWith(new Continuation<IBMDataFile, Void>() {
                @Override
                public Void then(Task<IBMDataFile> ibmDataFileTask) throws Exception {
                    if(ibmDataFileTask.isFaulted()){
                        return null;
                    }
                    else{

                        byte[] bitmapRawData=file.getContents();

                        BitmapFactory.Options bounds = new BitmapFactory.Options();
                        bounds.inSampleSize=4;
                        bitmap= BitmapFactory.decodeByteArray(bitmapRawData,0,bitmapRawData.length);
                      //  BitmapFactory.de

                    }

                    downloaded=true;
                    return null;
                }
            });
        }

        catch(Exception e){
            downloaded=true;
            e.printStackTrace();
        }


        while(!downloaded){

        }


        return bitmap;


    }


    public static String constructJSONRequest(String comment, String filename){

       // StringBuilder builder=new StringBuilder();
        try {
            JSONObject obj = new JSONObject();
            obj.put("username", comment);
            obj.put("photofilename",filename);
            obj.put("date",new Date().toString());



            return obj.toString();
        }
        catch (Exception e){
            e.printStackTrace();
        }

        return null;

    }



}
