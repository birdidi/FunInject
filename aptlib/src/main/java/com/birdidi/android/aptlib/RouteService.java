package com.birdidi.android.aptlib;

import java.util.HashMap;
import java.util.Map;

/**
 * @author xuyu.chen
 * @date 2019/03/26
 * @email xuyu.chen@ucarinc.com
 * @desc
 */
public class RouteService {

    public static final Map<String, String> sRouteDict = new HashMap<>();

    public static void put(String path, String clazz) {
        sRouteDict.put(path, clazz);
    }

    public static String get(String path) {
        return sRouteDict.get(path);
    }
}
