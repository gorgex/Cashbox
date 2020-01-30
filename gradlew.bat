/*
 * Copyright 2018 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package androidx.recyclerview.widget;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;

/**
 * A Sorted list implementation that can keep items in order and also notify for changes in the
 * list
 * such that it can be bound to a {@link RecyclerView.Adapter
 * RecyclerView.Adapter}.
 * <p>
 * It keeps items ordered using the {@link Callback#compare(Object, Object)} method and uses
 * binary search to retrieve items. If the sorting criteria of your items may change, make sure you
 * call appropriate methods while editing them to avoid data inconsistencies.
 * <p>
 * You can control the order of items and change notifications via the {@link Callback} parameter.
 */
@SuppressWarnings("unchecked")
public class SortedList<T> {

    /**
     * Used by {@link #indexOf(Object)} when the item cannot be found in the list.
     */
    public static final int INVALID_POSITION = -1;

    private static final int MIN_CAPACITY = 10;
    private static final int CAPACITY_GROWTH = MIN_CAPACITY;
    private static final int INSERTION = 1;
    private static final int DELETION = 1 << 1;
    private static final int LOOKUP = 1 << 2;
    T[] mData;

    /**
     * A reference to the previous set of data that is kept during a mutation operation (addAll or
     * replaceAll).
     */
    private T[] mOldData;

    /**
     * The current index into mOldData that has not yet been processed during a mutation operation
     * (addAll or replaceAll).
     */
    private int mOldDataStart;
    private int 