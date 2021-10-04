package com.crazicrafter1.gapi;

import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

class Data implements ConfigurationSerializable {

    public static boolean debug;
    public static boolean update;

    // Dummy serialize constructor
    public Data() {}

    public Data(HashMap<String, Object> result) {
        debug = (boolean) result.getOrDefault("debug", false);
        update = (boolean) result.getOrDefault("update", true);
    }

    @Override
    public Map<String, Object> serialize() {
        HashMap<String, Object> result = new LinkedHashMap<>();

        result.put("debug", debug);
        result.put("update", update);

        return result;
    }
}
