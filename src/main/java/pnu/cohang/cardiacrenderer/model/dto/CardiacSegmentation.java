package pnu.cohang.cardiacrenderer.model.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CardiacSegmentation {
    private Integer id;
    private String name;
    private Float ED_vol_LV;
    private Float ED_vol_RV;
    private Float EF_LV;
    private Float EF_RV;
    private Float ED_max_MTH;
    private String diseaseGroup;
}
