package com.bigsagebeast.hero.roguelike.world;

import com.bigsagebeast.hero.SetupException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class UnidMapping {
    public HashMap<String, String> mappings = new HashMap<>();

    public HashMap<String, List<String>> groupIden = new HashMap<>();
    public HashMap<String, List<String>> groupUnid = new HashMap<>();

    public void registerUnid(String group, String itemKey) {
        if (!groupUnid.containsKey(group)) {
            groupUnid.put(group, new ArrayList<String>());
        }
        groupUnid.get(group).add(itemKey);
    }

    public void registerIden(String group, String itemKey) {
        if (!groupIden.containsKey(group)) {
            groupIden.put(group, new ArrayList<String>());
        }
        groupIden.get(group).add(itemKey);
    }

    public void scan() {
        for (ItemType it : Itempedia.map.values()) {
            if (it.idenGroup != null) {
                registerIden(it.idenGroup, it.keyName);
            } else if (it.unidGroup != null) {
                registerUnid(it.unidGroup, it.keyName);
            }
        }
    }

    public void randomize() throws SetupException {
        for (String group : groupIden.keySet()) {
            if (groupIden.get(group).size() > groupUnid.get(group).size()) {
                throw new SetupException("More identified than unidentified types in iden group " + group + "!");
            }
            Collections.shuffle(groupUnid.get(group));
            for (int i=0; i<groupIden.get(group).size(); i++) {
                mappings.put(groupIden.get(group).get(i), groupUnid.get(group).get(i));
            }
        }
    }

    public void apply() {
        for (String key : mappings.keySet()) {
            ItemType iden = Itempedia.get(key);
            ItemType unid = Itempedia.get(mappings.get(key));
            iden.unidentifiedName = unid.unidentifiedName;
            iden.unidentifiedPluralName = unid.unidentifiedPluralName;
            iden.glyphName = unid.glyphName;
            iden.palette = unid.palette;
            // TODO material?
        }
        for (String groupKey : groupUnid.keySet()) {
            for (String unidKey : groupUnid.get(groupKey)) {
                Itempedia.map.remove(unidKey);
            }
        }
    }
}
