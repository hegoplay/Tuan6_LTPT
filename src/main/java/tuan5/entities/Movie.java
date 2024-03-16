package tuan5.entities;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;

public class Movie {
	private ObjectId id;
	private String plot;
	private List<String> genres = new ArrayList<>();
	private int runtime;
	private List<String> cast = new ArrayList<>();;
	private String title;
	private String fullplot;
	private List<String> language = new ArrayList<>();
	private Date released;
	private List<String> directors = new ArrayList<>();
	private List<String> writers = new ArrayList<>();
	private Award awards;
	private String lastupdated;
	private int year;
	private Imdb imdb;
	private List<String> countries;
	private String type;
	public Movie() {
	}
	public Movie(ObjectId id, String plot, List<String> genres, int runtime, List<String> cast, String title,
			String fullplot, List<String> language, Date released, List<String> directors, List<String> writers,
			Award awards, String lastupdated, int year, Imdb imdb, List<String> countries, String type) {
		super();
		this.id = id;
		this.plot = plot;
		this.genres = genres;
		this.runtime = runtime;
		this.cast = cast;
		this.title = title;
		this.fullplot = fullplot;
		this.language = language;
		this.released = released;
		this.directors = directors;
		this.writers = writers;
		this.awards = awards;
		this.lastupdated = lastupdated;
		this.year = year;
		this.imdb = imdb;
		this.countries = countries;
		this.type = type;
	}
	public ObjectId getId() {
		return id;
	}
	public void setId(ObjectId id) {
		this.id = id;
	}
	public String getPlot() {
		return plot;
	}
	public void setPlot(String plot) {
		this.plot = plot;
	}
	public List<String> getGenres() {
		return genres;
	}
	public void setGenres(List<String> genres) {
		this.genres = genres;
	}
	public int getRuntime() {
		return runtime;
	}
	public void setRuntime(int runtime) {
		this.runtime = runtime;
	}
	public List<String> getCast() {
		return cast;
	}
	public void setCast(List<String> cast) {
		this.cast = cast;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getFullplot() {
		return fullplot;
	}
	public void setFullplot(String fullplot) {
		this.fullplot = fullplot;
	}
	public List<String> getLanguage() {
		return language;
	}
	public void setLanguage(List<String> language) {
		this.language = language;
	}
	public Date getReleased() {
		return released;
	}
	public void setReleased(Date released) {
		this.released = released;
	}
	public List<String> getDirectors() {
		return directors;
	}
	public void setDirectors(List<String> directors) {
		this.directors = directors;
	}
	public List<String> getWriters() {
		return writers;
	}
	public void setWriters(List<String> writers) {
		this.writers = writers;
	}
	public Award getAwards() {
		return awards;
	}
	public void setAwards(Award awards) {
		this.awards = awards;
	}
	public String getLastupdated() {
		return lastupdated;
	}
	public void setLastupdated(String lastupdated) {
		this.lastupdated = lastupdated;
	}
	public int getYear() {
		return year;
	}
	public void setYear(int year) {
		this.year = year;
	}
	public Imdb getImdb() {
		return imdb;
	}
	public void setImdb(Imdb imdb) {
		this.imdb = imdb;
	}
	public List<String> getCountries() {
		return countries;
	}
	public void setCountries(List<String> countries) {
		this.countries = countries;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	@Override
	public String toString() {
		return "Movies [id=" + id + ", plot=" + plot + ", genres=" + genres + ", runtime=" + runtime + ", cast=" + cast
				+ ", title=" + title + ", fullplot=" + fullplot + ", language=" + language + ", released=" + released
				+ ", directors=" + directors + ", writers=" + writers + ", awards=" + awards + ", lastupdated="
				+ lastupdated + ", year=" + year + ", imdb=" + imdb + ", countries=" + countries + ", type=" + type
				+ "]";
	}
	
}
