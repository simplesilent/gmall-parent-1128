package com.atguigu.gmall.test.interview.sort;

import java.util.Arrays;

public class QuickSort {
    public static void main(String[] args) {
        int[] arr = {3, 44, 38, 5, 47, 15, 36, 26, 27, 2, 46, 4, 19, 50, 48};
        quickSort(arr, 0, arr.length - 1);
        System.out.println(Arrays.toString(arr));
    }

    /**快速排序：从小到大排序*/
    public static void quickSort(int[] arr, int left, int right) {
        int l = left;//左下标
        int r = right;//右下标
        int pivot = arr[(left + right) / 2];//中轴值
        int temp = 0;//临时变量，交换使用

        while (l < r) {

            //在pivot左边一直找，找到大于或者等于中轴值的下标l
            while (arr[l] < pivot) {
                l += 1;
            }
            //在pivot右边一直找，找到小于或者等于中轴值的下标r
            while (arr[r] > pivot) {
                r -= 1;
            }
            //当左边的索引大于右边的索引时，退出循环
            if (l >= r) {
                break;
            }

            // 交换左右两个值
            temp = arr[l];
            arr[l] = arr[r];
            arr[r] = temp;

            // 当左边的索引l的值等于pivot时，将l++，r--
            if (arr[l] == pivot) {
                l += 1;
                r -= 1;
            }
        }

        // 当l==r是，l++,r--否则栈溢出
        if (l == r) {
            l += 1;
            r -= 1;
        }

        // 向左递归
        if (left < r) {
            quickSort(arr, left, r);
        }
        // 向右递归
        if (right > l) {
            quickSort(arr,l,right);
        }
    }

}
