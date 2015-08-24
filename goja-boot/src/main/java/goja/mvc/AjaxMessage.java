/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2013-2014 sagyf Yang. The Four Group.
 */

package goja.mvc;

import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

import static goja.StringPool.EMPTY;

/**
 * <p> ajax请求返回信息 </p>
 *
 * @param <E> 数据格式
 * @author mumu@yfyang
 * @version 1.0 2013-08-10 12:27 PM
 * @since JDK 1.5
 */
public final class AjaxMessage<E> implements Serializable {


    public static final AjaxMessage OK        = ok(EMPTY, null);
    public static final AjaxMessage NODATA    = nodata(EMPTY, null);
    public static final AjaxMessage FORBIDDEN = forbidden(null);
    public static final AjaxMessage ERROR     = error(null);
    public static final AjaxMessage FAILURE   = failure(null);

    private static final long serialVersionUID = 1091092803607855861L;
    /**
     * Returns the message data
     */
    private final E data;

    /**
     * Message
     */
    private final String message;

    /**
     * Message State Machine.
     */
    private final MessageStatus status;
    /**
     * Exception
     */
    private final Exception     exception;

    /**
     * Message code.
     */
    private String code;

    /**
     * 构造函数
     *
     * @param data    消息数据
     * @param message Toast
     * @param status  消息状态
     */
    protected AjaxMessage(E data, String message, MessageStatus status) {
        this.data = data;
        this.message = message;
        this.status = status;
        this.exception = null;
    }

    /**
     * 构造一个包括异常信息的函数
     *
     * @param data      消息数据
     * @param message   Toast
     * @param status    消息状态
     * @param exception exception信息
     */
    protected AjaxMessage(E data, String message, MessageStatus status, Exception exception) {
        this.data = data;
        this.message = message;
        this.status = status;
        this.exception = exception;
    }

    public static AjaxMessage ok(){
        return OK;
    }

    /**
     * 返回处理正常的消息内容
     *
     * @param message Toast
     * @param data    消息数据
     * @param <E>     数据泛型类型
     * @return message content
     */
    public static <E> AjaxMessage ok(String message, E data) {
        return new AjaxMessage<E>(data, message, MessageStatus.OK);
    }

    /**
     * 返回处理正常的消息内容
     *
     * @param data 消息数据
     * @param <E>  数据泛型类型
     * @return message content
     */
    public static <E> AjaxMessage ok(E data) {
        return ok(StringUtils.EMPTY, data);
    }

    /**
     * 返回处理正常的消息内容
     *
     * @param message Toast
     * @return message content
     */
    public static AjaxMessage ok(String message) {
        return ok(message, null);
    }

    /**
     * 正在开发提示语
     *
     * @return 正在开发提示
     */
    public static AjaxMessage developing() {

        return ok("Online Developeing...", null);
    }

    public static AjaxMessage nodata(){
        return NODATA;
    }

    /**
     * 返回没有数据的消息内容
     *
     * @param data 消息数据
     * @param <E>  数据泛型类型
     * @return message content
     */
    public static <E> AjaxMessage nodata(E data) {
        return new AjaxMessage<E>(data, EMPTY, MessageStatus.NODATA);
    }

    /**
     * 返回没有数据的消息内容
     *
     * @param data 消息数据
     * @param <E>  数据泛型类型
     * @return message content
     */
    public static <E> AjaxMessage nodata(String message, E data) {
        return new AjaxMessage<E>(data, message, MessageStatus.NODATA);
    }


    /**
     * 返回没有登录时消息内容
     *
     * @param data 消息数据
     * @param <E>  数据泛型类型
     * @return message content
     */
    public static <E> AjaxMessage nologin(E data) {
        return new AjaxMessage<E>(data, EMPTY, MessageStatus.NOLOGIN);
    }

    /**
     * 返回没有登录时的消息内容
     *
     * @return message content
     */
    public static AjaxMessage nologin() {
        return nologin(null);
    }

    public static AjaxMessage forbidden(){
        return FORBIDDEN;
    }

    /**
     * 返回禁止访问消息内容
     *
     * @param data 消息数据
     * @param <E>  数据泛型类型
     * @return message content
     */
    public static <E> AjaxMessage forbidden(E data) {
        return new AjaxMessage<E>(data, EMPTY, MessageStatus.FORBIDDEN);
    }

    /**
     * 返回禁止访问消息内容
     *
     * @param data 消息数据
     * @param <E>  数据泛型类型
     * @return message content
     */
    public static <E> AjaxMessage forbidden(String message, E data) {
        return new AjaxMessage<E>(data, message, MessageStatus.FORBIDDEN);
    }


    public static AjaxMessage error(){
        return ERROR;
    }

    /**
     * 返回处理错误的消息内容
     *
     * @param message Toast
     * @return message content
     */
    public static AjaxMessage error(String message) {
        return error(message, null, null);
    }

    /**
     * 返回处理错误的消息内容
     *
     * @param data Toast
     * @return message content
     */
    public static <E> AjaxMessage error(E data) {
        return error(EMPTY, data, null);
    }

    /**
     * 返回处理错误的消息内容
     *
     * @param message   Toast
     * @param exception exception
     * @return message content
     */
    public static AjaxMessage error(String message, Exception exception) {
        return error(message, null, exception);
    }

    /**
     * 返回处理错误的消息内容
     *
     * @param message   Toast
     * @param exception exception
     * @param data      数据
     * @return message content
     */
    public static <E> AjaxMessage error(String message, E data, Exception exception) {
        return new AjaxMessage<E>(data, message, MessageStatus.ERROR, exception);
    }


    /**
     * 返回处理失败的消息内容
     *
     * @param message Toast
     * @return message content
     */
    public static AjaxMessage failure(String message) {
        return failure(message, null, null);
    }

    /**
     * 返回处理失败的消息内容
     *
     * @param data data
     * @return message content
     */
    public static <E> AjaxMessage failure(E data) {
        return failure(null, data, null);
    }

    /**
     * 返回处理失败的消息内容
     *
     * @param message   Toast
     * @param exception exception
     * @return message content
     */
    public static AjaxMessage failure(String message, Exception exception) {
        return failure(message, null, exception);
    }

    /**
     * 返回处理失败的消息内容
     *
     * @param message   Toast
     * @param exception exception
     * @param data      数据
     * @return message content
     */
    public static <E> AjaxMessage failure(String message, E data, Exception exception) {
        return new AjaxMessage<E>(data, message, MessageStatus.FAILURE, exception);
    }
    public AjaxMessage<E> code(String code) {
        this.code = code;
        return this;
    }
    /**
     * 获取消息数据
     *
     * @return 消息数据
     */
    public E getData() {
        return data;
    }

    /**
     * 获取消息提
     *
     * @return 消息提醒
     */
    public String getMessage() {
        return message;
    }

    /**
     * 获取状态
     *
     * @return 状态信息
     */
    public MessageStatus getStatus() {
        return status;
    }

    /**
     * 转换为JSON字符串。
     *
     * @return JSON字符串
     */
    public String toJSON() {
        return JSON.toJSONString(this);
    }

    /**
     * 获取错误异常.
     *
     * @return exception信息.
     */
    public Exception getException() {
        return exception;
    }


    public String getCode() {
        return code;
    }



    /**
     * Request message processing state
     */
    protected enum MessageStatus {
        /**
         * Normal
         */
        OK,
        /**
         * internal error occurred
         */
        ERROR,
        /**
         * To deal with failure
         */
        FAILURE,
        /**
         * No data
         */
        NODATA,
        /**
         * Forbidden
         */
        FORBIDDEN,
        /**
         * Not logged in
         */
        NOLOGIN

    }

}
