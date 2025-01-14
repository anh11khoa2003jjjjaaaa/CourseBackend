package org.example.sellingcourese.Request;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.sellingcourese.Model.CartDetail;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartWithDetailsDTO {
    private Long userID;
    private List<CartDetail> cartDetails;


}