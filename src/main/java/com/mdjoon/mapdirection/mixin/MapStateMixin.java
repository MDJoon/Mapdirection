package com.mdjoon.mapdirection.mixin;

import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.saveddata.maps.MapDecoration;
import net.minecraft.world.level.saveddata.maps.MapDecorationType;
import net.minecraft.world.level.saveddata.maps.MapDecorationTypes;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;
import java.util.Optional;

@Mixin(MapItemSavedData.class)
public abstract class MapStateMixin {
    @Final
    @Shadow
    private Map<String, net.minecraft.world.level.saveddata.maps.MapDecoration> decorations;

    @Inject(method = "tickCarriedBy", at = @At("TAIL"))
    private void showDirectionOutsideMap(
            Player player, ItemStack itemStack, ItemFrame placedInFrame, CallbackInfo ci
    ) {
        if (player == null) return;

        String key = player.getName().getString();
        MapDecoration decoration = decorations.get(key);if(decoration == null) return;
        Holder<MapDecorationType> decoType = decoration.type();

        if(decoType == MapDecorationTypes.PLAYER_OFF_MAP || decoType == MapDecorationTypes.PLAYER_OFF_LIMITS) {
            byte x = decoration.x();
            byte z = decoration.y();
            byte rotation = (byte) ((player.getYRot() % 360 + 360) % 360 / 22.5);
            decorations.remove(key);

            decorations.put(
                    key,
                    new MapDecoration(
                            MapDecorationTypes.PLAYER,
                            x,
                            z,
                            rotation,
                            Optional.of(Component.literal("Out of Map!"))
                    )
            );
        }
    }
}
