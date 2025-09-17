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
import net.onixary.shapeShifterCurseFabric.player_form.PlayerFormBase;
import net.onixary.shapeShifterCurseFabric.player_form.RegPlayerForms;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;


public class CustomFormArgumentType implements ArgumentType<Identifier> {

   public static final DynamicCommandExceptionType FORM_NOT_FOUND = new DynamicCommandExceptionType(
       o -> Text.translatable("commands.shape-shifter-curse.form_not_found", o)
   );

   public static CustomFormArgumentType form() {
      return new CustomFormArgumentType();
   }

   public Identifier parse(StringReader stringReader) throws CommandSyntaxException {
      return Identifier.fromCommandInput(stringReader);
   }

   public static PlayerFormBase getForm(CommandContext<ServerCommandSource> context, String argumentName) throws CommandSyntaxException {

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
            RegPlayerForms.playerForms.forEach((form) -> {
                if (form.GetIsCustomForm()) {
                    availableForms.add(form.FormID);
                }
            });
      }

      catch(IllegalArgumentException ignored) {}

      return CommandSource.suggestIdentifiers(availableForms.stream(), builder);

   }

}