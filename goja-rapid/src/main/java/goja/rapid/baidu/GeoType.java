package goja.rapid.baidu;

/**
 * <p>
 *
 * 1：GPS设备获取的角度坐标，wgs84坐标;
 *
 * 2：GPS获取的米制坐标、sogou地图所用坐标;
 *
 * 3：google地图、soso地图、aliyun地图、mapabc地图和amap地图所用坐标，国测局坐标;
 *
 * 4：3中列表地图坐标对应的米制坐标;
 *
 * 5：百度地图采用的经纬度坐标;
 *
 * 6：百度地图采用的米制坐标;
 *
 * 7：mapbar地图坐标;
 *
 * 8：51地图坐标
 *
 *
 * </p>
 *
 * @author sog
 * @version 1.0
 * @since JDK 1.6
 */
public enum GeoType {
    wgs84(1),
    sogou(2),
    google(3),
    google_mi(4),
    baidu(5),
    baidu_mi(6),
    mapbar(7),
    map51(8);

    private final int value;

    GeoType(int val) {
        this.value = val;
    }

    public int getValue() {
        return value;
    }
}
