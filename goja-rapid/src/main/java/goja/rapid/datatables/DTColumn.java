package goja.rapid.datatables;

/**
 * <p> </p>
 *
 * @author sogYF
 * @version 1.0
 * @since JDK 1.6
 */
public final class DTColumn {

    private String data;
    private String name;
    private boolean searchable;
    private boolean orderable;

    private DTSearch search;

    public DTColumn(String data, String name, boolean searchable, boolean orderable,
                    DTSearch search) {
        this.data = data;
        this.name = name;
        this.searchable = searchable;
        this.orderable = orderable;
        this.search = search;
    }

    public String getData() {
        return data;
    }

    protected void setData(String data) {
        this.data = data;
    }

    public String getName() {
        return name;
    }

    protected void setName(String name) {
        this.name = name;
    }

    public boolean isSearchable() {
        return searchable;
    }

    protected void setSearchable(boolean searchable) {
        this.searchable = searchable;
    }

    public boolean isOrderable() {
        return orderable;
    }

    protected void setOrderable(boolean orderable) {
        this.orderable = orderable;
    }

    public DTSearch getSearch() {
        return search;
    }

    protected void setSearch(DTSearch search) {
        this.search = search;
    }
}
