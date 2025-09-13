/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * The OpenSearch Contributors require contributions made to
 * this file be licensed under the Apache-2.0 license or a
 * compatible open source license.
 */

package org.opensearch.rest.action.admin.cluster;

import org.opensearch.core.rest.RestStatus;
import org.opensearch.core.xcontent.XContentBuilder;
import org.opensearch.index.store.remote.filecache.FileCache;
import org.opensearch.node.Node;
import org.opensearch.rest.BaseRestHandler;
import org.opensearch.rest.BytesRestResponse;
import org.opensearch.rest.RestRequest;
import org.opensearch.rest.RestRequest.Method;
import org.opensearch.transport.client.node.NodeClient;

import java.io.IOException;
import java.util.List;

import static java.util.Collections.singletonList;

/**
 * REST action to manually trigger FileCache prune operation.
 * This endpoint allows administrators to clear out non-referenced cache entries on demand.
 *
 * @opensearch.api
 */
public class RestPruneCacheAction extends BaseRestHandler {

    private final FileCache fileCache;

    // The constructor now accepts the 'Node' object as the Service Locator.
    public RestPruneCacheAction(Node node) {
        // The handler uses the locator to retrieve the specific service it needs.
        this.fileCache = node.fileCache();
    }

    @Override
    public List<Route> routes() {
        return singletonList(new Route(Method.POST, "/_cache/remote/prune"));
    }

    @Override
    public String getName() {
        return "prune_cache_action";
    }

    @Override
    protected RestChannelConsumer prepareRequest(RestRequest request, NodeClient client) throws IOException {
        return channel -> {
            // Execute the prune operation
            long prunedBytes = fileCache.prune();

            // Build JSON response
            XContentBuilder builder = channel.newBuilder();
            builder.startObject();
            builder.field("acknowledged", true);
            builder.field("pruned_bytes", prunedBytes);
            builder.endObject();

            channel.sendResponse(new BytesRestResponse(RestStatus.OK, builder));
        };
    }
}