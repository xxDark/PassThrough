package dev.xdark.passthrough;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import net.minecraft.network.protocol.Packet;

final class AttributeUpdateHandler extends ChannelOutboundHandlerAdapter {

  private final PacketPassThroughApi api;

  AttributeUpdateHandler(PacketPassThroughApi api) {
    this.api = api;
  }

  @Override
  public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise)
      throws Exception {
    if (msg instanceof Packet) {
      NetUtil.setPassThrough(ctx.channel(), api.shouldPassThrough((Packet<?>) msg));
    }
    ctx.write(msg, promise);
  }
}
