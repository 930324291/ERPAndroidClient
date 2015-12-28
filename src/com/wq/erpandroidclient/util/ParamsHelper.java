package com.wq.erpandroidclient.util;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;

/**
 * 
 * @author W.Q
 * @Date 2014年6月13日
 */
public class ParamsHelper {

	/**
	 * args 个数必须是偶数 ， 奇数是key，偶数是value
	 * 
	 * @param args
	 * @return
	 */
	public static final List<ContentValues> convert(Object... args) {
		List<ContentValues> res = new ArrayList<ContentValues>();
		ContentValues value = null;
		for (int i = 0; i < args.length; i += 2) {
			value = new ContentValues();
			if (args[i + 1] instanceof Integer) {
				value.put((String) args[i], (Integer) args[i + 1]);
			} else {
				value.put((String) args[i], (String) args[i + 1]);
			}

			res.add(value);
		}
		return res;
	}
}
