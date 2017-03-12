package com.forgeessentials.chat.command;

import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerNotFoundException;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.permission.PermissionLevel;

import com.forgeessentials.chat.ModuleChat;
import com.forgeessentials.chat.irc.IrcHandler;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import com.forgeessentials.core.misc.TranslatedCommandException;

public class CommandIrcPm extends ForgeEssentialsCommandBase
{

    @Override
    public String getCommandName()
    {
        return "ircpm";
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {
        return "/ircpm <name> <message...>: Envoie un massage priv\u00E9 \u00E0 un client sur IRC.";
    }

    @Override
    public String getPermissionNode()
    {
        return "fe.chat.ircpm";
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
        if (args.length < 2)
        {
            throw new WrongUsageException("commands.message.usage");
        }
        else
        {
            ICommandSender target = IrcHandler.getInstance().getIrcUser(args[0]);
            if (target == null)
            {
                throw new PlayerNotFoundException();
            }
            else if (target == sender)
            {
                throw new PlayerNotFoundException("commands.message.sameTarget");
            }
            else
            {
                IChatComponent message = func_147176_a(sender, args, 1, !(sender instanceof EntityPlayer));
                ModuleChat.tell(sender, message, target);
            }
        }
    }

    /**
     * Adds the strings available in this command to the given list of tab completion options.
     */
    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args)
    {
        if (args.length != 1)
            return null;
        return getListOfStringsMatchingLastWord(args, IrcHandler.getInstance().getIrcUserNames());
    }

}
