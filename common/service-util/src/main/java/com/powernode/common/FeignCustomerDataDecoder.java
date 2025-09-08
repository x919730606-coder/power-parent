package com.powernode.common;

import com.powernode.common.execption.PowerException;
import com.powernode.common.result.Result;
import feign.FeignException;
import feign.Response;
import feign.codec.DecodeException;
import feign.codec.Decoder;
import org.springframework.cloud.openfeign.support.SpringDecoder;

import java.io.IOException;
import java.lang.reflect.Type;

public class FeignCustomerDataDecoder implements Decoder {

    private SpringDecoder decoder;
    public FeignCustomerDataDecoder(SpringDecoder decoder) {
        this.decoder = decoder;
    }

    @Override
    public Object decode(Response response, Type type) throws IOException, DecodeException, FeignException {

        Object object = this.decoder.decode(response, type);
        if (object instanceof Result){
            Result result = (Result) object;
            if (result.getCode().intValue() != 200){
                throw new PowerException(result.getCode(), result.getMessage());
            }
            return result;
        }
        return object;
    }
}
