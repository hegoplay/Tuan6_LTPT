package tuan5.entities;

public class Award {
	private int wins;
	private int nominations;
	private String text;
	public Award() {
	}
	public Award(int wins, int nominations, String text) {
		super();
		this.wins = wins;
		this.nominations = nominations;
		this.text = text;
	}
	public int getWins() {
		return wins;
	}
	public void setWins(int wins) {
		this.wins = wins;
	}
	public int getNominations() {
		return nominations;
	}
	public void setNominations(int nominations) {
		this.nominations = nominations;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	@Override
	public String toString() {
		return "Award [wins=" + wins + ", nominations=" + nominations + ", text=" + text + "]";
	}
	
}
