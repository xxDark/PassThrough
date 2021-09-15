package dev.xdark.passthrough;

import com.velocitypowered.natives.util.Natives;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelPromise;
import net.minecraft.network.protocol.login.ClientboundLoginCompressionPacket;

final class CompressionEnableDetector extends ChannelOutboundHandlerAdapter {

  private final PacketPassThroughApi api;

  CompressionEnableDetector(PacketPassThroughApi api) {
    this.api = api;
  }

  @Override
  public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise)
      throws Exception {
    if (msg instanceof ClientboundLoginCompressionPacket) {
      // assert !promise.isVoid();
      promise.addListener(
          result -> {
            if (result.isSuccess()) {
              ChannelPipeline pipeline = ctx.pipeline();
              // attach handlers
              pipeline.addAfter(
                  ServerConstants.ENCODER_HANDLER,
                  "pass_through-attribute",
                  new AttributeUpdateHandler(api));
              pipeline.addBefore(
                  ServerConstants.ENCODER_HANDLER,
                  ServerConstants.COMPRESS_HANDLER,
                  new PacketCompressorDecorator(
                      Natives.compress.get().create(-1), ServerUtil.getThreshold()));
              pipeline.remove(this);
            }
          });
    }
    ctx.write(msg, promise);
  }
}
