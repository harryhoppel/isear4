package org.spbgu.pmpu.athynia.central.matrix.impl;

import org.spbgu.pmpu.athynia.central.matrix.Matrix;
import org.spbgu.pmpu.athynia.central.matrix.MatrixOperator;
import org.spbgu.pmpu.athynia.central.matrix.Vector;

/**
 * User: A.Selivanov
 * Date: 02.06.2007
 */
public class LocalMatrixOperator implements MatrixOperator {
    public Matrix multiply(Matrix a, Matrix b) {
        int size = a.size();
        Matrix result = new Matrix(size);
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                result.setElement(i, j, computeMultiply(a.getRow(i), b.getColumn(j)));
            }
        }
        return result;
    }

    public Matrix inverse(Matrix matrix) {
        return matrix.invert();
    }

    private double computeMultiply(Vector column, Vector raw) {
        double result = 0.0;
        for (int i = 0; i < column.size(); i++) {
            result += column.getElement(i) * raw.getElement(i);
        }
        return result;
    }
}
