/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.geaflow.dsl.runtime.function.table;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.PriorityQueue;
import org.apache.geaflow.dsl.common.data.Row;
import org.apache.geaflow.dsl.common.function.FunctionContext;
import org.apache.geaflow.dsl.runtime.function.table.order.SortInfo;
import org.apache.geaflow.dsl.runtime.function.table.order.TopNRowComparator;

public class OrderByHeapSort implements OrderByFunction {

    private final SortInfo sortInfo;

    private PriorityQueue<Row> topNQueue;

    private TopNRowComparator<Row> topNRowComparator;

    public OrderByHeapSort(SortInfo sortInfo) {
        this.sortInfo = sortInfo;
    }

    @Override
    public void open(FunctionContext context) {
        this.topNRowComparator = new TopNRowComparator<>(sortInfo);
        this.topNQueue = new PriorityQueue<>(
            sortInfo.fetch, topNRowComparator.getNegativeComparator());
    }

    @Override
    public void process(Row row) {
        if (topNQueue.size() == sortInfo.fetch) {
            if (sortInfo.orderByFields.isEmpty()) {
                return;
            }
            Row top = topNQueue.peek();
            if (topNQueue.comparator().compare(top, row) < 0) {
                topNQueue.remove();
                topNQueue.add(row);
            }
        } else {
            topNQueue.add(row);
        }
    }

    @Override
    public Iterable<Row> finish() {
        List<Row> results = new ArrayList<>();
        while (!topNQueue.isEmpty()) {
            results.add(topNQueue.remove());
        }
        Collections.reverse(results);
        topNQueue.clear();
        return results;
    }
}
