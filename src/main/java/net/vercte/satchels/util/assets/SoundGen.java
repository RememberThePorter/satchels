package net.vercte.satchels.util.assets;


import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.common.data.SoundDefinition;
import net.neoforged.neoforge.common.data.SoundDefinitionsProvider;
import net.vercte.satchels.ModSounds;
import net.vercte.satchels.Satchels;

public class SoundGen extends SoundDefinitionsProvider {
    public SoundGen(PackOutput output, ExistingFileHelper helper) {
        super(output, Satchels.ID, helper);
    }

    @Override
    public void registerSounds() {
        add(ModSounds.SATCHEL_EQUIP, SoundDefinition.definition()
                .with(
                        sound("minecraft:item.armor.equip_leather", SoundDefinition.SoundType.EVENT)
                                .volume(0.8f)
                )
                .subtitle("sound.satchels.satchel_rustle")
        );

        add(ModSounds.SATCHEL_OPEN, SoundDefinition.definition()
                .with(sound("satchels:satchel_open"))
                .subtitle("sound.satchels.satchel_rustle")
        );

        add(ModSounds.SATCHEL_CLOSE, SoundDefinition.definition()
                .with(
                        sound("minecraft:item.bundle.drop_contents", SoundDefinition.SoundType.EVENT)
                                .pitch(1.2f)
                )
                .subtitle("sound.satchels.satchel_rustle")
        );
    }
}
