package com.forgeessentials.commands.item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.command.ICommandSender;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraftforge.permission.PermissionLevel;

import org.apache.commons.lang3.StringUtils;

import com.forgeessentials.commands.ModuleCommands;
import com.forgeessentials.core.commands.ParserCommandBase;
import com.forgeessentials.core.misc.TranslatedCommandException;
import com.forgeessentials.util.CommandParserArgs;

public class CommandDechant extends ParserCommandBase
{
    private static final String PERM = ModuleCommands.PERM + ".dechant";

    @Override
    public String getCommandName()
    {
        return "fedechant";
    }

    @Override
    public String[] getDefaultAliases()
    {
        return new String[] { "dechant" };
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {
        return "/dechant <nom>: Enl\u00E8ve un enchantement de l'item tenu";
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
        return PERM;
    }

    @Override
    public void parse(CommandParserArgs arguments)
    {
        ItemStack stack = arguments.senderPlayer.getCurrentEquippedItem();
        if (stack == null)
            throw new TranslatedCommandException("Vous ne tenez pas un item valide");
        @SuppressWarnings("unchecked")
        Map<Integer, Integer> enchantments = EnchantmentHelper.getEnchantments(stack);

        List<String> validEnchantmentNames = new ArrayList<>();
        Map<String, Enchantment> validEnchantments = new HashMap<>();
        for (Enchantment enchantment : Enchantment.enchantmentsList)
            if (enchantment != null && enchantments.containsKey(enchantment.effectId))
            {
                String name = StatCollector.translateToLocal(enchantment.getName()).replaceAll(" ", "");
                validEnchantmentNames.add(name);
                validEnchantments.put(name.toLowerCase(), enchantment);
            }

        if (arguments.isEmpty())
        {
            if (arguments.isTabCompletion)
                return;
            arguments.confirm("D\u00E9senchantements possibles : %s", StringUtils.join(validEnchantmentNames, ", "));
            return;
        }

        while (!arguments.isEmpty())
        {
            arguments.tabComplete(validEnchantmentNames);
            String name = arguments.remove();
            Enchantment enchantment = validEnchantments.get(name.toLowerCase());
            if (enchantment == null)
                throw new TranslatedCommandException("Nom d'enchantement %s invalide !", name);
            enchantments.remove(enchantment.effectId);
        }
        EnchantmentHelper.setEnchantments(enchantments, stack);
    }

}
