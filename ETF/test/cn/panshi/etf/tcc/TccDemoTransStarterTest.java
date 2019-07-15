package cn.panshi.etf.tcc;

import java.util.Set;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import cn.panshi.etf.core.demo.EtfDemoComponent;

@SuppressWarnings({ "deprecation", "rawtypes", "unchecked" })
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:spring-test*.xml" })
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TccDemoTransStarterTest {
	Logger logger = Logger.getLogger(EtfDemoComponent.class);
	@Resource
	TccDemoStarterComponent tccDemoStarterComponent;
	@Resource
	EtfTccDaoRedis etfTccDaoRedis;
	@Resource
	RedisTemplate redisTemplate;

	@Test
	public void aFirstStep2ClearRedis() {
		Set keys = redisTemplate.keys("ETF_TCC*");
		for (Object key : keys.toArray()) {
			redisTemplate.delete(key);
		}
	}

	@Test
	public void testStartFlow1() throws Exception {
		tccDemoStarterComponent.startTccFlow1();

		Thread.sleep(60 * 1000);
	}

}
