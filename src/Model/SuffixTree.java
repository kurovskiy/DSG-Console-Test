package Model;

import java.util.*;

public class SuffixTree {
    private static final int MAXCHAR = 256;
    private char[] input = null;
    private Node root = null;

    private Node lastNewNode = null;
    private Node activeNode = null;

    private int activeEdge = -1;
    private int activeLength = 0;

    private int remainigsSuffixCount = 0;
    // private int leafEnd = -1;
    private End leafEnd = null;
    // ...
    private End rootEnd = null;
    private End splitEnd = null;
    private int size = -1;

    private class Node {
        private ArrayList<Node> childNodes = null;
        private Node suffixLink = null;
        private int start;
        private End end;
        private int suffixIndex;

        private Node(int start, End end) {
            this.childNodes = new ArrayList<Node>();
            for (int i = 0; i < MAXCHAR; i++) {
                childNodes.add(null);
            }
            this.suffixLink = root;
            this.start = start;
            this.end = end;
            this.suffixIndex = -1;
        }
    }

    private class End {
        private int value = 0;
        private End(int value) {
            this.value = value;
        }
    }

    public SuffixTree(String text) {
        size = text.length();
        input = text.toCharArray();
        rootEnd = new End(-1);
        root = new Node(-1, rootEnd);
        activeNode = root;
        for (int i = 0; i < size; i++) {
            extendSuffixTree(i);
        }
        int labelHeight = 0;
        setSuffixIndexByDFS(root, labelHeight);
    }

    private int edgeLength(Node node) {
        int start = node.start;
        int end = node.end.value;
        int le = leafEnd.value;
        if (node.equals(root)) {
            return 0;
        }
        return node.end.value - node.start + 1;
    }

    private boolean walkDown(Node node) {
        if (activeLength >= edgeLength(node)) {
            activeEdge += edgeLength(node);
            activeLength -= edgeLength(node);
            activeNode = node;
            return true;
        }
        return false;
    }

    private void extendSuffixTree(int pos) {
        // leafEnd = pos;
        if (leafEnd == null) {
            leafEnd = new End(pos);
        }
        else {
            leafEnd.value = pos;
        }
        // ...
        remainigsSuffixCount++;
        lastNewNode = null;
        while (remainigsSuffixCount > 0) {
            if (activeLength == 0) {
                activeEdge = pos;
            }
            if (activeNode.childNodes.get(input[activeEdge]) == null) {
                // activeNode.childNodes.set(input[activeEdge], new Node(pos, new End(leafEnd.value)));
                activeNode.childNodes.set(input[activeEdge], new Node(pos, leafEnd));
                // ...
                if (lastNewNode != null) {
                    lastNewNode.suffixLink = activeNode;
                    lastNewNode = null;
                }
            }
            else {
                Node next = activeNode.childNodes.get(input[activeEdge]);
                if (walkDown(next)) {
                    continue;
                }
                if (input[next.start + activeLength] == input[pos]) {
                    if (lastNewNode != null && activeNode != root) {
                        lastNewNode.suffixLink = activeNode;
                        lastNewNode = null;
                    }
                    activeLength++;
                    break;
                }
                /* if (splitEnd == null) {
                    splitEnd = new End(next.start + activeLength - 1);
                }
                else {
                    splitEnd.value = next.start + activeLength - 1;
                } */
                splitEnd = new End(next.start + activeLength - 1);
                Node split = new Node(next.start, splitEnd);
                activeNode.childNodes.set(input[activeEdge], split);
                // split.childNodes.set(input[pos], new Node(pos, new End(leafEnd)));
                split.childNodes.set(input[pos], new Node(pos, leafEnd));
                // ...
                next.start += activeLength;
                split.childNodes.set(input[next.start], next);
                if (lastNewNode != null) {
                    lastNewNode.suffixLink = split;
                }
                lastNewNode = split;
            }
            remainigsSuffixCount--;
            if (activeNode.equals(root) && activeLength > 0) {
                activeLength--;
                activeEdge = pos - remainigsSuffixCount + 1;
            }
            else if (activeNode != root) {
                activeNode = activeNode.suffixLink;
            }
        }
    }

    private void print(int i, int j) {
        for (int k = i; k <= j; k++) {
            System.out.print(input[k]);
        }
    }

    private void setSuffixIndexByDFS(Node node, int labelHeight) {
        if (node == null) {
            return;
        }
        if (node.start != -1) {
            // print(node.start, node.end.value); //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        }
        int leaf = 1;
        for (int i = 0; i < MAXCHAR; i++) {
            if (node.childNodes.get(i) != null) {
                if (leaf == 1 && node.start != 1) {
                    // System.out.print(" [" + node.suffixIndex + "]\n"); //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                }
                leaf = 0;
                setSuffixIndexByDFS(node.childNodes.get(i), labelHeight + edgeLength(node.childNodes.get(i)));
            }
        }
        if (leaf == 1) {
            node.suffixIndex = size - labelHeight;
            // System.out.print(" [" + node.suffixIndex + "]\n"); //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        }
    }

    private void freeSuffixTreeByPostOrder(Node node) {
        if (node == null) {
            return;
        }
        for (int i = 0; i < MAXCHAR; i++) {
            if (node.childNodes.get(i) != null) {
                freeSuffixTreeByPostOrder(node.childNodes.get(i));
            }
        }
        if (node.suffixIndex == -1) {
            node.end = null;
        }
        node = null;
    }

    private int traverseEdge(char[] str, int idx, int start, int end) {
        for (int k = start; k <= end && str[idx] != '\0'; k++, idx++) {
            if (input[k] != str[idx]) {
                return -1;
            }
        }
        if (str[idx] == '\0') {
            return 1;
        }
        return 0;
    }

    private int doTraversal(Node node, char[] str, int idx) {
        if (node == null) {
            return -1;
        }
        int res = -1;
        if (node.start != -1) {
            res = traverseEdge(str, idx, node.start, node.end.value);
            if (res != 0) {
                return res;
            }
        }
        idx = idx + edgeLength(node);
        if (node.childNodes.get(str[idx]) != null) {
            return doTraversal(node.childNodes.get(str[idx]), str, idx);
        }
        else {
            return -1;
        }
    }

    public boolean checkForSubString(String substring) {
        substring += '\0';
        int res = doTraversal(root, substring.toCharArray(), 0);
        if (res == 1) {
            return true;
        }
        else {
            return false;
        }
    }


    /* private End end = null;
    private Node root = null;
    private Counter counter = null;

    // !!!
    private int remainingSuffixCount;
    private static char UNIQUE_CHAR = '$';
    private char input[];

    // !!!

    private class Counter {
        private Node currentNode = null;
        private int currentEdge = -1;
        private int currentLenght = 0;

        private Counter (Node node) {
            this.currentNode = node;
        }
    }

    private class End {
        private int value = 0;
        private End(int value) {
            this.value = value;
        }
    }

    private class Node {
        private Node suffixLink = null;
        private ArrayList<Edge> edges = new ArrayList<Edge>();

        private int hasEdgesStartingWith(char firstCharacter) {
            if (!edges.isEmpty()) {
                for (int i = 0; i < edges.size(); i++) {
                    if (edges.get(i).suffix.charAt(0) == firstCharacter)
                        return i;
                }
            }
            return -1;
        }
    }

    private class Edge {
        private int start;
        private End end;
        private String suffix;
        private Node parentNode;
        private Node nextNode;

        private Edge(int start, End end, String suffix, Node parentNode) {
            this.start = start;
            this.end = end;
            this.suffix = suffix;
            this.parentNode = parentNode;
        }
    }

    public SuffixTree(String string) {
        // !!!

        this.input = new char[input.length+1];
        for(int i=0; i < input.length; i++){
            this.input[i] = string.charAt(i);
        }
        this.input[input.length] = UNIQUE_CHAR;

        // !!!

        root = new Node();
        counter = new Counter(root);
        end = new End(-1);
        int remaining = 0;

        for (int i = 0; i < string.length(); i++) {
            remaining++;
            end.value++;
            char c = string.charAt(i);
            if (!root.edges.isEmpty()) {
                for (Edge edge : root.edges) {
                    if (edge.nextNode == null) {
                        edge.suffix = edge.suffix.concat(String.valueOf(c));
                    }
                    else {
                        addToLeaf(edge.nextNode, c);
                    }
                }
            }
            if (counter.currentLenght != 0) {
                //do {
                    Edge edge = counter.currentNode.edges.get(counter.currentEdge);                                                             // Fehler (currentEdge > edges.count())
                    if (counter.currentLenght < edge.suffix.length() && edge.suffix.charAt(counter.currentLenght) == c) {
                        counter.currentLenght++;
                    } else if (counter.currentLenght >= edge.suffix.length() && edge.nextNode != null && edge.nextNode.hasEdgesStartingWith(c) != -1) {
                        counter.currentNode = edge.nextNode;
                        counter.currentEdge = edge.nextNode.hasEdgesStartingWith(c);
                        counter.currentLenght = 1;
                    } else {
                        Node newNode = new Node();
                        newNode.suffixLink = edge.parentNode;                                                                                                                    // ? ? ?
                        newNode.edges.add(new Edge(edge.start + counter.currentLenght, end, edge.suffix.substring(counter.currentLenght), newNode));
                        newNode.edges.add(new Edge(i, end, String.valueOf(c), newNode));
                        edge.nextNode = newNode;
                        edge.suffix = edge.suffix.substring(0, counter.currentLenght);
                        edge.end = new End(edge.start + counter.currentLenght - 1);
                        remaining--;
                        counter.currentLenght--;
                        counter.currentEdge++;
                    }
                //} while (counter.currentLenght != 0);
            }
            int edge = root.hasEdgesStartingWith(c);
            if (edge == -1) {
                root.edges.add(new Edge(i, end, String.valueOf(c), root));
                remaining--;
            }
            else {
                if (counter.currentEdge != edge && counter.currentLenght < 2) {
                    counter.currentEdge = edge;
                    counter.currentLenght = 1;
                }
            }
        }
    }

    private void addToLeaf(Node node, char c) {
        for (Edge edge : node.edges) {
            if (edge.nextNode != null) {
                addToLeaf(edge.nextNode, c);
            }
            else {
                edge.suffix = edge.suffix.concat(String.valueOf(c));
            }
        }
    }

*/

















    /*
    private int sz = 2;
    private String text;

    private static final int MAXLENGTH = 600000;
    private int[] pos = new int[MAXLENGTH];
    private int[] len = new int[MAXLENGTH];
    private int[] par = new int[MAXLENGTH];
    private ArrayList<Map<Character, Integer>> to = new ArrayList<Map<Character, Integer>>();
    private ArrayList<Map<Character, Integer>> link = new ArrayList<Map<Character, Integer>>();
    private int[] path = new int[MAXLENGTH];

    public SuffixTree(String text) {
        len[1] = 1; pos[1] = 0; par[1] = 0;
        link.add(new HashMap<Character, Integer>());
        for (int c = 0; c < 256; c++)
            link.get(0).put((char)c, 1);
        this.text = text;
        for (int i = text.length() - 1; i >= 0; i--)
            extend(i);
    }

    private void attach(int child, int parent, char c, int child_len) {
        to.get(parent).replace(c, child);
        len[child] = child_len;
        par[child] = parent;
    }

    private void extend(int i) {
        int v, vlen = text.length() - i, old = sz - 1, pstk = 0;
        for (v = old; !link.get(v).containsKey(text.charAt(i)); v = par[v]) {
            vlen -= len[v];
            path[pstk++] = v;
        }
        int w = link.get(v).get(text.charAt(i));
        if (to.get(w).containsKey(text.charAt(i + vlen))) {
            int u = to.get(w).get(text.charAt(i + vlen));
            for (pos[sz] = pos[u] - len[u]; text.charAt(pos[sz]) == text.charAt(i + vlen); pos[sz] += len[v]) {
                v = path[--pstk];
                vlen += len[v];
            }
            attach(sz, w, text.charAt(pos[u] - len[u]), len[u] - (pos[u] - pos[sz]));
            attach(u, sz, text.charAt(pos[sz]), pos[u] - pos[sz]);
            link.get(v).replace(text.charAt(i), sz++);
            w = link.get(v).get(text.charAt(i));
        }
        link.get(old).replace(text.charAt(i), sz);
        attach(sz, w, text.charAt(i + vlen), text.length() - (i + vlen));
        pos[sz++] = text.length();
    }

    */
}
