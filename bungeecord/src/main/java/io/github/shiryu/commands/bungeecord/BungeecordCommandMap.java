package io.github.shiryu.commands.bungeecord;

import io.github.shiryu.commands.api.models.CommandExecutable;
import io.github.shiryu.commands.api.models.SimpleParameter;
import lombok.RequiredArgsConstructor;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.TabCompleteEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

@RequiredArgsConstructor
public class BungeecordCommandMap implements Listener {

    private final BungeecordCommandManager commandManager;
    private final Map<UUID, String[]> parameters = new HashMap<>();

    @EventHandler
    public void tabComplete(final TabCompleteEvent event){
        if (!(event.getSender() instanceof ProxiedPlayer)) return;

        event.getSuggestions().clear();

        final ProxiedPlayer player = (ProxiedPlayer) event.getSender();
        final BungeeSender bungeeSender = new BungeeSender(player);

        final String cmdLine = event.getCursor();

        parameters.put(player.getUniqueId(), cmdLine.split(" "));

        try {
            int spaceIndex = cmdLine.indexOf(' ');
            Set<String> completions = new HashSet<>();

            boolean doneHere = false;

            CommandLoop:
            for (CommandExecutable command : commandManager.getCommands()) {
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


                            doneHere = true;

                            break CommandLoop;
                        } else {
                            String halfSplitString = split.toLowerCase().replaceFirst(alias.split(" ")[0].toLowerCase(), "").trim();
                            String[] splitString = halfSplitString.split(" ");

                            String fixedAlias = splitString[splitString.length - 1].trim();
                            String lastArg = cmdLine.endsWith(" ") ? "" : cmdLine.split(" ")[cmdLine.split(" ").length - 1];

                            if (fixedAlias.length() >= lastArg.length()) {
                                completions.add(fixedAlias);
                            }

                            doneHere = true;
                        }
                    }
                }
            }

            List<String> completionList = new ArrayList<>(completions);

            if (!doneHere) {
                List<String> vanillaCompletionList = event.getSuggestions();

                if (vanillaCompletionList == null) {
                    vanillaCompletionList = new ArrayList<>();
                }

                for (String vanillaCompletion : vanillaCompletionList) {
                    completionList.add(vanillaCompletion);
                }
            }

            Collections.sort(completionList, (o1, o2) -> (o2.length() - o1.length()));

            completionList.remove("w");

            event.getSuggestions().addAll(completionList);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            parameters.remove(player.getUniqueId());
        }
    }

}
