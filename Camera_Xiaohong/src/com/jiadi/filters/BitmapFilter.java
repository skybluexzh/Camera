package com.jiadi.filters;

import java.util.Random;



import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;

public class BitmapFilter {
	public static final int NATURE = 1;
	public static final int B = 2;
	public static final int C = 3;
	public static final int D = 4;
	public static final int E = 5;
	public static final int F = 6;
	public static final int G = 7;
	public static final int H = 8;
	public static final int I = 9;
	public static final int J = 10;
	public static final int PRE_FILTERING = 99;
	
	/**
	 * 设置滤镜效果，
	 * @param bitmap
	 * @param styeNo, 效果id
	 */
	public static Bitmap changeStyle(Bitmap bitmap, int filterId) {
		switch(filterId) {
		case NATURE:
			return Nature.filter(bitmap);
		case B:
			return b.filter(bitmap);
		case C:
			return c.filter(bitmap);
		case D:
			return d.filter(bitmap);
		case E:
			return e.filter(bitmap);
		case F:
			return f.filter(bitmap);
		case G:
			return g.filter(bitmap);
		case H:
			return h.filter(bitmap);
		case I:
			return i.filter(bitmap);
		case J:
			return j.filter(bitmap);
		case PRE_FILTERING:
			return PreFiltering.filter(bitmap);
		default:
			return bitmap;
		}
	}
	

}

















