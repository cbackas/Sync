package cback.eventFunctions;

import cback.MessageUtils;
import cback.SyncBot;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.member.UserBanEvent;
import sx.blah.discord.handle.impl.events.guild.member.UserJoinEvent;
import sx.blah.discord.handle.impl.events.guild.member.UserLeaveEvent;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.EmbedBuilder;

import java.awt.*;
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
        EmbedBuilder embed = new EmbedBuilder()
                .withFooterIcon(event.getGuild().getIconURL())
                .withFooterText(event.getGuild().getName())
                .withTimestamp(System.currentTimeMillis());

        embed.withDescription(user.getName() + " **joined** " + event.getGuild().getName() + " " + user.mention());

        MessageUtils.sendEmbed(bot.getClient().getChannelByID(SyncBot.MEMBERLOG_CHANNEL_ID), embed.withColor(Color.GREEN).build());

    }

    @EventSubscriber
    public void memberLeave(UserLeaveEvent event) {
        IUser user = event.getUser();

        //Memberlog message
        EmbedBuilder embed = new EmbedBuilder()
                .withFooterIcon(event.getGuild().getIconURL())
                .withFooterText(event.getGuild().getName())
                .withTimestamp(System.currentTimeMillis());

        embed.withDescription(user.getName() + " **left** " + event.getGuild().getName() + " " + user.mention());

        MessageUtils.sendEmbed(bot.getClient().getChannelByID(SyncBot.MEMBERLOG_CHANNEL_ID), embed.withColor(Color.ORANGE).build());

    }

    @EventSubscriber
    public void memberBanned(UserBanEvent event) {
        IUser user = event.getUser();

        //Memberlog message
        EmbedBuilder embed = new EmbedBuilder()
                .withFooterIcon(event.getGuild().getIconURL())
                .withFooterText(event.getGuild().getName())
                .withTimestamp(System.currentTimeMillis());

        embed.withDescription(user.getName() + " was banned from " + event.getGuild().getName() + " " + user.mention());

        MessageUtils.sendEmbed(bot.getClient().getChannelByID(SyncBot.MEMBERLOG_CHANNEL_ID), embed.withColor(Color.RED).build());

        if (ALL_SERVERS.contains(event.getGuild().getID())) {

            ALL_SERVERS.stream()
                    .filter(g -> !g.equals(event.getGuild().getID()))
                    .forEach(g -> {
                        IGuild guild = event.getClient().getGuildByID(g);

                        try {

                            if (!guild.getBannedUsers().contains(user)) {
                                guild.banUser(user, 1);
                            }

                            MessageUtils.sendMessage(bot.getClient().getChannelByID(SyncBot.MEMBERLOG_CHANNEL_ID), "**Ban successfully synced to " + guild.getName() + "**");
                        } catch (Exception e) {
                            e.printStackTrace();

                            MessageUtils.sendMessage(bot.getClient().getChannelByID(SyncBot.MEMBERLOG_CHANNEL_ID), "**Ban sync failed for " + guild.getName() + "**");
                        }

                    });
        }
    }


}
