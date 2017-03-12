package com.forgeessentials.chat.command;

import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.permission.PermissionLevel;

import com.forgeessentials.chat.irc.IrcHandler;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import com.forgeessentials.core.misc.TranslatedCommandException;

public class CommandIrc extends ForgeEssentialsCommandBase
{

    @Override
    public String getCommandName()
    {
        return "irc";
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {
        return "/irc <message...>: Envoie un massage \u00E0 un client sur IRC.";
    }

    @Override
    public String getPermissionNode()
    {
        return "fe.chat.irc";
    }

    @Override
    public PermissionLevel getPermissionLevel()
    {
        return PermissionLevel.TRUE;
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return true;
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args)
    {
        if (!IrcHandler.getInstance().isConnected())
            throw new TranslatedCommandException("Non connect\u00E9 \u00E0 IRC !");
        if (args.length < 1)
        {
            throw new WrongUsageException("commands.message.usage");
        }
        else
        {
            IChatComponent message = func_147176_a(sender, args, 0, !(sender instanceof EntityPlayer));
            IrcHandler.getInstance().sendPlayerMessage(sender, message);
        }
    }

}
