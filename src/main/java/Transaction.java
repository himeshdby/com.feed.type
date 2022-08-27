import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Transaction {

    private String id;
    private String feedType;
    private Integer feedQantity;
    private String quantityType;
    private String date;
    private String clientId;


}
