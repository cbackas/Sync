package cback.eventFunctions;

import cback.SyncBot;
import cback.Util;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.member.UserBanEvent;
import sx.blah.discord.handle.impl.events.guild.member.UserJoinEvent;
import sx.blah.discord.handle.impl.events.guild.member.UserLeaveEvent;
import sx.blah.discord.handle.impl.events.guild.member.UserPardonEvent;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.EmbedBuilder;

import java.awt.*;

public class MemberChange {
    private SyncBot bot;

    public MemberChange(SyncBot bot) {
        this.bot = bot;
    }

    @EventSubscriber
    public void memberJoin(UserJoinEvent event) {
        IUser user = event.getUser();

        /**
         * Memberlog
         */
        EmbedBuilder embed = new EmbedBuilder()
                .withFooterIcon(event.getGuild().getIconURL())
                .withFooterText(event.getGuild().getName())
                .withTimestamp(System.currentTimeMillis())
                .withDescription(user.getName() + " **joined** " + event.getGuild().getName() + " " + user.mention());

        Util.sendEmbed(event.getClient().getChannelByID(SyncBot.MEMBERLOG_CH_ID), embed.withColor(Color.GREEN).build());

    }

    @EventSubscriber
    public void memberLeave(UserLeaveEvent event) {
        IUser user = event.getUser();

        /**
         * Memberlog
         */
        EmbedBuilder embed = new EmbedBuilder()
                .withFooterIcon(event.getGuild().getIconURL())
                .withFooterText(event.getGuild().getName())
                .withTimestamp(System.currentTimeMillis())
                .withDescription(user.getName() + " **left** " + event.getGuild().getName() + " " + user.mention());

        Util.sendEmbed(event.getClient().getChannelByID(SyncBot.MEMBERLOG_CH_ID), embed.withColor(Color.ORANGE).build());

    }

    @EventSubscriber
    public void memberBanned(UserBanEvent event) {
        IUser user = event.getUser();

        /**
         * Memberlog
         */
        EmbedBuilder embed = new EmbedBuilder()
                .withFooterIcon(event.getGuild().getIconURL())
                .withFooterText(event.getGuild().getName())
                .withTimestamp(System.currentTimeMillis())
                .withDescription(user.getName() + " was banned from " + event.getGuild().getName() + " " + user.mention());

        Util.sendEmbed(event.getClient().getChannelByID(SyncBot.MEMBERLOG_CH_ID), embed.withColor(Color.RED).build());

        /**
         * Ban Syncing
         */
        if (SyncBot.ALL_SERVERS.contains(event.getGuild().getLongID())) {
            SyncBot.ALL_SERVERS.stream()
                    .filter(g -> !g.equals(event.getGuild().getLongID()))
                    .forEach(g -> {
                        IGuild guild = event.getClient().getGuildByID(g);

                        try {
                            if (!guild.getBannedUsers().contains(user)) {
                                guild.banUser(user, 1);
                            }
                        } catch (Exception e) {
                            Util.reportHome(e);
                            Util.simpleEmbed(event.getClient().getChannelByID(SyncBot.MEMBERLOG_CH_ID), "Ban sync failed for " + guild.getName(), Color.RED);
                        }
                    });
        }
    }

    @EventSubscriber
    public void memberPardoned(UserPardonEvent event) {
        IUser user = event.getUser();

        /**
         * Memberlog
         */
        EmbedBuilder bld = new EmbedBuilder()
                .withDesc(Util.getTag(user) + " was **unbanned** from the server. " + user.mention())
                .withTimestamp(System.currentTimeMillis())
                .withColor(Color.GREEN);

        Util.sendEmbed(event.getClient().getChannelByID(SyncBot.MEMBERLOG_CH_ID), bld.build());

        /**
         * Pardon Syncing
         */
        if (SyncBot.ALL_SERVERS.contains(event.getGuild().getLongID())) {
            SyncBot.ALL_SERVERS.stream()
                    .filter(g -> !g.equals(event.getGuild().getLongID()))
                    .forEach(g -> {
                        IGuild guild = event.getClient().getGuildByID(g);

                        try {
                            if (guild.getBannedUsers().contains(user)) {
                                guild.pardonUser(user.getLongID());
                            }
                        } catch (Exception e) {
                            Util.reportHome(e);
                            Util.simpleEmbed(event.getClient().getChannelByID(SyncBot.MEMBERLOG_CH_ID), "Pardon sync failed for " + guild.getName(), Color.RED);
                        }
                    });
        }
    }


}
