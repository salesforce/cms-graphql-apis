package com.salesforce.graphql.entities;

import lombok.Data;

import java.util.List;

@Data
public class ManagedContentChannel {
    private String channelId;
    private String channelName;
    private String channelType;
    private String domainId;
    private String domainName;
    private String isChannelSearchable;
    private String isDomainLocked;
    private List<ManagedContentItem> items;
}
