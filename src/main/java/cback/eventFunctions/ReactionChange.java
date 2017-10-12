package cback.eventFunctions;

import cback.SyncBot;
import cback.Util;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.api.internal.json.objects.EmbedObject;
import sx.blah.discord.handle.impl.events.guild.channel.message.reaction.ReactionAddEvent;
import sx.blah.discord.handle.obj.IEmbed;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IReaction;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.EmbedBuilder;
import sx.blah.discord.util.MissingPermissionsException;
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

        IMessage message = event.getMessage();
        IUser user = event.getUser();
        IReaction reaction = event.getReaction();
        if (user.getLongID() == SyncBot.CBACK_USR_ID) {
            String emojiName = event.getReaction().getEmoji().getName();

            if (emojiName.equals("📗")) {
                updateCompletedItem(message, user, reaction);
            } else if (emojiName.equals("📙")) {
                updateStartedItem(message, user, reaction);
            } else if (emojiName.equals("📕")) {
                message.delete();
            } else {
                removeReaction(message, user, reaction);
            }

        } else {
            removeReaction(message, user, reaction);
        }
    }

    public void updateStartedItem(IMessage message, IUser user, IReaction reaction) {
        try {
            IEmbed oldEmbed = message.getEmbeds().get(0);

            String ideaName = oldEmbed.getEmbedFields().get(0).getName();
            String ideaDesc = oldEmbed.getEmbedFields().get(0).getValue();

            String text = "📙 - todo item started";
            EmbedBuilder embed = new EmbedBuilder()
                    .appendField(ideaName, ideaDesc, false)
                    .withFooterText("ID: " + message.getStringID())
                    .withTimestamp(System.currentTimeMillis())
                    .withColor(Color.ORANGE);

            removeReaction(message, user, reaction);
            RequestBuffer.request(() -> message.edit(text, embed.build()));
        } catch (DiscordException | MissingPermissionsException e) {
            Util.reportHome(e);
        }
    }

    public void updateCompletedItem(IMessage message, IUser user, IReaction reaction) {
        try {
            IEmbed oldEmbed = message.getEmbeds().get(0);

            String ideaName = oldEmbed.getEmbedFields().get(0).getName();
            String ideaDesc = oldEmbed.getEmbedFields().get(0).getValue();

            String text = "📗 - todo item completed";
            EmbedBuilder embed = new EmbedBuilder()
                    .appendField(ideaName, ideaDesc, false)
                    .withFooterText("ID: " + message.getStringID())
                    .withTimestamp(System.currentTimeMillis())
                    .withColor(Color.GREEN);

            removeReaction(message, user, reaction);
            RequestBuffer.request(() -> message.edit(text, embed.build()));
        } catch (DiscordException | MissingPermissionsException e) {
            Util.reportHome(e);
        }
    }

    public void removeReaction(IMessage message, IUser user, IReaction reaction) {
        try {
            RequestBuffer.request(() -> message.removeReaction(user, reaction));
        } catch (DiscordException | MissingPermissionsException e) {
            Util.reportHome(e);
        }
    }
}
