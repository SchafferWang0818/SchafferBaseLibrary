---

title: Android之 HTTP/HTTPS 
categories: "android 总结"
tags: 
	- HTTP
	- HTTPS 

---
# HTTP/HTTPS 基础与版本 #

---

	目录:
		- http
			- 原理
			- 结构
			- 请求报文格式
			- 答复报文格式
			- 状态码
			- 版本
		- http与https的区别
		- https
			- 优点
			- 加密
			


---
### HTTP(HyperText Transport Protocol) ###
		超文本(超过计算机处理文本信息的方法)传输协议,最初为了提供发布和接收html页面的方法,运行在TCP/IP协议簇之上;
		服务器=html+http驻留程序(用于响应用户请求)

1. 1.0处理完请求之后就会释放连接;
2. 1.1请求可以多次重叠;
3. 客户端固定时间段向服务器发送保持连接请求,服务器收到请求后回复确认客户端在线;当长时间没有收到请求,则认为网络已经断开.


#### 原理 ####


1. **首先客户机与服务器需要建立连接。只要单击某个超级链接，HTTP的工作就开始了。**

2. **建立连接后，客户机发送一个请求给服务器，请求方式的格式为：统一资源标识符（URL）、协议版本号，后边是MIME信息包括请求修饰符、客户机信息和可能的内容。**

3. **服务器接到请求后，给予相应的响应信息，其格式为一个状态行，包括信息的协议版本号、一个成功或错误的代码，后边是MIME信息包括服务器信息、实体信息和可能的内容。**

4. **客户端接收服务器所返回的信息通过浏览器显示在用户的显示屏上，然后客户机与服务器断开连接。**


#### 结构 ####


		- 模型: 请求/响应模型.
		- 请求头包含:
			- 请求的方法
			- URL
			- 协议版本
			- 类似于MIME的消息结构
				- 请求修饰符
				- 客户信息
				- 内容
		- 响应包含:
			- 消息协议的版本
			- 成功/错误编码
			- 包含服务器信息、实体元信息以及可能的实体内容。
		- 请求/响应内容包括:
			- 一个起始行
			- 一个或者多个头域
				- 通用头
				- 请求头
				- 响应头
				- 实体头
			- 一个指示头域结束的空行
			- 可选的消息体

#### 请求报文格式 ####

		请求行 － 通用信息头 － 请求头 － 实体头 － 报文主体

		请求行以方法字段开始，后面分别是 URL 字段和 HTTP 协议版本字段，并以 CRLF 结尾。
		SP 是分隔符。除了在最后的 CRLF 序列中 CF 和 LF 是必需的之外，其他都可以不要。
		有关通用信息头，请求头和实体头方面的具体内容可以参照相关文件。
#### 应答报文格式 ####

		状态行 － 通用信息头 － 响应头 － 实体头 － 报文主体

		状态码元由3位数字组成，表示请求是否被理解或被满足。原因分析是对原文的状态码作简短的描述，
		状态码用来支持自动操作，而原因分析用来供用户使用。客户机无需用来检查或显示语法。
		有关通用信息头，响应头和实体头方面的具体内容可以参照相关文件。


#### 状态码 ####

		1xx: 信息响应类，表示接收到请求并且继续处理
		2xx: 处理成功响应类，表示动作被成功接收、理解和接受
		3xx: 重定向响应类，为了完成指定的动作，必须接受进一步处理
		4xx: 客户端错误，客户请求包含语法错误或者是不能正确执行
		5xx: 服务端错误，服务器不能正确执行一个正确的请求


#### 版本 ####

		HTTP/0.9　已过时。只接受 GET 一种请求方法，没有在通讯中指定版本号，且不支持请求头。
				由于该版本不支持 POST 方法，所以客户端无法向服务器传递太多信息。
		HTTP/1.0　这是第一个在通讯中指定版本号的HTTP 协议版本，至今仍被广泛采用，特别是在代理服务器中。
		HTTP/1.1　当前版本。持久连接被默认采用，并能很好地配合代理服务器工作。
				还支持以管道方式同时发送多个请求，以便降低线路负载，提高传输速度。

---

### HTTPS 与 HTTP 的区别 ###

- 证书: HTTPS有需要缴费的**ca证书**；
- 端口: HTTPS使用**443端口**,HTTP使用80端口；
- 安全: HTTPS有作为**应用层子层的,加密传输,身份认证**的SSL层;HTTP是位于传输层，明文传输，无状态无记忆能力的连接；
---
### HTTPS(Hyper Text Transfer Protocol over Secure Socket Layer) ###

		加入SSL层,以安全为目标的HTTP通道。

		以安全为目标的HTTP通道,添加的内容是SSl完全套接字层,主要进行详细内容的加密,Https存在不同于Http的默认端口,在Http和TCP之间还有加密/身份证明层,本来用于身份证明和加密通讯方法.
			
		　　客户端在使用HTTPS方式与Web服务器通信时有以下几个步骤，如图所示。

				（1）客户使用https的URL访问Web服务器，要求与Web服务器建立SSL连接。
				
				（2）Web服务器收到客户端请求后，会将网站的证书信息（证书中包含公钥）传送一份给客户端。
				
				（3）客户端的浏览器与Web服务器开始协商SSL连接的安全等级，也就是信息加密的等级。
				
				（4）客户端的浏览器根据双方同意的安全等级，建立会话密钥，然后利用网站的公钥将会话密钥加密，并传送给网站。
				
				（5）Web服务器利用自己的私钥解密出会话密钥。
				
				（6）Web服务器利用会话密钥加密与客户端之间的通信。

#### SSL层原理 ####


1. SSL 协议既用到了<font color = "red">**公钥加密技术**</font>又用到了<font color = "red">**对称加密技术**</font>，对称加密技术虽然比公钥加密技术的速度快，可是公钥加密技术提供了更好的身份认证技术。

2. 分为两层:

	- SSL记录协议（SSL Record Protocol）：它建立在可靠的传输协议（如TCP）之上，为高层协议提供<font color = "red">**数据封装、压缩、加密**</font>等基本功能的支持。
	- SSL握手协议（SSL Handshake Protocol）：它建立在SSL记录协议之上，用于<font color = "red">在实际的数据传输开始前，通讯双方进行身份认证、协商加密算法、交换加密密钥等。</font>

3. 工作流程

	1. 客户端Client: 向服务器发送一个开始信息“Hello”以便开始一个新的会话连接；
	2. 服务器Server: 根据客户的信息确定是否需要生成新的主密钥，如需要则服务器在响应客户的“Hello”信息时将包含生成主密钥所需的信息；
	3. 客户端Client: 根据收到的服务器响应信息，产生一个主密钥，并用服务器的公开密钥加密后传给服务器；
	4. 服务器Server: 恢复该主密钥，并返回给客户一个用主密钥认证的信息，以此让客户认证服务器。
		

		
			注:在此之前，服务器已经通过了客户认证，这一阶段主要完成对客户的认证。
			经认证的服务器发送一个提问给客户，客户则返回（数字）签名后的提问和其公开密钥，
			从而向服务器提供认证。

#### 握手过程 ####

为了便于更好的认识和理解SSL 协议，这里着重介绍SSL 协议的握手协议。SSL 协议既用到了公钥加密技术又用到了对称加密技术，对称加密技术虽然比公钥加密技术的速度快，可是公钥加密技术提供了更好的身份认证技术。SSL 的握手协议非常有效的让客户和服务器之间完成相互之间的身份认证，其主要过程如下：

	1. 客户端的浏览器向服务器传送客户端SSL 协议的版本号，加密算法的种类，产生的随机数，
		以及其他服务器和客户端之间通讯所需要的各种信息。

	2. 服务器向客户端传送SSL 协议的版本号，加密算法的种类，随机数以及其他相关信息，
		同时服务器还将向客户端传送自己的证书。

	3. 客户利用服务器传过来的信息验证服务器的合法性，服务器的合法性包括：证书是否过期，
	发行服务器证书的CA 是否可靠，发行者证书的公钥能否正确解开服务器证书的“发行者的数字签名”，
	服务器证书上的域名是否和服务器的实际域名相匹配。如果合法性验证没有通过，通讯将断开；
	如果合法性验证通过，将继续进行第四步。

	4. 用户端随机产生一个用于后面通讯的“对称密码”，然后用服务器的公钥（服务器的公钥从步骤②中的
	服务器的证书中获得）对其加密，然后将加密后的“预主密码”传给服务器。

	5. 如果服务器要求客户的身份认证（在握手过程中为可选），用户可以建立一个随机数然后对其进行数据签名，
	将这个含有签名的随机数和客户自己的证书以及加密过的“预主密码”一起传给服务器。

	6. 如果服务器要求客户的身份认证，服务器必须检验客户证书和签名随机数的合法性，具体的合法性验证过程包括：
	客户的证书使用日期是否有效，为客户提供证书的CA 是否可靠，发行CA 的公钥能否正确解开客户证书
	的发行CA 的数字签名，检查客户的证书是否在证书废止列表（CRL）中。检验如果没有通过，
	通讯立刻中断；如果验证通过，服务器将用自己的私钥解开加密的“预主密码”，
	然后执行一系列步骤来产生主通讯密码（客户端也将通过同样的方法产生相同的主通讯密码）。

	7. 服务器和客户端用相同的主密码即“通话密码”，一个对称密钥用于SSL协议的安全数据通讯的
	加解密通讯。同时在SSL 通讯过程中还要完成数据通讯的完整性，防止数据通讯中的任何变化。

	8. 客户端向服务器端发出信息，指明后面的数据通讯将使用的步骤⑦中的主密码为对称密钥，
	同时通知服务器客户端的握手过程结束。

	9. 服务器向客户端发出信息，指明后面的数据通讯将使用的步骤⑦中的主密码为对称密钥，
	同时通知客户端服务器端的握手过程结束。

	10. SSL 的握手部分结束，SSL 安全通道的数据通讯开始，客户和服务器开始使用相同的对称密钥
	进行数据通讯，同时进行通讯完整性的检验。


---
