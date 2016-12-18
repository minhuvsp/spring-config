package com.vsp.rating.db.config;

import java.sql.SQLException;
import java.sql.Statement;

public interface StatementRunner {
	void run(Statement statement) throws SQLException;
}
