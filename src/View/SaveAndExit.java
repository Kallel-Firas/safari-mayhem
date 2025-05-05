package View;

import java.util.HashMap;

import Model.Safari;
import org.json.simple.*;
import org.json.simple.parser.*;

public class SaveAndExit {
    static void save(Safari safari) {
        JSONObject safariJSON = new JSONObject();
        safariJSON.put("game name", safari.getGameName());
        safariJSON.put("difficulty", safari.getDifficultyLevel());
        System.out.println(safariJSON);
    }
}
