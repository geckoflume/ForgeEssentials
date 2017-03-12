package com.forgeessentials.commands.item;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.network.play.server.S2DPacketOpenWindow;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.permission.PermissionLevel;

import com.forgeessentials.commands.ModuleCommands;
import com.forgeessentials.commands.util.VirtualChest;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import com.forgeessentials.core.misc.FECommandManager.ConfigurableCommand;

/**
 * Opens a configurable virtual chest
 *
 * @author Dries007
 */
public class CommandVirtualchest extends ForgeEssentialsCommandBase implements ConfigurableCommand
{
    public static int size = 54;
    public static String name = "Vault 13";

    @Override
    public String getCommandName()
    {
        return "virtualchest";
    }

    @Override
    public String[] getDefaultAliases()
    {
        return new String[] { "vchest" };
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {
        return "/vchest Ouvre un coffre virtuel";
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
        return ModuleCommands.PERM + ".virtualchest";
    }

    @Override
    public void processCommandPlayer(EntityPlayerMP sender, String[] args)
    {
        EntityPlayerMP player = sender;
        if (player.openContainer != player.inventoryContainer)
        {
            player.closeScreen();
        }
        player.getNextWindowId();

        VirtualChest chest = new VirtualChest(player);
        player.playerNetServerHandler.sendPacket(new S2DPacketOpenWindow(player.currentWindowId, 0, name, size, true));
        player.openContainer = new ContainerChest(player.inventory, chest);
        player.openContainer.windowId = player.currentWindowId;
        player.openContainer.addCraftingToCrafters(player);
    }

    @Override
    public void loadConfig(Configuration config, String category)
    {
        size = config.get(category, "VirtualChestRows", 6, "1 row = 9 slots. 3 = 1 chest, 6 = double chest (max size!).").getInt(6) * 9;
        name = config.get(category, "VirtualChestName", "Vault 13", "Don't use special stuff....").getString();
    }

    @Override
    public void loadData()
    {
        /* do nothing */
    }

}
