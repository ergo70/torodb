
package com.torodb.torod.core.cursors;

import com.torodb.torod.core.exceptions.ClosedToroCursorException;
import com.torodb.torod.core.exceptions.ToroImplementationException;
import com.torodb.torod.core.exceptions.UnknownMaxElementsException;
import java.util.List;
import javax.annotation.Nonnegative;

/**
 *
 */
public interface UserCursor<E> {

    /**
     * 
     * @return the class of the type returned by this cursor
     */
    public Class<? extends E> getType();
    
    public CursorId getId();
    
    public boolean hasTimeout();
    
    /**
     * Returns true iff the cursor has a limit.
     * 
     * A cursor has a limit if that limit has been defined at creation time.
     * The value of this limit can be obtained with {@linkplain #getLimit() }.
     * If that number (or more) objects are read, then the cursor is exhausted.
     * An exhausted cursor will be closed automatically iff it is also
     * {@linkplain #isAutoclosable() autoclosable}
     * 
     * @return true iff the cursor has a limit
     */
    public boolean hasLimit();
    /**
     * 
     * @return the maximum number of elements this cursor will return
     * @throws ToroImplementationException if {@link #hasLimit() } returns true
     */
    @Nonnegative
    public int getLimit() throws ToroImplementationException;
    /**
     * Returns true iff this cursor is autoclosable.
     * 
     * An autoclosable cursor will be automatically closed when it is 
     * {@linkplain #hasLimit() exhausted} or when there are no more objects
     * that fulfill the query.
     * 
     * @return true iff this cursor is autoclosable
     */
    public boolean isAutoclosable();
    
    /**
     * Read all object pointed by this cursor.
     * 
     * If the cursor is {@linkplain #isAutoclosable() autoclosable}, then the
     * cursor will be closed.
     * 
     * @return 
     * @throws com.torodb.torod.core.exceptions.ClosedToroCursorException 
     */
    public List<E> readAll() throws ClosedToroCursorException;
    
    /**
     * Read a maximun number of documents from this cursor.
     * 
     * The maximum size of the returned list is the given limit, but less
     * documents can be send if the cursor does not have enough documents.
     * 
     * If the cursor is {@linkplain #isAutoclosable() autoclosable} and all
     * documents are read, then the cursor will be closed.
     * 
     * @param limit a positive integer (>= 1)
     * @return
     * @throws com.torodb.torod.core.exceptions.ClosedToroCursorException
     */
    public List<E> read(@Nonnegative int limit) throws ClosedToroCursorException;
    
    /**
     * Close the cursor, releasing the resources associated with it.
     * 
     * This method do not closeImmediately the cursor until the jobs that are already in
 the given executor have been executed
     */
    public void close();
    
    /**
     * 
     * @return The number of objects that have been already read
     * @throws com.torodb.torod.core.exceptions.ClosedToroCursorException
     */
    public int getPosition() throws ClosedToroCursorException;
    
    /**
     * Returns the maximum number of documents this cursor is going to return or
     * throws an exception if this number cannot be known without blocking the
     * thread.
     * 
     * The maximum number of documents is the minimum between {@linkplain #getLimit() }
     * (if {@linkplain #hasLimit() ) is true) and the number of elements 
     * iterated with the cursor.
     * 
     * This value should be constant over time.
     * 
     * @return 
     * @throws UnknownMaxElementsException
     */
    public int getMaxElements() throws UnknownMaxElementsException;
}
