package mods.ltr.client.models;

import net.minecraft.client.model.*;

public interface TaterModel {
	static TexturedModelData getModel() {
		ModelData data = new ModelData();
		data.getRoot().addChild("tater", ModelPartBuilder.create()
						.uv(0, 0)
						.cuboid(0, 0, 0, 4, 7, 4, Dilation.NONE, 0.5F, 0.5F),
				ModelTransform.pivot(-2, 18, -2));
		return TexturedModelData.of(data, 32, 32);
	}
}
