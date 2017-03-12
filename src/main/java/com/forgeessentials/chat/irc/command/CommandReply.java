package com.forgeessentials.chat.irc.command;

import java.util.Arrays;
import java.util.Collection;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerNotFoundException;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;

import com.forgeessentials.chat.ModuleChat;
import com.forgeessentials.chat.irc.IrcCommand;

public class CommandReply implements IrcCommand
{

    @Override
    public Collection<String> getCommandNames()
    {
        return Arrays.asList("reply", "r");
    }

    @Override
    public String getCommandUsage()
    {
        return "<message...>";
    }

    @Override
    public String getCommandHelp()
    {
        return "R\u00E9pondre \u00E0 votre dernier message priv\u00E9";
    }

    @Override
    public boolean isAdminCommand()
    {
        return false;
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args)
    {
        if (args.length < 1)
            throw new WrongUsageException("commands.message.usage", new Object[0]);

        ICommandSender target = com.forgeessentials.chat.command.CommandReply.getReplyTarget(sender);
        if (target == null)
            throw new PlayerNotFoundException();

        if (target == sender)
            throw new PlayerNotFoundException("commands.message.sameTarget", new Object[0]);

        ModuleChat.tell(sender, CommandBase.func_147176_a(sender, args, 0, !(sender instanceof EntityPlayer)), target);
    }

}
