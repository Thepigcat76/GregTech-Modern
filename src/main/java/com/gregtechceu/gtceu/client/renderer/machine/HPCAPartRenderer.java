package com.gregtechceu.gtceu.client.renderer.machine;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.capability.IWorkable;
import com.gregtechceu.gtceu.api.machine.MachineDefinition;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.pattern.util.RelativeDirection;
import com.gregtechceu.gtceu.common.machine.multiblock.electric.research.HPCAMachine;
import com.gregtechceu.gtceu.common.machine.multiblock.part.hpca.HPCAComponentPartMachine;
import com.lowdragmc.lowdraglib.client.bakedpipeline.FaceQuad;
import com.lowdragmc.lowdraglib.client.model.ModelFactory;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.function.Consumer;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class HPCAPartRenderer extends TieredHullMachineRenderer {

    private final boolean isAdvanced;
    private final ResourceLocation texture, activeTexture, activeEmissiveTexture, damagedTexture, damagedActiveTexture, damagedActiveEmissiveTexture;

    public HPCAPartRenderer(boolean isAdvanced, ResourceLocation texture, ResourceLocation damagedTexture) {
        super(GTValues.ZPM, isAdvanced ? GTCEu.id("block/computer_casing") : GTCEu.id("block/advanced_computer_casing"));
        this.isAdvanced = isAdvanced;
        this.texture = texture;
        this.activeTexture = new ResourceLocation(texture.getNamespace(), texture.getPath() + "_active");
        this.activeEmissiveTexture = new ResourceLocation(activeTexture.getNamespace(), activeTexture.getPath() + "_emissive");
        this.damagedTexture = damagedTexture;
        this.damagedActiveTexture = new ResourceLocation(damagedTexture.getNamespace(), damagedTexture.getPath() + "_active");
        this.damagedActiveEmissiveTexture = new ResourceLocation(damagedActiveTexture.getNamespace(), damagedActiveTexture.getPath() + "_emissive");
    }

    public HPCAPartRenderer(boolean isAdvanced,
                            ResourceLocation texture,
                            @Nullable  ResourceLocation activeTexture,
                            @Nullable ResourceLocation activeEmissiveTexture,
                            @Nullable ResourceLocation damagedTexture,
                            @Nullable ResourceLocation damagedActiveTexture,
                            @Nullable ResourceLocation damagedActiveEmissiveTexture) {
        super(GTValues.ZPM, isAdvanced ? GTCEu.id("block/computer_casing") : GTCEu.id("block/advanced_computer_casing"));
        this.isAdvanced = isAdvanced;
        this.texture = texture;
        this.activeTexture = activeTexture;
        this.activeEmissiveTexture = activeEmissiveTexture;
        this.damagedTexture = damagedTexture;
        this.damagedActiveTexture = damagedActiveTexture;
        this.damagedActiveEmissiveTexture = damagedActiveEmissiveTexture;
    }

    @Override
    public void renderMachine(List<BakedQuad> quads, MachineDefinition definition, @Nullable MetaMachine machine, Direction frontFacing, @Nullable Direction side, RandomSource rand, @Nullable Direction modelFacing, ModelState modelState) {
        super.renderMachine(quads, definition, machine, frontFacing, side, rand, modelFacing, modelState);
        if (machine instanceof HPCAComponentPartMachine hpcaComponent) {
            ResourceLocation texture, emissiveTexture = null;
            var controller = hpcaComponent.getControllers().isEmpty() ? null : hpcaComponent.getControllers().get(0);
            if (controller != null && (controller instanceof IWorkable workable && workable.isActive())) {
                if (hpcaComponent.isDamaged()) {
                    texture = damagedActiveTexture;
                    emissiveTexture = damagedActiveEmissiveTexture;
                } else {
                    texture = activeTexture;
                    emissiveTexture = activeEmissiveTexture;
                }
            } else {
                if (hpcaComponent.isDamaged()) {
                    texture = this.damagedTexture;
                } else {
                    texture = this.texture;
                }
            }
            if (texture == null) {
                texture = this.texture;
            }
            if (texture != null) {
                Direction facing = frontFacing;
                // Always render this outwards in the HPCA, in case it is not placed outwards in structure.
                // Check for HPCA specifically since these components could potentially be used in other multiblocks.
                if (controller instanceof HPCAMachine hpca) {
                    facing = RelativeDirection.RIGHT.getRelativeFacing(hpca.getFrontFacing(), Direction.NORTH, false);
                }
                facing = ModelFactory.modelFacing(frontFacing, facing);
                quads.add(FaceQuad.bakeFace(FaceQuad.BLOCK, facing, ModelFactory.getBlockSprite(texture), modelState, -1, 0, true, true));
                if (emissiveTexture != null) {
                    quads.add(FaceQuad.bakeFace(FaceQuad.BLOCK, facing, ModelFactory.getBlockSprite(emissiveTexture), modelState, -101, 15, true, false));
                }
            }
        } else {
            ResourceLocation texture = this.texture;
            if (texture != null) {
                quads.add(FaceQuad.bakeFace(FaceQuad.BLOCK, Direction.NORTH, ModelFactory.getBlockSprite(texture), modelState, -1, 0, true, true));
            }
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void onPrepareTextureAtlas(ResourceLocation atlasName, Consumer<ResourceLocation> register) {
        super.onPrepareTextureAtlas(atlasName, register);
        if (atlasName.equals(InventoryMenu.BLOCK_ATLAS)) {
            if (texture != null) {
                register.accept(texture);
            }
            if (activeTexture != null) {
                register.accept(activeTexture);
            }
            if (activeEmissiveTexture != null) {
                register.accept(activeEmissiveTexture);
            }
            if (damagedTexture != null) {
                register.accept(damagedTexture);
            }
            if (damagedActiveTexture != null) {
                register.accept(damagedActiveTexture);
            }
            if (damagedActiveEmissiveTexture != null) {
                register.accept(damagedActiveEmissiveTexture);
            }
        }
    }
}