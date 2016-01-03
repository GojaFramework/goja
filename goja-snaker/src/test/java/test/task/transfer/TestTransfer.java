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
package test.task.transfer;

import org.junit.Before;
import org.junit.Test;
import org.snaker.engine.access.QueryFilter;
import org.snaker.engine.entity.Order;
import org.snaker.engine.entity.Task;
import org.snaker.engine.helper.StreamHelper;
import org.snaker.engine.test.TestSnakerBase;

import java.util.List;

/**
 * @author yuqs
 * @since 1.0
 */
public class TestTransfer extends TestSnakerBase {
    @Before
    public void before() {
        processId = engine.process()
                .deploy(StreamHelper.getStreamFromClasspath("test/task/transfer/process.snaker"));
    }

    @Test
    public void test() {
        Order order = engine.startInstanceByName("transfer", 0);
        System.out.println("order=" + order);
        List<Task> tasks = queryService.getActiveTasks(new QueryFilter().setOrderId(order.getId()));
        for (Task task : tasks) {
            engine.task().createNewTask(task.getId(), 0, "test");
            engine.task().complete(task.getId());
        }
    }
}
