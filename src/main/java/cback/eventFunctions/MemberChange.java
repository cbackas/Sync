package cback.eventFunctions;

import cback.SyncBot;
import cback.Util;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.UserBanEvent;
import sx.blah.discord.handle.impl.events.UserJoinEvent;
import sx.blah.discord.handle.impl.events.UserLeaveEvent;
import sx.blah.discord.handle.obj.IUser;

public class MemberChange {
    private SyncBot bot;

    public MemberChange(SyncBot bot) {
        this.bot = bot;
    }

    @EventSubscriber
    public void memberJoin(UserJoinEvent event) {
        IUser user = event.getUser();

        //Memberlog message
        Util.sendMessage(bot.getClient().getChannelByID(SyncBot.MEMBERLOG_CHANNEL_ID), "\uD83D\uDCE5  " + user.getName() + " **joined** " + event.getGuild().getName() + user.mention());

    }

    @EventSubscriber
    public void memberLeave(UserLeaveEvent event) {
        IUser user = event.getUser();

        //Memberlog message
        Util.sendMessage(bot.getClient().getChannelByID(SyncBot.MEMBERLOG_CHANNEL_ID), "\uD83D\uDCE4  " + user.getName() + " **left** " + event.getGuild().getName() + " " + user.mention());

    }

    @EventSubscriber
    public void memberBanned(UserBanEvent event) {
        IUser user = event.getUser();

        //Memberlog message
        Util.sendMessage(bot.getClient().getChannelByID(SyncBot.MEMBERLOG_CHANNEL_ID), "\uD83D\uDD28  " + user.getName() + " was **banned**. " + user.mention());

    }

}
