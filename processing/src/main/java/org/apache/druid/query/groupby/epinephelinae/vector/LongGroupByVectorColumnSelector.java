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

package org.apache.druid.query.groupby.epinephelinae.vector;

import org.apache.druid.segment.vector.VectorValueSelector;

import java.nio.ByteBuffer;
import java.util.Map;

public class LongGroupByVectorColumnSelector implements GroupByVectorColumnSelector
{
  private final VectorValueSelector selector;

  LongGroupByVectorColumnSelector(final VectorValueSelector selector)
  {
    this.selector = selector;
  }

  @Override
  public int getGroupingKeySize()
  {
    return 2;
  }

  @Override
  public void writeKeys(
      final int[] keySpace,
      final int keySize,
      final int keyOffset,
      final int startRow,
      final int endRow
  )
  {
    final long[] vector = selector.getLongVector();

    for (int i = startRow, j = keyOffset; i < endRow; i++, j += keySize) {
      keySpace[j] = (int) (vector[i] >>> 32);
      keySpace[j + 1] = (int) (vector[i] & 0xffffffffL);
    }
  }

  @Override
  public void writeKeyToResultRow(
      final String outputName,
      final ByteBuffer keyBuffer,
      final int keyOffset,
      final Map<String, Object> resultMap
  )
  {
    final long value = keyBuffer.getLong(keyOffset * Integer.BYTES);
    resultMap.put(outputName, value);
  }
}
