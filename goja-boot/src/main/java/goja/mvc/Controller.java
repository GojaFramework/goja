/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2013-2014 sagyf Yang. The Four Group.
 */

package goja.mvc;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.upload.OreillyCos;
import com.jfinal.upload.UploadFile;
import com.oreilly.servlet.multipart.DefaultFileRenamePolicy;
import com.oreilly.servlet.multipart.FileRenamePolicy;
import goja.core.kits.lang.DateKit;
import goja.dao.Dao;
import goja.mvc.kit.Requests;
import goja.mvc.render.BadRequest;
import goja.mvc.render.NotModified;
import goja.rapid.datatables.DTCriterias;
import goja.rapid.datatables.DTResponse;
import goja.rapid.easyui.EuiDataGrid;
import goja.rapid.easyui.req.DataGridReq;
import goja.rapid.page.PageDto;
import goja.security.goja.SecurityKit;
import goja.security.shiro.AppUser;
import goja.security.shiro.Securitys;
import org.joda.time.DateTime;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

import static goja.core.StringPool.SLASH;

/**
 * <p> Controller. </p>
 *
 * @author sagyf yang
 * @version 1.0 2014-06-01 20:29
 * @since JDK 1.6
 */
public class Controller extends com.jfinal.core.Controller {
    /**
     * 上传时的文件命名方案
     */
    private static final FileRenamePolicy fileRenamePolicy = new DefaultFileRenamePolicy();

    /**
     * Send a 304 Not Modified response
     */
    protected static void notModified() {
        new NotModified().render();
    }

    /**
     * Send a 400 Bad request
     */
    protected static void badRequest() {
        new BadRequest().render();
    }

    /**
     * Rendering errors information, in Json format.
     *
     * @param error error message.
     */
    protected void renderAjaxError(String error) {
        renderJson(AjaxMessage.error(error));
    }

    /**
     * Rendering errors information, in Json format.
     *
     * @param data error data.
     */
    protected <T> void renderAjaxError(T data) {
        renderJson(AjaxMessage.error(data));
    }

    /**
     * Rendering errors information, in Json format.
     *
     * @param error errors information.
     * @param e     exception.
     */
    protected void renderAjaxError(String error, Exception e) {
        renderJson(AjaxMessage.error(error, e));
    }

    /**
     * In the form of JSON rendering failure information.
     *
     * @param failure failure information.
     */
    protected void renderAjaxFailure(String failure) {
        renderJson(AjaxMessage.failure(failure));
    }

    /**
     * In the form of JSON rendering failure information.
     */
    protected void renderAjaxFailure() {
        renderJson(AjaxMessage.FAILURE);
    }

    /**
     * In the form of JSON rendering forbidden information.
     */
    protected void renderAjaxForbidden() {
        renderJson(AjaxMessage.FORBIDDEN);
    }

    /**
     * In the form of JSON rendering forbidden information.
     *
     * @param data the forbidden data.
     * @param <T>  Generic parameter.
     */
    protected <T> void renderAjaxForbidden(T data) {
        renderJson(AjaxMessage.forbidden(data));
    }

    /**
     * In the form of JSON rendering forbidden information.
     *
     * @param message the forbidden message.
     * @param data    the forbidden data.
     * @param <T>     Generic parameter.
     */
    protected <T> void renderAjaxForbidden(String message, T data) {
        renderJson(AjaxMessage.forbidden(message, data));
    }

    /**
     * In the form of JSON rendering success information.
     *
     * @param message success information.
     */
    protected void renderAjaxSuccess(String message) {
        renderJson(AjaxMessage.ok(message));
    }

    /**
     * In the form of JSON rendering default success information.
     */
    protected void renderAjaxSuccess() {
        renderJson(AjaxMessage.OK);
    }

    /**
     * With the success of data information.
     *
     * @param data the render data.
     * @param <T>  Generic parameter.
     */
    protected <T> void renderAjaxSuccess(T data) {
        renderJson(AjaxMessage.ok(data));
    }

    protected <T> void renderAjaxSuccess(List<T> list) {
        if (list == null || list.isEmpty()) {
            renderJson(AjaxMessage.nodata());
        } else {
            renderJson(AjaxMessage.ok(list));
        }
    }

    /**
     * News Ajax rendering not logged in.
     */
    protected void renderAjaxNologin() {
        renderJson(AjaxMessage.nologin());
    }

    /**
     * News Ajax rendering not logged in.
     *
     * @param data the render data.
     * @param <T>  Generic parameter.
     */
    protected <T> void renderAjaxNologin(T data) {
        renderJson(AjaxMessage.nologin(data));
    }

    /**
     * Render the empty data.
     */
    protected void renderAjaxNodata() {
        renderJson(AjaxMessage.NODATA);
    }

    /**
     * Render the specified view as a string.
     *
     * @param view view template.
     * @return the string.
     */
    protected String renderTpl(String view) {
        return template(view);
    }

    /**
     * Render view as a string
     *
     * @param view view
     * @return render string.
     */
    protected String template(String view) {
        final Enumeration<String> attrs = getAttrNames();
        final Map<String, Object> root = Maps.newHashMap();
        while (attrs.hasMoreElements()) {
            String attrName = attrs.nextElement();
            root.put(attrName, getAttr(attrName));
        }
        return Freemarkers.processString(view, root);
    }

    /**
     * Renderingtodo prompt
     */
    protected void renderTODO() {
        renderJson(AjaxMessage.developing());
    }

    /**
     * Based on the current path structure is going to jump full Action of the path
     *
     * @param currentActionPath The current path, similar to/sau/index
     * @param url               The next path, similar to/au/login, the detail? The admin/detail.
     * @return An Action under the full path.
     */
    protected String parsePath(String currentActionPath, String url) {
        if (url.startsWith(SLASH)) {
            return url.split("\\?")[0];
        } else if (!url.contains(SLASH)) {
            return SLASH + currentActionPath.split(SLASH)[1] + SLASH + url.split("\\?")[0];
        } else if (url.contains("http:") || url.contains("https:")) {
            return null;
        }
        ///abc/def","bcd/efg?abc
        return currentActionPath + SLASH + url.split("\\?")[0];
    }

    /**
     * The Request is ajax with return <code>true</code>.
     *
     * @return true the request is ajax request.
     */
    protected boolean isAjax() {
        return Requests.ajax(getRequest());
    }

    /**
     * Get parameters from the Request and encapsulation as an object for processing。
     *
     * @return jquery DataTables
     */
    protected DTCriterias getCriterias() {
        return DTCriterias.criteriasWithRequest(getRequest());
    }

    /**
     * According to the request information of jquery.Datatables, the results of the query and returns
     * the JSON data to the client. <p/> The specified query set the data.
     *
     * @param datas     The data.
     * @param criterias datatable criterias.
     * @param <E>       Generic parameter.
     */
    protected <E> void renderDataTables(DTCriterias criterias, Page<E> datas) {
        Preconditions.checkNotNull(criterias, "datatable criterias is must be not null.");
        DTResponse<E> response =
                DTResponse.build(criterias, datas.getList(), datas.getTotalRow(), datas.getTotalPage());
        renderJson(response);
    }

    /**
     * According to the request information of jquery.Datatables, the results of the query and returns
     * the JSON data to the client. <p/> Automatically according to the request to create the query
     * SQL and encapsulating the results back to the client.
     *
     * @param m_cls     The Model class.
     * @param criterias datatable criterias.
     */
    protected void renderDataTables(DTCriterias criterias, Class<? extends Model> m_cls) {
        Preconditions.checkNotNull(criterias, "datatable criterias is must be not null.");
        DTResponse response = criterias.response(m_cls);
        renderJson(response);
    }

    /**
     * According to the request information of jquery.Datatables, the results of the query and returns
     * the JSON data to the client. <p/> According to the SQL configuration file, in accordance with
     * the Convention model_name.coloumns\model_name.where\model_name.order configured SQL to query
     * and returns the results to the client.
     *
     * @param model_name The Model name.
     * @param criterias  datatable criterias.
     */
    protected void renderDataTables(DTCriterias criterias, String model_name) {
        Preconditions.checkNotNull(criterias, "datatable criterias is must be not null.");
        final Page<Record> datas = Dao.paginate(model_name, criterias);
        DTResponse response =
                DTResponse.build(criterias, datas.getList(), datas.getTotalRow(), datas.getTotalRow());
        renderJson(response);
    }

    /**
     * According to the request information of jquery.Datatables, the results of the query and returns
     * the JSON data to the client. <p/> According to the SQL configuration file, in accordance with
     * the Convention model_name.coloumns\model_name.where\model_name.order configured SQL to query
     * and specify the parameters and return results to the client.
     *
     * @param criterias    datatable criterias.
     * @param sqlGroupName The Model name.
     * @param params       Query parameters
     */
    protected void renderDataTables(DTCriterias criterias, String sqlGroupName, List<Object> params) {
        Preconditions.checkNotNull(criterias, "datatable criterias is must be not null.");
        final Page<Record> datas = Dao.paginate(sqlGroupName, criterias, params);
        DTResponse response =
                DTResponse.build(criterias, datas.getList(), datas.getTotalRow(), datas.getTotalRow());
        renderJson(response);
    }

    /**
     * The source of data for rendering the jQuery Datatables
     *
     * @param m_cls The Model class.
     */
    protected void renderDataTables(Class<? extends Model> m_cls) {
        DTCriterias criterias = getCriterias();
        Preconditions.checkNotNull(criterias, "datatable criterias is must be not null.");
        DTResponse response = criterias.response(m_cls);
        renderJson(response);
    }

    /**
     * rendering the empty datasource.
     *
     * @param criterias datatable criterias.
     */
    protected void renderEmptyDataTables(DTCriterias criterias) {
        Preconditions.checkNotNull(criterias, "datatable criterias is must be not null.");
        DTResponse response = DTResponse.build(criterias, Collections.EMPTY_LIST, 0, 0);
        renderJson(response);
    }

    /**
     * 获取EasyUI Datagrid 的表格参数
     *
     * @return 表格请求参数
     */
    protected Optional<DataGridReq> getEuiDGReq() {
        return EuiDataGrid.req(getRequest());
    }

    /**
     * 渲染DataGrid的表格展示，支持排序和搜索
     *
     * @param modelClass 指定类
     */
    protected void renderEasyUIDataGrid(Class<? extends Model> modelClass) {
        final Optional<DataGridReq> reqOptional = EuiDataGrid.req(getRequest());
        if (reqOptional.isPresent()) {
            renderJson(EuiDataGrid.rsp(reqOptional.get(), modelClass));
        } else {
            renderJson(EuiDataGrid.EMPTY_DATAGRID);
        }
    }

    /**
     * 渲染DataGrid的表格展示，支持排序和搜索
     *
     * @param gridReq      请求参数
     * @param sqlGroupName 指定SQL管理的GroupName
     */
    protected void renderEasyUIDataGrid(DataGridReq gridReq, String sqlGroupName) {
        Preconditions.checkNotNull(gridReq, "参数不能为空");
        renderJson(EuiDataGrid.rsp(gridReq, sqlGroupName));
    }

    /**
     * 渲染DataGrid的表格展示，支持排序和搜索
     *
     * @param sqlGroupName 指定SQL管理的GroupName
     */
    protected void renderEasyUIDataGrid(String sqlGroupName) {
        Preconditions.checkNotNull(sqlGroupName, "sql Group Name 不能为空");
        final Optional<DataGridReq> reqOptional = EuiDataGrid.req(getRequest());
        if (reqOptional.isPresent()) {
            renderJson(EuiDataGrid.rsp(reqOptional.get(), sqlGroupName));
        } else {
            renderJson(EuiDataGrid.EMPTY_DATAGRID);
        }
    }

    /**
     * 渲染DataGrid的表格展示，支持排序和搜索
     *
     * @param sqlGroupName 指定SQL管理的GroupName
     */
    protected void renderEasyUIDataGrid(String sqlGroupName, List<Object> params) {
        Preconditions.checkNotNull(sqlGroupName, "sql Group Name 不能为空");
        final Optional<DataGridReq> reqOptional = EuiDataGrid.req(getRequest());
        if (reqOptional.isPresent()) {
            renderJson(EuiDataGrid.rsp(reqOptional.get(), sqlGroupName, params));
        } else {
            renderJson(EuiDataGrid.EMPTY_DATAGRID);
        }
    }

    /**
     * For information on the logged in user. <p/> This access is through the way the Cookie and
     * Session
     *
     * @param <M> Generic parameter.
     * @return user model.
     */
    protected <M extends Model> Optional<M> getLogin() {
        final HttpServletRequest request = getRequest();
        if (SecurityKit.isLogin(request)) {
            final M user = SecurityKit.getLoginUser(request);
            return Optional.of(user);
        } else {
            return Optional.absent();
        }
    }

    /**
     * The current Shiro login user. <p/> If it opens the secruity function can call this method to
     * obtain the logged in user.
     *
     * @param <U> Generic parameter.
     * @return Shiro login user.
     */
    protected <U extends Model> Optional<AppUser<U>> getPrincipal() {
        if (Securitys.isLogin()) {
            final AppUser<U> appUser = Securitys.getLogin();
            return Optional.of(appUser);
        }
        return Optional.absent();
    }

    /**
     * To get custom paging object.
     *
     * @return To get custom paging object
     */
    protected PageDto getQueryPage() {
        return PageDto.create(this);
    }

    /**
     * JodaTime time request
     *
     * @param name param. the datetime format : yyyy-MM-dd
     * @return datetime.
     */
    protected DateTime getDate(String name) {
        return getDate(name, DateTime.now());
    }

    /**
     * JodaTime time request
     *
     * @param name         param. the datetime format : yyyy-MM-dd
     * @param defaultValue the default value.
     * @return datetime.
     */
    protected DateTime getDate(String name, DateTime defaultValue) {
        String value = getRequest().getParameter(name);
        if (Strings.isNullOrEmpty(value)) {
            return defaultValue;
        }
        return DateKit.parseDashYMDDateTime(value);
    }

    /**
     * JodaTime time request
     *
     * @param name param. the datetime format : yyyy-MM-dd HH:mm:ss
     * @return datetime.
     */
    protected DateTime getDateTime(String name) {
        return getDateTime(name, DateTime.now());
    }

    /**
     * JodaTime time request
     *
     * @param name         param. the datetime format : yyyy-MM-dd HH:mm:ss
     * @param defaultValue the default value.
     * @return datetime.
     */
    protected DateTime getDateTime(String name, DateTime defaultValue) {
        String value = getRequest().getParameter(name);
        if (Strings.isNullOrEmpty(value)) {
            return defaultValue;
        }
        return DateKit.parseDashYMDHMSDateTime(value);
    }

    public List<UploadFile> getFiles(String saveDirectory, Integer maxPostSize,
                                     String encoding, FileRenamePolicy fileRenamePolicy) {
        OreillyCos.setFileRenamePolicy(fileRenamePolicy);
        return super.getFiles(saveDirectory, maxPostSize, encoding);
    }

    public List<UploadFile> getFiles(String saveDirectory, int maxPostSize,
                                     FileRenamePolicy fileRenamePolicy) {
        OreillyCos.setFileRenamePolicy(fileRenamePolicy);
        return super.getFiles(saveDirectory, maxPostSize);
    }

    public List<UploadFile> getFiles(String saveDirectory,
                                     FileRenamePolicy fileRenamePolicy) {
        OreillyCos.setFileRenamePolicy(fileRenamePolicy);
        return super.getFiles(saveDirectory);
    }

    public List<UploadFile> getFiles(FileRenamePolicy fileRenamePolicy) {
        OreillyCos.setFileRenamePolicy(fileRenamePolicy);
        return super.getFiles();
    }

    /**
     * call self
     */

    /**
     * File
     */
    public UploadFile getFile(String parameterName, String saveDirectory,
                              Integer maxPostSize, String encoding) {
        return this.getFile(parameterName, saveDirectory, maxPostSize, encoding,
                fileRenamePolicy);
    }

    public UploadFile getFile(String parameterName, String saveDirectory,
                              Integer maxPostSize, String encoding,
                              FileRenamePolicy fileRenamePolicy) {
        this.getFiles(saveDirectory, maxPostSize, encoding, fileRenamePolicy);
        return this.getFile(parameterName, fileRenamePolicy);
    }

    public UploadFile getFile(String parameterName, String saveDirectory,
                              int maxPostSize) {
        return this.getFile(parameterName, saveDirectory, maxPostSize,
                fileRenamePolicy);
    }

    public UploadFile getFile(String parameterName, String saveDirectory,
                              int maxPostSize, FileRenamePolicy fileRenamePolicy) {
        this.getFiles(saveDirectory, maxPostSize, fileRenamePolicy);
        return this.getFile(parameterName, fileRenamePolicy);
    }

    public UploadFile getFile(String parameterName, String saveDirectory) {
        return this.getFile(parameterName, saveDirectory, fileRenamePolicy);
    }

    public UploadFile getFile(String parameterName, String saveDirectory,
                              FileRenamePolicy fileRenamePolicy) {
        this.getFiles(saveDirectory, fileRenamePolicy);
        return this.getFile(parameterName, fileRenamePolicy);
    }

    public UploadFile getFile() {
        return this.getFile(fileRenamePolicy);
    }

    public UploadFile getFile(FileRenamePolicy fileRenamePolicy) {
        List<UploadFile> uploadFiles = this.getFiles(fileRenamePolicy);
        return uploadFiles.size() > 0 ? uploadFiles.get(0) : null;
    }

    public UploadFile getFile(String parameterName) {
        return this.getFile(parameterName, fileRenamePolicy);
    }

    public UploadFile getFile(String parameterName,
                              FileRenamePolicy fileRenamePolicy) {
        List<UploadFile> uploadFiles = this.getFiles(fileRenamePolicy);
        for (UploadFile uploadFile : uploadFiles) {
            if (uploadFile.getParameterName().equals(parameterName)) {
                return uploadFile;
            }
        }
        return null;
    }

    /**
     * Files
     */
    public List<UploadFile> getFiles(String saveDirectory, Integer maxPostSize,
                                     String encoding) {
        return this.getFiles(saveDirectory, maxPostSize, encoding, fileRenamePolicy);
    }

    public List<UploadFile> getFiles(String saveDirectory, int maxPostSize) {
        return this.getFiles(saveDirectory, maxPostSize, fileRenamePolicy);
    }

    public List<UploadFile> getFiles(String saveDirectory) {
        return this.getFiles(saveDirectory, fileRenamePolicy);
    }

    protected void renderAjaxSimpleSuccess(String msg) {
        renderJson(new SimpleAjaxMessage(true, msg));
    }

    protected void renderAjaxSimpleError(String msg) {
        renderJson(new SimpleAjaxMessage(false, msg));
    }
}
