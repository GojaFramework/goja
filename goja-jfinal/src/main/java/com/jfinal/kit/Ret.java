/**
 * Copyright (c) 2011-2016, James Zhan 詹波 (jfinal@126.com).
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

package com.jfinal.kit;

import java.util.HashMap;
import java.util.Map;

/**
 * 返回值封装，常用于业务层需要多个返回值
 */
public class Ret {
	
	private Map<Object, Object> data = new HashMap<Object, Object>();
	
	public Ret() {
		
	}
	
	public static Ret create() {
		return new Ret();
	}
	
	public static Ret create(Object key, Object value) {
		return new Ret().put(key, value);
	}
	
	public Ret put(Object key, Object value) {
		data.put(key, value);
		return this;
	}
	
	@SuppressWarnings({"rawtypes", "unchecked"})
	public Ret put(Map map) {
		this.data.putAll(map);
		return this;
	}
	
	public Ret put(Ret ret) {
		this.data.putAll(ret.data);
		return this;
	}
	
	@SuppressWarnings("unchecked")
	public <T> T get(Object key) {
		return (T)data.get(key);
	}
	
	public boolean isEmpty() {
		return data.isEmpty();
	}
	
	public int size() {
		return data.size();
	}
	
	public Ret clear() {
		data.clear();
		return this;
	}
	
	public boolean equals(Ret ret) {
		return ret != null && this.data.equals(ret.data);
	}
	
	/**
	 * key 存在，但 value 可能为 null
	 */
	public boolean containsKey(Object key) {
		return data.containsKey(key);
	}
	
	public boolean containsValue(Object value) {
		return data.containsValue(value);
	}
	
	/**
	 * key 存在，并且 value 不为 null
	 */
	public boolean notNull(Object key) {
		return data.get(key) != null;
	}
	
	/**
	 * key 不存在，或者 key 存在但 value 为null
	 */
	public boolean isNull(Object key) {
		return data.get(key) == null;
	}
	
	/**
	 * key 存在，并且 value 为 true，则返回 true
	 */
	public boolean isTrue(Object key) {
		Object value = data.get(key);
		return (value instanceof Boolean && ((Boolean)value == true));
	}
	
	/**
	 * key 存在，并且 value 为 false，则返回 true
	 */
	public boolean isFalse(Object key) {
		Object value = data.get(key);
		return (value instanceof Boolean && ((Boolean)value == false));
	}
	
	@SuppressWarnings("unchecked")
	public <T> T remove(Object key) {
		return (T)data.remove(key);
	}
	
	public Map<Object, Object> getData() {
		return data;
	}
}


