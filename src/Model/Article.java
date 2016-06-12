package Model;

import java.util.ArrayList;
import java.util.Date;

public class Article extends Text {
	private ArrayList<Text> items;

	protected Article(){}

	public Article(Date date, String number, ArrayList<Text> items) {
		super(date, number);
		this.items = items;
	}

	public ArrayList<Text> getItems() {
		return items;
	}
	public void setItems(ArrayList<Text> items) {
		this.items = items;
	}
}