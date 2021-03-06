package com.netflix.evcache.test;

import org.apache.log4j.BasicConfigurator;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.netflix.evcache.EVCache;
import com.netflix.evcache.pool.EVCacheClientPoolManager;

/**
 * Tests Zone based EVCacheClient.
 * @author smadappa
 *
 */
public class ZoneBasedEVCacheTest  extends AbstractEVCacheTest {
    private static Logger log = LoggerFactory.getLogger(SimpleEVCacheTest.class);

    @BeforeClass
    public static void initLibraries() {
        try {
            BasicConfigurator.configure();
            log.info("Logger intialized");

            System.setProperty("evcache.pool.provider", "com.netflix.evcache.pool.standalone.ZoneClusteredEVCacheClientPoolImpl");
            System.setProperty("EVCACHE.EVCacheClientPool.zones", "A,B");
            System.setProperty("EVCACHE.A.EVCacheClientPool.hosts",
                    "ec2-23-22-55-28.compute-1.amazonaws.com:11211,localhost:11211");
            System.setProperty("EVCACHE.B.EVCacheClientPool.hosts",
                    "ec2-184-73-63-197.compute-1.amazonaws.com:11211,ec2-50-19-26-171.compute-1.amazonaws.com:11211");
            EVCacheClientPoolManager.getInstance().initEVCache("EVCACHE");
            log.info("initializing EVCache");
            EVCacheClientPoolManager.getInstance().initEVCache("EVCACHE");
        } catch (Exception e) {
            log.error("Exception", e);
        }
    }

    @Test
    public void runTest() {
        EVCache gCache = (new EVCache.Builder()).setAppName("EVCACHE").setCacheName("test").enableZoneFallback().build();
        int executeCount = 0;
        while (executeCount++ < 3) {
            try {
                for (int i = 0; i < 20; i++) {
                    insert(i, gCache);
                    get(i, gCache);
                    getAndTouch(i, gCache);
                    getBulk(0, i, gCache);
                }
            } catch (Exception e) {
                log.error("Exception", e);
            }
        }
    }
}
