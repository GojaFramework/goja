package goja.rapid.datatables;

import java.io.Serializable;
import java.util.List;

/**
 * <p> </p>
 *
 * @author sogYF
 * @version 1.0
 * @since JDK 1.6
 */
public class DTResponse<E> implements Serializable {
    private static final long serialVersionUID = -491962368629446336L;

    private final List<E> data;
    private final long recordsTotal;
    private final long recordsFiltered;
    private final int draw;

    public DTResponse(List<E> data, long recordsTotal, long recordsFiltered, int draw) {
        this.data = data;
        this.recordsTotal = recordsTotal;
        this.recordsFiltered = recordsFiltered;
        this.draw = draw;
    }

    public static <E> DTResponse<E> build(DTCriterias dtCriterias, List<E> list, int recordsTotal,
                                          int recordsFiltered) {
        return new DTResponse<E>(list, recordsTotal, recordsFiltered, dtCriterias.getDraw());
    }

    public List<E> getData() {
        return data;
    }

    public long getRecordsTotal() {
        return recordsTotal;
    }

    public long getRecordsFiltered() {
        return recordsFiltered;
    }

    public int getDraw() {
        return draw;
    }
}
