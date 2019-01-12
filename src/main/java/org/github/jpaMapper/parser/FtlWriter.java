package org.github.jpaMapper.parser;

import org.apache.commons.lang3.Validate;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.function.BiConsumer;

class FtlWriter extends Writer {

    private LinkedList<StringBuilder> stack = new LinkedList<>();
    private Map<Object, BiConsumer<LinkedList<StringBuilder>, String>> listeners = new HashMap<>();

    public FtlWriter() {
        stack.addLast(new StringBuilder());
    }

    @Override
    public void write(char[] cbuf, int off, int len) throws IOException {
        StringBuilder stringBuilder = stack.getLast();
        BiConsumer<LinkedList<StringBuilder>, String> listener = listeners.get(stringBuilder);
        if (listener == null) {
            stack.getLast().append(cbuf, off, len);
        } else {
            listener.accept(stack, new String(cbuf, off, len));
        }
    }

    public void enterStack(BiConsumer<LinkedList<StringBuilder>, String> listener) {
        stack.addLast(new StringBuilder());
        listeners.put(stack.getLast(), listener);
    }

    public String toString() {
        Validate.isTrue(stack.size() == 1, "栈错误！");
        return stack.getLast().toString();
    }

    public void write(char cbuf[]) throws IOException {
        this.write(cbuf, 0, cbuf.length);
    }

    @Override
    public void flush() throws IOException {
    }

    @Override
    public void close() throws IOException {
    }
}
