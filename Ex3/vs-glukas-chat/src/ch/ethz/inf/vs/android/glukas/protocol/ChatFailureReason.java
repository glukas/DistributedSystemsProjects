package ch.ethz.inf.vs.android.glukas.protocol;

import android.annotation.SuppressLint;

public enum ChatFailureReason {
	notRegistered("not registered"),
	alreadyRegistered("already registered"),
	usernameAlreadyInUse("username already in use"),
	nethzNotRecognized("nethz not recognized"),
	invalidLength("invalid length"),
	notOnETHSubnet("not on ETH subnet"),
	timeout("timeout"),
	unknownCommand("unknown command"),
	noNetwork("no network"),
	unknown("unknown failure");
		
	private final String reasonString;
	
	ChatFailureReason (String s){
		this.reasonString = s;
	}
	
	public String getReasonString(){
		return this.reasonString;
	}
	
	@SuppressLint("DefaultLocale")
	public static ChatFailureReason getReasonFromString(String reason){
		try{
			for (ChatFailureReason r : ChatFailureReason.values()){
				if (reason.toLowerCase().startsWith(r.getReasonString())){
					return r;
				}
			}
			return ChatFailureReason.unknown;
		} catch (Exception ex){
			return ChatFailureReason.unknown;
		}
	}
};