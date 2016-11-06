package com.renegades.labs.searchimages;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


/**
 * Created by Виталик on 04.11.2016.
 */

public class Parser {
    private static final String URL_FORMAT = "https://www.google.com.ua/search?tbm=isch&q=%s";

    public List<String> getStrings(String searchTerm) {
        List<String> imgStrings = new ArrayList<>();
        Document doc;

        try {
            doc = getDocument(searchTerm);

            Elements elements = doc.select("div.rg_meta");
            JSONObject jsonObject;

            for (Element element : elements) {
                if (element.childNodeSize() > 0) {
                    jsonObject = (JSONObject) new JSONParser().parse(element.childNode(0).toString());
                    String imgSrc = (String) jsonObject.get("ou");
                    imgStrings.add(imgSrc);
                }
            }
        } catch (ParseException | IOException e) {
            e.printStackTrace();
        }
        return imgStrings;
    }

    private Document getDocument(String searchTerm) throws IOException {
        String url = String.format(Locale.ENGLISH, URL_FORMAT, searchTerm);
        String userAgent = "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.106 Safari/537.36";
        return Jsoup.connect(url).userAgent(userAgent).referrer("http://google.com.ua").ignoreContentType(true).ignoreHttpErrors(true).get();
    }
}
