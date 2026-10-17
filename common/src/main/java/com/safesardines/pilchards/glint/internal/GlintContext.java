package com.safesardines.pilchards.glint.internal;

import com.safesardines.pilchards.glint.GlintStyle;
import java.util.Deque;
import java.util.LinkedList;

public final class GlintContext {
    private static final ThreadLocal<Deque<GlintStyle>> STACK = ThreadLocal.withInitial(LinkedList::new);

    private GlintContext() {}

    public static void push(GlintStyle style) {
        STACK.get().push(style);
    }

    public static void pop() {
        Deque<GlintStyle> stack = STACK.get();
        if (!stack.isEmpty()) {
            stack.pop();
        }
    }

    public static GlintStyle current() {
        Deque<GlintStyle> stack = STACK.get();
        return stack.isEmpty() ? null : stack.peek();
    }
}
