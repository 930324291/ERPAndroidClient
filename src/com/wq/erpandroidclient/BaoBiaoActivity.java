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

import com.wq.erpandroidclient.fragment.DenDianLiRunFragment;
import com.wq.erpandroidclient.fragment.KaoQinManagerFragment;
import com.wq.erpandroidclient.fragment.KaoQinSearchFragment;
import com.wq.erpandroidclient.fragment.PeiSongLiRunFragment;
import com.wq.erpandroidclient.fragment.WuLiuLiRunFragment;

/**
 * 报表页面
 * 
 * @author 王强
 *
 */
public class BaoBiaoActivity extends FragmentActivity implements OnCheckedChangeListener, OnPageChangeListener {
	RadioGroup radioGroup;
	ViewPager viewPager;
	List<Fragment> pageData = new ArrayList<Fragment>();
	MyAdapter adapter = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.bao_biao_act_layout);
		radioGroup = (RadioGroup) this.findViewById(R.id.radioGroup);
		radioGroup.setOnCheckedChangeListener(this);
		viewPager = (ViewPager) this.findViewById(R.id.viewPager);
		viewPager.setOnPageChangeListener(this);
		adapter = new MyAdapter(this.getSupportFragmentManager());
		pageData.add(new DenDianLiRunFragment());
		pageData.add(new WuLiuLiRunFragment());
		pageData.add(new PeiSongLiRunFragment());
		viewPager.setAdapter(adapter);

	}// end onCreate

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		// TODO Auto-generated method stub
		switch(checkedId){
		case R.id.rb1:
			viewPager.setCurrentItem(0);
			break;
		case R.id.rb2:
			viewPager.setCurrentItem(1);
			break;
		case R.id.rb3:
			viewPager.setCurrentItem(2);
			break;
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
		switch(arg0){
		case 0:
			((RadioButton) radioGroup.findViewById(R.id.rb1)).setChecked(true);
			break;
		case 1:
			((RadioButton) radioGroup.findViewById(R.id.rb2)).setChecked(true);
			break;
		case 2:
			((RadioButton) radioGroup.findViewById(R.id.rb3)).setChecked(true);
			break;
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