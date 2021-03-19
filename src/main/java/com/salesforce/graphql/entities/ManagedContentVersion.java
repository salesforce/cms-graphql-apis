package com.salesforce.graphql.entities;

import com.salesforce.graphql.StatusEnum;
import lombok.Data;

@Data
public class ManagedContentVersion {
    private String Id;
    private String managedContentId;
    private String primaryVariantId;
    private StatusEnum status;
}
