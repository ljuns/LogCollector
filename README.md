# LogCollector
一个收集 app 输出日志的工具，输出文件保存在 /sdcard/Android/data/项目包名/cache/ 下

### 如何使用
1. 在 module 的 `build.gradle ` 中添加依赖：
	``` gradle
	dependencies {
	    implementation 'com.ljuns:logcollector:<latest-version>'
	}
	```
2. 在 AndroidManifest.xml 中申请如下权限：
	``` xml
  	<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
  	<uses-permission android:name="android.permission.READ_LOGS" />
  	```
3. 在 Application 的 `onCreate()` 方法中调用如下：
  	``` java
  	LogCollector.getInstance(this).start();
  	```
### 更多功能
1. 可以设置需要收集的 TAG 对应的日志：
	``` java
	LogCollector.getInstance(this)
	  	.setTag("MainActivity")
	  	.start();
	```
	<img src="/image/tag.png" width="25%" height="25%" />
2. 可选择收集某种类型的日志：
	``` java
  	LogCollector.getInstance(this)
	  	// 可配置类型：V、D、I、W、E、F、S
		// V 表示最低级，所有类型都会收集；S 标记最高级，可能不会收集任何东西
	  	.setLevel(LevelUtils.W) // 表示收集 W 以后的日志，即收集 W、E、F、S 这四种日志
	  	.start();
  	```
	<img src="/image/level.png" width="25%" height="25%" />
3. 可同时设置需要收集的 TAG:level ：
	  ``` java
	  LogCollector.getInstance(this)
		  .setTagWithLevel("EGL_emulation", LevelUtils.D)
		  .start(this);
	  ```
	  <img src="/image/tagwithlevel.png" width="25%" height="25%" />
4. 每次收集日志前是否清除之前的缓存文件：
	  ``` java
	  LogCollector.getInstance(this)
		  // 默认为 false，不清除缓存文件
		  .setCleanCache(true)
		  .start();
	  ```

### License
	Copyright 2018 ljuns

	Licensed under the Apache License, Version 2.0 (the "License");	you may not use this file except in compliance with the License.
	You may obtain a copy of the License at

		http://www.apache.org/licenses/LICENSE-2.0

	Unless required by applicable law or agreed to in writing, software
	distributed under the License is distributed on an "AS IS" BASIS,
	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
	See the License for the specific language governing permissions and
	limitations under the License.
