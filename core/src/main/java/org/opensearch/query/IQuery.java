package org.opensearch.query;

import java.util.Collection;

/**
 * query interface to fetch all the queries steps
 * @param <T>
 */
public interface IQuery<T> {
    Collection<T> getElements();
}
