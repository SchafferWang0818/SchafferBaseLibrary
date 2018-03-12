# SharedPreferences的使用 #
轻量级键值对存储形式.**可以被其他应用读取/写入.**


	目录:
		1. 存储位置
		2. 存储状态
		3. 存储方式

---

### 1. 存储位置

所有的SP均存储到**`data/data/packageName/sharedPrefs/`**目录下.

---
### 2. 存储状态
所有存储的内容都是将键值对存储到xml文件中.

	<?xml version='1.0' encoding='utf-8' standalone='yes' ?>  
	<map>  
	    <string name="name">Mr Lee</string>  
	    <int name="age" value="30" />  
	    <boolean name="married" value="true" />  
	    <float name="weight" value="100.0" />  
	</map>  


- SP的存储和读写模式有以下几种:

	- Context.MODE_PRIVATE

			默认的私有状态,覆盖原来的内容;

	- Context.MODE_APPEND

			在文件存在的情况下,追加到这个文件;

	- Context.MODE_WORLD _READABLE 

			可以被其他应用读取;

	- Context.MODE_WORLD _WRITEABLE

			可以被其他应用写入;

	- Context.MODE_MULTI_PROCESS

			多进程,非多进程安全

---
### 3. 存储方式

1. 存储

		SharedPreferences sps= getSharedPreferences("share", MODE_PRIVATE); 
		SharedPreferences.Editor editor = sps.edit();  
		editor.putString("name", "Mr Lee");  
		editor.putInt("age", 30);  
		editor.putBoolean("married", true);  
		editor.putFloat("weight", 100f);  
		editor.commit();  



2. 读取
		
		//读取其他app的sp
		try {
			otherAppContext = createPackageContext("com.package.name", Context.CONTEXT_IGNORE_SECURITY);
		} catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
		}
		//根据Context取得对应的SharedPreferences
		sp = otherAppContext.getSharedPreferences("share", Context.MODE_WORLD_READABLE);

		//读取自己的app
		SharedPreferences sps= getSharedPreferences("share", MODE_PRIVATE);    
		String name = sps.getString("name", "");  
		int age = sps.getInt("age", 0);  
		boolean married = sps.getBoolean("married", false);  
		float weight = sps.getFloat("weight", 0);  

---
