package helper;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by n-soorani on 2017/03/20.
 */
public class Utility {

    public static String LPad(String str, Integer length, char car) {
        return
                String.format("%" + (length - str.length()) + "s", "")
                        .replace(" ", String.valueOf(car)) + str;
    }

    public static String RPad(String str, Integer length, char car) {
        return str +
                String.format("%" + (length - str.length()) + "s", "")
                .replace(" ", String.valueOf(car));
    }

    public static String getYYYMMDDHHmmssSSS() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
        Date date = new Date();
        return dateFormat.format(date);
    }


}
