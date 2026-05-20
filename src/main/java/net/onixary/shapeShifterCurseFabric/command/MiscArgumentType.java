package net.onixary.shapeShifterCurseFabric.command;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.serialize.ArgumentSerializer;
import net.minecraft.network.PacketByteBuf;

import java.util.*;
import java.util.concurrent.CompletableFuture;


public class MiscArgumentType {
    public static class Enum_ArgumentType implements ArgumentType<String> {
        public final List<String> Suggestions = new ArrayList<>();

        public Enum_ArgumentType(String... suggestions) {
            Suggestions.addAll(Arrays.asList(suggestions));
        }

        @Override
        public String parse(StringReader reader) throws CommandSyntaxException {
            return reader.readString();
        }

        @Override
        public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
            String string = builder.getRemaining();
            for (String s : Suggestions) {
                if (s.startsWith(string)) {
                    builder.suggest(s);
                }
            }
            return builder.buildFuture();
        }
    }

    public static class Enum_ArgumentType_Serializer implements ArgumentSerializer<Enum_ArgumentType, Enum_ArgumentType_Serializer.Enum_ArgumentType_Properties> {

        @Override
        public void writePacket(Enum_ArgumentType_Properties properties, PacketByteBuf buf) {
            buf.writeInt(properties.data.size());
            for (String data : properties.data) {
                buf.writeString(data);
            }
        }

        @Override
        public Enum_ArgumentType_Properties fromPacket(PacketByteBuf buf) {
            int size = buf.readInt();
            List<String> datas = new ArrayList<>();
            for (int i = 0; i < size; i++) {
                datas.add(buf.readString());
            }
            return new Enum_ArgumentType_Properties(this, datas);
        }

        @Override
        public void writeJson(Enum_ArgumentType_Properties properties, JsonObject json) {
            JsonArray array = new JsonArray();
            for (String data : properties.data) {
                array.add(data);
            }
            json.add("data", array);
        }

        @Override
        public Enum_ArgumentType_Properties getArgumentTypeProperties(Enum_ArgumentType argumentType) {
            return new Enum_ArgumentType_Properties(this, argumentType.Suggestions);
        }

        public class Enum_ArgumentType_Properties implements ArgumentSerializer.ArgumentTypeProperties<Enum_ArgumentType> {
            final List<String> data;

            public Enum_ArgumentType_Properties(Enum_ArgumentType_Serializer ArgumentSerializer, List<String> data) {
                this.data = data;
            }

            public Enum_ArgumentType createType(CommandRegistryAccess commandRegistryAccess) {
                return new Enum_ArgumentType(data.toArray(new String[0]));
            }

            public ArgumentSerializer<Enum_ArgumentType, ?> getSerializer() {
                return Enum_ArgumentType_Serializer.this;
            }
        }
    }
}
