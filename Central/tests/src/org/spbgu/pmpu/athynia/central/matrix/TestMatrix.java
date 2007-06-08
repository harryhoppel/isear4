package org.spbgu.pmpu.athynia.central.matrix;

import junit.framework.TestCase;

import java.io.File;
import java.io.IOException;
import java.util.Random;

/**
 * User: A.Selivanov
 * Date: 02.06.2007
 */
public class TestMatrix extends TestCase {
    /**
     * generate matrixs
     * @throws IOException
     */
    public void testInit() throws IOException {
        final int COLUMN_SIZE = 3;
        final int ROW_SIZE = 3;
        double[][] elements = new double[COLUMN_SIZE][ROW_SIZE];
        Random random = new Random();
        for (int i = 0; i < COLUMN_SIZE; i++) {
            for (int j = 0; j < ROW_SIZE; j++) {
                elements[i][j] = random.nextDouble();
//                elements[i][j] = random.nextInt(10);
            }
        }
        Matrix matrix = new Matrix(elements);
//        File out = new File("matrixA.txt");
//        if(!out.exists())
//            out.createNewFile();
//        BufferedWriter writer = new BufferedWriter(new FileWriter(out));
//        writer.write(matrix.toString());
//        writer.flush();

//        for (int i = 0; i < COLUMN_SIZE; i++) {
//            for (int j = 0; j < ROW_SIZE; j++) {
//                elements[i][j] = random.nextDouble();
//                elements[i][j] = random.nextInt(10);
//            }
//        }
//        matrix = new Matrix(elements);
//        out = new File("matrixB.txt");
//        if(!out.exists())
//            out.createNewFile();
//        writer = new BufferedWriter(new FileWriter(out));
//        writer.write(matrix.toString());
//        writer.flush();

    }

    public void testMatrixFileInit() throws IOException {
        final double[][] result = {
            {7.0, 4.0, 6.0},
            {2.0, 5.0, 2.0},
            {6.0, 6.0, 0.0}
        };
        Matrix matrix = new Matrix(new File("tests/data/matrix/testMatrix.txt"));
        System.out.println("matrix = " + matrix);
        assertArrayEquals(matrix.getValues(), result);
    }

    public void testDeterminate() {
        double[][] elementsA = {
          {1.0, 2.0, 2.0},
          {4.0, 2.0, 1.0},
          {0.0, 1.0, 1.0}};
        final double result = 1.0;
        Matrix matrix = new Matrix(elementsA);
        assertEquals(matrix.determinate(), result);
        System.out.println("matrix = " + matrix);
        System.out.println(matrix.toString());
    }

    public void testCopy(){
        double[][] elementsA = {
          {1.0, 2.0, 2.0},
          {4.0, 2.0, 1.0},
          {0.0, 1.0, 1.0}};
        Matrix matrix = new Matrix(elementsA);
        assertArrayEquals(matrix.getValues(), matrix.copy().getValues());
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
