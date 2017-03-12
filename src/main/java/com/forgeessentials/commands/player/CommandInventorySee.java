package com.forgeessentials.commands.player;

import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.network.play.server.S2DPacketOpenWindow;
import net.minecraftforge.permission.PermissionLevel;

import com.forgeessentials.api.UserIdent;
import com.forgeessentials.commands.ModuleCommands;
import com.forgeessentials.commands.util.PlayerInvChest;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import com.forgeessentials.core.misc.TranslatedCommandException;

import cpw.mods.fml.common.FMLCommonHandler;

/**
 * Opens other player inventory.
 */
public class CommandInventorySee extends ForgeEssentialsCommandBase
{

    public CommandInventorySee()
    {
    }

    @Override
    public String getCommandName()
    {
        return "feinvsee";
    }

    @Override
    public String[] getDefaultAliases()
    {
        return new String[] { "invsee" };
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {
        return "/invsee Voir l'inventaire d'un joueur";
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
        return ModuleCommands.PERM + ".invsee";
    }

    @Override
    public void processCommandPlayer(EntityPlayerMP sender, String[] args)
    {
        if (args[0] == null)
            throw new TranslatedCommandException("Vous devez pr\u00E9ciser un joueur !");

        if (!FMLCommonHandler.instance().getEffectiveSide().isServer())
        {
            return;
        }
        EntityPlayerMP player = sender;
        EntityPlayerMP victim = UserIdent.getPlayerByMatchOrUsername(sender, args[0]);
        if (victim == null)
            throw new TranslatedCommandException("Joueur %s introuvable.", args[0]);

        if (player.openContainer != player.inventoryContainer)
        {
            player.closeScreen();
        }
        player.getNextWindowId();

        PlayerInvChest chest = new PlayerInvChest(victim, sender);
        player.playerNetServerHandler.sendPacket(new S2DPacketOpenWindow(player.currentWindowId, 0, chest.getInventoryName(), chest.getSizeInventory(), true));
        player.openContainer = new ContainerChest(player.inventory, chest);
        player.openContainer.windowId = player.currentWindowId;
        player.openContainer.addCraftingToCrafters(player);
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
