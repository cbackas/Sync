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
                .withColor(Color.CYAN)
                .withDescription(Util.getTag(user) + " **joined** " + event.getGuild().getName() + " " + user.mention());

        Util.sendEmbed(event.getClient().getChannelByID(SyncBot.MEMBERLOG_CH_ID), embed.build());

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
                .withColor(Color.YELLOW)
                .withDescription(Util.getTag(user) + " **left** " + event.getGuild().getName() + " " + user.mention());

        Util.sendEmbed(event.getClient().getChannelByID(SyncBot.MEMBERLOG_CH_ID), embed.build());

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
                .withColor(Color.RED)
                .withDescription(Util.getTag(user) + " was banned from " + event.getGuild().getName() + " " + user.mention());

        Util.sendEmbed(event.getClient().getChannelByID(SyncBot.MEMBERLOG_CH_ID), embed.build());

        /**
         * Ban Syncing
         */
        if (SyncBot.ALL_SERVERS.contains(event.getGuild().getStringID())) {
            SyncBot.ALL_SERVERS.stream()
                    .filter(g -> !g.equals(event.getGuild().getLongID()))
                    .forEach(g -> {
                        IGuild guild = event.getClient().getGuildByID(Long.parseLong(g));

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
                .withFooterIcon(event.getGuild().getIconURL())
                .withFooterText(event.getGuild().getName())
                .withTimestamp(System.currentTimeMillis())
                .withColor(Color.GREEN)
                .withDesc(Util.getTag(user) + " was **unbanned** from " + event.getGuild().getName() + user.mention());

        Util.sendEmbed(event.getClient().getChannelByID(SyncBot.MEMBERLOG_CH_ID), bld.build());

        /**
         * Pardon Syncing
         */
        if (SyncBot.ALL_SERVERS.contains(event.getGuild().getStringID())) {
            SyncBot.ALL_SERVERS.stream()
                    .filter(g -> !g.equals(event.getGuild().getStringID()))
                    .forEach(g -> {
                        IGuild guild = event.getClient().getGuildByID(Long.parseLong(g));

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
