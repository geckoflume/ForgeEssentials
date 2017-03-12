package com.forgeessentials.commands.player;

import java.util.List;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.permission.PermissionLevel;
import net.minecraftforge.permission.PermissionManager;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.UserIdent;
import com.forgeessentials.commands.ModuleCommands;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import com.forgeessentials.core.misc.TranslatedCommandException;
import com.forgeessentials.util.output.ChatOutputHandler;

import cpw.mods.fml.common.FMLCommonHandler;

public class CommandBurn extends ForgeEssentialsCommandBase
{
    @Override
    public String getCommandName()
    {
        return "feburn";
    }

    @Override
    public String[] getDefaultAliases()
    {
        return new String[] { "burn" };
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {
        if (sender instanceof EntityPlayer)
        {
            return "/burn <me|joueur> Met un joueur en feu.";
        }
        else
        {
            return "/burn <joueur> Met un joueur en feu.";
        }
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
        return ModuleCommands.PERM + ".burn";
    }

    @Override
    public void processCommandPlayer(EntityPlayerMP sender, String[] args)
    {
        if (args.length == 1)
        {
            if (args[0].toLowerCase().equals("me"))
            {
                sender.setFire(15);
                ChatOutputHandler.chatError(sender, "A\u00EFe ! C'est chaud !");
            }
            else if (PermissionManager.checkPermission(sender, getPermissionNode() + ".others"))
            {
                EntityPlayerMP player = UserIdent.getPlayerByMatchOrUsername(sender, args[0]);
                if (player != null)
                {
                    ChatOutputHandler.chatConfirmation(sender, "Vous devriez vous sentir coupable d'avoir fait ça...");
                    player.setFire(15);
                }
                else
                    throw new TranslatedCommandException("Le joueur %s n'existe pas ou n'est pas en ligne.", args[0]);
            }
        }
        else if (args.length == 2)
        {
            if (args[0].toLowerCase().equals("me"))
            {
                sender.setFire(parseInt(sender, args[1]));
                ChatOutputHandler.chatError(sender, "A\u00EFe ! C'est chaud !");
            }
            else if (PermissionManager.checkPermission(sender, getPermissionNode() + ".others"))
            {
                EntityPlayerMP player = UserIdent.getPlayerByMatchOrUsername(sender, args[0]);
                if (player != null)
                {
                    player.setFire(parseIntWithMin(sender, args[1], 0));
                    ChatOutputHandler.chatConfirmation(sender, "Vous devriez vous sentir coupable d'avoir fait ça...");
                }
                else
                    throw new TranslatedCommandException("Le joueur %s n'existe pas ou n'est pas en ligne.", args[0]);
            }
        }
        else
        {
            throw new TranslatedCommandException(getCommandUsage(sender));
        }
    }

    @Override
    public void processCommandConsole(ICommandSender sender, String[] args)
    {
        int time = 15;
        if (args.length == 2)
        {
            time = parseIntWithMin(sender, args[1], 0);
        }
        EntityPlayerMP player = UserIdent.getPlayerByMatchOrUsername(sender, args[0]);
        if (player != null)
        {
            player.setFire(time);
            ChatOutputHandler.chatConfirmation(sender, "Vous devriez vous sentir coupable d'avoir fait ça...");
        }
        else
            throw new CommandException("Le joueur %s n'existe pas ou n'est pas en ligne.", args[0]);
    }

    @Override
    public void registerExtraPermissions()
    {
        APIRegistry.perms.registerPermission(getPermissionNode() + ".others", PermissionLevel.OP);
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args)
    {
        if (args.length == 1)
        {
            return getListOfStringsMatchingLastWord(args, FMLCommonHandler.instance().getMinecraftServerInstance().getAllUsernames());
        }
        else
        {
            return null;
        }
    }
}
