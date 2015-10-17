/**
 * Copyright (c) 2011-2015, James Zhan 詹波 (jfinal@126.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.jfinal.plugin.activerecord.dialect;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.Table;
import goja.app.GojaConfig;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Dialect.
 */
public abstract class Dialect {

	private static final Logger logger = LoggerFactory.getLogger(Dialect.class);

	public abstract String forTableBuilderDoBuild(String tableName);

	public abstract void forModelSave(Table table, Map<String, Object> attrs, StringBuilder sql, List<Object> paras);

	public abstract String forModelDeleteById(Table table);

	public abstract void forModelUpdate(Table table, Map<String, Object> attrs, Set<String> modifyFlag, StringBuilder sql, List<Object> paras);

	public abstract String forModelFindById(Table table, String columns);

	public abstract String forDbFindById(String tableName, String[] pKeys);

	public abstract String forDbDeleteById(String tableName, String[] pKeys);

	public abstract void forDbSave(StringBuilder sql, List<Object> paras, String tableName, String[] pKeys, Record record);

	public abstract void forDbUpdate(String tableName, String[] pKeys, Object[] ids, Record record, StringBuilder sql, List<Object> paras);

	public abstract void forPaginate(StringBuilder sql, int pageNumber, int pageSize, String select, String sqlExceptSelect);

	public boolean isOracle() {
		return false;
	}

	public boolean isTakeOverDbPaginate() {
		return false;
	}

	public Page<Record> takeOverDbPaginate(Connection conn, int pageNumber, int pageSize, String select, String sqlExceptSelect, Object... paras) throws SQLException {
		throw new RuntimeException("You should implements this method in " + getClass().getName());
	}

	public boolean isTakeOverModelPaginate() {
		return false;
	}

	@SuppressWarnings("rawtypes")
	public Page takeOverModelPaginate(Connection conn, Class<? extends Model> modelClass, int pageNumber, int pageSize, String select, String sqlExceptSelect, Object... paras) throws Exception {
		throw new RuntimeException("You should implements this method in " + getClass().getName());
	}

	public void fillStatement(PreparedStatement pst, List<Object> paras) throws SQLException {
		  /* # edit by sogyf. */
		/* @description:  when dev model print sql parm*/
		final int param_size = paras.size();
		boolean show_param = GojaConfig.getApplicationMode().isDev();
		if (show_param) {
			logger.debug("Sql param size : {}", param_size == 0 ? " Empty" : param_size);

			for (int i = 0; i < param_size; i++) {
				final Object param = paras.get(i);
                if (param instanceof DateTime)
                    pst.setTimestamp(i + 1, new Timestamp(((DateTime) param).getMillis()));
                else
				    pst.setObject(i + 1, param);
				logger.debug("   param index: {}, param type: {}, param value: {}. ", i + 1, (param == null ? "null" : param.getClass().getSimpleName()), param);
			}
			logger.debug("Sql param end!");
		} else {
			for (int i = 0; i < param_size; i++) {
                final Object param = paras.get(i);
                if (param instanceof DateTime)
                    pst.setTimestamp(i + 1, new Timestamp(((DateTime) param).getMillis()));
                else
                    pst.setObject(i + 1, param);
			}
		}
		/* # end edited. */
	}

	public void fillStatement(PreparedStatement pst, Object... paras) throws SQLException {
		  /* # edit by sogyf. */
		/* @description: when dev model print sql parm */
        boolean show_param = GojaConfig.getApplicationMode().isDev();
		final int param_size = paras.length;
		if (show_param) {
			logger.debug("Sql param size : {}", param_size == 0 ? " Empty" : param_size);

			for (int i = 0; i < param_size; i++) {
				final Object param = paras[i];
                if (param instanceof DateTime)
                    pst.setTimestamp(i + 1, new Timestamp(((DateTime) param).getMillis()));
                else
                    pst.setObject(i + 1, param);
				logger.debug("   param index: {}, param type: {}, param value: {}. ", i + 1, (param == null ? "null" : param.getClass().getSimpleName()), param);
			}
			logger.debug("Sql param end!\n");
		} else {
			for (int i = 0; i < param_size; i++) {
                final Object param = paras[i];
                if (param instanceof DateTime)
                    pst.setTimestamp(i + 1, new Timestamp(((DateTime) param).getMillis()));
                else
                    pst.setObject(i + 1, param);
			}
		}
		/* # end edited. */
	}

	public String getDefaultPrimaryKey() {
		return "id";
	}

	protected boolean isPrimaryKey(String colName, String[] pKeys) {
		for (String pKey : pKeys)
			if (colName.equalsIgnoreCase(pKey))
				return true;
		return false;
	}

	/**
	 * 一、forDbXxx 系列方法中若有如下两种情况之一，则需要调用此方法对 pKeys 数组进行 trim():
	 * 1：方法中调用了 isPrimaryKey(...)：为了防止在主键相同情况下，由于前后空串造成 isPrimaryKey 返回 false
	 * 2：为了防止 tableName、colName 与数据库保留字冲突的，添加了包裹字符的：为了防止串包裹区内存在空串
	 * 如 mysql 使用的 "`" 字符以及 PostgreSql 使用的 "\"" 字符
	 * 不满足以上两个条件之一的 forDbXxx 系列方法也可以使用 trimPrimaryKeys(...) 方法让 sql 更加美观，但不是必须
	 * <p/>
	 * 二、forModelXxx 由于在映射时已经trim()，故不再需要调用此方法
	 */
	protected void trimPrimaryKeys(String[] pKeys) {
		for (int i = 0; i < pKeys.length; i++)
			pKeys[i] = pKeys[i].trim();
	}
}






