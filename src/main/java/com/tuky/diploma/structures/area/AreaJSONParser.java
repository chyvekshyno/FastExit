package com.tuky.diploma.structures.area;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AreaJSONParser {

    public static final String TAG_AREA_NAME    = "areaName";
    public static final String TAG_ZONES        = "zones";
    public static final String TAG_ZONE_NAME    = "zoneName";
    public static final String TAG_SHAPE        = "shape";
    public static final String TAG_PORTALS      = "portals";
    public static final String TAG_LEN          = "len";
    public static final String TAG_POLYGON_TYPE = "type";
    public static final String TAG_EXITS        = "exits";
    public static final String TAG_COORD        = "coord";

//    public static final String TAG_ = "";
    public static Area parse (String jsonfile) throws IOException, ParseException {

        JSONObject root = (JSONObject) new JSONParser()
                .parse(new FileReader(jsonfile));

        String areaName = (String) root.get(TAG_AREA_NAME);

        return new Area(parseZones((JSONArray) root.get(TAG_ZONES))
                , parseExitsConnect((JSONArray) root.get(TAG_EXITS)), 1);
    }

    /**
     * Here Coord uses as Pairs of Ints
     */
    private static List<List<Integer>> parseExitsConnect(JSONArray jsonExits) {
        List<List<Integer>> res = new ArrayList<>();
        for (Object it : jsonExits)
            res.add(List.of(
                    (int) (long) ((JSONArray) it).get(0),
                    (int) (long) ((JSONArray) it).get(1),
                    (int) (long) ((JSONArray) it).get(2),
                    (int) (long) ((JSONArray) it).get(3)));

        return res;
    }

    private static List<Zone> parseZones(JSONArray jsonZones) {
        List<Zone> zoneList = new ArrayList<>();
        for(Object it : jsonZones)
            zoneList.add(parseZone((JSONObject) it));
        return zoneList;
    }

    private static Zone parseZone(JSONObject object) {

        String zoneName = (String) object.get(TAG_ZONE_NAME);
        JSONArray polygonsJsonArr = (JSONArray) object.get(TAG_SHAPE);
        JSONArray exitsJsonArr = (JSONArray) object.get(TAG_PORTALS);
        double len = (Double) object.get(TAG_LEN);

        return new Zone(parseShape(polygonsJsonArr),
                        parseExits(exitsJsonArr),
                        len);
    }

    private static List<Exit> parseExits(JSONArray exits) {
        JSONArray exitCurr;
        List<Exit> exitList = new ArrayList<>();

        for (Object exit : exits) {
            exitCurr = (JSONArray) exit;
            exitList.add(parseExit( (JSONArray) exitCurr.get(0),
                                    (JSONArray) exitCurr.get(1)));
        }
        return exitList;
    }

    private static Exit parseExit(JSONArray eCoordStart, JSONArray eCoordEnd) {
        return new Exit(parseCoord(eCoordStart), parseCoord(eCoordEnd), null);
    }

    private static List<Side> parseShape(JSONArray shape) {
        JSONObject jsonObj = (JSONObject) shape.remove(0);
        JSONArray jsonCoord = (JSONArray) jsonObj.get(TAG_COORD);
        IntCoord prev = parseCoord(jsonCoord);
        IntCoord next;

        List<Side> sideList = new ArrayList<>();
        for (Object it : shape) {
            jsonObj = (JSONObject) it;
            jsonCoord = (JSONArray) jsonObj.get(TAG_COORD);
            next = parseCoord(jsonCoord);

            sideList.add(parseSide(prev, next, ((String) jsonObj.get(TAG_POLYGON_TYPE)).charAt(0)));

            prev = next;
        }
        return sideList;
    }

    private static Side parseSide(IntCoord a, IntCoord b, char type) {
        switch (type){
            case 'l' : return new Side(a, b);
            case 'e' : return new Exit(a, b, null);
        } throw new IllegalArgumentException("No polygon type : " + type);
    }

    private static IntCoord parseCoord(JSONArray jsonCoord) {
        return IntCoord.at(
                (int)(long)jsonCoord.get(0),
                (int)(long)jsonCoord.get(1));
    }
}
