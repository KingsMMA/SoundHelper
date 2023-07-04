package dev.kingrabbit.soundhelper.mixin;

import dev.kingrabbit.soundhelper.SoundHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.sound.SoundSystem;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SoundSystem.class)
public class SoundSystemMixin {

    @Inject(method = "play(Lnet/minecraft/client/sound/SoundInstance;)V", at = @At("RETURN"))
    public void play(SoundInstance sound, CallbackInfo ci) {
        if (sound == null || sound.getSound() == null) return;
        if (SoundHelper.isBlocked(sound)) return;
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) return;
        client.player.sendMessage(Text.of(
                "§2Sound: §a" + sound.getId().getPath() +
                        "\n§2Volume: §a" + sound.getVolume() +
                        "\n§2Pitch: §a" + sound.getPitch()
        ));
    }

}
