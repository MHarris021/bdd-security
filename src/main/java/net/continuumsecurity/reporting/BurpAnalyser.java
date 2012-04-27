package net.continuumsecurity.reporting;

import net.continuumsecurity.restyburp.model.ScanIssueBean;
import net.continuumsecurity.restyburp.model.ScanIssueList;
import net.continuumsecurity.Utils;

import org.apache.log4j.Logger;

public class BurpAnalyser {
	private static BurpAnalyser instance;
	Logger log = Logger.getLogger(BurpAnalyser.class);
	
	private BurpAnalyser() {
		
	}
	
	public static BurpAnalyser instance() {
		if (instance == null) {
			instance = new BurpAnalyser();
		}
		return instance;
	}
	
	public ScanIssueList filter(ScanIssueList issueList) {
		if (issueList.getIssues() == null) {
			log.info("No burp issues found");
			return issueList;
		}
		ScanIssueList ret = new ScanIssueList();
		for (ScanIssueBean issue : issueList.getIssues()) {
			ScanIssueBean filteredIssue = filterIssue(issue);
			if (filteredIssue == null) {
				log.info("Burp issue: "+issue.getIssueName()+" is a false positive.");
			} else {
				ret.getIssues().add(filteredIssue);
			}
		}
		return ret;
	}
	
	public ScanIssueBean filterIssue(ScanIssueBean issue) {
		if ("Session token in URL".equalsIgnoreCase(issue.getIssueName())) {
			//If the session ID is not in the URL, then this is a false positive
			if (Utils.extractSessionID(issue.getIssueDetail()) == null) {
				log.trace("session IDs not found in the burp detail, filterIssue() returning null. "+issue.getIssueDetail());
				return null;
			}
		}
		if ("Cookie without HttpOnly flag set".equalsIgnoreCase(issue.getIssueName())) {
			//This test is already defined in the session management scenario
			return null;
		}
		return issue;
	}
			
}
