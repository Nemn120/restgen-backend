package es.achavez.miw.tfm.restgen.generator.application.service.generator;

public final class GeneratorUtil {

    public static String snakeCaseToUpperCamelCase(String input) {
        StringBuilder result = new StringBuilder();
        boolean capitalizeNext = true;

        for (int i = 0; i < input.length(); i++) {
            char currentChar = input.charAt(i);

            if (currentChar == '_') {
                capitalizeNext = true;
            } else {
                if (capitalizeNext) {
                    result.append(Character.toUpperCase(currentChar));
                    capitalizeNext = false;
                } else {
                    result.append(currentChar);
                }
            }
        }

        return result.toString().replaceAll("_", "");
    }

    public static String convertCamelToSnakeCaseUpper(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        StringBuilder result = new StringBuilder();
        result.append(Character.toUpperCase(input.charAt(0)));

        for (int i = 1; i < input.length(); i++) {
            char currentChar = input.charAt(i);
            if (Character.isUpperCase(currentChar)) {
                result.append("_").append(Character.toUpperCase(currentChar));
            } else {
                result.append(Character.toUpperCase(currentChar));
            }
        }
        return result.toString();
    }

    public static String convertCamelToSnakeCaseLower(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        StringBuilder result = new StringBuilder();
        result.append(Character.toLowerCase(input.charAt(0)));

        for (int i = 1; i < input.length(); i++) {
            char currentChar = input.charAt(i);
            if (Character.isUpperCase(currentChar)) {
                result.append("_").append(Character.toUpperCase(currentChar));
            } else {
                result.append(Character.toUpperCase(currentChar));
            }
        }
        return result.toString();
    }

    public static String concatRelationAndKeyRelation(String relation, String keyRelation){
        return relation.toLowerCase().concat(keyRelation.substring(0,1).toUpperCase().concat(keyRelation.substring(1)));
    }

    public static String convertUpperFisrtLettersAndCamelCase(String str) {
        if(str == null)
            return null;
        str = str.toLowerCase();
        return convertUpperCaseFirstLetters(snakeCaseToCamelCase(str));
    }

    public static String convertUpperCaseFirstLetters(String str) {
        if (str == null) {
            return null;
        }

        return str.substring(0, 1).toUpperCase().concat(str.substring(1));
    }

    public static String snakeCaseToCamelCase(String start) {
        StringBuffer sb = new StringBuffer();
        for (String s : start.split("-")) {
            sb.append(Character.toUpperCase(s.charAt(0)));
            if (s.length() > 1) {
                sb.append(s.substring(1).toLowerCase());
            }
        }
        return sb.toString();
    }
}
