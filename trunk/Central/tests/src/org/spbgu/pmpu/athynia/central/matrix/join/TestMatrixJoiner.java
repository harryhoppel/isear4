package org.spbgu.pmpu.athynia.central.matrix.join;

import junit.framework.TestCase;
import org.spbgu.pmpu.athynia.central.matrix.Matrix;
import org.spbgu.pmpu.athynia.central.matrix.Vector;
import org.spbgu.pmpu.athynia.central.network.DataJoiner;
import org.spbgu.pmpu.athynia.common.JoinPart;
import org.spbgu.pmpu.athynia.common.impl.JoinPartImpl;

/**
 * User: A.Selivanov
 * Date: 08.06.2007
 */
public class TestMatrixJoiner extends TestCase {
    public void testJoin() {
        DataJoiner<Matrix> joiner = new MatrixJoiner();
        double[] elementsA = {15.0, 35.0, 8.0};
        Vector vectorA = new Vector(elementsA);
        double[] elementsB = {71.0, 105.0, 12.0};
        Vector vectorB = new Vector(elementsB);
        double[] elementsC = {42.0, 72.0, 4.0};
        Vector vectorC = new Vector(elementsC);
        Matrix result = new Matrix(new double[][]{elementsA, elementsB, elementsC});

        JoinPart[] parts = new JoinPart[2];
        parts[0] = new JoinPartImpl("testKey", vectorA.toString() + vectorB.toString(), 0, 2);
        parts[1] = new JoinPartImpl("testKey", vectorC.toString(), 1, 2);
        joiner.setData(parts);
        Matrix matrix = joiner.getResult();
        assertArrayEquals(result.transpose().getValues(), matrix.getValues());


        double[][] elements = {
            {1.0, 2.0, 3.0, 4.0, 5.0},
            {1.0, 2.0, 3.0, 4.0, 5.0},
            {1.0, 2.0, 3.0, 4.0, 5.0},
            {1.0, 2.0, 3.0, 4.0, 5.0},
            {1.0, 2.0, 3.0, 4.0, 5.0}};
        parts = new JoinPart[3];
        parts[0] = new JoinPartImpl("testKey", new Vector(elements[0]).toString() + new Vector(elements[1]).toString() , 0, 3);
        parts[1] = new JoinPartImpl("testKey", new Vector(elements[2]).toString() , 1, 3);
        parts[2] = new JoinPartImpl("testKey", new Vector(elements[3]).toString() + new Vector(elements[4]).toString(), 2, 3);
        joiner.setData(parts);
        matrix = joiner.getResult();
        assertArrayEquals(new Matrix(elements).transpose().getValues(), matrix.getValues());
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
