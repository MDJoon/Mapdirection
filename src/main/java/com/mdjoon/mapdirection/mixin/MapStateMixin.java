package com.mdjoon.mapdirection.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MapRenderer;
import net.minecraft.client.renderer.state.MapRenderState;
import net.minecraft.client.renderer.texture.TextureAtlas;
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
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;
import java.util.Optional;

@Mixin(MapRenderer.class)
public class MapStateMixin {

    @Shadow
    private TextureAtlas decorationSprites;

    @Inject(
            method = "extractDecorationRenderState",
            at = @At("RETURN"),
            cancellable = true
    )
    private void modifyDecoration(MapDecoration decoration, CallbackInfoReturnable<MapRenderState.MapDecorationRenderState> cir) {
        var state = cir.getReturnValue();

        var type = decoration.type();

        if (type == MapDecorationTypes.PLAYER_OFF_MAP ||
                type == MapDecorationTypes.PLAYER_OFF_LIMITS) {

            state.atlasSprite = decorationSprites.getSprite(
                    MapDecorationTypes.PLAYER.value().assetId()
            );
            state.name = Component.literal("Out of Map!");
            Player player = Minecraft.getInstance().player;

            if (player != null) {
                float yaw = player.getYRot();
                yaw = (yaw % 360 + 360) % 360;

                state.rot = (byte)(yaw / 360.0F * 16.0F);
            }
        }

        cir.setReturnValue(state);
    }
}
