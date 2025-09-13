/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * The OpenSearch Contributors require contributions made to
 * this file be licensed under the Apache-2.0 license or a
 * compatible open source license.
 */

package org.opensearch.rest.action.admin.cluster;

import org.opensearch.core.rest.RestStatus;
import org.opensearch.index.store.remote.filecache.FileCache;
import org.opensearch.node.Node;
import org.opensearch.rest.RestRequest;
import org.opensearch.test.rest.FakeRestRequest;
import org.opensearch.test.rest.RestActionTestCase;
import org.opensearch.threadpool.TestThreadPool;
import org.opensearch.threadpool.ThreadPool;
import org.junit.After;
import org.junit.Before;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class RestPruneCacheActionTests extends RestActionTestCase {

    private ThreadPool threadPool;

    @Before
    public void setup() {
        threadPool = new TestThreadPool(this.getClass().getSimpleName());
    }

    @After
    public void tearDown() throws Exception {
        super.tearDown();
        terminate(threadPool);
    }

    public void testRoutes() {
        Node mockNode = mock(Node.class);
        RestPruneCacheAction action = new RestPruneCacheAction(mockNode);
        assertEquals(1, action.routes().size());
        assertEquals(RestRequest.Method.POST, action.routes().get(0).getMethod());
        assertEquals("/_cache/remote/prune", action.routes().get(0).getPath());
    }

    public void testGetName() {
        Node mockNode = mock(Node.class);
        RestPruneCacheAction action = new RestPruneCacheAction(mockNode);
        assertEquals("prune_cache_action", action.getName());
    }

    public void testPrepareRequest() throws Exception {
        // 1. Mock the FileCache service
        FileCache mockFileCache = mock(FileCache.class);
        final long bytesPruned = 12345678L;
        when(mockFileCache.prune()).thenReturn(bytesPruned);

        // 2. Mock the Node (Service Locator) to return the mocked service
        Node mockNode = mock(Node.class);
        when(mockNode.fileCache()).thenReturn(mockFileCache);

        RestPruneCacheAction action = new RestPruneCacheAction(mockNode);
        RestRequest request = new FakeRestRequest();

        // Test that the action prepares correctly without throwing exceptions
        assertNotNull(action.prepareRequest(request, null));
    }
}