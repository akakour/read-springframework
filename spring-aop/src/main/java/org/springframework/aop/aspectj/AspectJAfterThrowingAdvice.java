/*
 * Copyright 2002-2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.aop.aspectj;

import java.io.Serializable;
import java.lang.reflect.Method;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import org.springframework.aop.AfterAdvice;

/**
 * Spring AOP advice wrapping an AspectJ after-throwing advice method.
 *
 * @author Rod Johnson
 * @since 2.0
 */
@SuppressWarnings("serial")
public class AspectJAfterThrowingAdvice extends AbstractAspectJAdvice
		implements MethodInterceptor, AfterAdvice, Serializable {

	public AspectJAfterThrowingAdvice(
			Method aspectJBeforeAdviceMethod, AspectJExpressionPointcut pointcut, AspectInstanceFactory aif) {

		super(aspectJBeforeAdviceMethod, pointcut, aif);
	}


	@Override
	public boolean isBeforeAdvice() {
		return false;
	}

	@Override
	public boolean isAfterAdvice() {
		return true;
	}

	@Override
	public void setThrowingName(String name) {
		setThrowingNameNoCheck(name);
	}

	/**
	 * @AfterThrowing 的增强逻辑调用
	 * @param mi
	 * @return
	 * @throws Throwable
	 */
	@Override
	public Object invoke(MethodInvocation mi) throws Throwable {
		try {
			// 后续节点调用如果出异常会被catch到，throwable是所有异常的顶级父类
			return mi.proceed();
		}
		catch (Throwable ex) {
			// 判断是否是需要拦截的异常类型
			if (shouldInvokeOnThrowing(ex)) {
				// 调用具体的增强逻辑
				invokeAdviceMethod(getJoinPointMatch(), null, ex);
			}
			throw ex;
		}
	}

	/**
	 * In AspectJ semantics, after throwing advice that specifies a throwing clause
	 * is only invoked if the thrown exception is a subtype of the given throwing type.
	 */
	private boolean shouldInvokeOnThrowing(Throwable ex) {
		return getDiscoveredThrowingType().isAssignableFrom(ex.getClass());
	}

}
