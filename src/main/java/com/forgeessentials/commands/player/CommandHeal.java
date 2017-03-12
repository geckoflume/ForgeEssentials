package com.forgeessentials.commands.player;

import java.util.List;

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

public class CommandHeal extends ForgeEssentialsCommandBase
{

    @Override
    public String getCommandName()
    {
        return "feheal";
    }

    @Override
    public String[] getDefaultAliases()
    {
        return new String[] { "heal" };
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {
        if (sender instanceof EntityPlayer)
        {
            return "/heal [joueur] Soigne vous ou un autre joueur";
        }
        else
        {
            return "/heal <joueur> Soigne un joueur";
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
        return ModuleCommands.PERM + ".heal";
    }

    @Override
    public void registerExtraPermissions()
    {
        APIRegistry.perms.registerPermission(getPermissionNode() + ".others", PermissionLevel.OP);
    }

    @Override
    public void processCommandPlayer(EntityPlayerMP sender, String[] args)
    {
        if (args.length == 0)
        {
            heal(sender);
        }
        else if (args.length == 1 && PermissionManager.checkPermission(sender, getPermissionNode() + ".others"))
        {
            EntityPlayerMP player = UserIdent.getPlayerByMatchOrUsername(sender, args[0]);
            if (player != null)
            {
                heal(player);
            }
            else
            {
                ChatOutputHandler.chatError(sender, String.format("Le joueur %s n'existe pas ou n'est pas en ligne.", args[0]));
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
        if (args.length == 1)
        {
            EntityPlayerMP player = UserIdent.getPlayerByMatchOrUsername(sender, args[0]);
            if (player != null)
            {
                heal(player);
            }
            else
                throw new TranslatedCommandException("Le joueur %s n'existe pas ou n'est pas en ligne.", args[0]);
        }
        else
            throw new TranslatedCommandException(getCommandUsage(sender));
    }

    public void heal(EntityPlayer target)
    {
        float toHealBy = target.getMaxHealth() - target.getHealth();
        target.heal(toHealBy);
        target.extinguish();
        target.getFoodStats().addStats(20, 1.0F);
        ChatOutputHandler.chatConfirmation(target, "Vous avez \u00E9t\u00E9 soign\u00E9.");
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
