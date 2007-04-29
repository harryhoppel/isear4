package org.spbgu.pmpu.athynia.central.communications;

/**
 * User: vasiliy
 */
public interface WorkersExecutorSender {
    boolean runExecutorOnWorker(Worker worker, String executorClassName);
}
