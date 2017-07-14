package com.chenqihong.watermarking.watermarking;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * Created by chenqihong on 2017/7/14.
 */

public class Watermarking {
    private static final int d = 5;
    public static Bitmap embed(String originalImagePath, String markingImagePath){
        Bitmap originalImage = BitmapFactory.decodeFile(originalImagePath);
        Bitmap markingImage = BitmapFactory.decodeFile(markingImagePath);
        int oWidth = originalImage.getWidth();
        int oHeight = originalImage.getHeight();
        int mWidth = markingImage.getWidth();
        int mHeight = markingImage.getHeight();
        int[] oPixels = new int[3 * oWidth * oHeight];
        int[] mPixels = new int[mWidth * mHeight];
        originalImage.getPixels(oPixels, 0, oWidth, 0, 0, oWidth, oHeight);
        markingImage.getPixels(mPixels, 0, mWidth, 0,0, mWidth, mHeight);

        int[][][] RGBPixels = ImageUtils.getRGBArrayToMatrix(oPixels, oWidth, oHeight);
        double[][] rPixels = MathTool.intToDoubleMatrix(RGBPixels[2]);
        int[][] mDMatrix = ImageUtils.arrayToMatrix(mPixels, mWidth, mHeight);
        double[][] result = rPixels;

        for(int i = 0; i < mWidth; i++){
            for(int j = 0; j < mHeight; j++){
                double[][] blk = new double[8][8];
                // 对原始图像8 * 8 分块
                for (int m = 0; m < 8; m++) {
                    for (int n = 0; n < 8; n++) {
                        blk[m][n] = rPixels[8 * i + m][8 * j + n];
                    }
                }
                double[][] dBlk = Fdct.fDctTransform(blk);
                if (mDMatrix[i][j] == 0) {
                    dBlk[3][3] = dBlk[3][3] - d;
                    dBlk[3][4] = dBlk[3][4] - d;
                    dBlk[3][5] = dBlk[3][5] - d;
                    dBlk[4][3] = dBlk[4][3] - d;
                    dBlk[5][3] = dBlk[5][3] - d;
                } else {
                    dBlk[3][3] = dBlk[3][3] + d;
                    dBlk[3][4] = dBlk[3][4] + d;
                    dBlk[3][5] = dBlk[3][5] + d;
                    dBlk[4][3] = dBlk[4][3] + d;
                    dBlk[5][3] = dBlk[5][3] + d;
                }
                blk = Fdct.invFDctTransform(dBlk);
                for (int m = 0; m < 8; m++) {
                    for (int n = 0; n < 8; n++) {
                        result[8 * i + m][8 * j + n] = blk[m][n];
                    }
                }
            }
        }
        double[][][] temp = new double[3][oWidth][oHeight];
        temp[0] = MathTool.intToDoubleMatrix(RGBPixels[0]);
        temp[1] = MathTool.intToDoubleMatrix(RGBPixels[1]);
        temp[2] = result;
        double[] rgbResult = ImageUtils.getRGBMatrixToArray(temp);
        return ImageUtils.getImage(rgbResult, oWidth, oHeight);
    }

    public static Bitmap extract(Bitmap encodeBitmap, Bitmap watermarkBitmap){
        
    }
}
