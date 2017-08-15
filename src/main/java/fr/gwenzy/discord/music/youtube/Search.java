/*
 * Copyright (c) 2012 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package fr.gwenzy.discord.music.youtube;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.*;
import fr.gwenzy.discord.music.Tokens;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

/**
 * Print a list of videos matching a search term.
 *
 * @author Jeremy Walker
 */
public class Search {

    /**
     * Define a global variable that identifies the name of a file that
     * contains the developer's API key.
     */
    private static final String PROPERTIES_FILENAME = "youtube.properties";

    private static final long NUMBER_OF_VIDEOS_RETURNED = 5;

    /**
     * Define a global instance of a Youtube object, which will be used
     * to make YouTube Data API requests.
     */
    private static YouTube youtube;


    public Search (){
        // Read the developer key from the properties file.

        try {
            // This object is used to make YouTube Data API requests. The last
            // argument is required, but since we don't need anything
            // initialized when the HttpRequest is initialized, we override
            // the interface and provide a no-op function.
            youtube = new YouTube.Builder(Auth.HTTP_TRANSPORT, Auth.JSON_FACTORY, new HttpRequestInitializer() {
                public void initialize(HttpRequest request) throws IOException {
                }
            }).setApplicationName("youtube-cmdline-search-sample").build();

        }
        catch (Throwable t) {
            t.printStackTrace();
        }
    }

    public List<String> search(String queryTerm) throws IOException {

        List<String> results = new ArrayList<String>();
        // Prompt the user to enter a query term.

        // Define the API request for retrieving search results.
        YouTube.Search.List search = null;
        try {
            search = youtube.search().list("id,snippet");
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Set your developer key from the {{ Google Cloud Console }} for
        // non-authenticated requests. See:
        // {{ https://cloud.google.com/console }}
        search.setKey(Tokens.API_KEY_YOUTUBE);
        search.setQ(queryTerm);

        // Restrict the search results to only include videos. See:
        // https://developers.google.com/youtube/v3/docs/search/list#type
        search.setType("video");

        // To increase efficiency, only retrieve the fields that the
        // application uses.
        search.setFields("items(id/kind,id/videoId,snippet/title,snippet/thumbnails/default/url)");
        search.setMaxResults(NUMBER_OF_VIDEOS_RETURNED);

        // Call the API and print results.
        SearchListResponse searchResponse = null;
            searchResponse = search.execute();

        List<SearchResult> searchResultList = searchResponse.getItems();
        if (searchResultList != null) {
            for(SearchResult result : searchResultList){
                YouTube.Videos.List videoRequest = null;
                videoRequest = youtube.videos().list("snippet,statistics,contentDetails");

                videoRequest.setId(result.getId().getVideoId());
                videoRequest.setKey(Tokens.API_KEY_YOUTUBE);
                VideoListResponse listResponse = videoRequest.execute();
                List<Video> videoList = listResponse.getItems();

                Video targetVideo = videoList.iterator().next();
                results.add(result.getSnippet().getTitle()+"!;;!"+
                        result.getId().getVideoId()+"!;;!"+
                        targetVideo.getContentDetails().getDuration()+"!;;!"+
                        targetVideo.getSnippet().getChannelTitle()+"!;;!"+
                        targetVideo.getSnippet().getThumbnails().getDefault().getUrl()
                );

            }
        }

        return results;
    }


}
