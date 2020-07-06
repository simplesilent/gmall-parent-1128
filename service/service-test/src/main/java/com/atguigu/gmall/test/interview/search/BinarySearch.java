package com.atguigu.gmall.test.interview.search;

import java.util.ArrayList;
import java.util.List;

public class BinarySearch {
    public static void main(String[] args) {
        int[] arr ={1, 8, 10, 89, 1000, 1000, 1234};
        List<Integer> integers = binarySearch2(arr, 0, arr.length, 1000);
        System.out.println(integers);
    }

    /**二分查找：递归实现【数据必须有序】，数据从小到大排序*/
    public static int binarySearch(int[] arr, int left, int right, int findVal) {
        if (left > right) {
            return -1;
        }

        int mid = (left + right) / 2;
        int midVal = arr[mid];

        if (findVal > midVal) {
            return binarySearch(arr, mid + 1, right, findVal);// 向右递归
        } else if (findVal < midVal) {
            return binarySearch(arr, left, mid - 1, findVal);// 向左递归
        } else {
            return mid;
        }
    }

    /**二分查找：查找多个相同的值*/
    public static List<Integer> binarySearch2(int[] arr, int left, int right, int findVal) {
        if (left > right) {
            return new ArrayList<>();
        }

        int mid = (left + right) / 2;
        int midVal = arr[mid];

        if (findVal > midVal) {
            return binarySearch2(arr, mid + 1, right, findVal);// 向右递归
        } else if (findVal < midVal) {
            return binarySearch2(arr, left, mid - 1, findVal);// 向左递归
        } else {
            List<Integer> indexList = new ArrayList<>();
            int temp = mid - 1;
            // 向mid的左边扫描，将所有满足条件的下标，加入到list中
            while (true) {
                if (temp < 0 || arr[temp] != findVal) {
                    break;
                }
                indexList.add(temp);
                temp--;
            }
            indexList.add(temp);
            // 向mid的右边扫描，将所有满足条件的下标，加入到list中
            temp = mid + 1;
            while (true) {
                if (temp > arr.length - 1 || arr[temp] != findVal) {
                    break;
                }
                indexList.add(temp);
                temp++;
            }
            return indexList;
        }
    }
}
