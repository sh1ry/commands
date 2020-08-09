package io.github.shiryu.commands.bungee;

import io.github.shiryu.commands.api.sender.SimpleSender;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import org.jetbrains.annotations.NotNull;

@Getter
@RequiredArgsConstructor
public class BungeeSender implements SimpleSender {

    private final CommandSender sender;

    @Override
    public boolean hasPermission(@NotNull final String permission) {
        return sender.hasPermission(permission);
    }

    @Override
    public void sendMessage(@NotNull final String message) {
        sender.sendMessage(new TextComponent(message));
    }
}
