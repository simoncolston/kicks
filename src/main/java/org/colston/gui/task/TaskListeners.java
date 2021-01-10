package org.colston.gui.task;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class TaskListeners {
    private static final List<TaskListener> listeners = new ArrayList<>();

    public static void register(TaskListener l) {
        listeners.add(l);
    }

    public static List<TaskListener> getAll() {
        return Collections.unmodifiableList(listeners);
    }
}
