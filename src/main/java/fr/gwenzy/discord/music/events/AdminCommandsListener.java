package fr.gwenzy.discord.music.events;

import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import fr.gwenzy.discord.music.Main;
import org.joda.time.Period;
import org.joda.time.Seconds;
import org.joda.time.format.ISOPeriodFormat;
import org.joda.time.format.PeriodFormatter;
import sx.blah.discord.api.events.IListener;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.EmbedBuilder;
import sx.blah.discord.util.RequestBuffer;

import java.awt.*;
import java.io.IOException;
import java.util.List;


/**
 * Created by gwend on 06/08/2017.
 */
public class AdminCommandsListener implements IListener<MessageReceivedEvent> {
    public void handle(MessageReceivedEvent messageReceivedEvent) {
        if(messageReceivedEvent.getMessage().getFormattedContent().startsWith(Main.COMMAND_PREFIX)){
            String[] args = messageReceivedEvent.getMessage().getFormattedContent().split(" ");




                if(args.length>1)
                    if(args[1].equalsIgnoreCase("join") && Main.operatorsID.contains(messageReceivedEvent.getAuthor().getStringID())){
                        messageReceivedEvent.getAuthor().getVoiceStateForGuild(messageReceivedEvent.getGuild()).getChannel().join();
                    }
                    else if(args[1].equalsIgnoreCase("stop") && (Main.operatorsID.contains(messageReceivedEvent.getAuthor().getStringID()) || Main.authors.get(messageReceivedEvent.getGuild().getLongID()).get(0).equals(messageReceivedEvent.getAuthor().getLongID()))){
                        Main.getGuildAudioPlayer(messageReceivedEvent.getGuild()).player.stopTrack();

                    }
                    else if(args[1].equalsIgnoreCase("leave") && Main.operatorsID.contains(messageReceivedEvent.getAuthor().getStringID())){
                        messageReceivedEvent.getAuthor().getVoiceStateForGuild(messageReceivedEvent.getGuild()).getChannel().leave();
                    }
                    else if(args[1].equalsIgnoreCase("next") && (Main.operatorsID.contains(messageReceivedEvent.getAuthor().getStringID()) || Main.authors.get(messageReceivedEvent.getGuild().getLongID()).get(0).equals(messageReceivedEvent.getAuthor().getLongID()))){
                        Main.getGuildAudioPlayer(messageReceivedEvent.getGuild()).scheduler.nextTrack();
                    }
                    else if(args[1].equalsIgnoreCase("disconnect") && Main.operatorsID.contains(messageReceivedEvent.getAuthor().getStringID())){
                        Main.client.logout();
                    }
                    else if(args[1].equalsIgnoreCase("infos")){

                        try {
                            AudioTrackInfo infos = Main.musicManagers.get(messageReceivedEvent.getGuild().getLongID()).player.getPlayingTrack().getInfo();
                            EmbedBuilder eb = new EmbedBuilder();
                            eb.withColor(Color.CYAN);
                            eb.withTitle("Track informations");
                            eb.withDesc("------------------------------------------------------------------");
                            eb.appendField("Title", infos.title, false);
                            eb.appendField("Youtube Channel", infos.author, false);
                            eb.appendField("Duration", (infos.length/1000)+"s", false);
                            eb.appendField("URL", infos.uri, false);
                            eb.appendField("Progress", ""+((System.currentTimeMillis()-Main.startingTimestamp)/1000)+"/"+(infos.length/1000)+"s - "+Math.round(((double)System.currentTimeMillis()-(double)Main.startingTimestamp)/(double)infos.length*100)+"% - "+((infos.length-System.currentTimeMillis()+Main.startingTimestamp)/1000)+"s left" , false);

                            messageReceivedEvent.getChannel().sendMessage(eb.build());
                            messageReceivedEvent.getMessage().delete();



                        }catch(Exception e){
                            messageReceivedEvent.getChannel().sendMessage("No song is currently playing");
                            e.printStackTrace();
                        }
                    }
                if(args.length>2)
                    if(args[1].equalsIgnoreCase("play")){


                        String path = args[2];
                        for(int i=3; i<args.length; i++){
                            path += " "+ args[i];

                        }
                        if(path.startsWith("#")&&Main.canUseIDs)
                            try{
                                path = Main.videoIDs.get(Integer.parseInt(path.substring(1, path.length())));
                            }catch(Exception e){}


                        Main.loadAndPlay(messageReceivedEvent.getChannel(), path, messageReceivedEvent.getAuthor().getLongID());

                    }

                    else if (args[1].equalsIgnoreCase("search")){
                        String query = args[2];
                        for(int i=3; i<args.length; i++){
                            query += " "+ args[i];

                        }
                        List<String> results = null;
                        try {
                            results = Main.search.search(query);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        int id=1;
                        if(results == null)
                            messageReceivedEvent.getChannel().sendMessage("No results");
                        for(String str : results){
                            EmbedBuilder eb = new EmbedBuilder();
                            eb.withColor(Color.CYAN);
                            eb.withTitle("Query result | ID #"+id);
                            eb.withDesc("------------------------------------------------------------------");

                            String title = str.split("!;;!")[0];
                            String videoID = str.split("!;;!")[1];
                            String duration = str.split("!;;!")[2];
                            String channel = str.split("!;;!")[3];
                            String thumbnailURL = str.split("!;;!")[4];

                            PeriodFormatter formatter = ISOPeriodFormat.standard();
                            Period p = formatter.parsePeriod(duration);
                            duration = String.format("%02d", p.getMinutes())+":"+String.format("%02d", p.getSeconds());


                            eb.appendField("Title", title, false);
                            eb.appendField("ID", videoID, false);

                            eb.appendField("Duration", duration, false);
                            eb.appendField("Channel", channel, false);
                            eb.withThumbnail(thumbnailURL);
                            Main.canUseIDs = true;
                            Main.videoIDs.put(id, videoID);

                            RequestBuffer.request(() -> {
                                messageReceivedEvent.getChannel().sendMessage(eb.build());
                            });
                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException e) {}
                            id++;
                        }


                    }



                    else if (args[1].equalsIgnoreCase("fastplay")){
                        String query = args[2];
                        for(int i=3; i<args.length; i++){
                            query += " "+ args[i];

                        }
                        List<String> results = null;
                        try {
                            results = Main.search.search(query);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        try {
                            Main.loadAndPlay(messageReceivedEvent.getChannel(), results.get(0).split("!;;!")[1], messageReceivedEvent.getAuthor().getLongID());
                        }catch(Exception e) {
                            messageReceivedEvent.getChannel().sendMessage("No results");
                        }
                    }






        }
    }
}
