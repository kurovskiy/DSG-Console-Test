package com.company;

import Controller.Downloader;
import Controller.Parser;
import Model.*;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        String s = "Soweit die Verwendung von personenbezogenen Daten nicht im lebenswichtigen Interesse des Betroffenen oder mit seiner Zustimmung erfolgt, sind Beschränkungen des Anspruchs auf Geheimhaltung nur zur Wahrung überwiegender berechtigter Interessen eines anderen zulässig, und zwar bei Eingriffen einer staatlichen Behörde nur auf Grund von Gesetzen, die aus den in Art. 8 Abs. 2 der Europäischen Konvention zum Schutze der Menschenrechte und Grundfreiheiten (EMRK), BGBl. Nr. 210/1958, genannten Gründen notwendig sind. Derartige Gesetze dürfen die Verwendung von Daten, die ihrer Art nach besonders schutzwürdig sind, nur zur Wahrung wichtiger öffentlicher Interessen vorsehen und müssen gleichzeitig angemessene Garantien für den Schutz der Geheimhaltungsinteressen der Betroffenen festlegen. Auch im Falle zulässiger Beschränkungen darf der Eingriff in das Grundrecht jeweils nur in der gelindesten, zum Ziel führenden Art vorgenommen werden.";
        SuffixTree st = new SuffixTree(s.toLowerCase());
        System.out.println(st.checkForSubString("personenb"));










        /* ArrayList<String> html = Downloader.downloadHTML("https://www.ris.bka.gv.at/GeltendeFassung.wxe?Abfrage=Bundesnormen&Gesetzesnummer=10001597");
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
        } */










        /* String s = "THIS IS A TEST TEXT$";
        SuffixTree st = new SuffixTree(s);
        System.out.println(st.checkForSubString("TEST"));
        System.out.println(st.checkForSubString("A"));
        System.out.println(st.checkForSubString(" "));
        System.out.println(st.checkForSubString("IS A"));
        System.out.println(st.checkForSubString(" IS A "));
        System.out.println(st.checkForSubString("TEST1"));
        System.out.println(st.checkForSubString("THIS IS GOOD"));
        System.out.println(st.checkForSubString("TES"));
        System.out.println(st.checkForSubString("TESA"));
        System.out.println(st.checkForSubString("ISB"));
        System.out.println(st.checkForSubString("THIS TEXT")); */

        /* ArrayList<String> html = Downloader.downloadHTML("https://www.ris.bka.gv.at/GeltendeFassung.wxe?Abfrage=Bundesnormen&Gesetzesnummer=10001597");
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
        } */
    }
}