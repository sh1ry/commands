package io.github.shiryu.commands.api.parameter.defaults;

import com.google.common.collect.Lists;
import io.github.shiryu.commands.api.locale.CommandLocale;
import io.github.shiryu.commands.api.parameter.ParameterType;
import io.github.shiryu.commands.api.sender.SimpleSender;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;

public class IntegerParameterType implements ParameterType<Integer> {

    @Override
    public Integer transform(@NotNull final SimpleSender sender, @NotNull final String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException exception) {
            sender.sendMessage(String.format(CommandLocale.NOT_VALID_NUMBER, value));
            return null;
        }
    }

    @Override
    public List<String> tabComplete(@NotNull final SimpleSender sender, @NotNull final Set<String> flags, @NotNull final String value) {
        return Lists.newArrayList();
    }

}