package org.spbgu.pmpu.athynia.central.matrix.impl;

import org.apache.log4j.Logger;
import org.spbgu.pmpu.athynia.central.matrix.Matrix;
import org.spbgu.pmpu.athynia.central.matrix.MatrixOperator;
import org.spbgu.pmpu.athynia.central.matrix.join.MatrixJoiner;
import org.spbgu.pmpu.athynia.central.matrix.split.MatrixRowSplitter;
import org.spbgu.pmpu.athynia.central.matrix.task.MatrixMuiltiplyTask;
import org.spbgu.pmpu.athynia.central.matrix.task.SaveToFileTask;
import org.spbgu.pmpu.athynia.central.network.NetworkRunner;
import org.spbgu.pmpu.athynia.central.network.communications.CommunicationException;
import org.spbgu.pmpu.athynia.central.network.communications.split.impl.EmptySplitter;
import org.spbgu.pmpu.athynia.central.network.impl.DataImpl;
import org.spbgu.pmpu.athynia.central.network.impl.NetworkRunnerImpl;

/**
 * User: A.Selivanov
 * Date: 03.06.2007
 */
public class RemoteMatrixMultiplier implements MatrixOperator {
    private static Logger LOG = Logger.getLogger(RemoteMatrixMultiplier.class);

    public Matrix multiply(Matrix matrixA, Matrix matrixB) {
        try {
            NetworkRunner<Matrix> networkRunner = new NetworkRunnerImpl<Matrix>();
            long time = System.currentTimeMillis();
            LOG.debug("Execute SaveToFileTask: ");
            networkRunner.runRemotely(SaveToFileTask.class,
                new DataImpl<Matrix>("matrixA", matrixA), new EmptySplitter<Matrix>());
            LOG.debug("Finish SaveToFileTask: it takes " + (System.currentTimeMillis() - time) + "ms");

            time = System.currentTimeMillis();
            LOG.debug("Execute MatrixMuiltiplyTask: " + time);
            Matrix result = networkRunner.runRemotely(MatrixMuiltiplyTask.class,
                new DataImpl<Matrix>("matrixB", matrixB), new MatrixRowSplitter<Matrix>(),
                new DataImpl<Matrix>("matrix-multiply", null), new MatrixJoiner());
            LOG.debug("Finish MatrixMuiltiplyTask: it takes " + (System.currentTimeMillis() - time) + "ms");

            LOG.debug("Result length = " + result.size() + ", result[0][0] = " + result.getValues()[0][0]);
            return result;
        } catch (CommunicationException e) {
            LOG.error("Exception while compute matrixA", e);
        }
        return null;
    }

   //not working now
    public Matrix inverse(Matrix matrix) {
//        try {
//            NetworkRunner networkRunner = new NetworkRunnerImpl();
//
//            String joined = networkRunner.runRemotely(MatrixInverseTask.class,
//                new DataImpl<Matrix>("matrix", matrix), new MatrixRowSplitter(),
//                new DataImpl<Matrix>("matrix", null), new MatrixJoiner());
//            LOG.debug("Has result size = " + joined.length());
//            return new Matrix(joined);
//        } catch (CommunicationException e) {
//            LOG.error("Exception while compute matrix", e);
//        }
        return null;
    }
}
