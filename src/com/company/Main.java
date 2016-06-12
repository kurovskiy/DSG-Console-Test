package com.company;

import Controller.Downloader;
import Controller.Parser;
import Model.*;

import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        ArrayList<String> html = Downloader.downloadHTML("https://www.ris.bka.gv.at/GeltendeFassung.wxe?Abfrage=Bundesnormen&Gesetzesnummer=10001597");
        Document document = Parser.parseHTML(html);

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

        for (Paragraph paragraph : paragraphs) {
            System.out.println("Paragraph " + paragraph.getNumber() + ": " + paragraph.getName());
            if (paragraph.getText() != null) {
                System.out.println("Text: " + paragraph.getText());
                if (paragraph.getItems().size() > 0) {
                    for (Text text : paragraph.getItems()) {
                        System.out.println("\t\tZiffer " + ((Digit)text).getNumber() + ": " + ((Digit)text).getText());
                    }
                }
            }
            else {
                for (Text text : paragraph.getItems()) {
                    System.out.println("\tAbsatz " + ((Indent)text).getNumber() + ": " + ((Indent)text).getText());
                    if (((Indent)text).getItems() != null) {
                        for (Digit digit : ((Indent) text).getItems()) {
                            System.out.println("\t\tZiffer " + digit.getNumber() + ": " + digit.getText());
                        }
                    }
                }
            }
            System.out.println();
        }
    }
}