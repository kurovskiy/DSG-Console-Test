package Controller;

import Model.Paragraph;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Search {
    private static Search instance = null;
    private ArrayList<Paragraph> paragraphs;

    private class searchTask implements Callable {
        Paragraph paragraph = null;
        String searchString = null;
        boolean mustMatch;

        private searchTask(Paragraph paragraph, String searchString, boolean mustMatch) {
            this.paragraph = paragraph;
            this.searchString = searchString;
            this.mustMatch = mustMatch;
        }

        public Paragraph call() {
            if (paragraph.getSuffixTree().checkForSubString(searchString) == mustMatch)
                return paragraph;
            else
                return null;
        }
    }

    private class SearchQuery {
        private final static String AND_OPERATOR = " und ";
        private final static String OR_OPERATOR = " oder ";
        private final static String NOT_OPERATOR = "nicht ";
        private String searchString;

        private class Operation {
            private String operationString;
            private Set<Paragraph> searchResults;

            private Operation(String operationString) {
                this.operationString = operationString;
                this.searchResults = new HashSet<Paragraph>();
            }
        }

        private SearchQuery(String searchString) {
            this.searchString = searchString;
        }

        private ArrayList<Paragraph> parseSearchString(String string) {
            ArrayList<ArrayList<Operation>> matches = new ArrayList<ArrayList<Operation>>();
            int bracketsDepth = -1;
            String regex = "(.+?)";
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(string);
            while (matcher.find()) {
                regex = ".*[(]" + regex + "[)].*";
                bracketsDepth++;
                pattern = Pattern.compile(regex);
                matcher = pattern.matcher(string);
            }
            for (int i = 0; i <= bracketsDepth; i++)
                matches.add(new ArrayList<Operation>());
            for (int i = bracketsDepth; i > 0; i--) {
                regex = regex.substring(5, regex.length() - 5);
                pattern = Pattern.compile(regex);
                matcher = pattern.matcher(string);
                int start = 0, end = 0;
                for (int j = 0; matcher.find(); j++) {
                    matches.get(i).add(new Operation(matcher.group(1)));
                    if (matcher.start(1) == 0) {
                        if (matcher.end(1) == string.length() - 1)
                            string = "$" + i + "$" + j;
                        else
                            string = "$" + i + "$" + j + string.substring(matcher.end(1) + 1, string.length());
                    } else {
                        if (matcher.end(1) == string.length())
                            string = string.substring(0, matcher.start(1) - 1) + "$" + i + "$" + j;
                        else
                            string = string.substring(0, matcher.start(1) - 1) + "$" + i + "$" + j + string.substring(matcher.end(1) + 1, string.length());
                    }
                    matcher = pattern.matcher(string);
                }
            }
            matches.get(0).add(new Operation(string));
            string = "$0$0";
            for (int m = matches.size() - 1; m >= 0; m--)
                for (Operation subMatch : matches.get(m)) {
                    String[] orOperands = subMatch.operationString.toLowerCase().split(OR_OPERATOR);
                    for (String orOperand : orOperands) {
                        String[] andOperands = orOperand.split(AND_OPERATOR);
                        ArrayList<ArrayList<Paragraph>> andVariables = new ArrayList<ArrayList<Paragraph>>();
                        for (String andOperand : andOperands) {
                            ArrayList<Paragraph> variable = paragraphs;
                            String[] notOperands = andOperand.split(NOT_OPERATOR);
                            if (notOperands.length > 1) {
                                if (notOperands[0].length() > 0) {
                                    for (int i = 0; i < notOperands.length - 1; i++)
                                        notOperands[i] = notOperands[i].substring(0, notOperands[i].length() - 1);
                                    try {
                                        variable = search(variable, notOperands[1], false);
                                        variable = search(variable, notOperands[0], true);
                                    } catch (Exception e) {
                                    }
                                } else {
                                    try {
                                        if (notOperands[1].startsWith("$")) {
                                            String[] split = notOperands[1].split("\\$");
                                            ArrayList<Paragraph> temp = new ArrayList<Paragraph>();
                                            temp.addAll(matches.get(Integer.parseInt(split[1])).get(Integer.parseInt(split[2])).searchResults);
                                            variable = temp;
                                        }
                                        else
                                            variable = search(variable, notOperands[1], false);
                                    } catch (Exception e) {
                                    }
                                }
                            } else if (notOperands.length == 1) {
                                try {
                                    if (notOperands[0].startsWith("$")) {
                                        String[] split = notOperands[0].split("\\$");
                                        ArrayList<Paragraph> temp = new ArrayList<Paragraph>();
                                        temp.addAll(matches.get(Integer.parseInt(split[1])).get(Integer.parseInt(split[2])).searchResults);
                                        variable = temp;
                                    }
                                    else
                                        variable = search(variable, notOperands[0], true);

                                } catch (Exception e) {
                                    System.out.println(e.getMessage());
                                }
                            }
                            andVariables.add(variable);
                        }
                        ArrayList<Paragraph> orOperandResult = andVariables.get(0);
                        for (int i = 1; i < andVariables.size(); i++)
                            orOperandResult.retainAll(andVariables.get(i));
                        subMatch.searchResults.addAll(orOperandResult);
                    }
                }
            ArrayList<Paragraph> result = new ArrayList<Paragraph>();
            result.addAll(matches.get(0).get(0).searchResults);
            return result;
        }
    }

    protected Search() {}

    public static Search getInstance() {
        if (instance == null)
            instance = new Search();
        return instance;
    }

    public SearchResult search(String query) {
        SearchQuery searchQuery = new SearchQuery(query);
        return new SearchResult(searchQuery.parseSearchString(query));
    }

    private ArrayList<Paragraph> search(ArrayList<Paragraph> dataSource, String word, boolean mustMatch) throws Exception {
        ArrayList<Paragraph> matched = new ArrayList<Paragraph>();
        ExecutorService exec = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        ArrayList<Callable<Paragraph>> tasks = new ArrayList<Callable<Paragraph>>();
        for (Paragraph paragraph : dataSource)
            tasks.add(new searchTask(paragraph, word, mustMatch));
        List<Future<Paragraph>> futures = exec.invokeAll(tasks);
        exec.shutdown();
        for (Future<Paragraph> future : futures) {
            if (!future.isCancelled())
                matched.add(future.get());
        }
        matched.remove(null);
        return matched;
    }

    private ArrayList<Paragraph> search(String firstWord, String secondWord, boolean isBounded) throws Exception {
        ArrayList<Paragraph> matched = search(paragraphs, firstWord, true);
        if (isBounded)
            return search(matched, secondWord, true);
        matched.addAll(search(paragraphs, secondWord, true));
        return matched;
    }

    public ArrayList<Paragraph> getParagraphs() { return paragraphs; }
    public void setParagraphs(ArrayList<Paragraph> paragraphs) { this.paragraphs = paragraphs; }
}