/**
 * Copyright (c) 2011-2015, Unas 小强哥 (unas@qq.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */

package com.jfinal.weixin.sdk.msg.out;

import com.jfinal.weixin.sdk.msg.in.InMsg;

/**
    转发多客服消息
	<xml>
		<ToUserName><![CDATA[toUser]]></ToUserName>
		<FromUserName><![CDATA[fromUser]]></FromUserName>
		<CreateTime>12345678</CreateTime>
		<MsgType><![CDATA[transfer_customer_service]]></MsgType>
	</xml>
 */
public class OutCustomMsg extends OutMsg {

	private String content;

	public OutCustomMsg() {
		this.msgType = "transfer_customer_service";
	}

	public OutCustomMsg(InMsg inMsg) {
		super(inMsg);
		this.msgType = "transfer_customer_service";
	}
	
	@Override
	protected void subXml(StringBuilder sb) {}
	
	public String getContent() {
		return content;
	}
	
	public OutCustomMsg setContent(String content) {
		this.content = content;
		return this;
	}

}


