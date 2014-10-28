package ch.ethz.inf.vs.android.glukas.chat;

public enum ChatFailureReason {
	notRegistered("not registered"),
	alreadyRegistered("already registered"),
	usernameAlreadyInUse("username already in use"),
	nethzNotRecognized("nethz not recognized"),
	invalidLength("invalid length"),
	notOnETHSubnet("not on ETH subnet"),
	timeout("timeout"),
	unknownCommand("unknown command");
		
	private final String reasonString;
	
	ChatFailureReason (String s){
		this.reasonString = s;
	}
	
	public String getReasonString(){
		return this.reasonString;
	}
};