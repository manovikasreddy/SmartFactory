package reports;

import java.util.*;

import model.Maintenance;

public class ReportManager {

    static class State {

        int length;
        int link = -1;

        Map<Character, Integer> next =
                new HashMap<>();
    }

    private ArrayList<State> states =
            new ArrayList<>();

    private int last;

    public ReportManager(String text) {

        states.add(new State());

        last = 0;

        for (char c : text.toCharArray())
            extend(c);
    }

    private void extend(char c) {

        int current = states.size();

        State state = new State();

        state.length =
                states.get(last).length + 1;

        states.add(state);

        int p = last;

        while (p != -1 &&
                !states.get(p).next.containsKey(c)) {

            states.get(p).next.put(c, current);

            p = states.get(p).link;
        }

        if (p == -1) {

            states.get(current).link = 0;

        } else {

            int q =
                    states.get(p).next.get(c);

            if (states.get(p).length + 1
                    == states.get(q).length) {

                states.get(current).link = q;

            } else {

                int clone = states.size();

                State cloned = new State();

                cloned.length =
                        states.get(p).length + 1;

                cloned.next.putAll(
                        states.get(q).next);

                cloned.link =
                        states.get(q).link;

                states.add(cloned);

                while (p != -1 &&
                        states.get(p).next
                                .getOrDefault(c, -1)
                        == q) {

                    states.get(p).next.put(
                            c, clone);

                    p = states.get(p).link;
                }

                states.get(q).link = clone;

                states.get(current).link = clone;
            }
        }

        last = current;
    }

    public boolean contains(String pattern) {

        int current = 0;

        for (char c : pattern.toCharArray()) {

            if (!states.get(current)
                    .next.containsKey(c)) {

                return false;
            }

            current =
                    states.get(current)
                            .next.get(c);
        }

        return true;
    }

	public static void searchReports(List<Maintenance> maintenance, String query) {
		// TODO Auto-generated method stub
		
	}
}