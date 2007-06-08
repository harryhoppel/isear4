package org.spbgu.pmpu.athynia.central.matrix;

import junit.framework.TestCase;

/**
 * User: A.Selivanov
 * Date: 03.06.2007
 */
public class TestVector extends TestCase {
    public void testSeriazation() {
        final double[] elementsA = {3.0, 8.0, 1.0};
        final int size = elementsA.length;
        final int index = 7;
        final int matrixSize = 10;

        Vector vectorA = new Vector(elementsA);
        Vector result = new Vector(vectorA.toBinaryForm());
        assertArrayEquals(result.getElements(), elementsA);
        assertEquals(result.getSize(), size);
        assertEquals(result.getVectorIndex(), 0);

        vectorA = new Vector(elementsA, index, matrixSize);
        result = new Vector(vectorA.toBinaryForm());
        assertEquals(result.getSize(), size);
        assertEquals(result.getVectorIndex(), index);
        assertEquals(result.getWholePartsNumber(), matrixSize);
        assertArrayEquals(result.getElements(), elementsA);
    }

    private void assertArrayEquals(double[] arrayA, double[] arrayB) {
        assertEquals(arrayA.length, arrayB.length);
        for (int i = 0; i < arrayA.length; i++) {
            assertEquals(arrayA[i], arrayB[i]);
        }
    }
}
