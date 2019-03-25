package com.birdidi.android.myapt;

import android.app.Activity;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author xuyu.chen
 * @date 2019/03/25
 * @email xuyu.chen@ucarinc.com
 * @desc
 */
public class Binder {

    public static void bind(Activity activity) {
        try {
            Class clazz = Class.forName(activity.getClass().getName() + "_InjectView");
            Constructor constructor = clazz.getConstructor(activity.getClass());
            Object binder = constructor.newInstance(activity);
            Method bind = binder.getClass().getMethod("bind");
            bind.invoke(binder);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

    }
}
