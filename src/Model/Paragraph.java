package Model;

import java.util.ArrayList;
import java.util.Date;

public class Paragraph extends Text{
	private ArrayList<Text> items;
	private  ArrayList<Text> links;
	private String name;
	private String text;
	private SuffixTree suffixTree;

	protected Paragraph(){}

	public Paragraph(Date date, String number, ArrayList<Text> items, ArrayList<Text> links, String name, String text) {
		super(date, number);
		this.items = items;
		this.links = links;
		this.name = name;
		this.text = text;
		createSuffixTree();
	}

	protected void createSuffixTree() {
		boolean hasText = this.text != null;
		String suffixSource = "";
		for (Text source : this.items) {
			if (source.getClass().getName().equals("Model.Indent")) {
				suffixSource += " " + ((Indent)source).getText().toLowerCase();
			}
			else if (source.getClass().getName().equals("Model.Digit")) {
				suffixSource += " " + ((Digit)source).getText().toLowerCase();
			}
		}
		suffixSource += "$";
		if (!hasText) {
			suffixSource = suffixSource.substring(1);
		}
		else {
			suffixSource = this.text.toLowerCase() + suffixSource;
		}
		this.suffixTree = new SuffixTree(suffixSource);
	}

	public SuffixTree getSuffixTree() { return suffixTree; }
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