package org.spbgu.pmpu.athynia.central.matrix.join;

import org.spbgu.pmpu.athynia.central.network.DataJoiner;
import org.spbgu.pmpu.athynia.central.matrix.Matrix;
import org.spbgu.pmpu.athynia.common.JoinPart;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

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
        ArrayList<JoinPart> filteredRetrievedParts = new ArrayList<JoinPart>();
        for (JoinPart retrievedPart : receivedJoinParts) {
            if (retrievedPart != null && retrievedPart.getWholePartsNumber() != -1) { // -1 - in case of null value found in worker's index
                filteredRetrievedParts.add(retrievedPart);
            }
        }
        if (filteredRetrievedParts.size() == 0) {
            return null;
        }
        if (filteredRetrievedParts.get(0).getWholePartsNumber() != filteredRetrievedParts.size()) {
            return null;
        }
        Collections.sort(filteredRetrievedParts, new Comparator<JoinPart>() {
            public int compare(JoinPart o1, JoinPart o2) {
                return ((Integer) o1.getPartNumber()).compareTo(o2.getPartNumber());
            }
        });

        StringBuffer ret = new StringBuffer();
        for (int index = 0; index < filteredRetrievedParts.size(); index++) {
            JoinPart joinPart = filteredRetrievedParts.get(index);
            if (joinPart.getPartNumber() != index) {
                return null;
            }
            System.out.println("**** get part = " + joinPart.getPartNumber() + ", value = " + joinPart.getValue());
            ret.append(joinPart.getValue());
        }
        return new Matrix(ret.toString());
    }
}
