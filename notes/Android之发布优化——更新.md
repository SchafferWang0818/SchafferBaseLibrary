# bugly(Tinker包装) 热更新

[视频链接](http://v.qq.com/boke/gplay/9f3b4b1232819f453becd2356a3493c4_bme000301803d13_5_w0384j4xrnd.html)

---

### 局限性

1. 不修改清单文件;
2. 不支持GooglePlay渠道;
3. Android N ,启动时间会延长;
4. sansung android-21 抛异常;
5. 厂商加固;
6. 资源替换,不支持remoteView,transition动画等,通知icon,桌面icon;

---
### 流程 ###

1. 发现问题上传热补丁,存在问题版本tinkerId字段和补丁包tinkerId;
2. 问题版本 启动后联网;
3. 后台获取版本号对应问题版本号和tinkerId;
4. 前台下载补丁;
5. 前台重启合成修复补丁,激活数+1;

---

### 对特定基准包打热补丁包

1. 指定固定路径的基准包
	
		build/bakApk/____________/渠道/app-渠道.apk
		//指定的tinkerId = "base-1.0.1"

2. 将下滑线部分内容copy至`tinker-support.gradle`的`baseApkDir`处;
	
		def baseApkDir = "app-0208-15-10-00"

3. 更改tinkerId保证唯一;

		tinkerId = "patch-1.0.1"

4. 更改热补丁需要修改的内容;

5. gradle命令完成打包

		:app/Tasks/tinker-support/buildTinkerPatch渠道名Release

6. 输出补丁包和更改记录等信息

		outputs/patch/release(or debug)/:补丁包
			apk下YAPATCH.MF记录基准包和补丁包tinkerId信息;
		outputs/tinkerPatch/:补丁更改信息

7. 联网告诉后台app当前版本
8. 上传`outputs/patch/`下的补丁包
9. 匹配成功后下发

---
### tinker多渠道打包 ###

tinker多渠道打包后

		`TINKER_ID` = 渠道名称 + 打包类型 + 定义的tinkerId,
		`BUGLY_CHANNEL = 渠道名称`


