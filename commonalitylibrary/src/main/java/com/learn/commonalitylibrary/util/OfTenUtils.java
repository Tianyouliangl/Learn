package com.learn.commonalitylibrary.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class OfTenUtils {

    public static String getRandomUid() {
        return UUID.randomUUID().toString();
    }

    public static String getConviction(String id1, String id2) {

        // [^a-z^A-Z]   提取所有的英文   [^0-9]  所有的数字
        String a1 = id1.replaceAll("[^a-z^A-Z]", "") + id2.replaceAll("[^a-z^A-Z]", "");
        String a2 = id1.replaceAll("[^0-9]", "") + id2.replaceAll("[^0-9]", "");
        String sort1 = sortString(a1);
        String a3 = deRedo(sort1);
        String sort2 = sortInt(a2);
        String a4 = deRedo(sort2);
        String a5 = combination(a3, a4);
        String a6 = getDeRedoMaxNumber(a1);
        String a7 = getDeRedoMaxNumber(a2);
        return a5 + a6 + a7 + sort1.substring(sort1.length()-1,sort1.length()) + sort2.substring(sort2.length()-1,sort2.length());
    }

    /**
     * 字符串去重
     *
     * @param args
     */
    public static String deRedo(String args) {
        char[] chars = args.toCharArray();
        List<String> str = new LinkedList<>();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < chars.length; i++) {
            if (str.contains(String.valueOf(chars[i]))) {
                continue;
            } else {
                str.add(String.valueOf(chars[i]));
                sb.append(String.valueOf(chars[i]));
            }
        }
//        System.out.println(sb.toString());
        return sb.toString();
    }

    /**
     * 字符串字母排序
     *
     * @param a
     * @return
     */
    public static String sortString(String a) {
        String context = "";
        ArrayList<Character> chars = new ArrayList<Character>();
        for (char c : a.toCharArray()) {
            chars.add(c);
        }
        Collections.sort(chars);
        for (int i = 0; i < chars.size(); i++) {
            context += chars.get(i).toString();
        }
//        System.out.println(context);
        return context;
    }

    /**
     * 字符串数字排序
     *
     * @param a2
     * @return
     */
    public static String sortInt(String a2) {
        // 新建一个int数组
        int[] b = new int[a2.length()];
//将字符串进行切割，并放入数组中
        for (int i = 0; i < a2.length(); i++) {
            b[i] = Integer.parseInt(a2.substring(i, i + 1));
        }
        for (int i = 0; i < b.length - 1; i++) {
            for (int j = b.length - 1; j > 0; j--) {
                if (b[j] < b[j - 1]) {
                    int temp = b[j];
                    b[j] = b[j - 1];
                    b[j - 1] = temp;
                }
            }
        }
        String str2 = "";
        for (int i = 0; i < b.length; i++) {
            str2 += b[i];
        }
//        System.out.println(str2);
        return str2;
    }

    /**
     * 字符串和数字组合
     *
     * @param a1
     * @param a2
     * @return
     */
    public static String combination(String a1, String a2) {
        String context = "";
        if (a1.length() > a2.length()) {
            for (int i = 0; i < a1.length() - 1; i++) {
                if ((i + 1) < a2.length()) {
                    context += a1.substring(i, i + 1) + a2.substring(i, i + 1);
                } else {
                    context += a1.substring(i, i + 1);
                }
            }
        } else {
            for (int i = 0; i < a2.length() - 1; i++) {
                if ((i + 1) < a1.length()) {
                    context += a2.substring(i, i + 1) + a1.substring(i, i + 1);
                } else {
                    context += a2.substring(i, i + 1);
                }
            }
        }
        return context;
    }

    /**
     * 获取出现最多的字母以及次数
     *
     * @param input
     * @return
     */
    public static String getDeRedoMaxNumber(String input) {
        String context = "";
        Set<String> set = new HashSet<String>();// 有序、不重复
        List<String> list = new ArrayList<String>();
        int len = input.length();
        for (int i = 0; i < len; i++) {
            set.add(input.charAt(i) + "");
            list.add(input.charAt(i) + "");
        }
        Collections.sort(list);
        StringBuilder stringBuilder = new StringBuilder();
        for (String string : list) {
            stringBuilder.append(string);
        }
        String orderString = stringBuilder.toString();
        int maxTimes = 0;
        String maxStr = "";
        Iterator<String> iterator = set.iterator();
        Map<String, Integer> result = new HashMap<String, Integer>();
        List<String> resultList = new ArrayList<String>();
        while (iterator.hasNext()) {
            String str = iterator.next();
            int start = orderString.indexOf(str);
            int end = orderString.lastIndexOf(str);
            if ((end - start) > maxTimes) {
                maxTimes = end - start + 1;
                maxStr = str;
                result.put(str, maxTimes);
                resultList.add(str);
            } else if ((end - start) == maxTimes) {
                result.put(str, maxTimes);
                resultList.add(str);
            }
        }
        int index = 0;
        for (int i = 0; i < resultList.size(); i++) {
            if (resultList.get(i).equals(maxStr)) {
                index = i;
                break;
            }
        }
        for (int i = index; i < result.size(); i++) {
            context = result.get(resultList.get(i)) + resultList.get(i);
        }
        return context;
    }

    public static String replace(String msg) {
        String removeStr = "-";
        String rpe = msg.replace(removeStr, "");
        return rpe;
    }
}
