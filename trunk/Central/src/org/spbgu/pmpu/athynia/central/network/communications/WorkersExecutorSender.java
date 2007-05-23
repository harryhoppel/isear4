package org.spbgu.pmpu.athynia.central.network.communications;

import org.spbgu.pmpu.athynia.central.network.Worker;

/**
 * User: vasiliy
 */
public interface WorkersExecutorSender {
    boolean runExecutorOnWorker(Worker worker, String executorClassName);
}
