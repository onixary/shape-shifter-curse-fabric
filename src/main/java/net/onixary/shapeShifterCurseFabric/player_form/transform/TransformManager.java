package net.onixary.shapeShifterCurseFabric.player_form.transform;

import dev.tr7zw.firstperson.FirstPersonModelCore;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
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
import net.onixary.shapeShifterCurseFabric.networking.ModPacketsS2C;
import net.onixary.shapeShifterCurseFabric.networking.ModPacketsS2CServer;
import net.onixary.shapeShifterCurseFabric.player_form.FormRandomSelector;
import net.onixary.shapeShifterCurseFabric.player_form.PlayerForms;
import net.onixary.shapeShifterCurseFabric.player_form.ability.FormAbilityManager;
import net.onixary.shapeShifterCurseFabric.player_form.ability.RegPlayerFormComponent;
import net.onixary.shapeShifterCurseFabric.player_form.instinct.InstinctTicker;
import net.onixary.shapeShifterCurseFabric.screen_effect.TransformOverlay;
import net.onixary.shapeShifterCurseFabric.status_effects.attachment.EffectManager;
import net.onixary.shapeShifterCurseFabric.status_effects.attachment.PlayerEffectAttachment;

import static net.onixary.shapeShifterCurseFabric.player_form.effect.PlayerTransformEffectManager.*;
import static net.onixary.shapeShifterCurseFabric.player_form.instinct.InstinctTicker.clearInstinct;
import static net.onixary.shapeShifterCurseFabric.screen_effect.TransformFX.beginTransformEffect;
import static net.onixary.shapeShifterCurseFabric.screen_effect.TransformFX.endTransformEffect;

public class TransformManager {
    private TransformManager() {
    }

    private static int beginTransformEffectTicks = 0;
    private static int endTransformEffectTicks = 0;
    private static boolean isEffectActive = false;
    private static boolean isEndEffectActive = false;
    private static PlayerEntity curPlayer = null;
    public static PlayerForms curFromForm = null;
    public static PlayerForms curToForm = null;
    private static boolean _isByCursedMoon = false;
    private static boolean _isByCursedMoonEnd = false;
    private static boolean _isRegressedFromFinal = false;
    private static boolean _isByCure = false;

    private static float nauesaStrength = 0.0f;
    private static float blackStrength = 0.0f;
    private static boolean isTransforming = false;

    public static boolean isTransforming(){
        return isTransforming;
    }

    // 设置变身状态并同步到客户端
    private static void setTransformingState(boolean transforming, PlayerEntity player) {
        isTransforming = transforming;

        // 如果在服务端，同步状态到客户端
        if (player instanceof ServerPlayerEntity serverPlayer) {
            String fromFormName = curFromForm != null ? curFromForm.name() : null;
            String toFormName = curToForm != null ? curToForm.name() : null;

            ModPacketsS2CServer.sendTransformState(serverPlayer, transforming, fromFormName, toFormName);
            ShapeShifterCurseFabric.LOGGER.info("Sent transform state to client: " + transforming +
                                              ", from: " + fromFormName + ", to: " + toFormName);
        }
    }

    private static final boolean IS_FIRST_PERSON_MOD_LOADED = FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT && FabricLoader.getInstance().isModLoaded("firstperson");

    public static void handleProgressiveTransform(PlayerEntity player, boolean isByCursedMoon){
        _isByCursedMoon = isByCursedMoon;
        _isRegressedFromFinal = false;
        _isByCure = false;
        PlayerForms currentForm = player.getComponent(RegPlayerFormComponent.PLAYER_FORM).getCurrentForm();

        // 设置变身的起始形态
        curFromForm = currentForm;

        RegPlayerFormComponent.PLAYER_FORM.get(player).setByCursedMoon(isByCursedMoon);
        RegPlayerFormComponent.PLAYER_FORM.sync(player);
        FormAbilityManager.saveForm(player);
        ShapeShifterCurseFabric.LOGGER.info("Progressive transform started, isByCursedMoon: " + isByCursedMoon + ", from: " + curFromForm);
        int currentFormIndex = currentForm.getIndex();
        String currentFormGroup = currentForm.getGroup();
        PlayerForms toForm = null;
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
                toForm = PlayerForms.getFormsByGroup(currentFormGroup)[1];
                break;
            case 1:
                toForm = PlayerForms.getFormsByGroup(currentFormGroup)[2];
                break;
            case 2:
                if(isByCursedMoon){
                    toForm = PlayerForms.getFormsByGroup(currentFormGroup)[0];
                    _isRegressedFromFinal = true;
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
        curPlayer = player;
        curToForm = toForm;
        ShapeShifterCurseFabric.LOGGER.info("Cur Player: " + curPlayer + " To Form: " + curToForm);
        applyStartTransformEffect((ServerPlayerEntity) player, StaticParams.TRANSFORM_FX_DURATION_IN);
        handleTransformEffect(player);
        RegPlayerFormComponent.PLAYER_FORM.sync(player);
        FormAbilityManager.saveForm(player);
        MinecraftServer server = player.getServer();
        syncCursedMoonData(player, server);
    }

    public static void handleMoonEndTransform(PlayerEntity player){
        PlayerForms currentForm = player.getComponent(RegPlayerFormComponent.PLAYER_FORM).getCurrentForm();

        // 设置变身的起始形态
        curFromForm = currentForm;

        int currentFormIndex = currentForm.getIndex();
        String currentFormGroup = currentForm.getGroup();
        PlayerForms toForm = null;
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
                    toForm = PlayerForms.getFormsByGroup(currentFormGroup)[2];
                }
                else{
                    toForm = PlayerForms.ORIGINAL_SHIFTER;
                }
                break;
            case 1:
                toForm = PlayerForms.getFormsByGroup(currentFormGroup)[0];
                break;
            case 2:
                toForm = PlayerForms.getFormsByGroup(currentFormGroup)[1];
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
        curPlayer = player;
        curToForm = toForm;
        ShapeShifterCurseFabric.LOGGER.info("Cur Player: " + curPlayer + " To Form: " + curToForm);
        _isByCursedMoonEnd = true;
        _isByCursedMoon = true;
        RegPlayerFormComponent.PLAYER_FORM.get(player).setByCursedMoon(true);
        RegPlayerFormComponent.PLAYER_FORM.sync(player);  // 立即同步组件
        applyStartTransformEffect((ServerPlayerEntity) player, StaticParams.TRANSFORM_FX_DURATION_IN);
        handleTransformEffect(player);
        RegPlayerFormComponent.PLAYER_FORM.sync(player);
        ShapeShifterCurseFabric.LOGGER.info("Moon end transform，_isByCursedMoonEnd=" + _isByCursedMoonEnd +
                "，component isByCursedMoon=" + RegPlayerFormComponent.PLAYER_FORM.get(player).isByCursedMoon());
        MinecraftServer server = player.getServer();
        syncCursedMoonData(player, server);
    }

    static PlayerForms getRandomOrBuffForm(PlayerEntity player){
        PlayerEffectAttachment currentTransformEffect = EffectManager.getCurrentEffectAttachment(player);
        if(currentTransformEffect != null && currentTransformEffect.currentEffect != null){
            return currentTransformEffect.currentToForm;
        }
        else{
            return FormRandomSelector.getRandomFormFromPredefined();
        }
    }

    public static void update() {
        if(isEffectActive){
            // handle overlay effect - 通过网络包触发客户端效果
            updateClientOverlayEffect();

            beginTransformEffectTicks--;

            if(beginTransformEffectTicks <= 0){
                isEffectActive = false;
                isEndEffectActive = true;
                if (curPlayer != null) {
                    // 只在非诅咒月亮变形时清除本能
                    // Only clear instinct when not transforming by Cursed Moon
                    boolean isCursedMoonRelated  = RegPlayerFormComponent.PLAYER_FORM.get(curPlayer).isByCursedMoon()
                            || _isByCursedMoonEnd
                            || _isByCursedMoon;

                    // 只有当不是诅咒月亮相关变形时才清除本能
                    // Only clear instinct when not related to Cursed Moon
                    if(!isCursedMoonRelated ){
                        clearInstinct(curPlayer);
                    }

                    FormAbilityManager.applyForm(curPlayer, curToForm);

                    // 不要覆盖组件中的诅咒月亮状态
                    // 只设置其他标志
                    // do not override the Cursed Moon state in the component
                    // only set other flags
                    RegPlayerFormComponent.PLAYER_FORM.get(curPlayer).setRegressedFromFinal(_isRegressedFromFinal);
                    RegPlayerFormComponent.PLAYER_FORM.get(curPlayer).setByCure(_isByCure);
                    RegPlayerFormComponent.PLAYER_FORM.sync(curPlayer);
                } else {
                    ShapeShifterCurseFabric.LOGGER.error("curPlayer is null when trying to apply form!");
                }
                applyEndTransformEffect((ServerPlayerEntity) curPlayer, StaticParams.TRANSFORM_FX_DURATION_OUT);
                //endTransformEffect();
            }
        }
        else if(isEndEffectActive){
            // handle overlay fade effect - 通过网络包触发客户端效果
            updateClientOverlayFadeEffect();

            endTransformEffectTicks--;
            if(endTransformEffectTicks <= 0){
                // 结束时的相关逻辑放在这里
                // 如果curFromForm为ORIGINAL_BEFORE_ENABLE，则代表玩家第一次开启mod，触发info
                // If curFromForm is ORIGINAL_BEFORE_ENABLE, it means the player is enabling the mod for the first time, trigger info
                if(curFromForm == PlayerForms.ORIGINAL_BEFORE_ENABLE){
                    // info
                    curPlayer.sendMessage(Text.translatable("info.shape-shifter-curse.on_enable_mod_after").formatted(Formatting.LIGHT_PURPLE));
                }

                // 发送客户端特定的完成逻辑
                sendClientTransformCompleteEffect();

                //PlayerTeamHandler.updatePlayerTeam((ServerPlayerEntity) curPlayer);
                applyFinaleTransformEffect((ServerPlayerEntity) curPlayer, 5);
                InstinctTicker.isPausing = false;

                if (_isByCursedMoonEnd) {
                    ShapeShifterCurseFabric.LOGGER.info("Finalizing moon end transform");
                    _isByCursedMoon = false;
                    RegPlayerFormComponent.PLAYER_FORM.get(curPlayer).setByCursedMoon(false);
                    RegPlayerFormComponent.PLAYER_FORM.sync(curPlayer);
                    _isByCursedMoonEnd = false;
                }
                isTransforming = false;
                isEndEffectActive = false;
                beginTransformEffectTicks = 0;
                endTransformEffectTicks = 0;

                // 添加变身结束状态同步
                if (curPlayer != null) {
                    setTransformingState(false, curPlayer);
                }
            }
        }
    }

    // 新增：客户端特定的overlay更新逻辑
    private static void updateClientOverlayEffect() {
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
            nauesaStrength = 1.0f - (beginTransformEffectTicks / (float)StaticParams.TRANSFORM_FX_DURATION_IN);
            if(nauesaStrength > 0.8f){
                blackStrength = (nauesaStrength - 0.8f) / 0.2f;
            }
            else{
                blackStrength = 0.0f;
            }
            TransformOverlay.INSTANCE.setNauesaStrength(nauesaStrength);
            TransformOverlay.INSTANCE.setBlackStrength(blackStrength);
        } else if (curPlayer instanceof ServerPlayerEntity) {
            // 在服务端，通过网络包发送overlay状态到客户端
            PacketByteBuf buf = PacketByteBufs.create();
            buf.writeFloat(1.0f - (beginTransformEffectTicks / (float)StaticParams.TRANSFORM_FX_DURATION_IN));
            buf.writeInt(beginTransformEffectTicks);
            ServerPlayNetworking.send((ServerPlayerEntity) curPlayer, ModPackets.UPDATE_OVERLAY_EFFECT, buf);
        }
    }

    // 新增：客户端特定的overlay淡出更新逻辑
    private static void updateClientOverlayFadeEffect() {
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
            nauesaStrength = endTransformEffectTicks / (float)StaticParams.TRANSFORM_FX_DURATION_OUT;
            if(nauesaStrength > 0.6f){
                blackStrength = 1.0f;
            }
            else{
                blackStrength = nauesaStrength / 0.6f;
            }
            TransformOverlay.INSTANCE.setNauesaStrength(nauesaStrength);
            TransformOverlay.INSTANCE.setBlackStrength(blackStrength);
        } else if (curPlayer instanceof ServerPlayerEntity) {
            // 在服务端，通过网络包发送overlay淡出状态到客户端
            PacketByteBuf buf = PacketByteBufs.create();
            buf.writeFloat(endTransformEffectTicks / (float)StaticParams.TRANSFORM_FX_DURATION_OUT);
            buf.writeInt(endTransformEffectTicks);
            ServerPlayNetworking.send((ServerPlayerEntity) curPlayer, ModPackets.UPDATE_OVERLAY_FADE_EFFECT, buf);
        }
    }

    // 新增：发送客户端变身完成效果
    private static void sendClientTransformCompleteEffect() {
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
            // 客户端直接执行
            executeClientTransformCompleteEffect();
        } else if (curPlayer instanceof ServerPlayerEntity) {
            // 服务端通过网络包通知客户端
            PacketByteBuf buf = PacketByteBufs.create();
            ServerPlayNetworking.send((ServerPlayerEntity) curPlayer, ModPackets.TRANSFORM_COMPLETE_EFFECT, buf);
        }
    }

    // 新增：客户端执行变身完成效果
    public static void executeClientTransformCompleteEffect() {
        // 只在客户端执行
        if (FabricLoader.getInstance().getEnvironmentType() != EnvType.CLIENT) {
            return;
        }

        // transform时重置firstperson offset
        // Reset firstperson offset when transforming
        if(IS_FIRST_PERSON_MOD_LOADED) {
            FirstPersonModelCore fpm = FirstPersonModelCore.instance;
            fpm.getConfig().xOffset = 0;
            fpm.getConfig().sitXOffset = 0;
            fpm.getConfig().sneakXOffset = 0;
        }

        // Overlay 关闭逻辑
        TransformOverlay.INSTANCE.setEnableOverlay(false);
    }

    // 新增：客户端overlay更新处理
    public static void handleClientOverlayUpdate(float nauseaStrength, int ticks) {
        if (FabricLoader.getInstance().getEnvironmentType() != EnvType.CLIENT) {
            return;
        }

        nauesaStrength = nauseaStrength;
        if(nauesaStrength > 0.8f){
            blackStrength = (nauesaStrength - 0.8f) / 0.2f;
        }
        else{
            blackStrength = 0.0f;
        }
        TransformOverlay.INSTANCE.setNauesaStrength(nauesaStrength);
        TransformOverlay.INSTANCE.setBlackStrength(blackStrength);
    }

    // 新增：客户端overlay淡出更新处理
    public static void handleClientOverlayFadeUpdate(float nauseaStrength, int ticks) {
        if (FabricLoader.getInstance().getEnvironmentType() != EnvType.CLIENT) {
            return;
        }

        nauesaStrength = nauseaStrength;
        if(nauesaStrength > 0.6f){
            blackStrength = 1.0f;
        }
        else{
            blackStrength = nauesaStrength / 0.6f;
        }
        TransformOverlay.INSTANCE.setNauesaStrength(nauesaStrength);
        TransformOverlay.INSTANCE.setBlackStrength(blackStrength);
    }

    public static void handleDirectTransform(PlayerEntity player, PlayerForms toForm, boolean isByCure){
        curPlayer = player;
        curToForm = toForm;
        curFromForm = player.getComponent(RegPlayerFormComponent.PLAYER_FORM).getCurrentForm();
        _isByCure = isByCure;
        // 检查cure应用时是否处于Cursed Moon，如果没有，则不设置flag
        // Check if the cure is applied during Cursed Moon, if not, do not set the flag
        if(!CursedMoon.isCursedMoon(player.getWorld())){
            _isByCure = false;
        }
        // 根据index触发自定义成就
        // Trigger custom achievement based on index
        int toFormIndex = curToForm.getIndex();
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

        ShapeShifterCurseFabric.LOGGER.info("Cur Player: " + curPlayer + " To Form: " + curToForm);
        handleTransformEffect(player);
        applyStartTransformEffect((ServerPlayerEntity) player, StaticParams.TRANSFORM_FX_DURATION_IN);
        // FormAbilityManager.applyForm(player, toForm);
        MinecraftServer server = player.getServer();
        syncCursedMoonData(player, server);
    }

    private static void syncCursedMoonData(PlayerEntity player, MinecraftServer server){
        /*if(FormAbilityManager.getForm(player) == PlayerForms.ORIGINAL_BEFORE_ENABLE){
            ShapeShifterCurseFabric.LOGGER.info("Cursed moon disabled");
            ShapeShifterCurseFabric.cursedMoonData.getInstance().disableCursedMoon(server.getOverworld());
        }
        else{
            ShapeShifterCurseFabric.LOGGER.info("Cursed moon enabled");
            ShapeShifterCurseFabric.cursedMoonData.getInstance().enableCursedMoon(server.getOverworld());
        }*/
        ShapeShifterCurseFabric.LOGGER.info("Cursed moon data saved by syncCursedMoonData");
        ShapeShifterCurseFabric.cursedMoonData.getInstance().save(server.getOverworld());
    }

    private static void handleTransformEffect(PlayerEntity player) {
        isTransforming = true;
        beginTransformEffectTicks = StaticParams.TRANSFORM_FX_DURATION_IN;
        endTransformEffectTicks = StaticParams.TRANSFORM_FX_DURATION_OUT;
        isEffectActive = true;
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

    public static void playClientTransformEffect() {
        // 再次确认这是在客户端环境
        if (FabricLoader.getInstance().getEnvironmentType() != EnvType.CLIENT) {
            return;
        }

        // 将原先handleTransformEffect中客户端独有的逻辑移到这里
        // 并设置那些在客户端update()方法中需要的状态变量
        isTransforming = true;
        beginTransformEffectTicks = StaticParams.TRANSFORM_FX_DURATION_IN;
        endTransformEffectTicks = StaticParams.TRANSFORM_FX_DURATION_OUT;
        isEffectActive = true;
        InstinctTicker.isPausing = true;
        ShapeShifterCurseFabricClient.emitTransformParticle(StaticParams.TRANSFORM_FX_DURATION_IN);
        beginTransformEffect();
        TransformOverlay.INSTANCE.setEnableOverlay(true);
    }

    public static void setFormDirectly(PlayerEntity player, PlayerForms toForm){
        curPlayer = player;
        curToForm = toForm;
        FormAbilityManager.applyForm(curPlayer, curToForm);
        clearFormFlag(curPlayer);
        clearInstinct(curPlayer);
        //PlayerTeamHandler.updatePlayerTeam((ServerPlayerEntity) curPlayer);

        // 发送客户端特定的FirstPerson重置逻辑
        sendClientFirstPersonReset();

        RegPlayerFormComponent.PLAYER_FORM.sync(curPlayer);
        MinecraftServer server = player.getServer();
        syncCursedMoonData(player, server);
    }

    // 新增：发送客户端FirstPerson重置
    private static void sendClientFirstPersonReset() {
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
            // 客户端直接执行
            executeClientFirstPersonReset();
        } else if (curPlayer instanceof ServerPlayerEntity) {
            // 服务端通过网络包通知客户端
            PacketByteBuf buf = PacketByteBufs.create();
            ServerPlayNetworking.send((ServerPlayerEntity) curPlayer, ModPackets.RESET_FIRST_PERSON, buf);
        }
    }

    // 新增：客户端执行FirstPerson重置
    public static void executeClientFirstPersonReset() {
        if (FabricLoader.getInstance().getEnvironmentType() != EnvType.CLIENT) {
            return;
        }

        if(IS_FIRST_PERSON_MOD_LOADED){
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


    public static void clearFormFlag(PlayerEntity player){
        boolean wasByCursedMoon = RegPlayerFormComponent.PLAYER_FORM.get(player).isByCursedMoon();

        ShapeShifterCurseFabric.LOGGER.info("Clearing form flags, wasByCursedMoon: " + wasByCursedMoon +
                ", _isByCursedMoonEnd: " + _isByCursedMoonEnd);

        // 只在诅咒月亮结束时才重置诅咒月亮标志\
        // Only reset the Cursed Moon flag when the Cursed Moon ends
        if (_isByCursedMoonEnd) {
            _isByCursedMoon = false;
            RegPlayerFormComponent.PLAYER_FORM.get(player).setByCursedMoon(false);
            ShapeShifterCurseFabric.LOGGER.info("Cleared cursed moon flag due to moon end");
        }

        // 重置其他标志
        // Reset other flags
        _isRegressedFromFinal = false;
        _isByCure = false;
        _isByCursedMoonEnd = false;

        RegPlayerFormComponent.PLAYER_FORM.get(player).setRegressedFromFinal(false);
        RegPlayerFormComponent.PLAYER_FORM.get(player).setByCure(false);
        RegPlayerFormComponent.PLAYER_FORM.sync(player);
    }

    public static void setIsByCursedMoonEnd(boolean value) {
        _isByCursedMoonEnd = value;
        ShapeShifterCurseFabric.LOGGER.info("设置_isByCursedMoonEnd=" + value);
    }

    public static void clearMoonEndFlags(PlayerEntity player) {
        ShapeShifterCurseFabric.LOGGER.info("安全清除月亮结束标记");

        // 记录清除前的状态
        // Record the state before clearing
        boolean wasByCursedMoon = RegPlayerFormComponent.PLAYER_FORM.get(player).isByCursedMoon();

        if (_isByCursedMoonEnd) {
            ShapeShifterCurseFabric.LOGGER.info("Clearing moon end flags (instinct should be preserved)");
            _isByCursedMoon = false;
            _isByCursedMoonEnd = false;
            RegPlayerFormComponent.PLAYER_FORM.get(player).setByCursedMoon(false);
            RegPlayerFormComponent.PLAYER_FORM.sync(player);
        }
        // 只清除状态标记，不影响instinct
        // Only clear the state flags, do not affect instinct
        _isRegressedFromFinal = false;
        _isByCure = false;

        RegPlayerFormComponent.PLAYER_FORM.get(player).setRegressedFromFinal(false);
        RegPlayerFormComponent.PLAYER_FORM.get(player).setByCure(false);
        RegPlayerFormComponent.PLAYER_FORM.sync(player);

        ShapeShifterCurseFabric.LOGGER.info("月亮标记已清除，原状态：" + wasByCursedMoon);
    }
}
