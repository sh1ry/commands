package io.github.shiryu.commands.api.parameter.defaults;

import com.google.common.collect.ImmutableMap;
import io.github.shiryu.commands.api.locale.CommandLocale;
import io.github.shiryu.commands.api.parameter.ParameterType;
import io.github.shiryu.commands.api.sender.SimpleSender;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class BooleanParameterType implements ParameterType<Boolean> {

    private final Map<String, Boolean> TYPINGS = ImmutableMap.<String, Boolean>builder()
            .put("true", true)
            .put("on", true)
            .put("yes", true)
            .put("false", false)
            .put("off", false)
            .put("no", false)
            .build();

    @NotNull
    @Override
    public Boolean transform(@NotNull final SimpleSender sender, @NotNull final String value) {
        if (!TYPINGS.containsKey(value)){
            sender.sendMessage(String.format(CommandLocale.NOT_FOUND, value));

            return false;
        }

        return TYPINGS.get(value);
    }

    @Override
    public @NotNull List<String> tabComplete(@NotNull final SimpleSender sender, @NotNull final Set<String> flags, @NotNull final String value) {
        return TYPINGS.keySet().stream()
                .filter(s -> StringUtils.startsWithIgnoreCase(s, value))
                .collect(Collectors.toList());
    }
}
