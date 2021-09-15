package dev.xdark.passthrough;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.md_5.bungee.compress.PacketDecompressor;

import java.util.List;

final class PacketDecompressorDecorator extends PacketDecompressor {

  @Override
  protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
    if (in.readBoolean()) {
      out.add(new PacketWrapperMarker(in.retainedSlice()));
      in.readerIndex(in.writerIndex());
    } else {
      super.decode(ctx, in, out);
    }
  }
}
