package com.example.notificationservice.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * CDC 이벤트의 메타데이터
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class SourceMetadata {
    
    private String version;
    
    private String connector;
    
    private String name;
    
    private String db;
    
    private String table;
    
    @JsonProperty("ts_ms")
    private Long timestamp;
}
