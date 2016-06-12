package Model;

import java.util.Date;

public abstract class Text {
	private Date date;
	private String number;
	
	protected Text(){}

	public Text (Date date, String number){
		this.date = date;
		this.number = number;
	}
	
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public String getNumber() {
		return number;
	}
	public void setNumber(String number) {
		this.number = number;
	}
}