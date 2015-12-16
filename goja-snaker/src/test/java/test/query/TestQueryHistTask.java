/* Copyright 2013-2015 www.snakerflow.com.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package test.query;

import org.junit.Test;
import org.snaker.engine.access.Page;
import org.snaker.engine.access.QueryFilter;
import org.snaker.engine.entity.HistoryTask;
import org.snaker.engine.entity.WorkItem;
import org.snaker.engine.test.TestSnakerBase;

/**
 * @author yuqs
 * @since 1.0
 */
public class TestQueryHistTask extends TestSnakerBase {
	@Test
	public void test() {
		System.out.println(queryService.getHistoryTasks(new Page<HistoryTask>(), 
				new QueryFilter().setOperator("admin")));
		System.out.println(queryService.getHistoryWorkItems(new Page<WorkItem>(), 
				new QueryFilter().setOperator("admin")));
	}
}
