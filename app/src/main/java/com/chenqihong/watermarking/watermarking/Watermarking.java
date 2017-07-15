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
        int oWidth = encodeBitmap.getWidth();
        int oHeight = encodeBitmap.getHeight();
        int wWidth = watermarkBitmap.getWidth();
        int wHeight = watermarkBitmap.getHeight();
        int[] oPixels = new int[3 * oWidth * oHeight];
        int[] wPixels = new int[3 * oWidth * oHeight];
        encodeBitmap.getPixels(oPixels, 0, oWidth, 0, 0, oWidth,oHeight);
        watermarkBitmap.getPixels(wPixels, 0, wWidth, 0, 0, wWidth, wHeight);
        int[][][] mRgbPixels = ImageUtils.getRGBArrayToMatrix(wPixels, oWidth,
                oHeight);
        int[][][] oRgbPixels = ImageUtils.getRGBArrayToMatrix(oPixels, oWidth,
                oHeight);
        double[][] oDPixels = MathTool.intToDoubleMatrix(mRgbPixels[2]);
        double[][] mDPixels = MathTool.intToDoubleMatrix(oRgbPixels[2]);
        double[][] result = new double[wWidth][wHeight];
        for (int i = 0; i < wWidth; i++) {
            for (int j = 0; j < wHeight; j++) {
                double[][] oBlk = new double[8][8];
                double[][] mBlk = new double[8][8];
                int d = 0;
                int f = 0;
                for (int m = 0; m < 8; m++) {
                    for (int n = 0; n < 8; n++) {
                        oBlk[m][n] = oDPixels[8 * i + m][8 * j + n];
                        mBlk[m][n] = mDPixels[8 * i + m][8 * j + n];
                    }
                }
                double[][] dOBlk = Fdct.fDctTransform(oBlk);
                double[][] dMBlk = Fdct.fDctTransform(mBlk);
                if (dOBlk[3][3] > dMBlk[3][3]) {
                    d++;
                } else {
                    f++;
                }
                if (dOBlk[3][4] > dMBlk[3][4]) {
                    d++;
                } else {
                    f++;
                }
                if (dOBlk[3][5] > dMBlk[3][5]) {
                    d++;
                } else {
                    f++;
                }
                if (dOBlk[4][3] > dMBlk[4][3]) {
                    d++;
                } else {
                    f++;
                }
                if (dOBlk[5][3] > dMBlk[5][3]) {
                    d++;
                } else {
                    f++;
                }
                if (d < f) {
                    result[i][j] = 0;
                } else {
                    result[i][j] = 1;
                }
            }
        }
        double[] outResult = ImageUtils.matrixToArray(result);
        return ImageUtils.getImage(outResult, wWidth, wHeight);
    }
}
