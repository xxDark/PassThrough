package dev.xdark.passthrough;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;

final class ServerChannelInitializer extends ChannelInitializer<Channel> {

  private final PacketPassThroughApi api;

  ServerChannelInitializer(PacketPassThroughApi api) {
    this.api = api;
  }

  @Override
  protected void initChannel(Channel channel) throws Exception {
    channel.pipeline().addFirst(new PlayerChannelInjector(api));
  }
}
