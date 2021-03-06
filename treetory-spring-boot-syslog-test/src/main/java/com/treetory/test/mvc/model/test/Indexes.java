/*
 * This file is generated by jOOQ.
 */
package com.treetory.test.mvc.model.test;


import com.treetory.test.mvc.model.test.tables.NormalizedlogTest;
import com.treetory.test.mvc.model.test.tables.RawlogTest;

import javax.annotation.Generated;

import org.jooq.Index;
import org.jooq.OrderField;
import org.jooq.impl.Internal;


/**
 * A class modelling indexes of tables of the <code>test</code> schema.
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.11.7"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Indexes {

    // -------------------------------------------------------------------------
    // INDEX definitions
    // -------------------------------------------------------------------------

    public static final Index NORMALIZEDLOG_TEST_CREATE_TIME = Indexes0.NORMALIZEDLOG_TEST_CREATE_TIME;
    public static final Index NORMALIZEDLOG_TEST_DEV_ID = Indexes0.NORMALIZEDLOG_TEST_DEV_ID;
    public static final Index NORMALIZEDLOG_TEST_D_IP = Indexes0.NORMALIZEDLOG_TEST_D_IP;
    public static final Index NORMALIZEDLOG_TEST_ID = Indexes0.NORMALIZEDLOG_TEST_ID;
    public static final Index NORMALIZEDLOG_TEST_S_IP = Indexes0.NORMALIZEDLOG_TEST_S_IP;
    public static final Index RAWLOG_TEST_PRIMARY = Indexes0.RAWLOG_TEST_PRIMARY;

    // -------------------------------------------------------------------------
    // [#1459] distribute members to avoid static initialisers > 64kb
    // -------------------------------------------------------------------------

    private static class Indexes0 {
        public static Index NORMALIZEDLOG_TEST_CREATE_TIME = Internal.createIndex("create_time", NormalizedlogTest.NORMALIZEDLOG_TEST, new OrderField[] { NormalizedlogTest.NORMALIZEDLOG_TEST.CREATE_TIME }, false);
        public static Index NORMALIZEDLOG_TEST_DEV_ID = Internal.createIndex("dev_id", NormalizedlogTest.NORMALIZEDLOG_TEST, new OrderField[] { NormalizedlogTest.NORMALIZEDLOG_TEST.DEV_ID, NormalizedlogTest.NORMALIZEDLOG_TEST.CREATE_TIME }, false);
        public static Index NORMALIZEDLOG_TEST_D_IP = Internal.createIndex("d_ip", NormalizedlogTest.NORMALIZEDLOG_TEST, new OrderField[] { NormalizedlogTest.NORMALIZEDLOG_TEST.D_IP, NormalizedlogTest.NORMALIZEDLOG_TEST.CREATE_TIME }, false);
        public static Index NORMALIZEDLOG_TEST_ID = Internal.createIndex("id", NormalizedlogTest.NORMALIZEDLOG_TEST, new OrderField[] { NormalizedlogTest.NORMALIZEDLOG_TEST.ID }, false);
        public static Index NORMALIZEDLOG_TEST_S_IP = Internal.createIndex("s_ip", NormalizedlogTest.NORMALIZEDLOG_TEST, new OrderField[] { NormalizedlogTest.NORMALIZEDLOG_TEST.S_IP, NormalizedlogTest.NORMALIZEDLOG_TEST.CREATE_TIME }, false);
        public static Index RAWLOG_TEST_PRIMARY = Internal.createIndex("PRIMARY", RawlogTest.RAWLOG_TEST, new OrderField[] { RawlogTest.RAWLOG_TEST.ID }, true);
    }
}
