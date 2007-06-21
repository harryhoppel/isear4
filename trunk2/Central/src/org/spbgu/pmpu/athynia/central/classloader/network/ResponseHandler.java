package org.spbgu.pmpu.athynia.central.classloader.network;

/**
 * Author: Selivanov
 * Date: 09.03.2007
 * Time: 1:59:09
 */
public class ResponseHandler {
    private byte[] rsp = null;

    public synchronized boolean handleResponse(byte[] rsp) {
        this.rsp = rsp;
        this.notify();
        return true;
    }

    public synchronized byte[] waitForResponse() {
        while (this.rsp == null) {
            try {
                this.wait();
            } catch (InterruptedException e) {
                // interrupt
            }
        }
        return this.rsp;
    }
}
