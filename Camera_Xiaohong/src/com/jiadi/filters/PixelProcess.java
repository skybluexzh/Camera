package com.jiadi.filters;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.ColorMatrix;

public class PixelProcess {
	
	public static int[] changeRGB(int[] pixels, int width, double[][] matrix) { 
        int pixColor = 0;  
        int pixR = 0;  
        int pixG = 0;  
        int pixB = 0;  
        int newR = 0;  
        int newG = 0;  
        int newB = 0;  
        for (int i = 0; i < width; i++)  
        {  
            for (int k = 0; k < width; k++)  
            {  
                pixColor = pixels[width * i + k];  
                pixR = Color.red(pixColor);  
                pixG = Color.green(pixColor);  
                pixB = Color.blue(pixColor);  
                newR = (int) (matrix[0][0] * pixR + matrix[0][1] * pixG + matrix[0][2] * pixB);  
                newG = (int) (matrix[1][0] * pixR + matrix[1][1] * pixG + matrix[1][2] * pixB);  
                newB = (int) (matrix[2][0] * pixR + matrix[2][1] * pixG + matrix[2][2] * pixB);  
                int newColor = Color.argb(255, newR > 255 ? 255 : newR, newG > 255 ? 255 : newG, newB > 255 ? 255 : newB);  
                pixels[width * i + k] = newColor;  
            }  
        }
        return pixels;
	}
	
	public static int[] sharpen(int[] pixels, int width) {
		double ratio = 1.0/64;
        double[] laplacian = new double[] { -ratio, -ratio, -ratio, -ratio, 1.125, -ratio, -ratio, -ratio, -ratio };  
        int pixR = 0;  
        int pixG = 0;  
        int pixB = 0;
        int pixColor = 0;  
          
        int newR = 0;  
        int newG = 0;  
        int newB = 0;  
          
        int idx = 0;  
        float alpha = 1.0F;    
        for (int i = 1; i < width - 1; i++)  // y
        {  
            for (int k = 1; k < width - 1; k++)  // x
            {  
                idx = 0; 
                for (int m = -1; m <= 1; m++)  
                {  
                    for (int n = -1; n <= 1; n++)  
                    {  
                        pixColor = pixels[(i + n) * width + k + m];  
                        pixR = Color.red(pixColor);  
                        pixG = Color.green(pixColor);  
                        pixB = Color.blue(pixColor);  
                          
                        newR = newR + (int) (pixR * laplacian[idx] * alpha);  
                        newG = newG + (int) (pixG * laplacian[idx] * alpha);  
                        newB = newB + (int) (pixB * laplacian[idx] * alpha);  
                        idx++;  
                    }  
                }  
                  
                newR = Math.min(255, Math.max(0, newR));  
                newG = Math.min(255, Math.max(0, newG));  
                newB = Math.min(255, Math.max(0, newB));  
                  
                pixels[i * width + k] = Color.argb(255, newR, newG, newB);  
                newR = 0;  
                newG = 0;  
                newB = 0;  
            }  
        }  
        return pixels;
	}
	
	public static int[] drawCircle(int[] pixels, int width) {
        int pixR = 0;  
        int pixG = 0;  
        int pixB = 0;  
          
        int pixColor = 0;  
          
        int newR = 0;  
        int newG = 0;  
        int newB = 0;  
          
        int radius = width / 4;
        int center = width / 2;
        final float strength = 1.2F; 
        
        int pos = 0;  
        for (int i = 0; i < width; i++)  // y
        {   
            for (int k = 0; k < width; k++) // x 
            {  
                pos = i * width + k;  
                pixColor = pixels[pos];  
                  
                pixR = Color.red(pixColor);  
                pixG = Color.green(pixColor);  
                pixB = Color.blue(pixColor);  
                
                newR = pixR;  
                newG = pixG;  
                newB = pixB;
                int mean = (pixR + pixG + pixB) / 3;
                int result;
                int distance = (int) Math.sqrt((Math.pow((center - i), 2) + Math.pow(center - k, 2)));
                if (distance <= radius) {
                	result = 0;
                } else {
                	result = (int) (strength * mean * Math.pow(1 - (double)radius / distance, 2));
                	// result max : 0.50 * mean
                }
                newR = pixR - result;  
                newG = pixG - result;  
                newB = pixB - result;  
                  
                newR = Math.min(255, Math.max(0, newR));  
                newG = Math.min(255, Math.max(0, newG));  
                newB = Math.min(255, Math.max(0, newB));  
                  
                pixels[pos] = Color.argb(255, newR, newG, newB);  
                
//                System.out.println("PPP: " + mean);
            }  
        }  
           
        return pixels;  
	}
	
	public static int[] drawLargeCircle(int[] pixels, int width) {
        int pixR = 0;  
        int pixG = 0;  
        int pixB = 0;  
          
        int pixColor = 0;  
          
        int newR = 0;  
        int newG = 0;  
        int newB = 0;  
          
        int radius = width * 3 / 8;
        int center = width / 2;
        final float strength = 2.2F; 
        
        int pos = 0;  
        for (int i = 0; i < width; i++)  // y
        {   
            for (int k = 0; k < width; k++) // x 
            {  
                pos = i * width + k;  
                pixColor = pixels[pos];  
                  
                pixR = Color.red(pixColor);  
                pixG = Color.green(pixColor);  
                pixB = Color.blue(pixColor);  
                  
                newR = pixR;  
                newG = pixG;  
                newB = pixB;
                int mean = (pixR + pixG + pixB) / 3;
                int result;
                int distance = (int) Math.sqrt((Math.pow((center - i), 2) + Math.pow(center - k, 2)));
                if (distance <= radius) {
                	result = 0;
                } else {
                	result = (int) (strength * mean * Math.pow(1 - (double)radius / distance, 2));
                	// result max: 0.49 * mean
                }
                newR = pixR - result;  
                newG = pixG - result;  
                newB = pixB - result;  
                  
                newR = Math.min(255, Math.max(0, newR));  
                newG = Math.min(255, Math.max(0, newG));  
                newB = Math.min(255, Math.max(0, newB));  
                  
                pixels[pos] = Color.argb(255, newR, newG, newB);  
            }  
        }  
           
        return pixels;  
	}
	
	public static int[] drawLargeDiamond(int[] pixels, int width) {
        int pixR = 0;  
        int pixG = 0;  
        int pixB = 0;  
          
        int pixColor = 0;  
          
        int newR = 0;  
        int newG = 0;  
        int newB = 0;  
        
        int side = width / 2;
        final float strength = 2F; 
        int pos = 0;  
        for (int i = 0; i < width; i++)  // y
        {   
            for (int j = 0; j < width; j++) // x 
            {  
                pos = i * width + j;  
                pixColor = pixels[pos];  
                  
                pixR = Color.red(pixColor);  
                pixG = Color.green(pixColor);  
                pixB = Color.blue(pixColor);  
                  
                newR = pixR;  
                newG = pixG;  
                newB = pixB;
                int mean = (pixR + pixG + pixB) / 3;
                int result;
                int distance;
                if (i + j < side) {
                	distance = side - (i + j);
                } else if (width - i + j < side) {
                	distance = side - (width - i + j);
                } else if (width - j + i < side) {
                	distance = side - (width - j + i);
                } else if (width - i + width - j < side) {
                	distance = side - (width - i + width - j);
                } else {
                	distance = 0;
                }
                distance += side;
                result = (int) (strength * mean * Math.pow(1 - (double)side / distance, 2));
                // result max: 0.50 * mean
                
                newR = pixR - result;  
                newG = pixG - result;  
                newB = pixB - result;  
                  
                newR = Math.min(255, Math.max(0, newR));  
                newG = Math.min(255, Math.max(0, newG));  
                newB = Math.min(255, Math.max(0, newB));  
                  
                pixels[pos] = Color.argb(255, newR, newG, newB);  
                
//                System.out.println("PPP: " + (double)center/distance);
            }  
        }  
           
        return pixels;  
	}
	
	public static int[] drawDiamond(int[] pixels, int width) {
        int pixR = 0;  
        int pixG = 0;  
        int pixB = 0;  
          
        int pixColor = 0;  
          
        int newR = 0;  
        int newG = 0;  
        int newB = 0;  
        
        int side = width * 3 / 4;
        int center = width / 2;
        final float strength = 2F; 
        int pos = 0;  
        for (int i = 0; i < width; i++)  // y
        {   
            for (int j = 0; j < width; j++) // x 
            {  
                pos = i * width + j;  
                pixColor = pixels[pos];  
                  
                pixR = Color.red(pixColor);  
                pixG = Color.green(pixColor);  
                pixB = Color.blue(pixColor);  
                  
                newR = pixR;  
                newG = pixG;  
                newB = pixB;
                int mean = (pixR + pixG + pixB) / 3;
                int result;
                int distance;
                if (i + j < side && i <= center && j <= center) {
                	distance = side - (i + j);
                } else if (width - i + j < side && i > center && j <= center) {
                	distance = side - (width - i + j);
                } else if (width - j + i < side && i <= center && j > center) {
                	distance = side - (width - j + i);
                } else if (width - i + width - j < side && i > center && j > center) {
                	distance = side - (width - i + width - j);
                } else {
                	distance = 0;
                }
                distance += side;
                result = (int) (strength * mean * Math.pow(1 - (double)side / distance, 2));
                // result max: 0.50 * mean
                
                newR = pixR - result;  
                newG = pixG - result;  
                newB = pixB - result;  
                  
                newR = Math.min(255, Math.max(0, newR));  
                newG = Math.min(255, Math.max(0, newG));  
                newB = Math.min(255, Math.max(0, newB));  
                  
                pixels[pos] = Color.argb(255, newR, newG, newB);  
                
//                System.out.println("PPP: " + (double)center/distance);
            }  
        }  
           
        return pixels;  
	}
	
	public static int[] drawStar(int[] pixels, int width) {
        int pixR = 0;  
        int pixG = 0;  
        int pixB = 0;  
          
        int pixColor = 0;  
          
        int newR = 0;  
        int newG = 0;  
        int newB = 0;  
        
        int radius = width / 2;
        final float strength = 2F; 
        int pos = 0;  
        for (int i = 0; i < width; i++)  // y
        {   
            for (int j = 0; j < width; j++) // x 
            {  
                pos = i * width + j;  
                pixColor = pixels[pos];  
                  
                pixR = Color.red(pixColor);  
                pixG = Color.green(pixColor);  
                pixB = Color.blue(pixColor);  
                  
                newR = pixR;  
                newG = pixG;  
                newB = pixB;
                int mean = (pixR + pixG + pixB) / 3;
                int result;
                int distance;
                if ((int) Math.sqrt((Math.pow(i, 2) + Math.pow(j, 2))) < radius) {
                	distance = radius - (int) Math.sqrt((Math.pow(i, 2) + Math.pow(j, 2)));
                } else if ((int) Math.sqrt((Math.pow(width - i, 2) + Math.pow(j, 2))) < radius) {
                	distance = radius - (int) Math.sqrt((Math.pow(width - i, 2) + Math.pow(j, 2)));
                } else if ((int) Math.sqrt((Math.pow(i, 2) + Math.pow(width - j, 2))) < radius) {
                	distance = radius - (int) Math.sqrt((Math.pow(i, 2) + Math.pow(width - j, 2)));
                } else if ((int) Math.sqrt((Math.pow(width - i, 2) + Math.pow(width - j, 2))) < radius) {
                	distance = radius - (int) Math.sqrt((Math.pow(width - i, 2) + Math.pow(width - j, 2)));
                } else {
                	distance = 0;
                }
                distance += radius;
                result = (int) (strength * mean * Math.pow(1 - (double)radius / distance, 2));
                // result max: 0.50 * mean
                
                newR = pixR - result;  
                newG = pixG - result;  
                newB = pixB - result;  
                  
                newR = Math.min(255, Math.max(0, newR));  
                newG = Math.min(255, Math.max(0, newG));  
                newB = Math.min(255, Math.max(0, newB));  
                  
                pixels[pos] = Color.argb(255, newR, newG, newB);  
                
//                System.out.println("PPP: " + (double)center/distance);
            }  
        }  
           
        return pixels;  
	}
	
	public static int[] drawLargeStar(int[] pixels, int width) {
        int pixR = 0;  
        int pixG = 0;  
        int pixB = 0;  
          
        int pixColor = 0;  
          
        int newR = 0;  
        int newG = 0;  
        int newB = 0;  
        
        int radius = width * 3 / 4;
        int center = width / 2;
        final float strength = 2F; 
        int pos = 0;  
        for (int i = 0; i < width; i++)  // y
        {   
            for (int j = 0; j < width; j++) // x 
            {  
                pos = i * width + j;  
                pixColor = pixels[pos];  
                  
                pixR = Color.red(pixColor);  
                pixG = Color.green(pixColor);  
                pixB = Color.blue(pixColor);  
                  
                newR = pixR;  
                newG = pixG;  
                newB = pixB;
                int mean = (pixR + pixG + pixB) / 3;
                int result;
                int distance;
                if ((int) Math.sqrt((Math.pow(i, 2) + Math.pow(j, 2))) < radius && i <= center && j <= center ) {
                	distance = radius - (int) Math.sqrt((Math.pow(i, 2) + Math.pow(j, 2)));
                } else if ((int) Math.sqrt((Math.pow(width - i, 2) + Math.pow(j, 2))) < radius && i > center && j <= center ) {
                	distance = radius - (int) Math.sqrt((Math.pow(width - i, 2) + Math.pow(j, 2)));
                } else if ((int) Math.sqrt((Math.pow(i, 2) + Math.pow(width - j, 2))) < radius && i <= center && j > center ) {
                	distance = radius - (int) Math.sqrt((Math.pow(i, 2) + Math.pow(width - j, 2)));
                } else if ((int) Math.sqrt((Math.pow(width - i, 2) + Math.pow(width - j, 2))) < radius && i > center && j > center) {
                	distance = radius - (int) Math.sqrt((Math.pow(width - i, 2) + Math.pow(width - j, 2)));
                } else {
                	distance = 0;
                }
                distance += radius;
                result = (int) (strength * mean * Math.pow(1 - (double)radius / distance, 2));
                // result max: 0.50 * mean
                
                newR = pixR - result;  
                newG = pixG - result;  
                newB = pixB - result;  
                  
                newR = Math.min(255, Math.max(0, newR));  
                newG = Math.min(255, Math.max(0, newG));  
                newB = Math.min(255, Math.max(0, newB));  
                  
                pixels[pos] = Color.argb(255, newR, newG, newB);  
                
//                System.out.println("PPP: " + (double)center/distance);
            }  
        }  
           
        return pixels;  
	}
	
	
	
	public static int[] drawSquare(int[] pixels, int width) {
        int pixR = 0;  
        int pixG = 0;  
        int pixB = 0;  
          
        int pixColor = 0;  
          
        int newR = 0;  
        int newG = 0;  
        int newB = 0;  
        
        int side = width / 10;
        final float strength = 1.5F; 
        int pos = 0;  
        for (int i = 0; i < width; i++)  // y
        {   
            for (int j = 0; j < width; j++) // x 
            {  
                pos = i * width + j;  
                pixColor = pixels[pos];  
                  
                pixR = Color.red(pixColor);  
                pixG = Color.green(pixColor);  
                pixB = Color.blue(pixColor);  
                  
                newR = pixR;  
                newG = pixG;  
                newB = pixB;
                int mean = (pixR + pixG + pixB) / 3;
                int result;
                int distance;
                if (i < side && j >= side && j <= width - side) {
                	distance = side - i;
                } else if (j < side && i >= side && i <= width - side) {
                	distance = side - j;
                } else if (width - i < side && j >= side && j <= width - side ) {
                	distance = side - (width - i);
                } else if (width - j < side && i >= side && i <= width - side) {
                	distance = side - (width - j);
                } else if (i < side && j < side) {
                	distance = (int) Math.sqrt((Math.pow(side - i, 2) + Math.pow(side - j, 2)));
                } else if (width - i < side && j < side) {
                	distance = (int) Math.sqrt((Math.pow(side - (width - i), 2) + Math.pow(side - j, 2)));
                } else if (i < side && width - j < side) {
                	distance = (int) Math.sqrt((Math.pow(side - i, 2) + Math.pow(side - (width - j), 2)));
                } else if (width - i < side && width - j < side) {
                	distance = (int) Math.sqrt((Math.pow(side - (width - i), 2) + Math.pow(side - (width - j), 2)));
                } else {
                	distance = 0;
                }
                distance += side;
                result = (int) (strength * mean * Math.pow(1 - (double)side / distance, 2));
                // result max: 0.375 * mean
                
                newR = pixR - result;  
                newG = pixG - result;  
                newB = pixB - result;  
                  
                newR = Math.min(255, Math.max(0, newR));  
                newG = Math.min(255, Math.max(0, newG));  
                newB = Math.min(255, Math.max(0, newB));  
                  
                pixels[pos] = Color.argb(255, newR, newG, newB);  
                
//                System.out.println("PPP: " + (double)center/distance);
            }  
        }  
           
        return pixels;  
	}
	
	public static int[] drawCrossing(int[] pixels, int width) {
        int pixR = 0;  
        int pixG = 0;  
        int pixB = 0;  
          
        int pixColor = 0;  
          
        int newR = 0;  
        int newG = 0;  
        int newB = 0;  
        
        int side = width / 2;
        final float strength = 2F; 
        int pos = 0;  
        for (int i = 0; i < width; i++)  // y
        {   
            for (int j = 0; j < width; j++) // x 
            {  
                pos = i * width + j;  
                pixColor = pixels[pos];  
                  
                pixR = Color.red(pixColor);  
                pixG = Color.green(pixColor);  
                pixB = Color.blue(pixColor);  
                  
                newR = pixR;  
                newG = pixG;  
                newB = pixB;
                int mean = (pixR + pixG + pixB) / 3;
                int result;
                int distance;
                if (i <= side && j <= side) {
                	distance = (side - i) * (side - j);
                } else if (width - i < side && j <= side) {
                	distance = (side - (width - i)) * (side - j);
                } else if (i <= side && width - j < side) {
                	distance = (side - i) * (side - (width - j));
                } else if (width - i < side && width - j < side) {
                	distance = (side - (width - i)) * (side - (width - j));
                } else {
                	distance = 0;
                }
                distance += side * side;
                result = (int) (strength * mean * Math.pow(1 - (double)(side * side) / distance, 2));
                // result max: 0.5 * mean
                
                newR = pixR - result;  
                newG = pixG - result;  
                newB = pixB - result;  
                  
                newR = Math.min(255, Math.max(0, newR));  
                newG = Math.min(255, Math.max(0, newG));  
                newB = Math.min(255, Math.max(0, newB));  
                  
                pixels[pos] = Color.argb(255, newR, newG, newB);  
                
//                System.out.println("PPP: " + (double)center/distance);
            }  
        }  
           
        return pixels;  
	}
}
