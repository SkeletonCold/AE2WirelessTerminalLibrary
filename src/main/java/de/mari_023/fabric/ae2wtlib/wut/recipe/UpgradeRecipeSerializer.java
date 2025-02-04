package de.mari_023.fabric.ae2wtlib.wut.recipe;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import de.mari_023.fabric.ae2wtlib.wut.WUTHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.util.Identifier;

public class UpgradeRecipeSerializer implements RecipeSerializer<Upgrade> {
    private UpgradeRecipeSerializer() { }

    public static final UpgradeRecipeSerializer INSTANCE = new UpgradeRecipeSerializer();

    public static final Identifier ID = new Identifier("ae2wtlib", "upgrade");

    @Override
    public Upgrade read(Identifier id, JsonObject json) {
        UpgradeJsonFormat recipeJson = new Gson().fromJson(json, UpgradeJsonFormat.class);
        if(recipeJson.Terminal == null || validateOutput(recipeJson.TerminalName))
            throw new JsonSyntaxException("A required attribute is missing or invalid!");

        Ingredient Terminal = Ingredient.fromJson(recipeJson.Terminal);

        return new Upgrade(Terminal, recipeJson.TerminalName, id);
    }

    @Override
    public void write(PacketByteBuf packetData, Upgrade recipe) {
        recipe.getTerminal().write(packetData);
        packetData.writeString(recipe.getTerminalName());
    }

    @Override
    public Upgrade read(Identifier id, PacketByteBuf packetData) {
        Ingredient TerminalA = Ingredient.fromPacket(packetData);
        String TerminalAName = packetData.readString(32767);
        return new Upgrade(TerminalA, TerminalAName, id);
    }

    private boolean validateOutput(String s) {
        if(s == null) return true;
        return !WUTHandler.terminalNames.contains(s);
    }
}