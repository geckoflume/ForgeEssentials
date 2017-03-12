package com.forgeessentials.chat.irc.command;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map.Entry;

import com.forgeessentials.chat.irc.IrcCommand;
import com.forgeessentials.chat.irc.IrcCommand.IrcCommandParser;
import com.forgeessentials.chat.irc.IrcHandler;
import com.forgeessentials.util.CommandParserArgs;

public class CommandHelp extends IrcCommandParser
{

    @Override
    public Collection<String> getCommandNames()
    {
        return Arrays.asList("help");
    }

    @Override
    public String getCommandUsage()
    {
        return "";
    }

    @Override
    public String getCommandHelp()
    {
        return "Affiche l'aide";
    }

    @Override
    public boolean isAdminCommand()
    {
        return false;
    }

    @Override
    public void parse(CommandParserArgs arguments)
    {
        arguments.confirm("Liste des commandes :");
        for (Entry<String, IrcCommand> command : IrcHandler.getInstance().commands.entrySet())
        {
            arguments.confirm(COMMAND_CHAR + command.getKey() + " " + command.getValue().getCommandUsage() + ": " + command.getValue().getCommandHelp());
        }
    }

}
