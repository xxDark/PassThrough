package dev.xdark.passthrough;

import net.minecraft.network.protocol.Packet;
import org.bukkit.plugin.Plugin;

public interface PacketPassThroughApi {

  void addPassThrough(Plugin owner, PacketPassThrough passThrough);

  void removePassThrough(PacketPassThrough passThrough);

  void removePassThrough(Plugin owner);

  boolean shouldPassThrough(Packet<?> packet);
}
