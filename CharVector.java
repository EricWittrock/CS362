public class CharVector {
    private double[] vector = new double[26];

    public CharVector(char c) {
        int index = -1;
        if (c <= 'Z' && c >= 'A') {
            index = c - 'A';
        } else if (c <= 'z' && c >= 'a') {
            index = c - 'a';
        }

        if (index != -1) {
            vector[index] = 1.0;
        }
    }

    public CharVector(double vec[]) {
        this.vector = vec;
    }

    public double[] getVector() {
        return vector;
    }

    public void scale(double scale) {
        for (int i = 0; i < vector.length; i++) {
            vector[i] *= scale;
        }
    }

    public void add(CharVector v) {
        for (int i = 0; i < vector.length; i++) {
            vector[i] += v.vector[i];
        }
    }

    public double dot(CharVector v) {
        double result = 0.0;
        for (int i = 0; i < vector.length; i++) {
            result += this.vector[i] * v.vector[i];
        }
        return result;
    }

    public double magnitude() {
        double sum = 0.0;
        for (int i = 0; i < vector.length; i++) {
            sum += vector[i] * vector[i];
        }
        return Math.sqrt(sum);
    }

    public CharVector copy() {
        double[] newVec = new double[26];
        for (int i = 0; i < 26; i++) {
            newVec[i] = this.vector[i];
        }
        return new CharVector(newVec);
    }

    public void multiply(CharVector v) {
        for (int i = 0; i < vector.length; i++) {
            this.vector[i] *= v.vector[i];
        }
    }
}
