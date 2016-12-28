package cback.eventFunctions;

import cback.Util;
import cback.globalChannels;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.NickNameChangeEvent;
import sx.blah.discord.handle.obj.IGuild;

import java.util.Arrays;
import java.util.List;

public class NicknameChange {

    public static List<String> guilds = Arrays.asList("263120914894422017", "192441520178200577", "256248900124540929", "191589587817070593");

    @EventSubscriber
    public void nicknameChangeEvent(NickNameChangeEvent event) {
        IGuild guild = event.getGuild();

        if (guilds.contains(guild.getID())) {

            guilds.stream()
                    .filter(c -> !c.equals(guild.getID()))
                    .forEach(c -> {
                        try {
                            event.getClient().getGuildByID(c).setUserNickname(event.getUser(), event.getUser().getDisplayName(guild));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });

        }
    }

}
