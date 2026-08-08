package org.colston.unittestutils;

import de.danielbechler.diff.ObjectDifferBuilder;
import de.danielbechler.diff.node.DiffNode;
import org.jspecify.annotations.NonNull;

import java.util.Collections;
import java.util.List;

public class UnitTestUtils {

    public static <T> void diff(T expected, T actual) {
        diff(expected, actual, Collections.emptyList());
    }

    public static <T> void diff(T expected, T actual, List<String> ignoredPaths) {
        DiffNode diff = ObjectDifferBuilder.buildDefault().compare(expected, actual);
        if (diff.hasChanges()) {
            diff.visit(visit(ignoredPaths));
        }
    }

    private static DiffNode.@NonNull Visitor visit(List<String> ignoredPaths) {
        return (node, _) -> {
            if (ignoredPaths != null && ignoredPaths.stream().anyMatch(ignoredPath -> node.getPath().toString().startsWith(ignoredPath))) {
                return;
            }
            System.out.println(node.getPath() + " => " + node.getState());
        };
    }

}
