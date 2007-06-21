package org.spbgu.pmpu.athynia.central.matrix.impl;

import org.apache.log4j.Logger;
import org.spbgu.pmpu.athynia.central.matrix.Matrix;
import org.spbgu.pmpu.athynia.central.matrix.MatrixOperator;
import org.spbgu.pmpu.athynia.central.matrix.join.MatrixJoiner;
import org.spbgu.pmpu.athynia.central.matrix.split.MatrixRowSplitter;
import org.spbgu.pmpu.athynia.central.matrix.split.MatrixRowIndexSplitter;
import org.spbgu.pmpu.athynia.central.matrix.task.MatrixMuiltiplyTask;
import org.spbgu.pmpu.athynia.central.matrix.task.SaveToFileTask;
import org.spbgu.pmpu.athynia.central.matrix.task.MatrixInverseTask;
import org.spbgu.pmpu.athynia.central.network.NetworkRunner;
import org.spbgu.pmpu.athynia.central.network.communications.CommunicationException;
import org.spbgu.pmpu.athynia.central.network.communications.split.impl.EmptySplitter;
import org.spbgu.pmpu.athynia.central.network.impl.DataImpl;
import org.spbgu.pmpu.athynia.central.network.impl.NetworkRunnerImpl;

/**
 * User: A.Selivanov
 * Date: 03.06.2007
 */
public class RemoteMatrixOperator implements MatrixOperator {
    private static Logger LOG = Logger.getLogger(RemoteMatrixOperator.class);

    public Matrix multiply(Matrix matrixA, Matrix matrixB) {
        try {
            NetworkRunner<Matrix> networkRunner = new NetworkRunnerImpl<Matrix>();
            long time = System.currentTimeMillis();
            LOG.debug("Execute SaveToFileTask: ");
            networkRunner.runRemotely(SaveToFileTask.class,
                new DataImpl<Matrix>("matrixA", matrixA), new EmptySplitter<Matrix>());
            LOG.debug("Finish SaveToFileTask: it takes " + (System.currentTimeMillis() - time) + "ms");

            LOG.debug("Execute MatrixMuiltiplyTask");
            Matrix result = networkRunner.runRemotely(MatrixMuiltiplyTask.class,
                new DataImpl<Matrix>("matrixB", matrixB), new MatrixRowSplitter(),
                new DataImpl<Matrix>("matrix-multiply", null), new MatrixJoiner());
            LOG.debug("Finish MatrixMuiltiplyTask: it takes " + (System.currentTimeMillis() - time) + "ms");

            if (result != null) {
                LOG.debug("Result length = " + result.size() + ", result[0][0] = " + result.getValues()[0][0]);
            }
            return result;
        } catch (CommunicationException e) {
            LOG.error("Exception while compute matrixA", e);
        }
        return null;
    }

    public Matrix inverse(Matrix matrix) {
        try {
            NetworkRunner<Matrix> networkRunner = new NetworkRunnerImpl<Matrix>();

            LOG.debug("Execute SaveToFileTask: ");
            long time = System.currentTimeMillis();
            networkRunner.runRemotely(SaveToFileTask.class,
                new DataImpl<Matrix>("matrix", matrix), new EmptySplitter<Matrix>());
            LOG.debug("Finish SaveToFileTask: it takes " + (System.currentTimeMillis() - time) + "ms");

            LOG.debug("Execute MatrixInverseTask");
            time = System.currentTimeMillis();
            Matrix result = networkRunner.runRemotely(MatrixInverseTask.class,
                new DataImpl<Matrix>("matrix", matrix), new MatrixRowIndexSplitter(),
                new DataImpl<Matrix>("InverseMatrix", null), new MatrixJoiner());
            LOG.debug("Finish MatrixInverseTask: it takes " + (System.currentTimeMillis() - time) + "ms");
            if (result != null) {
                LOG.debug("Result length = " + result.size() + ", result[0][0] = " + result.getValues()[0][0]);
            }
            return result;
        } catch (CommunicationException e) {
            LOG.error("Exception while compute matrix", e);
        }
        return null;
    }
}
