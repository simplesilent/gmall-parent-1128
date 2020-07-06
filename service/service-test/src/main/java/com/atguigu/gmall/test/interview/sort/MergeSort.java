package com.atguigu.gmall.test.interview.sort;

import java.util.Arrays;

public class MergeSort {
    public static void main(String[] args) {
        int[] arr = {3, 44, 38, 5, 47, 15, 36, 26, 27, 2, 46, 4, 19, 50, 48};
        int[] temp = new int[arr.length];
        merge(arr, 0, arr.length - 1, temp);
        System.out.println(Arrays.toString(arr));
    }

    /**归并排序----分与合*/
    public static void merge(int[] arr,int left, int right, int[] temp) {
        if (left < right) {
            int mid = (left + right) / 2;
            // 向左递归分解
            merge(arr, left, mid, temp);
            // 向右递归分解
            merge(arr, mid + 1, right, temp);
            // 合并
            mergeSort(arr, left, mid, right, temp);
        }
    }

    /**归并排序----合并*/
    private static void mergeSort(int[] arr, int left, int mid, int right, int[] temp) {
        int i = left;
        int j = mid + 1;
        int t = 0;

        // 1.当左右两边不为空时，比较左右两边数值大小，放入到中转数组
        while (i <= mid && j <= right) {
            if (arr[i] <= arr[j]) {
                temp[t] = arr[i];
                t++;
                i++;
            } else {
                temp[t] = arr[j];
                t++;
                j++;
            }
        }

        // 2.再将左右两边有剩余的数填充到中转数组中
        while (i <= mid) {
            temp[t] = arr[i];
            t++;
            i++;
        }
        while (j <= right) {
            temp[t] = arr[j];
            t++;
            j++;
        }

        // 3.将temp中的数拷贝到arr中
        t = 0;
        int tempLeft = left;
        while (tempLeft <= right) {
            arr[tempLeft] = temp[t];
            t++;
            tempLeft++;
        }

    }

}
