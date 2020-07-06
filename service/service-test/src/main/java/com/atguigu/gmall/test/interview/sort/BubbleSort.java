package com.atguigu.gmall.test.interview.sort;

import java.util.Arrays;

public class BubbleSort {
    public static void main(String[] args) {
        int[] arr = {3, 44, 38, 5, 47, 15, 36, 26, 27, 2, 46, 4, 19, 50, 48};
        bubbleSortRecursive(arr,0,arr.length);
        System.out.println(Arrays.toString(arr));
    }

    /**冒泡排序：1.使用for循环实现从小到排序*/
    public static void bubbleSortCycle(int[] arr) {

        int temp;
        boolean flag = false;
        for (int i = 0; i < arr.length - 1; i++) {
            for (int j = 0; j < arr.length - i - 1; j++) {
                if (arr[j] > arr[j + 1]) {
                    flag = true;
                    temp = arr[j];
                    arr[j] = arr[j + 1];
                    arr[j + 1] = temp;
                }
            }
            if (flag) {
                // 如果flag为true，说明发生了交换
                flag = false;
            } else {
                // flag为false，没有发生交换，直接return
                return;
            }
        }

        System.out.println(Arrays.toString(arr));
    }

    /**冒泡排序：2.使用递归实现从小到排序*/
    public static void bubbleSortRecursive(int[] arr,int start,int end) {

        if (start < end) {
            int temp = 0;
            for (int j = 0; j < end - start - 1; j++) {
                if (arr[j] > arr[j + 1]) {
                    temp = arr[j];
                    arr[j] = arr[j + 1];
                    arr[j + 1] = temp;
                }
            }
            end--;
            bubbleSortRecursive(arr, start, end);
        }
    }
}
