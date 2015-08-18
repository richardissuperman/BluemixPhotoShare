package com.example.qingzhong.bluemixphototshare;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;

import com.example.qingzhong.bluemixphototshare.Constants.Connection;
import com.example.qingzhong.bluemixphototshare.bluemixutil.BluemixUtl;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by qingzhong on 23/7/15.
 */
public class SyncTask extends AsyncTask<String,Integer,ArrayList<UserDataModel>> {


    public String connectionURL= Connection.getURL;

    private RecyclerViewAdapter adapter;
    public SyncTask(RecyclerViewAdapter adapter){
        this.adapter=adapter;

    }


    @Override
    protected ArrayList<UserDataModel> doInBackground(String... params) {

        BufferedReader reader;

        try {
            URL url = new URL(this.connectionURL);
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.setRequestMethod("POST");
            connection.connect();
            InputStream inputStream=connection.getInputStream();
           // InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {

                return null;
            }

          //  Log.e("inputstream", "4");

            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {

                buffer.append(line + "\n");

            }

            Log.e("JSONResult",buffer.toString());

            JSONArray array=new JSONArray(buffer.toString());



            ArrayList<UserDataModel> tmpList=new ArrayList<UserDataModel>();


            for(int i=0;i<array.length();i++){
                JSONObject obj=array.getJSONObject(i);
                String usernmae=obj.getString("username");
                String photoname=obj.getString("photofilename");
               // String date=obj.getString("date");

                Bitmap bitmap= BluemixUtl.downloadBitmap(photoname);

                tmpList.add(new UserDataModel(usernmae, new Date(), bitmap));

              //  adapter.notifyDataSetChanged();

            }


            adapter.setDataList(tmpList);

           // ArrayList<UserDataModel> realList=adapter.g

          //  realList=tmpList;

            //adapter.getDataList()=tmpList;
        }
        catch(Exception e){
            e.printStackTrace();
        }

        return null;
    }


    @Override
    protected void onPostExecute(ArrayList<UserDataModel> object) {
        super.onPostExecute(object);


        adapter.notifyDataSetChanged();
    }
}
