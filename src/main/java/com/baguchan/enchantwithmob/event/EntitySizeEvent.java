package com.baguchan.enchantwithmob.event;

import com.baguchan.enchantwithmob.EnchantWithMob;
import com.baguchan.enchantwithmob.capability.MobEnchantCapability;
import com.baguchan.enchantwithmob.registry.MobEnchants;
import com.baguchan.enchantwithmob.utils.MobEnchantUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = EnchantWithMob.MODID)
public class EntitySizeEvent {
	@SubscribeEvent
	public static void onSetSize(EntityEvent.Size event) {
		Entity entity = event.getEntity();


		LazyOptional<MobEnchantCapability> capLazy = entity.getCapability(EnchantWithMob.MOB_ENCHANT_CAP);
		capLazy.ifPresent(cap -> {
			if (cap.hasEnchant()) {
				if (MobEnchantUtils.findMobEnchantFromHandler(cap.getMobEnchants(), MobEnchants.HUGE)) {
					int level = MobEnchantUtils.getMobEnchantLevelFromHandler(cap.getMobEnchants(), MobEnchants.HUGE);

					float totalWidth = entity.getDimensions(entity.getPose()).width * (1.0F + level * 0.15F);
					float totalHeight = entity.getDimensions(entity.getPose()).height * (1.0F + level * 0.15F);

					event.setNewEyeHeight(entity.getEyeHeight(entity.getPose()) * (1.0F + level * 0.15F));
					event.setNewSize(EntityDimensions.fixed(totalWidth, totalHeight));
				} else {
					float totalWidth = entity.getDimensions(entity.getPose()).width * 1.05F;
					float totalHeight = entity.getDimensions(entity.getPose()).height * 1.05F;

					event.setNewEyeHeight(entity.getEyeHeight(entity.getPose()) * 1.05F);
					event.setNewSize(EntityDimensions.fixed(totalWidth, totalHeight));
				}
			}
		});
	}

	@SubscribeEvent
	@OnlyIn(Dist.CLIENT)
	public static void onRenderWorld(RenderWorldLastEvent event) {
		Player player = Minecraft.getInstance().player;
		float scale = player.getBbHeight() / 1.8F;

		switch (Minecraft.getInstance().options.getCameraType()) {
			case THIRD_PERSON_BACK:
				if (player.getBbHeight() > 1.8F) event.getMatrixStack().translate(0, 0, -scale * 2);
				break;
			case THIRD_PERSON_FRONT:
				if (player.getBbHeight() > 1.8F) event.getMatrixStack().translate(0, 0, scale * 2);
				break;
		}
	}

	@SubscribeEvent
	@OnlyIn(Dist.CLIENT)
	public static void onEntityRenderPre(RenderLivingEvent.Pre<LivingEntity, EntityModel<LivingEntity>> event) {
		final LivingEntity entity = event.getEntity();

		LazyOptional<MobEnchantCapability> capLazy = entity.getCapability(EnchantWithMob.MOB_ENCHANT_CAP);
		capLazy.ifPresent(cap -> {
			if (cap.hasEnchant()) {
				event.getMatrixStack().pushPose();
				if (MobEnchantUtils.findMobEnchantFromHandler(cap.getMobEnchants(), MobEnchants.HUGE)) {
					int level = MobEnchantUtils.getMobEnchantLevelFromHandler(cap.getMobEnchants(), MobEnchants.HUGE);
					event.getMatrixStack().scale(1.0F + 0.15F * level, 1.0F + 0.15F * level, 1.0F + 0.15F * level);
				} else {
					event.getMatrixStack().scale(1.05F, 1.05F, 1.05F);
				}


			}
		});

	}

	@SubscribeEvent
	@OnlyIn(Dist.CLIENT)
	public static void onLivingRenderPost(RenderLivingEvent.Post<LivingEntity, EntityModel<LivingEntity>> event) {
		final LivingEntity entity = event.getEntity();

		LazyOptional<MobEnchantCapability> capLazy = entity.getCapability(EnchantWithMob.MOB_ENCHANT_CAP);
		capLazy.ifPresent(cap -> {
			if (cap.hasEnchant()) {
				event.getMatrixStack().popPose();
			}
		});

	}
}
