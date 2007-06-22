package org.spbgu.pmpu.athynia.central.matrix.join;

import org.spbgu.pmpu.athynia.central.network.DataJoiner;
import org.spbgu.pmpu.athynia.central.matrix.Matrix;
import org.spbgu.pmpu.athynia.common.JoinPart;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.StringTokenizer;

/**
 * User: A.Selivanov
 * Date: 06.06.2007
 */
public class MatrixJoiner implements DataJoiner<Matrix> {
    JoinPart[] receivedJoinParts;

    public void setData(JoinPart[] receivedData) {
        receivedJoinParts = receivedData;
    }

    public Matrix getResult() {
        Matrix result = null;
        ArrayList<JoinPart> filteredRetrievedParts = new ArrayList<JoinPart>();
        for (JoinPart retrievedPart : receivedJoinParts) {
            if (retrievedPart != null && retrievedPart.getWholePartsNumber() != -1) { // -1 - in case of null value found in worker's index
                filteredRetrievedParts.add(retrievedPart);
            }
        }
        if (filteredRetrievedParts.size() == 0) {
            return null;
        }
//        if (filteredRetrievedParts.get(0).getWholePartsNumber() != filteredRetrievedParts.size()) {
//            return null;
//        }
        Collections.sort(filteredRetrievedParts, new Comparator<JoinPart>() {
            public int compare(JoinPart o1, JoinPart o2) {
                return ((Integer) o1.getPartNumber()).compareTo(o2.getPartNumber());
            }
        });

        int currentVectorPos = 0;
        result = new Matrix();
        for (int index = 0; index < filteredRetrievedParts.size(); index++, currentVectorPos++) {
            JoinPart joinPart = filteredRetrievedParts.get(index);
            if (joinPart.getPartNumber() != index) {
                return null;
            }
            StringTokenizer tokenizer = new StringTokenizer(joinPart.getValue(), Matrix.DELIMETERS);
            int size = Integer.parseInt(tokenizer.nextToken());
            result.setSize(size);
            int pos = 0;
            while (tokenizer.hasMoreTokens()) {
                if (pos != size) {
                    double value = Double.parseDouble(tokenizer.nextToken());
                    result.setElement(pos, currentVectorPos, value);
                    pos++;
                } else {
                    pos = 0;//just size
                    tokenizer.nextToken();
                    currentVectorPos++;
                }
            }

        }
        return result;
    }
}
