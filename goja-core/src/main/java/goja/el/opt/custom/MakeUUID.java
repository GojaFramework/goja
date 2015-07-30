/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2013-2014 sagyf Yang. The Four Group.
 */

package goja.el.opt.custom;

import goja.el.opt.RunMethod;

import java.util.List;
import java.util.UUID;


public class MakeUUID implements RunMethod {

	public boolean canWork() {
		return true;
	}

	public Object run(List<Object> fetchParam) {
		return UUID.randomUUID().toString().replace("-", "");
	}

	public String fetchSelf() {
		return "uuid";
	}

}
