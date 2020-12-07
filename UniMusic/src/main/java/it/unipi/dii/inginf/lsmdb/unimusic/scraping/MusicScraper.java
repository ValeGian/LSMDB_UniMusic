package it.unipi.dii.inginf.lsmdb.unimusic.scraping;

import com.mongodb.client.*;
import it.unipi.dii.inginf.lsmdb.unimusic.middleware.dao.SongDAOImpl;
import it.unipi.dii.inginf.lsmdb.unimusic.middleware.entities.Album;
import it.unipi.dii.inginf.lsmdb.unimusic.middleware.entities.Song;
import it.unipi.dii.inginf.lsmdb.unimusic.middleware.exception.ActionNotCompletedException;
import it.unipi.dii.inginf.lsmdb.unimusic.middleware.persistence.mongoconnection.MongoDriver;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.json.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

public class MusicScraper {

    public static void main(String[] args) throws ActionNotCompletedException {

        int miss = 0;
        int noResponse = 0;
        int i;

        for(i = 1; i < 1000000; i++) {

            StringBuffer responseGenius = getResponse("https://api.genius.com/songs/" + i, " Yfr3zMge1KSmUXSrHkp9BeT8nxcm_kPfJUUa4TvrNyjjL2HHKLPS88Atx1mfdPLr");
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

            StringBuffer responseSpotify = getResponse("https://api.spotify.com/v1/tracks/" + uriSpotify, " BQC2Eg4JOJkOxa2yFAeI-mJ9NjRN2V-RTgIExEqxIEYgmcmwIKWzZp_71BVSB1jWIaf-R_WDUgM5fMg0N4D6QzzLAgMvd-o_pbVI873cYERYYeP5ikJjqahNaorFPfrDNwIRIdydKBjZNQIuOPitp1mntphbf_lRhK40f45POA");
            if(responseSpotify == null) {
                System.out.println("Spotify response missed, check the spotify bearer if the problem persists!");
                miss++;
                continue;
            }

            songToInsert.setID(new ObjectId().toString());

            songToInsert.setRating(new JSONObject(responseSpotify.toString()).getInt("popularity"));

            try{
                songAlbum.setImageURL(song.getJSONObject("album").getString("cover_art_url"));
            } catch (JSONException e) {
                songAlbum.setImageURL("");
            }

            try{
                songAlbum.setTitle(song.getJSONObject("album").getString("full_title"));
            } catch (JSONException e) {
            }

            System.out.format("Response:-\tAlbum: %s\tTitle: %s\tindex: %s\n\n", songAlbum.getImageURL(), songToInsert.getTitle(), i);

            songToInsert.setAlbum(songAlbum);
            songToInsert.setGenre("Unknown"); //            songToInsert.setGenre(getGenre);
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