package ch.ethz.inf.vs.android.glukas.chat;

public enum ChatFailureReason {
	notRegistered,
	alreadyRegistered,
	usernameAlreadyInUse,
	nethzNotRecognized,
	invalidLength,
	notOnETHSubnet,
	timeout,
	unknownCommand;
};