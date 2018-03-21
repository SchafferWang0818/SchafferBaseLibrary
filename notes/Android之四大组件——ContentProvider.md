---

title: Android之 ContentProvider
categories: "android 总结"
tags: 
	- ContentProvider

---

# ContentProvider #
<font color="blue"> **`ContentProvider` 由于操作函数在Binder线程池中被调用,不需要手动停止,所以 <font color="red">增删改查的操作需要处理线程同步 </font>** </font>




## Xml ##

```xml

<provider 
	android:authorities="list"
    android:enabled=["true" | "false"]
    android:exported=["true" | "false"]
    android:grantUriPermissions=["true" | "false"]
    android:icon="drawable resource"
    android:initOrder="integer"
    android:label="string resource"
    android:multiprocess=["true" | "false"]
    android:name="string"
    android:permission="string"
    android:process="string"
    android:readPermission="string"
    android:syncable=["true" | "false"]
    android:writePermission="string" >
    . . .
</provider>

```
- `android:authorities`
	- 一/多个 URI authority列表，标识了 `ContentProvider` 内提供的数据。 多个 authority 名称之间用分号分隔。
	- 一般来说，它是实现 `ContentProvider` 的子类名。 

- `android:enabled`
	- 是否允许被实例化

- `android:exported`
	- 是否被其他进程使用,"true"时受限于其声明的权限。 
	
- `android:initOrder`
	- 同一进程中`ContentProvider`的实例化顺序
	- 数值越大越先被初始化

- `android:multiprocess`
	- 每个客户端进程是否都能创建 `ContentProvider` 实例
	- 多个进程实例避免进程通讯开销

- `android:name`
	- 实现 `Content Provider` 的类名称，即 `ContentProvider` 的子类。 这应该是一个完全限定的类名称（比如“`com.example.project.TransportationProvider`”）。
	- 必须指定,无默认值.

- `android:syncable`
	- `Content Provider` 所控制的数据是否需要与某个服务器进行同步

- `android:grantUriPermissions`
	- 是否能临时超越 `readPermission`、 `writePermission`和 `permission `属性的限制， 给平常无权对 Content Provider 数据的访问进行授权
	-  默认值是“false”,只能对 **存在的**`<grant-uri-permission>` 子元素中列出的数据子集进行授权。
	-  **授权机制使得程序组件能对那些受权限保护的数据进行一次性的临时访问**。
		-  **这时候，可以通过设置启动组件的 Intent 对象的 `FLAG_GRANT_READ_URI_PERMISSION`和 `FLAG_GRANT_WRITE_URI_PERMISSION` 标志位进行授权。 比如，邮件程序可以在传入 `Context.startActivity()` 的 Intent 中设置 `FLAG_GRANT_READ_URI_PERMISSION`。 权限即指定授予该 Intent 中的 URI。**
		-  **如果启用了这种临时授权的特性，不论是将本属性设为“true”还是定义了 <grant-uri-permission> 子元素，那么当所涉及的 URI 要从 Content Provider 中删除时，必须调用一下 Context.revokeUriPermission()。**

				<grant-uri-permission 
					android:path="string"
                	android:pathPattern="string"
                	android:pathPrefix="string" />
				android:path: 指定完整路径，只能对该路径指定部分的数据子集进行授权。			
				android:pathPattern: 指定路径的起始部分，只能对那些以此为路径前缀的数据进行授权。
				android:pathPrefix: 指定完整路径，可包含通配符
										
						星号（'*'）匹配紧随其前字符的0次或多次出现。
						句点加星号（“.*”）匹配任何字符的0次或多次出现。


- `android:read/writePermission`
	- 查询/修改 `Content Provider`数据的客户端所必需的权限

- `android:permission`
	- 客户端查询/写入/修改 `ContentProvider`中数据必须的权限名称.
	- 一次性读写权限的快捷途径,**优先级低于`android:read/writePermission`**
	- 如果设置了`writePermission`属性，则其也将控制对 `Content Provider` 数据的修改。
---
## 特性 ##

---
## Java ##


---