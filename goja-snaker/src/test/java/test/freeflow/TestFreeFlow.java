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
package test.freeflow;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.snaker.engine.entity.Order;
import org.snaker.engine.entity.Task;
import org.snaker.engine.helper.StreamHelper;
import org.snaker.engine.model.TaskModel;
import org.snaker.engine.test.TestSnakerBase;

/**
 * @author yuqs
 * @since 1.0
 */
public class TestFreeFlow extends TestSnakerBase {
	@Before
	public void before() {
		processId = engine.process().deploy(StreamHelper.getStreamFromClasspath("test/freeflow/free.snaker"));
	}
	
	@Test
	public void test() {
		Map<String, Object> args = new HashMap<String, Object>();
		args.put("task1.operator", new String[]{"1"});
		Order order = engine.startInstanceById(processId, "2", args);
		//System.out.println("order=" + order);
		TaskModel tm1 = new TaskModel();
		tm1.setName("task1");
		tm1.setDisplayName("任务1");
		TaskModel tm2 = new TaskModel();
		tm2.setName("task2");
		tm2.setDisplayName("任务2");
		List<Task> tasks = null;
		tasks = engine.createFreeTask(order.getId(), "1", args, tm1);
		for(Task task : tasks) {
			engine.task().complete(task.getId(), "1", null);
		}
		
//		tasks = engine.createFreeTask(order.getId(), "1", args, tm2);
//		for(Task task : tasks) {
//			engine.task().complete(task.getId(), "1", null);
//		}
		engine.order().terminate(order.getId());
	}
}
