package com.wq.erpandroidclient.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;

/**
 * 解析JSON 数据
 * @author 王强
 *
 */
public class JSONParser {
	/**
	 * 将json对象转换为ContentValues
	 * @param json
	 * @return
	 */
	public ContentValues convertJsonToContentValue(JSONObject json){
		ContentValues value=new ContentValues();
		try {
			@SuppressWarnings("unchecked")
			Iterator<String> iterator = json.keys();
			while (iterator.hasNext()) {
				String key = iterator.next();
				value.put(key, json.getString(key));
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		return value.valueSet().size() > 0 ? value : null;
	}//end convertJsonToContentValue
	
	public List<ContentValues> convertJSONArrayToList(JSONArray jsonArray){
		return convertJSONArrayToList(jsonArray, 0);
	}
	
	/**
	 * 将一个JSONArray 转化为 List<ContentValue>
	 * @param jsonArray
	 * @return
	 */
	public List<ContentValues> convertJSONArrayToList(JSONArray jsonArray,int index){
		List<ContentValues> res=new ArrayList<ContentValues>();
		ContentValues value=null;
		try {
			for(int i=index;i<jsonArray.length();i++){
				value=convertJsonToContentValue(jsonArray.getJSONObject(i));
				if(value!=null){
					res.add(value);
				}
			}//end for
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return res.size()>0?res:null;
		}
		return res.size()>0?res:null;
	}//end convertJSONArrayToList
	
	
}
