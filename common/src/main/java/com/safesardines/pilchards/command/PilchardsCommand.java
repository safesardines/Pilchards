package com.safesardines.pilchards.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.safesardines.pilchards.glint.Glint;
import com.safesardines.pilchards.glint.GlintStyle;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public final class PilchardsCommand {
    private PilchardsCommand() {}

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        LiteralArgumentBuilder<CommandSourceStack> color = Commands.literal("color")
                .then(Commands.argument("color", StringArgumentType.string())
                        .executes(context -> color(
                                context.getSource(),
                                StringArgumentType.getString(context, "color"),
                                1.0F))
                        .then(Commands.argument("brightness", FloatArgumentType.floatArg(0.0F, 8.0F))
                                .executes(context -> color(
                                        context.getSource(),
                                        StringArgumentType.getString(context, "color"),
                                        FloatArgumentType.getFloat(context, "brightness")))));
        LiteralArgumentBuilder<CommandSourceStack> texture = Commands.literal("texture")
                .then(Commands.argument("id", ResourceLocationArgument.id())
                        .executes(context -> texture(
                                context.getSource(),
                                ResourceLocationArgument.getId(context, "id"))));
        LiteralArgumentBuilder<CommandSourceStack> glintClear = Commands.literal("clear")
                .executes(context -> clear(context.getSource()));
        LiteralArgumentBuilder<CommandSourceStack> glint = Commands.literal("glint")
                .then(color)
                .then(texture)
                .then(glintClear);

        dispatcher.register(Commands.literal("pilchards")
                .requires(source -> source.hasPermission(2))
                .then(glint));
    }

    private static int color(CommandSourceStack source, String input, float brightness) {
        Integer rgb = parseHex(input);
        if (rgb == null) {
            source.sendFailure(Component.translatable("commands.pilchards.glint.invalid_color"));
            return 0;
        }
        ItemStack stack = heldItem(source);
        if (stack == null) {
            return 0;
        }
        Glint.custom(stack, GlintStyle.ofColor(rgb).toBuilder().brightness(brightness).build());
        source.sendSuccess(() -> Component.translatable("commands.pilchards.glint.color.success"), false);
        return 1;
    }

    private static int texture(CommandSourceStack source, ResourceLocation id) {
        ItemStack stack = heldItem(source);
        if (stack == null) {
            return 0;
        }
        Glint.texture(stack, id);
        source.sendSuccess(() -> Component.translatable("commands.pilchards.glint.texture.success"), false);
        return 1;
    }

    private static int clear(CommandSourceStack source) {
        ItemStack stack = heldItem(source);
        if (stack == null) {
            return 0;
        }
        Glint.clear(stack);
        source.sendSuccess(() -> Component.translatable("commands.pilchards.glint.clear.success"), false);
        return 1;
    }

    private static ItemStack heldItem(CommandSourceStack source) {
        if (!source.isPlayer()) {
            source.sendFailure(Component.translatable("commands.pilchards.players_only"));
            return null;
        }
        ItemStack stack = source.getPlayer().getMainHandItem();
        if (stack.isEmpty()) {
            source.sendFailure(Component.translatable("commands.pilchards.no_item"));
            return null;
        }
        return stack;
    }

    private static Integer parseHex(String input) {
        String value = input.startsWith("#") ? input.substring(1) : input;
        try {
            int rgb = Integer.parseInt(value, 16);
            return rgb >= 0 && rgb <= 0xFFFFFF ? rgb : null;
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
