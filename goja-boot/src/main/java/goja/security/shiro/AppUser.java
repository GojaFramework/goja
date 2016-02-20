/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2013-2014 sagyf Yang. The Four Group.
 */

package goja.security.shiro;

import com.jfinal.plugin.activerecord.Model;

import com.alibaba.fastjson.annotation.JSONCreator;
import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;

/**
 * <p> 登录对象包装. </p>
 *
 * @author sogyf yang
 * @version 1.0 2013-10-26 9:57 AM
 * @since JDK 1.5
 */
public class AppUser<U extends Model> implements Serializable {
    private static final long serialVersionUID = -4452393798317565037L;
    /**
     * 用户ID
     */
    public final int id;
    /**
     * 用户名称
     */
    public final String name;
    /**
     * 用户昵称
     */
    public final String nickName;
    /**
     * 用户类型
     */
    public final int type;
    /**
     * 用户状态
     */
    public final int status;

    /**
     * 用户数据库对象
     */
    public final U user;

    /**
     * Instantiates a new Shiro user.
     *
     * @param id       the id
     * @param name     the name
     * @param nickName the nick name
     * @param status   用户状态
     * @param user     the db model.
     */
    @JSONCreator
    public AppUser(@JSONField(name = "id") int id
            , @JSONField(name = "name") String name
            , @JSONField(name = "nickName") String nickName
            , @JSONField(name = "type") int type
            , @JSONField(name = "status") int status
            , @JSONField(name = "user") U user) {
        this.id = id;
        this.name = name;
        this.nickName = nickName;
        this.status = status;
        this.user = user;
        this.type = type;
    }

    /**
     * Gets id.
     *
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * Gets name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets nick name.
     *
     * @return the nick name
     */
    public String getNickName() {
        return nickName;
    }

    /**
     * 获取用户信息
     *
     * @return 用户信息
     */
    public U getUser() {
        return user;
    }

    public int getType() {
        return type;
    }

    public int getStatus() {
        return status;
    }

    /**
     * 本函数输出将作为默认的<shiro:principal/>输出.
     */
    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AppUser appUser = (AppUser) o;

        return id == appUser.id
                && status == appUser.status
                && type == appUser.type && !(name != null ? !name.equals(appUser.name)
                : appUser.name != null)
                && !(nickName != null ? !nickName.equals(appUser.nickName) : appUser.nickName != null)
                && !(user != null ? !user.equals(appUser.user) : appUser.user != null);
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (nickName != null ? nickName.hashCode() : 0);
        result = 31 * result + type;
        result = 31 * result + status;
        result = 31 * result + (user != null ? user.hashCode() : 0);
        return result;
    }
}
