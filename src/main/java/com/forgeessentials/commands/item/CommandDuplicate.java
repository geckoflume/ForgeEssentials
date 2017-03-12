package com.forgeessentials.commands.item;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraftforge.permission.PermissionLevel;

import com.forgeessentials.commands.ModuleCommands;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import com.forgeessentials.core.misc.TranslatedCommandException;
import com.forgeessentials.util.PlayerUtil;

public class CommandDuplicate extends ForgeEssentialsCommandBase
{

    @Override
    public String getCommandName()
    {
        return "feduplicate";
    }

    @Override
    public String[] getDefaultAliases()
    {
        return new String[] { "duplicate" };
    }

    @Override
    public String getCommandUsage(ICommandSender par1ICommandSender)
    {
        return "/duplicate [quantit\u00E9]: Duplique l'item actuel";
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
        return ModuleCommands.PERM + "." + getCommandName();
    }

    @Override
    public void processCommandPlayer(EntityPlayerMP player, String[] args)
    {
        ItemStack stack = player.getCurrentEquippedItem();
        if (stack == null)
            throw new TranslatedCommandException("Aucun item tenu");

        int stackSize = 0;
        if (args.length > 0)
            stackSize = parseInt(player, args[0]);

        ItemStack newStack = stack.copy();
        if (stackSize > 0)
            newStack.stackSize = stackSize;

        PlayerUtil.give(player, newStack);
    }

}
