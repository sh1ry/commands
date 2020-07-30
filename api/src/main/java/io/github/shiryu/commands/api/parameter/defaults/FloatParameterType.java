package io.github.shiryu.commands.api.parameter.defaults;


import com.google.common.collect.Lists;
import io.github.shiryu.commands.api.locale.CommandLocale;
import io.github.shiryu.commands.api.parameter.ParameterType;
import io.github.shiryu.commands.api.sender.SimpleSender;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class FloatParameterType implements ParameterType<Float> {

    public Float transform(@NotNull final SimpleSender sender, @NotNull final String value) {
        if (value.toLowerCase().contains("e")) {
            sender.sendMessage(String.format(CommandLocale.NOT_VALID_NUMBER, value));
            return (null);
        }

        try {
            float parsed = Float.parseFloat(value);

            if (Float.isNaN(parsed) || !Float.isFinite(parsed)) {
                sender.sendMessage(String.format(CommandLocale.NOT_VALID_NUMBER, value));
                return (null);
            }

            return (parsed);
        } catch (NumberFormatException exception) {
            sender.sendMessage(String.format(CommandLocale.NOT_VALID_NUMBER, value));
            return (null);
        }
    }

    @NotNull
    public List<String> tabComplete(@NotNull final SimpleSender sender, @NotNull final Set<String> flags, @NotNull final String value) {
        return Lists.newArrayList();
    }

}