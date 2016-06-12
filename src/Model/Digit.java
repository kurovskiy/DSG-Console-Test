package Model;

import java.util.ArrayList;
import java.util.Date;

public class Digit extends Text {
	private ArrayList<Text> links;
	private String text;

	protected Digit() {}

	public Digit(Date date, String number, ArrayList<Text> links, String text) {
		super(date, number);
		this.links = links;
		this.text = text;
	}

	public ArrayList<Text> getLinks() {
		return links;
	}
	public void setLinks(ArrayList<Text> links) {
		this.links = links;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
}