package com.scottlindley.touchmelabs;

import android.os.AsyncTask;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Scott Lindley on 11/30/2016.
 */

public class NewsXmlParser {
    //An interface/listener is used to notify MainActivity when the AsyncTask is completed
    private ParseFinishedListener mListener;

    //The google news rss link
    private final String mUrl = "https://news.google.com/news?cf=all&hl=en&pz=1&ned=us&output=rss";
    private List<String> mStoryLinks;

    //Constructor
    public NewsXmlParser(ParseFinishedListener listener) {
        mStoryLinks = new ArrayList<>();
        mListener = listener;
    }

    //On a new thread, pulls XML from google news RSS with an XmlPullParer
    public void getXML() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    URL url = new URL(mUrl);
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("GET");
                    urlConnection.connect();
                    InputStream stream = urlConnection.getInputStream();

                    XmlPullParserFactory parserFactory = XmlPullParserFactory.newInstance();
                    parserFactory.setNamespaceAware(true);
                    XmlPullParser parser = parserFactory.newPullParser();
                    parser.setInput(stream, null);

                    parseXML(parser);
                    stream.close();

                } catch (MalformedURLException e) {

                } catch (XmlPullParserException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                mListener.onXmlParseFinished();
            }
        }.execute();
    }

    //Uses the created parser to walk through the tags and extract the links to each article
    public void parseXML(XmlPullParser parser) {
        try {
            int event = parser.getEventType();
            boolean inAnItem = false;
            boolean inLinkTag = false;
            //Continue until the end of the XML document is reached
            while (event != XmlPullParser.END_DOCUMENT) {
                String tagName = parser.getName();

                switch (event) {
                    case XmlPullParser.START_TAG:
                        //Note if we've entered an 'item' or 'link' tag
                        if (tagName.equalsIgnoreCase("item")) {
                            inAnItem = true;
                        }
                        if (tagName.equalsIgnoreCase("link")) {
                            inLinkTag = true;
                        }
                        break;
                    case XmlPullParser.TEXT:
                        if (inAnItem && inLinkTag) {
                            //Cut the actual link from the all th text found inside the 'link' tag
                            String[] linkPieces = parser.getText().split("&url=");
                            mStoryLinks.add(linkPieces[1]);
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        //Note if we've now left an 'item' or 'link' tag
                        if (tagName.equalsIgnoreCase("item")) {
                            inAnItem = false;
                        }
                        if (tagName.equalsIgnoreCase("link")) {
                            inLinkTag = false;
                        }
                        break;
                }
                event = parser.next();
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<String> getStoryLinks() {
        return mStoryLinks;
    }

    //Here is the interface that is implemented by MainActivity
    public interface ParseFinishedListener {
        void onXmlParseFinished();
    }

}
