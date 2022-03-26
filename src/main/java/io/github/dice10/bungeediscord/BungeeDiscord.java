package io.github.dice10.bungeediscord;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.MessageAttachment;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.embed.EmbedBuilder;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public final class BungeeDiscord extends Plugin {

    private File file;
    private Configuration config;
    private String token;
    private long chatChannel_ID;
    private long repoChannel_ID;
    private DiscordApi api;

    @Override
    public void onEnable() {
        file = new File(getDataFolder(),"/discord_config.yml");
        try {
            if(!getDataFolder().exists()){
                getDataFolder().mkdir();
            }
            if(!file.exists()){
                file.createNewFile();
                getProxy().getConsole().sendMessage("§6Discord's BOT won't start because BOT's token and");
                getProxy().getConsole().sendMessage("§6channel ID are not specified in the configuration file.");
                config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(file);
                config.set("TOKEN","xxxxxxxxxxx");
                config.set("TextChannel_ID","xxxxxxxxxxx");
                config.set("reportChannel_ID","xxxxxxxxxxx");
                config.set("chatLink",0);
                ConfigurationProvider.getProvider(YamlConfiguration.class).save(config,file);
            }
            else{
                config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(file);
                chatChannel_ID=(Long) config.get("TextChannel_ID");
                setChatChannelID(chatChannel_ID);
                token = (String) config.get("TOKEN");
                repoChannel_ID = (Long) config.get("reportChannel_ID");
                setToken(token);
                DiscordApi $api = new DiscordApiBuilder().setToken(token).login().join();
                $api.createBotInvite();
                setApi($api);
                Message message = new Message(token,$api,chatChannel_ID);
                TextChannel channel = api.getTextChannelById(chatChannel_ID).get();
                new MessageBuilder()
                        .append(":blue_circle:" + "サーバーが起動しました。")
                        .send(channel);
                api.addMessageCreateListener(event -> {
                    TextComponent msg = null;
                    long channelID = event.getChannel().getId();
                    List<MessageAttachment> list = event.getMessage().getAttachments();

                    String name = event.getMessageAuthor().getName();
                    String sendMessage = event.getMessage().getContent();
                    if(channelID == message.getChatChannelID() && !event.getMessageAuthor().isBotUser() && !event.getMessageContent().equalsIgnoreCase("!userList")){
                        String imageLink = null;

                        if(list.size() >0) {
                            imageLink = list.get(0).getUrl().toString();
                            msg = new TextComponent("§b[" + name + "@Discord]§f" + sendMessage);
                            ProxyServer.getInstance().broadcast(msg);
                            msg = new TextComponent(ChatColor.LIGHT_PURPLE+"[Image File Link]\n"+ ChatColor.GOLD+ imageLink);
                            msg.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, imageLink));
                        }
                        else {
                            msg = new TextComponent("§b[" + name + "@Discord]§f" + sendMessage);
                        }
                        ProxyServer.getInstance().broadcast(msg);
                    }
                    else if(event.getMessageContent().equalsIgnoreCase("!userList")){
                        MessageBuilder msBuilder = new MessageBuilder();
                        EmbedBuilder emBuilder= new EmbedBuilder();
                        String UserName = null;
                        Collection<ProxiedPlayer> Users = ProxyServer.getInstance().getPlayers();
                        Map<String, ServerInfo> servers = ProxyServer.getInstance().getServers();
                        String info;
                        String res = null;
                        if(Users.size() > 0) {
                            for(Map.Entry<String,ServerInfo> server : servers.entrySet()) {
                                UserName = null;
                                info = "\n **<"+server.getValue().getName() + ">** \n";
                                for (ProxiedPlayer user : Users) {
                                    if (UserName == null && user.getServer().getInfo().getName().equalsIgnoreCase(server.getValue().getName())) {
                                        UserName = user.getDisplayName();
                                    } else if(user.getServer().getInfo().getName().equalsIgnoreCase(server.getValue().getName())){
                                        UserName += user.getDisplayName() + "\n";
                                    }
                                }
                                if(UserName == null || UserName.length() <= 0){
                                    UserName = "No User.";
                                }
                                if(res == null){
                                    res = info + UserName + "\n";
                                }
                                else {
                                    res += info + UserName + "\n";
                                }
                            }
                        }
                        else{
                            res = "No User.";
                        }
                        msBuilder.setEmbed(emBuilder.setTitle("Online Users.")
                                                    .setDescription(res)
                                                    .setColor(Color.GREEN)).send(api.getTextChannelById(channelID).get());
                    }

                });
                message.setApi(api);
                getProxy().getPluginManager().registerListener(this, new Message(token,api,chatChannel_ID));
                getProxy().registerChannel("my:channel");

                getLogger().info(token);
                getProxy().getPluginManager().registerCommand(this,new Commands(token,$api,chatChannel_ID,repoChannel_ID));
                getProxy().getConsole().sendMessage(ChatColor.AQUA+"Launch BungeeDiscord.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onDisable() {
        DiscordApi _api = getApi();
        // Plugin shutdown logic
        new MessageBuilder()
                .append(":red_circle:" + "サーバーが停止しました。")
                .send(_api.getTextChannelById(getChatChannelID()).get());
        getProxy().getConsole().sendMessage(new TextComponent(ChatColor.DARK_GREEN +"Stop BungeeDiscord."));
    }

    public void setToken(String _token){
        this.token = _token;
    }

    public String getToken(){
        return this.token;
    }

    public void setApi(DiscordApi _api){
        this.api = _api;
    }

    public DiscordApi getApi(){
        return this.api;
    }

    public void setChatChannelID(long Id){
        this.chatChannel_ID = Id;
    }

    public long getChatChannelID(){
        return this.chatChannel_ID;
    }
}
