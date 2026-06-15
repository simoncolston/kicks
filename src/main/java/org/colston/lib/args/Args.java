package org.colston.lib.args;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Args {

    private final Map<String, Param> params = new HashMap<String, Param>();
    private final Map<String, String> values = new HashMap<>();

    private boolean allowVargs = false;
    private final List<String> vargs = new ArrayList<String>();

    public static Args builder() {
        return new Args();
    }

    public Args parse(String[] args) {
        boolean vargsHaveStarted = false;
        for (String arg : args) {
            String name;
            String value = null;
            int equals = arg.indexOf('=');
            if (equals != -1) {
                name = arg.substring(0, equals);
                value = arg.substring(equals + 1);
            } else {
                name = arg;
            }
            Param param = params.get(name);
            if (param == null) {
                if (name.startsWith("--")) {
                    throw new IllegalArgumentException("Parameter " + name + " is not a valid parameter name");
                }
                if (!allowVargs)  {
                    throw new IllegalArgumentException("Parameter " + name + " is invalid");
                }
                vargsHaveStarted = true;
                vargs.add(name);
            } else {
                if (vargsHaveStarted) {
                    throw new IllegalArgumentException("Parameter " + name + " cannot come after vargs");
                }
                if (param.flag()) {
                    if (value != null) {
                        throw new IllegalArgumentException("Parameter " + name + " cannot have a value");
                    }
                    values.put(name, "SET");
                } else {
                    if (value == null) {
                        throw new IllegalArgumentException("Parameter " + name + " must have a value");
                    }
                    values.put(name, value);
                }
            }
        }
        return this;
    }

    public String get(String name) {
        return values.get(name);
    }

    public boolean is(String name) {
        return values.containsKey(name);
    }

    public List<String> getVargs() {
        return vargs;
    }

    public Args withVargs(boolean flag) {
        this.allowVargs = flag;
        return this;
    }

    public Args parameters(Param... params) {
        for (Param param : params) {
            this.params.put(param.name(), param);
        }
        return this;
    }
}
