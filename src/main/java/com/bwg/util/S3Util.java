package com.bwg.util;

public class S3Util {
    public static String extractFileKey(String fileName) {
        if (fileName.startsWith("http")) {
            return fileName.substring(fileName.lastIndexOf("/") + 1);
        }
        return fileName;
    }
}
