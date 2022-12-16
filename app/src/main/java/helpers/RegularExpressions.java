package helpers;

import java.util.regex.Pattern;

public class RegularExpressions {
    public static Pattern usernameCharacterCount = Pattern.compile("^.{7,}", Pattern.MULTILINE);
    public static Pattern usernameDigitCount = Pattern.compile("\\d", Pattern.MULTILINE);
    public static Pattern usernameSymbolCount = Pattern.compile("\\W", Pattern.MULTILINE);
    public static Pattern usernameUppercaseCount = Pattern.compile("[A-Z]", Pattern.MULTILINE);
    public static Pattern usernameLowercaseCount = Pattern.compile("[a-z]", Pattern.MULTILINE);

    public static Pattern passwordCharacterCount = Pattern.compile("^.{7,}", Pattern.MULTILINE);
    public static Pattern passwordDigitCount = Pattern.compile("\\d", Pattern.MULTILINE);
    public static Pattern passwordSymbolCount = Pattern.compile("\\W", Pattern.MULTILINE);
    public static Pattern passwordUppercaseCount = Pattern.compile("[A-Z]", Pattern.MULTILINE);
    public static Pattern passwordLowercaseCount = Pattern.compile("[a-z]", Pattern.MULTILINE);


    public static boolean[] ValidateUsername(String username) {
        boolean[] validations = new boolean[] { false, false, false, false, false };
        boolean isAllowedLength = usernameCharacterCount.matcher(username).find();
        boolean containsDigits = usernameDigitCount.matcher(username).find();
        boolean containsSymbols = usernameSymbolCount.matcher(username).find();
        boolean containsUppercase = usernameUppercaseCount.matcher(username).find();
        boolean containsLowercase = usernameLowercaseCount.matcher(username).find();

        if (!isAllowedLength) {
            validations[0] = true;
        } if (!containsDigits) {
            validations[1] = true;
        } if (!containsSymbols) {
            validations[2] = true;
        } if (!containsUppercase) {
            validations[3] = true;
        } if (!containsLowercase) {
            validations[4] = true;
        }
        return validations;
    }

    public static boolean[] ValidatePassword(String password) {
        boolean[] validations = new boolean[] { false, false, false, false, false };
        boolean isAllowedLength = passwordCharacterCount.matcher(password).find();
        boolean containsDigits = passwordDigitCount.matcher(password).find();
        boolean containsSymbols = passwordSymbolCount.matcher(password).find();
        boolean containsUppercase = passwordUppercaseCount.matcher(password).find();
        boolean containsLowercase = passwordLowercaseCount.matcher(password).find();

        if (!isAllowedLength) {
            validations[0] = true;
        } if (!containsDigits) {
            validations[1] = true;
        } if (!containsSymbols) {
            validations[2] = true;
        } if (!containsUppercase) {
            validations[3] = true;
        } if (!containsLowercase) {
            validations[4] = true;
        }
        return validations;
    }
}
