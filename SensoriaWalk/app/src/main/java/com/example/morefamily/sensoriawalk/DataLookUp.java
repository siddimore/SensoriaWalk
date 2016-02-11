package com.example.morefamily.sensoriawalk;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by MoreFamily on 12/14/2015.
 */ public class DataLookUp {

    private String mInputFile;
    private Context mContext;
    private String mArrayName;

    public  DataLookUp()
    {

    }

    public DataLookUp(String file, String arrayName, Context pContext)
    {
        mInputFile = file;
        mArrayName = arrayName;
        mContext = pContext;
    }

//   public  ArrayList<HashMap<String, String>> convertJSONArrayToList() {

    public  ArrayList<String> convertJSONArrayToList() {

//       ArrayList<HashMap<String, String>> formList = null;
       ArrayList<String> formList = null;
       try {
           JSONObject obj = new JSONObject(loadJSONFromAsset());
           JSONArray m_jArry = obj.getJSONArray(mArrayName);
           //formList = new ArrayList<HashMap<String, String>>();
           formList = new ArrayList<String>();
           HashMap<String, String> m_li;

           for (int i = 0; i < m_jArry.length(); i++) {
               JSONObject jo_inside = m_jArry.getJSONObject(i);
               String value = jo_inside.getString("Value");
               String text = jo_inside.getString("Text");

               //Add your values in your `ArrayList` as below:
               formList.add(value);
               //m_li = new HashMap<String, String>();
               //m_li.put("Value", value);
               //m_li.put("Text", text);

               //formList.add(m_li);
           }
       } catch (JSONException e) {
           e.printStackTrace();
           return null;
       }
       return formList;
   }

       private String loadJSONFromAsset()
       {
           String json = null;
               try {
                   InputStream is = mContext.getAssets().open(mInputFile);
                   int size = is.available();
                   byte[] buffer = new byte[size];
                   is.read(buffer);
                   is.close();
                   json = new String(buffer, "UTF-8");

               }
               catch (IOException ex)
               {
                   System.out.println("Exception:"+ ex.toString());
                   return null;
               }
           return json;
       }
   }

