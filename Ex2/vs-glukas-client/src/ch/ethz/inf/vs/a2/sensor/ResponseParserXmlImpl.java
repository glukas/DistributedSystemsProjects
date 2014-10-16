package ch.ethz.inf.vs.a2.sensor;

import java.io.IOException;
import java.io.StringReader;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.util.Log;

public class ResponseParserXmlImpl implements ResponseParser{
	
	static final String TAG_NAME = "temperature";
	
	@Override
	public double parseResponse(String response) {
		
        Double result = Double.NaN;
        try {
        	XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        	XmlPullParser xpp = factory.newPullParser();
        	
        	xpp.setInput( new StringReader (response) );
        	int eventType = xpp.getEventType();
        	boolean temperatureTag = false;
        	while (eventType != XmlPullParser.END_DOCUMENT) {
        		
        		if (eventType == XmlPullParser.START_TAG) {
        			
        			if (xpp.getName().equals(TAG_NAME)) {
        				temperatureTag = true;
        			}

        		} if (eventType == XmlPullParser.TEXT) {
        			//Log.v("XML", xpp.getText());
        			if (temperatureTag) {
        				result = Double.valueOf(xpp.getText());
        				if (result == null) {
        					result = Double.NaN;
        				}
        				break;
        			}
        		}
        		eventType = xpp.next();
        	}
        } catch (XmlPullParserException e) {
        	e.printStackTrace();
        } catch (IOException e) {
        	e.printStackTrace();
        }
        return result;
	}

}
