package com.mdjoon.mapdirection.mixin;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.map.MapDecoration;
import net.minecraft.item.map.MapDecorationType;
import net.minecraft.item.map.MapDecorationTypes;
import net.minecraft.item.map.MapState;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;
import java.util.Optional;

@Mixin(MapState.class)
public abstract class MapStateMixin {
    @Final
    @Shadow
    Map<String, MapDecoration> decorations;

    @Inject(method = "update", at = @At("TAIL"))
    private void showDirectionOutsideMap(
            PlayerEntity player,
            ItemStack map,
            CallbackInfo ci
    ) {
        if (player == null) return;

        String key = player.getName().getString();
        MapDecoration decoration = decorations.get(key);
        RegistryEntry<MapDecorationType> decoType = decoration.type();

        if(decoType == MapDecorationTypes.PLAYER_OFF_MAP || decoType == MapDecorationTypes.PLAYER_OFF_LIMITS) {
            byte x = decoration.x();
            byte z = decoration.z();
            byte rotation = (byte) ((player.getYaw() % 360 + 360) % 360 / 22.5);
            decorations.remove(key);

            decorations.put(
                    key,
                    new MapDecoration(
                            MapDecorationTypes.PLAYER,
                            x,
                            z,
                            rotation,
                            Optional.of(Text.of("Out of Map!"))
                    )
            );
        }
    }
}
