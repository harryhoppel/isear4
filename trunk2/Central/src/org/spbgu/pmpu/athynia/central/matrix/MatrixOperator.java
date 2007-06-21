package org.spbgu.pmpu.athynia.central.matrix;

/**
 * User: A.Selivanov
 * Date: 02.06.2007
 */
public interface MatrixOperator {
    Matrix multiply(Matrix a, Matrix b);
    Matrix inverse(Matrix a);
}
