package com.vsp.rating.db.config;

public interface DbSetup {
	void setup(StatementRunner runner);
	void setupOnce(StatementRunner runner, String name);
}
