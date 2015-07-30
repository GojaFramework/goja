
package goja.el.obj;


import goja.el.ElCache;

public interface Elobj {
    public String getVal();
    public Object fetchVal();
    public void setEc(ElCache ec);
}
