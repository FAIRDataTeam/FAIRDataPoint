/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package nl.dtls.fairdatapoint.aoipmh.writables;

import com.lyncode.builder.Builder;
import com.lyncode.xoai.dataprovider.exceptions.HandlerException;
import com.lyncode.xoai.dataprovider.exceptions.OAIException;
import com.lyncode.xoai.exceptions.InvalidResumptionTokenException;
import nl.dtls.fairdatapoint.aoipmh.Context;
import nl.dtls.fairdatapoint.aoipmh.Repository;
import nl.dtls.fairdatapoint.aoipmh.parameters.OAICompiledRequest;
import nl.dtls.fairdatapoint.aoipmh.parameters.OAIRequest;
import nl.dtls.fairdatapoint.aoipmh.parameters.OAIRequestParametersBuilder;

/**
 *
 * @author Shamanou van Leeuwen
 * @Since 2016-07-02
 * @param <T>
 */
public abstract class VerbHandler<T extends Writable> {
    private final Context context;
    private final Repository repository;

    public VerbHandler (Context context, Repository repository) {
        this.context = context;
        this.repository = repository;
    }

    public Context getContext() {
        return context;
    }

    public Repository getRepository() {
        return repository;
    }

    public T handle (OAIRequest parameters) throws HandlerException, InvalidResumptionTokenException, OAIException {
        return handle(parameters.compile());
    }

    public T handle (OAIRequestParametersBuilder parameters) throws OAIException, HandlerException, InvalidResumptionTokenException {
        return handle(parameters.build());
    }

    public T handle(Builder<OAICompiledRequest> parameters) throws OAIException, HandlerException {
        return handle(parameters.build());
    }

    public abstract T handle(OAICompiledRequest params) throws OAIException, HandlerException;
}
