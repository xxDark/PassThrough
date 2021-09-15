package dev.xdark.passthrough;

import io.netty.util.AttributeKey;
import io.netty.util.AttributeMap;

final class NetUtil {

  private static final AttributeKey<Boolean> PASS_THROUGH = AttributeKey.newInstance("passthrough");

  private NetUtil() {}

  /*
  static void clear(AttributeMap map) {
    map.attr(PASS_THROUGH).remove();
  }
   */

  static void setPassThrough(AttributeMap map, boolean passThrough) {
    map.attr(PASS_THROUGH).set(passThrough);
  }

  static boolean shouldPassThrough(AttributeMap map) {
    Boolean value = map.attr(PASS_THROUGH).get();
    return value != null && value;
  }
}
