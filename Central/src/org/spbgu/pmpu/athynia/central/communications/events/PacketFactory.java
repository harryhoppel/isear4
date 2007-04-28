package org.spbgu.pmpu.athynia.central.communications.events;

import org.spbgu.pmpu.athynia.central.communications.events.impl.CommunicationPacketImpl;

/**
 * User: vasiliy
 */
public class PacketFactory {
    public static CommunicationPacket createSendEvent(String data, int partNumber) {
        return new CommunicationPacketImpl();
    }
}
