package dev.xdark.passthrough;

import com.velocitypowered.natives.compression.VelocityCompressor;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.network.CompressionEncoder;

final class PacketCompressorDecorator extends CompressionEncoder {

  PacketCompressorDecorator(VelocityCompressor compressor, int compressionThreshold) {
    super(compressor, compressionThreshold);
  }

  @Override
  protected void encode(ChannelHandlerContext ctx, ByteBuf msg, ByteBuf out) throws Exception {
    out.writeBoolean(NetUtil.shouldPassThrough(ctx.channel()));
    super.encode(ctx, msg, out);
  }
}
