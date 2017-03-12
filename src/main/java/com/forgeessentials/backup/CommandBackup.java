package com.forgeessentials.backup;

import net.minecraft.command.ICommandSender;
import net.minecraftforge.permission.PermissionLevel;

import com.forgeessentials.core.commands.ParserCommandBase;
import com.forgeessentials.util.CommandParserArgs;

public class CommandBackup extends ParserCommandBase
{

    @Override
    public String getCommandName()
    {
        return "febackup";
    }

    @Override
    public String[] getDefaultAliases()
    {
        return new String[] { "backup" };
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {
        return "/febackup [dim]: Faire une sauvegarde de la dimension dim";
    }

    @Override
    public String getPermissionNode()
    {
        return "fe.backup.command";
    }

    @Override
    public PermissionLevel getPermissionLevel()
    {
        return PermissionLevel.OP;
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return true;
    }

    @Override
    public void parse(CommandParserArgs arguments)
    {
        if (arguments.isEmpty())
        {
            arguments.confirm("D\u00E9but de la sauvegarde...");
            ModuleBackup.backupAll();
            return;
        }

        int dim = arguments.parseInt();
        if (arguments.isTabCompletion)
            return;

        ModuleBackup.backup(dim);
    }

}
