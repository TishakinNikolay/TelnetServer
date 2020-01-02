package ua.nikolay;

public class Utils {
    public static boolean isNumber(String string) {
        if(string == null) {
            return false;
        }
        if (string.matches("\\d+")) {
            return true;
        } else {
            return false;
        }
    }
}
