package goja.core.concurrent;

import goja.core.G;
import goja.core.concurrent.impl.FutureImpl;
import goja.core.lambda.Mapper;

import java.util.concurrent.TimeoutException;

/**
 * <p> </p>
 *
 * @author sogYF
 * @version 1.0
 * @since JDK 1.6
 */
public class Futures {


    public static <FROM, TO> Future<TO> mapping(final Future<FROM> future, final Mapper<FROM, TO> mapper) {
        return new FutureImpl<TO>() {

            @Override
            public TO get(long timeoutMs, long sleepingIntervalMs) throws TimeoutException {
                try {
                    return mapper.map(future.get(timeoutMs, sleepingIntervalMs));
                } catch (Exception e) {
                    throw G.rte(e);
                }
            }

        };
    }
}
