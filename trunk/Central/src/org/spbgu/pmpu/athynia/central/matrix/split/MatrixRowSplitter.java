package org.spbgu.pmpu.athynia.central.matrix.split;

import org.spbgu.pmpu.athynia.central.matrix.Matrix;
import org.spbgu.pmpu.athynia.central.network.communications.split.DataSplitter;

import java.util.Arrays;

/**
 * User: A.Selivanov
 * Date: 06.06.2007
 */
public class MatrixRowSplitter<Value extends Matrix> implements DataSplitter<Value> {
    public String[] splitData(Value matrix, int parts) {
        int size = matrix.size();
        String[] result = new String[parts];
        Arrays.fill(result, "");
        int part = (int) (Math.floor(size / parts) + size % parts);
        int index;
        for (index = 0; index < part; index++) {
            result[0] += matrix.getRow(index).toString();
        }

        int preIndex = part;
        int elseParts = 0;
        if (parts != 1) {
            elseParts = (size - part) / (parts - 1);
        }
        for (int j = 1; j < parts; j++) {
            for (;index < size; index++) {
                if (index - preIndex == elseParts) {
                    preIndex = index;
                    break;
                }
                result[j] += matrix.getRow(index).toString();
            }
        }
        return result;
    }
}
