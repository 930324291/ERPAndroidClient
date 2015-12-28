package com.wq.erpandroidclient;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.wq.erpandroidclient.fragment.KaoQinManagerFragment;
import com.wq.erpandroidclient.fragment.KaoQinSearchFragment;
import com.wq.erpandroidclient.fragment.YaoHuoHuiZongFragment;
import com.wq.erpandroidclient.fragment.YaoHuoJiHuaFragment;

/**
 * 考情页面
 * 
 * @author 王强
 * 
 */
public class KaoQinActivity extends FragmentActivity implements OnCheckedChangeListener, OnPageChangeListener {
	RadioGroup radioGroup;
	ViewPager viewPager;
	List<Fragment> pageData = new ArrayList<Fragment>();
	MyAdapter adapter = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.kao_qin_act_layout);
		radioGroup = (RadioGroup) this.findViewById(R.id.radioGroup);
		radioGroup.setOnCheckedChangeListener(this);
		viewPager = (ViewPager) this.findViewById(R.id.viewPager);
		viewPager.setOnPageChangeListener(this);
		adapter = new MyAdapter(this.getSupportFragmentManager());
		pageData.add(new KaoQinManagerFragment());
		pageData.add(new KaoQinSearchFragment());
		viewPager.setAdapter(adapter);

	}// end onCreate

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		// TODO Auto-generated method stub
		if (R.id.rb1 == checkedId) {
			viewPager.setCurrentItem(0);
		} else {
			viewPager.setCurrentItem(1);
		}
	}// end onCheckedChanged

	@Override
	public void onPageScrollStateChanged(int arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPageSelected(int arg0) {
		// TODO Auto-generated method stub
		if (arg0 == 0) {
			((RadioButton) radioGroup.findViewById(R.id.rb1)).setChecked(true);
		} else {
			((RadioButton) radioGroup.findViewById(R.id.rb2)).setChecked(true);
		}
	}

	/**
	 * 
	 * @author W.Q
	 * @date 2014年4月2日
	 */
	class MyAdapter extends FragmentPagerAdapter {

		public MyAdapter(FragmentManager fm) {
			super(fm);
			// TODO Auto-generated constructor stub
		}

		@Override
		public Fragment getItem(int arg0) {
			// TODO Auto-generated method stub
			return pageData.get(arg0);
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return pageData.size();
		}

	}// end MyAdapter
}
