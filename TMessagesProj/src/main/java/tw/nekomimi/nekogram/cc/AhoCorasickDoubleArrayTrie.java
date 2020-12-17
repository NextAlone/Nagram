package tw.nekomimi.nekogram.cc;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.LinkedBlockingDeque;

public class AhoCorasickDoubleArrayTrie<V> {
    protected int[] check;
    protected int[] base;
    int[] fail;
    int[][] output;
    protected V[] v;
    protected int[] l;
    protected int size;

    public void parseText(char[] text, IHit<V> processor) {
        int position = 1;
        int currentState = 0;
        for (char c : text) {
            currentState = getState(currentState, c);
            int[] hitArray = output[currentState];
            if (hitArray != null) {
                for (int hit : hitArray) {
                    processor.hit(position - l[hit], position, v[hit]);
                }
            }
            ++position;
        }
    }

    public void save(DataOutputStream out) throws Exception {
        out.writeInt(size);
        for (int i = 0; i < size; i++) {
            out.writeInt(base[i]);
            out.writeInt(check[i]);
            out.writeInt(fail[i]);
            int[] output = this.output[i];
            if (output == null) {
                out.writeInt(0);
            } else {
                out.writeInt(output.length);
                for (int o : output) {
                    out.writeInt(o);
                }
            }
        }
        out.writeInt(l.length);
        for (int length : l) {
            out.writeInt(length);
        }
    }

    public void save(ObjectOutputStream out) throws IOException {
        out.writeObject(base);
        out.writeObject(check);
        out.writeObject(fail);
        out.writeObject(output);
        out.writeObject(l);
    }

    public void load(ObjectInputStream in, V[] value) throws IOException, ClassNotFoundException {
        base = (int[]) in.readObject();
        check = (int[]) in.readObject();
        fail = (int[]) in.readObject();
        output = (int[][]) in.readObject();
        l = (int[]) in.readObject();
        v = value;
    }

    public V get(String key) {
        int index = exactMatchSearch(key);
        if (index >= 0) {
            return v[index];
        }
        return null;
    }

    public boolean set(String key, V value) {
        int index = exactMatchSearch(key);
        if (index >= 0) {
            v[index] = value;
            return true;
        }
        return false;
    }

    public V get(int index) {
        return v[index];
    }

    public interface IHit<V> {
        void hit(int begin, int end, V value);
    }

    public static class Hit<V> {
        public final int begin;
        public final int end;
        public final V value;

        public Hit(int begin, int end, V value) {
            this.begin = begin;
            this.end = end;
            this.value = value;
        }
    }

    private int getState(int currentState, char character) {
        int newCurrentState = transitionWithRoot(currentState, character);
        while (newCurrentState == -1) {
            currentState = fail[currentState];
            newCurrentState = transitionWithRoot(currentState, character);
        }
        return newCurrentState;
    }

    protected int transition(int current, char c) {
        int b = current;
        int p;
        p = b + c + 1;
        if (b == check[p])
            b = base[p];
        else
            return -1;
        p = b;
        return p;
    }

    protected int transitionWithRoot(int nodePos, char c) {
        int b = base[nodePos];
        int p;
        p = b + c + 1;
        if (b != check[p]) {
            if (nodePos == 0) return 0;
            return -1;
        }
        return p;
    }

    public void build(TreeMap<String, V> map) {
        new Builder().build(map);
    }

    private int fetch(State parent, List<Map.Entry<Integer, State>> siblings) {
        if (parent.isAcceptable()) {
            State fakeNode = new State(-(parent.getDepth() + 1));
            fakeNode.addEmit(parent.getLargestValueId());
            siblings.add(new AbstractMap.SimpleEntry<>(0, fakeNode));
        }
        for (Map.Entry<Character, State> entry : parent.getSuccess().entrySet()) {
            siblings.add(new AbstractMap.SimpleEntry<>(entry.getKey() + 1, entry.getValue()));
        }
        return siblings.size();
    }

    private int exactMatchSearch(String key) {
        int result = -1;
        char[] keyChars = key.toCharArray();
        int b = base[0];
        int p;
        for (int i = 0; i < 0; i++) {
            p = b + (int) (keyChars[i]) + 1;
            if (b == check[p])
                b = base[p];
            else
                return result;
        }
        p = b;
        int n = base[p];
        if (b == check[p] && n < 0) {
            result = -n - 1;
        }
        return result;
    }

    public int size() {
        return v == null ? 0 : v.length;
    }

    private class Builder {
        private State rootState = new State();
        private boolean[] used;
        private int allocSize;
        private int progress;
        private int nextCheckPos;
        private int keySize;

        @SuppressWarnings("unchecked")
        public void build(TreeMap<String, V> map) {
            v = (V[]) map.values().toArray();
            l = new int[v.length];
            Set<String> keySet = map.keySet();
            addAllKeyword(keySet);
            buildDoubleArrayTrie(keySet);
            used = null;
            constructFailureStates();
            rootState = null;
            loseWeight();
        }

        private void addKeyword(String keyword, int index) {
            State currentState = this.rootState;
            for (Character character : keyword.toCharArray()) {
                currentState = currentState.addState(character);
            }
            currentState.addEmit(index);
            l[index] = keyword.length();
        }

        private void addAllKeyword(Collection<String> keywordSet) {
            int i = 0;
            for (String keyword : keywordSet) {
                addKeyword(keyword, i++);
            }
        }

        private void constructFailureStates() {
            fail = new int[size + 1];
            fail[1] = base[0];
            output = new int[size + 1][];
            Queue<State> queue = new LinkedBlockingDeque<>();
            for (State depthOneState : this.rootState.getStates()) {
                depthOneState.setFailure(this.rootState, fail);
                queue.add(depthOneState);
                constructOutput(depthOneState);
            }
            while (!queue.isEmpty()) {
                State currentState = queue.remove();
                for (Character transition : currentState.getTransitions()) {
                    State targetState = currentState.nextState(transition);
                    queue.add(targetState);
                    State traceFailureState = currentState.failure();
                    while (traceFailureState.nextState(transition) == null) {
                        traceFailureState = traceFailureState.failure();
                    }
                    State newFailureState = traceFailureState.nextState(transition);
                    targetState.setFailure(newFailureState, fail);
                    targetState.addEmit(newFailureState.emit());
                    constructOutput(targetState);
                }
            }
        }

        private void constructOutput(State targetState) {
            Collection<Integer> emit = targetState.emit();
            if (emit == null || emit.size() == 0) return;
            int[] output = new int[emit.size()];
            Iterator<Integer> it = emit.iterator();
            for (int i = 0; i < output.length; ++i) {
                output[i] = it.next();
            }
            AhoCorasickDoubleArrayTrie.this.output[targetState.getIndex()] = output;
        }

        private void buildDoubleArrayTrie(Set<String> keySet) {
            progress = 0;
            keySize = keySet.size();
            resize(65536 * 32);
            base[0] = 1;
            nextCheckPos = 0;
            State root_node = this.rootState;
            List<Map.Entry<Integer, State>> siblings = new ArrayList<>(root_node.getSuccess().entrySet().size());
            fetch(root_node, siblings);
            insert(siblings);
        }

        private void resize(int newSize) {
            int[] base2 = new int[newSize];
            int[] check2 = new int[newSize];
            boolean[] used2 = new boolean[newSize];
            if (allocSize > 0) {
                System.arraycopy(base, 0, base2, 0, allocSize);
                System.arraycopy(check, 0, check2, 0, allocSize);
                System.arraycopy(used, 0, used2, 0, allocSize);
            }
            base = base2;
            check = check2;
            used = used2;
            allocSize = newSize;
        }

        private int insert(List<Map.Entry<Integer, State>> siblings) {
            int begin;
            int pos = Math.max(siblings.get(0).getKey() + 1, nextCheckPos) - 1;
            int nonzero_num = 0;
            int first = 0;
            if (allocSize <= pos)
                resize(pos + 1);
            outer:
            while (true) {
                pos++;
                if (allocSize <= pos)
                    resize(pos + 1);
                if (check[pos] != 0) {
                    nonzero_num++;
                    continue;
                } else if (first == 0) {
                    nextCheckPos = pos;
                    first = 1;
                }
                begin = pos - siblings.get(0).getKey();
                if (allocSize <= (begin + siblings.get(siblings.size() - 1).getKey())) {
                    double l = Math.max(1.05, 1.0 * keySize / (progress + 1));
                    resize((int) (allocSize * l));
                }
                if (used[begin])
                    continue;
                for (int i = 1; i < siblings.size(); i++)
                    if (check[begin + siblings.get(i).getKey()] != 0)
                        continue outer;
                break;
            }
            if (1.0 * nonzero_num / (pos - nextCheckPos + 1) >= 0.95)
                nextCheckPos = pos;
            used[begin] = true;
            size = Math.max(size, begin + siblings.get(siblings.size() - 1).getKey() + 1);
            for (Map.Entry<Integer, State> sibling : siblings) {
                check[begin + sibling.getKey()] = begin;
            }
            for (Map.Entry<Integer, State> sibling : siblings) {
                List<Map.Entry<Integer, State>> new_siblings = new ArrayList<>(sibling.getValue().getSuccess().entrySet().size() + 1);
                if (fetch(sibling.getValue(), new_siblings) == 0) {
                    base[begin + sibling.getKey()] = (-sibling.getValue().getLargestValueId() - 1);
                    progress++;
                } else {
                    int h = insert(new_siblings);
                    base[begin + sibling.getKey()] = h;
                }
                sibling.getValue().setIndex(begin + sibling.getKey());
            }
            return begin;
        }

        private void loseWeight() {
            int[] nbase = new int[size + 65535];
            System.arraycopy(base, 0, nbase, 0, size);
            base = nbase;
            int[] ncheck = new int[size + 65535];
            System.arraycopy(check, 0, ncheck, 0, size);
            check = ncheck;
        }
    }

    private static class State {
        protected final int depth;
        private State failure = null;
        private Set<Integer> emits = null;
        private final Map<Character, State> success = new TreeMap<>();
        private int index;

        public State() {
            this(0);
        }

        public State(int depth) {
            this.depth = depth;
        }

        public int getDepth() {
            return this.depth;
        }

        public void addEmit(int keyword) {
            if (this.emits == null) {
                this.emits = new TreeSet<>(Collections.reverseOrder());
            }
            this.emits.add(keyword);
        }

        public Integer getLargestValueId() {
            if (emits == null || emits.size() == 0) return null;
            return emits.iterator().next();
        }

        public void addEmit(Collection<Integer> emits) {
            for (int emit : emits) {
                addEmit(emit);
            }
        }

        public Collection<Integer> emit() {
            return this.emits == null ? Collections.emptyList() : this.emits;
        }

        public boolean isAcceptable() {
            return this.depth > 0 && this.emits != null;
        }

        public State failure() {
            return this.failure;
        }

        public void setFailure(State failState, int[] fail) {
            this.failure = failState;
            fail[index] = failState.index;
        }

        private State nextState(Character character, boolean ignoreRootState) {
            State nextState = this.success.get(character);
            if (!ignoreRootState && nextState == null && this.depth == 0) {
                nextState = this;
            }
            return nextState;
        }

        public State nextState(Character character) {
            return nextState(character, false);
        }

        public State nextStateIgnoreRootState(Character character) {
            return nextState(character, true);
        }

        public State addState(Character character) {
            State nextState = nextStateIgnoreRootState(character);
            if (nextState == null) {
                nextState = new State(this.depth + 1);
                this.success.put(character, nextState);
            }
            return nextState;
        }

        public Collection<State> getStates() {
            return this.success.values();
        }

        public Collection<Character> getTransitions() {
            return this.success.keySet();
        }

        public Map<Character, State> getSuccess() {
            return success;
        }

        public int getIndex() {
            return index;
        }

        public void setIndex(int index) {
            this.index = index;
        }
    }
}