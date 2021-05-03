package com.salesforce.graphql.graphqlapi;

import com.salesforce.graphql.SessionRequest;
import com.salesforce.graphql.entities.ManagedContentChannel;
import com.salesforce.graphql.entities.ManagedContentSearchItem;
import com.salesforce.graphql.utils.HttpUtility;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingFieldSelectionSet;
import org.apache.http.util.TextUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.stream.*;

@Component
public class GraphQLDataFetchers extends Object{
    @Autowired
    SessionRequest sessionRequest;

    public DataFetcher getChannelByIdDataFetcher() {


        return dataFetchingEnvironment -> {
            DataFetchingFieldSelectionSet selectionSet = dataFetchingEnvironment.getSelectionSet();
            String channelId = dataFetchingEnvironment.getArgument("channelId");

            List<ManagedContentChannel> channels = HttpUtility.getManagedContentChannelObject(
                    sessionRequest,
                    dataFetchingEnvironment,
                    selectionSet);

            if(TextUtils.isEmpty(channelId)){
                return  channels;
            }

            return channels
                    .stream()
                    .filter(channel -> channel.getChannelId().equals(channelId))
                    .findFirst()
                    .stream()
                    .collect(Collectors.toList());
        };
    }

    public DataFetcher searchContent() throws IOException, URISyntaxException {
        return dataFetchingEnvironment -> {
            String channelId = dataFetchingEnvironment.getArgument("channelId");
            String queryTerm = dataFetchingEnvironment.getArgument("queryTerm");
            List<ManagedContentSearchItem> searchItems = HttpUtility.searchContent(sessionRequest, channelId, queryTerm);
            return searchItems;
        };
    }
}

