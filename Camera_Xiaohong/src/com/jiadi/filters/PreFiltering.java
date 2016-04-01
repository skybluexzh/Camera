package com.jiadi.filters;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;

public class PreFiltering {
	// 黑白效果函数
	public static Bitmap filter(Bitmap bitmap) {
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		int[] pixels = new int[width * height];
		Bitmap returnBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
        PixelProcess.sharpen(pixels, width);
        returnBitmap.setPixels(pixels, 0, width, 0, 0, width, height);  
		return returnBitmap;
	}
}
