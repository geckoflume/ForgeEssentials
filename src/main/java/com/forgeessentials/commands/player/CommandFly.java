package com.forgeessentials.commands.player;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.permission.PermissionLevel;

import com.forgeessentials.commands.ModuleCommands;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import com.forgeessentials.util.WorldUtil;
import com.forgeessentials.util.output.ChatOutputHandler;

public class CommandFly extends ForgeEssentialsCommandBase
{
    @Override
    public String getCommandName()
    {
        return "fefly";
    }

    @Override
    public String[] getDefaultAliases()
    {
        return new String[] { "fly" };
    }

    @Override
    public String getCommandUsage(ICommandSender p_71518_1_)
    {
        return "/fly [true|false] Active le fly mode";
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
        return ModuleCommands.PERM + ".fly";
    }

    @Override
    public void processCommandPlayer(EntityPlayerMP player, String[] args)
    {
        if (args.length == 0)
        {
            if (!player.capabilities.allowFlying)
                player.capabilities.allowFlying = true;
            else
                player.capabilities.allowFlying = false;
        }
        else
        {
            player.capabilities.allowFlying = Boolean.parseBoolean(args[0]);
        }
        if (!player.onGround)
            player.capabilities.isFlying = player.capabilities.allowFlying;
        if (!player.capabilities.allowFlying)
            WorldUtil.placeInWorld(player);
        player.sendPlayerAbilities();
        ChatOutputHandler.chatNotification(player, "Flying " + (player.capabilities.allowFlying ? "activ\u00E9" : "d\u00E9sactiv\u00E9"));
    }

}
