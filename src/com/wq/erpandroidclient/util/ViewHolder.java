package com.wq.erpandroidclient.util;

import android.util.SparseArray;
import android.view.View;
/**
 * 改进viewholder的使用
 * @author heartinfei
 * @date 2014年1月18日\
 * public View getView(int position, View convertView, ViewGroup parent) {
 
    if (convertView == null) {
        convertView = LayoutInflater.from(context)
          .inflate(R.layout.banana_phone, parent, false);
    }
 
    ImageView bananaView = ViewHolder.get(convertView, R.id.banana);
    TextView phoneView = ViewHolder.get(convertView, R.id.phone);
 
    BananaPhone bananaPhone = getItem(position);
    phoneView.setText(bananaPhone.getPhone());
    bananaView.setImageResource(bananaPhone.getBanana());
 
    return convertView;
}
 */
public class ViewHolder {
	public static <T extends View>T get(View convertView ,int id){
		SparseArray<View> viewHolder = (SparseArray<View>) convertView.getTag();
		if(viewHolder == null){
			viewHolder = new SparseArray<View>();
			convertView.setTag(viewHolder);
		}
		View childView = viewHolder.get(id);
		if(childView == null){
			childView = convertView.findViewById(id);
			viewHolder.put(id, childView);
		}
		return (T)childView;
	}
}
