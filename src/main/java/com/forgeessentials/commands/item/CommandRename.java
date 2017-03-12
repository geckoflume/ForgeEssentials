package com.forgeessentials.commands.item;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraftforge.permission.PermissionLevel;

import com.forgeessentials.commands.ModuleCommands;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import com.forgeessentials.core.misc.TranslatedCommandException;

public class CommandRename extends ForgeEssentialsCommandBase
{
    
    @Override
    public String getCommandName()
    {
        return "ferename";
    }

    @Override
    public String[] getDefaultAliases()
    {
        return new String[] { "rename" };
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {
        return "/rename <nouveau nom> Renomme l'item tenu";
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return false;
    }

    @Override
    public PermissionLevel getPermissionLevel()
    {
        return PermissionLevel.OP;
    }

    @Override
    public String getPermissionNode()
    {
        return ModuleCommands.PERM + ".rename";
    }

    @Override
    public void processCommandPlayer(EntityPlayerMP sender, String[] args)
    {
        if (args.length == 0)
            throw new TranslatedCommandException(getCommandUsage(sender));

        ItemStack is = sender.inventory.getCurrentItem();
        if (is == null)
            throw new TranslatedCommandException("Vous ne tenez pas un item valide.");

        StringBuilder sb = new StringBuilder();
        for (String arg : args)
        {
            sb.append(arg + " ");
        }
        is.setStackDisplayName(sb.toString().trim());
    }

}