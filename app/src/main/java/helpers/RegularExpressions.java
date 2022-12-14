package helpers;

import java.util.regex.Pattern;

public class RegularExpressions {
    public static Pattern usernameCharacterCount = Pattern.compile("\\w{7,}", Pattern.MULTILINE);
    public static Pattern usernameDigitCount = Pattern.compile("\\d", Pattern.MULTILINE);
    public static Pattern usernameSymbolCount = Pattern.compile("\\W", Pattern.MULTILINE);
    
    public static boolean[] ValidateUsername(String username) {
        boolean[] validations = new boolean[] { true, false, false };
        if (!usernameCharactercount.matche
    }
}
