package tuan5.entities;

public class Imdb {
	private float rating;
	private int votes;
	private int id;
	public Imdb() {
		super();
	}
	public Imdb(float rating, int votes, int id) {
		super();
		this.rating = rating;
		this.votes = votes;
		this.id = id;
	}
	public float getRating() {
		return rating;
	}
	public void setRating(float rating) {
		this.rating = rating;
	}
	public int getVotes() {
		return votes;
	}
	public void setVotes(int votes) {
		this.votes = votes;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	@Override
	public String toString() {
		return "Imdb [rating=" + rating + ", votes=" + votes + ", id=" + id + "]";
	}
	
}
