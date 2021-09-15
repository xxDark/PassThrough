package dev.xdark.passthrough;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;

final class PlayerChannelInitializer extends ChannelInitializer<Channel> {

  private final PacketPassThroughApi api;

  PlayerChannelInitializer(PacketPassThroughApi api) {
    this.api = api;
  }

  @Override
  protected void initChannel(Channel ch) throws Exception {
    ch.pipeline()
        .addAfter(
            ServerConstants.ENCODER_HANDLER,
            "pass_through-detect",
            new CompressionEnableDetector(api));
  }
}
