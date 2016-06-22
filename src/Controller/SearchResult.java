package Controller;

import Model.Paragraph;

import java.util.ArrayList;

public class SearchResult {
    private ArrayList<Paragraph> paragraphs;

    public SearchResult(ArrayList<Paragraph> paragraphs) {
        this.paragraphs = paragraphs;
    }

    public ArrayList<Paragraph> getParagraphs() { return paragraphs; }
}