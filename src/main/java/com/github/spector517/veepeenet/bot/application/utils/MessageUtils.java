package com.github.spector517.veepeenet.bot.application.utils;

import lombok.experimental.UtilityClass;

@UtilityClass
public class MessageUtils {

    private final char[] MD_2_CHARS_TO_ESCAPE = new char[] {
            '_', '*', '[', ']', '(', ')', '~', '`', '>', '#', '+', '-', '=', '|', '{', '}', '.', '!'
    };

    public String escapeMD2Chars(String str) {
        var resultSb = new StringBuilder();
        for (char strChar: str.toCharArray()) {
            for (char targetChar: MD_2_CHARS_TO_ESCAPE) {
                if (strChar == targetChar) {
                    resultSb.append('\\');
                    break;
                }
            }
            resultSb.append(strChar);
        }
        return resultSb.toString();
    }
}
