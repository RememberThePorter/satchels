package net.vercte.satchels.client.model;

import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.vercte.satchels.Satchels;
import org.jetbrains.annotations.UnknownNullability;

public class DetachedModel {
    private final ResourceLocation location;
    private BakedModel model;

    public DetachedModel(ResourceLocation location) {
        this.location = location;
    }

    @UnknownNullability
    public BakedModel get() {
        return model;
    }

    public void load(BakedModel model) {
        this.model = model;
    }

    public ResourceLocation getLocation() {
        return location;
    }

    public ModelResourceLocation getModelLocation() {
        return ModelResourceLocation.standalone(getLocation());
    }

    @Override
    public String toString() {
        return "BakedModel{" + location.toString() + "}";
    }
}
