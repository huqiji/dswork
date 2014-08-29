package dswork.core.mybatis.dialect;

/**
 * Dialect for DB2
 * @author skey
 */
public class DB2Dialect extends Dialect
{
	/**
	 * 是否支持分页，limit和offset
	 * @return boolean
	 */
	public boolean supportsLimitOffset()
	{
		return true;
	}

	/**
	 * 将sql变成分页sql
	 * @param sql 原始sql语句
	 * @param offset 跳过行数
	 * @param limit 返回行数
	 * @return String
	 */
	public String getLimitString(String sql, int offset, int limit)
	{
		StringBuilder sb = new StringBuilder(sql.length() + 110);
		sb.append("select * from ( select ROW_NUMBER() over() rn,  _t.* from (")
		.append(sql)
		.append(") _t ")
		.append(") _n where _n.rn > ").append(offset).append(" and _n.rn <=").append(offset + limit);
		return sb.toString();
	}
}
