package cback.eventFunctions;

import cback.SyncBot;
import cback.Util;
import com.vdurmont.emoji.EmojiManager;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.api.internal.json.objects.EmbedObject;
import sx.blah.discord.handle.impl.events.guild.channel.message.reaction.ReactionAddEvent;
import sx.blah.discord.handle.obj.IEmbed;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.EmbedBuilder;
import sx.blah.discord.util.RequestBuffer;

import java.awt.*;

public class ReactionChange {
    private SyncBot bot;

    public ReactionChange(SyncBot bot) {
        this.bot = bot;
    }

    @EventSubscriber
    public void todoMessageReactions(ReactionAddEvent event) {
        if (event.getUser().isBot() || event.getChannel().getLongID() != SyncBot.TODO_CH_ID) {
            return; //ignores bot reactions and reactions not related to the list
        }
        IUser user = event.getUser();
        IMessage message = event.getMessage();
        if (user.getLongID() == SyncBot.CBACK_USR_ID) {
            String emojiName = event.getReaction().getEmoji().getName();
            if (emojiName.equals("✅")) {
                updateCompletedItem(message);
            } else if (emojiName.equals("❌")) {
                message.delete();
            }
        } else {
            resetReactions(event.getMessage());
        }
    }

    /**
     * Builds embeds so that editing can be done and stuff
     */
    public static EmbedObject buildNewItem(String ideaName, String ideaDesc) {
        try {
            EmbedBuilder embed = new EmbedBuilder()
                    .withAuthorName("\uD83D\uDDC3 new todo item")
                    .appendField(ideaName, ideaDesc, false)
                    .withFooterText("ID: null")
                    .withTimestamp(System.currentTimeMillis())
                    .withColor(Color.WHITE);
            return embed.build();
        } catch (Exception e) {
            Util.reportHome(e);
        }
        return null;
    }

    public static void updateMessageID(IMessage message) {
        try {
            IEmbed oldEmbed = message.getEmbeds().get(0);

            String ideaName = oldEmbed.getEmbedFields().get(0).getName();
            String ideaDesc = oldEmbed.getEmbedFields().get(0).getValue();

            String text = "\uD83D\uDDC3 - new todo item";
            EmbedBuilder embed = new EmbedBuilder()
                    .appendField(ideaName, ideaDesc, false)
                    .withFooterText("ID: " + message.getStringID())
                    .withTimestamp(System.currentTimeMillis())
                    .withColor(Color.WHITE);

            RequestBuffer.request(() -> {
                message.edit(text, embed.build());
            });
        } catch (Exception e) {
            Util.reportHome(e);
        }
    }

    public static void updateCompletedItem(IMessage message) {
        try {
            IEmbed oldEmbed = message.getEmbeds().get(0);

            String ideaName = oldEmbed.getEmbedFields().get(0).getName();
            String ideaDesc = oldEmbed.getEmbedFields().get(0).getValue();

            String text = "✅ - todo item completed";
            EmbedBuilder embed = new EmbedBuilder()
                    .appendField(ideaName, ideaDesc, false)
                    .withFooterText("ID: " + message.getStringID())
                    .withTimestamp(System.currentTimeMillis())
                    .withColor(Color.GREEN);

            RequestBuffer.request(() -> {
                message.removeAllReactions();
            });

            RequestBuffer.request(() -> {
                message.edit(text, embed.build());
            });


        } catch (Exception e) {
            Util.reportHome(e);
        }
    }

    public static void resetReactions(IMessage message) {
        RequestBuffer.request(() -> {
                message.removeAllReactions();
        });
        RequestBuffer.request(() -> {
                message.addReaction(EmojiManager.getByUnicode("\u2705"));
        });
        RequestBuffer.request(() -> {
                message.addReaction(EmojiManager.getByUnicode("\u274C"));
        });
    }
}
