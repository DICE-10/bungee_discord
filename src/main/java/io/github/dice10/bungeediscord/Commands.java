package io.github.dice10.bungeediscord;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.embed.EmbedBuilder;

import java.awt.*;

public class Commands extends Command {
    protected String token;
    protected DiscordApi api;
    protected String senderServer;
    protected long chatChannelID;
    protected long repoChannelID;

    public Commands(String _token, DiscordApi _api, long _chatChannelID,long _repoChannelID){
        super("bDiscord");
        setToken(_token);
        setApi(_api);
        setChatChannelID(_chatChannelID);
        setRepoChannelID(_repoChannelID);
    }


    @Override
    public void execute(CommandSender commandSender,String[] args){
        String cmd = args[0];
        DiscordApi api = getApi();
        if(commandSender instanceof ProxiedPlayer){
            ProxiedPlayer player = (ProxiedPlayer) commandSender;
            if(cmd.equalsIgnoreCase("repo")){
                TextChannel channel = api.getTextChannelById(getRepoChannelID()).get();
                new MessageBuilder()
                        .setEmbed(new EmbedBuilder().setTitle(args[1])
                        .setAuthor("対象者："+args[2])
                        .setDescription(args[3])
                        .setFooter("通報者："+player.getName())
                        .setColor(Color.RED)).send(channel);
            }
            else if(cmd.equalsIgnoreCase("bc")||cmd.equalsIgnoreCase("bungeediscord")){

            }
        }
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
        this.chatChannelID = Id;
    }

    public long getChatChannelID(){
        return this.chatChannelID;
    }

    public void setRepoChannelID(long Id){
        this.repoChannelID = Id;
    }

    public long getRepoChannelID(){
        return this.repoChannelID;
    }
}
