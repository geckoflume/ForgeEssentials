package com.forgeessentials.chat.command;

import net.minecraft.command.ICommandSender;
import net.minecraftforge.permission.PermissionLevel;

import com.forgeessentials.api.UserIdent;
import com.forgeessentials.api.permissions.FEPermissions;
import com.forgeessentials.chat.Mailer;
import com.forgeessentials.chat.Mailer.Mail;
import com.forgeessentials.chat.Mailer.Mails;
import com.forgeessentials.core.FEConfig;
import com.forgeessentials.core.commands.ParserCommandBase;
import com.forgeessentials.core.misc.TranslatedCommandException;
import com.forgeessentials.core.misc.Translator;
import com.forgeessentials.util.CommandParserArgs;
import com.forgeessentials.util.output.ChatOutputHandler;

public class CommandMail extends ParserCommandBase
{

    @Override
    public String getCommandName()
    {
        return "mail";
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {
        return "/mail send|read: Envoyer ou recevoir des courriers";
    }

    @Override
    public String getPermissionNode()
    {
        return "fe.chat.mail";
    }

    @Override
    public PermissionLevel getPermissionLevel()
    {
        return PermissionLevel.TRUE;
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
            arguments.confirm("/mail read: Lire le prochain courrier");
            arguments.confirm("/mail readall: Lire tous les courriers");
            arguments.confirm("/mail send <joueur> <msg...>: Envoyer un courrier");
            return;
        }

        arguments.tabComplete("read", "readall", "send");
        String subArg = arguments.remove().toLowerCase();
        switch (subArg)
        {
        case "read":
        {
            if (arguments.senderPlayer == null)
                throw new TranslatedCommandException(FEPermissions.MSG_NO_CONSOLE_COMMAND);
            if (arguments.isTabCompletion)
                return;
            Mails mailBag = Mailer.getMailBag(arguments.ident);
            if (mailBag.mails.isEmpty())
                throw new TranslatedCommandException("Vous n'avez pas de courrier");
            readMail(arguments.sender, mailBag.mails.remove(0));
            Mailer.saveMails(arguments.ident, mailBag);
            break;
        }
        case "readall":
        {
            if (arguments.senderPlayer == null)
                throw new TranslatedCommandException(FEPermissions.MSG_NO_CONSOLE_COMMAND);
            if (arguments.isTabCompletion)
                return;
            Mails mailBag = Mailer.getMailBag(arguments.ident);
            if (mailBag.mails.isEmpty())
                throw new TranslatedCommandException("Vous n'avez pas de courrier");
            for (Mail mail : mailBag.mails)
                readMail(arguments.sender, mail);
            mailBag.mails.clear();
            Mailer.saveMails(arguments.ident, mailBag);
            break;
        }
        case "send":
        {
            UserIdent receiver = arguments.parsePlayer(false, false);
            if (arguments.isTabCompletion)
                return;
            if (arguments.isEmpty())
                throw new TranslatedCommandException("Pas de message sp\u00E9cifi\u00E9");
            Mailer.sendMail(arguments.ident, receiver, arguments.toString());
            arguments.confirm("Vous avez envoy\u00E9 un courrier \u00E0 %s", receiver.getUsernameOrUuid());
            break;
        }
        default:
            throw new TranslatedCommandException(FEPermissions.MSG_UNKNOWN_SUBCOMMAND, subArg);
        }
    }

    public static void readMail(ICommandSender sender, Mail mail)
    {
        ChatOutputHandler.chatNotification(sender,
                Translator.format("Courrier de %s le %s", mail.sender.getUsernameOrUuid(), FEConfig.FORMAT_DATE_TIME.format(mail.timestamp)));
        ChatOutputHandler.chatConfirmation(sender, ChatOutputHandler.formatColors(mail.message));
    }

}
