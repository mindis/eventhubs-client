/*******************************************************************************
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.microsoft.eventhubs.client;

import org.apache.qpid.amqp_1_0.client.Connection;
import org.apache.qpid.amqp_1_0.client.ConnectionException;
import org.apache.qpid.amqp_1_0.client.Session;

public class EventHubConsumerGroup {

  private final Connection connection;
  private final String entityPath;
  private final String consumerGroupName;

  private Session session;

  public EventHubConsumerGroup(Connection connection, String entityPath, String consumerGroupName) {
    this.connection = connection;
    this.entityPath = entityPath;
    this.consumerGroupName = consumerGroupName;
  }

  public EventHubReceiver createReceiver(String partitionId, IEventHubFilter filter, int defaultCredits) throws EventHubException {
    ensureSessionCreated();

    if(filter == null) {
      filter = new EventHubOffsetFilter(Constants.DefaultStartingOffset);
    }
    if(defaultCredits < 0) {
      defaultCredits = Constants.DefaultAmqpCredits;
    }

    return new EventHubReceiver(session, entityPath, consumerGroupName,
        partitionId, filter.getFilterString(), defaultCredits);
  }
  
  public void close() {
    if (session != null) {
      session.close();
    }
  }

  synchronized void ensureSessionCreated() throws EventHubException {
    try {
      if (session == null) {
        session = connection.createSession();
      }
    } catch (ConnectionException e) {
      throw new EventHubException(e);
    }
  }
}
