package com.forgeessentials.commands.player;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.permission.PermissionLevel;
import net.minecraftforge.permission.PermissionManager;

import org.apache.commons.lang3.StringUtils;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.UserIdent;
import com.forgeessentials.commands.ModuleCommands;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import com.forgeessentials.core.misc.TranslatedCommandException;
import com.forgeessentials.core.misc.Translator;
import com.forgeessentials.util.output.ChatOutputHandler;

import cpw.mods.fml.common.FMLCommonHandler;

/**
 * Allows you to modify a bunch of interesting stuff...
 */

public class CommandCapabilities extends ForgeEssentialsCommandBase
{
    public static ArrayList<String> names;

    static
    {
        names = new ArrayList<>();
        names.add("disabledamage");
        names.add("isflying");
        names.add("allowflying");
        names.add("iscreativemode");
        names.add("allowedit");
    }

    @Override
    public String getCommandName()
    {
        return "fecapabilities";
    }

    @Override
    public String[] getDefaultAliases()
    {
        return new String[] { "capabilities" };
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {
        return "/capabilities [joueur] [capacit\u00E9] [value|default] Vous permet de modifier les capacit\u00E9s d'un joueur";
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return true;
    }

    @Override
    public PermissionLevel getPermissionLevel()
    {
        return PermissionLevel.OP;
    }

    @Override
    public String getPermissionNode()
    {
        return ModuleCommands.PERM + ".capabilities";
    }

    @Override
    public void registerExtraPermissions()
    {
        APIRegistry.perms.registerPermission(getPermissionNode() + ".others", PermissionLevel.OP);
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args)
    {
        if (args.length > 3)
        {
            throw new TranslatedCommandException(getCommandUsage(sender));
        }

        if (args.length == 0)
        {
            ChatOutputHandler.chatNotification(sender, "Capacit\u00E9s possibles :");
            ChatOutputHandler.chatNotification(sender, StringUtils.join(names.toArray(), ", "));
        }
        else if (args.length == 1)
        {
            EntityPlayerMP player = UserIdent.getPlayerByMatchOrUsername(sender, args[0]);
            if (player != null)
            {
                ChatOutputHandler.chatNotification(sender, Translator.format("Capacit\u00E9s pour %s:", player.getCommandSenderName()));
                ChatOutputHandler.chatNotification(sender, names.get(0) + " = " + player.capabilities.disableDamage);
                ChatOutputHandler.chatNotification(sender, names.get(1) + " = " + player.capabilities.isFlying);
                ChatOutputHandler.chatNotification(sender, names.get(2) + " = " + player.capabilities.allowFlying);
                ChatOutputHandler.chatNotification(sender, names.get(3) + " = " + player.capabilities.isCreativeMode);
                ChatOutputHandler.chatNotification(sender, names.get(4) + " = " + player.capabilities.allowEdit);
            }
            else
            {
                ChatOutputHandler.chatError(sender, String.format("Le joueur %s n'existe pas ou n'est pas en ligne.", args[0]));
            }
        }
        else if (args.length == 2)
        {
            if (sender instanceof EntityPlayer && !PermissionManager.checkPermission((EntityPlayer) sender, getPermissionNode() + ".others"))
                throw new TranslatedCommandException("Vous n'avez pas la permission pour faire cela !");

            EntityPlayerMP player = UserIdent.getPlayerByMatchOrUsername(sender, args[0]);
            if (player != null)
            {
                if (args[1].equalsIgnoreCase(names.get(0)))
                {
                    ChatOutputHandler
                            .chatNotification(sender, player.getCommandSenderName() + " => " + names.get(0) + " = " + player.capabilities.disableDamage);
                }
                else if (args[1].equalsIgnoreCase(names.get(1)))
                {
                    ChatOutputHandler.chatNotification(sender, player.getCommandSenderName() + " => " + names.get(1) + " = " + player.capabilities.isFlying);
                }
                else if (args[1].equalsIgnoreCase(names.get(2)))
                {
                    ChatOutputHandler.chatNotification(sender, player.getCommandSenderName() + " => " + names.get(2) + " = " + player.capabilities.allowFlying);
                }
                else if (args[1].equalsIgnoreCase(names.get(3)))
                {
                    ChatOutputHandler
                            .chatNotification(sender, player.getCommandSenderName() + " => " + names.get(3) + " = " + player.capabilities.isCreativeMode);
                }
                else if (args[1].equalsIgnoreCase(names.get(4)))
                {
                    ChatOutputHandler.chatNotification(sender, player.getCommandSenderName() + " => " + names.get(4) + " = " + player.capabilities.allowEdit);
                }
                else
                    throw new CommandException("Capacit\u00E9 '%s' inconnue.", args[1]);
            }
        }
        else if (args.length == 3)
        {
            if (sender instanceof EntityPlayer && !PermissionManager.checkPermission((EntityPlayer) sender, getPermissionNode() + ".others"))
                throw new TranslatedCommandException("Vous n'avez pas la permission pour faire cela !");
            EntityPlayerMP player = UserIdent.getPlayerByMatchOrUsername(sender, args[0]);
            if (player != null)
            {
                if (args[1].equalsIgnoreCase(names.get(0)))
                {
                    boolean bln = Boolean.parseBoolean(args[2]);
                    player.capabilities.disableDamage = bln;
                    ChatOutputHandler.chatNotification(sender, names.get(0) + " = " + player.capabilities.disableDamage);
                }
                else if (args[1].equalsIgnoreCase(names.get(1)))
                {
                    boolean bln = Boolean.parseBoolean(args[2]);
                    player.capabilities.isFlying = bln;
                    ChatOutputHandler.chatNotification(sender, names.get(1) + " = " + player.capabilities.isFlying);
                }
                else if (args[1].equalsIgnoreCase(names.get(2)))
                {
                    boolean bln = Boolean.parseBoolean(args[2]);
                    player.capabilities.allowFlying = bln;
                    ChatOutputHandler.chatNotification(sender, names.get(2) + " = " + player.capabilities.allowFlying);
                }
                else if (args[1].equalsIgnoreCase(names.get(3)))
                {
                    boolean bln = Boolean.parseBoolean(args[2]);
                    player.capabilities.isCreativeMode = bln;
                    ChatOutputHandler.chatNotification(sender, names.get(3) + " = " + player.capabilities.isCreativeMode);
                }
                else if (args[1].equalsIgnoreCase(names.get(4)))
                {
                    boolean bln = Boolean.parseBoolean(args[2]);
                    player.capabilities.allowEdit = bln;
                    ChatOutputHandler.chatNotification(sender, names.get(4) + " = " + player.capabilities.allowEdit);
                }
                else
                    throw new CommandException("command.capabilities.capabilityUnknown", args[1]);
                player.sendPlayerAbilities();
            }
        }
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args)
    {
        if (args.length == 1)
        {
            return getListOfStringsMatchingLastWord(args, FMLCommonHandler.instance().getMinecraftServerInstance().getAllUsernames());
        }
        else if (args.length == 2)
        {
            return getListOfStringsMatchingLastWord(args, names);
        }
        else if (args.length == 3)
        {
            return getListOfStringsMatchingLastWord(args, "true", "false");
        }
        else
        {
            return null;
        }
    }

}
