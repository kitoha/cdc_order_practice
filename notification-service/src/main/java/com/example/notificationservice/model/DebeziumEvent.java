package com.example.notificationservice.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class DebeziumEvent {
    
    private OrderData before;
    
    private OrderData after;
    
    private SourceMetadata source;
    
    private String op;
    
    @JsonProperty("ts_ms")
    private Long timestamp;
    
    /**
     * CREATE 이벤트 체크
     */
    public boolean isCreate() {
        return "c".equals(op);
    }
    
    /**
     * UPDATE 이벤트 체크
     */
    public boolean isUpdate() {
        return "u".equals(op);
    }
    
    /**
     * DELETE 이벤트 체크
     */
    public boolean isDelete() {
        return "d".equals(op);
    }
    
    /**
     * SNAPSHOT 이벤트 체크 (초기 스냅샷)
     */
    public boolean isSnapshot() {
        return "r".equals(op);
    }
}
