/**
 * Copyright (c) 2011-2016, James Zhan 詹波 (jfinal@126.com).
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package com.jfinal.core;

import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Table;
import com.jfinal.plugin.activerecord.TableMapping;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Injector.
 */
public class Injector {

    private static <T> T createInstance(Class<T> objClass) {
        try {
            return objClass.newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T injectModel(Class<T> modelClass, HttpServletRequest request,
                                    boolean skipConvertError) {
        String modelName = modelClass.getSimpleName();
        return (T) injectModel(modelClass, StrKit.firstCharToLowerCase(modelName), request,
                skipConvertError);
    }

    public static <T> T injectBean(Class<T> beanClass, HttpServletRequest request,
                                   boolean skipConvertError) {
        String beanName = beanClass.getSimpleName();
        return (T) injectBean(beanClass, StrKit.firstCharToLowerCase(beanName), request,
                skipConvertError);
    }

    @SuppressWarnings("unchecked")
    public static final <T> T injectBean(Class<T> beanClass, String beanName,
                                         HttpServletRequest request, boolean skipConvertError) {
        Object bean = createInstance(beanClass);
        String modelNameAndDot = StrKit.notBlank(beanName) ? beanName + "." : null;

        Map<String, String[]> parasMap = request.getParameterMap();
        Method[] methods = beanClass.getMethods();
        for (Method method : methods) {
            String methodName = method.getName();
            if (methodName.startsWith("set") == false
                    || methodName.length() <= 3) {  // only setter method
                continue;
            }
            Class<?>[] types = method.getParameterTypes();
            if (types.length != 1) {            // only one parameter
                continue;
            }

            String attrName = StrKit.firstCharToLowerCase(methodName.substring(3));
            String paraName = modelNameAndDot != null ? modelNameAndDot + attrName : attrName;
            if (parasMap.containsKey(paraName)) {
                try {
                    String paraValue = request.getParameter(paraName);
                    Object value = paraValue != null ? TypeConverter.convert(types[0], paraValue) : null;
                    method.invoke(bean, value);
                } catch (Exception e) {
                    if (skipConvertError == false) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }

        return (T) bean;
    }

    @SuppressWarnings("unchecked")
    public static final <T> T injectModel(Class<T> modelClass, String modelName,
                                          HttpServletRequest request, boolean skipConvertError) {
        Object temp = createInstance(modelClass);
        if (temp instanceof Model == false) {
            throw new IllegalArgumentException(
                    "getModel only support class of Model, using getBean for other class.");
        }

        Model<?> model = (Model<?>) temp;
        String modelNameAndDot = StrKit.notBlank(modelName) ? modelName + "." : null;

        Table table = TableMapping.me().getTable(model.getClass());
        Map<String, String[]> parasMap = request.getParameterMap();
        for (Entry<String, Class<?>> entry : table.getColumnTypeMapEntrySet()) {
            String attrName = entry.getKey();
            String paraName = modelNameAndDot != null ? modelNameAndDot + attrName : attrName;
            if (parasMap.containsKey(paraName)) {
                try {
                    Class<?> colType = entry.getValue();
                    String paraValue = request.getParameter(paraName);
                    Object value = paraValue != null ? TypeConverter.convert(colType, paraValue) : null;
                    model.set(attrName, value);
                } catch (Exception e) {
                    if (skipConvertError == false) {
                        throw new RuntimeException("Can not convert parameter: " + paraName, e);
                    }
                }
            }
        }

        return (T) model;
    }
}

