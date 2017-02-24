package com.i4hq.flame.core;

public class Timestamp extends MetadataItem {
	public static String TIMESTAMP_NAME = "ts";
	private final long timestamp;
	
	public Timestamp(long ts) {
		super(TIMESTAMP_NAME, ts + "");
		this.timestamp = ts;
	}
	
	public Timestamp(Long ts) {
		this(ts == null ? System.currentTimeMillis() : ts);
	}
	
	public Timestamp() {
		this(System.currentTimeMillis());
	}

	public long getTimestamp() {
		return timestamp;
	}
}
