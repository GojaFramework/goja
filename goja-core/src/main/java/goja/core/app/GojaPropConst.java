package goja.core.app;

/**
 * <p> 常量配置 </p>
 *
 * @author sogYF
 * @version 1.0
 * @since JDK 1.6
 */
public interface GojaPropConst {
  /**
   * 外部配置文件夹
   */
  String APPCONFIGFOLDER = "app.config.folder";

  /**
   * 运行模式， 有 dev,test,prod三种模式，分别标示开发,测试,线上运行
   */
  String APPMODE = "app.mode";
  /**
   * 系统版本
   */
  String APPVERSION = "app.version";
  /**
   * 应用名称
   */
  String APPNAME = "app.name";

  /**
   * JSON渲染配置
   */
  String APP_JSON_MODE = "app.json.mode";

  /**
   * 应用包前缀 默认 app
   */
  String APP_PACKAGE_PREFIX = "app.prefix.pkg";
  /**
   * 应用默认的域名
   */
  String APPDOMAIN = "app.domain";
  /**
   * 是否启用身份验证，也就是是否启用shiro 配置为：true, false
   */
  String APPSECURITY = "app.security";
  /**
   * 身份验证配置
   */
  String APPSECURITYCONFIG = "app.security.config";
  /**
   * 第三方包，可以扫描加载相关的类 模型等 配置为：classpath下的jar名称
   */
  String APPJARS = "app.jars";
  /**
   * 是否启用定时任务 配置为：true, false
   */
  String APPJOB = "app.job";
  /**
   * 是否启用全文检索，配置为一个存在的路径地址
   */
  //    String APPFULLTEXT       = "app.fulltext";
  /**
   * 最大支持的上传文件大小
   */
  String APP_MAXFILESIZE = "app.upload.maxfilesize";
  /**
   * JSP视图类型,默认为Freemwrker
   */
  String APP_VIEW_JSP = "app.jsp";
  /**
   * 微信地址，配置了则启用微信插件
   */
  String APP_WXCHAT_URL = "app.wxchat.url";
  /**
   * 视图存储路径，默认 WEB-INF/views/
   */
  String APP_VIEWPATH = "app.viewpath";
  /**
   * 框架内部异步处理的线程数，默认10
   */
  String APP_JOB_POOL = "app.job.pool";
  /**
   * 应用的日志级别
   */
  String APP_LOGGER = "app.logger";
  /**
   * 默认的文件上传路径
   */
  String APP_UPLOAD_PATH = "app.upload.path";
  /**
   * snaker工作流的配置，默认为false
   */
  String APP_SNAKER = "app.snaker";

  /**
   * 数据库连接地址 如果是多数据源，配置文件中应该是 db.config1.url
   */
  String DBURL = "db.url";
  /**
   * 数据库连接登录账号 如果是多数据源，配置文件中应该是 db.config1.username
   */
  String DBUSERNAME = "db.username";
  /**
   * 数据库文件存储日志
   */
  String DBLOGFILE = "db.logfile";
  /**
   * 数据库连接登录密码 如果是多数据源，配置文件中应该是 db.config1.password
   */
  String DBPASSWORD = "db.password";
  /**
   * 数据库连接池初始化大小
   */
  String DB_INITIAL_SIZE = "db.initial.size";
  /**
   * 数据库连接池最小闲置连接数
   */
  String DB_INITIAL_MINIDLE = "db.initial.minidle";
  /**
   * 数据库连接池最大
   */
  String DB_INITIAL_MAXWAIT = "db.initial.maxwait";
  /**
   * 数据库连接池活跃数
   */
  String DB_INITIAL_ACTIVE = "db.initial.active";
  String DB_TIME_BETWEEN_EVICTION_RUNS_MILLIS = "db.timeBetweenEvictionRunsMillis";
  String DB_MIN_EVICTABLE_IDLE_TIME_MILLIS = "db.minEvictableIdleTimeMillis";

  /**
   * 是否开启sql IN xml的机制
   */
  String DB_SQLINXML = "db.sqlinxml";
  /**
   * 是否开启 sqlmap机制
   */
  String DB_SQLMAP = "db.sqlmap";

  /**
   * druid的监控地址
   */
  String DB_MONITOR_URL = "db.monitor.url";
  /**
   * druid是否开启监控网页查看
   */
  String DB_MONITOR = "db.monitor";

  /**
   * 邮箱配置
   */
  String MAILSMTP = "mail.smtp";

  /**
   * MongoDB 的连接地址
   */
  String MONGO_HOST = "mongo.host";
  /**
   * MongoDB 的连接端口
   */
  String MONGO_PORT = "mongo.port";
  /**
   * MongoDB 连接数据库
   */
  String MONGO_DB = "mongo.db";
  /**
   * MongoDB 的 模型对象 包名
   */
  String MONGO_MODELS = "mongo.models";

  /**
   * Reids的外部配置文件
   */
  String REDIS_CONFIG = "redis.config";
  /**
   * Redis的外部配置文件的Cache名称，多个以逗号相隔 会从外部文件中取得Redis的配置端口和地址，格式为 缓存名称.host 和 缓存名称.port
   * 如果配置了该项，则默认的则不启动。
   */
  String REDIS_CACHES = "redis.caches";
  /**
   * 默认的redis配置，不采用外部文件，本地配置文件的方式， redis的连接地址
   */
  String REDIS_HOST = "redis.host";
  /**
   * redis的缓存名称，默认机制
   */
  String REDIS_CACHENAME = "redis.cachename";
  /**
   * 默认的redis连接端口
   */
  String REDIS_PORT = "redis.port";

  /**
   * UE控件上传的目录
   */
  String UE_UPLOAD_PATH = "ue.upload";
  /**
   * UE控件访问文件（上传的图片、文件）获取的地址前缀
   */
  String UE_URLPREFIX = "ue.urlprefix";

  /**
   * 分页Sql 分割标记
   */
  String WHERESPLIT = "--where--";
  /**
   * 条件sql 分割标识
   */
  String CONDITIONSSPLIT = "--conditions--";
}
