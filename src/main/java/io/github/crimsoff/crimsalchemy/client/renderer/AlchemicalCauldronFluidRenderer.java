package io.github.crimsoff.crimsalchemy.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import io.github.crimsoff.crimsalchemy.blockentities.AlchemicalCauldronBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

public class AlchemicalCauldronFluidRenderer implements BlockEntityRenderer<AlchemicalCauldronBlockEntity> {
    private final BlockEntityRendererProvider.Context context;

    public AlchemicalCauldronFluidRenderer(BlockEntityRendererProvider.Context ctx) {
        this.context = ctx;
    }

    @Override
    public void render(AlchemicalCauldronBlockEntity pBlockEntity, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, int pPackedOverlay) {
        if (pBlockEntity.isEmpty()) {
            return;
        }
        if (!pBlockEntity.hasLevel()) {
            return;
        }
        Level level = pBlockEntity.getLevel();
        BlockPos pos = pBlockEntity.getBlockPos();
        FluidStack fluidStack = pBlockEntity.getFluidStack();

        IClientFluidTypeExtensions fluidTypeExtensions = IClientFluidTypeExtensions.of(fluidStack.getFluid());
        ResourceLocation stillTexture = fluidTypeExtensions.getStillTexture(fluidStack);
        if (stillTexture == null)
            return;

        FluidState state = fluidStack.getFluid().defaultFluidState();

        TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(stillTexture);
        int tintColor = pBlockEntity.color;

        float height = (((float) pBlockEntity.getFluidAmount() / pBlockEntity.getFluidCapacity()) * 0.8125f) + 0.125f;

        VertexConsumer builder = pBuffer.getBuffer(ItemBlockRenderTypes.getRenderLayer(state));

        drawQuad(builder, pPoseStack, 0.125f, height, 0.125f, 0.875f, height, 0.875f, sprite.getU0(), sprite.getV0(), sprite.getU1(), sprite.getV1(), pPackedLight, tintColor);

    }

    private static void drawVertex(VertexConsumer builder, PoseStack poseStack, float x, float y, float z, float u, float v, int packedLight, int color) {
        builder.vertex(poseStack.last().pose(), x, y, z)
                .color(color)
                .uv(u, v)
                .uv2(packedLight)
                .normal(1, 0, 0)
                .endVertex();
    }

    private static void drawQuad(VertexConsumer builder, PoseStack poseStack, float x0, float y0, float z0, float x1, float y1, float z1, float u0, float v0, float u1, float v1, int packedLight, int color) {
        drawVertex(builder, poseStack, x0, y0, z0, u0, v0, packedLight, color);
        drawVertex(builder, poseStack, x0, y1, z1, u0, v1, packedLight, color);
        drawVertex(builder, poseStack, x1, y1, z1, u1, v1, packedLight, color);
        drawVertex(builder, poseStack, x1, y0, z0, u1, v0, packedLight, color);
    }
}
