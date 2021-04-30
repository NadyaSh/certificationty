package org.niitp.model;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;

public class ChooseData {
    JsonArrayBuilder objbuilder = Json.createArrayBuilder();

    public JsonArray createObject() {
        return objbuilder.build();
    }

    public void setFileName(String id, String parid, String name, String path) {
        objbuilder.add(Json.createObjectBuilder().add("id", id).add("parid", parid).add("name", name).add("path", path));
    }
}
