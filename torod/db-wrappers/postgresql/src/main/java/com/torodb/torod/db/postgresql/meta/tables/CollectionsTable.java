/*
 *     This file is part of ToroDB.
 *
 *     ToroDB is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Affero General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     ToroDB is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Affero General Public License for more details.
 *
 *     You should have received a copy of the GNU Affero General Public License
 *     along with ToroDB. If not, see <http://www.gnu.org/licenses/>.
 *
 *     Copyright (c) 2014, 8Kdata Technology
 *     
 */
package com.torodb.torod.db.postgresql.meta.tables;

import com.torodb.torod.db.postgresql.meta.TorodbKeys;
import com.torodb.torod.db.postgresql.meta.TorodbSchema;
import com.torodb.torod.db.postgresql.meta.tables.records.CollectionsRecord;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import org.jooq.Field;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.UniqueKey;
import org.jooq.impl.SQLDataType;
import org.jooq.impl.TableImpl;

@SuppressFBWarnings("EQ_DOESNT_OVERRIDE_EQUALS")
public class CollectionsTable extends TableImpl<CollectionsRecord> {

	private static final long serialVersionUID = 740755688;

	/**
	 * The singleton instance of <code>torodb.collections</code>
	 */
	public static final CollectionsTable COLLECTIONS = new CollectionsTable();

	/**
	 * The class holding records for this type
     * @return 
	 */
	@Override
	public Class<CollectionsRecord> getRecordType() {
		return CollectionsRecord.class;
	}

	/**
	 * The column <code>torodb.collections.name</code>.
	 */
	public final TableField<CollectionsRecord, String> NAME 
            = createField("name", SQLDataType.VARCHAR.nullable(false), this, "");

	/**
	 * The column <code>torodb.collections.schema</code>.
	 */
	public final TableField<CollectionsRecord, String> SCHEMA 
            = createField("schema", SQLDataType.VARCHAR.nullable(false), this, "");

	/**
	 * The column <code>torodb.collections.capped</code>.
	 */
	public final TableField<CollectionsRecord, Boolean> CAPPED 
            = createField("capped", SQLDataType.BOOLEAN.nullable(false), this, "");

	/**
	 * The column <code>torodb.collections.max_size</code>.
	 */
	public final TableField<CollectionsRecord, Integer> MAX_SIZE 
            = createField("max_size", SQLDataType.INTEGER.nullable(false), this, "");

	/**
	 * The column <code>torodb.collections.max_elementes</code>.
	 */
	public final TableField<CollectionsRecord, Integer> MAX_ELEMENTES 
            = createField("max_elementes", SQLDataType.INTEGER.nullable(false), this, "");

	/**
	 * The column <code>torodb.collections.other</code>.
	 */
	public final TableField<CollectionsRecord, String> OTHER 
            = createField("other", SQLDataType.VARCHAR, this, "");
    
    public final TableField<CollectionsRecord, String> STORAGE_ENGINE 
            = createField("storage_engine", SQLDataType.VARCHAR.nullable(false), this, "");

	/**
	 * Create a <code>torodb.collections</code> table reference
	 */
	public CollectionsTable() {
		this("collections", null);
	}

	/**
	 * Create an aliased <code>torodb.collections</code> table reference
	 */
	public CollectionsTable(String alias) {
		this(alias, CollectionsTable.COLLECTIONS);
	}

	private CollectionsTable(String alias, Table<CollectionsRecord> aliased) {
		this(alias, aliased, null);
	}

	private CollectionsTable(String alias, Table<CollectionsRecord> aliased, Field<?>[] parameters) {
		super(alias, TorodbSchema.TORODB, aliased, parameters, "");
	}
    
    public String getSQLCreationStatement(Connection c)
            throws SQLException {
        return "CREATE TABLE \""
                + getSchema().getName() +"\".\"" + getName() + "\"("
                + "name varchar primary key, "
                + "schema varchar not null unique, "
                + "capped boolean not null, "
                + "max_size int not null, "
                + "max_elementes int not null, "
                + "other jsonb,"
                + "storage_engine varchar not null)";
    }

	/**
	 * {@inheritDoc}
	 */
	@Override
	public UniqueKey<CollectionsRecord> getPrimaryKey() {
		return TorodbKeys.COLLECTIONS_PKEY;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<UniqueKey<CollectionsRecord>> getKeys() {
		return Arrays.<UniqueKey<CollectionsRecord>>asList(TorodbKeys.COLLECTIONS_PKEY, 
                TorodbKeys.COLLECTIONS_SCHEMA_KEY
        );
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CollectionsTable as(String alias) {
		return new CollectionsTable(alias, this);
	}

	/**
	 * Rename this table
	 */
	public CollectionsTable rename(String name) {
		return new CollectionsTable(name, null);
	}

    public boolean isSemanticallyEquals(Table table) {
        if (!table.getName().equals(getName())) {
            return false;
        }
        if (table.getSchema() == null || !getSchema().getName().equals(table.getSchema().getName())) {
            return false;
        }
        if (table.fields().length != 7) {
            return false;
        }
        return true; //TODO: improve the check
    }
}
