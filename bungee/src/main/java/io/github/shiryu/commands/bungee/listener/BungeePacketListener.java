package io.github.shiryu.commands.bungee.listener;

import de.exceptionflug.protocolize.api.event.PacketReceiveEvent;
import de.exceptionflug.protocolize.api.handler.PacketAdapter;
import de.exceptionflug.protocolize.api.protocol.Stream;
import io.github.shiryu.commands.api.models.CommandExecutable;
import io.github.shiryu.commands.api.models.SimpleParameter;
import io.github.shiryu.commands.bungee.BungeeCommandManager;
import io.github.shiryu.commands.bungee.BungeeSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.protocol.AbstractPacketHandler;
import net.md_5.bungee.protocol.packet.TabCompleteResponse;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class BungeePacketListener extends PacketAdapter<TabCompleteResponse> {

    private final BungeeCommandManager commandManager;

    public BungeePacketListener(@NotNull final BungeeCommandManager commandManager) {
        super(Stream.DOWNSTREAM, TabCompleteResponse.class);

        this.commandManager = commandManager;
    }

    @Override
    public void receive(@NotNull final PacketReceiveEvent<TabCompleteResponse> event) {
        if (event.getPlayer() == null) return;

        final TabCompleteResponse packet = event.getPacket();

        final ProxiedPlayer player = event.getPlayer();
        final BungeeSender bungeeSender = new BungeeSender(player);

        final List<String> completions = new ArrayList<>(packet.getCommands());

        for (CommandExecutable command : commandManager.getCommands()){
            String cmdLine = command.getName();
            int spaceIndex = cmdLine.indexOf(' ');

            if (!command.canAccess(bungeeSender))
                continue;

            for (String alias : command.getNames()) {
                String split = alias.split(" ")[0];

                if (spaceIndex != -1)
                    split = alias;


                if (StringUtils.startsWithIgnoreCase(split.trim(), cmdLine.trim()) || StringUtils.startsWithIgnoreCase(cmdLine.trim(), split.trim())) {
                    if (spaceIndex == -1 && cmdLine.length() < alias.length()) {
                        completions.add("/" + split.toLowerCase());
                    } else if (cmdLine.toLowerCase().startsWith(alias.toLowerCase() + " ") && command.getParameters().size() > 0) {
                        int paramIndex = (cmdLine.split(" ").length - alias.split(" ").length);

                        if (paramIndex == command.getParameters().size() || !cmdLine.endsWith(" "))
                            paramIndex = paramIndex - 1;


                        if (paramIndex < 0)
                            paramIndex = 0;


                        SimpleParameter paramData = command.getParameters().get(paramIndex);
                        String[] params = cmdLine.split(" ");

                        for (String completion : commandManager.tabCompleteParameter(bungeeSender, cmdLine.endsWith(" ") ? "" : params[params.length - 1], paramData.getParameterClass(), paramData.getTabCompleteFlags()))
                            completions.add(completion);

                        break;
                    } else {
                        String halfSplitString = split.toLowerCase().replaceFirst(alias.split(" ")[0].toLowerCase(), "").trim();
                        String[] splitString = halfSplitString.split(" ");

                        String fixedAlias = splitString[splitString.length - 1].trim();
                        String lastArg = cmdLine.endsWith(" ") ? "" : cmdLine.split(" ")[cmdLine.split(" ").length - 1];

                        if (fixedAlias.length() >= lastArg.length()) {
                            completions.add(fixedAlias);
                        }
                    }
                }
            }
        }

        Collections.sort(completions, (o1, o2) -> (o2.length() - o1.length()));
        
        packet.setCommands(completions);

        event.markForRewrite();
    }
}
