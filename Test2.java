package catalog;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class Test2 {

    public static void main(String[] args) {
        List<int[]> points = new ArrayList<>(); // Valid points
        List<String> wrongTestCases = new ArrayList<>(); // To store wrong test cases
        int n = 0; // Declare n outside to use later
        int k = 0; // Declare k outside to use later

        try {
            // Create ObjectMapper instance
            ObjectMapper objectMapper = new ObjectMapper();

            // Read JSON file and parse it into JsonNode
            JsonNode rootNode = objectMapper.readTree(new File("resources/testCases2.json"));

            // Extract n and k from the "keys" object
            n = rootNode.get("keys").get("n").asInt();
            k = rootNode.get("keys").get("k").asInt();
            System.out.println("n = " + n + ", k = " + k);

            // Iterate over the keys from 1 to n
            for (int i = 1; i <= n; i++) {
                String key = String.valueOf(i);
                JsonNode pointNode = rootNode.get(key);

                if (pointNode != null) {
                    int x = Integer.parseInt(key);  // The key itself is x
                    int base = pointNode.get("base").asInt();  // Base of the y value
                    String encodedY = pointNode.get("value").asText();  // Encoded y value

                    System.out.println("Decoding y value: " + encodedY + " with base: " + base);
                    try {
                        BigInteger y = new BigInteger(encodedY, base);
                        points.add(new int[] { x, y.intValue() });
                    } catch (NumberFormatException e) {
                        wrongTestCases.add("Point (x = " + x + ", base = " + base + ", value = " + encodedY + ") - Invalid value");
                        System.out.println("Failed to decode point (x = " + x + ", base = " + base + ", value = " + encodedY + ")");
                    }

                } else {
                    wrongTestCases.add("Key " + key + " not found.");
                }
            }

            // Print the parsed valid points
            for (int[] point : points) {
                System.out.println("Point (x = " + point[0] + ", y = " + point[1] + ")");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        // Calculate the constant term c using Lagrange interpolation
        if (points.size() >= k) {
            BigInteger c = lagrangeInterpolation(points.subList(0, k)); // Use only first k points
            System.out.println("Constant term (c) = " + c);
        } else {
            System.out.println("Not enough points to determine the polynomial coefficients.");
        }

        // Print wrong test cases
        if (!wrongTestCases.isEmpty()) {
            System.out.println("Wrong test cases:");
            for (String wrongCase : wrongTestCases) {
                System.out.println(wrongCase);
            }
        }
    }

    private static BigInteger lagrangeInterpolation(List<int[]> points) {
        BigInteger c = BigInteger.ZERO;
        int n = points.size();

        for (int i = 0; i < n; i++) {
            int[] point = points.get(i);
            int x_i = point[0];
            BigInteger y_i = BigInteger.valueOf(point[1]);
            BigInteger term = y_i;

            for (int j = 0; j < n; j++) {
                if (i != j) {
                    int x_j = points.get(j)[0];
                    term = term.multiply(BigInteger.valueOf(-x_j)).divide(BigInteger.valueOf(x_i - x_j));
                }
            }
            
            c = c.add(term);
        }
        
        return c;
    }
}