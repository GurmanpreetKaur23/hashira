package hashira;

// IOException: to handle file reading errors
// BigInteger: to handle HUGE numbers
// Files, Paths: to read the file 
// List, ArrayList: to store numbers
// Matcher, Pattern: to search text using a pattern (regex)
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CatalogPlacements {

    public static void main(String[] args) throws IOException {
        // reading string as json
        String json = new String(Files.readAllBytes(Paths.get("input.json")));

        // n = total puzzle pieces
        // k = puzzle pieces needed to solve 
        int n = extractInt(json, "\"n\"\\s*:\\s*(\\d+)");
        int k = extractInt(json, "\"k\"\\s*:\\s*(\\d+)");

        System.out.println("n = " + n);
        System.out.println("k = " + k);
        
        List<BigInteger> xs = new ArrayList<>(); // the point
        List<BigInteger> ys = new ArrayList<>(); // the value of the point

        // pattern to find data :
        // "1": { "base": "10", "value": "1234" }
        // Pattern means: 
        //   (1) grab the first number in quotes → group(1)
        //   (2) grab the base → group(2)
        //   (3) grab the value → group(3)
        Pattern keyPattern = Pattern.compile(
            "\"(\\d+)\"\\s*:\\s*\\{[^}]*\"base\"\\s*:\\s*\"(\\d+)\"[^}]*\"value\"\\s*:\\s*\"([^\"]+)\""
        );
        Matcher matcher = keyPattern.matcher(json);

        while (matcher.find()) {
            BigInteger x = new BigInteger(matcher.group(1)); // x coordinate
            
            int base = Integer.parseInt(matcher.group(2));
            
            String valueStr = matcher.group(3);

            // base to BigInteger
            BigInteger y = new BigInteger(valueStr, base);

            xs.add(x);
            ys.add(y);
        }

       
        // Lagrange interpolation to figure out the constant term (the secret number)
        BigInteger constantTerm = lagrangeConstant(xs.subList(0, k), ys.subList(0, k));

        System.out.println("Secret constant c = " + constantTerm);
    }

    //finds the first number that matches a pattern (regex)
    private static int extractInt(String text, String regex) {
        Matcher m = Pattern.compile(regex).matcher(text);
        if (m.find()) {
            return Integer.parseInt(m.group(1)); // found text into a real number
        }
        throw new IllegalArgumentException("Could not find integer for pattern: " + regex);
    }

    // Lagrange interpolation
    // rebuild the hidden secret number (the constant term of the polynomial)
    private static BigInteger lagrangeConstant(List<BigInteger> xs, List<BigInteger> ys) {
        BigInteger result = BigInteger.ZERO; // result = 0
        
        for (int i = 0; i < xs.size(); i++) {
            BigInteger term = ys.get(i);
            BigInteger numerator = BigInteger.ONE;   
            BigInteger denominator = BigInteger.ONE; 

            for (int j = 0; j < xs.size(); j++) {
                if (i != j) {
                    numerator = numerator.multiply(xs.get(j).negate());
                    denominator = denominator.multiply(xs.get(i).subtract(xs.get(j)));
                }
            }

            BigInteger fraction = numerator.multiply(term).divide(denominator);
            result = result.add(fraction);
        }

        return result;
    }
}
