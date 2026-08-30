package lk.ijse.etechbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.ResponseEntity;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommonResponse {

    private int status;
    private Object body;
    private String message;

    public CommonResponse(int status, String message) {
        this.status = status;
        this.message = message;
    }


}
