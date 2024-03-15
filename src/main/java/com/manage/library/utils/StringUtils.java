package com.manage.library.utils;

/**
 *
 * @author PC
 */
public class StringUtils {

    public static String addThumbnail(String input) {

        int lastIndex = input.lastIndexOf('.');

        if (lastIndex != -1) {
            int secondLastIndex = input.lastIndexOf('.', lastIndex - 1);

            if (secondLastIndex != -1) {
                return input.substring(0, secondLastIndex) + "_thumbnail" + ".jpg.encrypted";
            }
        }

        return null;
    }
}
