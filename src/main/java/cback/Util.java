package cback;

import com.google.gson.JsonSyntaxException;
import in.ashwanthkumar.slack.webhook.Slack;
import in.ashwanthkumar.slack.webhook.SlackMessage;
import org.apache.http.message.BasicNameValuePair;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.internal.DiscordClientImpl;
import sx.blah.discord.api.internal.DiscordEndpoints;
import sx.blah.discord.api.internal.DiscordUtils;
import sx.blah.discord.api.internal.json.objects.UserObject;
import sx.blah.discord.handle.obj.*;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.RequestBuffer;

import java.io.File;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Util {

    public static File botPath;

    private static final Pattern USER_MENTION_PATTERN = Pattern.compile("^<@!?(\\d+)>$");

    static {
        try {
            botPath = new File(SyncBot.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath()).getParentFile();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public static void sendMessage(IChannel channel, String message) {
        try {
            channel.sendMessage(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static IMessage sendBufferedMessage(IChannel channel, String message) {
        RequestBuffer.RequestFuture<IMessage> sentMessage = RequestBuffer.request(() -> {
            try {
                return channel.sendMessage(message);
            } catch (MissingPermissionsException | DiscordException e) {
                e.printStackTrace();
            }
            return null;
        });
        return sentMessage.get();
    }

    public static void deleteMessage(IMessage message) {
        try {
            message.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void deleteBufferedMessage(IMessage message) {
        RequestBuffer.request(() -> {
            try {
                message.delete();
            } catch (MissingPermissionsException | DiscordException e) {
                e.printStackTrace();
            }
        });
    }

    public static void bulkDelete(IChannel channel, List<IMessage> toDelete) {
        RequestBuffer.request(() -> {
            if (toDelete.size() > 0) {
                if (toDelete.size() == 1) {
                    try {
                        toDelete.get(0).delete();
                    } catch (MissingPermissionsException | DiscordException e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        channel.getMessages().bulkDelete(toDelete);
                    } catch (DiscordException | MissingPermissionsException e) {
                        e.printStackTrace();
                    }

                }
            }
        });
    }

    public static void errorLog(IMessage message, String text) {
        try {
            Util.sendPrivateMessage(SyncBot.getInstance().getClient().getUserByID("73416411443113984"), text + " in ``#" + message.getChannel().getName() + "``");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Boolean permissionCheck(IMessage message, String role) {
        try {
            return message.getGuild().getRolesForUser(message.getAuthor()).contains(message.getGuild().getRolesByName(role).get(0));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void sendPrivateMessage(IUser user, String message) {
        try {
            user.getClient().getOrCreatePMChannel(user).sendMessage(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void sendGlobalChat(String URL, IMessage message) {
        String content = message.getFormattedContent().replaceAll("@everyone","everyone").replaceAll("@here","here");
        try {
            new Slack(URL)
                    .icon(message.getAuthor().getAvatarURL())
                    .displayName(message.getAuthor().getDisplayName(message.getGuild()) + " (" + message.getGuild().getName() + ")")
                    .push(new SlackMessage(content));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static int toInt(long value) {
        try {
            return Math.toIntExact(value);
        } catch (ArithmeticException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static int getCurrentTime() {
        return toInt(System.currentTimeMillis() / 1000);
    }

    public static IUser getUserFromMentionArg(String arg) {
        Matcher matcher = USER_MENTION_PATTERN.matcher(arg);
        if (matcher.matches()) {
            return SyncBot.getInstance().getClient().getUserByID(matcher.group(1));
        }
        return null;
    }

    public static String to12Hour(String time) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("H:mm");
            Date dateObj = sdf.parse(time);
            return new SimpleDateFormat("K:mm").format(dateObj);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return time;
    }

    public static String requestUsernameByID(String id) {
        IDiscordClient client = SyncBot.getInstance().getClient();

        RequestBuffer.RequestFuture<String> userNameResult = RequestBuffer.request(() -> {
            try {
                String result = ((DiscordClientImpl) client).REQUESTS.GET.makeRequest(DiscordEndpoints.USERS + id,
                        new BasicNameValuePair("authorization", SyncBot.getInstance().getClient().getToken()));
                return DiscordUtils.GSON.fromJson(result, UserObject.class).username;
            } catch (JsonSyntaxException e) {
                e.printStackTrace();
            } catch (DiscordException e) {
                e.printStackTrace();
            }

            return "NULL";
        });

        return userNameResult.get();
    }

    public static List<IUser> getUsersByRole(String roleID) {
        try {
            IGuild guild = SyncBot.getInstance().getClient().getGuildByID("256248900124540929");
            IRole role = guild.getRoleByID(roleID);

            if (role != null) {
                List<IUser> allUsers = guild.getUsers();
                List<IUser> ourUsers = new ArrayList<>();


                for (IUser u : allUsers) {
                    List<IRole> userRoles = u.getRolesForGuild(guild);

                    if (userRoles.contains(role)) {
                        ourUsers.add(u);
                    }
                }

                return ourUsers;

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<IMessage> getSuggestions() {
        try {
            IChannel channel = SyncBot.getInstance().getClient().getGuildByID("256248900124540929").getChannelByID("256491839870337024");

            List<IMessage> messages = channel.getPinnedMessages();

            return messages;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getRule(String ruleID) {
        try {
            String rule = SyncBot.getInstance().getClient().getChannelByID("251916332747063296").getMessageByID(ruleID).getContent();

            return rule;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
