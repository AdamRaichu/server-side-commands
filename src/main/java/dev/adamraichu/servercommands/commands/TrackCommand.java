package dev.adamraichu.servercommands.commands;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

import java.text.ParseException;
import java.util.Objects;
import java.util.UUID;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import dev.adamraichu.servercommands.ServerCommands;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
// import net.minecraft.nbt.NbtList;
// import net.minecraft.nbt.NbtString;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class TrackCommand {
  public static void register() {
    try {
      Integer requiredPermissionLevel = ServerCommands
          .toNumber(ServerCommands.CONFIG.getProperty("cmds.track.permissionLevel", "2")).intValue();

      CommandRegistrationCallback.EVENT
          .register((dispatcher, registryAccess, environment) -> dispatcher
              .register(literal("track")
                  .requires((source) -> source.hasPermissionLevel(requiredPermissionLevel))
                  .then(argument("player", EntityArgumentType.player())
                      .executes(ctx -> {
                        return getCompassForPlayer(ctx.getSource(), EntityArgumentType.getPlayer(ctx, "player"));
                      }))));
      UseItemCallback.EVENT.register((p, w, h) -> ItemUseListener(p, w, h));
      ServerCommands.LOGGER.info("Command `/track` registered.");
    } catch (ParseException e) {
      e.printStackTrace();
    }
  }

  private static int getCompassForPlayer(ServerCommandSource source, ServerPlayerEntity target)
      throws CommandSyntaxException {
    if (!(source.isExecutedByPlayer())) {
      source.sendError(Text.literal("Command must be run by a player!"));
      ServerCommands.LOGGER.error("/track was called, but caller was not a player.");
      return 0;
    }

    ServerCommands.LOGGER
        .info(
            "`/track " + target.getDisplayName().getString() + " `was called by "
                + source.getDisplayName().getString());

    // Get player inventory
    PlayerInventory inv = source.getPlayerOrThrow().getInventory();

    // Create compass
    Item compassItem = Items.COMPASS;
    ItemStack compassItemStack = compassItem.getDefaultStack();
    compassItemStack.setCount(1);

    compassItemStack.setNbt(getUpdatedPosition(source.getWorld().getEntityById(target.getId())));

    // Give compass to caller
    inv.offerOrDrop(compassItemStack);

    return Command.SINGLE_SUCCESS;
  }

  private static TypedActionResult<ItemStack> ItemUseListener(PlayerEntity player, World world, Hand hand) {
    ItemStack heldItem = player.getStackInHand(hand);

    if (player.isSpectator()) {
      return new TypedActionResult<ItemStack>(ActionResult.PASS, heldItem);
    }

    ServerWorld sWorld = player.getServer().getOverworld();

    NbtCompound itemNbt = heldItem.getNbt();

    if (itemNbt.contains("adamraichu:TrackedPlayer")) {
      Entity entityToTrack = sWorld.getEntity(UUID.fromString(itemNbt.getString("adamraichu:TrackedPlayer")));
      if (Objects.isNull(entityToTrack)) {
        Text entityNotFoundText = Text
            .of("No entity could be found with the UUID in the compass NBT. Try running /track again.");
        player.sendMessage(entityNotFoundText);
        ServerCommands.LOGGER.warn("Player " + player.getDisplayName().getString() + " has a bad tracking compass.");
      }
      heldItem.setNbt(getUpdatedPosition(entityToTrack));
    }

    return new TypedActionResult<ItemStack>(ActionResult.PASS, heldItem);
  }

  private static NbtCompound getUpdatedPosition(Entity target) {
    // Define lodestone data
    NbtCompound compassData = new NbtCompound();
    byte falsey = 0;
    NbtCompound compassPos = new NbtCompound();
    compassPos.putInt("X", (int) Math.round(target.getX()));
    compassPos.putInt("Y", (int) Math.round(target.getY()));
    compassPos.putInt("Z", (int) Math.round(target.getZ()));

    // Add tracking id
    compassData.putString("adamraichu:TrackedPlayer", target.getUuidAsString());

    // TODO: Add name and tracking data to lore
    /*
     * NbtCompound displayData = new NbtCompound();
     * NbtList loreList = new NbtList();
     * loreList.add(NbtString.of("\"Last location:\""));
     * loreList.add(NbtString.of("\"X Y Z\""));
     * displayData.put("Lore", loreList);
     * displayData.putString("Name", "\"Tracking [Name]\"");
     */

    compassData.put("LodestonePos", compassPos);
    compassData.putByte("LodestoneTracked", falsey);
    // TODO: Dynamically get dimension
    compassData.putString("LodestoneDimension", "minecraft:overworld");

    return compassData;
  }
}
