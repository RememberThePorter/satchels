package net.vercte.satchels.compat;

import net.neoforged.fml.loading.LoadingModList;
import net.vercte.satchels.compat.curios.CuriosCompat;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public enum SatchelsCompat {
    CURIOS("curios", CuriosCompat::new);

    final String id;
    final boolean isLoaded;

    @Nullable
    final CompatEntrypoint entrypoint;

    SatchelsCompat(String id, Supplier<CompatEntrypoint> entrypoint) {
        this.id = id;

        this.entrypoint = entrypoint.get();
        this.isLoaded = LoadingModList.get().getModFileById(id) != null;
    }

    public static void initialize() {
        for(SatchelsCompat compat : values()) {
            if(compat.entrypoint != null) compat.entrypoint.initialize();
        }
    }
}
