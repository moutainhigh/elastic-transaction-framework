package cn.panshi.etf.core;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import com.alibaba.fastjson.JSONObject;

@Component
public class EtfTransBeanUtil implements BeanPostProcessor {
	static Logger log = LoggerFactory.getLogger(EtfTransBeanUtil.class);

	Map<String, Object> etfTransBeanMap = new HashMap<>();
	Map<String, Method> etpTransBeanMethodMap = new HashMap<>();

	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		return bean;
	}

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		Map<String, Object> etfBeanMethodMap = new HashMap<>();
		Method[] methods = ReflectionUtils.getAllDeclaredMethods(bean.getClass());
		if (methods != null) {
			for (Method method : methods) {
				EtfAnnTransApi ann = AnnotationUtils.findAnnotation(method, EtfAnnTransApi.class);
				if (ann == null) {
					continue;
				}
				boolean firstTimeCheckTheMethod = true;
				if (etfBeanMethodMap.get(method.getName()) != null) {
					firstTimeCheckTheMethod = false;
				} else {
					etfBeanMethodMap.put(method.getName(), "");
				}

				String key = ann.transEnumClazz().getName() + "." + ann.transEnumValue();
				if (etfTransBeanMap.get(key) != null && firstTimeCheckTheMethod) {
					throw new RuntimeException("ETF api[" + key + "]被重复定义了:[" + beanName + "." + method.getName()
							+ "]和[" + etfTransBeanMap.get(key).getClass().getName() + "."
							+ etpTransBeanMethodMap.get(key).getName() + "]");
				}
				etfTransBeanMap.put(key, bean);
				etpTransBeanMethodMap.put(key, method);
			}
		}
		return bean;
	}

	private static final LocalVariableTableParameterNameDiscoverer parameterNameDiscoverer = new LocalVariableTableParameterNameDiscoverer();

	public void invokeEtfBean(String transTypeEnumClazz, String transType, JSONObject paramJsonObj) {
		String key = transTypeEnumClazz + "." + transType;
		Object target = etfTransBeanMap.get(key);
		Method method = etpTransBeanMethodMap.get(key);

		Parameter[] methodParamArray = method.getParameters();

		Object[] argArry2InvokeTarget = new Object[methodParamArray.length];

		String[] paraNames = parameterNameDiscoverer.getParameterNames(method);

		for (int i = 0; i < methodParamArray.length; i++) {
			Parameter methodParam = methodParamArray[i];
			argArry2InvokeTarget[i] = JSONObject.toJavaObject(paramJsonObj.getJSONObject(paraNames[i]),
					methodParam.getType());
		}
		Object object = ReflectionUtils.invokeMethod(method, target, argArry2InvokeTarget);
		log.debug(object + "");
	}
}