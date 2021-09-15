package dev.xdark.passthrough;

import net.minecraft.network.protocol.Packet;

public interface PacketPassThrough {

  boolean shouldPassThrough(Packet<?> packet);
}
