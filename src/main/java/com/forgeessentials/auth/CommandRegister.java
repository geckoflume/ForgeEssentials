package com.forgeessentials.auth;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerNotFoundException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.permission.PermissionLevel;
import net.minecraftforge.permission.PermissionManager;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.UserIdent;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import com.forgeessentials.core.commands.PermissionDeniedException;
import com.forgeessentials.core.misc.TranslatedCommandException;
import com.forgeessentials.core.misc.Translator;
import com.forgeessentials.util.events.PlayerAuthLoginEvent;
import com.forgeessentials.util.events.PlayerAuthLoginEvent.Success.Source;
import com.forgeessentials.util.output.ChatOutputHandler;

import cpw.mods.fml.common.FMLCommonHandler;

public class CommandRegister extends ForgeEssentialsCommandBase
{
    @Override
    public String getCommandName()
    {
        return "register";
    }

    @Override
    public void processCommandPlayer(EntityPlayerMP sender, String[] args)
    {
        if (args.length == 0)
        {
            if (!ModuleAuth.isEnabled())
            {
                ChatOutputHandler.chatWarning(sender, "Le service d'authentification a \u00E9t\u00E9 d\u00E9sactiv\u00E9 par l'administrateur.");
                return;
            }

            if (ModuleAuth.isRegistered(sender.getPersistentID()))
            {
                if (ModuleAuth.isAuthenticated(sender))
                {
                    ChatOutputHandler.chatNotification(sender, "Vous \u00EAtes connect\u00E9.");
                }
                else
                {
                    ChatOutputHandler.chatNotification(sender, "Vous \u00EAtes inscrit mais vous n'\u00EAtes pas connect\u00E9.");
                }
            }
            else
            {
                ChatOutputHandler.chatWarning(sender, "Vous n'\u00EAtes pas inscrit.");
            }

            throw new TranslatedCommandException("command.auth.usage");
        }

        boolean hasAdmin = PermissionManager.checkPermission(sender, getPermissionNode() + ".admin");

        // one arg? must be password.
        if (args.length == 1)
        {
            if (ModuleAuth.isRegistered(sender.getPersistentID()))
                throw new TranslatedCommandException("Le joueur %s est d\u00E9j\u00E0 inscrit !", sender.getPersistentID());

            if (ModuleAuth.isEnabled() && !ModuleAuth.allowOfflineRegistration)
                throw new TranslatedCommandException("Les inscriptions ont \u00E9t\u00E9 d\u00E9sactiv\u00E9es.");

            PasswordManager.setPassword(sender.getPersistentID(), args[0]);
            ChatOutputHandler.chatConfirmation(sender, "Inscription effectu\u00E9e !");
            return;
        }
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return false;
    }

    @Override
    public String getPermissionNode()
    {
        return "fe.auth";
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {
        String s = "Syntaxe invalide !";
        if (sender instanceof EntityPlayer)
        {
            s = s + " G\u00E8re votre profil.";
        }
        else
        {
            s = s + " Contr\u00F4le le module d'authentification.";
        }
        return s;
    }

    @Override
    public PermissionLevel getPermissionLevel()
    {
        return PermissionLevel.TRUE;
    }

}
