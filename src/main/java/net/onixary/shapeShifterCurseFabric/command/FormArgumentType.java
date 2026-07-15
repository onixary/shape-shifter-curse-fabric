package net.onixary.shapeShifterCurseFabric.command;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.serialize.ArgumentSerializer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.player_form.IForm;
import net.onixary.shapeShifterCurseFabric.player_form.ISubForm;
import net.onixary.shapeShifterCurseFabric.player_form.RegPlayerForms;
import net.onixary.shapeShifterCurseFabric.player_form.utils.FormUtils;
import net.onixary.shapeShifterCurseFabric.util.ClientUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiPredicate;
import java.util.function.Function;


public class FormArgumentType implements ArgumentType<Identifier> {
    public static final HashMap<Identifier, Function<@Nullable PlayerEntity, @NotNull List<Identifier>>> SUGGESTIONS_PROVIDERS_REGISTRY = new HashMap<>();

    public static @NotNull Identifier registerSuggestionsProvider(@NotNull Identifier id, @NotNull Function<@Nullable PlayerEntity, @NotNull List<Identifier>> suggestionsProvider) {
        SUGGESTIONS_PROVIDERS_REGISTRY.put(id, suggestionsProvider);
        return id;
    }

    public static @Nullable Function<@Nullable PlayerEntity, @NotNull List<Identifier>> getRegisteredSuggestionsProvider(@Nullable Identifier id) {
        return SUGGESTIONS_PROVIDERS_REGISTRY.get(id);
    }

    public static Function<@Nullable PlayerEntity, @NotNull List<Identifier>> buildSuggestionsProvider(BiPredicate<@Nullable PlayerEntity, @NotNull IForm> filter) {
        return player -> {
            List<Identifier> availableForms = new ArrayList<>();
            RegPlayerForms.playerForms.forEach((formID, form) -> {
                if (filter.test(player, form)) {
                    availableForms.add(form.getFormID());
                }
            });
            return availableForms;
        };
    }

    public static final BiPredicate<@Nullable PlayerEntity, @NotNull IForm> NonDynamicForms = (player, form) -> !form.isDynamicForm();
    public static final BiPredicate<@Nullable PlayerEntity, @NotNull IForm> DynamicForms = (player, form) -> form.isDynamicForm();
    public static final BiPredicate<@Nullable PlayerEntity, @NotNull IForm> NonSubForms = (player, form) -> !(form instanceof ISubForm);
    public static final BiPredicate<@Nullable PlayerEntity, @NotNull IForm> SubForms = (player, form) -> form instanceof ISubForm;
    public static final BiPredicate<@Nullable PlayerEntity, @NotNull IForm> UsableForms = FormUtils::isFormCanUse;
    public static final Identifier ALL_FORM_ARG = registerSuggestionsProvider(ShapeShifterCurseFabric.identifier("all_form_arg"), buildSuggestionsProvider(((player, form) -> true)));
    public static final Identifier SET_FORM_ARG = registerSuggestionsProvider(ShapeShifterCurseFabric.identifier("set_form_arg"), buildSuggestionsProvider(NonDynamicForms.and(NonSubForms)));
    public static final Identifier SET_DYNAMIC_FORM_ARG = registerSuggestionsProvider(ShapeShifterCurseFabric.identifier("set_dynamic_form_arg"), buildSuggestionsProvider(DynamicForms.and(NonSubForms)));
    public static final Identifier SET_SUB_FORM_ARG = registerSuggestionsProvider(ShapeShifterCurseFabric.identifier("set_sub_form_arg"), buildSuggestionsProvider(NonDynamicForms.and(SubForms)));

    private final Identifier suggestionsProvider;

    public FormArgumentType(Identifier suggestionsProvider) {
        this.suggestionsProvider = suggestionsProvider;
    }

    public static final DynamicCommandExceptionType FORM_NOT_FOUND = new DynamicCommandExceptionType(
        o -> Text.translatable("commands.shape-shifter-curse.form_not_found", o)
    );

    public Identifier parse(StringReader stringReader) throws CommandSyntaxException {
       return Identifier.fromCommandInput(stringReader);
    }

    public static IForm getForm(CommandContext<ServerCommandSource> context, String argumentName) throws CommandSyntaxException {

       Identifier id = context.getArgument(argumentName, Identifier.class);

       try {
             return RegPlayerForms.playerForms.get(id);
       }

       catch(IllegalArgumentException e) {
          throw FORM_NOT_FOUND.create(id);
       }

    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {

       List<Identifier> availableForms = new ArrayList<>();

       try {
           if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
               PlayerEntity player = ClientUtils.getPlayer();
               Function<@Nullable PlayerEntity, @NotNull List<Identifier>> suggestionsProviderFunc = getRegisteredSuggestionsProvider(suggestionsProvider);
               if (suggestionsProviderFunc != null) {
                   availableForms = suggestionsProviderFunc.apply(player);
               }
           }
       }
       catch(IllegalArgumentException ignored) {}
       return CommandSource.suggestIdentifiers(availableForms.stream(), builder);
    }

    public static class Form_ArgumentType_Serializer implements ArgumentSerializer<FormArgumentType, FormArgumentType.Form_ArgumentType_Serializer.Form_ArgumentType_Properties> {

        @Override
        public void writePacket(Form_ArgumentType_Properties properties, PacketByteBuf buf) {
            buf.writeIdentifier(properties.data);
        }

        @Override
        public Form_ArgumentType_Properties fromPacket(PacketByteBuf buf) {
            return new Form_ArgumentType_Properties(this, buf.readIdentifier());
        }

        @Override
        public void writeJson(Form_ArgumentType_Properties properties, JsonObject json) {
            json.addProperty("data", properties.data.toString());
        }

        @Override
        public Form_ArgumentType_Properties getArgumentTypeProperties(FormArgumentType argumentType) {
            return new Form_ArgumentType_Properties(this, argumentType.suggestionsProvider);
        }

        public class Form_ArgumentType_Properties implements ArgumentSerializer.ArgumentTypeProperties<FormArgumentType> {
            final Identifier data;

            public Form_ArgumentType_Properties(Form_ArgumentType_Serializer ArgumentSerializer, Identifier data) {
                this.data = data;
            }

            public FormArgumentType createType(CommandRegistryAccess commandRegistryAccess) {
                return new FormArgumentType(data);
            }

            public ArgumentSerializer<FormArgumentType, ?> getSerializer() {
                return Form_ArgumentType_Serializer.this;
            }
        }
    }
}