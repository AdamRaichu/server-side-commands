package io.github.adamraichu.servercommands.forge.commands;

import static io.github.adamraichu.servercommands.forge.ServerCommandsForge.CONFIG;
import static io.github.adamraichu.servercommands.forge.ServerCommandsForge.LOGGER;
import static io.github.adamraichu.servercommands.forge.ServerCommandsForge.toNumber;
import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

import java.text.ParseException;
import java.util.Objects;
import java.util.UUID;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;

public class TrackCommand {
  @SubscribeEvent
  public static void onRegisterCommandEvent(RegisterCommandsEvent event) {
    try {
      Integer requiredPermissionLevel = toNumber(CONFIG.getProperty("cmds.track.permissionLevel", "2")).intValue();
      event.getDispatcher().register(literal("track")
          .requires((source) -> source
              .hasPermission(requiredPermissionLevel))
          .then(argument("player", EntityArgument.player()).executes(ctx -> {
            return getCompassForPlayer(ctx.getSource(), EntityArgument.getPlayer(ctx, "player"));
          })));
      LOGGER.info("Command /track registered.");
    } catch (ParseException e) {
      e.printStackTrace();
    }
  }

  private static int getCompassForPlayer(CommandSourceStack source, ServerPlayer target) throws CommandSyntaxException {
    if (!source.isPlayer()) {
      source.sendFailure(Component.literal("Command must be run by a player!"));
      LOGGER.error("/track was called, but caller was not a player.");
      return 0;
    }

    LOGGER.info(
        "`/track " + target.getDisplayName().getString() + "` was called by " + source.getDisplayName().getString());

    Inventory inv = source.getPlayerOrException().getInventory();

    Item compassItem = Items.COMPASS;
    ItemStack compassItemStack = compassItem.getDefaultInstance();
    compassItemStack.setCount(1);

    compassItemStack.setTag(getUpdatedPosition(target));

    inv.add(compassItemStack);

    return Command.SINGLE_SUCCESS;
  }

  private static CompoundTag getUpdatedPosition(Player target) {
    CompoundTag compassData = new CompoundTag();
    byte falsey = 0;
    CompoundTag compassPos = new CompoundTag();
    compassPos.putInt("X", (int) Math.round(target.getX()));
    compassPos.putInt("Y", (int) Math.round(target.getY()));
    compassPos.putInt("Z", (int) Math.round(target.getZ()));

    compassData.putString("adamraichu:TrackedPlayer", target.getStringUUID());

    // TODO: Add name and tracking data to lore

    compassData.put("LodestonePos", compassPos);
    compassData.putByte("LodestoneTracked", falsey);

    // TODO: Dynamically get dimension
    compassData.putString("LodestoneDimension", "minecraft:overworld");

    // LOGGER.info("Target dimension: " +
    // target.getLevel().dimension().location().getPath());

    return compassData;
  }

  @SubscribeEvent
  public void onPlayerInteractEvent(PlayerInteractEvent.RightClickItem event) {
    Player player = event.getEntity();

    // Make sure that this is the server side.
    if (event.getSide().equals(LogicalSide.CLIENT) || player.isSpectator()) {
      return;
    }

    // Get item NBT.
    ItemStack item = event.getItemStack();
    CompoundTag tag = item.getTag();

    // Make sure that NBT is not null.
    if (Objects.isNull(tag)) {
      return;
    } else {
      if (!tag.contains("adamraichu:TrackedPlayer")) {
        return;
      }
    }

    item.setTag(getUpdatedPosition(
        event.getLevel().getPlayerByUUID(UUID.fromString(tag.getString("adamraichu:TrackedPlayer")))));
  }
}
