package Controller;

import Model.*;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Parser {
    public static Document parseHTML(ArrayList<String> html) {
        Document document = Document.getInstance();
        document.setItems(parse(html));
        return document;
    }

    private static ArrayList<Article> parse(ArrayList<String> html) {
        ArrayList<Article> articles = new ArrayList<Article>();
        ArrayList<Integer> breaks = new ArrayList<Integer>();
        String articleBreak = "<p class=\"UeberschrG1 AlignCenter\">Artikel";
        for (int i = 0; i < html.size(); i++) {
            if (html.get(i).contains(articleBreak))
                breaks.add(i);
        }
        for (int i = 0; i < breaks.size(); i++) {
            ArrayList<String> articleStrings = new ArrayList<String>();
            int articleBegin = breaks.get(i);
            if (i < breaks.size() - 1) {
                int articleEnd = breaks.get(i + 1);
                for (int j = articleBegin; j < articleEnd; j++) {
                    if (html.get(j).contains("<div><div class=\"contentBlock\">")) {
                        articleStrings.add(html.get(j));
                    }
                }
            }
            else {
                int articleEnd = html.size() - 1;
                for (int j = articleBegin; j <= articleEnd; j++) {
                    if (html.get(j).contains("<div><div class=\"contentBlock\">")) {
                        articleStrings.add(html.get(j));
                    }
                }
            }
            articles.add(createArticle(articleStrings));
        }
        return articles;
    }

    private static Article createArticle(ArrayList<String> html) {
        String articleNumber = null;
        Pattern pattern = Pattern.compile("<p class=\"UeberschrG1 AlignCenter\">Artikel&#160;(.*?)</p>");
        Matcher matcher = pattern.matcher(html.get(0));
        if (matcher.find()) {
            articleNumber = matcher.group(1);
        }
        ArrayList<Text> articleItems = new ArrayList<Text>();
        ArrayList<Integer> breaks = new ArrayList<Integer>();
        String sectionBreak1 = ". Abschnitt</p>";
        String sectionBreak2 = ".&#160;Abschnitt</p>";
        for (int i = 0; i < html.size(); i++) {
            if (html.get(i).contains(sectionBreak1) || html.get(i).contains(sectionBreak2))
                breaks.add(i);
        }
        if (breaks.isEmpty()) {
            for (int i = 0; i < html.size(); i++) {
                articleItems.add(createParagraph(html.get(i)));
            }
        }
        else {
            for (int i = 0; i < breaks.get(0); i++) {
                articleItems.add(createParagraph(html.get(i)));
            }
            for (int i = 0; i < breaks.size(); i++) {
                ArrayList<String> sectionStrings = new ArrayList<String>();
                int sectionBegin = breaks.get(i);
                if (i < breaks.size() - 1) {
                    int sectionEnd = breaks.get(i + 1);
                    for (int j = sectionBegin; j < sectionEnd; j++) {
                        sectionStrings.add(html.get(j));
                    }
                } else {
                    int articleEnd = html.size() - 1;
                    for (int j = sectionBegin; j <= articleEnd; j++) {
                        sectionStrings.add(html.get(j));
                    }
                }
                articleItems.add(createSection(sectionStrings));
            }
        }
        return new Article(new Date(), articleNumber, articleItems);
    }

    private static Section createSection(ArrayList<String> html) {
        String sectionNumber = null;
        Pattern pattern = Pattern.compile("</p><p class=\"UeberschrG2 AlignCenter\">([0-9])+. Abschnitt</p>");
        Matcher matcher = pattern.matcher(html.get(0));
        if (matcher.find()) {
            sectionNumber = matcher.group(1);
        }
        else {
            pattern = Pattern.compile("<div class=\"contentBlock\"><p class=\"UeberschrG1 AlignCenter\">([0-9])+. Abschnitt</p>");
            matcher = pattern.matcher(html.get(0));
            if (matcher.find()) {
                sectionNumber = matcher.group(1);
            }
        }
        String sectionName = null;
        pattern = Pattern.compile("Abschnitt</p><p class=\"UeberschrG[1-2][-]? AlignCenter\">(.*?)</p>");
        matcher = pattern.matcher(html.get(0));
        if (matcher.find()) {
            sectionName = matcher.group(1);
        }
        ArrayList<Paragraph> sectionParagraphs = new ArrayList<Paragraph>();
        for (int i = 0; i < html.size(); i++) {
            sectionParagraphs.add(createParagraph(html.get(i)));
        }
        return new Section(new Date(), sectionNumber, sectionParagraphs, sectionName);
    }

    private static Paragraph createParagraph(String html) {
        String paragraphNumber = null;
        Pattern pattern = Pattern.compile("<span class=\"GldSymbol\">§&#160;(.*?).</span>");
        Matcher matcher = pattern.matcher(html);
        if (matcher.find()) {
            paragraphNumber = matcher.group(1);
        }
        String paragraphName = null;
        pattern = Pattern.compile("<p class=\"UeberschrPara AlignCenter\">(.*?)</p>");
        matcher = pattern.matcher(html);
        if (matcher.find()) {
            paragraphName = matcher.group(1);
        }
        String paragraphText = null;
        ArrayList<Text> paragraphItems = new ArrayList<Text>();
        pattern = Pattern.compile("<span class=\"GldSymbol\">§&#160;(.*).</span> [(]([0-9]+)[)] (.*?)</p>");
        matcher = pattern.matcher(html);
        if (!matcher.find()) {
            pattern = Pattern.compile("<span class=\"GldSymbol\">§&#160;(.*).</span> <span class=\"Kursiv\">(.*?)</span>");
            matcher = pattern.matcher(html);
            if (!matcher.find()) {
                pattern = Pattern.compile("<span class=\"GldSymbol\">§&#160;(.*).</span> (.*?)</p>");
                matcher = pattern.matcher(html);
                if (matcher.find()) {
                    paragraphText = matcher.group(2);
                    pattern = Pattern.compile("<table (.*?)</table>");
                    matcher = pattern.matcher(html);
                    if (matcher.find()) {
                        ArrayList<Digit> paragraphDigits = createDigits(matcher.group(1));
                        for (Digit digit : paragraphDigits) {
                            paragraphItems.add(digit);
                        }
                    }
                }
            }
            else {
                String indentNumber = "1";
                String indentText = matcher.group(2);
                paragraphItems.add(new Indent(new Date(), indentNumber, null, null, indentText));
                pattern = Pattern.compile("<p class=\"Abs AlignJustify\">[(]([0-9]+)[)] (.*?)</p>");
                matcher = pattern.matcher(html);
                while (matcher.find()) {
                    indentNumber = matcher.group(1);
                    indentText = matcher.group(2);
                    paragraphItems.add(new Indent(new Date(), indentNumber, null, null, indentText));
                }
            }
        }
        else {
            String indentNumber = matcher.group(2);
            String indentText = matcher.group(3);
            String indentDigits = null;
            pattern = Pattern.compile("<span class=\"GldSymbol\">§&#160;(.*).</span> [(]" + indentNumber + "[)] " + indentText + "</p><table (.*?)</table>");
            matcher = pattern.matcher(html);
            if (matcher.find()) {
                indentDigits = matcher.group(2);
            }
            paragraphItems.add(new Indent(new Date(), indentNumber, createDigits(indentDigits), null, indentText));
            pattern = Pattern.compile("<p class=\"Abs AlignJustify\">[(]([0-9]+)[)] (.*?)</p>");
            matcher = pattern.matcher(html);
            while (matcher.find()) {
                indentNumber = matcher.group(1);
                indentText = matcher.group(2);
                indentDigits = null;
                Pattern tablePattern = Pattern.compile("<p class=\"Abs AlignJustify\">[(]" + indentNumber + "[)] " + indentText + "</p><table (.*?)</table>");
                Matcher tableMatcher = tablePattern.matcher(html);
                if (tableMatcher.find()) {
                    indentDigits = tableMatcher.group(1);
                }
                paragraphItems.add(new Indent(new Date(), indentNumber, createDigits(indentDigits), null, indentText));
            }
        }
        return new Paragraph(new Date(), paragraphNumber, paragraphItems, null, paragraphName, paragraphText);
    }

    private static ArrayList<Digit> createDigits(String html) {
        if (html == null) {
            return null;
        }
        ArrayList<Digit> digits = new ArrayList<Digit>();
        String digitNumber = null;
        String digitText = null;
        Pattern pattern = Pattern.compile("<td class=\"AlignRight VAlignTop\" colspan=\"[0-9]+\"><p class=\"ZifferE1\">(.*?).</p></td>");
        Matcher matcher = pattern.matcher(html);
        while (matcher.find()) {
            digitNumber = matcher.group(1);
            Pattern textPattern = Pattern.compile("<td class=\"AlignRight VAlignTop\" colspan=\"[0-9]+\"><p class=\"ZifferE1\">" + digitNumber + ".</p></td><td class=\"AlignJustify VAlignTop\" colspan=\"[0-9]+\"><p class=\"ZifferE1\">(.*?)</p>");
            Matcher textMatcher = textPattern.matcher(html);
            if (textMatcher.find()) {
                digitText = textMatcher.group(1);
            }
            digits.add(new Digit(new Date(), digitNumber, null, digitText));
        }
        return digits;
    }
}