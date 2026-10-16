package reports;

import java.util.*;

public class ReportContentSearch {

    static class Node {

        Map<Character, Node> children =
                new HashMap<>();
    }

    private Node root = new Node();

    public void build(String text) {

        for (int i = 0; i < text.length(); i++) {

            Node current = root;

            for (int j = i; j < text.length(); j++) {

                char c = text.charAt(j);

                current.children.putIfAbsent(
                        c, new Node());

                current =
                        current.children.get(c);
            }
        }
    }

    public boolean contains(String pattern) {

        Node current = root;

        for (char c : pattern.toCharArray()) {

            if (!current.children.containsKey(c))
                return false;

            current =
                    current.children.get(c);
        }

        return true;
    }
}