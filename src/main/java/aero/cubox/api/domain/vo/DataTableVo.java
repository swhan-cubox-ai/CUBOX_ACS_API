package aero.cubox.api.domain.vo;

import lombok.*;

import java.util.List;

@ToString
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class DataTableVo<T> {

    private int draw;
    private int start;
    private long recordsTotal;
    private long recordsFiltered;
    private List<T> data;
}