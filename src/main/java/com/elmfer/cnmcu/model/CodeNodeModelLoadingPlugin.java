package com.elmfer.cnmcu.model;

import com.elmfer.cnmcu.CodeNodeMicrocontrollers;

import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.util.Identifier;

public class CodeNodeModelLoadingPlugin implements ModelLoadingPlugin {

    public static final Identifier CODE_NODE_NANO_MODEL_ID = CodeNodeMicrocontrollers.id("nano");
    public static final CNnanoModel CODE_NODE_NANO_MODEL = new CNnanoModel();

    @Override
    public void onInitializeModelLoader(Context pluginContext) {

        pluginContext.modifyModelOnLoad().register((original, context) -> {
            String namespace = context.id().getNamespace();

            if (!namespace.equals(CodeNodeMicrocontrollers.MOD_ID))
                return original;

            String path = context.id().getPath();
            String variant = context.id() instanceof ModelIdentifier ? ((ModelIdentifier) context.id()).getVariant()
                    : "";

            if (variant.equals("inventory"))
                return original;

            Identifier id = new Identifier(namespace, path);
            if (id.equals(CODE_NODE_NANO_MODEL_ID))
                return CODE_NODE_NANO_MODEL;

            return original;
        });
    }
}
