PassThrough
===========
**DISCLAIMER**: this plugin is useless unless you are using compression.

What is this?
-------------

The plugin allows proxies to pass packets through: they will not be handled by the proxy, and forwarded directly to the
player. The goal is to reduce CPU usage on proxies that could've been spent on packet decompression/compression.

---
Building:
---------

Requires JDK **16** to build.  
Clone Paper and BungeeCord,  
For Paper, run `./gradlew publishToMavenLocal`  
For BungeeCord, run `./mvn install`  
Finally, run `./gradlew build` to build this plugin.

---
Tuning:
---------

By default, the plugin does not filter any packets.  
You must decide by yourself which packets you want to keep from being proceed by the proxy.  
Plugin exposes it's API via Bukkit services,  
see `dev.xdark.passthrough.PacketPassThroughApi` for more info.

---
Example:
---------

```Java
import dev.xdark.passthrough.PacketPassThroughApi;
import net.minecraft.network.protocol.game.ClientboundDisconnectPacket;
import net.minecraft.network.protocol.game.ClientboundLoginPacket;
import net.minecraft.network.protocol.login.ClientboundGameProfilePacket;
import org.bukkit.plugin.java.JavaPlugin;

public final class MyFilterPlugin extends JavaPlugin {

  @Override
  public void onEnable() {
    PacketPassThroughApi api =
        getServer().getServicesManager().getRegistration(PacketPassThroughApi.class).getProvider();
    api.addPassThrough(
        this,
        packet ->
            // Filter any packet except these that are required for login/disconnect
            !(packet instanceof ClientboundGameProfilePacket)
                && !(packet instanceof ClientboundLoginPacket)
                && !(packet instanceof ClientboundDisconnectPacket));
  }
}
```

---
WARNING
=======

The compression on both proxy and the server **MUST** be set to the same value, otherwise things will **NOT** work
properly.

---