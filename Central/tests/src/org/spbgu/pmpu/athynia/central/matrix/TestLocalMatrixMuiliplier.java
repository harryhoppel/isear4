package org.spbgu.pmpu.athynia.central.matrix;

import junit.framework.TestCase;
import org.spbgu.pmpu.athynia.central.matrix.impl.LocalMatrixOperator;

/**
 * User: A.Selivanov
 * Date: 03.06.2007
 */
public class TestLocalMatrixMuiliplier extends TestCase {
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
//        Matrix A = new Matrix(new File("Central/matrixA.txt"));
        Matrix A = new Matrix(elementsA);
//        Matrix B = new Matrix(new File("Central/matrixB.txt"));
        Matrix B = new Matrix(elementsB);
        System.out.println("Finish opening, time: " + (System.currentTimeMillis() - currentTime) + "ms");
        currentTime = System.currentTimeMillis();
//        Matrix matrix = operator.multiply(new Matrix(elementsA), new Matrix(elementsB));
        Matrix matrix = operator.multiply(A, B);
        System.out.println(matrix);
        System.out.println("Finish calculating, time: " + (System.currentTimeMillis() - currentTime) + "ms");
//        assertArrayEquals(result, matrix.getValues());
    }

    public void testInverse() {
////        << 1.0, 2.0, 2.0>|< 4.0, 2.0, 1.0>|< 0.0, 1.0, 1.0>>
//
//      double[][] elementsA = {
//            {1.0, 2.0, 2.0},
//            {4.0, 2.0, 1.0},
//            {0.0, 1.0, 1.0}};
//        double[][] result = {
//            {1.0, 0.0, -2.0},
//            {-4.0, 1.0, 7.0},
//            {4.0, -1.0, -6.0}};
////        MatrixOperator operator = new LocalMatrixOperator();
//        Matrix mMatrix = new Matrix(new File("matrixA.txt"));
//        System.out.println("Start calculating matrix");
//        long currentTime = System.currentTimeMillis();
//        Matrix res = mMatrix.invert();
//        System.out.println(res.getValues()[99][99]);
//        System.out.println("Finish calculating, time: " + (System.currentTimeMillis() - currentTime) + "ms");
////        System.out.println(new Matrix(doubles).toString());
////        assertArrayEquals(doubles, result);
////        Matrix matrix = operator.inverse(new Matrix(elementsA));
////        printMatrix(matrix);
////        assertArrayEquals(result, matrix.getMatrixValues());

    }


    private void assertArrayEquals(double[][] arrayA, double[][] arrayB) {
        assertEquals(arrayA.length, arrayB.length);
        assertEquals(arrayA[0].length, arrayB[0].length);
        for (int i = 0; i < arrayA.length; i++) {
            for (int j = 0; j < arrayA[i].length; j++) {
                assertEquals(arrayA[i][j], arrayB[i][j]);
            }
        }
    }
}
