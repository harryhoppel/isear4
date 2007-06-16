package org.spbgu.pmpu.athynia.central.matrix.split;

import org.spbgu.pmpu.athynia.central.matrix.Matrix;
import org.spbgu.pmpu.athynia.central.network.communications.split.DataSplitter;

import java.util.Arrays;

/**
 * User: A.Selivanov
 * Date: 08.06.2007
 */
public class MatrixColumnSplitter<Value extends Matrix> implements DataSplitter<Value> {
    public String[] splitData(Value matrix, int parts) {
        int size = matrix.size();
        String[] result = new String[parts];
        Arrays.fill(result, "");
        int part = (int) (Math.floor(size / parts) + size % parts);
        int index;
        StringBuffer stringBuffer = new StringBuffer();
        for (index = 0; index < part; index++) {
            stringBuffer.append(matrix.getColumn(index).toString());
        }
        result[0] = stringBuffer.toString();

        int preIndex = part;
        int elseParts = 0;
        if (parts != 1) {
            elseParts = (size - part) / (parts - 1);
        }
        for (int j = 1; j < parts; j++) {
            stringBuffer = new StringBuffer();
            for (; index < size; index++) {
                if (index - preIndex == elseParts) {
                    preIndex = index;
                    break;
                }
                stringBuffer.append(matrix.getColumn(index).toString());
            }
            result[j] = stringBuffer.toString();
        }
        return result;
    }
}
