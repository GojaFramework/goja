/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2013-2014 sagyf Yang. The Four Group.
 */

package goja.el.parse;

import goja.exceptions.ElException;
import goja.el.Parse;
import goja.el.obj.Elobj;
import goja.el.obj.FieldObj;
import goja.el.obj.IdentifierObj;
import goja.el.obj.MethodObj;
import goja.el.opt.arithmetic.LBracketOpt;
import goja.el.opt.arithmetic.NegativeOpt;
import goja.el.opt.arithmetic.RBracketOpt;
import goja.el.opt.arithmetic.SubOpt;
import goja.el.opt.object.AccessOpt;
import goja.el.opt.object.CommaOpt;
import goja.el.opt.object.InvokeMethodOpt;
import goja.el.opt.object.MethodOpt;
import goja.lang.Lang;

import java.io.Reader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


/**
 * 转换器,也就是用来将字符串转换成队列. TODO 这个类的名字不知道取什么好...
 * 
 * @author juqkai(juqkai@gmail.com)
 * 
 */
public class Converter {
    private final List<Parse> parses = new ArrayList<Parse>();

    // 表达式字符队列
    private CharQueue          exp;
    // 表达式项
    private LinkedList<Object> itemCache;
    // 方法栈
    private LinkedList<MethodOpt> methods = new LinkedList<MethodOpt>();

    // 上一个数据
    private Object prev = null;

    public Converter(CharQueue reader) {
        this.exp = reader;
        itemCache = new LinkedList<Object>();
        skipSpace();
        initParse();
    }

    public Converter(String val) {
        this(Lang.inr(val));
    }

    public Converter(Reader reader) {
        this(new CharQueueDefault(reader));
    }

    /**
     * 初始化解析器
     */
    private void initParse() {
        parses.add(new OptParse());
        parses.add(new StringParse());
        parses.add(new IdentifierParse());
        parses.add(new ValParse());
    }

    /**
     * 重新设置解析器
     */
    public void setParse(List<Parse> val) {
        parses.addAll(val);
    }

    /**
     * 初始化EL项
     */
    public void initItems() {
        while (!exp.isEmpty()) {
            Object obj = parseItem();
            // 处理数组的情况
            if (obj.getClass().isArray()) {
                for (Object o : (Object[]) obj) {
                    itemCache.add(o);
                }
                continue;
            }
            itemCache.add(obj);
        }
        itemCache = clearUp(itemCache);
    }

    /**
     * 清理转换后的结果, 主要做一些标识性的转换
     * 
     * @param rpn
     * @return
     */
    private LinkedList<Object> clearUp(LinkedList<Object> rpn) {
        LinkedList<Object> dest = new LinkedList<Object>();
        while (!rpn.isEmpty()) {
            if (!(rpn.getFirst() instanceof Elobj)) {
                dest.add(rpn.removeFirst());
                continue;
            }
            Elobj obj = (Elobj) rpn.removeFirst();
            // 方法对象
            if (!rpn.isEmpty() && rpn.getFirst() instanceof MethodOpt) {
                dest.add(new MethodObj(obj.getVal()));
                continue;
            }
            // 属性对象
            if (dest.size() > 0
                && dest.getLast() instanceof AccessOpt
                && rpn.size() > 0
                && rpn.getFirst() instanceof AccessOpt) {
                dest.add(new FieldObj(obj.getVal()));
                continue;
            }
            // //普通的对象
            // if(!(dest.getLast() instanceof AccessOpt) && !(rpn.peekFirst()
            // instanceof MethodOpt)){
            // continue;
            // }
            dest.add(new IdentifierObj(obj.getVal()));
        }
        return dest;
    }

    /**
     * 解析数据
     */
    private Object parseItem() {
        Object obj = Parse.nullobj;
        for (Parse parse : parses) {
            obj = parse.fetchItem(exp);
            if (obj != Parse.nullobj) {
                skipSpace();
                return parseItem(obj);
            }
        }
        throw new ElException("无法解析!");
    }

    /**
     * 转换数据,主要是转换负号,方法执行
     */
    private Object parseItem(Object item) {
        // 处理参数个数
        if (methods.peek() != null) {
            MethodOpt opt = methods.peek();
            if (opt.getSize() <= 0) {
                if (!(item instanceof CommaOpt) && !(item instanceof RBracketOpt)) {
                    opt.setSize(1);
                }
            } else {
                if (item instanceof CommaOpt) {
                    opt.setSize(opt.getSize() + 1);
                }
            }
        }

        // 左括号
        if (item instanceof LBracketOpt) {
            if (prev instanceof Elobj) {
                MethodOpt prem = new MethodOpt();
                item = new Object[]{prem, new LBracketOpt()};
                methods.addFirst(prem);
            } else {
                methods.addFirst(null);
            }
        }

        // 右括号
        if (item instanceof RBracketOpt) {
            if (methods.poll() != null) {
                item = new Object[]{new RBracketOpt(), new InvokeMethodOpt()};
            }
        }
        // 转换负号'-'
        if (item instanceof SubOpt && NegativeOpt.isNegetive(prev)) {
            item = new NegativeOpt();
        }
        prev = item;
        return item;
    }

    /**
     * 跳过空格,并返回是否跳过空格(是否存在空格)
     */
    private boolean skipSpace() {
        boolean space = false;
        while (!exp.isEmpty() && Character.isWhitespace(exp.peek())) {
            space = true;
            exp.poll();
        }
        return space;
    }

    /**
     * 取得一个有效数据
     */
    public Object fetchItem() {
        return itemCache.poll();
    }

    /**
     * 是否结束
     */
    public boolean isEnd() {
        return itemCache.isEmpty();
    }
}
