# LogCollector
一个收集 app 输出日志的工具，输出文件保存在 /Android/data/项目包名/cache/ 下

### 如何使用
1. 在 module 的 `build.gradle ` 中添加依赖：
	``` gradle
	dependencies {
	    implementation 'com.ljuns:logcollector:0.0.1'
	}
	```
2. 在 AndroidManifest.xml 中申请如下权限：
	``` xml
  	<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
  	<uses-permission android:name="android.permission.READ_LOGS" />
  	```
3. 在 Application 的 `onCreate()` 方法中调用如下：
  	``` java
  	LogCollector.getInstance().start(this);
  	```
### 更多功能
1. 可选择收集一种或多种类型的日志：
	``` java
  	LogCollector.getInstance()
	  	// 可配置类型：VERBOSE、DEBUG、INFO、WARN、ERROR、ASSERT，默认收集这 6 种
	  	.setLogType(TagUtils.DEBUG)
	  	.start(this);
  	```
2. 缓存日志文件是个 html，可以设置背景颜色，目前支持黑色和白色：
	  ``` java
	  LogCollector.getInstance()
		  // 默认背景黑色，输出的 logcat 字体有各种颜色，如果设置背景为白色，输出的 logcat 字体为黑色
		  .setBgColor(TagUtils.BLACK_COLOR) // TagUtils.WHITE_COLOR
		  .start(this);
	  ```
3. 每次收集日志前是否清除之前的缓存文件：
	  ``` java
	  LogCollector.getInstance()
		  // 默认为 false，不清除缓存文件
		  .setCleanCache(true)
		  .start(this);
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
