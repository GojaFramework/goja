/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2013-2014 sagyf Yang. The Four Group.
 */

package goja.el.parse;


import goja.exceptions.ElException;
import goja.el.Parse;
import goja.el.opt.arithmetic.DivOpt;
import goja.el.opt.arithmetic.LBracketOpt;
import goja.el.opt.arithmetic.ModOpt;
import goja.el.opt.arithmetic.MulOpt;
import goja.el.opt.arithmetic.PlusOpt;
import goja.el.opt.arithmetic.RBracketOpt;
import goja.el.opt.arithmetic.SubOpt;
import goja.el.opt.bit.BitAnd;
import goja.el.opt.bit.BitNot;
import goja.el.opt.bit.BitOr;
import goja.el.opt.bit.BitXro;
import goja.el.opt.bit.LeftShift;
import goja.el.opt.bit.RightShift;
import goja.el.opt.bit.UnsignedLeftShift;
import goja.el.opt.logic.AndOpt;
import goja.el.opt.logic.EQOpt;
import goja.el.opt.logic.GTEOpt;
import goja.el.opt.logic.GTOpt;
import goja.el.opt.logic.LTEOpt;
import goja.el.opt.logic.LTOpt;
import goja.el.opt.logic.NEQOpt;
import goja.el.opt.logic.NotOpt;
import goja.el.opt.logic.OrOpt;
import goja.el.opt.logic.QuestionOpt;
import goja.el.opt.logic.QuestionSelectOpt;
import goja.el.opt.object.AccessOpt;
import goja.el.opt.object.ArrayOpt;
import goja.el.opt.object.CommaOpt;
import goja.el.opt.object.FetchArrayOpt;

/**
 * 操作符转换器
 * @author juqkai(juqkai@gmail.com)
 *
 */
public class OptParse implements Parse {

    public Object fetchItem(CharQueue exp){
        switch(exp.peek()){
        case '+':
            exp.poll();
            return new PlusOpt();
        case '-':
            exp.poll();
            return new SubOpt();
        case '*':
            exp.poll();
            return new MulOpt();
        case '/':
            exp.poll();
            return new DivOpt();
        case '%':
            exp.poll();
            return new ModOpt();
        case '(':
            exp.poll();
            return new LBracketOpt();
        case ')':
            exp.poll();
            return new RBracketOpt();
        case '>':
            exp.poll();
            switch(exp.peek()){
            case '=':
                exp.poll();
                return new GTEOpt();
            case '>':
                exp.poll();
                if(exp.peek() == '>'){
                    exp.poll();
                    return new UnsignedLeftShift();
                }
                return new RightShift();
            }
            return new GTOpt();
        case '<':
            exp.poll();
            switch(exp.peek()){
            case '=':
                exp.poll();
                return new LTEOpt();
            case '<':
                exp.poll();
                return new LeftShift();
            }
            return new LTOpt();
        case '=':
            exp.poll();
            switch(exp.peek()){
            case '=':
                exp.poll();
                return new EQOpt();
            }
            throw new ElException("表达式错误,请检查'='后是否有非法字符!");
        case '!':
            exp.poll();
            switch(exp.peek()){
            case '=':
                exp.poll();
                return new NEQOpt();
            }
            return new NotOpt();
        case '|':
            exp.poll();
            switch(exp.peek()){
            case '|':
                exp.poll();
                return new OrOpt();
            }
            return new BitOr();
        case '&':
            exp.poll();
            switch(exp.peek()){
            case '&':
                exp.poll();
                return new AndOpt();
            }
            return new BitAnd();
        case '~':
            exp.poll();
            return new BitNot();
        case '^':
            exp.poll();
            return new BitXro();
        case '?':
            exp.poll();
            return new QuestionOpt();
        case ':':
            exp.poll();
            return new QuestionSelectOpt();
        
        case '.':
            if(!Character.isJavaIdentifierStart(exp.peek(1))){
                return nullobj;
            }
            exp.poll();
            return new AccessOpt();
        case ',':
            exp.poll();
            return new CommaOpt();
        case '[':
            exp.poll();
            return new Object[]{new ArrayOpt(),new LBracketOpt()};
        case ']':
            exp.poll();
            return new Object[]{new RBracketOpt(), new FetchArrayOpt()};
        }
        return nullobj;
    }

}
