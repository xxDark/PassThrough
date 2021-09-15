package dev.xdark.passthrough;

import com.google.common.base.Preconditions;
import io.netty.channel.ChannelFuture;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerConnectionListener;

import java.lang.reflect.Field;
import java.util.Collection;

final class ServerUtil {

  private ServerUtil() {}

  static int getThreshold() {
    return MinecraftServer.getServer().getCompressionThreshold();
  }

  static void ensureThresholdSet() {
    Preconditions.checkState(
        getThreshold() >= 0, "Compression is not enabled, check server.properties");
  }

  static Collection<ChannelFuture> getEndpoints() {
    Field endpointsField;
    try {
      endpointsField = ServerConnectionListener.class.getDeclaredField("channels");
    } catch (NoSuchFieldException ex) {
      throw new IllegalStateException("Could not obtain field with endpoints", ex);
    }
    try {
      endpointsField.setAccessible(true);
      return (Collection<ChannelFuture>)
          endpointsField.get(MinecraftServer.getServer().getConnection());
    } catch (IllegalAccessException ex) {
      throw new IllegalStateException("Could not obtain endpoints", ex);
    }
  }
}
