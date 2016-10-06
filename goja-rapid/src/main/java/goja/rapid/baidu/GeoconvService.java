package goja.rapid.baidu;

import goja.rapid.baidu.dto.GeoLocationDto;

import com.google.common.base.Charsets;
import com.google.common.base.MoreObjects;
import com.google.common.base.Strings;
import com.google.common.io.Files;
import com.google.common.primitives.Ints;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.beust.jcommander.internal.Lists;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.utils.URIBuilder;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 百度地图坐标转换API是一套以HTTP形式提供的坐标转换接口， 用于将常用的非百度坐标
 * （目前支持GPS设备获取的坐标、google地图坐标、soso地图坐标、amap地图坐标、mapbar地图坐标）
 * 转换成百度地图中使用的坐标，并可将转化后的坐标在百度地图JavaScriptAPI、车联网API、静态图API、web服务API等产品中使用。
 *
 * @author sog
 * @version 1.0
 * @since JDK 1.6
 */
public class GeoconvService {

    private static final String        BAIDU_GEOCONV_PATH = "http://api.map.baidu.com/geoconv/v1/";
    /**
     * 百度开发者账号
     */
    private static       List<String>  aks                = Lists.newArrayList();
    private              AtomicInteger atomic             = new AtomicInteger();

    /**
     * 私有构造函数,确保对象只能通过单例方法来调用.
     */
    private GeoconvService() {
    }

    /**
     * 获取单例对象,如果要调用该单例的使用,只能通过该方法获取.
     */
    public static GeoconvService getInstance() {
        return GeoconvServiceHolder.instance;
    }

    /**
     * 该接口适用于需将非百度地图坐标的坐标进行转化，进而将其运用到百度地图开发的用户。该接口还支持批量坐标转化，一次最多可转换100个坐标点。
     *
     * @param coords 源坐标
     *               格式：经度,纬度;经度,纬度…
     *               限制：最多支持100个
     *               格式举例：
     *               114.21892734521,29.575429778924;
     *               114.21892734521,29.575429778924
     * @param from   源坐标类型
     * @return 转换后的百度坐标地址
     * @throws BaiduException 异常信息
     */
    public List<GeoLocationDto> conv(String coords, GeoType from) throws BaiduException {
        if (Strings.isNullOrEmpty(coords)) {
            throw new BaiduException("源坐标不能为空!");
        }
        if (aks.isEmpty()) {
            throw new BaiduException("请先设置开发者密钥信息,通过配置文件进行指定读取!");
        }
        // 随机获取开发者信息
        final int index = atomic.incrementAndGet() - 1;
        String ak;
        if (index == aks.size()) {
            atomic.set(0);
            ak = aks.get(index);
        } else {
            ak = aks.get(index);
        }
        try {

            URI baiduURI = new URIBuilder(BAIDU_GEOCONV_PATH)
                    .addParameter("coords", coords)
                    .addParameter("ak", ak)
                    .addParameter("from", String.valueOf(from.getValue()))
                    .addParameter("to", "5").build();
            final String baiduRspTxt = Request.Get(baiduURI)
                    .execute()
                    .returnContent()
                    .asString(Charset.forName("GBK"));

            Map<String, String> rspMap = JSON.parseObject(baiduRspTxt, new TypeReference<Map<String, String>>() {
            });
            int status = MoreObjects.firstNonNull(Ints.tryParse(rspMap.get("status")), -1);
            if (status == 0) {
                String result = rspMap.get("result");
                return JSON.parseArray(result, GeoLocationDto.class);
            } else {
                throw new BaiduException(rspMap.get("message"));
            }
        } catch (IOException e) {
            throw new BaiduException("转换错误!", e);
        } catch (URISyntaxException e) {
            throw new BaiduException("地址错误!", e);
        }

    }

    public void loadAks(String path) {
        if (StringUtils.startsWith(path, "classpath:")) {

        } else {
            File configFile = new File(path);
            if (configFile.exists()) {
                try {
                    final List<String> readLines = Files.readLines(configFile, Charsets.UTF_8);
                    for (String readLine : readLines) {
                        if (StringUtils.isEmpty(readLine)) {
                            continue;
                        }
                        aks.add(readLine);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    /**
     * lazy 加载的内部类,用于实例化单例对象.
     */
    private static class GeoconvServiceHolder {
        static GeoconvService instance = new GeoconvService();
    }
}
