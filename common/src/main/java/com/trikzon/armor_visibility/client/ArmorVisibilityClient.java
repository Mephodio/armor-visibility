package com.trikzon.armor_visibility.client;

import com.trikzon.armor_visibility.ArmorVisibility;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

public final class ArmorVisibilityClient {
    private static KeyBinding keyBinding;
    private static boolean keyWasDown;

    public static void initialize() {
        ArmorVisibilityClient.keyBinding = PlatformClient.registerKeyBinding(
                new Identifier(ArmorVisibility.MOD_ID, "armor_visibility_toggle"),
                GLFW.GLFW_KEY_V,
                "key.categories." + ArmorVisibility.MOD_ID
        );
        PlatformClient.registerClientTickEvent(ArmorVisibilityClient::onClientTick);
        PlatformClient.registerJoinEvent(ArmorVisibilityClient::onJoin);
    }

    private static void onClientTick(MinecraftClient client) {
        if (client.player == null) {
            return;
        }

        if (keyBinding.isPressed() && !keyWasDown) {
            keyWasDown = true;

            client.player.playSound(SoundEvents.BLOCK_TRIPWIRE_CLICK_ON, 0.5f, 1.0f);

            if (client.player.isSneaking()) {
                ArmorVisibility.save.allArmorVisibilityToggle = !ArmorVisibility.save.allArmorVisibilityToggle;
                client.player.addChatMessage(new TranslatableText(
                        "message." +
                                ArmorVisibility.MOD_ID +
                                ".all_armor_visibility_toggle." +
                                ArmorVisibility.save.allArmorVisibilityToggle
                ), true);
                ArmorVisibility.writeSaveFile();
                return;
            }

            ArmorVisibility.save.myArmorVisibilityToggle = !ArmorVisibility.save.myArmorVisibilityToggle;
            client.player.addChatMessage(new TranslatableText(
                    "message." +
                            ArmorVisibility.MOD_ID +
                            ".my_armor_visibility_toggle." +
                            ArmorVisibility.save.myArmorVisibilityToggle
            ), true);
            ArmorVisibility.writeSaveFile();

        } else if (!keyBinding.isPressed() && keyWasDown) {
            keyWasDown = false;
        }
    }

    private static void onJoin(ClientPlayerEntity player) {
        if (ArmorVisibility.save.showJoinMessage && isVisibilityToggled()) {
            player.sendMessage(new TranslatableText(
                    "message." + ArmorVisibility.MOD_ID + ".join",
                    visibleToString(ArmorVisibility.save.myArmorVisibilityToggle),
                    visibleToString(ArmorVisibility.save.allArmorVisibilityToggle)
            ));
        }
    }

    private static boolean isVisibilityToggled() {
        return !ArmorVisibility.save.myArmorVisibilityToggle || !ArmorVisibility.save.allArmorVisibilityToggle;
    }

    private static String visibleToString(boolean visible) {
        return visible ? "Visible" : "Invisible";
    }
}
