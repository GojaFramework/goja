## 0.1.5 (2015-02-25)

* 修复开发模式下，扫描SQL配置文件加载问题;
* 增加 百度Ueditor 的支持;
* 增加 文件存储的 便捷脚手架代码支持；
* 增加 支持在 jar 包中读取 Model 和 sql config 的功能。

## 0.1.2 (2014-12-07)

* 修复在开发模式下，自动扫描Sql配置文件自动加载无法成功运行的问题；
* 增加SqlSelect等工具处理的方法，具体使用可见 [goja/db](https://github.com/GojaFramework/goja/tree/master/goja-mvt/src/test/java/goja/db)
* druid 初步尝试 logFilter 的配置
* 去掉了 `ehcache.xml` 和 `shiro.ini`的必须，合并到`application.conf`.

## 0.1.1 (2014-11-15)

* 修复错误的400和500的显示问题
* 修复分页查询和排序的处理问题
* 修复Job插件关闭的空指针问题

## 0.1 (2014-11-09)

* 增加对`logback`的上下文监听事件；
* 修复如果 `logback.xml` 文件不存在，会出现`NullPoint`问题；
* 修复 `render('/view.ftl')`时，视图文件路径不正确的问题；
* 补充 `Controller` 中对`AjaxMessage`的处理；
* 升级`jFinal`为`1.9`版本；
* 增加`Controller`中对JSON参数转换为`Model`和Rquest中直接封装为`Model`的方法；详见`goja.mvc.Controller#getModelByJson`和`goja.mvc.Controller#getModelByRequest`;
* 增加对`Model`的测试用例;
* 修复一些BUG问题。

## 0.0.10 (2014-10-01)

* 增加类似`PlayFramework`的任务机制；
* 优化基础工具类；
* 调整相关代码，按模块重新规划；
* 对于`Freemarker`视图，自动增加模版继承的指令；
* 增加日志文件的配置，同时去掉对`logback.xml`的必须使用的支持，详见 `goja.logging.Logger`;
* 增加对`Lucene`的支持
