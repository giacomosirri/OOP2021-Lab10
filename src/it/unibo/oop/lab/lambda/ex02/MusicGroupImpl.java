package it.unibo.oop.lab.lambda.ex02;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.Set;
import java.util.stream.DoubleStream;
import java.util.stream.Stream;

/**
 * Class that implements a {@link it.unibo.oop.lab.lambda.ex02.MusicGroup.java}.
 * 
 */
public final class MusicGroupImpl implements MusicGroup {

    private final Map<String, Integer> albums = new HashMap<>();
    private final Set<Song> songs = new HashSet<>();

    @Override
    public void addAlbum(final String albumName, final int year) {
        this.albums.put(albumName, year);
    }

    @Override
    public void addSong(final String songName, final Optional<String> albumName, final double duration) {
        if (albumName.isPresent() && !this.albums.containsKey(albumName.get())) {
            throw new IllegalArgumentException("invalid album name");
        }
        this.songs.add(new MusicGroupImpl.Song(songName, albumName, duration));
    }

    @Override
    public Stream<String> orderedSongNames() {
        return this.songs.stream()
        	    .map(i -> i.getSongName())
        		.sorted();
    }

    @Override
    public Stream<String> albumNames() {
        return this.albums.keySet().stream();
    }

    @Override
    public Stream<String> albumInYear(final int year) {
       return this.albums.entrySet().stream()
    		   .filter(i -> i.getValue() == year)
    		   .map(i -> i.getKey());
    }

    @Override
    public int countSongs(final String albumName) {
        return (int) this.songs.stream()
        		.filter(i -> i.getAlbumName().isPresent())
        		.filter(i -> albumName.equals(i.getAlbumName().get()))
        		.count();
    }

    @Override
    public int countSongsInNoAlbum() {
    	return (int) this.songs.stream()
    			.map(i -> i.getAlbumName())
    			.filter(i -> i.isEmpty())
    			.count();
    }

    @Override
    public OptionalDouble averageDurationOfSongs(final String albumName) {
    	return OptionalDouble.of(this.albumDuration(albumName).orElse(0) / this.countSongs(albumName));
    }

    private OptionalDouble albumDuration(final String albumName) {
    	return OptionalDouble.of(songs.stream()
        		.filter(i -> albumName.equals(i.getAlbumName().orElse(null)))
        		.flatMapToDouble(i -> DoubleStream.builder().add(i.getDuration()).build())
        		.sum());
    }

    @Override
    public Optional<String> longestSong() {
        return Optional.of(this.songs.stream()
        		.max((x, y) -> Double.compare(x.getDuration(), y.getDuration()))
        		.get().getSongName());
    }

    @Override
    public Optional<String> longestAlbum() {
        return this.albums.keySet().stream()
        		.max((x, y) -> Double.compare(this.albumDuration(x).orElse(0), this.albumDuration(y).orElse(0)));
    }

    private static final class Song {

        private final String songName;
        private final Optional<String> albumName;
        private final double duration;
        private int hash;

        Song(final String name, final Optional<String> album, final double len) {
            super();
            this.songName = name;
            this.albumName = album;
            this.duration = len;
        }

        public String getSongName() {
            return songName;
        }

        public Optional<String> getAlbumName() {
            return albumName;
        }

        public double getDuration() {
            return duration;
        }

        @Override
        public int hashCode() {
            if (hash == 0) {
                hash = songName.hashCode() ^ albumName.hashCode() ^ Double.hashCode(duration);
            }
            return hash;
        }

        @Override
        public boolean equals(final Object obj) {
            if (obj instanceof Song) {
                final Song other = (Song) obj;
                return albumName.equals(other.albumName) && songName.equals(other.songName)
                        && duration == other.duration;
            }
            return false;
        }

        @Override
        public String toString() {
            return "Song [songName=" + songName + ", albumName=" + albumName + ", duration=" + duration + "]";
        }

    }

}
