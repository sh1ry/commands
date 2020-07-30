package io.github.shiryu.commands.bukkit.parameter;

import com.google.common.collect.ImmutableMap;
import io.github.shiryu.commands.api.locale.CommandLocale;
import io.github.shiryu.commands.api.parameter.ParameterType;
import io.github.shiryu.commands.api.sender.SimpleSender;
import org.bukkit.GameMode;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class GameModeParameterType implements ParameterType<GameMode> {

    private final Map<String, GameMode> TYPINGS = ImmutableMap.<String, GameMode>builder()
            .put("a", GameMode.ADVENTURE)
            .put("adventure", GameMode.ADVENTURE)
            .put("2", GameMode.ADVENTURE)
            .put("c", GameMode.CREATIVE)
            .put("creative", GameMode.CREATIVE)
            .put("1", GameMode.CREATIVE)
            .put("s", GameMode.SURVIVAL)
            .put("survival", GameMode.SURVIVAL)
            .put("0", GameMode.SURVIVAL)
            .build();

    @NotNull
    @Override
    public GameMode transform(@NotNull final SimpleSender sender, @NotNull String value) {
        if (!TYPINGS.containsKey(value)){
            sender.sendMessage(CommandLocale.NOT_FOUND);

            return null;
        }

        return TYPINGS.get(value);
    }

    @Override
    public @NotNull List<String> tabComplete(@NotNull SimpleSender sender, @NotNull Set<String> flags, @NotNull String value) {
        return TYPINGS.keySet().stream()
                .filter(s -> StringUtil.startsWithIgnoreCase(s, value))
                .collect(Collectors.toList());
    }
}
