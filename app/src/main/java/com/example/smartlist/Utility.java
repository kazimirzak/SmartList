package com.example.smartlist;

import androidx.sqlite.db.SimpleSQLiteQuery;

import com.example.smartlist.Database.Product;

import java.util.Arrays;
import java.util.Comparator;

public class Utility {

    public static final double substitutionCost = 0.5;
    public static final double insertionCost = 1;
    public static final double deletionCost = 1;

    /**
     * Implementation of the Levenshtein distance algorithm.
     */

    public static double levenshteinDistance(String a, String b) {
        double[][] matrix = new double[a.length() + 1][b.length() + 1];
        for(double[] m : matrix) {
            Arrays.fill(m, 0);
        }
        for(int i = 1; i <= a.length(); i++) {
            matrix[i][0] = i;
        }
        for(int i = 1; i <= b.length(); i++) {
            matrix[0][i] = i;
        }
        for(int i = 1; i <= b.length(); i++) {
            for(int j = 1; j <= a.length(); j++) {
                double subCost = a.charAt(j - 1) == b.charAt(i - 1) ? 0 : substitutionCost;
                double min = Math.min(matrix[j-1][i] + deletionCost, matrix[j][i-1] + insertionCost);
                matrix[j][i] = Math.min(min, matrix[j - 1][i - 1] + subCost);
            }
        }
        return matrix[a.length()][b.length()];
    }

    /**
     * Returns a comparator that will compare two product on their levenshtein distance from the
     * string s.
     */

    public static Comparator<Product> bestMatchComparator(String s) {
        return (o1, o2) -> {
            double matchO1 = Utility.levenshteinDistance(o1.description, s);
            double matchO2 = Utility.levenshteinDistance(o2.description, s);
            return Double.compare(matchO1, matchO2);
        };
    }

    /**
     * Returns a comparator that will compare the price of two product, if they are the same,
     * it will compare them by their levenshtein distnace from the string s.
     */

    public static Comparator<Product> bestMatchAndPriceComparator(String s) {
        return (o1, o2) -> {
            double matchO1 = Utility.levenshteinDistance(o1.description, s);
            double matchO2 = Utility.levenshteinDistance(o2.description, s);
            int comparison = Double.compare(o1.unitPrice, o2.unitPrice);
            if(comparison != 0) {
                return comparison;
            } else {
                return Double.compare(matchO1, matchO2);
            }
        };
    }

    /**
     * Will take the input string and split on spaces. Will the create a raw query where it selects
     * all columns from the products table. The WHERE clause is using the split and taking every
     * string from where and doing like %split% on the description. The chaining of like is done
     * with and.
     */

    public static SimpleSQLiteQuery stringSplitRawQuery(String input) {
        StringBuilder rawQuery = new StringBuilder("SELECT * FROM products WHERE");
        String[] split = input.split(" ");
        for(int i = 0; i < split.length; i++) {
            rawQuery.append(" description LIKE '%").append(split[i]).append("%'");
            if(i != split.length - 1) {
                rawQuery.append(" AND");
            }
        }
        return new SimpleSQLiteQuery(rawQuery.toString());
    }

    /**
     * Will take the input string and split on spaces. Will the create a raw query where it selects
     * all columns from the products table. The WHERE clause is using the split and taking every
     * string from where and doing like %split% on the description. The chaining of like is done
     * with and. Lastly it groups by description.
     */

    public static SimpleSQLiteQuery stringSplitRawQueryGroupByDescription(String input) {
        StringBuilder rawQuery = new StringBuilder("SELECT * FROM products WHERE");
        String[] split = input.split(" ");
        for(int i = 0; i < split.length; i++) {
            rawQuery.append(" description LIKE '%").append(split[i]).append("%'");
            if(i != split.length - 1) {
                rawQuery.append(" AND");
            }
        }
        rawQuery.append(" GROUP BY description");
        return new SimpleSQLiteQuery(rawQuery.toString());
    }
}
