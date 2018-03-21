---

title: Android之 多渠道打包
categories: "android 总结"
tags: 
	- 多渠道打包

---
# 多渠道打包

	http://www.jianshu.com/p/2f2ce33e670f

---
### Gradle → 友盟 ###

1. 清单文件

		<meta-data android:name="UMENG_CHANNEL" 
			android:value="${UMENG_CHANNEL_VALUE}"/>

2. 签名配置

		android {
			signingConfigs {
			        config {
			            try {
			                keyAlias 'xigua'
			                keyPassword 'yglxigua'
			                storeFile file('C:/appkey/xigua.jks')
			                storePassword 'yglxigua'
			            } catch (ex) {
			                throw new InvalidUserDataException(ex.toString())
			            }
			        }
			        debug_config {
			            try {
			                keyAlias 'xigua'
			                keyPassword 'yglxigua'
			                storeFile file('C:/appkey/xigua.jks')
			                storePassword 'yglxigua'
			            } catch (ex) {
			                throw new InvalidUserDataException(ex.toString())
			            }
			        }
			    }
			    buildTypes {
			        release {
			            signingConfig signingConfigs.config

			            //buildConfigField "boolean", "LOG_DEBUG", "false"
			            //zipAlignEnabled true
			            //minifyEnabled true
			            //清除无用资源
			            //shrinkResources true
			            //proguardFiles getDefaultProguardFile('proguard-android.txt'), 'proguard-rules.pro'

			            applicationVariants.all { variant ->
			                variant.outputs.each { output ->
			                    def outputFile = output.outputFile
			                    if (outputFile != null && outputFile.name.endsWith('.apk')) {
			                        // 输出apk名称为xigua_v1.0_2015-01-15_渠道名.apk
			                        def fileName = "xigua_v${defaultConfig.versionName}_${releaseTime()}_${variant.productFlavors[0].name}.apk"
			                        output.outputFile = new File(outputFile.parent, fileName)
			                    }
			                }
			            }
			        }
			        debug {
			            //buildConfigField "boolean", "LOG_DEBUG", "true"
			            //versionNameSuffix "-debug"
			            //minifyEnabled false
			            //zipAlignEnabled false
			            //shrinkResources false
			            signingConfig signingConfigs.debug_config
			        }
			    }
		}

3. 渠道名

		android {
			productFlavors {
		        mainflavor {}
		        tencent {}
		        ali {}
		        qh360 {}
		        xiaomi {}
				meizu {}
		        huawei {}
		        oppo {}
		        vivo {}
		        wandoujia {}
		        baidu {}
		    }
		    productFlavors.all { flavor ->
		        flavor.manifestPlaceholders = [UMENG_CHANNEL_VALUE: name]
		    }
		}


4. gradle打包,运行命令行或右侧gradle → projectName → :app  → Tasks → build → assembleRelease

		//运行命令行
		./gradlew assembleRelease

		

---
### Python → 美团  ###





---