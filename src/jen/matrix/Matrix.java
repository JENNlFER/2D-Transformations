package jen.matrix;

import lombok.Getter;
import lombok.Setter;

public class Matrix {

    @Getter @Setter private boolean enabled = true;
    @Getter boolean complex = false;
    private final double[] matrix = new double[9];

    public Matrix() {
        for (int i = 0; i < 9; ++i) {
            matrix[i] = (i % 4 == 0 ? 1 : 0);
        }
    }

    public Matrix(double... values) {
        System.arraycopy(values, 0, matrix, 0, 9);
    }

    private Matrix matrixMult(Matrix m1) {
        if (!m1.isEnabled()) return this;
        Matrix m2 = new Matrix();
        m2.matrix[0] = cell(m1, 0, 0, 3);
        m2.matrix[1] = cell(m1, 0, 1, 3);
        m2.matrix[2] = cell(m1, 0, 2, 3);
        m2.matrix[3] = cell(m1, 3, 0, 6);
        m2.matrix[4] = cell(m1, 3, 1, 6);
        m2.matrix[5] = cell(m1, 3, 2, 6);
        m2.matrix[6] = cell(m1, 6, 0, 9);
        m2.matrix[7] = cell(m1, 6, 1, 9);
        m2.matrix[8] = cell(m1, 6, 2, 9);
        return m2;
    }

    public Matrix mult(Matrix m1) {
        if (m1.isComplex()) {
            Matrix m2 = matrixMult(new Translation(-((Transformation) m1).getCx(),
                    -((Transformation) m1).getCy()));
            m2 = m2.matrixMult(m1);
            return m2.matrixMult(new Translation(((Transformation) m1).getCx(),
                    ((Transformation) m1).getCy()));
        } else {
            return matrixMult(m1);
        }
    }

    private double cell(Matrix m, int x, int y, int e) {
        double sum = 0;
        while(x < e) {
            sum += matrix[x] * m.matrix[y];
            x++;
            y += 3;
        }
        return sum;
    }

    double set(int index, double value) {
        return matrix[index] = value;
    }

    public double get(int index) {
        return matrix[index];
    }
}
