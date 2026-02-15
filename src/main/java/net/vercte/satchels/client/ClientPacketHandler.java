package net.vercte.satchels.client;

import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.vercte.satchels.network.packets.SatchelStatusPacketS2C;

public class ClientPacketHandler {
    public static void handleSatchelStatusPacket(SatchelStatusPacketS2C packet, IPayloadContext cx) {
        SatchelStatusPacketS2C.handle(packet, cx.player());
    }
}
