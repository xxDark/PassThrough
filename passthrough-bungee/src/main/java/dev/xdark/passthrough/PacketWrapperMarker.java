package dev.xdark.passthrough;

import io.netty.buffer.ByteBuf;
import net.md_5.bungee.protocol.PacketWrapper;

final class PacketWrapperMarker extends PacketWrapper {

  PacketWrapperMarker(ByteBuf buf) {
    super(null, buf);
  }
}
