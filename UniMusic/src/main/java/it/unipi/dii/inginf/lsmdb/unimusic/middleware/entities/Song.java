package it.unipi.dii.inginf.lsmdb.unimusic.middleware.entities;

public class Song {
    private long ID;
    private String title;
    private String album;
    private String albumImageURL;
    private String artist;
    private String[] featuredArtists;
    private int releaseYear;
    private double rating;
    private int likeCount;
    private String youtubeMediaURL;
    private String spotifyMediaURL;
    private String geniusMediaURL;

    public Song() {

    }

    public Song(long ID,
                String album,
                String albumImageURL,
                String artist,
                String[] featuredArtists,
                int releaseYear,
                double rating,
                int likeCount,
                String youtubeMediaURL,
                String spotifyMediaURL,
                String geniusMediaURL) {
        this.ID = ID;
        this.album = album;
        this.albumImageURL = albumImageURL;
        this.artist = artist;
        this.featuredArtists = featuredArtists;
        this.releaseYear = releaseYear;
        this.rating = rating;
        this.likeCount = likeCount;
        this.youtubeMediaURL = youtubeMediaURL;
        this.spotifyMediaURL = spotifyMediaURL;
        this.geniusMediaURL = geniusMediaURL;
    }

    public long getID() {
        return ID;
    }

    public void setID(long ID) {
        this.ID = ID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getAlbumImageURL() {
        return albumImageURL;
    }

    public void setAlbumImageURL(String albumImageURL) {
        this.albumImageURL = albumImageURL;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String[] getFeaturedArtists() {
        return featuredArtists;
    }

    public void setFeaturedArtists(String[] featuredArtists) {
        this.featuredArtists = featuredArtists;
    }

    public int getReleaseYear() {
        return releaseYear;
    }

    public void setReleaseYear(int releaseYear) {
        this.releaseYear = releaseYear;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    public String getYoutubeMediaURL() {
        return youtubeMediaURL;
    }

    public void setYoutubeMediaURL(String youtubeMediaURL) {
        this.youtubeMediaURL = youtubeMediaURL;
    }

    public String getSpotifyMediaURL() {
        return spotifyMediaURL;
    }

    public void setSpotifyMediaURL(String spotifyMediaURL) {
        this.spotifyMediaURL = spotifyMediaURL;
    }

    public String getGeniusMediaURL() {
        return geniusMediaURL;
    }

    public void setGeniusMediaURL(String geniusMediaURL) {
        this.geniusMediaURL = geniusMediaURL;
    }
}
