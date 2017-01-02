package cback.eventFunctions;

import cback.SyncBot;
import cback.Util;
import cback.globalChannels;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.UserBanEvent;
import sx.blah.discord.handle.impl.events.UserJoinEvent;
import sx.blah.discord.handle.impl.events.UserLeaveEvent;
import sx.blah.discord.handle.obj.IUser;

import java.util.Arrays;
import java.util.List;

public class MemberChange {
    private SyncBot bot;

    public MemberChange(SyncBot bot) {
        this.bot = bot;
    }

    public static final List<String> ALL_SERVERS = Arrays.asList("192441520178200577", "256248900124540929", "191589587817070593");

    @EventSubscriber
    public void memberJoin(UserJoinEvent event) {
        IUser user = event.getUser();

        //Memberlog message
        Util.sendMessage(bot.getClient().getChannelByID(SyncBot.MEMBERLOG_CHANNEL_ID), "\uD83D\uDCE5  " + user.getName() + " **joined** " + event.getGuild().getName() + " " + user.mention());

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
        Util.sendMessage(bot.getClient().getChannelByID(SyncBot.MEMBERLOG_CHANNEL_ID), "\uD83D\uDD28  " + user.getName() + " was **banned** from " + event.getGuild().getName() + " " + user.mention());

        if (ALL_SERVERS.contains(event.getGuild().getID())) {

            ALL_SERVERS.stream()
                    .filter(g -> !g.equals(event.getGuild().getID()))
                    .forEach(g -> {

                        try {
                            event.getClient().getGuildByID(g).banUser(user, 1);

                            Util.sendMessage(bot.getClient().getChannelByID(SyncBot.MEMBERLOG_CHANNEL_ID), "**Ban Successfully Synced**");
                        } catch (Exception e) {
                            e.printStackTrace();

                            Util.sendMessage(bot.getClient().getChannelByID(SyncBot.MEMBERLOG_CHANNEL_ID), "**Ban Sync Failed**");
                        }

                    });
        }
    }

}
