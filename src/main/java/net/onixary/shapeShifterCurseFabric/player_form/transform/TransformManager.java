package net.onixary.shapeShifterCurseFabric.player_form.transform;

import dev.tr7zw.firstperson.FirstPersonModelCore;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.client.ShapeShifterCurseFabricClient;
import net.onixary.shapeShifterCurseFabric.cursed_moon.CursedMoon;
import net.onixary.shapeShifterCurseFabric.data.StaticParams;
import net.onixary.shapeShifterCurseFabric.networking.ModPackets;
import net.onixary.shapeShifterCurseFabric.networking.ModPacketsS2CServer;
import net.onixary.shapeShifterCurseFabric.player_form.FormRandomSelector;
import net.onixary.shapeShifterCurseFabric.player_form.PlayerFormBase;
import net.onixary.shapeShifterCurseFabric.player_form.PlayerFormGroup;
import net.onixary.shapeShifterCurseFabric.player_form.RegPlayerForms;
import net.onixary.shapeShifterCurseFabric.player_form.ability.FormAbilityManager;
import net.onixary.shapeShifterCurseFabric.player_form.ability.RegPlayerFormComponent;
import net.onixary.shapeShifterCurseFabric.player_form.instinct.InstinctTicker;
import net.onixary.shapeShifterCurseFabric.screen_effect.TransformOverlay;
import net.onixary.shapeShifterCurseFabric.status_effects.attachment.EffectManager;
import net.onixary.shapeShifterCurseFabric.status_effects.transformative_effects.TransformativeStatusInstance;

import java.util.HashMap;
import java.util.UUID;

import static net.onixary.shapeShifterCurseFabric.player_form.effect.PlayerTransformEffectManager.*;
import static net.onixary.shapeShifterCurseFabric.player_form.instinct.InstinctTicker.clearInstinct;
import static net.onixary.shapeShifterCurseFabric.screen_effect.TransformFX.beginTransformEffect;

public class TransformManager {
    public static class PlayerTransformData {
        public int beginTransformEffectTicks = 0;
        public int endTransformEffectTicks = 0;
        public boolean isEffectActive = false;
        public boolean isEndEffectActive = false;
        public PlayerEntity curPlayer = null;
        public PlayerFormBase curFromForm = null;
        public PlayerFormBase curToForm = null;
        public boolean _isByCursedMoon = false;
        public boolean _isByCursedMoonEnd = false;
        public boolean _isRegressedFromFinal = false;
        public boolean _isByCure = false;
        public float nauesaStrength = 0.0f;
        public float blackStrength = 0.0f;
        public boolean isTransforming = false;
    }
    // 仅客户端数据
    private static final boolean IS_FIRST_PERSON_MOD_LOADED = FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT && FabricLoader.getInstance().isModLoaded("firstperson");
    public static final PlayerTransformData LocalPlayerTransformData = new PlayerTransformData();
    // 仅服务端数据
    public static final HashMap<UUID, PlayerTransformData> PlayerTransformDataMap = new HashMap<>();

    // 仅服务器端 - 获取PlayerTransformData
    public static PlayerTransformData getPlayerTransformData(PlayerEntity player) {
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT && (player instanceof ClientPlayerEntity || player == null)) {
            if (player != null) {
                LocalPlayerTransformData.curPlayer = player;
            }
            return LocalPlayerTransformData;
        }
        UUID uuid = player.getUuid();
        if (!PlayerTransformDataMap.containsKey(uuid)) {
            PlayerTransformDataMap.put(uuid, new PlayerTransformData());
        }
        PlayerTransformData data = PlayerTransformDataMap.get(uuid);
        data.curPlayer = player;
        return data;
    }

    // 双端执行
    private static void setTransformingState(boolean transforming, PlayerEntity player) {
        PlayerTransformData data = getPlayerTransformData(player);
        data.isTransforming = transforming;

        // 如果在服务端，同步状态到客户端
        if (player instanceof ServerPlayerEntity serverPlayer) {
            String fromFormName = data.curFromForm != null ? data.curFromForm.getIDString() : null;
            String toFormName = data.curToForm != null ? data.curToForm.getIDString() : null;

            ModPacketsS2CServer.sendTransformState(serverPlayer, transforming, fromFormName, toFormName);
            ShapeShifterCurseFabric.LOGGER.info("Sent transform state to client: " + transforming +
                    ", from: " + fromFormName + ", to: " + toFormName);
        }
    }

    // 仅服务端
    public static void handleProgressiveTransform(ServerPlayerEntity player, boolean isByCursedMoon){
        PlayerTransformData data = getPlayerTransformData(player);
        data._isByCursedMoon = isByCursedMoon;
        data._isRegressedFromFinal = false;
        data._isByCure = false;
        PlayerFormBase currentForm = player.getComponent(RegPlayerFormComponent.PLAYER_FORM).getCurrentForm();

        // 设置变身的起始形态
        data.curFromForm = currentForm;

        RegPlayerFormComponent.PLAYER_FORM.get(player).setByCursedMoon(isByCursedMoon);
        RegPlayerFormComponent.PLAYER_FORM.sync(player);
        FormAbilityManager.saveForm(player);
        ShapeShifterCurseFabric.LOGGER.info("Progressive transform started, isByCursedMoon: " + isByCursedMoon + ", from: " + data.curFromForm);
        int currentFormIndex = currentForm.getIndex();
        PlayerFormGroup currentFormGroup = currentForm.getGroup();
        PlayerFormBase toForm = null;
        switch (currentFormIndex) {
            case -2:
                // 未激活mod内容，不做任何事
                // Mod content not activated, do nothing
                break;
            case -1:
                // 如果没有buff则随机选择一个形态，如果有buff則buff形态+1
                // If there is no buff, randomly select a form; if there is a buff, buff form +1
                toForm = getRandomOrBuffForm(player);
                // 触发自定义成就
                // Trigger custom achievement
                ShapeShifterCurseFabric.ON_TRANSFORM_0.trigger((ServerPlayerEntity) player);
                break;
            case 0:
                toForm = currentFormGroup.getForm(1);
                break;
            case 1:
                toForm = currentFormGroup.getForm(2);
                break;
            case 2:
                if(isByCursedMoon){
                    toForm = currentFormGroup.getForm(0);
                    data._isRegressedFromFinal = true;
                    // 触发自定义成就
                    // Trigger custom achievement
                    ShapeShifterCurseFabric.ON_TRIGGER_CURSED_MOON_FORM_2.trigger((ServerPlayerEntity) player);
                }
                else{
                    ShapeShifterCurseFabric.LOGGER.info("Triggered transformation when at max phase, this should not happen!");
                }
                break;
            case 3:
                // 永久形态，不受影响
                // Permanent form, not affected
                player.sendMessage(Text.translatable("info.shape-shifter-curse.on_cursed_moon_permanent").formatted(Formatting.YELLOW));
                break;
            case 5:
                // SP形态只有一个阶段，不会受到CursedMoon影响
                // SP form has only one stage, not affected by CursedMoon
                if(isByCursedMoon){
                    //toForm = PlayerForms.ORIGINAL_SHIFTER;
                    player.sendMessage(Text.translatable("info.shape-shifter-curse.on_cursed_moon_special").formatted(Formatting.YELLOW));
                }
            default:
                break;
        }
        if (toForm == null) {
            ShapeShifterCurseFabric.LOGGER.info("No next form found, unless you haven't unlock mod contents, then this should not happen!");
            return;
        }
        data.curPlayer = player;
        data.curToForm = toForm;
        ShapeShifterCurseFabric.LOGGER.info("Cur Player: " + data.curPlayer + " To Form: " + data.curToForm);
        applyStartTransformEffect((ServerPlayerEntity) player, StaticParams.TRANSFORM_FX_DURATION_IN);
        handleTransformEffect(player);
        RegPlayerFormComponent.PLAYER_FORM.sync(player);
        FormAbilityManager.saveForm(player);
    }

    // 仅服务端
    public static void handleMoonEndTransform(PlayerEntity player){
        PlayerTransformData data = getPlayerTransformData(player);
        PlayerFormBase currentForm = player.getComponent(RegPlayerFormComponent.PLAYER_FORM).getCurrentForm();

        // 设置变身的起始形态
        data.curFromForm = currentForm;

        int currentFormIndex = currentForm.getIndex();
        PlayerFormGroup currentFormGroup = currentForm.getGroup();
        PlayerFormBase toForm = null;
        switch (currentFormIndex) {
            case -2:
                // 不应该触发
                // Should not trigger
                ShapeShifterCurseFabric.LOGGER.error("Moon end transformation triggered when mod is not enabled, this should not happen!");
                break;
            case -1:
                // 回到之前的SP form
                // go back to the previous SP form
                //toForm = player.getComponent(RegPlayerFormComponent.PLAYER_FORM).getPreviousForm();
                //ShapeShifterCurseFabric.LOGGER.error("Moon end transformation triggered when has original form, this should not happen!");
                break;
            case 0:
                if(player.getComponent(RegPlayerFormComponent.PLAYER_FORM).isRegressedFromFinal()){
                    toForm = currentFormGroup.getForm(2);
                }
                else{
                    toForm = RegPlayerForms.ORIGINAL_SHIFTER;
                }
                break;
            case 1:
                toForm = currentFormGroup.getForm(0);
                break;
            case 2:
                toForm = currentFormGroup.getForm(1);
            case 3:
            case 5:
                // 永久形态或SP形态不会受到CursedMoon影响
                break;
            default:
                break;
        }
        if (toForm == null) {
            ShapeShifterCurseFabric.LOGGER.info("No next form found, this should not happen!");
            return;
        }
        data.curPlayer = player;
        data.curToForm = toForm;
        ShapeShifterCurseFabric.LOGGER.info("Cur Player: " + data.curPlayer + " To Form: " + data.curToForm);
        data._isByCursedMoonEnd = true;
        data._isByCursedMoon = true;
        RegPlayerFormComponent.PLAYER_FORM.get(player).setByCursedMoon(true);
        RegPlayerFormComponent.PLAYER_FORM.sync(player);  // 立即同步组件
        applyStartTransformEffect((ServerPlayerEntity) player, StaticParams.TRANSFORM_FX_DURATION_IN);
        handleTransformEffect(player);
        RegPlayerFormComponent.PLAYER_FORM.sync(player);
        ShapeShifterCurseFabric.LOGGER.info("Moon end transform，_isByCursedMoonEnd=" + data._isByCursedMoonEnd +
                "，component isByCursedMoon=" + RegPlayerFormComponent.PLAYER_FORM.get(player).isByCursedMoon());
    }

    // 仅服务端
    static PlayerFormBase getRandomOrBuffForm(ServerPlayerEntity player){
        TransformativeStatusInstance instance = EffectManager.getTransformativeEffect(player);
        if(instance != null && instance.getTransformativeEffectType() != null){
            return instance.getTransformativeEffectType().getToForm(player);
        }
        else{
            return FormRandomSelector.getRandomForm_CurseMoon();
        }
    }

    // 仅服务端
    public static void update(ServerPlayerEntity player) {
        PlayerTransformData data = getPlayerTransformData(player);
        if(data.isEffectActive){
            // handle overlay effect - 通过网络包触发客户端效果
            updateClientOverlayEffect(player);

            data.beginTransformEffectTicks--;

            if(data.beginTransformEffectTicks <= 0){
                data.isEffectActive = false;
                data.isEndEffectActive = true;
                if (data.curPlayer != null) {
                    // 只在非诅咒月亮变形时清除本能
                    // Only clear instinct when not transforming by Cursed Moon
                    boolean isCursedMoonRelated  = RegPlayerFormComponent.PLAYER_FORM.get(data.curPlayer).isByCursedMoon()
                            || data._isByCursedMoonEnd
                            || data._isByCursedMoon;

                    // 只有当不是诅咒月亮相关变形时才清除本能
                    // Only clear instinct when not related to Cursed Moon
                    if(!isCursedMoonRelated ){
                        clearInstinct(data.curPlayer);
                    }

                    EffectManager.clearTransformativeEffect(player);
                    FormAbilityManager.applyForm(data.curPlayer, data.curToForm);

                    // 不要覆盖组件中的诅咒月亮状态
                    // 只设置其他标志
                    // do not override the Cursed Moon state in the component
                    // only set other flags
                    RegPlayerFormComponent.PLAYER_FORM.get(data.curPlayer).setRegressedFromFinal(data._isRegressedFromFinal);
                    RegPlayerFormComponent.PLAYER_FORM.get(data.curPlayer).setByCure(data._isByCure);
                    RegPlayerFormComponent.PLAYER_FORM.sync(data.curPlayer);
                } else {
                    ShapeShifterCurseFabric.LOGGER.error("curPlayer is null when trying to apply form!");
                }
                applyEndTransformEffect((ServerPlayerEntity) data.curPlayer, StaticParams.TRANSFORM_FX_DURATION_OUT);
                //endTransformEffect();
            }
        }
        else if(data.isEndEffectActive){
            // handle overlay fade effect - 通过网络包触发客户端效果
            updateClientOverlayFadeEffect(player);

            data.endTransformEffectTicks--;
            if(data.endTransformEffectTicks <= 0){
                // 结束时的相关逻辑放在这里
                // 如果curFromForm为ORIGINAL_BEFORE_ENABLE，则代表玩家第一次开启mod，触发info
                // If curFromForm is ORIGINAL_BEFORE_ENABLE, it means the player is enabling the mod for the first time, trigger info
                if(data.curFromForm.equals(RegPlayerForms.ORIGINAL_BEFORE_ENABLE)){
                    // info
                    data.curPlayer.sendMessage(Text.translatable("info.shape-shifter-curse.on_enable_mod_after").formatted(Formatting.LIGHT_PURPLE));
                }

                // 发送客户端特定的完成逻辑
                sendClientTransformCompleteEffect(player);

                //PlayerTeamHandler.updatePlayerTeam((ServerPlayerEntity) curPlayer);
                applyFinaleTransformEffect((ServerPlayerEntity) data.curPlayer, 5);
                InstinctTicker.isPausing = false;

                if (data._isByCursedMoonEnd) {
                    ShapeShifterCurseFabric.LOGGER.info("Finalizing moon end transform");
                    data._isByCursedMoon = false;
                    RegPlayerFormComponent.PLAYER_FORM.get(data.curPlayer).setByCursedMoon(false);
                    RegPlayerFormComponent.PLAYER_FORM.sync(data.curPlayer);
                    data._isByCursedMoonEnd = false;
                }
                data.isTransforming = false;
                data.isEndEffectActive = false;
                data.beginTransformEffectTicks = 0;
                data.endTransformEffectTicks = 0;

                // 添加变身结束状态同步
                if (data.curPlayer != null) {
                    setTransformingState(false, data.curPlayer);
                }
            }
        }
    }

    // 新增：客户端特定的overlay更新逻辑
    // 仅服务端
    private static void updateClientOverlayEffect(PlayerEntity player) {
        PlayerTransformData data = getPlayerTransformData(player);
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT && player instanceof ClientPlayerEntity) {
            handleClientOverlayUpdate(1.0f - (data.beginTransformEffectTicks / (float)StaticParams.TRANSFORM_FX_DURATION_IN), data.beginTransformEffectTicks);
            return;
        }
        // 在服务端，通过网络包发送overlay状态到客户端
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeFloat(1.0f - (data.beginTransformEffectTicks / (float)StaticParams.TRANSFORM_FX_DURATION_IN));
        buf.writeInt(data.beginTransformEffectTicks);
        ServerPlayNetworking.send((ServerPlayerEntity) data.curPlayer, ModPackets.UPDATE_OVERLAY_EFFECT, buf);
    }

    // 新增：客户端特定的overlay淡出更新逻辑
    // 仅服务端
    private static void updateClientOverlayFadeEffect(PlayerEntity player) {
        PlayerTransformData data = getPlayerTransformData(player);
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT && player instanceof ClientPlayerEntity) {
            handleClientOverlayFadeUpdate(data.endTransformEffectTicks / (float)StaticParams.TRANSFORM_FX_DURATION_OUT, data.endTransformEffectTicks);
            return;
        }
        // 在服务端，通过网络包发送overlay淡出状态到客户端
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeFloat(data.endTransformEffectTicks / (float)StaticParams.TRANSFORM_FX_DURATION_OUT);
        buf.writeInt(data.endTransformEffectTicks);
        ServerPlayNetworking.send((ServerPlayerEntity) data.curPlayer, ModPackets.UPDATE_OVERLAY_FADE_EFFECT, buf);
    }

    // 新增：发送客户端变身完成效果
    // 仅服务端
    private static void sendClientTransformCompleteEffect(PlayerEntity player) {
        PlayerTransformData data = getPlayerTransformData(player);
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT && player instanceof ClientPlayerEntity) {
            executeClientTransformCompleteEffect();
            return;
        }
        // 服务端通过网络包通知客户端
        PacketByteBuf buf = PacketByteBufs.create();
        ServerPlayNetworking.send((ServerPlayerEntity) data.curPlayer, ModPackets.TRANSFORM_COMPLETE_EFFECT, buf);
    }

    // 新增：客户端执行变身完成效果
    // 仅客户端
    public static void executeClientTransformCompleteEffect() {
        // 只在客户端执行
        // transform时重置firstperson offset
        // Reset firstperson offset when transforming
        if(IS_FIRST_PERSON_MOD_LOADED && ShapeShifterCurseFabric.clientConfig.enableChangeFPMConfig) {
            FirstPersonModelCore fpm = FirstPersonModelCore.instance;
            fpm.getConfig().xOffset = 0;
            fpm.getConfig().sitXOffset = 0;
            fpm.getConfig().sneakXOffset = 0;
        }
        // Overlay 关闭逻辑
        TransformOverlay.INSTANCE.setEnableOverlay(false);
    }

    // 新增：客户端overlay更新处理
    // 仅客户端
    public static void handleClientOverlayUpdate(float nauseaStrength, int ticks) {
        PlayerTransformData data = getPlayerTransformData(null);
        data.nauesaStrength = nauseaStrength;
        if(data.nauesaStrength > 0.8f){
            data.blackStrength = (data.nauesaStrength - 0.8f) / 0.2f;
        }
        else{
            data.blackStrength = 0.0f;
        }
        TransformOverlay.INSTANCE.setNauesaStrength(data.nauesaStrength);
        TransformOverlay.INSTANCE.setBlackStrength(data.blackStrength);
    }

    // 新增：客户端overlay淡出更新处理
    // 仅客户端
    public static void handleClientOverlayFadeUpdate(float nauseaStrength, int ticks) {
        PlayerTransformData data = getPlayerTransformData(null);
        data.nauesaStrength = nauseaStrength;
        if(data.nauesaStrength > 0.6f){
            data.blackStrength = 1.0f;
        }
        else{
            data.blackStrength = data.nauesaStrength / 0.6f;
        }
        TransformOverlay.INSTANCE.setNauesaStrength(data.nauesaStrength);
        TransformOverlay.INSTANCE.setBlackStrength(data.blackStrength);
    }

    // 双端 但我觉得是仅服务端
    public static void handleDirectTransform(PlayerEntity player, PlayerFormBase toForm, boolean isByCure){
        PlayerTransformData data = getPlayerTransformData(player);
        data.curPlayer = player;
        data.curToForm = toForm;
        data.curFromForm = player.getComponent(RegPlayerFormComponent.PLAYER_FORM).getCurrentForm();
        data._isByCure = isByCure;
        // 检查cure应用时是否处于Cursed Moon，如果没有，则不设置flag
        // Check if the cure is applied during Cursed Moon, if not, do not set the flag
        if(!CursedMoon.isCursedMoon(player.getWorld())){
            data._isByCure = false;
        }
        // 根据index触发自定义成就
        // Trigger custom achievement based on index
        int toFormIndex = data.curToForm.getIndex();
        if(!isByCure){
            switch(toFormIndex){
                case 0:
                    ShapeShifterCurseFabric.ON_TRANSFORM_0.trigger((ServerPlayerEntity) player);
                    break;
                case 1:
                    ShapeShifterCurseFabric.ON_TRANSFORM_1.trigger((ServerPlayerEntity) player);
                    break;
                case 2:
                    ShapeShifterCurseFabric.ON_TRANSFORM_2.trigger((ServerPlayerEntity) player);
                    break;
                default:
                    break;
            }
        }

        ShapeShifterCurseFabric.LOGGER.info("Cur Player: " + data.curPlayer + " To Form: " + data.curToForm);
        handleTransformEffect(player);
        applyStartTransformEffect((ServerPlayerEntity) player, StaticParams.TRANSFORM_FX_DURATION_IN);
        // FormAbilityManager.applyForm(player, toForm);
    }

    // 双端 但我觉得是仅服务端 继承于handleDirectTransform
    private static void handleTransformEffect(PlayerEntity player) {
        PlayerTransformData data = getPlayerTransformData(player);
        data.isTransforming = true;
        data.beginTransformEffectTicks = StaticParams.TRANSFORM_FX_DURATION_IN;
        data.endTransformEffectTicks = StaticParams.TRANSFORM_FX_DURATION_OUT;
        data.isEffectActive = true;
        InstinctTicker.isPausing = true;

        // 添加变身状态同步
        setTransformingState(true, player);

        //if(client) {
        //    beginTransformEffect();
        //    TransformOverlay.INSTANCE.setEnableOverlay(true);
        //}
        if (!player.getWorld().isClient && player instanceof ServerPlayerEntity) {
            // 创建一个空的数据包，因为我们只需要一个触发信号
            PacketByteBuf buf = PacketByteBufs.create();
            ServerPlayNetworking.send((ServerPlayerEntity) player, ModPackets.TRANSFORM_EFFECT_ID, buf);
        }
    }

    // 仅客户端
    public static void playClientTransformEffect() {
        // 再次确认这是在客户端环境
        if (FabricLoader.getInstance().getEnvironmentType() != EnvType.CLIENT) {
            return;
        }

        PlayerTransformData data = getPlayerTransformData(null);

        // 将原先handleTransformEffect中客户端独有的逻辑移到这里
        // 并设置那些在客户端update()方法中需要的状态变量
        data.isTransforming = true;
        data.beginTransformEffectTicks = StaticParams.TRANSFORM_FX_DURATION_IN;
        data.endTransformEffectTicks = StaticParams.TRANSFORM_FX_DURATION_OUT;
        data.isEffectActive = true;
        InstinctTicker.isPausing = true;
        ShapeShifterCurseFabricClient.emitTransformParticle(StaticParams.TRANSFORM_FX_DURATION_IN);
        beginTransformEffect();
        TransformOverlay.INSTANCE.setEnableOverlay(true);
    }

    // 仅服务端
    public static void setFormDirectly(PlayerEntity player, PlayerFormBase toForm){
        PlayerTransformData data = getPlayerTransformData(player);
        data.curPlayer = player;
        data.curToForm = toForm;
        EffectManager.clearTransformativeEffect(player);
        FormAbilityManager.applyForm(data.curPlayer, data.curToForm);
        clearFormFlag(data.curPlayer);
        clearInstinct(data.curPlayer);
        //PlayerTeamHandler.updatePlayerTeam((ServerPlayerEntity) curPlayer);

        // 发送客户端特定的FirstPerson重置逻辑
        sendClientFirstPersonReset(player);

        RegPlayerFormComponent.PLAYER_FORM.sync(data.curPlayer);
    }

    // 新增：发送客户端FirstPerson重置
    // 仅服务端
    private static void sendClientFirstPersonReset(PlayerEntity player) {
        PlayerTransformData data = getPlayerTransformData(player);
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT && player instanceof ClientPlayerEntity) {
            // 客户端直接执行
            executeClientFirstPersonReset();
        } else if (data.curPlayer instanceof ServerPlayerEntity) {
            // 服务端通过网络包通知客户端
            PacketByteBuf buf = PacketByteBufs.create();
            ServerPlayNetworking.send((ServerPlayerEntity) data.curPlayer, ModPackets.RESET_FIRST_PERSON, buf);
        }
    }

    // 新增：客户端执行FirstPerson重置
    // 仅客户端
    public static void executeClientFirstPersonReset() {
        if (FabricLoader.getInstance().getEnvironmentType() != EnvType.CLIENT) {
            return;
        }

        if(IS_FIRST_PERSON_MOD_LOADED && ShapeShifterCurseFabric.clientConfig.enableChangeFPMConfig) {
            FirstPersonModelCore fpm = FirstPersonModelCore.instance;
            fpm.getConfig().xOffset = 0;
            fpm.getConfig().sitXOffset = 0;
            fpm.getConfig().sneakXOffset = 0;

            // 0.05s 0.1s 0.2s 1s 后重置 防止 ExtraItemFeatureRenderer 未同步玩家变形状态 减少玩家感知未同步
            new Thread(() -> {
                try { Thread.sleep(50); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }  // 0.05s
                fpm.getConfig().xOffset = 0; fpm.getConfig().sitXOffset = 0; fpm.getConfig().sneakXOffset = 0;
                try { Thread.sleep(50); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }  // 0.1s
                fpm.getConfig().xOffset = 0; fpm.getConfig().sitXOffset = 0; fpm.getConfig().sneakXOffset = 0;
                try { Thread.sleep(100); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }  // 0.2s
                fpm.getConfig().xOffset = 0; fpm.getConfig().sitXOffset = 0; fpm.getConfig().sneakXOffset = 0;
                try { Thread.sleep(800); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }  // 1s  最终修复 大部分均在1s内恢复同步
                fpm.getConfig().xOffset = 0; fpm.getConfig().sitXOffset = 0; fpm.getConfig().sneakXOffset = 0;
            }).start();
        }
    }

    // 仅服务端
    public static void clearFormFlag(PlayerEntity player){
        PlayerTransformData data = getPlayerTransformData(player);
        boolean wasByCursedMoon = RegPlayerFormComponent.PLAYER_FORM.get(player).isByCursedMoon();

        ShapeShifterCurseFabric.LOGGER.info("Clearing form flags, wasByCursedMoon: " + wasByCursedMoon +
                ", _isByCursedMoonEnd: " + data._isByCursedMoonEnd);

        // 只在诅咒月亮结束时才重置诅咒月亮标志\
        // Only reset the Cursed Moon flag when the Cursed Moon ends
        if (data._isByCursedMoonEnd) {
            data._isByCursedMoon = false;
            RegPlayerFormComponent.PLAYER_FORM.get(player).setByCursedMoon(false);
            ShapeShifterCurseFabric.LOGGER.info("Cleared cursed moon flag due to moon end");
        }

        // 重置其他标志
        // Reset other flags
        data._isRegressedFromFinal = false;
        data._isByCure = false;
        data._isByCursedMoonEnd = false;

        RegPlayerFormComponent.PLAYER_FORM.get(player).setRegressedFromFinal(false);
        RegPlayerFormComponent.PLAYER_FORM.get(player).setByCure(false);
        RegPlayerFormComponent.PLAYER_FORM.sync(player);
    }

    // 仅服务端
    public static void setIsByCursedMoonEnd(ServerPlayerEntity player, boolean value) {
        PlayerTransformData data = getPlayerTransformData(player);
        data._isByCursedMoonEnd = value;
        ShapeShifterCurseFabric.LOGGER.info("设置_isByCursedMoonEnd=" + value);
    }

    // 没有调用 默认双端
    public static void clearMoonEndFlags(PlayerEntity player) {
        ShapeShifterCurseFabric.LOGGER.info("安全清除月亮结束标记");
        PlayerTransformData data = getPlayerTransformData(player);

        // 记录清除前的状态
        // Record the state before clearing
        boolean wasByCursedMoon = RegPlayerFormComponent.PLAYER_FORM.get(player).isByCursedMoon();

        if (data._isByCursedMoonEnd) {
            ShapeShifterCurseFabric.LOGGER.info("Clearing moon end flags (instinct should be preserved)");
            data._isByCursedMoon = false;
            data._isByCursedMoonEnd = false;
            RegPlayerFormComponent.PLAYER_FORM.get(player).setByCursedMoon(false);
            RegPlayerFormComponent.PLAYER_FORM.sync(player);
        }
        // 只清除状态标记，不影响instinct
        // Only clear the state flags, do not affect instinct
        data._isRegressedFromFinal = false;
        data._isByCure = false;

        RegPlayerFormComponent.PLAYER_FORM.get(player).setRegressedFromFinal(false);
        RegPlayerFormComponent.PLAYER_FORM.get(player).setByCure(false);
        RegPlayerFormComponent.PLAYER_FORM.sync(player);

        ShapeShifterCurseFabric.LOGGER.info("月亮标记已清除，原状态：" + wasByCursedMoon);
    }
}
