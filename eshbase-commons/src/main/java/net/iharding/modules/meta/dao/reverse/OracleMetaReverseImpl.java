package net.iharding.modules.meta.dao.reverse;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import net.iharding.modules.meta.model.DBTable;
import net.iharding.modules.meta.model.DataSource;
import net.iharding.modules.meta.model.Database;
import net.iharding.modules.meta.model.DbColumn;
import net.iharding.modules.meta.model.MetaProperty;

import org.apache.commons.lang.StringUtils;
import org.guess.sys.model.User;

public class OracleMetaReverseImpl  extends JDBCMetaReverse {


	@Override
	public DataSource reverseMeta(DataSource datasource, List<MetaProperty> mproes, User cuser) {
		datasource.addDatabase(reverseDatabaseMeta(datasource, mproes, cuser, getMetaProperty("database", mproes).getPropertyValue()));
		return datasource;
	}

	@Override
	public Database reverseDatabaseMeta(DataSource datasource, List<MetaProperty> mproes, User cuser, String dbName) {
		Connection conn = null;
		ResultSet rsSchema = null;
		ResultSet rs = null;
		Database db = null;
		try {
			conn = getConnection(mproes);
			DatabaseMetaData dmd = conn.getMetaData();
			db = datasource.getDatabase(dbName, cuser);
			db.setDatasource(datasource);
			db.setDbname(dbName);
			db.setCheckLabel(1);
			rsSchema = dmd.getSchemas();
			String schma = "";
			while (rsSchema.next()) {
				schma = schma + " " + rsSchema.getString("TABLE_SCHEM");
			}
			rsSchema.close();
			db.setSchemaName(schma);
			db.setRemark(db.getDbname());
			String[] types = { "TABLE", "VIEW" };
			rs = dmd.getTables(null, null, "%", types);
			while (rs.next()) {
				String tableName = rs.getString("TABLE_NAME");
				if (StringUtils.isNotEmpty(tableName)) {
					DBTable table = this.reverseTableMeta(datasource, mproes, cuser, db, dmd, rs, tableName);
					db.addTable(table);
				}
			}
			rs.close();
			datasource.addDatabase(db);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			closeResult(rs);
			closeResult(rsSchema);
			closeConnection(conn);
		}
		return db;
	}

	@Override
	public DBTable reverseTableMeta(DataSource datasource, List<MetaProperty> mproes, User cuser, String dbName, String tableName) {
		Connection conn = null;
		ResultSet rsSchema = null;
		ResultSet rs = null;
		ResultSet rsColumn = null;
		DBTable table =null;
		try {
			conn = getConnection(mproes);
			DatabaseMetaData dmd = conn.getMetaData();
			Database db = datasource.getDatabase(dbName, cuser);
			db.setDatasource(datasource);
			db.setDbname(dbName);
			db.setCheckLabel(1);
			rsSchema = dmd.getSchemas();
			String schma = "";
			while (rsSchema.next()) {
				schma = schma + " " + rsSchema.getString("TABLE_SCHEM");
			}
			rsSchema.close();
			db.setSchemaName(schma);
			db.setRemark(db.getDbname());
			String[] types = { "TABLE", "VIEW" };
			rs = dmd.getTables(null, null, tableName, types);
			while (rs.next()) {
				String tabName = rs.getString("TABLE_NAME");
				if (StringUtils.isNotEmpty(tableName)) {
					table = this.reverseTableMeta(datasource, mproes, cuser, db, dmd, rsColumn, tabName);
					break;
				}
			}
			rs.close();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			closeResult(rs);
			closeResult(rsSchema);
			closeConnection(conn);
		}
		return table;
	}

	public DBTable reverseTableMeta(DataSource datasource, List<MetaProperty> mproes, User cuser, Database db, DatabaseMetaData dmd, ResultSet rs, String tableName) {
		ResultSet rsColumn = null;
		DBTable table = null;
		try {
			if (StringUtils.isNotEmpty(tableName)) {
				table = db.getDBTable(tableName, cuser);// new DBTable();
				table.setTableName(tableName);
				table.setCheckLabel(1);
				String tablecomment = rs.getString("REMARKS");
				if (StringUtils.isNotEmpty(tablecomment)) {
					table.setTablePname(tablecomment);
					table.setRemark(tablecomment);
				} else {
					table.setTablePname(tableName);
					table.setRemark(tableName);
				}
				String tableType = rs.getString("TABLE_TYPE");
				if ("table".equalsIgnoreCase(tableType)) {
					table.setTableType(1);
				} else if ("view".equalsIgnoreCase(tableType)) {
					table.setTableType(2);
				}
				table.setClassName(this.sql2javaName(tableName));
				table.setDatabase(db);
				rsColumn = dmd.getColumns(getMetaProperty("database", mproes).getPropertyValue(), getMetaProperty("schema", mproes).getPropertyValue(), tableName, "%");
				while (rsColumn.next()) {
					int colType = rsColumn.getInt("DATA_TYPE");
					String sqlColName = rsColumn.getString("COLUMN_NAME");
					if (StringUtils.isNotEmpty(sqlColName)) {
						DbColumn col = table.getNewDbColumn(sqlColName);
						int colTypeLength = 0;
						int decimalLength = 0;
						String tit = "";
						try {
							colTypeLength = rsColumn.getInt("COLUMN_SIZE");
							decimalLength = rsColumn.getInt("DECIMAL_DIGITS");
							tit = rsColumn.getString("REMARKS");
						} catch (Exception ex) {
						}
						col.setFieldCode(this.sql2javaName(sqlColName));
						col.setColumnName(sqlColName);
						if ("".equalsIgnoreCase(tit) || tit == null) {
							col.setColumnPname(sqlColName);
						} else {
							col.setColumnPname(tit);
						}
						col.setRemark(col.getColumnPname());
						col.setCheckLabel(1);
						col.setDbtable(table);
						col.setType(convert(colType, colTypeLength, decimalLength));
						table.addColumn(col);
					}
				}
				rsColumn.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			closeResult(rsColumn);
		}
		return table;
	}

}
