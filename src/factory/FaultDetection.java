package factory;

import java.util.*;

public class FaultDetection {

    static class Node {

        Map<Character, Integer> next =
                new HashMap<>();

        int fail = 0;

        List<String> output =
                new ArrayList<>();
    }

    private List<Node> trie;

    public FaultDetection(List<String> keywords) {

        trie = new ArrayList<>();
        trie.add(new Node());

        for (String keyword : keywords)
            add(keyword.toLowerCase());

        buildLinks();
    }

    private void add(String word) {

        int current = 0;

        for (char c : word.toCharArray()) {

            if (!trie.get(current)
                    .next.containsKey(c)) {

                trie.get(current)
                        .next.put(c, trie.size());

                trie.add(new Node());
            }

            current =
                    trie.get(current)
                            .next.get(c);
        }

        trie.get(current)
                .output.add(word);
    }

    private void buildLinks() {

        Queue<Integer> queue =
                new LinkedList<>();

        for (int child :
                trie.get(0).next.values()) {

            queue.add(child);
        }

        while (!queue.isEmpty()) {

            int current = queue.poll();

            for (Map.Entry<Character, Integer> e :
                    trie.get(current).next.entrySet()) {

                char c = e.getKey();

                int child = e.getValue();

                int fail =
                        trie.get(current).fail;

                while (fail != 0 &&
                        !trie.get(fail)
                                .next.containsKey(c)) {

                    fail =
                            trie.get(fail).fail;
                }

                if (trie.get(fail)
                        .next.containsKey(c)
                        && trie.get(fail)
                        .next.get(c) != child) {

                    fail =
                            trie.get(fail)
                            .next.get(c);
                }

                trie.get(child).fail = fail;

                trie.get(child).output.addAll(
                        trie.get(fail).output);

                queue.add(child);
            }
        }
    }

    public List<String> detect(String text) {

        List<String> found =
                new ArrayList<>();

        int current = 0;

        text = text.toLowerCase();

        for (char c : text.toCharArray()) {

            while (current != 0 &&
                    !trie.get(current)
                            .next.containsKey(c)) {

                current =
                        trie.get(current).fail;
            }

            if (trie.get(current)
                    .next.containsKey(c)) {

                current =
                        trie.get(current)
                                .next.get(c);
            }

            found.addAll(
                    trie.get(current).output);
        }

        return found;
    }
}
