package org.spbgu.pmpu.athynia.central.matrix;

import org.spbgu.pmpu.athynia.central.matrix.impl.RemoteMatrixMultiplier;
import org.spbgu.pmpu.athynia.central.DataManager;
import org.spbgu.pmpu.athynia.common.settings.Settings;
import org.apache.log4j.Logger;

import java.io.File;


/**
 * User: A.Selivanov
 * Date: 08.06.2007
 */
public class MatrixExecutor implements Runnable{
    private Logger LOG = Logger.getLogger(MatrixExecutor.class);
    static final Settings matrixSetting = DataManager.getInstance().getData(Settings.class).childSettings("tasks").childSettings("matrix");

    public void run() {
        LOG.info("try to read matrix");
        Matrix matrixA = new Matrix(new File(matrixSetting.getValue("matrixA")));
        Matrix matrixB = new Matrix(new File(matrixSetting.getValue("matrixB")));
        LOG.info("matrixA.size= " + matrixA.toString().length());
        LOG.info("matrixB.size= " + matrixB.toString().length());
        LOG.info("Start sending the code");
        MatrixOperator operator = new RemoteMatrixMultiplier();
        long currentTime = System.currentTimeMillis();
        operator.multiply(matrixA, matrixB);
        LOG.info("Finish calculating, time: " + (System.currentTimeMillis() - currentTime) + "ms");

    }
}
