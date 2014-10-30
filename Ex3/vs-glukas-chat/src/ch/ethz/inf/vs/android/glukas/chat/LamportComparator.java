package ch.ethz.inf.vs.android.glukas.chat;

import java.util.Comparator;

public class LamportComparator implements Comparator<ChatMessage>{


	public int compare(ChatMessage first, ChatMessage second) {
		Integer result = 0 ;
		if (first.getLamport().getTimestamp() < second.getLamport().getTimestamp()){
			result = -1;
		}
		else if (first.getLamport().getTimestamp() > second.getLamport().getTimestamp()){
			result = 1;
		}
		else if (first.getLamport().getTimestamp() == second.getLamport().getTimestamp()){
			result = 0 ;
		}
		return result;
	}

}
