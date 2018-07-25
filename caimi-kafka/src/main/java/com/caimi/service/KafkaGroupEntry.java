package com.caimi.service;

import java.util.Set;
import java.util.TreeSet;

public class KafkaGroupEntry {
	
	public String group;
	public Set<String> topic = new TreeSet<>();
	public volatile long eventsReceived;
	
	public KafkaGroupEntry(String group) {
		this.group = group;
	}
	
	public double getValue() {
		return eventsReceived;
	}

}
