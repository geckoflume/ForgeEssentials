package com.forgeessentials.tickets;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.minecraft.command.ICommandSender;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.permission.PermissionLevel;

import com.forgeessentials.api.permissions.FEPermissions;
import com.forgeessentials.core.commands.ParserCommandBase;
import com.forgeessentials.data.v2.DataManager;
import com.forgeessentials.tickets.Poll.PollOption;
import com.forgeessentials.util.CommandParserArgs;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.forgeessentials.util.questioner.QuestionData;
import com.forgeessentials.util.questioner.Questioner;
import com.forgeessentials.util.questioner.QuestionerCallback;

import cpw.mods.fml.common.FMLCommonHandler;

public class CommandPoll extends ParserCommandBase
{
    @Override
    public String getCommandName()
    {
        return "poll";
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {
        return "/poll [create|stop|view|<option>]";
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return false;
    }

    @Override
    public String getPermissionNode()
    {
        return ModuleTickets.PERMBASE + ".poll";
    }

    @Override
    public PermissionLevel getPermissionLevel()
    {
        return PermissionLevel.TRUE;
    }

    @Override
    public void parse(CommandParserArgs arguments)
    {
        if (arguments.isEmpty())
        {
            parseView(arguments);
        }

        switch(arguments.peek())
        {
        case "create":
            parseCreate(arguments);
            arguments.remove();
            break;
        case "stop":
            parseStop(arguments);
            arguments.remove();
            break;
        case "view":
            parseView(arguments);
            arguments.remove();
            break;
        case "help":
            arguments.confirm("You'll need a book to create a poll.");
            arguments.confirm("It has to be signed, and the title of the book must be 'FEPoll'");
            arguments.confirm("The first page is the question you want to ask.");
            arguments.confirm("You can also specify a ");
        default:
            parseVote(arguments);
            break;
        }
    }

    private void parseCreate(CommandParserArgs args)
    {
        if (!args.hasPermission(getPermissionNode() + ".admin"))
        {
            args.error(FEPermissions.MSG_NO_COMMAND_PERM);
        }
        if (args.senderPlayer.getCurrentEquippedItem().getItem() != Items.written_book)
        {
            args.error("You need a book for this! Here's one to get you started.");
            args.error("Try /poll help for more info.");
            args.senderPlayer.inventory.addItemStackToInventory(new ItemStack(Items.writable_book));
            return;
        }

        NBTTagCompound tag = args.senderPlayer.getCurrentEquippedItem().getTagCompound();
        String title = tag.getString("title");
        if (!title.equals("FEPoll"))
        {
            args.error("Invalid book. The title of the book should be 'FEPoll'.");
        }
        NBTTagList list = tag.getTagList("pages", 9);
        String question = list.getStringTagAt(0);
        args.confirm("Valid book for poll creation found, reading 1st page as question.");
        args.confirm("Question: " + question);
        args.confirm("Possible choices: ");
        System.out.println(list.tagCount());

        List<PollOption> options = new ArrayList<>();
        for (int i = 1; i == list.tagCount(); i++)
        {
            PollOption option = new PollOption(list.getStringTagAt(i));
            options.add(option);
            args.confirm(options.indexOf(option) + ". " + list.getStringTagAt(i));
        }
        QuestionData toConfirm = new QuestionData(args.sender, "Confirm adding this poll?", new QuestionerCallback()
    {
        @Override
        public void respond(Boolean response)
        {
            if (response = true)
            {
                Poll.currentPoll = new Poll(question, options);
                args.confirm("Successfully added poll!");
            }
        }
    }, 5000, FMLCommonHandler.instance().getMinecraftServerInstance());
    }

    private void parseView(CommandParserArgs args)
    {
        if (Poll.getCurrentPoll() == null)
        {
            args.error("There is no poll being conducted now!");
        }
        else
        {
            args.confirm(Poll.getCurrentPoll().question);
            Iterator<PollOption> ite = Poll.getCurrentPoll().options.iterator();

            while (ite.hasNext())
            {
                PollOption option = ite.next();
                args.sendMessage(Poll.getCurrentPoll().getChatComponentForOption(Poll.currentPoll.options.lastIndexOf(option)));
            }
        }

    }

    private void parseStop(CommandParserArgs args)
    {
        if (!args.hasPermission(getPermissionNode() + ".admin"))
        {
            args.error(FEPermissions.MSG_NO_COMMAND_PERM);
        }
        Poll poll = Poll.currentPoll;
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
        DataManager.getInstance().save(poll, dateFormat.format(new Date()));
        ChatOutputHandler.broadcast("The poll has closed.");
        ChatOutputHandler.broadcast("Question asked: " + poll.question);
        ChatOutputHandler.broadcast("Results:");

        Iterator<PollOption> ite = poll.options.iterator();

        while (ite.hasNext())
        {
            PollOption option = ite.next();
            ChatOutputHandler.broadcast(option.option + " - " + option.votes + " votes");
        }
        Poll.currentPoll = null;
    }

    private void parseVote(CommandParserArgs args)
    {
        Poll.getCurrentPoll().options.get(parseInt(args.sender, args.remove())).votes++;
    }
}
