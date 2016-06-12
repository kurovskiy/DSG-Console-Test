package Model;

import java.util.ArrayList;
import java.util.Date;

public class Paragraph extends Text{
	private ArrayList<Text> items;
	private  ArrayList<Text> links;
	private String name;
	private String text;
	//private SuffixTree suffixTree;

	protected Paragraph(){}

	public Paragraph(Date date, String number, ArrayList<Text> items, ArrayList<Text> links, String name, String text) {
		super(date, number);
		this.items = items;
		this.links = links;
		this.name = name;
		this.text = text;
	}

	public ArrayList<Text> getItems() {
		return items;
	}
	public void setItems(ArrayList<Text> items) {
		this.items = items;
	}
	public ArrayList<Text> getLinks() {
		return links;
	}
	public void setLinks(ArrayList<Text> links) {
		this.links = links;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
}