package fr.gwenzy.discord.music;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import fr.gwenzy.discord.music.events.AdminCommandsListener;
import fr.gwenzy.discord.music.events.DevelopperCommandsListener;
import fr.gwenzy.discord.music.events.PrivateMessageCommandsListener;
import fr.gwenzy.discord.music.events.ReadyListener;
import fr.gwenzy.discord.music.youtube.Search;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.audio.IAudioManager;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IVoiceChannel;
import sx.blah.discord.util.MissingPermissionsException;

import java.util.*;

public class Main {
  private static final Logger log = LoggerFactory.getLogger(Main.class);
  public static final List<String> operatorsID = Arrays.asList("205809466514472960", "224940744362819584", "214105906789613569", "228176532722548736");


  public static final String COMMAND_PREFIX = "@Sagiri ";
    public static Search search;
  public static IDiscordClient client;
    public static HashMap<Integer, String> videoIDs = new HashMap<>();
  public static HashMap<Long,List<Long>> authors = new HashMap<>();
  public static HashMap<Long,Long> currentAuthor = new HashMap<>();
  public static HashMap<Long,List<Long>> nextCount = new HashMap<>();
    public static boolean canUseIDs = false;
  public static long startingTimestamp;


  public static void main(String[] args) throws Exception {
    client = new ClientBuilder()
        .withToken(Tokens.TOKEN_BOT)
            .registerListener(new Main())
            .registerListener(new AdminCommandsListener())
            .registerListener(new ReadyListener())
            .registerListener(new PrivateMessageCommandsListener())
            .registerListener(new DevelopperCommandsListener())
        .login();

    search = new Search();
  }

  public static AudioPlayerManager playerManager;
  public static Map<Long, GuildMusicManager> musicManagers;

  private Main() {
    this.musicManagers = new HashMap();

    this.playerManager = new DefaultAudioPlayerManager();

    AudioSourceManagers.registerRemoteSources(playerManager);
    AudioSourceManagers.registerLocalSource(playerManager);





  }

  public static synchronized GuildMusicManager getGuildAudioPlayer(IGuild guild) {
    long guildId = guild.getLongID();
    GuildMusicManager musicManager = musicManagers.get(guildId);

    if (musicManager == null) {
      musicManager = new GuildMusicManager(playerManager, guildId);
      musicManagers.put(guildId, musicManager);
      authors.put(guildId, new ArrayList<Long>());
    }

    guild.getAudioManager().setAudioProvider(musicManager.getAudioProvider());

    return musicManager;
  }

  public static void loadAndPlay(final IChannel channel, final String trackUrl, long userID, IGuild guild) {

    final GuildMusicManager musicManager = getGuildAudioPlayer(guild);
    System.out.println("Loading "+trackUrl+" on "+guild.getLongID());
    playerManager.loadItemOrdered(musicManager, trackUrl, new AudioLoadResultHandler() {
      public void trackLoaded(AudioTrack track) {

        play(guild, musicManager, track, channel, userID);

        if(musicManager.scheduler.getQueueSize()==0)
          sendMessageToChannel(channel, "Now playing: "+track.getInfo().title);
        else
          sendMessageToChannel(channel, "Added to queue: "+track.getInfo().title+". Queue rank : #"+musicManager.scheduler.getQueueSize());

      }

      public void playlistLoaded(AudioPlaylist playlist) {
        AudioTrack firstTrack = playlist.getSelectedTrack();

        if (firstTrack == null) {
          firstTrack = playlist.getTracks().get(0);
        }


        play(guild, musicManager, firstTrack, channel, userID);
      }

      public void noMatches() {
        sendMessageToChannel(channel, "Nothing was found, try with something else");
      }

      public void loadFailed(FriendlyException exception) {
        sendMessageToChannel(channel, "Error while attempting to loading track");
        exception.printStackTrace();
      }
    });
  }

  private static void play(IGuild guild, GuildMusicManager musicManager, AudioTrack track, IChannel channel, long userID) {
    connectToFirstVoiceChannel(guild.getAudioManager());

    musicManager.scheduler.queue(track, channel, userID, guild);




  }

  private void skipTrack(IChannel channel, long guildID) {
    GuildMusicManager musicManager = getGuildAudioPlayer(channel.getGuild());
    musicManager.scheduler.nextTrack(null, guildID);

  }

  public static void sendMessageToChannel(IChannel channel, String message) {
    try {
      channel.sendMessage(message);
    } catch (Exception e) {
      log.warn("Failed to send message {} to {}", message, channel.getName(), e);
    }
  }

  private static void connectToFirstVoiceChannel(IAudioManager audioManager) {
    for (IVoiceChannel voiceChannel : audioManager.getGuild().getVoiceChannels()) {
      if (voiceChannel.isConnected()) {
        return;
      }
    }

    for (IVoiceChannel voiceChannel : audioManager.getGuild().getVoiceChannels()) {
      try {
        voiceChannel.join();
      } catch (MissingPermissionsException e) {
        log.warn("Cannot enter voice channel {}", voiceChannel.getName(), e);
      }
    }
  }
}
