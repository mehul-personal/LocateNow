package com.analytics.locatenow;

import java.util.ArrayList;
import java.util.List;

import com.analytics.locatenow.MainActivity.CircleTransform;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class NavigationDrawerFragment extends Fragment implements
		NavigationDrawerCallbacks {
	private static final String PREF_USER_LEARNED_DRAWER = "navigation_drawer_learned";
	private static final String STATE_SELECTED_POSITION = "selected_navigation_drawer_position";
	private static final String PREFERENCES_FILE = "my_app_settings"; // TODO:
																		// change
																		// this
																		// to
																		// your
																		// file
	private NavigationDrawerCallbacks mCallbacks;
	private RecyclerView mDrawerList;
	private View mFragmentContainerView;
	private DrawerLayout mDrawerLayout;
	private ActionBarDrawerToggle mActionBarDrawerToggle;
	private boolean mUserLearnedDrawer;
	private boolean mFromSavedInstanceState;
	private int mCurrentSelectedPosition;

	ImageView profileImage;
	TextView userName, userEmail;
Context mcontext;
Typeface mediumFont, boldFont;
	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_navigation_google,
				container, false);
		mcontext=getActivity();
		mediumFont = Typeface.createFromAsset(mcontext.getAssets(), "Lato-Medium.ttf");
		boldFont = Typeface.createFromAsset(mcontext.getAssets(), "Lato-Bold.ttf");
		mDrawerList = (RecyclerView) view.findViewById(R.id.drawerList);
		LinearLayoutManager layoutManager = new LinearLayoutManager(
				getActivity());
		layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
		mDrawerList.setLayoutManager(layoutManager);
		// mDrawerList.setHasFixedSize(true);
		profileImage = (ImageView) view.findViewById(R.id.imgAvatar);
		userName = (TextView) view.findViewById(R.id.txtUsername);
		userEmail = (TextView) view.findViewById(R.id.txtUserEmail);
		final List<NavigationItem> navigationItems = getMenu();
		NavigationDrawerAdapter adapter = new NavigationDrawerAdapter(
				navigationItems,mcontext);
		adapter.setNavigationDrawerCallbacks(this);
		mDrawerList.setAdapter(adapter);
		selectItem(mCurrentSelectedPosition);

		SharedPreferences mPrefs = getActivity().getSharedPreferences(
				"LOGIN_DETAIL", getActivity().MODE_PRIVATE);
		userName.setText("" + mPrefs.getString("NAME", ""));
		userEmail.setText("" + mPrefs.getString("EMAIL", ""));
		userName.setTypeface(boldFont);
		userEmail.setTypeface(mediumFont);
		if (!mPrefs.getString("IMAGE", "").isEmpty()) {
			Picasso.with(getActivity()).load(mPrefs.getString("IMAGE", ""))
					.transform(new CircleTransform()).into(profileImage);
		}
		return view;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mUserLearnedDrawer = Boolean.valueOf(readSharedSetting(getActivity(),
				PREF_USER_LEARNED_DRAWER, "false"));
		if (savedInstanceState != null) {
			mCurrentSelectedPosition = savedInstanceState
					.getInt(STATE_SELECTED_POSITION);
			mFromSavedInstanceState = true;
		}
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mCallbacks = (NavigationDrawerCallbacks) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(
					"Activity must implement NavigationDrawerCallbacks.");
		}
	}

	public ActionBarDrawerToggle getActionBarDrawerToggle() {
		return mActionBarDrawerToggle;
	}

	public void setActionBarDrawerToggle(
			ActionBarDrawerToggle actionBarDrawerToggle) {
		mActionBarDrawerToggle = actionBarDrawerToggle;
	}

	public class CircleTransform implements Transformation {
		@Override
		public Bitmap transform(Bitmap source) {
			int size = Math.min(source.getWidth(), source.getHeight());

			int x = (source.getWidth() - size) / 2;
			int y = (source.getHeight() - size) / 2;

			Bitmap squaredBitmap = Bitmap
					.createBitmap(source, x, y, size, size);
			if (squaredBitmap != source) {
				source.recycle();
			}

			Bitmap bitmap = Bitmap.createBitmap(size, size, source.getConfig());

			Canvas canvas = new Canvas(bitmap);
			Paint paint = new Paint();
			BitmapShader shader = new BitmapShader(squaredBitmap,
					BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP);
			paint.setShader(shader);
			paint.setAntiAlias(true);

			float r = size / 2f;
			canvas.drawCircle(r, r, r, paint);

			squaredBitmap.recycle();
			return bitmap;
		}

		@Override
		public String key() {
			return "circle";
		}
	}

	public void setup(int fragmentId, DrawerLayout drawerLayout, Toolbar toolbar) {
		mFragmentContainerView = getActivity().findViewById(fragmentId);
		if (mFragmentContainerView.getParent() instanceof ScrimInsetsFrameLayout) {
			mFragmentContainerView = (View) mFragmentContainerView.getParent();
		}
		mDrawerLayout = drawerLayout;
		mDrawerLayout.setStatusBarBackgroundColor(getResources().getColor(
				R.color.myPrimaryDarkColor));

		mActionBarDrawerToggle = new ActionBarDrawerToggle(getActivity(),
				mDrawerLayout, toolbar, R.string.drawer_open,
				R.string.drawer_close) {
			@Override
			public void onDrawerClosed(View drawerView) {
				super.onDrawerClosed(drawerView);
				if (!isAdded())
					return;
				getActivity().invalidateOptionsMenu();
			}

			@Override
			public void onDrawerOpened(View drawerView) {
				super.onDrawerOpened(drawerView);
				if (!isAdded())
					return;
				if (!mUserLearnedDrawer) {
					mUserLearnedDrawer = true;
					saveSharedSetting(getActivity(), PREF_USER_LEARNED_DRAWER,
							"true");
				}

				getActivity().invalidateOptionsMenu();
			}
		};

		if (!mUserLearnedDrawer && !mFromSavedInstanceState)
			mDrawerLayout.openDrawer(mFragmentContainerView);

		mDrawerLayout.post(new Runnable() {
			@Override
			public void run() {
				mActionBarDrawerToggle.syncState();
			}
		});

		mDrawerLayout.setDrawerListener(mActionBarDrawerToggle);
	}

	public void openDrawer() {
		mDrawerLayout.openDrawer(mFragmentContainerView);
	}

	public void closeDrawer() {
		mDrawerLayout.closeDrawer(mFragmentContainerView);
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mCallbacks = null;
	}

	@SuppressWarnings("deprecation")
	public List<NavigationItem> getMenu() {
		List<NavigationItem> items = new ArrayList<NavigationItem>();
		items.add(new NavigationItem("Home", getResources().getDrawable(
				R.drawable.ic_home)));
		items.add(new NavigationItem("Share Location", getResources()
				.getDrawable(R.drawable.ic_share_location)));
		items.add(new NavigationItem("Current Location", getResources()
				.getDrawable(R.drawable.ic_current_location)));
		items.add(new NavigationItem("Save Location", getResources()
				.getDrawable(R.drawable.ic_set_location)));
		items.add(new NavigationItem("Messages", getResources().getDrawable(
				R.drawable.ic_messages)));
		items.add(new NavigationItem("Chat", getResources().getDrawable(
				R.drawable.ic_chat)));
		items.add(new NavigationItem("History", getResources().getDrawable(
				R.drawable.ic_history)));
		items.add(new NavigationItem("Sign Out", getResources().getDrawable(
				R.drawable.ic_logout)));
		return items;
	}

	/**
	 * Changes the icon of the drawer to back
	 */
	public void showBackButton() {
		if (getActivity() instanceof ActionBarActivity) {
			((ActionBarActivity) getActivity()).getSupportActionBar()
					.setDisplayHomeAsUpEnabled(true);
		}
	}

	public void showDrawerButton() {
		if (getActivity() instanceof ActionBarActivity) {
			((ActionBarActivity) getActivity()).getSupportActionBar()
					.setDisplayHomeAsUpEnabled(false);
		}
		mActionBarDrawerToggle.syncState();
	}

	void selectItem(int position) {
		mCurrentSelectedPosition = position;
		if (mDrawerLayout != null) {
			mDrawerLayout.closeDrawer(mFragmentContainerView);
		}
		if (mCallbacks != null) {
			mCallbacks.onNavigationDrawerItemSelected(position);
		}
		((NavigationDrawerAdapter) mDrawerList.getAdapter())
				.selectPosition(position);
	}

	public boolean isDrawerOpen() {
		return mDrawerLayout != null
				&& mDrawerLayout.isDrawerOpen(mFragmentContainerView);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		mActionBarDrawerToggle.onConfigurationChanged(newConfig);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt(STATE_SELECTED_POSITION, mCurrentSelectedPosition);
	}

	@Override
	public void onNavigationDrawerItemSelected(int position) {
		selectItem(position);
	}

	public DrawerLayout getDrawerLayout() {
		return mDrawerLayout;
	}

	public void setDrawerLayout(DrawerLayout drawerLayout) {
		mDrawerLayout = drawerLayout;
	}

	public static void saveSharedSetting(Context ctx, String settingName,
			String settingValue) {
		SharedPreferences sharedPref = ctx.getSharedPreferences(
				PREFERENCES_FILE, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPref.edit();
		editor.putString(settingName, settingValue);
		editor.apply();
	}

	public static String readSharedSetting(Context ctx, String settingName,
			String defaultValue) {
		SharedPreferences sharedPref = ctx.getSharedPreferences(
				PREFERENCES_FILE, Context.MODE_PRIVATE);
		return sharedPref.getString(settingName, defaultValue);
	}
}
