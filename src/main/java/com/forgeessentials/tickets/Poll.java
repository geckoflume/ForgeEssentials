package com.forgeessentials.tickets;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import net.minecraft.event.ClickEvent;
import net.minecraft.event.ClickEvent.Action;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.IChatComponent;

/**
 * Only one poll can currently be run on the server at a time.
 */
public class Poll
{
    public static Poll currentPoll;

    public String question;

    public List<PollOption> options = new ArrayList<>();

    public static class PollOption
    {
        public String option;

        public int votes;

        PollOption(String option)
        {
            this.option = option;
            this.votes = 0;
        }
    }

    protected Poll(String question, List options)
    {
        this.options = options;
        this.question = question;

    }

    public IChatComponent getChatComponentForOption(int i)
    {
        IChatComponent returned = new ChatComponentText(i + ". " + options.get(i).option);
        returned.setChatStyle(new ChatStyle().setUnderlined(true).setBold(true).setChatClickEvent(
                new ClickEvent(Action.RUN_COMMAND, "poll " + i)));
        return returned;
    }

    public static Poll getCurrentPoll()
    {
        return currentPoll;
    }

}
