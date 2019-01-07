/*
 * This file is generated by jOOQ.
 */
package com.treetory.test.mvc.model.test.tables;


import com.treetory.test.mvc.model.test.Indexes;
import com.treetory.test.mvc.model.test.Keys;
import com.treetory.test.mvc.model.test.Test;
import com.treetory.test.mvc.model.test.tables.records.RawlogTestRecord;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Generated;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Identity;
import org.jooq.Index;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Schema;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.UniqueKey;
import org.jooq.impl.DSL;
import org.jooq.impl.TableImpl;


/**
 * This class is generated by jOOQ.
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.11.7"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class RawlogTest extends TableImpl<RawlogTestRecord> {

    private static final long serialVersionUID = 1879041483;

    /**
     * The reference instance of <code>test.RAWLOG_TEST</code>
     */
    public static final RawlogTest RAWLOG_TEST = new RawlogTest();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<RawlogTestRecord> getRecordType() {
        return RawlogTestRecord.class;
    }

    /**
     * The column <code>test.RAWLOG_TEST.id</code>.
     */
    public final TableField<RawlogTestRecord, Integer> ID = createField("id", org.jooq.impl.SQLDataType.INTEGER.nullable(false).identity(true), this, "");

    /**
     * The column <code>test.RAWLOG_TEST.log</code>.
     */
    public final TableField<RawlogTestRecord, String> LOG = createField("log", org.jooq.impl.SQLDataType.CLOB.nullable(false), this, "");

    /**
     * The column <code>test.RAWLOG_TEST.system_timestamp</code>.
     */
    public final TableField<RawlogTestRecord, Timestamp> SYSTEM_TIMESTAMP = createField("system_timestamp", org.jooq.impl.SQLDataType.TIMESTAMP.nullable(false), this, "");

    /**
     * Create a <code>test.RAWLOG_TEST</code> table reference
     */
    public RawlogTest() {
        this(DSL.name("RAWLOG_TEST"), null);
    }

    /**
     * Create an aliased <code>test.RAWLOG_TEST</code> table reference
     */
    public RawlogTest(String alias) {
        this(DSL.name(alias), RAWLOG_TEST);
    }

    /**
     * Create an aliased <code>test.RAWLOG_TEST</code> table reference
     */
    public RawlogTest(Name alias) {
        this(alias, RAWLOG_TEST);
    }

    private RawlogTest(Name alias, Table<RawlogTestRecord> aliased) {
        this(alias, aliased, null);
    }

    private RawlogTest(Name alias, Table<RawlogTestRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""));
    }

    public <O extends Record> RawlogTest(Table<O> child, ForeignKey<O, RawlogTestRecord> key) {
        super(child, key, RAWLOG_TEST);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Schema getSchema() {
        return Test.TEST;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Index> getIndexes() {
        return Arrays.<Index>asList(Indexes.RAWLOG_TEST_PRIMARY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Identity<RawlogTestRecord, Integer> getIdentity() {
        return Keys.IDENTITY_RAWLOG_TEST;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UniqueKey<RawlogTestRecord> getPrimaryKey() {
        return Keys.KEY_RAWLOG_TEST_PRIMARY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UniqueKey<RawlogTestRecord>> getKeys() {
        return Arrays.<UniqueKey<RawlogTestRecord>>asList(Keys.KEY_RAWLOG_TEST_PRIMARY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RawlogTest as(String alias) {
        return new RawlogTest(DSL.name(alias), this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RawlogTest as(Name alias) {
        return new RawlogTest(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public RawlogTest rename(String name) {
        return new RawlogTest(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public RawlogTest rename(Name name) {
        return new RawlogTest(name, null);
    }
}