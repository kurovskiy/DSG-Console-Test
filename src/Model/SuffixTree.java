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
    private End leafEnd = null;
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
            for (int i = 0; i < MAXCHAR; i++)
                childNodes.add(null);
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
        for (int i = 0; i < size; i++)
            extendSuffixTree(i);
        int labelHeight = 0;
        setSuffixIndexByDFS(root, labelHeight);
    }

    private int edgeLength(Node node) {
        int start = node.start;
        int end = node.end.value;
        int le = leafEnd.value;
        if (node.equals(root))
            return 0;
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
        if (leafEnd == null)
            leafEnd = new End(pos);
        else
            leafEnd.value = pos;
        remainigsSuffixCount++;
        lastNewNode = null;
        while (remainigsSuffixCount > 0) {
            if (activeLength == 0)
                activeEdge = pos;
            if (activeNode.childNodes.get(input[activeEdge]) == null) {
                activeNode.childNodes.set(input[activeEdge], new Node(pos, leafEnd));
                if (lastNewNode != null) {
                    lastNewNode.suffixLink = activeNode;
                    lastNewNode = null;
                }
            }
            else {
                Node next = activeNode.childNodes.get(input[activeEdge]);
                if (walkDown(next))
                    continue;
                if (input[next.start + activeLength] == input[pos]) {
                    if (lastNewNode != null && activeNode != root) {
                        lastNewNode.suffixLink = activeNode;
                        lastNewNode = null;
                    }
                    activeLength++;
                    break;
                }
                splitEnd = new End(next.start + activeLength - 1);
                Node split = new Node(next.start, splitEnd);
                activeNode.childNodes.set(input[activeEdge], split);
                split.childNodes.set(input[pos], new Node(pos, leafEnd));
                next.start += activeLength;
                split.childNodes.set(input[next.start], next);
                if (lastNewNode != null)
                    lastNewNode.suffixLink = split;
                lastNewNode = split;
            }
            remainigsSuffixCount--;
            if (activeNode.equals(root) && activeLength > 0) {
                activeLength--;
                activeEdge = pos - remainigsSuffixCount + 1;
            }
            else if (activeNode != root)
                activeNode = activeNode.suffixLink;
        }
    }

    private void print(int i, int j) {
        for (int k = i; k <= j; k++)
            System.out.print(input[k]);
    }

    private void setSuffixIndexByDFS(Node node, int labelHeight) {
        if (node == null)
            return;
        if (node.start != -1) {
            // print(node.start, node.end.value);
        }
        int leaf = 1;
        for (int i = 0; i < MAXCHAR; i++) {
            if (node.childNodes.get(i) != null) {
                if (leaf == 1 && node.start != 1) {
                    // System.out.print(" [" + node.suffixIndex + "]\n");
                }
                leaf = 0;
                setSuffixIndexByDFS(node.childNodes.get(i), labelHeight + edgeLength(node.childNodes.get(i)));
            }
        }
        if (leaf == 1) {
            node.suffixIndex = size - labelHeight;
            // System.out.print(" [" + node.suffixIndex + "]\n");
        }
    }

    private void freeSuffixTreeByPostOrder(Node node) {
        if (node == null)
            return;
        for (int i = 0; i < MAXCHAR; i++)
            if (node.childNodes.get(i) != null)
                freeSuffixTreeByPostOrder(node.childNodes.get(i));
        if (node.suffixIndex == -1)
            node.end = null;
        node = null;
    }

    private int traverseEdge(char[] str, int idx, int start, int end) {
        for (int k = start; k <= end && str[idx] != '\0'; k++, idx++)
            if (input[k] != str[idx])
                return -1;
        if (str[idx] == '\0')
            return 1;
        return 0;
    }

    private int doTraversal(Node node, char[] str, int idx) {
        if (node == null)
            return -1;
        int res = -1;
        if (node.start != -1) {
            res = traverseEdge(str, idx, node.start, node.end.value);
            if (res != 0)
                return res;
        }
        idx = idx + edgeLength(node);
        if (node.childNodes.get(str[idx]) != null)
            return doTraversal(node.childNodes.get(str[idx]), str, idx);
        else
            return -1;
    }

    public boolean checkForSubString(String substring) {
        substring += '\0';
        int res = doTraversal(root, substring.toCharArray(), 0);
        if (res == 1)
            return true;
        else
            return false;
    }
}
