package com.example.q.project2;

import org.json.JSONException;
import org.json.JSONObject;

public class TextFileManager {

    public JSONObject save(String email) throws JSONException {
        JSONObject result = new JSONObject();
//        result.put("user", name);
        result.put("email", email);
        return result;
    }

}
