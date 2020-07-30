package io.github.shiryu.commands.bukkit;

import com.google.common.collect.Lists;
import io.github.shiryu.commands.api.models.CommandExecutable;
import io.github.shiryu.commands.api.models.SimpleParameter;
import lombok.Getter;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.util.*;

@Getter
public class BukkitCommandMap extends SimpleCommandMap {

    private final BukkitCommandManager commandManager;
    private final Map<UUID, String[]> parameters = new HashMap<>();

    public BukkitCommandMap(@NotNull final Server server, @NotNull final BukkitCommandManager commandManager) {
        super(server);

        this.commandManager = commandManager;
    }

    @Override
    public List<String> tabComplete(@NotNull final CommandSender sender, @NotNull final String cmdLine) {
        if (!(sender instanceof Player)) {
            return (null);
        }

        final Player player = (Player) sender;
        final BukkitSender bukkitSender = new BukkitSender(player);

        parameters.put(player.getUniqueId(), cmdLine.split(" "));

        try {
            int spaceIndex = cmdLine.indexOf(' ');
            Set<String> completions = new HashSet<>();

            boolean doneHere = false;

            CommandLoop:
            for (CommandExecutable command : commandManager.getCommands()) {
                if (!command.canAccess(bukkitSender))
                    continue;


                for (String alias : command.getNames()) {
                    String split = alias.split(" ")[0];

                    if (spaceIndex != -1)
                        split = alias;


                    if (StringUtil.startsWithIgnoreCase(split.trim(), cmdLine.trim()) || StringUtil.startsWithIgnoreCase(cmdLine.trim(), split.trim())) {
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

                            for (String completion : commandManager.tabCompleteParameter(bukkitSender, cmdLine.endsWith(" ") ? "" : params[params.length - 1], paramData.getParameterClass(), paramData.getTabCompleteFlags()))
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
                List<String> vanillaCompletionList = super.tabComplete(sender, cmdLine);

                if (vanillaCompletionList == null) {
                    vanillaCompletionList = new ArrayList<>();
                }

                for (String vanillaCompletion : vanillaCompletionList) {
                    completionList.add(vanillaCompletion);
                }
            }

            Collections.sort(completionList, (o1, o2) -> (o2.length() - o1.length()));

            completionList.remove("w");

            return completionList;
        } catch (Exception e) {
            return Lists.newArrayList();
        } finally {
            parameters.remove(player.getUniqueId());
        }
    }
}
