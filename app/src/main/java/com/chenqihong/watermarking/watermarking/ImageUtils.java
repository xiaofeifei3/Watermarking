package com.chenqihong.watermarking.watermarking;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by chenqihong on 2017/7/14.
 */

public class ImageUtils {
    /**
     * 获取图片
     *
     * @param filepath
     * @return
     */
    public static Bitmap getImage(String filepath) {
        Bitmap image = null;
        image = BitmapFactory.decodeFile(filepath);
        return image;
    }

    public static Bitmap getImage(double[] colors, int width, int height){
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        int count = 0;
        for(int i = 0; i < width; i++){
            for (int j = 0; j < height; j++){
                bitmap.setPixel(i, j, (int)colors[count ++]);
            }
        }

        return bitmap;
    }

    /**
     * 获取图像文件的像素（图片转换为像素）
     *
     * @param filepath
     */
    public int[] getImagePixels(String filepath) {
        Bitmap image = null;
        image = BitmapFactory.decodeFile(filepath);
        // 得到图像的宽度
        int width = image.getWidth();
        // 得到图像的高度
        int height = image.getHeight();
        // RGB格式图像文件每一个点的颜色由红、绿、兰三种颜色构成，即实际图像可为3层，
        // 分别为R，G，B层，因此分解后的文件象素是实际坐标高度和宽度的三倍。
        int[] pixels = new int[3 * width * height];
        // 读取坐标的范围是从(0,0)坐标开始宽width,高height
        image.getPixels(pixels, 0, width, 0, 0, width, height);
        return pixels;
    }

    /**
     * 像素转换成图像文件
     *
     * @param result
     * @param width
     * @param height
     * @param filepath
     * @param format
     */
    public static void setImage(double[] result, int width, int height,
                                String filepath, String format, int type) {
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        int count = 0;
        for(int i = 0; i < width; i++){
            for (int j = 0; j < height; j++){
                bitmap.setPixel(i, j, (int)result[count ++]);
            }
        }

        // 图像文件的写入
        File outFile = new File(filepath);
        try {
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(outFile));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            bos.flush();
            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 一维数组转为二维数组
     *
     * @param m
     * @param width
     * @param height
     * @return
     */
    public static int[][] arrayToMatrix(int[] m, int width, int height) {
        int[][] result = new int[height][width];
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int p = j * height + i;
                result[i][j] = m[p];
            }
        }
        return result;
    }

    /**
     * 一维数组转换为三维数组
     *
     * @param pixels
     * @param width
     * @param height
     * @return
     */
    public static int[][][] getRGBArrayToMatrix(int[] pixels, int width,
                                                int height) {
        // 已知有3个二维数组组成分别代表RGB
        int[][][] result = new int[3][height][width];
        int[][] temp = new int[3][width * height];
        for (int i = 0; i < pixels.length; i++) {
            int m = i / 3;
            int n = i % 3;
            temp[n][m] = pixels[i];
        }
        result[0] = arrayToMatrix(temp[0], width, height);
        result[1] = arrayToMatrix(temp[1], width, height);
        result[2] = arrayToMatrix(temp[2], width, height);
        return result;
    }

    /**
     * 二维数组转为一维数组
     *
     * @param m
     * @return
     */
    public static double[] matrixToArray(double[][] m) {
        int p = m.length * m[0].length;
        double[] result = new double[p];
        for (int i = 0; i < m.length; i++) {
            for (int j = 0; j < m[i].length; j++) {
                int q = j * m.length + i;
                result[q] = m[i][j];
            }
        }
        return result;
    }

    /**
     * 三维数组转为一维数组
     *
     * @param m
     * @return
     */
    public static double[] getRGBMatrixToArray(double[][][] m) {
        int width = m[0].length;
        int height = m[0][0].length;
        int len = width * height;
        double[] result = new double[3 * len];
        double[][] temp = new double[3][len];
        temp[0] = matrixToArray(m[0]);
        temp[1] = matrixToArray(m[1]);
        temp[2] = matrixToArray(m[2]);
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < temp[i].length; j++)
                result[3 * j + i] = temp[i][j];
        }
        return result;
    }
}
