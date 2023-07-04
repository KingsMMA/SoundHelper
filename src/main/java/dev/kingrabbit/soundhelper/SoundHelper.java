package dev.kingrabbit.soundhelper;

import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Environment(EnvType.CLIENT)
public class SoundHelper implements ClientModInitializer {

    public static List<BlockedSound> blockedSounds = new ArrayList<>();

    @Override
    public void onInitializeClient() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(
                    ClientCommandManager.literal("blocked_sounds")
                            .executes(context -> {
                                if (blockedSounds.isEmpty()) {
                                    context.getSource().sendFeedback(Text.of("§aNo combinations are currently being blocked."));
                                    return 1;
                                }
                                context.getSource().sendFeedback(Text.of("§2The following combinations are blocked:"));
                                for (BlockedSound blockedSound : blockedSounds) {
                                    String message = "    §7• §2";
                                    if (blockedSound.sound() == null) message += "all sounds";
                                    else message += blockedSound.sound();
                                    if (blockedSound.volume() != null || blockedSound.pitch() != null) {
                                        if (blockedSound.volume() != null) {
                                            message += "§a with volume §2" + blockedSound.volume();
                                            if (blockedSound.pitch() != null) {
                                                message += "§a and pitch §2" + blockedSound.pitch();
                                            }
                                        } else message += "§a with pitch §2" + blockedSound.pitch();
                                    }
                                    context.getSource().sendFeedback(Text.of(message));
                                }
                                return 1;
                            }));
            dispatcher.register(
                    ClientCommandManager.literal("block_sound")
                            .then(ClientCommandManager.argument("sound", StringArgumentType.word())
                                    .then(ClientCommandManager.argument("volume", FloatArgumentType.floatArg())
                                            .then(ClientCommandManager.argument("pitch", FloatArgumentType.floatArg())
                                                    .executes(context -> {
                                                        String sound = context.getArgument("sound", String.class);
                                                        float volume = context.getArgument("volume", float.class);
                                                        float pitch = context.getArgument("pitch", float.class);
                                                        blockSound(context, sound, volume, pitch);
                                                        return 1;
                                                    }))
                                            .executes(context -> {
                                                String sound = context.getArgument("sound", String.class);
                                                float volume = context.getArgument("volume", float.class);
                                                blockSound(context, sound, volume, null);
                                                return 1;
                                            }))
                                    .executes(context -> {
                                        String sound = context.getArgument("sound", String.class);
                                        blockSound(context, sound, null, null);
                                        return 1;
                                    }))
                            .executes(context -> {
                                context.getSource().sendError(Text.of("§cPlease provide a sound to block."));
                                return 1;
                            }));
            dispatcher.register(
                    ClientCommandManager.literal("unblock_sound")
                            .then(ClientCommandManager.argument("sound", StringArgumentType.word())
                                    .then(ClientCommandManager.argument("volume", FloatArgumentType.floatArg())
                                            .then(ClientCommandManager.argument("pitch", FloatArgumentType.floatArg())
                                                    .executes(context -> {
                                                        String sound = context.getArgument("sound", String.class);
                                                        float volume = context.getArgument("volume", float.class);
                                                        float pitch = context.getArgument("pitch", float.class);
                                                        unblockSound(context, sound, volume, pitch);
                                                        return 1;
                                                    }))
                                            .executes(context -> {
                                                String sound = context.getArgument("sound", String.class);
                                                float volume = context.getArgument("volume", float.class);
                                                unblockSound(context, sound, volume, null);
                                                return 1;
                                            }))
                                    .executes(context -> {
                                        String sound = context.getArgument("sound", String.class);
                                        unblockSound(context, sound, null, null);
                                        return 1;
                                    }))
                            .executes(context -> {
                                context.getSource().sendError(Text.of("§cPlease provide a sound to unblock."));
                                return 1;
                            }));
        });
    }

    public static boolean isBlocked(SoundInstance sound) {
        if (blockedSounds.isEmpty()) return false;
        for (BlockedSound blockedSound : blockedSounds) {
            if (blockedSound.sound() != null && !Objects.equals(blockedSound.sound(), sound.getId().getPath()))
                continue;
            if (blockedSound.volume() != null && !Objects.equals(blockedSound.volume(), sound.getVolume())) continue;
            if (blockedSound.pitch() != null && !Objects.equals(blockedSound.pitch(), sound.getPitch())) continue;
            return true;
        }
        return false;
    }

    public static void blockSound(CommandContext<FabricClientCommandSource> context, String sound, Float volume, Float pitch) {
        if (sound.equalsIgnoreCase("none") || sound.equalsIgnoreCase("null")) sound = null;
        if (volume != null && volume < 0) volume = null;
        if (pitch != null && pitch < 0) pitch = null;
        BlockedSound blockedSoundInstance = new BlockedSound(sound, volume, pitch);
        if (blockedSounds.contains(blockedSoundInstance)) {
            context.getSource().sendError(Text.of("§cThis combination has already been blocked!"));
            return;
        }
        blockedSounds.add(blockedSoundInstance);
        String message = "§aBlocked §2";
        if (sound == null) message += "all sounds";
        else message += sound;
        if (volume != null || pitch != null) {
            if (volume != null) {
                message += "§a with volume §2" + volume;
                if (pitch != null) {
                    message += "§a and pitch §2" + pitch;
                }
            } else message += "§a with pitch §2" + pitch;
        }
        message += "§a.";
        context.getSource().sendFeedback(Text.of(message));
    }

    public static void unblockSound(CommandContext<FabricClientCommandSource> context, String sound, Float volume, Float pitch) {
        if (sound.equalsIgnoreCase("none") || sound.equalsIgnoreCase("null")) sound = null;
        if (volume != null && volume < 0) volume = null;
        if (pitch != null && pitch < 0) pitch = null;
        BlockedSound blockedSoundInstance = new BlockedSound(sound, volume, pitch);
        if (!blockedSounds.contains(blockedSoundInstance)) {
            context.getSource().sendError(Text.of("§cThis combination has not been blocked!"));
            return;
        }
        blockedSounds.remove(blockedSoundInstance);
        String message = "§aUnblocked §2";
        if (sound == null) message += "all sounds";
        else message += sound;
        if (volume != null || pitch != null) {
            if (volume != null) {
                message += "§a with volume §2" + volume;
                if (pitch != null) {
                    message += "§a and pitch §2" + pitch;
                }
            } else message += "§a with pitch §2" + pitch;
        }
        message += "§a.";
        context.getSource().sendFeedback(Text.of(message));
    }

}
