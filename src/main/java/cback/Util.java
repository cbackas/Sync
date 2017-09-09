package cback;

import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.internal.json.objects.EmbedObject;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.EmbedBuilder;
import sx.blah.discord.util.MessageBuilder;
import sx.blah.discord.util.RequestBuffer;

import java.awt.*;

public class Util {
    static IDiscordClient client = SyncBot.getInstance().getClient();

    /**
     * Sends a message to the provided channel
     */
    public static void sendMessage(IChannel channel, String message) {
        try {
            channel.sendMessage(message);
        } catch (Exception e) {
            reportHome(e);
        }
    }

    /**
     * Sends an embed to the provided channel
     */
    public static IMessage sendEmbed(IChannel channel, EmbedObject embedObject) {
        RequestBuffer.RequestFuture<IMessage> future = RequestBuffer.request(() -> {
            try {
                return new MessageBuilder(SyncBot.getInstance().getClient()).withEmbed(embedObject)
                        .withChannel(channel).send();
            } catch (Exception e) {
                reportHome(e);
            }
            return null;
        });
        return future.get();
    }

    /**
     * Send simple fast embeds
     */
    public static void simpleEmbed(IChannel channel, String message) {
        sendEmbed(channel, new EmbedBuilder().withDescription(message).withColor(SyncBot.BOT_COLOR).build());
    }

    public static void simpleEmbed(IChannel channel, String message, Color color) {
        sendEmbed(channel, new EmbedBuilder().withDescription(message).withColor(color).build());
    }

    /**
     * Returns clean little tag to use to talk about users in a very accurate and descriptive manor
     */
    public static String getTag(IUser user) {
        return user.getName() + '#' + user.getDiscriminator();
    }

    /**
     * Send report
     */
    public static void reportHome(IMessage message, Exception e) {
        e.printStackTrace();

        EmbedBuilder bld = new EmbedBuilder()
                .withColor(SyncBot.BOT_COLOR)
                .withTimestamp(System.currentTimeMillis())
                .withAuthorName(getTag(message.getAuthor()))
                .withAuthorIcon(message.getAuthor().getAvatarURL())
                .withDesc(message.getContent())
                .appendField("\u200B", "\u200B", false)

                .appendField("Exeption:", e.toString(), false);

        StringBuilder stack = new StringBuilder();
        for (StackTraceElement s : e.getStackTrace()) {
            stack.append(s.toString());
            stack.append("\n");
        }

        String stackString = stack.toString();
        if (stackString.length() > 800) {
            stackString = stackString.substring(0, 800);
        }

        bld
                .appendField("Stack:", stackString, false);

        sendEmbed(client.getChannelByID(SyncBot.ERROR_CH_ID), bld.build());
    }

    public static void reportHome(Exception e) {
        e.printStackTrace();

        EmbedBuilder bld = new EmbedBuilder()
                .withColor(SyncBot.BOT_COLOR)
                .withTimestamp(System.currentTimeMillis())
                .appendField("Exeption:", e.toString(), false);

        StringBuilder stack = new StringBuilder();
        for (StackTraceElement s : e.getStackTrace()) {
            stack.append(s.toString());
            stack.append("\n");
        }

        String stackString = stack.toString();
        if (stackString.length() > 800) {
            stackString = stackString.substring(0, 800);
        }

        bld
                .appendField("Stack:", stackString, false);

        sendEmbed(client.getChannelByID(SyncBot.ERROR_CH_ID), bld.build());
    }

    /**
     * Send botLog
     */
    public static void botLog(IMessage message) {
        try {
            EmbedBuilder bld = new EmbedBuilder()
                    .withColor(SyncBot.BOT_COLOR)
                    .withAuthorName(message.getAuthor().getName() + '#' + message.getAuthor().getDiscriminator())
                    .withAuthorIcon(message.getAuthor().getAvatarURL())
                    .withDesc(message.getFormattedContent())
                    .withFooterText(message.getGuild().getName() + "/#" + message.getChannel().getName());

            sendEmbed(message.getClient().getChannelByID(SyncBot.BOTLOG_CH_ID), bld.build());
        } catch (Exception e) {
            reportHome(message, e);
        }
    }
}
