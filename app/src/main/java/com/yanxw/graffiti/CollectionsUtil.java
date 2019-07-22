package com.yanxw.graffiti;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * Created by summer on 2016/9/20.
 */
public class CollectionsUtil {

    public static boolean isEmpty(Collection collection) {
        return collection == null || collection.isEmpty();
    }

    public static boolean contains(Collection collection, Object value) {
        return !isEmpty(collection) && collection.contains(value);
    }

    public static <T> boolean equals(List<T> c1, List<T> c2, Comparator<T> comparator) {
        if (isEmpty(c1)) return isEmpty(c2);
        if (isEmpty(c2)) return false;
        if (c1.size() != c2.size()) return false;
        for (int i = 0; i < c1.size(); i++) {
            if (comparator.compare(c1.get(i), c2.get(i)) != 0) {
                return false;
            }
        }
        return true;
    }

    public static <T> boolean equals(List<T> c1, List<T> c2) {
        if (isEmpty(c1)) return isEmpty(c2);
        if (isEmpty(c2)) return false;
        if (c1.size() != c2.size()) return false;
        for (int i = 0; i < c1.size(); i++) {
            if (!c1.get(i).equals(c2.get(i))) {
                return false;
            }
        }
        return true;
    }

    //无序比较
    public static <T> boolean equalsNoOrder(List<T> c1, List<T> c2) {
        if (isEmpty(c1)) return isEmpty(c2);
        if (isEmpty(c2)) return false;
        if (c1.size() != c2.size()) return false;
        List<T> tmp = new ArrayList<>(c2);
        for (T t : c1) {
            if (!tmp.remove(t)) {
                return false;
            }
        }
        return tmp.size() == 0;
    }

    public static boolean isNotEmpty(Collection collection) {
        return !isEmpty(collection);
    }

    public static boolean isNotEmpty(String ... strs) {
        return strs != null && strs.length > 0;
    }

    public static boolean isEmpty(Map map) {
        return map == null || map.isEmpty();
    }

    public static boolean isNotEmpty(Map map) {
        return !isEmpty(map);
    }

    public static int[] toIntArray(List<Integer> list) {
        if (isEmpty(list)) return null;
        int[] result = new int[list.size()];
        for (int i = 0; i < list.size(); i++) {
            result[i] = list.get(i);
        }
        return result;
    }

    public static List<Integer> toIntList(int[] array) {
        if (array == null) return null;
        ArrayList<Integer> list = new ArrayList<>(array.length);
        for (int i : array) {
            list.add(i);
        }
        return list;
    }

    public static <T> void move(List<T> list, int from, int to) {
        if (isEmpty(list) || list.size() <= from || list.size() <= to) return;
        T remove = list.remove(from);
        list.add(to, remove);
    }
}
