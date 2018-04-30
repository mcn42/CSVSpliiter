/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.mtahq.csv.splitter;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author MNILSEN
 */
public class Utils {

    private static AppPropertyManager appProperties;
    private static String logPrefix;

    public static void config(String path) {
        appProperties = new AppPropertyManager(path + "/csvsplitter.properties", SplitterAppProperties.values());
        logPrefix = Utils.getAppProperties().get(AppProperty.LOG_FILE_PREFIX);
        Log.configure(logPrefix);
    }

    public static AppPropertyManager getAppProperties() {
        return appProperties;
    }

    public static void sleep(int seconds) {
        try {
            Thread.sleep(seconds * 1000);
        } catch (InterruptedException ex) {
            //  no op
        }
    }

    public static String bytesToString(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return "null";
        }
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append("0x");
            String hex = Integer.toHexString(b & 0xFF).toUpperCase();
            if (hex.length() == 1) {
                hex = "0" + hex;
            }
            sb.append(hex);
            sb.append(",");
        }
        return sb.toString().substring(0, sb.length() - 1);
    }

    public static String[] bytesToStringArray(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return new String[]{};
        }
        String[] out = new String[bytes.length];
        StringBuilder sb = null;
        for (int i = 0; i < bytes.length; i++) {
            sb = new StringBuilder();
            byte b = bytes[i];
            sb.append("0x");
            String hex = Integer.toHexString(b & 0xFF).toUpperCase();
            if (hex.length() == 1) {
                hex = "0" + hex;
            }
            sb.append(hex);
            out[i] = sb.toString();
        }
        return out;
    }

    private static final SimpleDateFormat shortTime = new SimpleDateFormat("HH:mm:ss.SSS");

    public static final String formatShortTime(long timestamp) {
        return shortTime.format(new Date(timestamp));
    }

    public static float[] getDataFromCsvString(String dataList) {
        dataList = dataList.replace("[", "");
        dataList = dataList.replace("]", "");
        String[] vals = dataList.split("");
        float[] floats = new float[vals.length];
        for (int i = 0; i < vals.length; i++) {
            try {
                float f = Float.parseFloat(vals[i]);
                floats[i] = f;
            } catch (NumberFormatException e) {
                Log.getActivityLog().warning(String.format("Float parse error at idx %s: %s", i,vals[i]));
                floats[i] = -1.0f;
            }
        }
        return floats;
    }

}
