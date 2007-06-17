package org.spbgu.pmpu.athynia.central.matrix;

import junit.framework.TestCase;
import org.spbgu.pmpu.athynia.central.matrix.impl.LocalMatrixOperator;

/**
 * User: A.Selivanov
 * Date: 03.06.2007
 */
public class TestLocalMatrixOperator extends TestCase {
    public void testMatrixMultiply() {
        double[][] elementsA = {
            {3.0, 8.0, 1.0},
            {2.0, 8.0, 9.0},
            {2.0, 0.0, 0.0}};

        double[][] elementsB = {
            {4.0, 6.0, 2.0},
            {0.0, 6.0, 4.0},
            {3.0, 5.0, 4.0}};

        double[][] result = {
            {28.0, 20.0, 27.0},
            {80.0, 48.0, 64.0},
            {58.0, 54.0, 48.0}};

        MatrixOperator operator = new LocalMatrixOperator();
        System.out.println("Start calculating matrix");
        long currentTime = System.currentTimeMillis();
//        Matrix A = new Matrix(new File("matrixA.txt"));
        Matrix A = new Matrix(elementsA);
//        Matrix B = new Matrix(new File("matrixB.txt"));
        Matrix B = new Matrix(elementsB);
        System.out.println("Finish opening, time: " + (System.currentTimeMillis() - currentTime) + "ms");
        currentTime = System.currentTimeMillis();
//        Matrix matrix = operator.multiply(new Matrix(elementsA), new Matrix(elementsB));
        Matrix matrix = operator.multiply(A, B);
//        System.out.println(matrix);
        System.out.println("Finish calculating, time: " + (System.currentTimeMillis() - currentTime) + "ms");
//        assertArrayEquals(result, matrix.getValues());
    }

    public void testInverse() {
      double[][] elementsA = {
            {1.0, 2.0, 2.0},
            {4.0, 2.0, 1.0},
            {0.0, 1.0, 1.0}};
        double[][] doublesResult = {
            {1.0, 0.0, -2.0},
            {-4.0, 1.0, 7.0},
            {4.0, -1.0, -6.0}};
        MatrixOperator operator = new LocalMatrixOperator();
        Matrix matrix = new Matrix(elementsA);
        Matrix result = operator.inverse(matrix);
        Matrix result2 = matrix.invert2();
        System.out.println("result2 = " + result2);
        assertEquals(result2, result);
        assertArrayEquals(doublesResult, result.getValues());
        result = operator.inverse(result);
        assertEquals(matrix, result);
    }

     public void testInverse2() {
//         System.gc();
//         AthyniaMatrix matrix = new AthyniaMatrix(new File("matrixInverse.txt"));
//         long currentTime = System.currentTimeMillis();
//         matrix.invert2();
//         System.out.println("Finish calculating, time: " + (System.currentTimeMillis() - currentTime) + "ms");
     }

    private void assertArrayEquals(double[][] arrayA, double[][] arrayB) {
        assertEquals(arrayA.length, arrayB.length);
        assertEquals(arrayA[0].length, arrayB[0].length);
        for (int i = 0; i < arrayA.length; i++) {
            for (int j = 0; j < arrayA[i].length; j++) {
                assertEquals(Matrix.getDouble(arrayA[i][j]), Matrix.getDouble(arrayB[i][j]));
            }
        }
    }
}
