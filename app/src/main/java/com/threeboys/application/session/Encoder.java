package com.threeboys.application.session;

public interface Encoder {
	String encode(String raw);
	boolean matches(String raw, String stored);
}
