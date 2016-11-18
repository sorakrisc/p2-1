package io.muic.dcom.p2;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by james on 18/11/2559.
 */
public class test {
    public static void main(String[] args) {
        ConcurrentHashMap<String, Integer> CHM = new ConcurrentHashMap<>();
        Integer var = CHM.putIfAbsent("a", 1);
        System.out.println("Should add a and 1");
        System.out.println(CHM);
        System.out.println("Should print null");
        System.out.println(var);
        Integer var2 = CHM.putIfAbsent("a", 2);
        System.out.println("Should add a and 1");
        System.out.println(CHM);
        System.out.println("Should print 1");
        System.out.println(var2);

    }
}
