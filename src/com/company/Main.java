package com.company;

import Controller.*;
import Model.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class Main {
    private final static String url = "https://www.ris.bka.gv.at/GeltendeFassung.wxe?Abfrage=Bundesnormen&Gesetzesnummer=10001597";

    public static void main(String[] args) {
        Document document = createDocument();
        System.out.println("[ - The document is ready ...]\n\n");
        ArrayList<Paragraph> paragraphs = new ArrayList<Paragraph>();
        for (Article article : document.getItems()) {
            for (Text text : article.getItems()) {
                if (text.getClass().getName().equals("Model.Section")) {
                    ArrayList<Paragraph> sectionParagraphs = ((Section)text).getItems();
                    for (Paragraph paragraph : sectionParagraphs) {
                        paragraphs.add(paragraph);
                    }
                }
                else if (text.getClass().getName().equals("Model.Paragraph")) {
                    paragraphs.add((Paragraph)text);
                }
            }
        }
        paragraphs.remove(paragraphs.size() - 1);
        Search.getInstance().setParagraphs(paragraphs);
        menu();
    }

    public static Document createDocument() {
        System.out.println("[ - Downloading HTML-document ...]");
        ArrayList<String> html = Downloader.downloadHTML(url);
        System.out.println("[ - Creating data structure ...]");
        return Parser.parseHTML(html);
    }

    public static void menu() {
        while (true) {
            System.out.print("Please, enter the search string: ");
            String string = "";
            BufferedReader bufferRead = new BufferedReader(new InputStreamReader(System.in));
            try {
                string = bufferRead.readLine();
            }
            catch (Exception e) {}
            if (string.length() > 0) {
                SearchResult result = Search.getInstance().search(string);
                System.out.println("Found in following paragraphs: ");
                for (Paragraph paragraph : result.getParagraphs())
                    if (paragraph != null)
                        System.out.println(" - Paragraph " + paragraph.getNumber());
            }
            try {
                bufferRead.readLine();
                Runtime.getRuntime().exec("cls");
            }
            catch (Exception e) {}
        }
    }
}