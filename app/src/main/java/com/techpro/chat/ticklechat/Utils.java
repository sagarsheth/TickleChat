package com.techpro.chat.ticklechat;

import java.security.MessageDigest;

/**
 * Created by Sagar on 3/4/2017.
 */

public class Utils {

    // For Password
    public static String SHA1(String text) {
        if (text == null)
            return text;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            md.update(text.getBytes("iso-8859-1"), 0, text.length());
            byte[] sha1hash = md.digest();
            StringBuilder buf = new StringBuilder();
            for (byte b : sha1hash) {
                int halfbyte = (b >>> 4) & 0x0F;
                int two_halfs = 0;
                do {
                    buf.append((0 <= halfbyte) && (halfbyte <= 9) ? (char) ('0' + halfbyte) : (char) ('a' + (halfbyte - 10)));
                    halfbyte = b & 0x0F;
                } while (two_halfs++ < 1);
            }
            return buf.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
