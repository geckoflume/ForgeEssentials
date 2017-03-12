package com.forgeessentials.commands.item;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.Action;
import net.minecraftforge.permission.PermissionLevel;

import com.forgeessentials.commands.ModuleCommands;
import com.forgeessentials.core.commands.ParserCommandBase;
import com.forgeessentials.core.misc.TranslatedCommandException;
import com.forgeessentials.util.CommandParserArgs;
import com.forgeessentials.util.ItemUtil;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class CommandBind extends ParserCommandBase
{

    private static final String TAG_NAME = "FEbinding";

    public static final String LORE_TEXT_TAG = EnumChatFormatting.RESET.toString() + EnumChatFormatting.AQUA;

    public CommandBind()
    {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public String getCommandName()
    {
        return "febind";
    }

    @Override
    public String[] getDefaultAliases()
    {
        return new String[] { "bind" };
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {
        return "/bind <left|right>: Lie une commande \u00E0 un item";
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
        return ModuleCommands.PERM + ".bind";
    }

    @Override
    @SuppressWarnings("unchecked")
    public void parse(CommandParserArgs arguments)
    {
        if (arguments.isEmpty())
        {
            arguments.confirm("/bind <left|right> <commande...>: Lie une commande \u00E0 un item");
            return;
        }

        arguments.tabComplete("left", "right", "clear");
        String side = arguments.remove().toLowerCase();

        // If sub-command is "clear"
        if (side.equals("clear"))
        {
            if (!arguments.isTabCompletion)
            {
                ItemStack is = arguments.senderPlayer.inventory.getCurrentItem();
                if (is == null)
                    throw new TranslatedCommandException("Vous ne tenez pas un item valide.");
                NBTTagCompound tag = is.getTagCompound();
                if (tag != null)
                    tag.removeTag(TAG_NAME);
                arguments.confirm("Commandes r\u00E9initialis\u00E9es pour cet item");
            }
            return;
        }

        // Get correct side
        boolean isLeft = side.equals("left");
        if (!isLeft && !side.equals("right"))
            throw new TranslatedCommandException("Vous devez choisir left ou right");

        if (arguments.isEmpty())
        {
            arguments.confirm("/bind " + side + " <commande...>: Lie une commande \u00E0 un item");
            arguments.confirm("/bind " + side + " none : R\u00E9initialise la commande li\u00E9e");
            return;
        }

        ItemStack is = arguments.senderPlayer.inventory.getCurrentItem();
        if (is == null)
            throw new TranslatedCommandException("Vous ne tenez pas un item valide.");
        NBTTagCompound tag = ItemUtil.getTagCompound(is);
        NBTTagCompound bindTag = ItemUtil.getCompoundTag(tag, TAG_NAME);
        NBTTagCompound display = tag.getCompoundTag("display");

        if (arguments.isTabCompletion)
        {
            arguments.tabCompletion = MinecraftServer.getServer().getPossibleCompletions(arguments.sender,
                    arguments.toString().startsWith("/") ? arguments.toString() : "/" + arguments.toString());
            if ("none".startsWith(arguments.peek()))
                arguments.tabCompletion.add(0, "none");
            return;
        }
        if (arguments.peek().equals("none"))
        {
            bindTag.removeTag(side);
            display.setTag("Lore", new NBTTagList());
            arguments.confirm("La commande " + side + " li\u00E9e \u00E0 l'item a \u00E9t\u00E9 r\u00E9initialis\u00E9e");
        }
        else
        {
            String command = arguments.toString();
            bindTag.setString(side, command);

            String loreStart = LORE_TEXT_TAG + side + "> ";
            NBTTagString loreTag = new NBTTagString(loreStart + command);

            NBTTagList lore = display.getTagList("Lore", 9);
            for (int i = 0; i < lore.tagCount(); ++i)
            {
                if (lore.getStringTagAt(i).startsWith(loreStart))
                {
                    lore.func_150304_a(i, loreTag);
                    arguments.confirm("Commande li\u00E9e \u00E0 l'item");
                    return;
                }
            }
            lore.appendTag(loreTag);
            display.setTag("Lore", lore);
            tag.setTag("display", display);
        }
        arguments.confirm("Commande li\u00E9e \u00E0 l'item");
    }

    @SubscribeEvent
    public void playerInteractEvent(PlayerInteractEvent event)
    {
        if (!(event.entityPlayer instanceof EntityPlayerMP))
            return;
        ItemStack stack = event.entityPlayer.getCurrentEquippedItem();
        if (stack == null || stack.getTagCompound() == null || !stack.getTagCompound().hasKey(TAG_NAME))
            return;
        NBTTagCompound nbt = stack.getTagCompound().getCompoundTag(TAG_NAME);

        String command = event.action == Action.LEFT_CLICK_BLOCK ? nbt.getString("left") : nbt.getString("right");
        if (command == null || command.isEmpty())
            return;

        MinecraftServer.getServer().getCommandManager().executeCommand(event.entityPlayer, command);
        event.setCanceled(true);
    }

}