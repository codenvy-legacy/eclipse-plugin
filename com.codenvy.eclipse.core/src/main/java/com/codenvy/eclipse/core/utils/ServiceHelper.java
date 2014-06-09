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
package com.codenvy.eclipse.core.utils;

import static com.google.common.base.Preconditions.checkNotNull;

import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;

import com.codenvy.eclipse.core.exceptions.ServiceUnavailableException;

/**
 * This class provides methods to encapsulate and simplify OSGi service call.
 * 
 * @author Kevin Pollet
 * @param <T> the OSGi service {@linkplain java.lang.reflect.Type Type}.
 */
public final class ServiceHelper<T> {
    /**
     * Returns a {@link ServiceHelper} for the given service.
     * 
     * @param clazz the OSGi service clazz.
     * @return the {@link ServiceHelper} instance.
     * @throws NullPointerException if clazz parameter is {@code null}.
     */
    public static <T> ServiceHelper<T> forService(Class<T> clazz) {
        return new ServiceHelper<>(clazz);
    }

    /**
     * The OSGi service class.
     */
    private final Class<T> clazz;

    /**
     * Constructs an instance of {@link ServiceHelper}.
     * 
     * @param clazz the OSGi service class.
     * @throws NullPointerException if clazz parameter is {@code null}.
     */
    private ServiceHelper(Class<T> clazz) {
        checkNotNull(clazz);

        this.clazz = clazz;
    }

    /**
     * Invokes the OSGi service of type T in a safe way.
     * 
     * @param invoker the {@link ServiceInvoker}.
     * @throws NullPointerException if invoker parameter is {@code null}.
     */
    public <S> S invoke(ServiceInvoker<T, S> invoker) throws ServiceUnavailableException {
        checkNotNull(invoker);

        final BundleContext context = FrameworkUtil.getBundle(invoker.getClass()).getBundleContext();
        final ServiceReference<T> serviceReference = context.getServiceReference(clazz);
        if (serviceReference == null) {
            throw new ServiceUnavailableException(clazz);
        }

        try {

            final T service = context.getService(serviceReference);
            if (service == null) {
                throw new ServiceUnavailableException(clazz);
            }

            return invoker.run(service);

        } finally {
            context.ungetService(serviceReference);
        }
    }

    /**
     * The OSGi service invoker contract.
     * 
     * @author Kevin Pollet
     * @param <T> the OSGi service {@linkplain java.lang.reflect.Type Type}.
     * @param <S> the return {@linkplain java.lang.reflect.Type Type}.
     */
    public interface ServiceInvoker<T, S> {
        /**
         * Method called by the {@link ServiceHelper} when invoking an OSGi service.
         * 
         * @param service the OSGi service instance, never {@code null}.
         */
        S run(T service);
    }
}
