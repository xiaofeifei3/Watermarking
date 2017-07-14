package com.chenqihong.watermarking.watermarking;

/**
 * Created by chenqihong on 2017/7/14.
 */

public class MathTool {
    public static double[][] intToDoubleMatrix(int[][] input) {
        int height = input.length;
        int width = input[0].length;
        double[][] output = new double[height][width];
        for (int i = 0; i < height; i++) {
            // 列
            for (int j = 0; j < width; j++) {
                // 行
                output[i][j] = Double.valueOf(String.valueOf(input[i][j]));
                System.out.print(output[i][j]);
            }
            System.out.println();
        }
        return output;
    }

    public static double[] intToDoubleArray(int[] input) {
        int length = input.length;
        double[] output = new double[length];
        for (int i = 0; i < length; i++)
            output[i] = Double.valueOf(String.valueOf(input[i]));
        return output;
    }
}
