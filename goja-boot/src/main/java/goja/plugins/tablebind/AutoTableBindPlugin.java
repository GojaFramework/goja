/**
 * Copyright (c) 2011-2013, kidzhou 周磊 (zhouleib1412@gmail.com)
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
package goja.plugins.tablebind;

import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.activerecord.DbKit;
import com.jfinal.plugin.activerecord.IDataSourceProvider;
import goja.Func;
import goja.StringPool;
import goja.annotation.TableBind;
import goja.initialize.ctxbox.ClassBox;
import goja.initialize.ctxbox.ClassType;
import org.apache.commons.codec.binary.StringUtils;
import org.apache.commons.lang3.ArrayUtils;

import java.util.List;

public class AutoTableBindPlugin extends ActiveRecordPlugin {
    private final String confiName;


    public AutoTableBindPlugin(String configName, IDataSourceProvider dataSourceProvider) {
        super(configName, dataSourceProvider);
        this.confiName = configName;
    }


    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public boolean start() {
        List<Class> modelClasses = ClassBox.getInstance().getClasses(ClassType.MODEL);
        if (modelClasses != null && !modelClasses.isEmpty()) {
            TableBind tb;
            for (Class modelClass : modelClasses) {
                String tableName;
                tb = (TableBind) modelClass.getAnnotation(TableBind.class);
                if (StringUtils.equals(DbKit.MAIN_CONFIG_NAME, confiName) && tb == null) {
                    tableName = name(modelClass.getSimpleName());
                    this.addMapping(tableName, modelClass);
                } else {
                    if(tb.ignore()){
                        continue;
                    }
                    final String[] configNames = tb.configName();
                    if(configNames == null){
                        tableName = name(modelClass.getSimpleName());
                        this.addMapping(tableName, modelClass);
                    } else {
                        final boolean contains = ArrayUtils.contains(configNames, this.confiName);
                        if(contains){
                            tableName = tb.tableName();
                            final String[] pks = tb.pks();
                            if(pks != null ){
                                if(pks.length == 1){
                                    String pk_name = pks[0];
                                    this.addMapping(tableName, pk_name, modelClass);
                                } else if (pks.length > 1){
                                    this.addMapping(tableName, Func.COMMA_JOINER.join(pks), modelClass);
                                } else {
                                    this.addMapping(tableName, modelClass);
                                }
                            } else {
                                this.addMapping(tableName, modelClass);
                            }
                        }
                    }

                }
            }
            return super.start();
        }
        return false;
    }

    @Override
    public boolean stop() {
        return super.stop();
    }


    public String name(String className) {
        String tableName = StringPool.EMPTY;
        for (int i = 0; i < className.length(); i++) {
            char ch = className.charAt(i);
            if (i == 0) {
                tableName += Character.toLowerCase(ch);
            } else if (Character.isUpperCase(ch)) {
                tableName += StringPool.UNDERSCORE + Character.toLowerCase(ch);
            } else {
                tableName += ch;
            }
        }
        return tableName;
    }
}