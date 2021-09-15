package dev.xdark.passthrough;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelPipeline;
import net.md_5.bungee.netty.PipelineUtils;
import net.md_5.bungee.protocol.DefinedPacket;
import net.md_5.bungee.protocol.PacketWrapper;
import net.md_5.bungee.protocol.packet.Login;
import net.md_5.bungee.protocol.packet.SetCompression;

@ChannelHandler.Sharable
final class CompressionEnableDetector extends ChannelInboundHandlerAdapter {

  static final CompressionEnableDetector INSTANCE = new CompressionEnableDetector();

  private CompressionEnableDetector() {}

  @Override
  public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    ctx.fireChannelRead(msg);
    if (msg instanceof PacketWrapper) {
      DefinedPacket packet = ((PacketWrapper) msg).packet;
      if (packet instanceof SetCompression) {
        // replace handler
        ChannelPipeline pipeline = ctx.pipeline();
        pipeline.replace(
            ProxyConstants.DECOMPRESS_HANDLER,
            ProxyConstants.DECOMPRESS_HANDLER,
            new PacketDecompressorDecorator());
        pipeline.addBefore(
                PipelineUtils.BOSS_HANDLER, "pass_through-write", new PacketMarkerHandler());
        pipeline.remove(this);
      } else if (packet instanceof Login) {
        ctx.pipeline().remove(this);
      }
    }
  }
}
