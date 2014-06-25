/*
 * CODENVY CONFIDENTIAL
 * ________________
 * 
 * [2012] - [2014] Codenvy, S.A.
 * All Rights Reserved.
 * NOTICE: All information contained herein is, and remains
 * the property of Codenvy S.A. and its suppliers,
 * if any. The intellectual and technical concepts contained
 * herein are proprietary to Codenvy S.A.
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Codenvy S.A..
 */
package com.codenvy.eclipse.client;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * {@link Request} implementation adapting an API request response to another type.
 * 
 * @author Kevin Pollet
 * @param <T> the {@linkplain java.lang.reflect.Type Type} of the adapted request response.
 * @param <S> the {@linkplain java.lang.reflect.Type Type} of the request response to adapt.
 */
public class RequestResponseAdaptor<T, S> implements Request<T> {
    private final Request<S> adaptee;
    private final Adaptor<T, S> adaptor;

    /**
     * Constructs an instance of {@link RequestResponseAdaptor}.
     * 
     * @param adaptee the {@link Request} to adapt.
     * @param adaptor the {@link Request} response adaptor.
     * @throws NullPointerException if adaptee or adaptor parameter is {@code null}.
     */
    RequestResponseAdaptor(Request<S> adaptee, Adaptor<T, S> adaptor) {
        checkNotNull(adaptee);
        checkNotNull(adaptor);

        this.adaptee = adaptee;
        this.adaptor = adaptor;
    }

    @Override
    public T execute() throws CodenvyException {
        return adaptor.adapt(adaptee.execute());
    }

    /**
     * The request response adaptor contract.
     * 
     * @author Kevin Pollet
     * @param <T> the {@linkplain java.lang.reflect.Type Type} of the adapted request response
     * @param <S> the {@linkplain java.lang.reflect.Type Type} of the request response to adapt.
     */
    public interface Adaptor<T, S> {
        /**
         * Adapts the request response.
         * 
         * @param response the request response to adapt
         * @return the adapted response.
         */
        T adapt(S response);
    }
}
