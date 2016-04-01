package com.jiadi.camera;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.ShutterCallback;
import android.hardware.Camera.Size;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.OrientationEventListener;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

public class CameraPreview extends Activity implements OnClickListener, AppConstants {
	private static final int FLASH_OFF = 0;
	private static final int FLASH_ON = 1;
	private static final int FLASH_AUTO = 2;
	private static final int DELAY = 1200;
	private static final int HALF_TRANSPARENT = 127;
	private static final int FULLY_OPAQUE = 255;
	private static final String TAG = "CameraPreview";
	private static final int PORTRAIT_NORMAL = 0;
	private static final int LANDSCAPE_NORMAL = 1;
	private static final int PORTRAIT_INVERTED = 2;
	private static final int LANDSCAPE_INVERTED = 3;
//	private static final String RAW_FILE_PATH = "raw_image";
	RelativeLayout.LayoutParams layoutParams;
	RelativeLayout frame;
	RelativeLayout topperplate;
	RelativeLayout hiddenplate;
	FrameLayout preview;
	PreviewSurface surface;
	ImageButton shutter;
	ImageButton switchcamera;
	ImageButton flash;
	Camera camera;
	Camera.Parameters cameraParams;
	Handler timer = new Handler();
	OrientationEventListener orientationListener;
	private int currentOrientation;
	private int lastOrientation;
	private int flashMode;
	private int cameraMode;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.camera_preview);

		
		preview = (FrameLayout) findViewById(R.id.camera_preview);
		frame = (RelativeLayout) findViewById(R.id.preview_frame);
		topperplate = (RelativeLayout) findViewById(R.id.topper_plate);
		hiddenplate = (RelativeLayout) findViewById(R.id.hidden_plate);
		surface = new PreviewSurface(this);
		preview.addView(surface);
		shutter = (ImageButton) findViewById(R.id.button_shutter);
		switchcamera = (ImageButton) findViewById(R.id.switch_camera);
		flash = (ImageButton) findViewById(R.id.button_flash);
		flashMode = FLASH_OFF;
		cameraMode = Camera.CameraInfo.CAMERA_FACING_BACK;
		shutter.setOnClickListener(this);
		flash.setOnClickListener(this);
		switchcamera.setOnClickListener(this);
		currentOrientation = -1;
		lastOrientation = -1;
		
		DisplayMetrics metrics = getResources().getDisplayMetrics();
		int heiPx = metrics.heightPixels; // height max of the device
		int widPx = metrics.widthPixels;
		    
		layoutParams = new RelativeLayout.LayoutParams(widPx, widPx*4/3);
		frame.setLayoutParams(layoutParams);
		layoutParams = new RelativeLayout.LayoutParams(widPx, widPx/6);
		topperplate.setLayoutParams(layoutParams);
		layoutParams = new RelativeLayout.LayoutParams(widPx, widPx*4/3-widPx/6);
		hiddenplate.setLayoutParams(layoutParams);
		
		orientationListener = new OrientationEventListener(this, SensorManager.SENSOR_DELAY_NORMAL) {
	        public void onOrientationChanged(int orientation) {
	        	if (orientation < 45 || orientation >= 315) {
	        		currentOrientation = PORTRAIT_NORMAL;
	        	} else if (orientation >= 45 && orientation < 135) {
	        		currentOrientation = LANDSCAPE_NORMAL;
	        	} else if (orientation >= 135 && orientation < 225) {
	        		currentOrientation = PORTRAIT_INVERTED;
	        	} else if (orientation >= 225 && orientation < 315) {
	        		currentOrientation = LANDSCAPE_INVERTED;
	        	}
	        	if (currentOrientation != lastOrientation) {
	        		System.out.println("OOO: " + currentOrientation);
	        		lastOrientation = currentOrientation;
	        	}
	        	
	        }
	    };

		Log.d(TAG, "onCreate'd");
	}

	public void onResume() {
		super.onResume();
		orientationListener.enable();
	}
	
	public void onPause() {
		super.onPause();
		orientationListener.disable();
	}
	
	

	
	
	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.button_shutter:
			camera.takePicture(shutterCallback, rawCallback, jpegCallback);
			break;
		case R.id.button_flash:
			switch(flashMode) {
			case FLASH_OFF:
				flash.setBackgroundDrawable(getResources().getDrawable(R.drawable.flash_on));
				cameraParams.setFlashMode(Camera.Parameters.FLASH_MODE_ON);
				camera.setParameters(cameraParams);
				flashMode = FLASH_ON;
				break;
			case FLASH_ON:
				flash.setBackgroundDrawable(getResources().getDrawable(R.drawable.flash_auto));
				cameraParams.setFlashMode(Camera.Parameters.FLASH_MODE_AUTO);
				camera.setParameters(cameraParams);
				flashMode = FLASH_AUTO;
				break;
			case FLASH_AUTO:
				flash.setBackgroundDrawable(getResources().getDrawable(R.drawable.flash_off));
				cameraParams.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
				camera.setParameters(cameraParams);
				flashMode = FLASH_OFF;
				break;
			default:
				break;
			}
			break;
		case R.id.switch_camera:
			shutter.setEnabled(false);
			switchcamera.setEnabled(false);
			switch(cameraMode) {
			case Camera.CameraInfo.CAMERA_FACING_BACK:
				cameraMode = Camera.CameraInfo.CAMERA_FACING_FRONT;
				flash.getBackground().setAlpha(HALF_TRANSPARENT);
				flash.setEnabled(false);
				break;
			case Camera.CameraInfo.CAMERA_FACING_FRONT:
				cameraMode = Camera.CameraInfo.CAMERA_FACING_BACK;
				flash.getBackground().setAlpha(FULLY_OPAQUE);
				flash.setEnabled(true);
				break;
			default:
				break;
			}
			if (camera != null) {
				camera.stopPreview();
	            camera.setPreviewCallback(null);
	            camera.release();
	            camera = null;
	            surface = null;
	            preview.removeAllViews();
			}
			surface = new PreviewSurface(CameraPreview.this);
			preview.addView(surface);
			timer.postDelayed(new Runnable(){
		        public void run() {
		        	shutter.setEnabled(true);
					switchcamera.setEnabled(true);
		        }
		    }, DELAY);
			break;
		default:
			break;
		}
		
	}
	
	ShutterCallback shutterCallback = new ShutterCallback() {
		public void onShutter() {
			Log.d(TAG, "onShutter'd");
		}
	};

	/** Handles data for raw picture */
	PictureCallback rawCallback = new PictureCallback() {
		public void onPictureTaken(byte[] data, Camera camera) {
			Log.d(TAG, "onPictureTaken - raw");
		}
	};

	/** Handles data for jpeg picture */
	PictureCallback jpegCallback = new PictureCallback() {
		public void onPictureTaken(byte[] data, Camera camera) {
			String filePath = FILE_ROOT_DIRECTORY + System.currentTimeMillis() + ".jpg";
			FileOutputStream outStream = null;
			try {
//				 write to local sandbox file system
//				outStream = CameraPreview.this.openFileOutput(RAW_FILE_PATH, MODE_PRIVATE);	
				// Or write to sdcard
				outStream = new FileOutputStream(filePath);	
				outStream.write(data);
				outStream.close();
				Log.d(TAG, "onPictureTaken - wrote bytes: " + data.length);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
			}
//			Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
//			Matrix matrix = new Matrix();
//			if (bitmap.getWidth() > bitmap.getHeight()) {
//		    	bitmap = Bitmap.createBitmap(bitmap, bitmap.getHeight()/6, 0, bitmap.getHeight(), bitmap.getHeight(), matrix, true);
//		    } else {
//		    	bitmap = Bitmap.createBitmap(bitmap, 0, bitmap.getWidth()/6, bitmap.getWidth(), bitmap.getWidth(), matrix, true);
//		    }
//			try {
//			    FileOutputStream out = new FileOutputStream(filePath);
//			    bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
//			} catch (Exception e) {
//			       e.printStackTrace();
//			}
			Intent intent = new Intent(CameraPreview.this, PictureProcess.class);
			intent.putExtra(FILE_PATH_KEY, filePath);
			startActivity(intent);
			Log.d(TAG, "onPictureTaken - jpeg " + getFilesDir());
		}
	};
	
	private class SwitchCameraTask extends AsyncTask<Object, Object, Object> {
		@Override
		protected void onPreExecute() {
			
		}
		@Override
		protected Object doInBackground(Object... arg0) {
			
			return null;
		}
		protected void onPostExecute(Object result) {
			
	    }
	}
	
	private class PreviewSurface extends SurfaceView implements SurfaceHolder.Callback {
		private static final String TAG = "PreviewSurface";

	    protected SurfaceHolder surfaceholder;
	    
	    
	    PreviewSurface(Context context) {
	        super(context);
	        
	        // Install a SurfaceHolder.Callback so we get notified when the
	        // underlying surface is created and destroyed.
	        surfaceholder = getHolder();
	        surfaceholder.addCallback(this);
	        surfaceholder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	    }

	    public void surfaceCreated(SurfaceHolder holder) {
	        // The Surface has been created, acquire the camera and tell it where
	        // to draw.
	        
	        try {
	        	camera = Camera.open(cameraMode);
	        	cameraParams = camera.getParameters();
				camera.setPreviewDisplay(holder);
				
				camera.setPreviewCallback(new PreviewCallback() {
					public void onPreviewFrame(byte[] data, Camera camera) {
					}
				});
			} catch (IOException e) {
				camera.release();
		        camera = null;
			}
	    }

	    public void surfaceDestroyed(SurfaceHolder holder) {
	        // Surface will be destroyed when we return, so stop the preview.
	        // Because the CameraDevice object is not a shared resource, it's very
	        // important to release it when the activity is paused.
	    	if (camera != null) {
	    		camera.stopPreview();
		        camera.setPreviewCallback(null);
		        camera.release();
		        camera = null;
	    	}
	        
	    }

	    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
	        // Now that the size is known, set up the camera parameters and begin
	        // the preview.
	    	camera.setDisplayOrientation(90);
	        cameraParams.setPreviewSize(w, h);
	        cameraParams.setRotation(90);
	        List<Size> list = cameraParams.getSupportedPictureSizes();
	        for (int i = 0; i < list.size(); i++) {
	        	int width = list.get(i).width;
	        	int height = list.get(i).height;
	        	System.out.println("jiadi: " + width + ", " + height);
	        }
	        for (int i = 0; i < list.size(); i++) {
	        	int width = list.get(i).width;
	        	int height = list.get(i).height;
	        	if ((double)height / width == 0.75 && width * height <= 500000) {
	        		cameraParams.setPictureSize(width, height);
	        		System.out.println("jiadi: " + width + ", " + height);
	        		break;
	        	}
//	        	System.out.println("jiadi: " + width + ", " + height);
	        }
	        switch(flashMode) {
	        case FLASH_OFF:
	        	cameraParams.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
	        	break;
	        case FLASH_ON:
	        	cameraParams.setFlashMode(Camera.Parameters.FLASH_MODE_ON);
	        	break;
	        case FLASH_AUTO:
	        	cameraParams.setFlashMode(Camera.Parameters.FLASH_MODE_AUTO);
	        	break;	
	        default:
	        	break;
	        }
	        camera.setParameters(cameraParams);
	        camera.startPreview();
	    }

	    @Override
	    public void draw(Canvas canvas) {
	    		super.draw(canvas);
//	    		Paint p= new Paint(Color.RED);
//	    		Log.d(TAG,"draw");
//	    		canvas.drawText("PREVIEW", canvas.getWidth()/2, canvas.getHeight()/2, p );
	    }
	}
	

}
