package dev.xdark.passthrough;

import com.google.common.base.Preconditions;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import net.minecraft.network.protocol.Packet;
import org.bukkit.Server;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;

public final class BukkitPlugin extends JavaPlugin implements Listener, PacketPassThroughApi {

  private final List<PassThroughWrapper> passThroughWrappers = new ArrayList<>();

  @Override
  public void onLoad() {
    Preconditions.checkState(
        getServer().getCurrentTick() == 0, "The plugin cannot be loaded after server boot");
    ServerUtil.ensureThresholdSet();
    Collection<ChannelFuture> endpoints = ServerUtil.getEndpoints();
    getLogger().info(String.format("Injecting into %d endpoints", endpoints.size()));
    for (ChannelFuture future : endpoints) {
      if (future.isDone()) {
        injectServerChannelFuture(future);
      } else {
        future.addListener((ChannelFutureListener) this::injectServerChannelFuture);
      }
    }
  }

  @Override
  public void onEnable() {
    Server server = getServer();
    server.getPluginManager().registerEvents(this, this);
    server
        .getServicesManager()
        .register(PacketPassThroughApi.class, this, this, ServicePriority.Highest);
  }

  @Override
  public void onDisable() {
    Preconditions.checkState(
        getServer().isStopping(),
        "The plugin must NOT be disabled with plugin reloaders, that will cause unexpected errors!");
  }

  private void injectServerChannelFuture(ChannelFuture future) {
    // assert future.isDone();
    if (!future.isSuccess()) {
      getLogger()
          .log(
              Level.WARNING,
              "Unable to inject into one of the endpoints, skipping",
              future.cause());
    } else {
      injectServerChannel(future.channel());
    }
  }

  private void injectServerChannel(Channel ch) {
    getLogger().info(String.format("Injecting into %s endpoint", ch));
    ch.pipeline().addLast("pass_through-init", new ServerChannelInitializer(this));
  }

  @EventHandler(priority = EventPriority.LOWEST)
  private void onPluginDisable(PluginDisableEvent e) {
    Plugin plugin = e.getPlugin();
    passThroughWrappers.removeIf(wrapper -> wrapper.owner == plugin);
  }

  @Override
  public void addPassThrough(Plugin owner, PacketPassThrough passThrough) {
    Preconditions.checkState(owner.isEnabled(), "plugin is not enabled");
    passThroughWrappers.add(new PassThroughWrapper(owner, passThrough));
  }

  @Override
  public void removePassThrough(PacketPassThrough passThrough) {
    List<PassThroughWrapper> passThroughWrappers = this.passThroughWrappers;
    for (int j = passThroughWrappers.size(); j != 0; ) {
      int k;
      if (passThroughWrappers.get((k = --j)).passThrough == passThrough) {
        passThroughWrappers.remove(k);
        return;
      }
    }
  }

  @Override
  public void removePassThrough(Plugin owner) {
    Preconditions.checkState(owner.isEnabled(), "plugin is not enabled");
    passThroughWrappers.removeIf(wrapper -> wrapper.owner == owner);
  }

  @Override
  public boolean shouldPassThrough(Packet<?> packet) {
    List<PassThroughWrapper> passThroughWrappers = this.passThroughWrappers;
    int j = passThroughWrappers.size();
    if (j == 0) {
      return false;
    }
    for (int i = 0; i < j; i++) {
      if (!passThroughWrappers.get(i).passThrough.shouldPassThrough(packet)) {
        return false;
      }
    }
    return true;
  }
}
