package net.vercte.satchels.network;

import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.HandlerThread;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.vercte.satchels.client.ClientPacketHandler;
import net.vercte.satchels.network.packets.SatchelStatusPacketS2C;
import net.vercte.satchels.network.packets.ToggleSatchelPacketC2S;

public class ModPackets {
    public static void registerPayloadHandlers(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar("1").executesOn(HandlerThread.MAIN);

        registrar.playToServer(
                ToggleSatchelPacketC2S.TYPE,
                ToggleSatchelPacketC2S.STREAM_CODEC,
                (p, cx) -> ToggleSatchelPacketC2S.handle(p, (ServerPlayer) cx.player())
        );

//        registrar.playToServer(
//                ClientConfigUpdatePacketC2S.TYPE,
//                ClientConfigUpdatePacketC2S.STREAM_CODEC,
//                (p, cx) -> ClientConfigUpdatePacketC2S.handle(p, (ServerPlayer) cx.player())
//        );

        registrar.playToClient(
                SatchelStatusPacketS2C.TYPE,
                SatchelStatusPacketS2C.STREAM_CODEC,
                ClientPacketHandler::handleSatchelStatusPacket
        );
    }
}
