package goja.rapid.dto;

import com.google.common.collect.Lists;

import java.io.Serializable;
import java.util.List;

/**
 * <p> </p>
 *
 * @author sogYF
 * @version 1.0
 * @since JDK 1.6
 */
public class ZtreeDto<T> implements Serializable {
    private static final long serialVersionUID = -739901398488050752L;

    /**
     * dto ID
     */
    public int id;

    /**
     * 节点名称。 1、如果不使用 name 属性保存节点名称，请修改 setting.data.key.name
     */
    public String name;
    /**
     * 设置点击节点后在何处打开 url。[treeNode.url 存在时有效]
     */
    public String target;
    /**
     * 节点链接的目标 URL 1、编辑模式 (setting.edit.enable = true) 下此属性功能失效，如果必须使用类似功能，请利用 onClick 事件回调函数自行控制。
     * 2、如果需要在 onClick 事件回调函数中进行跳转控制，那么请将 URL 地址保存在其他自定义的属性内，请勿使用 url
     */
    public String url;

    /**
     * 判断 treeNode 节点是否被隐藏。 1、初始化 zTree 时，如果节点设置 isHidden = true，会被自动隐藏 2、请勿对已加载的节点修改此属性，隐藏 / 显示 请使用
     * hideNode() / hideNodes() / showNode() / showNodes() 方法
     */
    public boolean isHidden;
    /**
     * 记录 treeNode 节点是否为父节点。 1、初始化节点数据时，根据 treeNode.children 属性判断，有子节点则设置为 true，否则为 false
     * 2、初始化节点数据时，如果设定 treeNode.isParent = true，即使无子节点数据，也会设置为父节点 3、为了解决部分朋友生成 json 数据出现的兼容问题, 支持
     * "false","true" 字符串格式的数据
     */
    public boolean isParent;

    /**
     * 记录 treeNode 节点的 展开 / 折叠 状态。 1、初始化节点数据时，如果设定 treeNode.open = true，则会直接展开此节点 2、叶子节点 treeNode.open
     * = false 3、为了解决部分朋友生成 json 数据出现的兼容问题, 支持 "false","true" 字符串格式的数据 4、非异步加载模式下，无子节点的父节点设置 open=true
     * 后，可显示为展开状态，但异步加载模式下不会生效。（v3.5.15+）
     */
    public boolean open;

    /**
     * 节点的子节点数据集合
     */
    public List<ZtreeDto<T>> children = Lists.newArrayList();

    public T data;

    public T getData() {
        return data;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public boolean isIsParent() {
        return isParent;
    }

    public String getTarget() {
        return target;
    }

    public String getUrl() {
        return url;
    }

    public boolean isIsHidden() {
        return isHidden;
    }

    public boolean isOpen() {
        return open;
    }

    public List<ZtreeDto<T>> getChildren() {
        return children;
    }
}
