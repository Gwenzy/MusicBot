package fr.gwenzy.discord.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * This class schedules tracks for the audio player. It contains the queue of tracks.
 */
public class TrackScheduler extends AudioEventAdapter {
  private final AudioPlayer player;
  private final BlockingQueue<AudioTrack> queue;
  private final long guildID;
  /**
   * @param player The audio player this scheduler uses
   */
  public TrackScheduler(AudioPlayer player, long guildID) {
    this.player = player;
    this.queue = new LinkedBlockingQueue();
    this.guildID = guildID;
  }

  /**
   * Add the next track to queue or play right away if nothing is in the queue.
   *
   * @param track The track to play or add to queue.
   */
  public void queue(AudioTrack track, IChannel channel, long userID, IGuild guild) {
    // Calling startTrack with the noInterrupt set to true will start the track only if nothing is currently playing. If
    // something is playing, it returns false and does nothing. In that case the player was already playing so this
    // track goes to the queue instead.
    List<Long> author = Main.authors.get(guild.getLongID());
    author.add(userID);
    Main.authors.put(guild.getLongID(), author);
    if (!player.startTrack(track, true)) {
      queue.offer(track);


    }
  }

  public List<AudioTrack> getQueueTracks(){
    List<AudioTrack> tracks = new ArrayList<>();
    Iterator<AudioTrack> it = queue.iterator();
    while(it.hasNext()){
      tracks.add(it.next());
    }

    return tracks;
  }
  public int getQueueSize(){
    return queue.size();
  }
  /**
   * Start the next track, stopping the current one if it is playing.
   */
  public void nextTrack(IChannel channel, long guildID) {
    // Start the next track, regardless of if something is already playing or not. In case queue was empty, we are
    // giving null to startTrack, which is a valid argument and will simply stop the player.
    if(channel!=null)
      channel.sendMessage("Passage à la musique suivante (ou arrêt du lecteur si queue vide)");
    player.startTrack(queue.poll(), false);



  }

  @Override
  public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
    // Only start the next track if the end reason is suitable for it (FINISHED or LOAD_FAILED)

    if (endReason.mayStartNext) {
      nextTrack(null, this.guildID);
    }
  }

  @Override
  public void onTrackStart(AudioPlayer player, AudioTrack track){
    Main.startingTimestamp = System.currentTimeMillis();
    List<Long> author = Main.authors.get(guildID);
    Main.currentAuthor.put(guildID, author.get(0));
    author.remove(0);
    Main.authors.put(guildID, author);
    List<Long> emptyList = new ArrayList<>();
    Main.nextCount.put(guildID, emptyList);
  }
}
