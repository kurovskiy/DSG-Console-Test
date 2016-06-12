package Model;

import java.util.ArrayList;
import java.util.Date;

public class Document {
	private Date date;
	private static Document instance = null;
	private ArrayList<Article> items;
	
	protected Document() {}

	public static Document getInstance() {
		if (instance == null)
			instance = new Document();
		return instance;
	}

	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public ArrayList<Article> getItems() {
		return items;
	}
	public void setItems(ArrayList<Article> items) {
		this.items = items;
	}
}