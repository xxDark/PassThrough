package dev.xdark.passthrough;

import org.bukkit.plugin.Plugin;

@SuppressWarnings("ClassCanBeRecord")
final class PassThroughWrapper {

  final Plugin owner;
  final PacketPassThrough passThrough;

  PassThroughWrapper(Plugin owner, PacketPassThrough passThrough) {
    this.owner = owner;
    this.passThrough = passThrough;
  }
}
