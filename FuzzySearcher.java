import java.util.ArrayList;

public class FuzzySearcher 
{
    static ArrayList<String> findMatches(String dbText, int column, double threshold) {
        ArrayList<String> matches = new ArrayList<>();
        String[] rows = dbText.split("\n");
        for (String row : rows) {
            String[] columns = row.split(",");
            if (columns.length > column) {
                double similarity = getSimilarity(columns[column], dbText);
                if (similarity >= threshold) {
                    matches.add(row);
                }
            }
        }
        return matches;
    }

    private static double getSimilarity(String s1, String s2) {
        int maxLen = Math.max(s1.length(), s2.length());
        double[] kernel1 = new double[maxLen];
        double[] kernel2 = new double[maxLen];

        for (int i = 0; i < maxLen; i++) {
            kernel1[i] = ((double) i / maxLen) * 0.5 + 0.5;
            kernel2[i] = 1.0 - ((double) i / maxLen) * 0.5;
        }

        double cos1 = cosSimilarity(s1, s2, kernel1);
        double cos2 = cosSimilarity(s1, s2, kernel2);

        return (cos1 + cos2) / 2.0;
    }

    private static double cosSimilarity(String s1, String s2, double[] kernel) {
        CharVector v1 = new CharVector(new double[26]);
        CharVector v2 = new CharVector(new double[26]);

        for (int i = 0; i < s1.length(); i++) {
            char c = s1.charAt(i);
            CharVector cv = new CharVector(c);
            cv.scale(kernel[i]);
            v1.add(cv);
        }

        for (int i = 0; i < s2.length(); i++) {
            char c = s2.charAt(i);
            CharVector cv = new CharVector(c);
            cv.scale(kernel[i]);
            v2.add(cv);
        }

        double dot = v1.dot(v2);
        double cos = dot / (v1.magnitude() * v2.magnitude());

        return cos;
    }
}
