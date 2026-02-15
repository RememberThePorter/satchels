package net.vercte.satchels.client;

import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.vercte.satchels.Satchels;
import net.vercte.satchels.client.model.DetachedModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

// This system is inspired+referenced on Flywheel's PartialModels. Thank you Flywheel :)
public class ModModels {
    private static final List<DetachedModel> MODELS = new ArrayList<>();

    public static final DetachedModel SATCHEL_LAYER = create("layer/satchel");

    public static void onRegisterAdditional(ModelEvent.RegisterAdditional event) {
        MODELS.forEach(m -> event.register(m.getModelLocation()));
    }

    public static void onBakingCompleted(ModelEvent.BakingCompleted event) {
        Map<ModelResourceLocation, BakedModel> bakedModels = event.getModels();

        MODELS.forEach(m -> {
            BakedModel baked = bakedModels.get(m.getModelLocation());
            m.load(baked);
        });
    }

    private static DetachedModel create(String path) {
        DetachedModel model = new DetachedModel(Satchels.at(path));
        MODELS.add(model);
        return model;
    }
}
