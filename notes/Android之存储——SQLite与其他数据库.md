# SQLite 操作  #
---
- 目录

	```
	1. SQLite ( SQLite 命令中 以"[]"包含的内容表示可省略 )
		- SQLiteHelper
		- 操作
			- 创建数据库
			- 增
			- 删
			- 改
			- 查
			- 关闭数据库
			- 删除数据库
		- SQL语句中的子句
		- SQLite 位运算符

	2. 其他数据库
		- Realm
		- DBFlow
		
	```
- 参考
	- [ **SQLite 教程 | 菜鸟教程** ](http://www.runoob.com/sqlite/sqlite-tutorial.html)
	- [ **Android ：这是一份详细 & 全面的 SQLlite数据库 使用手册** ](https://juejin.im/post/5a5bfc016fb9a01ca10ae0a9#heading-5)
	- [ **玩转SQLite系列** ](http://blog.csdn.net/linglongxin24/article/details/53230842)


---
## SQLite ##

![Sqlite](https://i.imgur.com/CHgDAL0.png)

<font color = red >**字段支持 **</font>
<font color = red > > `NULL`、`INTEGER`、`FLOAT`、`STRING`、`BLOB ` </font>




### 1. `SQLiteHelper` ###

```java
public class MySqliteHelper extends SQLiteOpenHelper {


    public MySqliteHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}

```

### 2. 数据库操作 ###

- 创建或获取数据库

	- 打开或创建原始位置的数据库

		```java
		PersonalSQLiteHelper helper = new PersonalSQLiteHelper(this, "db_name", 1);
		SQLiteDatabase db0 = helper.getReadableDatabase();
		//获取位置信息
		List<Pair<String, String>> attachedDbs = db0.getAttachedDbs();
		String path = attachedDbs.get(0).first + attachedDbs.get(0).second;
		```

	- 打开指定位置的数据库

		```java
		SQLiteDatabase db1 = SQLiteDatabase.openOrCreateDatabase(filePath, null);
		SQLiteDatabase db2 = SQLiteDatabase.openOrCreateDatabase(file, null);
		//获取位置信息
		List<Pair<String, String>> attachedDbs = db1.getAttachedDbs();
		String path = attachedDbs.get(0).first + attachedDbs.get(0).second;
		```

	- 打开或创建指定操作模式的数据库

		```java
		SQLiteDatabase db3 = this.openOrCreateDatabase("db_name", MODE_PRIVATE, null, null);
		//获取位置信息
		List<Pair<String, String>> attachedDbs = db3.getAttachedDbs();
		String path = attachedDbs.get(0).first + attachedDbs.get(0).second;
		```

- 创建表单`(create)`
	- SQL 语句
		```
		CREATE TABLE IF NOT EXISTS 表名(	
				列名  列类型(大小)  属性,
				列名  列类型(大小)  属性,
				列名  列类型(大小)  属性
				... )
		eg:
			CREATE TABLE IF NOT EXISTS tableName(_id integer primary key autoincrement, name varchar(20) not null , ...)
		```
- 删除表单`(drop)`
	- SQL 语句
		```
		DROP TABLE IF  EXISTS 表名	
		eg:
			DROP TABLE IF EXISTS tableName
		```
		
- 增`(insert)`

	- SQL 语句
		```
		INSERT INTO TABLE_NAME [(column1, column2, column3,...columnN)]  VALUES (value1, value2, value3,...valueN)
				
		eg:
			- INSERT INTO User VALUES (1,'张三',26)
			- INSERT INTO User(id,name,age) VALUES (1,'张三',26)
		```	
		
		```
		// 从一张表填充到另一个表
		INSERT INTO first_table_name [(column1, column2, ... columnN)]  SELECT column1, column2, ...columnN 
   			FROM second_table_name [WHERE condition]

		```
	- API

		```java
		    /**
		     * 给user表中新增一条数据
		     * <p>
		     * long insert(String table, String nullColumnHack, ContentValues values)
		     * 第一个参数：数据库表名
		     * 第二个参数：当values参数为空或者里面没有内容的时候，
		     * insert是会失败的(底层数据库不允许插入一个空行)，
		     * 为了防止这种情况，要在这里指定一个列名，
		     * 到时候如果发现将要插入的行为空行时，
		     * 就会将你指定的这个列名的值设为null，然后再向数据库中插入。
		     * 第三个参数：要插入的值
		     * 返回值：成功操作的行号，错误返回-1
		     *
		     * @param v
		     */
		    public void insert(View v) {
		        String table = "user";
		        ContentValues contentValues = new ContentValues();
		        contentValues.put("name", "张三");
		        contentValues.put("age", 25);
		        long num = sqLiteDatabase.insert(table, null, contentValues);
		        if (num == -1) {
		            Toast.makeText(this, "插入失败", Toast.LENGTH_SHORT).show();
		        } else {
		            Toast.makeText(this, "成功插入到第" + num + "行", Toast.LENGTH_SHORT).show();
		        }
		    }
		
		```
	- <font color = red>**API 批量操作的优化**</font>

		```java 
			long startTime = System.currentTimeMillis();
			String table = "user";
			/**开启一个事务**/
			sqLiteDatabase.beginTransaction();
			try {
		            for (int i = 0; i < 100; i++) {
		                ContentValues contentValues = new ContentValues();
		                contentValues.put("name", "张三" + i);
		                contentValues.put("age", i);
		                sqLiteDatabase.insertOrThrow(table, null, contentValues);
		            }
		            /**将数据库事务设置为成功**/
		            sqLiteDatabase.setTransactionSuccessful();
			} catch (Exception e) {
		            e.printStackTrace();
			} finally {
		            /**结束数据库事务**/
		            sqLiteDatabase.endTransaction();
			}
		
		```
- 删`(delete)`
	- SQL 语句
		```
		DELETE FROM table_name WHERE [condition]
		
		eg:
			DELETE FROM User WHERE id=2
			DELETE FROM COMPANY //删除所有记录
		```
	- API

		```java
		 /**
		     * 删除user表中id为2的记录
		     * <p>
		     * int delete(String table, String whereClause, String[] whereArgs)
		     * 第一个参数：删除的表名
		     * 第二个参数：修改的条件的字段
		     * 第三个参数：修改的条件字段对应的值
		     * 返回值：影响的行数
		     *
		     * @param v
		     */
		    public void delete(View v) {
		    	//1
		        //String table = "user";
		        //String where = "id=2";
		        //sqLiteDatabase.delete(table, where, null);
		        
		        //2
		        String table = "user";
		        String where = "id=?";
		        String[] whereArgs = new String[]{"2"};
		        int num = sqLiteDatabase.delete(table, where, whereArgs);
		    }
		```
- 改`(update)`
	- SQL 语句
		```
		UPDATE table_name SET column1 = value1, column2 = value2.... WHERE [condition]
		//可以使用 AND 或 OR 运算符来结合 N 个数量的条件
		
		eg:
			UPDATE User SET name="李四" WHERE id=2;
			UPDATE COMPANY SET ADDRESS = 'Texas' WHERE ID = 6;
			UPDATE COMPANY SET ADDRESS = 'Texas', SALARY = 20000.00;//修改所有
		```
	- API

		```java
		     /* UPDATE User SET name="李四" WHERE id=2; */
		     public void update(View v) {
		         /**
		          * 方式一
		          */		
		         //String table = "user";
		         //ContentValues contentValues = new ContentValues();
		         //contentValues.put("name", "李四");
		         //String where = "id=2";
		         //sqLiteDatabase.update(table, contentValues, where, null);

		         /**
		          * 方式二
		          */
		         String table = "user";
		         ContentValues contentValues = new ContentValues();
		         contentValues.put("name", "李四");
		         String where = "id=?";
		         String[] whereArgs = new String[]{"2"};
		         int num = sqLiteDatabase.update(table, contentValues, where, whereArgs);
		         Toast.makeText(this, "修改了" + num + "行", Toast.LENGTH_SHORT).show();
		     }
		```

- 查`(select)`
	- SQL 语句
		```
		SELECT * 		FROM 表名 WHERE 查询的条件表达式  GROUP BY 分组的字段 ORDER BY 排序的字段
		SELECT 字段名 	FROM 表名 WHERE 查询的条件表达式  GROUP BY 分组的字段 ORDER BY 排序的字段
		
		eg:
			SELECT * FROM  User
			SELECT * FROM  User WHERE id=2
			
			SELECT name,age FROM  User WHERE age>25
			SELECT name,age FROM  User WHERE age BETWEEN 20 AND 40
			SELECT name,age FROM  User WHERE name LIKE "亮"
			SELECT name,age FROM  User WHERE name IS NULL
			SELECT name,age FROM  User ORDER BY age
		
		//查询过程		
		public void query(View v) {

			String sql = "SELECT * FROM user";
			/***这里得到的是一个游标*/
			Cursor cursor = sqLiteDatabase.rawQuery(sql, null, null);
			if (cursor == null) {
				return;
			}
			/***循环游标得到数据*/
			while (cursor.moveToNext()) {
				Log.d(Contacts.TAG,
					"id=" + cursor.getInt(0) 
					+ "，name=" + cursor.getString(1)
					+ "，age=" + cursor.getInt(2));
				int id = cursor.getInt(cursor.getColumnIndex("id"));
			}
			/***记得操作完将游标关闭*/
			cursor.close();
		}
		```

- 关闭数据库

	```java
	//查询的游标
	queryCursor.close();
	//数据库对象
	sqLiteDatabase.close();
	
	```
- 删除数据库,通过删除文件进行删除操作

	```java
		//关闭数据库文件及操作
		db2.close();
		String path1 = db2.getPath();
		File file = new File(path1);
		if (file.exists() && file.isFile()) {
			try {
				file.delete();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	```
### 3. SQL语句中的子句 ###

- `WHERE`
	1. 比较 / 逻辑运算符: >、<、=
	2. 与( AND )、或( OR 、  IN ( value1, value2 ) )、非( IS NOT )、包含( LIKE / GLOB  )
	3. 筛选语句,先子后父

```
	eg:
	SELECT * FROM COMPANY WHERE AGE >= 25 AND SALARY >= 65000;
	SELECT * FROM COMPANY WHERE AGE IS NOT NULL;	
	
	SELECT * FROM COMPANY WHERE NAME LIKE 'Ki%'; 
	SELECT * FROM COMPANY WHERE NAME GLOB 'Ki*';
	// LIKE 'Ki%' 与 GLOB 'Ki*' 相同 , 均表示 以 'Ki' 开始的所有记录，'Ki' 之后的字符不做限制;
	
	//列出了 AGE 的值为 25 或 27 的所有记录
	SELECT * FROM COMPANY WHERE AGE IN ( 25, 27 );
	//列出了 AGE 的值既不是 25 也不是 27 的所有记录
	SELECT * FROM COMPANY WHERE AGE NOT IN ( 25, 27 );
	//列出了 AGE 的值在 25 与 27 之间的所有记录
	SELECT * FROM COMPANY WHERE AGE BETWEEN 25 AND 27;
	
	//筛选结果 要包含子句筛选结果
	SELECT AGE FROM COMPANY WHERE 	EXISTS (SELECT AGE FROM COMPANY WHERE SALARY > 65000);
	// 列出了比 SALARY大于65000的值的AGE项 更大的值的所有信息
	SELECT * FROM COMPANY WHERE 	AGE > (SELECT AGE FROM COMPANY WHERE SALARY > 65000);

```

- `ORDER BY`
排序操作。使用 `ASC | DESC`设置升序降序。

```
	SELECT column-list FROM table_name [WHERE condition] [ORDER BY column1, column2, .. columnN] [ASC | DESC];
	
```
- `GROUP BY`
对相同的数据进行分组。

```	
	SELECT column0, SUM(column1) FROM tabName 	[GROUP BY column0] 	[ORDER BY column0][ASC | DESC];
	
	eg:
		//筛选出带有 NAME 与 SALARY 项的信息,根据NAME字段排序,并将相同NAME字段的SALARY合并为SUM(SALARY) ;
		SELECT NAME, SUM(SALARY) FROM COMPANY 	GROUP BY NAME 	ORDER BY NAME	ASC;//升序
		SELECT NAME, SUM(SALARY) FROM COMPANY 	GROUP BY NAME 	ORDER BY NAME 	DESC;//降序
```
- `LIKE`
用来匹配通配符指定模式的文本值。如果搜索表达式与模式表达式匹配，LIKE 运算符将返回真（true），也就是 1。
通配符有" % "、" _ ":
	- " % " : 零个、一个或多个数字或字符；
	- " _ " : 代表一个单一的数字或字符。

<font color = red>**示例**</font>:

`LIKE`语句	|	描述
:-|:-
WHERE SALARY LIKE '200%'	|查找以 200 开头的任意值
WHERE SALARY LIKE '%200%'	|查找任意位置包含 200 的任意值
WHERE SALARY LIKE '_00%'	|查找第二位和第三位为 00 的任意值
WHERE SALARY LIKE '2_%_%'	|查找以 2 开头，且长度至少为 3 个字符的任意值
WHERE SALARY LIKE '%2'		|查找以 2 结尾的任意值
WHERE SALARY LIKE '_2%3'	|查找第二位为 2，且以 3 结尾的任意值
WHERE SALARY LIKE '2___3'	|查找长度为 5 位数，且以 2 开头以 3 结尾的任意值


- `GLOB`
用来匹配通配符指定模式的文本值。如果搜索表达式与模式表达式匹配，GLOB 运算符将返回真（true），也就是 1。<font color = red>**大小写敏感**</font>。

	- 星号 （*） : 代表零个、一个或多个数字或字符。
	- 问号 （?） : 代表一个单一的数字或字符。

<font color = red>**示例**</font>:

`GLOB`语句	|	描述
:-|:-
WHERE SALARY GLOB '200*'	|查找以 200 开头的任意值
WHERE SALARY GLOB '\*200\*'	|查找任意位置包含 200 的任意值
WHERE SALARY GLOB '?00*'	|查找第二位和第三位为 00 的任意值
WHERE SALARY GLOB '2??'		|查找以 2 开头，且长度至少为 3 个字符的任意值
WHERE SALARY GLOB '*2'		|查找以 2 结尾的任意值
WHERE SALARY GLOB '?2*3'	|查找第二位为 2，且以 3 结尾的任意值
WHERE SALARY GLOB '2???3'	|查找长度为 5 位数，且以 2 开头以 3 结尾的任意值


- `HAVING`
HAVING 子句必须放在 GROUP BY 子句之后，必须放在 ORDER BY 子句之前。

```
	SELECT column1, column2 FROM table1, table2 WHERE [ conditions ]
		GROUP BY column1, column2 HAVING [ conditions ] ORDER BY column1, column2
	
	eg:
		//name 字段 重复次数小于2的内容按名称排序
		SELECT * FROM COMPANY GROUP BY name HAVING count(name) < 2;
```

---
### 4. SQLite 位运算符  ###


位运算符作用于位，并逐位执行操作。真值表 & 和 | 如下：

p| q | p & q | p &#124; q
:-:|:-:|:-:|:-:
0	|0	|0	|0
0	|1	|0	|1
1	|1	|1	|1
1	|0	|0	|1
假设如果 A = 60，且 B = 13，现在以二进制格式，它们如下所示：
A = 0011 1100;
B = 0000 1101;
A&B = 0000 1100;
A|B = 0011 1101;
~A  = 1100 0011;
下表中列出了 SQLite 语言支持的位运算符。假设变量 A=60，变量 B=13，则：

运算符	|	描述	|实例
-|-|-
&	|如果同时存在于两个操作数中，二进制 AND 运算符复制一位到结果中。|( A & B ) =12，即为 0000 1100
&#124;	|如果存在于任一操作数中，二进制 OR 运算符复制一位到结果中。	|( A &#124; B ) = 61，即为 0011 1101
~	|二进制补码运算符是一元运算符，具有"翻转"位效应，即0变成1，1变成0。	|( ~ A ) = -61，即为 1100 0011，一个有符号二进制数的补码形式。
<<	|二进制左移运算符。左操作数的值向左移动右操作数指定的位数。	|A << 2=240，即为 1111 0000
>>	|二进制右移运算符。左操作数的值向右移动右操作数指定的位数。	|A >> 2 =15，即为 0000 1111


---
