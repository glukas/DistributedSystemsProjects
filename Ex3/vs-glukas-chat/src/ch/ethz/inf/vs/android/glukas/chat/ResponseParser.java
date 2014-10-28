package ch.ethz.inf.vs.android.glukas.chat;

import org.json.JSONException;
import org.json.JSONObject;

public class ResponseParser {
	
	private String responseString;
	private JSONObject responseJSON;
	
	public ResponseParser(String responseString) {
		this.responseString = responseString;
		buildJSON();
		
	}
	
	public String getText() {
		try {
			return this.responseJSON.getString("text");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "Failure";
		}
		
	}
	
	public String getCommand(){
		try {
			return this.responseJSON.getString("cmd");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "Failure";
		}
		
	}
	
	public String getStatus(){
		try {
			return this.responseJSON.getString("status");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "Failure";
			
		}
	}
	
	public String getSender(){
		try {
			return this.responseJSON.getString("sender");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "Failure";
		}
		
		
	}
	public String getLamport(){
		try {
			return this.responseJSON.getString("lamport");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "Failure";
		}
		
		
	}
	public String getVector(){
		try {
			return this.responseJSON.getString("time_vector");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "Failure";
		}
		
		
	}
	
	public String getClients(){
		try {
			return this.responseJSON.getString("clients");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "Failure";
		}
		
	}
	
	
	public void buildJSON(){
			try {
				this.responseJSON = new JSONObject(responseString);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
	}

}
