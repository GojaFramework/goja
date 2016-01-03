/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2013-2014 sagyf Yang. The Four Group.
 */
package goja.core.sqlinxml;

import com.google.common.collect.Lists;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement
class SqlGroup {
    @XmlAttribute
    String name;
    @XmlElement(name = "sql")
    List<SqlItem> sqlItems = Lists.newArrayList();

    void addSqlgroup(SqlItem sqlGroup) {
        sqlItems.add(sqlGroup);
    }
}
