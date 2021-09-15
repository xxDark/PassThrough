package dev.xdark.passthrough;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

final class PlayerChannelInjector extends ChannelInboundHandlerAdapter {

  private final PacketPassThroughApi api;

  PlayerChannelInjector(PacketPassThroughApi api) {
    this.api = api;
  }

  @Override
  public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    ctx.fireChannelRead(msg);
    ((Channel) msg).pipeline().addLast(new PlayerChannelInitializer(api));
  }
}
