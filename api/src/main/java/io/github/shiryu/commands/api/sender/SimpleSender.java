package io.github.shiryu.commands.api.sender;

import org.jetbrains.annotations.NotNull;

public interface SimpleSender {

    boolean hasPermission(@NotNull final String permission);

    void sendMessage(@NotNull final String message);
}
