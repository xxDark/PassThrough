package dev.xdark.passthrough;

import com.google.common.base.Preconditions;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.plugin.Plugin;

public final class BungeePlugin extends Plugin {

  @Override
  public void onEnable() {
    BungeeCord proxy = (BungeeCord) getProxy();
    Preconditions.checkState(
        proxy.config.getCompressionThreshold() >= 0,
        "Compression is not enabled, check config.yml");
    ProxyInternals.setup();
  }
}
