package goja.rapid.baidu;

import goja.rapid.baidu.dto.GeoLocationDto;

import org.junit.Test;

import java.util.List;

/**
 * <p> </p>
 *
 * @author sog
 * @version 1.0
 * @since JDK 1.6
 */
public class GeoconvServiceTest {
    @Test
    public void conv() throws Exception {
        GeoconvService geoconvService = GeoconvService.getInstance();
        geoconvService.loadAks("/Users/sog/iDo/iMe/GojaFramework/goja/goja-rapid/src/test/resources/baidu_aks.txt");

        try {
            final List<GeoLocationDto> locationDtos = geoconvService.conv("114.21892734521,29.575429778924;114.21892734521,29.575429778924", GeoType.wgs84);
            for (GeoLocationDto locationDto : locationDtos) {
                System.out.println("locationDto = " + locationDto);
            }
        } catch (BaiduException e) {
            e.printStackTrace();
        }
    }

}