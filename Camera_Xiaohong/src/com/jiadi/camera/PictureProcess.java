package com.jiadi.camera;

import java.io.FileOutputStream;

import com.jiadi.filters.BitmapFilter;

import android.app.Activity;
import android.app.AlertDialog;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class PictureProcess extends Activity implements AppConstants, OnClickListener{
	
	LinearLayout.LayoutParams layoutParams;
	RelativeLayout topbar;
	RelativeLayout middlebar;
	ImageView picture;
	Button save;
	Bitmap picBm;
	private String filePath;
	Bitmap[] filteredBm;
	Bitmap preBm;
	Bitmap curBm;
	HorizontalListView listview;
	Drawable previous;
	Drawable current;
	ImageView frame[];
	private boolean isDefault = true;
	private int filterId = 0;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.picture_process);
		
		DisplayMetrics metrics = getResources().getDisplayMetrics();
		int heiPx = metrics.heightPixels; // height max of the device
		int widPx = metrics.widthPixels;
		
		topbar = (RelativeLayout) findViewById(R.id.top_bar);
		middlebar = (RelativeLayout) findViewById(R.id.middle_bar);
		
		layoutParams = new LinearLayout.LayoutParams(widPx, widPx/6);
		topbar.setLayoutParams(layoutParams);
		layoutParams = new LinearLayout.LayoutParams(widPx, widPx);
		middlebar.setLayoutParams(layoutParams);
		
		save = (Button) findViewById(R.id.button_save);
		save.setOnClickListener(this);
		listview = (HorizontalListView) findViewById(R.id.list_view_filter);
		listview.setAdapter(mAdapter);
		
		frame = new ImageView[11];
		
		listview.setOnItemClickListener(new OnItemClickListener() {
		    public void onItemClick(AdapterView<?> a, View v,int position, long id) {
		    	if (filteredBm[position] == null) {
		    		if (preBm == null) {
		    			preBm = BitmapFilter.changeStyle(picBm, 99);
		    		}
		    		filteredBm[position] = BitmapFilter.changeStyle(preBm, position);
		    	}
		    	current = new BitmapDrawable(filteredBm[position]);
		    	Drawable[] drawables = { previous, current };
	            TransitionDrawable transition = new TransitionDrawable(drawables);
	            picture.setImageDrawable(transition);
	            transition.startTransition(500);
	            previous = current;
	            curBm = (filteredBm[position]);
	            for (int i = 0; i < frame.length; i++) {
	            	if (i != position) {
	            		if (frame[i] != null) {
	            			frame[i].setBackgroundDrawable(null);
	            		}
	            	} else {
	            		frame[i].setBackgroundDrawable(getResources().getDrawable(R.drawable.frame_border));
	            	}
	            	
	            }
	            filterId = position;
//		    	picture.setImageBitmap(filteredBm[position]);
		    }
		});

		picture = (ImageView) findViewById(R.id.picture);
		Bundle extras = getIntent().getExtras();
	    if (extras != null) {
	        filePath = extras.getString(FILE_PATH_KEY);
	    }
	    picBm = BitmapFactory.decodeFile(filePath);
		Matrix matrix = new Matrix();
		matrix.postRotate(90);
	    if (picBm.getWidth() > picBm.getHeight()) {
	    	picBm = Bitmap.createBitmap(picBm, picBm.getHeight()/6, 0, picBm.getHeight(), picBm.getHeight(), matrix, true);
	    } else {
	    	picBm = Bitmap.createBitmap(picBm, 0, picBm.getWidth()/6, picBm.getWidth(), picBm.getWidth(), matrix, true);
	    }
	    picture.setImageBitmap(picBm);
	    previous = new BitmapDrawable(picBm);
	    filteredBm = new Bitmap[11];
	    filteredBm[0] = picBm;
    	new ProcessPictureTask().execute(picBm);
    	
	}
	
	private class ProcessPictureTask extends AsyncTask<Bitmap, Object, Object> {
		protected Object doInBackground(Bitmap... params) {
			if (preBm == null) {
    			preBm = BitmapFilter.changeStyle(picBm, 99);
    		}
			for (int i = 1; i < 11; i++) {
				if (filteredBm[i] == null) {
					filteredBm[i] = BitmapFilter.changeStyle(preBm, i);
				}
			}
			try {
			    FileOutputStream out = new FileOutputStream(filePath);
			    params[0].compress(Bitmap.CompressFormat.PNG, 90, out);
			} catch (Exception e) {
			       e.printStackTrace();
			}
			return null;
	    }
	    protected void onPostExecute(Object result) {
	    }
	}
	
	private class SavePictureTask extends AsyncTask<Bitmap, Object, Object> {
		protected Object doInBackground(Bitmap... params) {
			try {
			    FileOutputStream out = new FileOutputStream(filePath);
			    params[0].compress(Bitmap.CompressFormat.PNG, 90, out);
			} catch (Exception e) {
			       e.printStackTrace();
			}
			return null;
	    }
	    protected void onPostExecute(Object result) {
	    }
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.button_save:
			new SavePictureTask().execute(curBm);
		default:
			break;
		}
	} 
	
	
	
	private static String[] dataObjects = new String[]{ "normal", "nature", "hug", "blue", "breeze",
		"dawn", "picnic", "vintage", "Saturday", "calm", "memory"}; 
	
	private BaseAdapter mAdapter = new BaseAdapter() {

		@Override
		public int getCount() {
			return dataObjects.length;
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View retval = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_view_filter_item, null);
			TextView title = (TextView) retval.findViewById(R.id.filter_name);
			frame[position] = (ImageView) retval.findViewById(R.id.filter_frame);
//			if (isDefault && position == 0) {
//				frame[0].setBackgroundDrawable(getResources().getDrawable(R.drawable.frame_border));
//				isDefault = false;
//			}
			frame[filterId].setBackgroundDrawable(getResources().getDrawable(R.drawable.frame_border));
			title.setText(dataObjects[position]);
			return retval;
		}
		
	};
}
