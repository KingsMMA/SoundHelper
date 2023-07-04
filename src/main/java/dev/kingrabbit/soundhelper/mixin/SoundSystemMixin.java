package dev.kingrabbit.soundhelper.mixin;

import dev.kingrabbit.soundhelper.SoundHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.client.sound.SoundSystem;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SoundSystem.class)
public class SoundSystemMixin {

    @Shadow @Final private SoundManager loader;

    @Inject(method = "play(Lnet/minecraft/client/sound/SoundInstance;)V", at = @At("HEAD"), cancellable = true)
    public void play(SoundInstance sound, CallbackInfo cir) {
        if (sound == null) return;
        sound.getSoundSet(loader);
        if (sound.getSound() == null) return;
        if (SoundHelper.isBlocked(sound)) {
            cir.cancel();
            return;
        }
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) return;
        client.player.sendMessage(Text.of(
                "§2Sound: §a" + sound.getId().getPath() +
                        "\n§2Volume: §a" + sound.getVolume() +
                        "\n§2Pitch: §a" + sound.getPitch()
        ));
    }

}
