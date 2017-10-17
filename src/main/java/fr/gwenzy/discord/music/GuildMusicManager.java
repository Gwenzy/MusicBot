package fr.gwenzy.discord.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import fr.gwenzy.discord.music.events.StartTrackListener;

/**
 * Holder for both the player and a track scheduler for one guild.
 */
public class GuildMusicManager {
  /**
   * Audio player for the guild.
   */
  public final AudioPlayer player;
  /**
   * Track scheduler for the player.
   */
  public final TrackScheduler scheduler;

  /**
   * Creates a player and a track scheduler.
   * @param manager Audio player manager to use for creating the player.
   */
  public GuildMusicManager(AudioPlayerManager manager, long guildID) {
    player = manager.createPlayer();
    player.addListener(new StartTrackListener(guildID));
    scheduler = new TrackScheduler(player);
    player.addListener(scheduler);
  }

  /**
   * @return Wrapper around AudioPlayer to use it as an AudioSendHandler.
   */
  public AudioProvider getAudioProvider() {
    return new AudioProvider(player);
  }
}
