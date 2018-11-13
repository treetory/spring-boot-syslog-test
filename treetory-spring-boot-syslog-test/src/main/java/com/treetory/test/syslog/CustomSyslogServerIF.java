package com.treetory.test.syslog;

import org.jooq.DSLContext;
import org.productivity.java.syslog4j.server.SyslogServerIF;

public interface CustomSyslogServerIF extends SyslogServerIF {

    public void setDSLContext(DSLContext dsl);

}
