package dev.xdark.passthrough;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ServerChannel;
import net.md_5.bungee.netty.PipelineUtils;
import net.md_5.bungee.protocol.Varint21LengthFieldPrepender;

@ChannelHandler.Sharable
final class Varint21LengthFieldPrependerDecorator extends Varint21LengthFieldPrepender {

  Varint21LengthFieldPrependerDecorator() {}

  @Override
  public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
    if (!(ctx.channel() instanceof ServerChannel)) {
      ctx.pipeline()
          .addAfter(
                  PipelineUtils.FRAME_PREPENDER,
                  "pass_through-detect",
              CompressionEnableDetector.INSTANCE);
    }
  }
}
