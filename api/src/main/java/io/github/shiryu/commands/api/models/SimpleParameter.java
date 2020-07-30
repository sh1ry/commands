package io.github.shiryu.commands.api.models;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class SimpleParameter {

    private final String name;
    private final String defaultValue;
    private final String[] tabCompleteFlags;
    private final Class<?> parameterClass;

}
