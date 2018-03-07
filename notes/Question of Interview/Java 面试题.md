# Java面试题 #
目录: 

	- java中==和equals和hashCode的区别
	- int、char、long各占多少字节数
	- int与integer的区别
	- 探探对java多态的理解
	- String、StringBuffer、StringBuilder区别
	- 什么是内部类？内部类的作用
	- 抽象类和接口区别
	- 抽象类的意义
	- 抽象类与接口的应用场景
	- 抽象类是否可以没有方法和属性？
	- 接口的意义
	- 泛型中extends和super的区别
	- 父类的静态方法能否被子类重写
	- 进程和线程的区别
	- final，finally，finalize的区别
	- 序列化的方式
	- Serializable 和Parcelable 的区别
	- 静态属性和静态方法是否可以被继承？是否可以被重写？以及原因？
	- 静态内部类的设计意图
	- 成员内部类、静态内部类、局部内部类和匿名内部类的理解，以及项目中的应用
	- 谈谈对kotlin的理解
	- 闭包和局部内部类的区别
	- string 转换成 integer的方式及原理

- java中==和equals和hashCode的区别

	- `==`: 比较两个对象的地址；
	- `equal()`: `Object`类的方法，默认比较两个对象地址是否相等；
	- `hashCode()`: `Object`类的方法，默认返回对象在内存中地址转换成的一个int值，**任何对象的hashCode()方法都是不相等的**。



- int、char、long各占多少字节数
	
		boolean:short:char:int:long = 1:1:2:4:8
		float:double = 4:8

- int与integer的区别

	基本数据类型通过装包变成包装类(可序列化的Object子类),对象可以有自己的缓存,hashCode()

- 探探对java多态的理解

	一龙生九子九子各不同。

- String、StringBuffer、StringBuilder区别
	
	- String: Java中对String对象进行的操作实际上是一个不断创建新的对象并且将旧的对象回收的一个过程，所以执行速度很慢。
	- StringBuffer: 线程安全，很多方法可以带有synchronized关键字
	- StringBuilder: 线程不安全
	
		执行速度: StringBuilder > StringBuffer > String
		

- 什么是内部类？内部类的作用
- 抽象类和接口区别

		继承抽象类是为了使用抽象类的属性和行为;
		实现接口只是为了使用接口的行为.
- 接口的意义
- 抽象类的意义
- 抽象类与接口的应用场景
- 抽象类是否可以没有方法和属性？


- 泛型中extends和super的区别


- 父类的静态方法能否被子类重写


- [**进程和线程的区别**](http://blog.csdn.net/sunhuaqiang1/article/details/52687518)
- final，finally，finalize的区别

		当垃圾回收器将无用对象从内存中清除时，该对象的finalize()方法被调用以整理资源或者执行其他的清理工作。

- 序列化的方式


- Serializable 和Parcelable 的区别


- 静态属性和静态方法是否可以被继承？是否可以被重写？以及原因？


- 静态内部类的设计意图


- 成员内部类、静态内部类、局部内部类和匿名内部类的理解，以及项目中的应用


- 谈谈对kotlin的理解


- 闭包和局部内部类的区别
	
		局部内部类相当于外部类函数的局部变量,不存在修饰符;
		
		

- string 转换成 integer的方式及原理