package com.analytics.locatenow;

import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class NavigationDrawerAdapter extends
		RecyclerView.Adapter<NavigationDrawerAdapter.ViewHolder> {

	private List<NavigationItem> mData;
	private NavigationDrawerCallbacks mNavigationDrawerCallbacks;
	private int mSelectedPosition;
	private int mTouchedPosition = -1;
	Context mcontext;
	Typeface mediumFont,boldFont;
	public NavigationDrawerAdapter(List<NavigationItem> data, Context mcontext) {
		mData = data;
		this.mcontext = mcontext;
	}

	public NavigationDrawerCallbacks getNavigationDrawerCallbacks() {
		return mNavigationDrawerCallbacks;
	}

	public void setNavigationDrawerCallbacks(
			NavigationDrawerCallbacks navigationDrawerCallbacks) {
		mNavigationDrawerCallbacks = navigationDrawerCallbacks;
	}

	@Override
	public NavigationDrawerAdapter.ViewHolder onCreateViewHolder(
			ViewGroup viewGroup, int i) {
		View v = LayoutInflater.from(viewGroup.getContext()).inflate(
				R.layout.drawer_row, viewGroup, false);
		final ViewHolder viewholder = new ViewHolder(v);

		// viewholder.itemView.setOnTouchListener(new View.OnTouchListener() {
		// @Override
		// public boolean onTouch(View v, MotionEvent event) {
		//
		// switch (event.getAction()) {
		// case MotionEvent.ACTION_DOWN:
		// touchPosition(viewholder.getAdapterPosition());
		// return false;
		// case MotionEvent.ACTION_CANCEL:
		// touchPosition(-1);
		// return false;
		// case MotionEvent.ACTION_MOVE:
		// return false;
		// case MotionEvent.ACTION_UP:
		// touchPosition(-1);
		// return false;
		// }
		// return true;
		// }
		// });
		viewholder.itemView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mNavigationDrawerCallbacks != null)
					mNavigationDrawerCallbacks
							.onNavigationDrawerItemSelected(viewholder
									.getAdapterPosition());
				// notifyDataSetChanged();
			}
		});

		return viewholder;
	}

	@Override
	public void onBindViewHolder(NavigationDrawerAdapter.ViewHolder viewHolder,
			final int i) {
		mediumFont = Typeface.createFromAsset(mcontext.getAssets(), "Lato-Medium.ttf");
		boldFont = Typeface.createFromAsset(mcontext.getAssets(), "Lato-Bold.ttf");
		viewHolder.textView.setText(mData.get(i).getText());
		viewHolder.image.setImageDrawable(mData.get(i).getDrawable());
		viewHolder.textView.setTypeface(mediumFont);
		try {
			// "chat_seller_id", "chat_user_id",
			// "chat_offer_id", "chat_id",

			SQLiteDatabase mdatabase = mcontext.openOrCreateDatabase(
					"CHAT_DATABASE.db", Context.MODE_PRIVATE, null);

			String DATABASE_CREATE = "CREATE TABLE IF NOT EXISTS "
					+ "CHAT_TABLE"
					+ "(iID INTEGER PRIMARY KEY AUTOINCREMENT,chat_to_id TEXT,chat_from_id TEXT,chat_comment TEXT,chat_id TEXT,chat_username TEXT)";

			String DATABASE_COUNT = "CREATE TABLE IF NOT EXISTS "
					+ "CHAT_COUNT"
					+ "(iID INTEGER PRIMARY KEY AUTOINCREMENT,chat_to_id TEXT,chat_from_id TEXT,chat_comment TEXT,chat_id TEXT,chat_username TEXT)";

			mdatabase.execSQL(DATABASE_CREATE);
			mdatabase.execSQL(DATABASE_COUNT);
			mdatabase.close();

			SQLiteDatabase db = mcontext.openOrCreateDatabase(
					"CHAT_DATABASE.db", Context.MODE_PRIVATE, null);
			// BadgeView badge = new BadgeView(GPSActivityOnline.this, chat);
			Cursor cursor = db.rawQuery("SELECT * FROM CHAT_COUNT ", null);
			cursor.moveToFirst();
			Log.e("chat user list size",
					"chat userid:" + ",count:" + cursor.getCount());
			if (cursor.getCount() > 0) {
				if (i == 5) {
					viewHolder.count
							.setImageResource(R.drawable.ic_count_orange);
					viewHolder.chatcount.setText(cursor.getCount() + "");
				} else {
					viewHolder.count.setImageResource(0);
					viewHolder.chatcount.setText( "");
					viewHolder.chatcount.setTypeface(boldFont);
				}
			}else{
				viewHolder.count.setImageResource(0);
				viewHolder.chatcount.setText("");
			}

			cursor.close();
			db.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

		// TODO: selected menu position, change layout accordingly
		if (mSelectedPosition == i || mTouchedPosition == i) {
			viewHolder.itemView.setBackgroundColor(viewHolder.itemView
					.getContext().getResources()
					.getColor(R.color.selected_gray));
		} else {
			viewHolder.itemView.setBackgroundColor(Color.TRANSPARENT);
		}
	}

	private void touchPosition(int position) {
		int lastPosition = mTouchedPosition;
		mTouchedPosition = position;
		if (lastPosition >= 0)
			notifyItemChanged(lastPosition);
		if (position >= 0)
			notifyItemChanged(position);

	}

	public void selectPosition(int position) {
		int lastPosition = mSelectedPosition;
		mSelectedPosition = position;
		notifyItemChanged(lastPosition);
		notifyItemChanged(position);
	}

	@Override
	public int getItemCount() {
		return mData != null ? mData.size() : 0;
	}

	public static class ViewHolder extends RecyclerView.ViewHolder {
		public TextView textView, chatcount;
		public ImageView image;
		public ImageView count;

		public ViewHolder(View itemView) {
			super(itemView);
			textView = (TextView) itemView.findViewById(R.id.item_name);
			image = (ImageView) itemView.findViewById(R.id.item_image);
			count = (ImageView) itemView.findViewById(R.id.item_count);
			chatcount = (TextView) itemView.findViewById(R.id.txvChatCount);
		}
	}
}
