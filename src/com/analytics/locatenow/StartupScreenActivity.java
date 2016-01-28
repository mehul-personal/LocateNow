package com.analytics.locatenow;

import android.app.ActionBar;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.TypefaceSpan;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class StartupScreenActivity extends FragmentActivity{
	ViewPager slider;
	CirclePageIndicator circleIndicator;
	Button skip,next;
	static int counter=0;
	Typeface mediumFont, boldFont, regularFont, semiboldFont;
	
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.startup_layout);
		
		ActionBar actionBar = getActionBar();
		actionBar.setBackgroundDrawable(new ColorDrawable(getResources()
				.getColor(R.color.myPrimaryColor)));
				actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setHomeButtonEnabled(true);
		
		SpannableString s = new SpannableString("Welcome to Locate Now");
		s.setSpan(new TypefaceSpan("Lato-Medium.ttf"), 0, s.length(),
				Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		getActionBar().setTitle(s);
		
		slider=(ViewPager) findViewById(R.id.view_pager);
		circleIndicator=(CirclePageIndicator) findViewById(R.id.indicator);
		skip=(Button) findViewById(R.id.btnSkip);
		next=(Button) findViewById(R.id.btnNext);
		
		SharedPreferences mPrefs = getSharedPreferences(
				"INSTALL_DETAIL", MODE_PRIVATE);
		if(mPrefs.getString("DETAIL", "NOTINSTALL").equals("INSTALL")){
			Intent i=new Intent(StartupScreenActivity.this, WelcomePageActivity.class);
			startActivity(i);
			finish();
		}
		
		
		
		StartupImageAdapter adapter = new StartupImageAdapter(getSupportFragmentManager(), this);
		
		mediumFont = Typeface.createFromAsset(getAssets(), "Lato-Medium.ttf");
		boldFont = Typeface.createFromAsset(getAssets(), "Lato-Bold.ttf");
		regularFont = Typeface.createFromAsset(getAssets(), "Lato-Regular.ttf");
		semiboldFont = Typeface.createFromAsset(getAssets(),
				"Lato-Semibold.ttf");
		skip.setTypeface(boldFont);
		next.setTypeface(boldFont);
		
		slider.setCurrentItem(counter);
		slider.setAdapter(adapter);
         circleIndicator.setViewPager(slider);
        slider.setOnPageChangeListener(new OnPageChangeListener() {
			
			@Override
			public void onPageSelected(int arg0) {
				// TODO Auto-generated method stub
				Log.e(" onPageSelected postion",""+arg0);
				circleIndicator.setCurrentItem(arg0);
				counter=arg0;
			}
			
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub
				Log.e(" onPageScrollStateChanged postion",""+arg0);
			}
		});
         
        
         skip.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				SharedPreferences mPrefs = getSharedPreferences(
						"INSTALL_DETAIL", MODE_PRIVATE);
				Editor edit=mPrefs.edit();
				edit.putString("DETAIL", "INSTALL");
				edit.commit();
				
				Intent i=new Intent(StartupScreenActivity.this, WelcomePageActivity.class);
				startActivity(i);
				finish();
			}
		});
         next.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				counter++;
				if(counter==3){
					SharedPreferences mPrefs = getSharedPreferences(
							"INSTALL_DETAIL", MODE_PRIVATE);
					Editor edit=mPrefs.edit();
					edit.putString("DETAIL", "INSTALL");
					edit.commit();
					
					Intent i=new Intent(StartupScreenActivity.this, WelcomePageActivity.class);
					startActivity(i);
					finish();
				}
				slider.setCurrentItem(counter);
				
			}
		});
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case android.R.id.home:
			onBackPressed();
			return true;
		}

		return super.onOptionsItemSelected(item);
	}
}
