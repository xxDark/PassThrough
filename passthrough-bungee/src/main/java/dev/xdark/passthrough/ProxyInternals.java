package dev.xdark.passthrough;

import net.md_5.bungee.ServerConnector;
import net.md_5.bungee.UserConnection;
import net.md_5.bungee.netty.ChannelWrapper;
import net.md_5.bungee.netty.HandlerBoss;
import net.md_5.bungee.netty.PacketHandler;
import net.md_5.bungee.netty.PipelineUtils;
import sun.misc.Unsafe;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;

final class ProxyInternals {

  private static final MethodHandle MH_USER;
  private static final MethodHandle MH_HANDLER;
  private static final MethodHandle MH_CH;

  private ProxyInternals() {}

  static UserConnection getUserConnection(ServerConnector connector) {
    try {
      return (UserConnection) MH_USER.invokeExact(connector);
    } catch (Throwable t) {
      throw new IllegalStateException("Could not get user from connector", t);
    }
  }

  static PacketHandler getPacketHandler(HandlerBoss handlerBoss) {
    try {
      return (PacketHandler) MH_HANDLER.invokeExact(handlerBoss);
    } catch (Throwable t) {
      throw new IllegalStateException("Could not get packet handler", t);
    }
  }

  static ChannelWrapper getUserChannel(UserConnection userConnection) {
    try {
      return (ChannelWrapper) MH_CH.invokeExact(userConnection);
    } catch (Throwable t) {
      throw new IllegalStateException("Could not get user's channel wrapper", t);
    }
  }

  static void setup() {}

  static {
    Unsafe unsafe;
    try {
      Field field = Unsafe.class.getDeclaredField("theUnsafe");
      field.setAccessible(true);
      unsafe = (Unsafe) field.get(null);
    } catch (NoSuchFieldException | IllegalAccessException ex) {
      throw new IllegalStateException("Could not obtain sun.misc.Unsafe", ex);
    }
    MethodHandles.Lookup lookup;
    try {
      Field field = MethodHandles.Lookup.class.getDeclaredField("IMPL_LOOKUP");
      MethodHandles.publicLookup();
      lookup =
          (MethodHandles.Lookup)
              unsafe.getObject(unsafe.staticFieldBase(field), unsafe.staticFieldOffset(field));
    } catch (NoSuchFieldException ex) {
      throw new IllegalStateException("Could not obtain Lookup$IMPL_LOOKUP", ex);
    }
    try {
      MH_USER = lookup.findGetter(ServerConnector.class, "user", UserConnection.class);
    } catch (NoSuchFieldException | IllegalAccessException ex) {
      throw new IllegalStateException("Could not obtain ServerConnector#user", ex);
    }
    try {
      MH_HANDLER = lookup.findGetter(HandlerBoss.class, "handler", PacketHandler.class);
    } catch (NoSuchFieldException | IllegalAccessException ex) {
      throw new IllegalStateException("Could not obtain HandlerBoss#handler", ex);
    }
    try {
      MH_CH = lookup.findGetter(UserConnection.class, "ch", ChannelWrapper.class);
    } catch (NoSuchFieldException | IllegalAccessException ex) {
      throw new IllegalStateException("Could not obtain UserConnection#ch", ex);
    }
    try {
      Field field = PipelineUtils.class.getDeclaredField(ProxyConstants.FRAME_PREPENDER_FIELD);
      unsafe.putObject(
          unsafe.staticFieldBase(field),
          unsafe.staticFieldOffset(field),
          new Varint21LengthFieldPrependerDecorator());
    } catch (NoSuchFieldException ex) {
      throw new IllegalStateException("Could not inject into PipelineUtils#BASE", ex);
    }
  }
}
