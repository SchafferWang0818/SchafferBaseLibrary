# adb命令[Android Debug Bridge]

---
adb是一个标准的CS结构的工具, 连接开发电脑和调试手机.包含如下几个部分:

- Client端, 运行在PC机. 用来发送adb命令.
- Deamon守护进程, 运行在调试手机或模拟器中.
- Server端, 后台进程,运行在开发PC机. 用来管理PC中的Client端和手机的Deamon之间的通信.

		其他命令参考:http://www.jianshu.com/p/5980c8c282ef
---
### 基础 ###

	- adb start-server
		启动adb服务,如果它没启动的话
	- adb kill-server
		关闭服务
	- adb devices
		查看所连接的设备以及设备所对应的序列号
	- adb reboot
		重启
	- adb install xxxx.apk
		安装apk
		- adb install -s demo.apk  安装到SD卡
		- adb install -r xxxx.apk  保留数据和缓存文件，重新安装apk,如果连接了两台设备,
		则会报错,此时可以添加-s <serialNumber>来处理
	- adb uninstall <packagename>
		卸载app,有时候在手机上卸载App会出现数据清理不干净,导致App再也装不上了,这个时候可以敲命令来卸载
		- adb uninstall -k <packagename> 保留数据
	- adb connect <device-ip-address>
		连接到指定的ip,这个通常配合wifidebug
	- adb pull <remote> <local>
		从手机复制文件出来,比如把Crash日志写在SD卡上,再pull到电脑上 或者 pull ANR的trace日志
	- adb push <local> <remote>
		向手机发送文件,比如测试热修复补丁~
		eg. adb push foo.txt /sdcard/foo.txt

---
### 设备信息 ###

	- adb get-serialno
		查看序列号
	- adb shell  cat /sys/class/net/wlan0/address
		查看MAC地址
	- adb shell getprop ro.product.model
		查看设备型号
	- adb shell getprop ro.build.version.release
		查看Android系统版本
	- adb shell wm size
		查看屏幕分辨率
	- adb shell wm density
		查看屏幕密度
---
### 包管理 

	- adb shell pm list packages
		列出手机所有app的包名
		- adb shell pm list packages -s	
			系统应用
		- adb shell pm list packages -3
			系统应用外的其他应用
		- adb shell pm list packages | grep **
			grep筛选出包括**的包名

	- adb shell pm clear <packagename>
		清除应用的数据


	- adb shell am start -n <packagename>/<packagename>.<activityname>
		启动某个应用的某个Activity
	- adb shell am force-stop <packagename>
		强制停止某应用


---
### 其他 ###
	- adb shell
		进入shell环境
	- adb shell dumpsys activity top
		查看栈顶Activity,可以用来获取包名,可以用来查看其它app的包名
	- adb shell ps
		查看进程信息
	- adb shell pm list packages -f
		查看所有已安装的应用的包名
	- adb shell dumpsys activity
		dumpsys系列命令可以帮助我们查看各种信息
		am的状态 Activity Manager State
	- adb shell dumpsys package
		包信息 Package Information
	- adb shell dumpsys meminfo
		内存使用情况Memory Usage
	- adb shell cat /proc/cpuinfo
		查看手机CPU,可以看到手机架构(eg.ARMv7) 和几核处理器
		可以帮助我们选择so库,排查手机cpu架构相关的问题

---
### 附录:adb命令英文选项 ###


	global options:
	 -a         listen on all network interfaces, not just localhost
	 -d         use USB device (error if multiple devices connected)
	 -e         use TCP/IP device (error if multiple TCP/IP devices available)
	 -s SERIAL
	     use device with given serial number (overrides $ANDROID_SERIAL)
	 -p PRODUCT
	     name or path ('angler'/'out/target/product/angler');
	     default $ANDROID_PRODUCT_OUT
	 -H         name of adb server host [default=localhost]
	 -P         port of adb server [default=5037]
	 -L SOCKET  listen on given socket for adb server [default=tcp:localhost:5037]
	
	general commands:
	 devices [-l]             list connected devices (-l for long output)
	 help                     show this help message
	 version                  show version num
	
	networking:
	 connect HOST[:PORT]      connect to a device via TCP/IP [default port=5555]
	 disconnect [HOST[:PORT]]
	     disconnect from given TCP/IP device [default port=5555], or all
	 forward --list           list all forward socket connections
	 forward [--no-rebind] LOCAL REMOTE
	     forward socket connection using:
	       tcp:<port> (<local> may be "tcp:0" to pick any open port)
	       localabstract:<unix domain socket name>
	       localreserved:<unix domain socket name>
	       localfilesystem:<unix domain socket name>
	       dev:<character device name>
	       jdwp:<process pid> (remote only)
	 forward --remove LOCAL   remove specific forward socket connection
	 forward --remove-all     remove all forward socket connections
	 ppp TTY [PARAMETER...]   run PPP over USB
	 reverse --list           list all reverse socket connections from device
	 reverse [--no-rebind] REMOTE LOCAL
	     reverse socket connection using:
	       tcp:<port> (<remote> may be "tcp:0" to pick any open port)
	       localabstract:<unix domain socket name>
	       localreserved:<unix domain socket name>
	       localfilesystem:<unix domain socket name>
	 reverse --remove REMOTE  remove specific reverse socket connection
	 reverse --remove-all     remove all reverse socket connections from device
	
	file transfer:
	 push LOCAL... REMOTE
	     copy local files/directories to device
	 pull [-a] REMOTE... LOCAL
	     copy files/dirs from device
	     -a: preserve file timestamp and mode
	 sync [DIR]
	     copy all changed files to device; if DIR is "system", "vendor", "oem",
	     or "data", only sync that partition (default all)
	     -l: list but don't copy
	
	shell:
	 shell [-e ESCAPE] [-n] [-Tt] [-x] [COMMAND...]
	     run remote shell command (interactive shell if no command given)
	     -e: choose escape character, or "none"; default '~'
	     -n: don't read from stdin
	     -T: disable PTY allocation
	     -t: force PTY allocation
	     -x: disable remote exit codes and stdout/stderr separation
	 emu COMMAND              run emulator console command
	
	app installation:
	 install [-lrtsdg] PACKAGE
	 install-multiple [-lrtsdpg] PACKAGE...
	     push package(s) to the device and install them
	     -l: forward lock application
	     -r: replace existing application
	     -t: allow test packages
	     -s: install application on sdcard
	     -d: allow version code downgrade (debuggable packages only)
	     -p: partial application install (install-multiple only)
	     -g: grant all runtime permissions
	 uninstall [-k] PACKAGE
	     remove this app package from the device
	     '-k': keep the data and cache directories
	
	backup/restore:
	 backup [-f FILE] [-apk|-noapk] [-obb|-noobb] [-shared|-noshared] [-all] [-system|-nosystem] [PACKAGE...]
	     write an archive of the device's data to FILE [default=backup.adb]
	     package list optional if -all/-shared are supplied
	     -apk/-noapk: do/don't back up .apk files (default -noapk)
	     -obb/-noobb: do/don't back up .obb files (default -noobb)
	     -shared|-noshared: do/don't back up shared storage (default -noshared)
	     -all: back up all installed applications
	     -system|-nosystem: include system apps in -all (default -system)
	 restore FILE             restore device contents from FILE
	
	debugging:
	 bugreport [PATH]
	     write bugreport to given PATH [default=bugreport.zip];
	     if PATH is a directory, the bug report is saved in that directory.
	     devices that don't support zipped bug reports output to stdout.
	 jdwp                     list pids of processes hosting a JDWP transport
	 logcat                   show device log (logcat --help for more)
	
	security:
	 disable-verity           disable dm-verity checking on userdebug builds
	 enable-verity            re-enable dm-verity checking on userdebug builds
	 keygen FILE
	     generate adb public/private key; private key stored in FILE,
	     public key stored in FILE.pub (existing files overwritten)
	
	scripting:
	 wait-for[-TRANSPORT]-STATE
	     wait for device to be in the given state
	     State: device, recovery, sideload, or bootloader
	     Transport: usb, local, or any [default=any]
	 get-state                print offline | bootloader | device
	 get-serialno             print <serial-number>
	 get-devpath              print <device-path>
	 remount
	     remount /system, /vendor, and /oem partitions read-write
	 reboot [bootloader|recovery|sideload|sideload-auto-reboot]
	     reboot the device; defaults to booting system image but
	     supports bootloader and recovery too. sideload reboots
	     into recovery and automatically starts sideload mode,
	     sideload-auto-reboot is the same but reboots after sideloading.
	 sideload OTAPACKAGE      sideload the given full OTA package
	 root                     restart adbd with root permissions
	 unroot                   restart adbd without root permissions
	 usb                      restart adb server listening on USB
	 tcpip PORT               restart adb server listening on TCP on PORT
	
	internal debugging:
	 start-server             ensure that there is a server running
	 kill-server              kill the server if it is running
	 reconnect                kick connection from host side to force reconnect
	 reconnect device         kick connection from device side to force reconnect
	
	environment variables:
	 $ADB_TRACE
	     comma-separated list of debug info to log:
	     all,adb,sockets,packets,rwx,usb,sync,sysdeps,transport,jdwp
	 $ADB_VENDOR_KEYS         colon-separated list of keys (files or directories)
	 $ANDROID_SERIAL          serial number to connect to (see -s)
	 $ANDROID_LOG_TAGS        tags to be used by logcat (see logcat --help)
