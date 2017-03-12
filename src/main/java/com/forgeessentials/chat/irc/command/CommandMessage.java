package com.forgeessentials.chat.irc.command;

import java.util.Arrays;
import java.util.Collection;

import net.minecraft.command.CommandBase;
import net.minecraft.util.IChatComponent;

import com.forgeessentials.api.UserIdent;
import com.forgeessentials.chat.ModuleChat;
import com.forgeessentials.chat.irc.IrcCommand.IrcCommandParser;
import com.forgeessentials.util.CommandParserArgs;

public class CommandMessage extends IrcCommandParser
{

    @Override
    public Collection<String> getCommandNames()
    {
        return Arrays.asList("msg", "m");
    }

    @Override
    public String getCommandUsage()
    {
        return "<joueur> <message...>";
    }

    @Override
    public String getCommandHelp()
    {
        return "Envoie un message priv\u00E9 \u00E0 un joueur";
    }

    @Override
    public boolean isAdminCommand()
    {
        return false;
    }

    @Override
    public void parse(CommandParserArgs arguments)
    {
        if (arguments.isEmpty())
        {
            arguments.error("Aucun joueur sp\u00E9cifi\u00E9 !");
            return;
        }

        UserIdent player = arguments.parsePlayer(true, true);
        
        if (arguments.isEmpty())
        {
            arguments.error("Aucun message sp\u00E9cifi\u00E9 !");
            return;
        }

        IChatComponent msg = CommandBase.func_147176_a(arguments.sender, arguments.toArray(), 0, true);
        ModuleChat.tell(arguments.sender, msg, player.getPlayer());
    }

}
