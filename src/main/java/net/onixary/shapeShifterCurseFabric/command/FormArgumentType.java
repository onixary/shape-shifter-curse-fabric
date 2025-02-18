package net.onixary.shapeShifterCurseFabric.command;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.command.CommandSource;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.integration.origins.origin.Origin;
import net.onixary.shapeShifterCurseFabric.integration.origins.origin.OriginLayer;
import net.onixary.shapeShifterCurseFabric.integration.origins.origin.OriginLayers;
import net.onixary.shapeShifterCurseFabric.integration.origins.origin.OriginRegistry;
import net.onixary.shapeShifterCurseFabric.player_form.PlayerForms;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric.MOD_ID;

public class FormArgumentType implements ArgumentType<Identifier> {

   public static final DynamicCommandExceptionType FORM_NOT_FOUND = new DynamicCommandExceptionType(
       o -> Text.translatable("commands.shape_shifter_curse.form_not_found", o)
   );

   public static FormArgumentType form() {
      return new FormArgumentType();
   }

   public Identifier parse(StringReader stringReader) throws CommandSyntaxException {
      return Identifier.fromCommandInput(stringReader);
   }

   public static PlayerForms getForm(CommandContext<ServerCommandSource> context, String argumentName) throws CommandSyntaxException {

      Identifier id = context.getArgument(argumentName, Identifier.class);

      try {
            return PlayerForms.valueOf(id.getPath().toUpperCase());
      }

      catch(IllegalArgumentException e) {
         throw FORM_NOT_FOUND.create(id);
      }

   }

   @Override
   public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {

      List<Identifier> availableForms = new ArrayList<>();

      try {
            availableForms.add(new Identifier(MOD_ID, "original_before_enable"));
            availableForms.add(new Identifier(MOD_ID, "original_shifter"));
            availableForms.add(new Identifier(MOD_ID, "bat_0"));
            availableForms.add(new Identifier(MOD_ID, "bat_1"));
            availableForms.add(new Identifier(MOD_ID, "bat_2"));
      }

      catch(IllegalArgumentException ignored) {}

      return CommandSource.suggestIdentifiers(availableForms.stream(), builder);

   }

}