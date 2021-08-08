package com.springframework.core;

/**
 * @author: zfan
 * @create: 2021-08-03 22:11
 **/
abstract class GraalDetector {

    private static final boolean imageCode = (System.getProperty("org.graalvm.nativeimage.imagecode") != null);

    public static boolean inImageCode() {
        return imageCode;
    }


}
