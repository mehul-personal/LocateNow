package com.analytics.locatenow;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class StartupImageAdapter extends FragmentPagerAdapter{

	

	public Context ctx;

	public StartupImageAdapter(FragmentManager fm,Context ctx) {
		super(fm);
		this.ctx=ctx;
	}

	public Integer[] mThumbIds = {
			R.drawable.bg_first_intro_screen, R.drawable.bg_second_intro_screen,
			R.drawable.bg_third_intro_screen
	};

	@Override
	public Fragment getItem(int i) {
		Bundle args = new Bundle();
		args.putInt("image", mThumbIds[i]);

		SingleViewFragment fragment = new SingleViewFragment();
		fragment.setArguments(args);

		return fragment;
	}

	@Override
	public int getCount() {
		return mThumbIds.length;
	}
}