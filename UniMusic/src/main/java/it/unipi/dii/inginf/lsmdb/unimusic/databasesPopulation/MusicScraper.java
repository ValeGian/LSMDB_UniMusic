package it.unipi.dii.inginf.lsmdb.unimusic.databasesPopulation;

import it.unipi.dii.inginf.lsmdb.unimusic.middleware.dao.SongDAOImpl;
import it.unipi.dii.inginf.lsmdb.unimusic.middleware.entities.Album;
import it.unipi.dii.inginf.lsmdb.unimusic.middleware.entities.Song;
import it.unipi.dii.inginf.lsmdb.unimusic.middleware.exception.ActionNotCompletedException;
import org.bson.types.ObjectId;
import org.json.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Class that takes care of performing all actions related to scarping.
 * It is used to fill databases with all the information needed about songs.
 */
public class MusicScraper {

    public static void main(String[] args) throws ActionNotCompletedException {

        int miss = 0;
        int noResponse = 0;
        int i;

        for(i = 1; i < 100; i++) {

            StringBuffer responseGenius = getResponse(
                    "https://api.genius.com/songs/" + i, " " +
                    "BQCdh8aec2FNVTMsnCm0CyxjpshKE0JnTHWpAQpQzTXZh2kmPkvMLz2M4YCXNyR6mLOeDNhmu5Ok6BVG_L-HMMlELGA_i0GODqBcCaUqgw-bPTObEG0idAKntXoTFXabYaXqDEsv4Jje");
            if(responseGenius == null){
                noResponse++;
                continue;
            }

            JSONObject song;
            Song songToInsert = new Song();
            Album songAlbum = new Album();
            ArrayList<String> artists = new ArrayList<>();
            String uriSpotify = "";

            try {
                song = new JSONObject(responseGenius.toString()).getJSONObject("response").getJSONObject("song");

                songToInsert.setTitle(song.getString("full_title"));
                songToInsert.setGeniusMediaURL(song.getString("url"));
                songToInsert.setArtist(song.getJSONObject("primary_artist").getString("name"));

                JSONArray media = song.getJSONArray("media");

                for (int iter = 0; iter < media.length(); iter++) {
                    String provider = media.getJSONObject(iter).getString("provider");
                    if (provider.equals("youtube"))
                        songToInsert.setYoutubeMediaURL(media.getJSONObject(iter).getString("url"));

                    else if (provider.equals("spotify")) {
                        songToInsert.setSpotifyMediaURL(media.getJSONObject(iter).getString("url"));
                        uriSpotify = media.getJSONObject(iter).getString("native_uri").split(":")[2];
                    }
                }

                if (songToInsert.getYoutubeMediaURL() == null || songToInsert.getSpotifyMediaURL() == null){
                    miss++;
                    continue;
                }

            }catch (JSONException ex){
                miss++;
                continue;
            }

            StringBuffer responseSpotify = getResponse("https://api.spotify.com/v1/tracks/" + uriSpotify, " BQCpfpPIujVBtY-Rup5HzXrXSmAo4cuQJJLBgjz4EW7PGZ7bP3WqX9BzFCJRslx0Xl8JbtwjUC6Ueui-8XsKgUbLroYI5Z3OuiBrLg2YzCP0P5LAkLjT2lsRnHD1yc9IWlmS_NWqZvqdX7szorKT7Eh9OJRWXJPPJw-FuPjiNg");
            if(responseSpotify == null) {
                System.out.println("Spotify response missed, check the spotify bearer if the problem persists!");
                miss++;
                continue;
            }

            songToInsert.setID(new ObjectId().toString());

            double spotifyRating = new JSONObject(responseSpotify.toString()).getInt("popularity");
            double youtubeRating = ScraperUtil.getPopularity(songToInsert.getYoutubeMediaURL());
            double rating = spotifyRating * 0.7 + youtubeRating * 0.3;

            songToInsert.setRating(rating);

            try{
                songAlbum.setImage(song.getJSONObject("album").getString("cover_art_url"));
            } catch (JSONException e) {
                songAlbum.setImage("");
            }

            try{
                songAlbum.setTitle(song.getJSONObject("album").getString("full_title"));
            } catch (JSONException e) {
            }

            System.out.format("Response:-\tAlbum: %s\tTitle: %s\tindex: %s\n\n", songAlbum.getImage(), songToInsert.getTitle(), i);

            songToInsert.setAlbum(songAlbum);
            songToInsert.setGenre(ScraperUtil.getGenre(songToInsert.getGeniusMediaURL()));
            JSONArray featuredArtists;
            try{
                featuredArtists = song.getJSONArray("featured_artists");
                if(featuredArtists.length() != 0) {
                    for (int iter = 0; iter < featuredArtists.length(); iter++)
                        artists.add(featuredArtists.getJSONObject(iter).getString("name"));

                    songToInsert.setFeaturedArtists(artists);
                }
            } catch (JSONException e) {
            }

            try{
                int year = Integer.parseInt(song.getString("release_date").split("-")[0]);
                songToInsert.setReleaseYear(year);
            } catch (JSONException e) {
            }

            new SongDAOImpl().createSong(songToInsert);

        }
        System.out.format("Missed mandatory field: %d\tMissed Url: %s\tIndex: %d", miss, noResponse, i);
    }


    /**
     * @param inputUrl
     * @param bearer
     * @return The response from the given API, specified by inputUrl, using the bearer to authentication.
     */
    private static StringBuffer getResponse(String inputUrl, String bearer){
        StringBuffer response = new StringBuffer();
        try {
            URL url = new URL(inputUrl);
            HttpURLConnection conn = null;

            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("Authorization", "Bearer " + bearer);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestMethod("GET");

            if(conn.getResponseCode() == 200) {
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String output;

                while ((output = in.readLine()) != null)
                    response.append(output);

                in.close();
            }else {
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return response;
    }

}