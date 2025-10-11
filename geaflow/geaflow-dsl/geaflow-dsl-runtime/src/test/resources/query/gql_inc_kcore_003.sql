/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the License.  You may obtain a copy of the License at
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

/*
 * Incremental K-Core algorithm edge addition test
 * Test incremental update after adding edges on dynamic graph
 */
CREATE TABLE inc_kcore_edge_add_result (
  vid int,
  core_value int,
  degree int,
  change_status varchar
) WITH (
    type='file',
    geaflow.dsl.file.path = '${target}'
);

USE GRAPH dynamic_graph;

-- Add new edge for testing edge addition scenario
INSERT INTO dynamic_graph.connects VALUES (1001, 1002, 1.0);

-- K-Core calculation with the added edge
INSERT INTO inc_kcore_edge_add_result
CALL incremental_kcore(2) YIELD (vid, core_value, degree, change_status)
RETURN vid, core_value, degree, change_status
ORDER BY vid;
