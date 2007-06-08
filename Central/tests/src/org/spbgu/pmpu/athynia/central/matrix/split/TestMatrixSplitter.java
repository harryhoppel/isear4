package org.spbgu.pmpu.athynia.central.matrix.split;

import junit.framework.TestCase;
import org.spbgu.pmpu.athynia.central.network.communications.split.DataSplitter;
import org.spbgu.pmpu.athynia.central.matrix.Matrix;
import org.spbgu.pmpu.athynia.central.matrix.task.MatrixMuiltiplyTask;

/**
 * User: A.Selivanov
 * Date: 08.06.2007
 */
public class TestMatrixSplitter extends TestCase {
    public void testRowSplit() {
        DataSplitter<Matrix> splitter = new MatrixRowSplitter<Matrix>();
        double[][] rowElements = {
            {1.0, 2.0, 3.0, 4.0, 5.0},
            {1.0, 2.0, 3.0, 4.0, 5.0},
            {1.0, 2.0, 3.0, 4.0, 5.0},
            {1.0, 2.0, 3.0, 4.0, 5.0},
            {1.0, 2.0, 3.0, 4.0, 5.0}};

        String[] test1 = {"5 1.0 1.0 1.0 1.0 1.0 \n5 2.0 2.0 2.0 2.0 2.0 \n5 3.0 3.0 3.0 3.0 3.0 \n5 4.0 4.0 4.0 4.0 4.0 \n5 5.0 5.0 5.0 5.0 5.0 \n"};
        String[] test2 = {
            "5 1.0 1.0 1.0 1.0 1.0 \n5 2.0 2.0 2.0 2.0 2.0 \n5 3.0 3.0 3.0 3.0 3.0 \n",
            "5 4.0 4.0 4.0 4.0 4.0 \n5 5.0 5.0 5.0 5.0 5.0 \n"};
        String[] test3 = {
            "5 1.0 1.0 1.0 1.0 1.0 \n5 2.0 2.0 2.0 2.0 2.0 \n5 3.0 3.0 3.0 3.0 3.0 \n",
            "5 4.0 4.0 4.0 4.0 4.0 \n",
            "5 5.0 5.0 5.0 5.0 5.0 \n"};
        String[] test4 = {
            "5 1.0 1.0 1.0 1.0 1.0 \n5 2.0 2.0 2.0 2.0 2.0 \n",
            "5 3.0 3.0 3.0 3.0 3.0 \n",
            "5 4.0 4.0 4.0 4.0 4.0 \n",
            "5 5.0 5.0 5.0 5.0 5.0 \n"};
        String[] test5 = {
            "5 1.0 1.0 1.0 1.0 1.0 \n",
            "5 2.0 2.0 2.0 2.0 2.0 \n",
            "5 3.0 3.0 3.0 3.0 3.0 \n",
            "5 4.0 4.0 4.0 4.0 4.0 \n",
            "5 5.0 5.0 5.0 5.0 5.0 \n"};
        Matrix matrix = new Matrix(rowElements);
        String[] result = splitter.splitData(matrix, 1);
        assertStringArrayEquals(result, test1);

        result = splitter.splitData(matrix, 2);
        assertStringArrayEquals(result, test2);

        result = splitter.splitData(matrix, 3);
        assertStringArrayEquals(result, test3);

        result = splitter.splitData(matrix, 4);
        assertStringArrayEquals(result, test4);

        result = splitter.splitData(matrix, 5);
        assertStringArrayEquals(result, test5);
    }

    public void testColumnSplit() {
        DataSplitter<Matrix> splitter = new MatrixColumnSplitter<Matrix>();
        double[][] rowElements = {
            {1.0, 1.0, 1.0, 1.0, 1.0},
            {2.0, 2.0, 2.0, 2.0, 2.0},
            {3.0, 3.0, 3.0, 3.0, 3.0},
            {4.0, 4.0, 4.0, 4.0, 4.0},
            {5.0, 5.0, 5.0, 5.0, 5.0}};

        String[] test1 = {"5 1.0 1.0 1.0 1.0 1.0 \n5 2.0 2.0 2.0 2.0 2.0 \n5 3.0 3.0 3.0 3.0 3.0 \n5 4.0 4.0 4.0 4.0 4.0 \n5 5.0 5.0 5.0 5.0 5.0 \n"};
        String[] test2 = {
            "5 1.0 1.0 1.0 1.0 1.0 \n5 2.0 2.0 2.0 2.0 2.0 \n5 3.0 3.0 3.0 3.0 3.0 \n",
            "5 4.0 4.0 4.0 4.0 4.0 \n5 5.0 5.0 5.0 5.0 5.0 \n"};
        String[] test3 = {
            "5 1.0 1.0 1.0 1.0 1.0 \n5 2.0 2.0 2.0 2.0 2.0 \n5 3.0 3.0 3.0 3.0 3.0 \n",
            "5 4.0 4.0 4.0 4.0 4.0 \n",
            "5 5.0 5.0 5.0 5.0 5.0 \n"};
        String[] test4 = {
            "5 1.0 1.0 1.0 1.0 1.0 \n5 2.0 2.0 2.0 2.0 2.0 \n",
            "5 3.0 3.0 3.0 3.0 3.0 \n",
            "5 4.0 4.0 4.0 4.0 4.0 \n",
            "5 5.0 5.0 5.0 5.0 5.0 \n"};
        String[] test5 = {
            "5 1.0 1.0 1.0 1.0 1.0 \n",
            "5 2.0 2.0 2.0 2.0 2.0 \n",
            "5 3.0 3.0 3.0 3.0 3.0 \n",
            "5 4.0 4.0 4.0 4.0 4.0 \n",
            "5 5.0 5.0 5.0 5.0 5.0 \n"};
        Matrix matrix = new Matrix(rowElements);
        String[] result = splitter.splitData(matrix, 1);
        assertStringArrayEquals(result, test1);

        result = splitter.splitData(matrix, 2);
        assertStringArrayEquals(result, test2);

        result = splitter.splitData(matrix, 3);
        assertStringArrayEquals(result, test3);

        result = splitter.splitData(matrix, 4);
        assertStringArrayEquals(result, test4);

        result = splitter.splitData(matrix, 5);
        assertStringArrayEquals(result, test5);
    }

    public void test() {
        assertEquals(MatrixMuiltiplyTask.getStartIndex(10, 0, 5), 0);
        assertEquals(MatrixMuiltiplyTask.getStartIndex(10, 1, 10), 1);
        assertEquals(MatrixMuiltiplyTask.getStartIndex(10, 1, 3), 4);
        assertEquals(MatrixMuiltiplyTask.getStartIndex(10, 2, 4), 6);
    }

    private void assertStringArrayEquals(String[] first, String[] second) {
        assertEquals(first.length, second.length);
        for (int i = 0; i < first.length; i++) {
            assertEquals(first[i], second[i]);
        }
    }
}
