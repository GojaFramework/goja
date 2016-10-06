package goja.rapid.mongo;

import com.jfinal.plugin.IPlugin;

import com.mongodb.MongoClient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p> </p>
 *
 * @author sog
 * @version 1.0
 * @since JDK 1.6
 */
public class MongoPlugin implements IPlugin {

    public static final String DEFAULT_HOST = "127.0.0.1";
    public static final int    DEFAUL_PORT  = 27017;
    private static MongoClient client;
    protected final Logger logger = LoggerFactory.getLogger(getClass());
    private final  String      host;
    private final  int         port;
    private final  String      database;

    public MongoPlugin(String database) {
        this.host = DEFAULT_HOST;
        this.port = DEFAUL_PORT;
        this.database = database;
    }

    public MongoPlugin(String host, int port, String database) {
        this.host = host;
        this.port = port;
        this.database = database;
    }

    @Override
    public boolean start() {

        try {
            client = new MongoClient(host, port);
        } catch (Exception e) {
            throw new RuntimeException(
                    "can't connect mongodb, please check the host and port:" + host + "," + port, e);
        }

        MongoKit.init(client, database);
        return true;
    }

    @Override
    public boolean stop() {
        if (client != null) {
            client.close();
        }
        return true;
    }
}
