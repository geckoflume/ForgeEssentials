package com.forgeessentials.commands.item;

import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraftforge.permission.PermissionLevel;
import net.minecraftforge.permission.PermissionManager;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.UserIdent;
import com.forgeessentials.commands.ModuleCommands;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import com.forgeessentials.core.misc.TranslatedCommandException;

import cpw.mods.fml.common.FMLCommonHandler;

public class CommandRepair extends ForgeEssentialsCommandBase
{

    @Override
    public String getCommandName()
    {
        return "ferepair";
    }

    @Override
    public String[] getDefaultAliases()
    {
        return new String[] { "repair" };
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {
        if (sender instanceof EntityPlayer)
        {
            return "/repair [joueur] R\u00E9pare l'item que vous ou un autre joueur tenez";
        }
        else
        {
            return "/repair <joueur> R\u00E9pare l'item que le joueur tient";
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
        return ModuleCommands.PERM + ".repair";
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
            ItemStack item = sender.getHeldItem();
            if (item == null)
                throw new TranslatedCommandException("Vous ne tenez pas un item r\u00E9parable.");
            item.setItemDamage(0);
        }
        else if (args.length == 1 && PermissionManager.checkPermission(sender, getPermissionNode() + ".others"))
        {
            EntityPlayerMP player = UserIdent.getPlayerByMatchOrUsername(sender, args[0]);
            if (player == null)
                throw new TranslatedCommandException("Le joueur %s n'existe pas ou n'est pas en ligne.", args[0]);

            ItemStack item = player.getHeldItem();
            if (item != null)
                item.setItemDamage(0);
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
            // PlayerSelector.matchPlayers(sender, args[0])
            EntityPlayerMP player = UserIdent.getPlayerByMatchOrUsername(sender, args[0]);
            if (player != null)
            {

                ItemStack item = player.getHeldItem();

                if (item != null)
                {
                    item.setItemDamage(0);
                }

            }
            else
                throw new TranslatedCommandException("Le joueur %s n'existe pas ou n'est pas en ligne.", args[0]);
        }
        else
            throw new TranslatedCommandException(getCommandUsage(sender));
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
