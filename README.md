# Goja

> 感谢：[@JFinal](http://git.oschina.net/jfinal/jfinal) [@JFinal-ext](http://git.oschina.net/zhouleib1412/jfinal-ext)

1. 在 `JFinal`基础上，参考`Play 1.2`配置方式，将大部分的配置整合到一个配置文件，并支持动态启动相关插件等！
2. 需要使用`JDK-1.6` 以及 `Servlet 3.0` 以上版本
3. 通过`javax.servlet.ServletContainerInitializer`(需要`Servlet3.0`以上容器)的方式去掉了`web.xml`的配置


## 一、[Goja-Cli](https://github.com/GojaFramework/goja-cli)（推荐）

一个为了方便使用idea和不喜欢`Maven`的脚本工具。可点击[这里](https://github.com/GojaFramework/goja-cli/releases)进行下载。

        $ goja
        ~
        ~ Gaja Application.
        ~ Usage: goja cmd application_name [-options]
        ~
        ~ with, new          Create a new application
        ~       war          Export the application as a standalone WAR archive
        ~       syncdb       Sync Database table to Jfinal Model
        ~       idea         Convert the project to Intellij IDEA project
        ~       pom          Generate Maven POM file, and comply with the standard configuration Goja-app
        ~       upgrade      Upgrade Code To Goja.
        ~       help         Show goja help
        ~

## 更多信息

可以点击 [Goja Document](https://github.com/GojaFramework/goja-doc) 查看更详细的信息。

## 示例

[jfinal-showcase](https://github.com/GojaFramework/goja/tree/master/goja-showcase)
