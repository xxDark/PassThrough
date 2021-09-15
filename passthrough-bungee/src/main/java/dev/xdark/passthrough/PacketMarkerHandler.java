package dev.xdark.passthrough;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import net.md_5.bungee.ServerConnector;
import net.md_5.bungee.UserConnection;
import net.md_5.bungee.netty.HandlerBoss;
import net.md_5.bungee.netty.PipelineUtils;

final class PacketMarkerHandler extends ChannelInboundHandlerAdapter {

  private ChannelHandlerContext client;

  PacketMarkerHandler() {}

  @Override
  public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
    ServerConnector connector =
        (ServerConnector)
            ProxyInternals.getPacketHandler(
                (HandlerBoss) ctx.pipeline().get(PipelineUtils.BOSS_HANDLER));
    UserConnection connection = ProxyInternals.getUserConnection(connector);
    client =
        ProxyInternals.getUserChannel(connection)
            .getHandle()
            .pipeline()
            .context(ProxyConstants.COMPRESS_HANDLER);
  }

  @Override
  public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    if (msg instanceof PacketWrapperMarker) {
      PacketWrapperMarker marker = (PacketWrapperMarker) msg;
      ByteBuf packet = marker.buf;
      marker.setReleased(true);
      ChannelHandlerContext client = this.client;
      client.writeAndFlush(packet, client.voidPromise());
      return;
    }
    ctx.fireChannelRead(msg);
  }
}
