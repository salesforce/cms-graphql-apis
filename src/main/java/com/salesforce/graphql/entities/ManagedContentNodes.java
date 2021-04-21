package com.salesforce.graphql.entities;

import lombok.Data;

@Data
public class ManagedContentNodes {
    private ManagedContentAltText altText;
    private ManagedContentSource source;
    private ManagedContentTitle title;
    private ManagedContentBannerImage bannerImage;
    private ManagedContentExcerpt excerpt;
    private ManagedContentBody body;
}
