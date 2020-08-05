package io.github.shiryu.commands.bungeecord;

import io.github.shiryu.commands.api.sender.SimpleSender;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

@Getter
@RequiredArgsConstructor
public class BungeeSender implements SimpleSender {

    private final CommandSender sender;

    @Override
    public boolean hasPermission(@NotNull String permission) {
        return this.sender.hasPermission(permission);
    }

    @Override
    public void sendMessage(@NotNull String message) {
        this.sender.sendMessage(new TextComponent(message));
    }
}
